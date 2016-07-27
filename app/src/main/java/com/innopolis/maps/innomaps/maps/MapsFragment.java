package com.innopolis.maps.innomaps.maps;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.common.collect.Collections2;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.CustomScrollView;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.app.SearchableItem;
import com.innopolis.maps.innomaps.app.SuggestionAdapter;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.database.SQLQueries;
import com.innopolis.maps.innomaps.network.Constants;
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.qr.Scanner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.AdapterView.OnItemClickListener;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import static com.innopolis.maps.innomaps.database.TableFields.ALL_CAPITAL;
import static com.innopolis.maps.innomaps.database.TableFields.ALL_FILTER;
import static com.innopolis.maps.innomaps.database.TableFields.EVENTS_CAPITAL;
import static com.innopolis.maps.innomaps.database.TableFields.EVENTS_FILTER;
import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.FOOD_CAPITAL;
import static com.innopolis.maps.innomaps.database.TableFields.FOOD_FILTER;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.OTHER_CAPITAL;
import static com.innopolis.maps.innomaps.database.TableFields.OTHER_FILTER;
import static com.innopolis.maps.innomaps.database.TableFields.WC_CAPITAL;
import static com.innopolis.maps.innomaps.database.TableFields.WC_FILTER;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.CAMERA_LAT_BOTTOM;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.CAMERA_LAT_TOP;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.CAMERA_LNG_LEFT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.CAMERA_LNG_RIGHT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_FIVE_LAT_BOTTOM;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_FIVE_LAT_TOP;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_FIVE_LNG_LEFT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_FIVE_LNG_RIGHT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_FOUR_LAT_BOTTOM;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_FOUR_LAT_TOP;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_FOUR_LNG_LEFT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_FOUR_LNG_RIGHT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_ONE_LAT_BOTTOM;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_ONE_LAT_TOP;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_ONE_LNG_LEFT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_ONE_LNG_RIGHT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_THREE_LAT_BOTTOM;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_THREE_LAT_TOP;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_THREE_LNG_LEFT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_THREE_LNG_RIGHT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_TWO_LAT_BOTTOM;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_TWO_LAT_TOP;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_TWO_LNG_LEFT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.FLOOR_TWO_LNG_RIGHT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.UI_LAT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.UI_LNG;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.UI_OUTLINE_LAT_BOTTOM;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.UI_OUTLINE_LAT_TOP;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.UI_OUTLINE_LNG_LEFT;
import static com.innopolis.maps.innomaps.maps.CoordinatesConstants.UI_OUTLINE_LNG_RIGHT;
import static com.innopolis.maps.innomaps.network.Constants.LOG;

public class MapsFragment extends MarkersAdapter implements ActivityCompat.OnRequestPermissionsResultCallback {

    private GroundOverlay imageOverlay;
    private LocationManager locationManager;
    private DBHelper dbHelper;

    private SearchView searchView;
    private SearchView.SearchAutoComplete searchBox;

    /*Don't be confused by class name - it is the element, which is shown during search, with 5 categories*/
    private AHBottomNavigation topNavigation;

    /*Dialog, that asks user how to select his location */
    public Dialog currentDialog;

