package com.innopolis.maps.innomaps.app;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.Collections2;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.pathfinding.JGraphTWrapper;
import com.innopolis.maps.innomaps.pathfinding.LatLngGraphVertex;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static android.widget.AdapterView.OnItemClickListener;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.POI;

public class MapsFragment extends MarkersAdapter implements ActivityCompat.OnRequestPermissionsResultCallback {

    private GroundOverlay imageOverlay;
    private UiSettings mSettings;
    private LocationManager locationManager;
    DBHelper dbHelper;

    SearchView searchView;
    SearchView.SearchAutoComplete searchBox;
    AHBottomNavigation topNavigation;


    JGraphTWrapper graphWrapper;
    Polyline current;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.maps_fragment, container, false);
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getReadableDatabase();

        scrollView = (NestedScrollView) getActivity().findViewById(R.id.bottom_sheet);
        durationLayout = (LinearLayout) scrollView.findViewById(R.id.durationLayout);
        startLayout = (LinearLayout) scrollView.findViewById(R.id.startLayout);
        mBottomSheetBehavior = BottomSheetBehavior.from(scrollView);
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        floorPicker.animate()
                                .alpha(0f)
                                .setDuration(200)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        floorPicker.setVisibility(View.INVISIBLE);
                                    }
                                });
                    } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        floorPicker.setAlpha(0f);
                        floorPicker.setVisibility(View.VISIBLE);
                        floorPicker.animate()
                                .alpha(1f)
                                .setDuration(200)
                                .setListener(null);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {


                }
            });
        }

        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            case ConnectionResult.SUCCESS:
                mapView = (MapView) v.findViewById(R.id.map);
                floorPicker = (RadioGroup) v.findViewById(R.id.floorPicker);
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        MapsInitializer.initialize(getActivity().getApplicationContext());
                    }
                });
                mapView.onCreate(savedInstanceState);
                if (mapView != null) {
                    map = mapView.getMap();
                    map.setMyLocationEnabled(true);
                    mSettings = map.getUiSettings();
                    mSettings.setMyLocationButtonEnabled(true);
                    mSettings.setZoomControlsEnabled(true);
                    final LatLng university = new LatLng(55.752116019, 48.7448166297);
                    markers = new ArrayList<>();
                    filterList = new ArrayList<>();
                    filterList.add(ALL_FILTER);
                    makeAllMarkers(1);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(university, 17));
                    map.setMapType(MAP_TYPE_NORMAL);
                    markerList = new ArrayList<>();
                    map.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {
                        @Override
                        public boolean onMyLocationButtonClick() {
                            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                displayPromptForEnablingGPS(getActivity());
                            }
                            return false;
                        }
                    });
                    map.setOnMapClickListener(new OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            pinMarker(latLng);
                            scrollView.setVisibility(View.GONE);
                        }
                    });
                    map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                            LatLng cameraTarget = cameraPosition.target;
                            if ((cameraTarget.latitude > 55.752116019 && cameraTarget.latitude < 55.754923377) &&
                                    (cameraTarget.longitude < 48.7448166297 && cameraTarget.longitude > 48.742106790) && cameraPosition.zoom > 17.50) {

                                floorPicker.setVisibility(View.VISIBLE);

                            } else {
                                floorPicker.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), "SERVICE MISSING", Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getActivity(), "UPDATE REQUIRED", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
        }
        initializeOverlay();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topNavigation = (AHBottomNavigation) view.findViewById(R.id.bottom_navigation);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("WC", R.drawable.wc_rast);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Food", R.drawable.food_fork_drink);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("All", R.drawable.all_pack);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Events", R.drawable.calendar_mult);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("Other", R.drawable.duck_rast);
        topNavigation.addItem(item1);
        topNavigation.addItem(item2);
        topNavigation.addItem(item3);
        topNavigation.addItem(item4);
        topNavigation.addItem(item5);
        topNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.colorPrimary));
        topNavigation.setBehaviorTranslationEnabled(false);
        topNavigation.setInactiveColor(Color.WHITE);
        topNavigation.setAccentColor(getResources().getColor(R.color.colorAccent));
        topNavigation.setVisibility(View.GONE);
        topNavigation.setForceTitlesDisplay(true);
        topNavigation.setCurrentItem(2);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchBox = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchBox.setThreshold(1);
        MenuItemCompat.setOnActionExpandListener((MenuItem) menu.findItem(R.id.search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (topNavigation.getVisibility() == View.GONE) {
                    topNavigation.setVisibility(View.VISIBLE);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (topNavigation.getVisibility() == View.VISIBLE) {
                    topNavigation.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });
        topNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            List<SearchableItem> allItems = new LinkedList<>(((SuggestionAdapter) searchBox.getAdapter()).items);

            @Override
            public void onTabSelected(int position, boolean wasSelected) {
                int floor;
                if (floorPicker.getCheckedRadioButtonId() != -1) {
                    int id = floorPicker.getCheckedRadioButtonId();
                    View radioButton = floorPicker.findViewById(id);
                    int radioId = floorPicker.indexOfChild(radioButton);
                    RadioButton btn = (RadioButton) floorPicker.getChildAt(radioId);
                    String selection = (String) btn.getText();
                    floor = Integer.parseInt(selection);
                } else {
                    floor = 1;
                }

                List<SearchableItem> items = ((MainActivity) getActivity()).searchItems;
                Collection<SearchableItem> input;
                String snackbarText;
                if (!wasSelected) {
                    switch (position) {
                        case 0:
                            input = Collections2.filter(allItems, SearchableItem.isWc);
                            snackbarText = "There are no WC";
                            resetMarkers(position, snackbarText, items, input, floor);
                            break;
                        case 1:
                            input = Collections2.filter(allItems, SearchableItem.isFood);
                            snackbarText = "There are no food POI";
                            resetMarkers(position, snackbarText, items, input, floor);
                            break;
                        case 2:
                            sortClearAdd(2);
                            items.clear();
                            for (SearchableItem item : allItems)
                                items.add(item);
                            isMarkerSorted(floor);
                            break;
                        case 3:
                            input = Collections2.filter(allItems, SearchableItem.isEvent);
                            snackbarText = "There are no events";
                            resetMarkers(position, snackbarText, items, input, floor);
                            break;
                        case 4:
                            input = Collections2.filter(allItems, SearchableItem.isOther);
                            snackbarText = "There are no other POI";
                            resetMarkers(position, snackbarText, items, input, floor);
                            break;
                    }
                }
            }
        });
        searchBox.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchableItem item = (SearchableItem) parent.getAdapter().getItem(position);
                inSearchBottomList(item, view);
                menu.findItem(R.id.search).collapseActionView();
            }
        });
    }


    private void resetMarkers(int selectedButton, String snackbarText, List<SearchableItem> items, Collection<SearchableItem> input, int floor) {

        if (input.isEmpty()) {
            Snackbar.make(getView(), snackbarText, Snackbar.LENGTH_SHORT);
        } else {
            sortClearAdd(selectedButton);
            items.clear();
            for (SearchableItem item : input)
                items.add(item);
            isMarkerSorted(floor);
            ((SuggestionAdapter) searchBox.getAdapter()).notifyDataSetChanged();
        }

    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();

        MainActivity.getInstance().trackScreenView("Maps Fragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        scrollView.setVisibility(View.GONE);
    }

    private class RestRequest extends AsyncTask<LatLng, Void, String> {

        LatLng source;
        LatLng destination;

        @Override
        protected String doInBackground(LatLng... params) {
            source = params[0];
            destination = params[1];
            return Utils.doGetRequest(Utils.restServerUrl + "/innomaps/graphml/loadmap?floor=9");
        }

        @Override
        protected void onPostExecute(String str) {
            Log.i("graph", str);
            if (str.equals("")) {
                return;
            }
            try {
                graphWrapper.importGraphML(IOUtils.toInputStream(str, "UTF-8"));
            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

            ArrayList<LatLngGraphVertex> path = graphWrapper.shortestPath(source, destination);
            if (current != null) current.remove();

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(4);
            polylineOptions.color(Color.GREEN);
            polylineOptions.geodesic(true);
            for (LatLngGraphVertex v : path) {
                polylineOptions.add(v.getVertex());
            }
            current = map.addPolyline(polylineOptions);
        }
    }

    private void displayPromptForEnablingGPS(Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Enable either GPS or any other location"
                + " service to find current location. Click OK to go to"
                + " location services settings to let you do so.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                Intent intent = new Intent(action);
                                d.dismiss();
                                startActivity(intent);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    private void initializeOverlay() {
        putOverlayToMap(new LatLng(55.752533, 48.742492), new LatLng(55.754656, 48.744589), BitmapDescriptorFactory.fromResource(R.raw.ai6_floor1));
        setFloorPOIHashMap(1);
        floorPicker.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LatLng southWest, northEast;
                switch (checkedId) {
                    case R.id.button1:
                    default:
                        southWest = new LatLng(55.752533, 48.742492);
                        northEast = new LatLng(55.754656, 48.744589);
                        buttonClickFloorPicker(southWest, northEast, R.raw.ai6_floor1, 1);
                        break;
                    case R.id.button2:
                        southWest = new LatLng(55.752828, 48.742661);
                        northEast = new LatLng(55.754597, 48.744469);
                        buttonClickFloorPicker(southWest, northEast, R.raw.ai6_floor2, 2);
                        break;
                    case R.id.button3:
                        southWest = new LatLng(55.752875, 48.742739);
                        northEast = new LatLng(55.754572, 48.744467);
                        buttonClickFloorPicker(southWest, northEast, R.raw.ai6_floor3, 3);
                        break;
                    case R.id.button4:
                        southWest = new LatLng(55.752789, 48.742711);
                        northEast = new LatLng(55.754578, 48.744569);
                        buttonClickFloorPicker(southWest, northEast, R.raw.ai6_floor4, 4);
                        break;
                    case R.id.button5:
                        southWest = new LatLng(55.752808, 48.743497);
                        northEast = new LatLng(55.753383, 48.744519);
                        buttonClickFloorPicker(southWest, northEast, R.raw.ai6_floor5, 5);
                        break;
                }
            }
        });

    }

    private void buttonClickFloorPicker(LatLng southWest, LatLng northEast, int floorSource, int floor) {
        clearMarkerList();
        isMarkerSorted(floor);
        putOverlayToMap(southWest, northEast, BitmapDescriptorFactory.fromResource(floorSource));
        setFloorPOIHashMap(floor);
    }


    private void putOverlayToMap(LatLng southWest, LatLng northEast, BitmapDescriptor bitmapDescriptor) {
        if (imageOverlay != null) {
            imageOverlay.remove();
        }
        LatLngBounds latLngBounds;
        GroundOverlayOptions groundOverlayOptions;
        latLngBounds = new LatLngBounds(southWest, northEast);
        groundOverlayOptions = new GroundOverlayOptions();
        groundOverlayOptions.positionFromBounds(latLngBounds);
        groundOverlayOptions.image(bitmapDescriptor);
        imageOverlay = map.addGroundOverlay(groundOverlayOptions);
    }


    private void setFloorPOIHashMap(Integer floor) {
        latLngMap = new HashMap<>();
        String sqlQuery = "SELECT " + LATITUDE + "," + LONGITUDE + "," + FLOOR + " FROM " + POI + " WHERE " + FLOOR + "=?";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{floor + "floor"});
        if (cursor.moveToFirst()) {
            do {
                latLngMap.put(cursor.getString(cursor.getColumnIndex(LATITUDE)), cursor.getString(cursor.getColumnIndex(LONGITUDE)));
            } while (cursor.moveToNext());
        } else {
            latLngMap = null;
        }
    }


    public void showRoute(LatLng source, LatLng destination) {
        graphWrapper = new JGraphTWrapper();
        new RestRequest().execute(source, destination);
    }

    private void sortClearAdd(int num) {
        filterList.clear();
        filterList.add(num);
    }
}

