package com.innopolis.maps.innomaps.app;

/**
 * Created by Nikolay on 02.02.2016.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.innopolis.maps.innomaps.R;

import java.util.ArrayList;
import java.util.List;

public class Maps extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback {

    MapView mapView; //an element of the layout
    private GoogleMap map;
    private UiSettings mSettings;

    private Marker markerFrom;
    private Marker markerTo;

    private PathFinder pathFinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_maps, container, false);

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
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(university, 15));
                    map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng latLng) {
                            LatLng p1 = new LatLng(55.2, 48.2);
                            LatLng p2 = new LatLng(55.2, 49.2);
                            LatLng p3 = new LatLng(56.2, 49.2);
                            LatLng p4 = new LatLng(56.2, 48.2);
                            JGraphTWrapper graphWrapper = new JGraphTWrapper();
                            graphWrapper.addVertex(p1);
                            graphWrapper.addVertex(p2);
                            graphWrapper.addVertex(p3);
                            graphWrapper.addVertex(p4);
                            graphWrapper.addVertex(university);
                            graphWrapper.addEdge(university, p1);
                            graphWrapper.addEdge(p1, p2);
                            graphWrapper.addEdge(p2, p3);
                            graphWrapper.addEdge(p3, p4);
                            ArrayList<LatLng> path = graphWrapper.findShortestPath(university, p4);
                            map.addPolyline(new PolylineOptions()
                                    .addAll(path)
                                    .width(12)
                                    .color(Color.parseColor("#05b1fb"))     // Google maps blue color
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
        return v;
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

