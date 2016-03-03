package com.innopolis.maps.innomaps.app;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.pathfinding.JGraphTWrapper;
import com.innopolis.maps.innomaps.pathfinding.LatLngGraphEdge;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MapsFragment extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    MapView mapView; //an element of the layout
    private GoogleMap map;
    private UiSettings mSettings;

    DBHelper dbHelper;
    SQLiteDatabase database;

    SearchView searchView;
    SearchView.SearchAutoComplete searchBox;

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
        MapsInitializer.initialize(getActivity());

        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity())) {
            case ConnectionResult.SUCCESS:
                mapView = (MapView) v.findViewById(R.id.map);
                mapView.onCreate(savedInstanceState);
                if (mapView != null) {
                    map = mapView.getMap();
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                    map.setMyLocationEnabled(true);
                    mSettings = map.getUiSettings();
                    mSettings.setZoomControlsEnabled(true);
                    final LatLng university = new LatLng(55.752321, 48.744674);
                    Cursor cursor = database.query(DBHelper.TABLE3,null,null,null,null,null,null);
                    if (cursor.moveToFirst()) {
                        do {
                            String latitude = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_LATITIDE));
                            String longitude = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_LONGITUDE));
                            MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
                            map.addMarker(marker);
                        } while (cursor.moveToNext());
                    };
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(university, 15));
                    map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng latLng) {
                            LatLng p1 = new LatLng(55.2, 48.2);
                            LatLng p2 = new LatLng(55.2, 49.2);
                            LatLng p3 = new LatLng(56.2, 49.2);
                            LatLng p4 = new LatLng(56.5, 48.7);
                            LatLng end = new LatLng(56.2, 48.2);
                            JGraphTWrapper graphWrapper = new JGraphTWrapper(getContext());
                            graphWrapper.addVertex(university);
                            graphWrapper.addVertex(p1);
                            graphWrapper.addVertex(p2);
                            graphWrapper.addVertex(p3);
                            graphWrapper.addVertex(p4);
                            graphWrapper.addVertex(end);
                            graphWrapper.addEdge(university, p1, LatLngGraphEdge.EdgeType.DEFAULT);
                            graphWrapper.addEdge(p1, p2, LatLngGraphEdge.EdgeType.DEFAULT);
                            graphWrapper.addEdge(p2, p3, LatLngGraphEdge.EdgeType.DEFAULT);
                            graphWrapper.addEdge(p3, end, LatLngGraphEdge.EdgeType.ELEVATOR);
                            graphWrapper.addEdge(p3, p4, LatLngGraphEdge.EdgeType.DEFAULT);
                            graphWrapper.addEdge(p4, end, LatLngGraphEdge.EdgeType.DEFAULT);

                            ArrayList<LatLng> path = graphWrapper.shortestPath(university, end);
                            map.addPolyline(new PolylineOptions()
                                    .addAll(path)
                                    .width(12)
                                    .color(Color.BLACK)
                                    .geodesic(true));

                            ArrayList<LatLng> defaultPath = graphWrapper.defaultShortestPath(university, end);
                            map.addPolyline(new PolylineOptions()
                                    .addAll(defaultPath)
                                    .width(12)
                                    .color(Color.GREEN)
                                    .geodesic(true));

                            graphWrapper.exportGraphML("test.graphml");
                            try {
                                graphWrapper.importGraphML("test.graphml");
                            }
                            catch (XmlPullParserException | FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            map.clear();
                            path = graphWrapper.shortestPath(university, end);
                            map.addPolyline(new PolylineOptions()
                                    .addAll(path)
                                    .width(12)
                                    .color(Color.BLUE)
                                    .geodesic(true));

                            defaultPath = graphWrapper.defaultShortestPath(university, end);
                            map.addPolyline(new PolylineOptions()
                                    .addAll(defaultPath)
                                    .width(12)
                                    .color(Color.CYAN)
                                    .geodesic(true));

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

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.raw.ai6_floor1);
        LatLng southWest = new LatLng(55.752533,48.742492);
        LatLng northEast = new LatLng(55.754656,48.744589);
        LatLngBounds latLngBounds = new LatLngBounds(southWest, northEast);
        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
        groundOverlayOptions.positionFromBounds(latLngBounds);
        groundOverlayOptions.image(bitmapDescriptor);
        map.addGroundOverlay(groundOverlayOptions);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchBox = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String latitude = null;
                String longitude = null;
                CheckedTextView text = (CheckedTextView) view.findViewById(R.id.text1);
                String sqlQuery = "SELECT * FROM location inner join events on events.eventID=location.eventID WHERE events.summary=?";
                Cursor cursor = database.rawQuery(sqlQuery, new String[]{String.valueOf(text.getText())});
                if (cursor.moveToFirst()) {
                    do {
                        latitude = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_LATITIDE));
                        longitude = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_LONGITUDE));
                        map.clear();
                        map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude))));
                    } while (cursor.moveToNext());
                };
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 17));
            }
        });
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
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

    private Marker addMarker(LatLng point) {
        return map.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.test_custom_marker)));
    }


}