    /*Map route, that is build on map if user started a route
    * public modifier left for testing purposes */
    public MapRoute mapRoute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.maps_fragment, container, false);
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getReadableDatabase();

        scrollView = (CustomScrollView) getActivity().findViewById(R.id.bottom_sheet);
        durationLayout = (LinearLayout) scrollView.findViewById(R.id.durationLayout);
        startLayout = (LinearLayout) scrollView.findViewById(R.id.startLayout);

        scrollView.setVisibilityListener(new CustomScrollView.OnVisibilityChangedListener() {
            @Override
            public void visibilityChanged(int visibility) {
                if (visibility == VISIBLE) {
                    setFloorPickerMargin(false);
                } else {
                    setFloorPickerMargin(true);
                }
            }
        });

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
                        setFloorPickerMargin(false);
                        floorPicker.setAlpha(0f);
                        if (checkIfZoomIsEnough(map.getCameraPosition()))
                            floorPicker.setVisibility(View.VISIBLE);
                        floorPicker.animate()
                                .alpha(1f)
                                .setDuration(200)
                                .setListener(null);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    //not yet implemented
                }
            });
        }

        /*The listener below is invoked on every UI change of scrollView
        * and shows floorPicker if scrollView is hidden*/
        scrollView.setTag(scrollView.getVisibility());
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int newVis = scrollView.getVisibility();
                if ((int) scrollView.getTag() != newVis) {
                    scrollView.setTag(scrollView.getVisibility());
                    if (scrollView.getVisibility() == GONE || scrollView.getVisibility() == INVISIBLE) {
                        floorPicker.setVisibility(View.VISIBLE);
                        floorPicker.animate()
                                .alpha(1f)
                                .setDuration(200)
                                .setListener(null);
                    }
                }
            }
        });
        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            case ConnectionResult.SUCCESS:
                mapView = (MapView) view.findViewById(R.id.map);
                floorPicker = (RadioGroup) view.findViewById(R.id.floorPicker);
                ((RadioButton) floorPicker.getChildAt(4)).setChecked(true); //1st floor
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        MapsInitializer.initialize(getActivity().getApplicationContext());
                    }
                });
                mapView.onCreate(savedInstanceState);
                if (mapView != null) {
                    map = mapView.getMap();


                    UiSettings mSettings = map.getUiSettings();
                    zoomToUniversityAlways();
                    mSettings.setMapToolbarEnabled(false);
                    markers = new ArrayList<>();
                    filterList = new ArrayList<>();
                    filterList.add(ALL_FILTER);

                    map.setMapType(MAP_TYPE_NORMAL);
                    markerList = new ArrayList<>();
                    if (checkIfZoomIsEnough(map.getCameraPosition())) {
                        floorPicker.setVisibility(View.VISIBLE);
                        initializeOverlay();
                    } else {
                        floorPicker.setVisibility(View.INVISIBLE);
                        makeUiOutline();
                        if (markers != null) {
                            for (Marker marker : markers) {
                                marker.remove();
                            }
                            markers.clear();
                        }
                    }
                    /*Invokes when location button is triggered – checks whether user has GPS turned on*/
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

                    /*This listener pins marker to nearest POI on map next to click LatLng coordinates */
                    map.setOnMapClickListener(mapClickListener);

                    map.setOnMarkerClickListener(
                            new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    /*If the path has started and a path handling marker was clicked*/
                                    if (mapRoute != null && mapRoute.hasCurrentPath && marker.getSnippet() != null) {
                                        if (marker.getSnippet().equals(getContext().getString(R.string.next))) {
                                            mapRoute.nextPath();
                                        } else if (marker.getSnippet().equals(getContext().getString(R.string.previous))) {
                                            mapRoute.prevPath();
                                        } else if (marker.getSnippet().equals(getContext().getString(R.string.finish))) {
                                            mapRoute.finishRoute(true);
                                        }
                                        marker.hideInfoWindow();
                                        return true;

                                    } else {
                                        pinMarker(marker.getPosition(), true);
                                        return true;
                                    }
                                }
                            }
                    );

                    /* This listener checks current camera position in order to show custom
                    * level picker over the university building */
                    map.setOnCameraChangeListener(showUniversityPicker);

                    floorPicker.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (scrollView.getVisibility() == View.VISIBLE) {
                                scrollView.setVisibility(View.GONE);
                            }
                            floorPickerGroundOverlaySwitch(checkedId);
                        }
                    });
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), R.string.service_missing, Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                // TODO: extract string constants +
                Toast.makeText(getActivity(), R.string.update_required, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
        }

        topNavigation = (AHBottomNavigation) view.findViewById(R.id.bottom_navigation);
        // TODO: probably these constants are part of your domain model
        AHBottomNavigationItem wc = new AHBottomNavigationItem(WC_CAPITAL, R.drawable.wc_rast);
        AHBottomNavigationItem food = new AHBottomNavigationItem(FOOD_CAPITAL, R.drawable.food_fork_drink);
        AHBottomNavigationItem all = new AHBottomNavigationItem(ALL_CAPITAL, R.drawable.all_pack);
        AHBottomNavigationItem events = new AHBottomNavigationItem(EVENTS_CAPITAL, R.drawable.calendar_mult);
        AHBottomNavigationItem other = new AHBottomNavigationItem(OTHER_CAPITAL, R.drawable.duck_rast);
        topNavigation.addItem(wc);
        topNavigation.addItem(food);
        topNavigation.addItem(all);
        topNavigation.addItem(events);
        topNavigation.addItem(other);
        topNavigation.setDefaultBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        topNavigation.setAccentColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        topNavigation.setBehaviorTranslationEnabled(false);
        topNavigation.setInactiveColor(Color.WHITE);
        topNavigation.setVisibility(View.GONE);
        topNavigation.setForceTitlesDisplay(true);
        topNavigation.setCurrentItem(ALL_FILTER);
    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchBox = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        //Specifies number of characters to type before dropdown is shown
        searchBox.setThreshold(1);
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.search), new MenuItemCompat.OnActionExpandListener() {
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
                        // TODO: are these floors? +
                        case WC_FILTER:
                            input = Collections2.filter(allItems, SearchableItem.isWc);
                            snackbarText = getString(R.string.no_wc);
                            resetMarkers(position, snackbarText, items, input, floor);
                            break;
                        case FOOD_FILTER:
                            input = Collections2.filter(allItems, SearchableItem.isFood);
                            snackbarText = getString(R.string.no_food_poi);
                            resetMarkers(position, snackbarText, items, input, floor);
                            break;
                        case ALL_FILTER:
                            sortClearAdd(ALL_FILTER);
                            items.clear();
                            for (SearchableItem item : allItems)
                                items.add(item);
                            isMarkerSorted(floor);
                            break;
                        case EVENTS_FILTER:
                            input = Collections2.filter(allItems, SearchableItem.isEvent);
                            snackbarText = getString(R.string.no_events);
                            resetMarkers(position, snackbarText, items, input, floor);
                            break;
                        case OTHER_FILTER:
                            input = Collections2.filter(allItems, SearchableItem.isOther);
                            snackbarText = getString(R.string.no_other_poi);
                            resetMarkers(position, snackbarText, items, input, floor);
                            break;
                    }
                    hideSoftKeyboard(getActivity());
                }
            }
        });
        searchBox.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchableItem item = (SearchableItem) parent.getAdapter().getItem(position);
                inSearchBottomList(item);
                menu.findItem(R.id.search).collapseActionView();
            }
        });
    }

    private static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    // TODO: make it readable
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

        MainActivity.getInstance().trackScreenView(getContext().getString(R.string.maps_fragment));
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
        mapRoute = null;
        scrollView.setVisibility(View.GONE);
    }

    private void getAndDrawPath(LatLngFlr source, LatLngFlr destination) {
        NetworkController networkController = new NetworkController();

        ArrayList<LatLngFlrGraphVertex> path = (ArrayList<LatLngFlrGraphVertex>) networkController.findShortestPath(source.getLatitude(), source.getLongitude(),
                source.getFloor(), destination.getLatitude(), destination.getLongitude(), destination.getFloor());

            /*Creation and start of a new route*/
        if (path != null && !path.isEmpty()) {
            scrollView.setVisibility(View.GONE);
            if (mapRoute != null && mapRoute.hasCurrentPath) {
                mapRoute.finishRoute(false);
            }
            mapRoute = new MapRoute(map, path, getActivity(), floorPicker);
            mapRoute.startRoute();
        } else {
            // TODO: use resource string here +
            Toast.makeText(getContext(), R.string.already_there, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayPromptForEnablingGPS(Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        // TODO: string resource
        final String message = activity.getString(R.string.navigation_location_message);

        builder.setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                Intent intent = new Intent(action);
                                d.dismiss();
                                startActivity(intent);
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    private void initializeOverlay() {
        floorPickerGroundOverlaySwitch(floorPicker.getCheckedRadioButtonId());
    }

    private void floorPickerGroundOverlaySwitch(int checkedId) {
        LatLng southWest, northEast;
        clearMarkerList();
        if (mapRoute != null && mapRoute.hasCurrentPath) mapRoute.addMarkerPolylineToMap(checkedId);
        switch (checkedId) {
            // TODO: this is a part of your domain model, so you need to create a class representing it
            // Then you will need a manager, that will instantiate objects of that class with these constant values
            case R.id.button1:
            default:
                southWest = new LatLng(FLOOR_ONE_LAT_BOTTOM, FLOOR_ONE_LNG_LEFT);
                northEast = new LatLng(FLOOR_ONE_LAT_TOP, FLOOR_ONE_LNG_RIGHT);
                changeOnFloorPickerClick(southWest, northEast, R.raw.ai6_floor1, 1);
                break;
            case R.id.button2:
                southWest = new LatLng(FLOOR_TWO_LAT_BOTTOM, FLOOR_TWO_LNG_LEFT);
                northEast = new LatLng(FLOOR_TWO_LAT_TOP, FLOOR_TWO_LNG_RIGHT);
                changeOnFloorPickerClick(southWest, northEast, R.raw.ai6_floor2, 2);
                break;
            case R.id.button3:
                southWest = new LatLng(FLOOR_THREE_LAT_BOTTOM, FLOOR_THREE_LNG_LEFT);
                northEast = new LatLng(FLOOR_THREE_LAT_TOP, FLOOR_THREE_LNG_RIGHT);
                changeOnFloorPickerClick(southWest, northEast, R.raw.ai6_floor3, 3);
                break;
            case R.id.button4:
                southWest = new LatLng(FLOOR_FOUR_LAT_BOTTOM, FLOOR_FOUR_LNG_LEFT);
                northEast = new LatLng(FLOOR_FOUR_LAT_TOP, FLOOR_FOUR_LNG_RIGHT);
                changeOnFloorPickerClick(southWest, northEast, R.raw.ai6_floor4, 4);
                break;
            case R.id.button5:
                southWest = new LatLng(FLOOR_FIVE_LAT_BOTTOM, FLOOR_FIVE_LNG_LEFT);
                northEast = new LatLng(FLOOR_FIVE_LAT_TOP, FLOOR_FIVE_LNG_RIGHT);
                changeOnFloorPickerClick(southWest, northEast, R.raw.ai6_floor5, 5);
                break;
        }
    }

    private void changeOnFloorPickerClick(LatLng southWest, LatLng northEast, int id, int floor) {
        // TODO: constant values of size (600x600px) for all screen sizes
        buttonClickFloorPicker(southWest, northEast, decodeSampledBitmapFromResource(getResources(), id, 600, 600), floor);
        isMarkerSorted(floor);
        clearMarkerList();
    }

    private Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void buttonClickFloorPicker(LatLng southWest, LatLng northEast, Bitmap bitmap, int floor) {
        isMarkerSorted(floor);
        putOverlayToMap(southWest, northEast, bitmap);
        setFloorPOIHashMap(floor);
    }

    private void putOverlayToMap(LatLng southWest, LatLng northEast, Bitmap bitmap) {
        if (imageOverlay != null) {
            imageOverlay.remove();
        }
        LatLngBounds latLngBounds;
        GroundOverlayOptions groundOverlayOptions;
        latLngBounds = new LatLngBounds(southWest, northEast);
        groundOverlayOptions = new GroundOverlayOptions();
        groundOverlayOptions.positionFromBounds(latLngBounds);
        groundOverlayOptions.image(BitmapDescriptorFactory.fromBitmap(bitmap));

        if (!bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }

        imageOverlay = map.addGroundOverlay(groundOverlayOptions);
    }

    // TODO: check if this method is used or not, because it is useless
    private void setFloorPOIHashMap(Integer floor) {
        latLngMap = new HashMap<>();
        String sqlQuery = SQLQueries.selectFloorPoiHashmapQuery();
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{floor + FLOOR});
        if (cursor.moveToFirst()) {
            do {
                latLngMap.put(cursor.getString(cursor.getColumnIndex(LATITUDE)), cursor.getString(cursor.getColumnIndex(LONGITUDE)));
            } while (cursor.moveToNext());
        } else {
            latLngMap = null;
        }
        cursor.close();
    }

    public void showRoute(LatLngFlr source, LatLngFlr destination) {
        getAndDrawPath(source, destination);
    }

    private void sortClearAdd(int num) {
        filterList.clear();
        filterList.add(num);
    }

    public void allowSelection(final Dialog dialog, final LatLng destinationLatLng) {
        // TODO: this is so wrong!
        dialog.hide();

        // This whole code block could be replaces with an xml and layout inflater
        final LinearLayout buttons = new LinearLayout(getContext());
        LinearLayout.LayoutParams buttonsLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        LinearLayout.LayoutParams buttonsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);
        buttons.setLayoutParams(buttonsLayoutParams);
        AppCompatButton back = new AppCompatButton(getContext());
        back.setLayoutParams(buttonsParams);
        back.setText(R.string.back_caps);
        AppCompatButton select = new AppCompatButton(getContext());
        select.setLayoutParams(buttonsParams);
        select.setText(R.string.go_caps);
        buttons.addView(back);
        buttons.addView(select);

        RelativeLayout.LayoutParams parentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        parentParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        parentParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        if ((getView()) != null) {
            ((RelativeLayout) getView()).addView(buttons, parentParams);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                ((RelativeLayout) getView()).removeView(buttons);
            }
        });

        final Context context = super.getContext();
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closest != null) {
                    ((RelativeLayout) getView()).removeView(buttons);

                    // TODO: Figure out why the floor for the point from the graph cannot be detected
                    // Will write an error message to log. Don't know why it cannot determine the floor
                    // for the point from the graph.
                    // All should be fixed with the planned change of the database structure
                    // Currently everything works fine because there are no coordinates with the same
                    // latitude and longitude but with different floor numbers and because the floor for
                    // given latitude and longitude is detected on the server side
                    Cursor cursor = database.rawQuery(SQLQueries.selectFloorForCoordinate(closest), null);
                    int floorSource = 1;
                    int floorDestination = 1;
                    if (cursor.moveToFirst())
                        floorSource = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FLOOR)).substring(0, 1));
                    else {
                        // TODO: Remove commented code after and only after the app will work with 3D coordinates and the DB will support them
                        // To be honest, the error message should be shown. If there was no floor detection on server shortest path will work incorrectly.
                        // Since, as I hope, we will rewrite app and DB to support 3D coordinates and such floor detection won't be needed
                        // I will leave it as it is. But honestly, I understand that everything here holds on a hair.
                        Log.e(LOG, String.format("%1$s %2$s: %3$s, %4$s: %5$s", Constants.FLOOR_CALCULATION_ERROR, Constants.LATITUDE,
                                closest.latitude, Constants.LONGITUDE, closest.longitude));
                    }
                    cursor.close();
                    cursor = database.rawQuery(SQLQueries.selectFloorForCoordinate(destinationLatLng), null);
                    if (cursor.moveToFirst())
                        floorDestination = Integer.parseInt(cursor.getString(cursor.getColumnIndex(FLOOR)).substring(0, 1));
                    else {
                        // TODO: Remove commented code after and only after the app will work with 3D coordinates and the DB will support them
                        // To be honest, the error message should be shown. If there was no floor detection on server shortest path will work incorrectly.
                        // Since, as I hope, we will rewrite app and DB to support 3D coordinates and such floor detection won't be needed
                        // I will leave it as it is. But honestly, I understand that everything here holds on a hair.
                        Log.e(LOG, String.format("%1$s %2$s: %3$s, %4$s: %5$s", Constants.FLOOR_CALCULATION_ERROR, Constants.LATITUDE,
                                destinationLatLng.latitude, Constants.LONGITUDE, destinationLatLng.longitude));
                    }
                    cursor.close();

                    LatLngFlr source = new LatLngFlr(closest.latitude, closest.longitude, floorSource);
                    LatLngFlr destination = new LatLngFlr(destinationLatLng.latitude, destinationLatLng.longitude, floorDestination);

                    showRoute(source, destination);
                    dialog.cancel();
                } else {
                    Toast.makeText(getContext(), R.string.marker_in_university, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void openQrScanner(Dialog dialog) {
        currentDialog = dialog;
        dialog.hide();
        Intent intent = new Intent(getActivity(), Scanner.class);
        Bundle bundle = new Bundle();
        // TODO: extract constants
        bundle.putDouble(LATITUDE, closest.latitude);
        bundle.putDouble(LONGITUDE, closest.longitude);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    GoogleMap.OnCameraChangeListener showUniversityPicker = new GoogleMap.OnCameraChangeListener() {
        boolean overlay = false, outline = false;

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {

            if (mapRoute != null && mapRoute.hasCurrentPath) {
                mapRoute.redrawMarkers(Double.valueOf(Math.floor((double) cameraPosition.zoom)).intValue());
            }
            if (checkIfZoomIsEnough(cameraPosition)) {
                floorPicker.setVisibility(View.VISIBLE);
                if (!overlay) {
                    initializeOverlay();
                    overlay = true;
                    outline = false;
                }
            } else {
                floorPicker.setVisibility(View.INVISIBLE);
                if (!outline) {
                    makeUiOutline();
                    if (markers != null) {
                        for (Marker marker : markers) {
                            marker.remove();
                        }
                        markers.clear();
                    }
                    outline = true;
                    overlay = false;
                }
            }
        }
    };


    private boolean checkIfZoomIsEnough(CameraPosition cameraPosition) {
        LatLng cameraTarget = cameraPosition.target;
        return (cameraTarget.latitude > CAMERA_LAT_BOTTOM && cameraTarget.latitude < CAMERA_LAT_TOP) &&
                (cameraTarget.longitude < CAMERA_LNG_RIGHT && cameraTarget.longitude > CAMERA_LNG_LEFT) && cameraPosition.zoom > 17.50;
    }

    OnMapClickListener mapClickListener = new OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            pinMarker(latLng, true);
            hideSoftKeyboard(getActivity());
        }
    };

    private void makeUiOutline() {
        LatLng southWest = new LatLng(UI_OUTLINE_LAT_BOTTOM, UI_OUTLINE_LNG_LEFT);
        LatLng northEast = new LatLng(UI_OUTLINE_LAT_TOP, UI_OUTLINE_LNG_RIGHT);
        int res = 600;
        Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), R.raw.ui_unzoomed, res, res);
        putOverlayToMap(southWest, northEast, bitmap);
    }

    private void zoomToUniversityAlways() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(UI_LAT, UI_LNG), (float) 17.7));
    }
}
