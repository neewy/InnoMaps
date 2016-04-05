package com.innopolis.maps.innomaps.app;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableTextView;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.Collections2;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.database.TableFields;
import com.innopolis.maps.innomaps.events.Event;
import com.innopolis.maps.innomaps.events.MapBottomEventListAdapter;
import com.innopolis.maps.innomaps.pathfinding.JGraphTWrapper;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static android.widget.AdapterView.OnItemClickListener;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import static com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import static com.innopolis.maps.innomaps.database.TableFields.BUILDING;
import static com.innopolis.maps.innomaps.database.TableFields.DESCRIPTION;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_ID;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_TYPE;
import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.ID;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.POI;
import static com.innopolis.maps.innomaps.database.TableFields.POI_NAME;
import static com.innopolis.maps.innomaps.database.TableFields.START;
import static com.innopolis.maps.innomaps.database.TableFields.SUMMARY;
import static com.innopolis.maps.innomaps.database.TableFields.TYPE;

public class MapsFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    MapView mapView;
    private GoogleMap map;
    private GroundOverlay imageOverlay;
    private UiSettings mSettings;
    private LocationManager locationManager;
    DBHelper dbHelper;
    SQLiteDatabase database;

    SearchView searchView;
    SearchView.SearchAutoComplete searchBox;
    AHBottomNavigation topNavigation;

    private BottomSheetBehavior mBottomSheetBehavior;

    RadioGroup floorPicker;
    private HashMap<String, String> latLngMap;
    private LatLng closest = null;
    List<Marker> markerList;
    List<Marker> markers;
    List<Integer> sort;
    JGraphTWrapper graphWrapper;
    Polyline current;

    NestedScrollView scrollView;

    /*These components are the part of scrollview elements*/
    TextView headerText;
    TextView locationText;
    TextView startText;
    TextView durationText;
    FrameLayout relatedLayout;
    FloatingActionButton fab;
    TextView idPoi;
    LinearLayout durationLayout, startLayout;

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
                    sort = new ArrayList<>();
                    sort.add(2);
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
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("WC", R.drawable.wc);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Food", R.drawable.food_fork_drink);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("All", R.drawable.all_pack);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Events", R.drawable.calendar_mult);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem("Other", R.drawable.duck);
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
                int sorter;
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
                if (!wasSelected) {
                    switch (position) {
                        case 0:
                            Collection<SearchableItem> wc = Collections2.filter(allItems, SearchableItem.isWc);
                            if (wc.isEmpty()) {
                                Snackbar.make(getView(), "There are no WC", Snackbar.LENGTH_SHORT);
                            } else {
                                sortClearAdd(0);
                                items.clear();
                                for (SearchableItem item : wc)
                                    items.add(item);
                                isMarkerSorted(floor);
                                ((SuggestionAdapter) searchBox.getAdapter()).notifyDataSetChanged();

                            }
                            break;
                        case 1:
                            Collection<SearchableItem> food = Collections2.filter(allItems, SearchableItem.isFood);
                            if (food.isEmpty()) {
                                Snackbar.make(getView(), "There are no food POI", Snackbar.LENGTH_SHORT);
                            } else {
                                sortClearAdd(1);
                                items.clear();
                                for (SearchableItem item : food)
                                    items.add(item);
                                isMarkerSorted(floor);
                                ((SuggestionAdapter) searchBox.getAdapter()).notifyDataSetChanged();
                            }
                            break;
                        case 2:
                            sortClearAdd(2);
                            items.clear();
                            for (SearchableItem item : allItems)
                                items.add(item);
                            isMarkerSorted(floor);
                            break;
                        case 3:
                            sortClearAdd(3);
                            Collection<SearchableItem> events = Collections2.filter(allItems, SearchableItem.isEvent);
                            if (events.isEmpty()) {
                                Snackbar.make(getView(), "There are no events", Snackbar.LENGTH_SHORT);
                            } else {
                                sortClearAdd(3);
                                items.clear();
                                for (SearchableItem item : events)
                                    items.add(item);
                                ((SuggestionAdapter) searchBox.getAdapter()).notifyDataSetChanged();
                            }
                            break;
                        case 4:
                            sortClearAdd(4);
                            Collection<SearchableItem> other = Collections2.filter(allItems, SearchableItem.isOther);
                            if (other.isEmpty()) {
                                Snackbar.make(getView(), "There are no other POI", Snackbar.LENGTH_SHORT);
                            } else {
                                sortClearAdd(4);
                                items.clear();
                                for (SearchableItem item : other)
                                    items.add(item);
                                isMarkerSorted(floor);
                                ((SuggestionAdapter) searchBox.getAdapter()).notifyDataSetChanged();
                            }
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

    public void inSearchBottomList(SearchableItem item, View view) {

        if (scrollView.getVisibility() == View.GONE) {
            scrollView.setVisibility(View.VISIBLE);
        }

        List<String> location = new LinkedList<>();
        if (item.getBuilding() != null) location.add(item.getBuilding());
        if (item.getFloor() != null) location.add(item.getFloor());
        if (item.getRoom() != null) location.add(item.getRoom());
        String[] locationArray = new String[location.size()];

        for (int i = 0; i < location.size(); i++) {
            locationArray[i] = location.get(i);
        }

        CheckedTextView text = (CheckedTextView) view.findViewById(R.id.name);
        headerText = (TextView) scrollView.findViewById(R.id.headerText);
        locationText = (TextView) scrollView.findViewById(R.id.locationText);
        startText = (TextView) scrollView.findViewById(R.id.startText);
        durationText = (TextView) scrollView.findViewById(R.id.durationText);
        relatedLayout = (FrameLayout) scrollView.findViewById(R.id.relatedLayout);
        idPoi = (TextView) scrollView.findViewById(R.id.idPoi);

        if (relatedLayout.getChildCount() != 0) {
            relatedLayout.removeView(relatedLayout.getChildAt(0));
        }

        if (item.getType().equals("event")) {
            typeEvent(text.getText().toString());
            idPoi.setText("event");
        } else {
            idPoi.setText(item.getId());
            typeEventNon(item.getId());
        }
        locationText.setText(StringUtils.join(locationArray, ", "));

        Utils.hideKeyboard(getActivity());
    }

    private void typeEvent(String summary) {

        FrameLayout relatedLayout = (FrameLayout) scrollView.findViewById(R.id.relatedLayout);
        FloatingActionButton fab = (FloatingActionButton) scrollView.findViewById(R.id.goto_fab);

        String sqlQuery = "SELECT * FROM events INNER JOIN event_poi ON events.eventID = event_poi.eventID INNER JOIN poi ON event_poi.poi_id = poi._id WHERE events.summary=?";

        String name = "", latitude = "", longitude = "", startDateText = "", description = "";
        Date startDate = null;

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{summary});

        durationLayout.setVisibility(View.VISIBLE);
        startLayout.setVisibility(View.VISIBLE);

        if (cursor.moveToFirst()) {
            latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
            longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
            startDateText = cursor.getString(cursor.getColumnIndex(START));
            name = cursor.getString(cursor.getColumnIndex(SUMMARY));

            Cursor cursor_type = database.query(EVENT_TYPE, null, "summary=?", new String[]{summary}, null, null, null);
            if (cursor_type.moveToFirst()) {
                description = cursor_type.getString(cursor_type.getColumnIndex(DESCRIPTION));
            }
            LinkableTextView descriptionText = new LinkableTextView(getContext());
            Link linkUsername = new Link(Pattern.compile("(@\\w+)"))
                    .setUnderlined(false)
                    .setTextColor(Color.parseColor("#D00000"))
                    .setTextStyle(Link.TextStyle.BOLD)
                    .setClickListener(new Link.OnClickListener() {
                        @Override
                        public void onClick(String text) {
                            System.out.println("Go to telegram");
                        }
                    });
            descriptionText.setTextSize(16);
            descriptionText.setPadding(0, 20, 10, 10);
            descriptionText.setText(description).addLink(linkUsername).build();
            relatedLayout.addView(descriptionText);
            try {
                startDate = Utils.googleTimeFormat.parse(startDateText);
            } catch (ParseException e) {
                Log.e("Maps", "Time parse exception", e);
            }
            pinMarker(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
        }

        headerText.setText(name);
        startText.setText(Utils.commonTime.format(startDate));
        durationText.setText(Utils.prettyTime.format(startDate));
        mBottomSheetBehavior.setPeekHeight(headerText.getLayout().getHeight() + fab.getHeight() + 42);

    }

    public void typeEventNon(String poi_id) {

        FrameLayout relatedLayout = (FrameLayout) scrollView.findViewById(R.id.relatedLayout);
        FloatingActionButton fab = (FloatingActionButton) scrollView.findViewById(R.id.goto_fab);

        String sqlQuery = "SELECT * FROM poi LEFT OUTER JOIN event_poi on event_poi.poi_id = poi._id LEFT OUTER JOIN events on events.eventID = event_poi.eventID WHERE poi._id=?";

        Cursor cursor = database.rawQuery(sqlQuery, new String[]{poi_id});
        String poi_name, latitude, longitude;

        durationLayout.setVisibility(View.GONE);
        startLayout.setVisibility(View.GONE);

        if (cursor.moveToFirst()) {
            poi_name = cursor.getString(cursor.getColumnIndex(POI_NAME));
            latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
            longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
            headerText.setText(poi_name);
            List<Event> events = new LinkedList<>();
            do {
                Event event = new Event();
                event.setSummary(cursor.getString(cursor.getColumnIndex(SUMMARY)));
                try {
                    if (cursor.getString(cursor.getColumnIndex(START)) != null) {
                        event.setStart(Utils.googleTimeFormat.parse(cursor.getString(cursor.getColumnIndex(START))));
                    }
                } catch (ParseException e) {
                    Log.e("Maps", "Date parse exception", e);
                }
                event.setEventID(cursor.getString(cursor.getColumnIndex(EVENT_ID)));
                if (event.getEventID() != null) {
                    events.add(event);
                }
            } while (cursor.moveToNext());
            if (events.size() == 0) {
                TextView noEvents = new TextView(getContext());
                noEvents.setText("There are no events");
                relatedLayout.addView(noEvents);
            } else {
                ListView eventList = new ListView(getContext());
                eventList.setAdapter(new MapBottomEventListAdapter(getContext(), events));
                relatedLayout.addView(eventList);
            }
            pinMarker(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
            mBottomSheetBehavior.setPeekHeight(headerText.getLayout().getHeight() + fab.getHeight() + 42);
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

            ArrayList<LatLng> path = graphWrapper.shortestPath(source, destination);
            if (current != null) current.remove();
            current = map.addPolyline(new PolylineOptions()
                    .addAll(path)
                    .width(4)
                    .color(Color.GREEN)
                    .geodesic(true));

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
                        if (markerList != null && markerList.size() > 0)
                            markerList.get(0).remove();
                        markerList.clear();
                        southWest = new LatLng(55.752533, 48.742492);
                        northEast = new LatLng(55.754656, 48.744589);
                        isMarkerSorted(1);
                        putOverlayToMap(southWest, northEast, BitmapDescriptorFactory.fromResource(R.raw.ai6_floor1));
                        setFloorPOIHashMap(1);
                        break;
                    case R.id.button2:
                        if (markerList != null && markerList.size() > 0)
                            markerList.get(0).remove();
                        markerList.clear();
                        southWest = new LatLng(55.752828, 48.742661);
                        northEast = new LatLng(55.754597, 48.744469);
                        isMarkerSorted(2);
                        putOverlayToMap(southWest, northEast, BitmapDescriptorFactory.fromResource(R.raw.ai6_floor2));
                        setFloorPOIHashMap(2);
                        break;
                    case R.id.button3:
                        if (markerList != null && markerList.size() > 0)
                            markerList.get(0).remove();
                        markerList.clear();
                        southWest = new LatLng(55.752875, 48.742739);
                        northEast = new LatLng(55.754572, 48.744467);
                        isMarkerSorted(3);
                        putOverlayToMap(southWest, northEast, BitmapDescriptorFactory.fromResource(R.raw.ai6_floor3));
                        setFloorPOIHashMap(3);
                        break;
                    case R.id.button4:
                        if (markerList != null && markerList.size() > 0)
                            markerList.get(0).remove();
                        markerList.clear();
                        southWest = new LatLng(55.752789, 48.742711);
                        northEast = new LatLng(55.754578, 48.744569);
                        isMarkerSorted(4);
                        putOverlayToMap(southWest, northEast, BitmapDescriptorFactory.fromResource(R.raw.ai6_floor4));
                        setFloorPOIHashMap(4);
                        break;
                    case R.id.button5:
                        if (markerList != null && markerList.size() > 0)
                            markerList.get(0).remove();
                        markerList.clear();
                        southWest = new LatLng(55.752808, 48.743497);
                        northEast = new LatLng(55.753383, 48.744519);
                        isMarkerSorted(5);
                        putOverlayToMap(southWest, northEast, BitmapDescriptorFactory.fromResource(R.raw.ai6_floor5));
                        setFloorPOIHashMap(5);
                        break;
                }
            }
        });

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

    public void pinMarker(LatLng latLng) {
        if (markerList != null && markerList.size() > 0) {
            markerList.get(0).remove();
            markerList.clear();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(findClosestPOI(latLng));
        markerOptions.position(closest == null ? latLng : closest);
        Marker marker = map.addMarker(markerOptions);
        marker.showInfoWindow();
        markerList.add(marker);
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

    private String findClosestPOI(LatLng latLng) {
        if (latLngMap != null) {
            Iterator iterator = latLngMap.entrySet().iterator();
            double distance, closestDistance = Double.MAX_VALUE;
            String lat = "", lng = "";
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                distance = Utils.haversine(latLng.latitude, latLng.longitude, Double.parseDouble(pair.getKey().toString()), Double.parseDouble(pair.getValue().toString()));
                if (distance < closestDistance) {
                    closestDistance = distance;
                    lat = pair.getKey().toString();
                    lng = pair.getValue().toString();
                }
            }
            closest = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            String sqlQuery = "SELECT " + POI_NAME + " FROM " + POI + " WHERE " + LATITUDE + "=?" + " AND " + LONGITUDE + "=?";
            Cursor cursor = database.rawQuery(sqlQuery, new String[]{lat, lng});
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(POI_NAME));
        } else {
            closest = null;
            return "";
        }
    }

    private void isMarkerSorted(int floor) {
        int filter = sort.get(0);
        if (filter == 0) {
            makeWcMarkers(floor);
        } else if (filter == 1) {
            makeFoodMarkers(floor);
        } else if (filter == 2) {
            makeAllMarkers(floor);
        } else if (filter == 3) {
            makeAllMarkers(floor);
        } else if (filter == 4) {
            makeOtherMarkers(floor);
        }
    }


    private void makeWcMarkers(int floor) {
        String numFloor = String.valueOf(floor) + "floor";

        String sqlQuery = "SELECT * FROM " + POI + " WHERE " + FLOOR + "=?" + " AND " + TYPE + " like 'wc'";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{numFloor});
        refreshMarkers(cursor);

    }

    private void makeFoodMarkers(int floor) {
        String numFloor = String.valueOf(floor) + "floor";

        String sqlQuery = "SELECT * FROM " + POI + " WHERE " + FLOOR + "= ?" + " AND " + TYPE + " = 'food'";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{numFloor});
        refreshMarkers(cursor);

    }

    private void makeOtherMarkers(int floor) {
        String selection = FLOOR + " = ? AND (" + TYPE + " = ? or " + TYPE + " = ?)";
        String[] selectionArgs = {floor + "floor", "wc", "food"};
        Cursor cursor = database.query(POI, null, selection, selectionArgs, null, null, null);
        refreshMarkers(cursor);

    }

    private void makeAllMarkers(int floor) {
        String selection = FLOOR + " = ? AND (" + TYPE + " = ? or " + TYPE + " = ? or " + TYPE + " = ?)";
        String[] selectionArgs = {floor + "floor", "room", "wc", "food"};
        Cursor cursor = database.query(POI, null, selection, selectionArgs, null, null, null);
        refreshMarkers(cursor);

    }

    private void refreshMarkers(Cursor cursor) {
        if (markers != null) {
            for (Marker marker : markers) {
                marker.remove();
            }
        }
        markers.clear();


        if (cursor.moveToFirst()) {
            do {
                String room = cursor.getString(cursor.getColumnIndex(POI_NAME));
                String type = cursor.getString(cursor.getColumnIndex(TYPE));
                String latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
                String longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
                setMarkersRoom(room, type, latitude, longitude);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void setMarkersRoom(String room, String type, String latitude, String longitude) {

        float center = (float) 0.5;
        final Marker markersRoom = map.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmapAdapter(type)))
                        .title(room)
                        .anchor(center,center)

        );

        markers.add(markersRoom);

        map.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (markers.contains(marker)) {
                    searchMarker(marker);
                }
                return false;
            }
        });
    }


    public Bitmap bitmapAdapter(String type) {
        Bitmap src;

        Bitmap icon;

        if (type.equals("room")) {
            src = BitmapFactory.decodeResource(getResources(), R.drawable.room_icon);

        } else if (type.equals("wc")) {
            src = BitmapFactory.decodeResource(getResources(), R.drawable.room_icon_wc);

        } else if (type.equals("food")) {
            src = BitmapFactory.decodeResource(getResources(), R.drawable.room_icon_food);

        } else {
            src = BitmapFactory.decodeResource(getResources(), R.drawable.duck_map);
        }

        icon = Bitmap.createBitmap(src);
        return icon;

    }


    private void searchMarker(Marker marker) {
        String room = marker.getTitle();
        String sqlQuery = "SELECT * FROM " + POI + " WHERE " + TableFields.POI_NAME + " like '" + room.replaceAll("'", "''") + "';";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{});

        if (cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndex(ID));
            String type = cursor.getString(cursor.getColumnIndex(TYPE));
            String building = cursor.getString(cursor.getColumnIndex(BUILDING));
            String floor = cursor.getString(cursor.getColumnIndex(FLOOR));

            SearchableItem item = new SearchableItem();
            item.setBuilding(building);
            item.setType(type);
            item.setFloor(floor);
            item.setRoom(room);
            item.setId(id);
            inSearchBottomList(item, mapView);
        }
        cursor.close();
    }

    public void showRoute(LatLng source, LatLng destination) {
        graphWrapper = new JGraphTWrapper(getContext());
        new RestRequest().execute(source, destination);
    }

    private void sortClearAdd(int num) {
        sort.clear();
        sort.add(num);
    }
}

