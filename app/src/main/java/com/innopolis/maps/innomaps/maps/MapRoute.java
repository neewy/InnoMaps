package com.innopolis.maps.innomaps.maps;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.pathfinding.LatLngGraphVertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class represents an object, responsible for handling shortest path parts
 * to different floors of the university building.

 * Created by neewy on 16.04.16.
 */
public class MapRoute {

    GoogleMap mMap; //GoogleMap, that stores polyline and markers

    /*Map, that contains floors and paths, combining navigation path*/
    Map<String, ArrayList<LatLngGraphVertex>> currentNavPath = new ConcurrentHashMap<>();

    /*Endpoints for each path*/
    Map<String, LatLngGraphVertex> pathEndpoints = new ConcurrentHashMap<>();

    /*Current path that is displayed on mMap*/
    PolylineWrapper current = new PolylineWrapper();

    /*An array of different markers for different zoom levels*/
    SparseArray<List<Marker>> markerEndpointsZoom = new SparseArray<>();

    String from; //starting floor
    String to; //ending floor
    Integer currentZoom; //current zoom level

    boolean isPathUp; //indicates whether the path is in ascending order of floors
    public boolean hasCurrentPath; //indicates whether the path has been displayed on map

    List<String> floorRange; //a range of floors

    Activity activity; //current activity

    RadioGroup floorPicker; //floorPicker object, that is used to handle different map overlays

    public MapRoute(GoogleMap mMap, ArrayList<LatLngGraphVertex> path, Activity activity, RadioGroup floorPicker) {
        this.mMap = mMap;
        this.activity = activity;
        setCurrentZoom(Double.valueOf(Math.floor((double) mMap.getCameraPosition().zoom)).intValue()); //initial zoom level

        from = String.valueOf(path.get(0).getVertexId()).substring(0,1);
        to = String.valueOf(path.get(path.size()-1).getVertexId()).substring(0,1);
        isPathUp = (to.compareTo(from) > 0);

        this.floorPicker = floorPicker;

        splitPathtoFloors(currentNavPath, path);
        getEndpoints(currentNavPath, pathEndpoints);
        setFloorRange();
    }

    /**
     * Sets zoom level and redraws markers, if such exist
     * @param currentZoom
     */
    public void setCurrentZoom(Integer currentZoom) {
        if (this.currentZoom == null) {
            this.currentZoom = currentZoom;
        } else if (!this.currentZoom.equals(currentZoom)) {
            redrawMarkers(currentZoom);
        }

    }

    /**
     * Splits shortest path into individual parts, corresponding to floors
     * @param currentNavPath - map, that contains there parts
     * @param path - initial shortest path
     */
    public void splitPathtoFloors(Map<String, ArrayList<LatLngGraphVertex>> currentNavPath, ArrayList<LatLngGraphVertex> path) {
        currentNavPath.clear();
        if (path == null) return;
        ArrayList<LatLngGraphVertex> pathPart = new ArrayList<>();
        LatLngGraphVertex vertexTemp = new LatLngGraphVertex(path.get(0));
        for (LatLngGraphVertex vertex : path) {
            String vertexTempID = String.valueOf(vertexTemp.getVertexId());
            String vertexID = String.valueOf(vertex.getVertexId());
            if (vertexTempID.substring(0, 1).equals(vertexID.substring(0, 1))) {
                pathPart.add(vertexTemp);
                vertexTemp = vertex;
            } else {
                pathPart.add(vertexTemp);
                currentNavPath.put(vertexTempID.substring(0, 1), pathPart);
                pathPart = new ArrayList<>();
                vertexTemp = vertex;
            }
        }
        if (pathPart.size() != 0) {
            pathPart.add(vertexTemp);
            String lastVerticeId = String.valueOf(path.get(path.size() - 1).getVertexId());
            currentNavPath.put(lastVerticeId.substring(0, 1), pathPart);
        }

        for (String floor : currentNavPath.keySet()){
            if (currentNavPath.get(floor).size() == 1) {
                currentNavPath.remove(floor); //eliminating parts, that consist of one point
            }
        }
    }

    /**
     * Populates pathEndpoints with endpoints
     * @param currentNavPath
     * @param pathEndpoints
     */
    public void getEndpoints(Map<String, ArrayList<LatLngGraphVertex>> currentNavPath, Map<String, LatLngGraphVertex> pathEndpoints) {
        for (String floor: currentNavPath.keySet()) {
            LatLngGraphVertex begin = currentNavPath.get(floor).get(0);
            LatLngGraphVertex end = currentNavPath.get(floor).get(currentNavPath.get(floor).size()-1);
            pathEndpoints.put(floor + "_0", begin);
            pathEndpoints.put(floor + "_1", end);
        }
    }

    /**
     * Starts a route on GoogleMap, drawing initial path and markers for it
     */
    public void startRoute() {
        drawPathOnMap(mMap, from, currentNavPath.get(from));
        putMarkerEndpoints();
        redrawMarkers(currentZoom);
        hasCurrentPath = true;
    }

    /**
     * Draws next part of a route
     */
    public void nextPath() {
        Integer nextFloorIndex = floorRange.indexOf(current.getFloor()) + 1;
        try {
            String nextFloor = floorRange.get(nextFloorIndex);
            drawPathOnMap(mMap, nextFloor, currentNavPath.get(nextFloor));
            putMarkerEndpoints();
            redrawMarkers(Double.valueOf(Math.floor((double) mMap.getCameraPosition().zoom)).intValue());
        } catch (IndexOutOfBoundsException e) {
            startRoute();
        }
    }

    /**
     * Draws previous part of a route
     */
    public void prevPath() {
        Integer prevFloorIndex = floorRange.indexOf(current.getFloor()) - 1;
        try {
            String prevFloor = floorRange.get(prevFloorIndex);
            drawPathOnMap(mMap, prevFloor, currentNavPath.get(prevFloor));
            putMarkerEndpoints();
            redrawMarkers(Double.valueOf(Math.floor((double) mMap.getCameraPosition().zoom)).intValue());
        } catch (IndexOutOfBoundsException e) {
            startRoute();
        }
    }

    /**
     * Method, that should be called when the route is finished
     * @param showMessage
     */
    public void finishRoute(boolean showMessage) {
        current.deleteFromMap();
        cleanMarkerEndpointsZoom();
        current = null;
        hasCurrentPath = false;
        if (showMessage) {
            Toast.makeText(activity, "You have reached the destination", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Draws path on GoogleMap
     * @param map - GoogleMap, where a path should be drawn
     * @param floor - floor, that contains the path
     * @param path - ArrayList of vertices, that should be drawn on a map
     */
    public void drawPathOnMap(GoogleMap map, String floor, ArrayList<LatLngGraphVertex> path) {
        RadioButton button = ((RadioButton)floorPicker.getChildAt(5 - Integer.parseInt(floor)));
        if (!button.isChecked()) button.setChecked(true);

        if (!current.isEmpty()) current.deleteFromMap();

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions
                .width(7)
                .color(Color.parseColor("#b4f57c00")) //current style color
                .geodesic(true);
        for (LatLngGraphVertex v : path) {
            polylineOptions.add(v.getVertex());
        }
        current.setPolyline(map, polylineOptions);
        current.setFloor(floor);
    }

    /**
     * Puts active markers, that are linked to current floor
     * Notice that they could only be identified by their snippets only.
     */
    public void putMarkerEndpoints() {

        cleanMarkerEndpointsZoom();

        float center = 0.5f;

        LatLngGraphVertex begin = pathEndpoints.get(current.getFloor()+"_0");
        LatLngGraphVertex end = pathEndpoints.get(current.getFloor()+"_1");

        if (from.equals(to)) {

            for (int i = 16; i < 20; i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                int size = (int) (30 + ((i - 15)/0.1));

                markerOptions
                        .icon(convertDrawable(R.drawable.route_finish, size))
                        .position(end.getVertex())
                        .anchor(center, center)
                        .snippet("FINISH")
                        .visible(false);

                Marker marker = mMap.addMarker(markerOptions);
                ArrayList<Marker> markers = new ArrayList<>();
                markers.add(marker);
                markerEndpointsZoom.put(i, markers);
            }

        } else {
            if (current.getFloor().equals(from)) {

                for (int i = 16; i < 20; i++) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    int size = (int) (30 + ((i - 15) / 0.1));

                    if (isPathUp) markerOptions.icon(convertDrawable(R.drawable.route_up, size));
                    else markerOptions.icon(convertDrawable(R.drawable.route_down, size));

                    markerOptions
                            .position(end.getVertex())
                            .anchor(center, center)
                            .snippet("NEXT")
                            .visible(false);

                    Marker marker = mMap.addMarker(markerOptions);
                    ArrayList<Marker> markers = new ArrayList<>();
                    markers.add(marker);
                    markerEndpointsZoom.put(i, markers);
                }

            } else if (current.getFloor().equals(to)) {

                for (int i = 16; i < 20; i++) {
                    MarkerOptions markerOptionsBegin = new MarkerOptions();
                    MarkerOptions markerOptionsEnd = new MarkerOptions();
                    int size = (int) (30 + ((i - 15) / 0.1));

                    if (isPathUp) markerOptionsBegin.icon(convertDrawable(R.drawable.route_down, size));
                    else markerOptionsBegin.icon(convertDrawable(R.drawable.route_up, size));

                    markerOptionsBegin.position(begin.getVertex())
                            .anchor(center, center)
                            .snippet("PREV")
                            .visible(false);

                    markerOptionsEnd
                            .icon(convertDrawable(R.drawable.route_finish, size))
                            .position(end.getVertex())
                            .anchor(center, center)
                            .snippet("FINISH")
                            .visible(false);

                    Marker markerBegin = mMap.addMarker(markerOptionsBegin);
                    Marker markerEnd = mMap.addMarker(markerOptionsEnd);
                    ArrayList<Marker> markers = new ArrayList<>();
                    markers.add(markerBegin);
                    markers.add(markerEnd);
                    markerEndpointsZoom.put(i, markers);
                }

            } else {

                for (int i = 16; i < 20; i++) {
                    MarkerOptions markerOptionsBegin = new MarkerOptions();
                    MarkerOptions markerOptionsEnd = new MarkerOptions();
                    int size = (int) (30 + ((i - 15) / 0.1));

                    if (isPathUp) {
                        markerOptionsBegin
                                .icon(convertDrawable(R.drawable.route_down, size));
                        markerOptionsEnd
                                .icon(convertDrawable(R.drawable.route_up, size));
                    } else {
                        markerOptionsBegin
                                .icon(convertDrawable(R.drawable.route_up, size));
                        markerOptionsEnd
                                .icon(convertDrawable(R.drawable.route_down, size));
                    }

                    markerOptionsBegin
                            .position(begin.getVertex())
                            .snippet("PREV")
                            .visible(false);
                    markerOptionsEnd
                            .position(end.getVertex())
                            .snippet("NEXT")
                            .visible(false);

                    Marker markerBegin = mMap.addMarker(markerOptionsBegin);
                    Marker markerEnd = mMap.addMarker(markerOptionsEnd);
                    ArrayList<Marker> markers = new ArrayList<>();
                    markers.add(markerBegin);
                    markers.add(markerEnd);
                    markerEndpointsZoom.put(i, markers);
                }

            }
        }
    }

    /**
     * Updates markers in accordance to new zoom level
     * @param newZoom
     */
    public void redrawMarkers(Integer newZoom) {
        if (currentZoom == null) {
            setCurrentZoom(16);
        }

        for (Marker marker : markerEndpointsZoom.get(currentZoom)) {
            marker.setVisible(false);
        }
        if (markerEndpointsZoom.get(newZoom) != null) {
            for (Marker marker : markerEndpointsZoom.get(newZoom)) {
                marker.setVisible(true);
            }
        } else {
            if (newZoom > currentZoom) {
                int lastIndex = markerEndpointsZoom.keyAt(markerEndpointsZoom.size()-1);
                for (Marker marker : markerEndpointsZoom.get(lastIndex)) {
                    marker.setVisible(true);
                }
                this.currentZoom = lastIndex;
            } else {
                int firstIndex = markerEndpointsZoom.keyAt(0);
                for (Marker marker : markerEndpointsZoom.get(firstIndex)) {
                    marker.setVisible(true);
                }
                this.currentZoom = firstIndex;
            }
        }
    }

    /**
     * Adds markers and polylines to GoogleMap,
     * if the floor was manually selected by a user.
     * @param floorId - button of a level picker, that was clicked
     */
    public void addMarkerPolylineToMap(int floorId) {
        String floor = "";
        switch (floorId) {
            case R.id.button1:
                floor = "1";
                break;
            case R.id.button2:
                floor = "2";
                break;
            case R.id.button3:
                floor = "3";
                break;
            case R.id.button4:
                floor = "4";
                break;
            case R.id.button5:
                floor = "5";
                break;
        }
        if (!current.isNull() && !floor.equals(current.getFloor()) && floorRange.contains(floor)) {
            drawPathOnMap(mMap, floor, currentNavPath.get(floor));
            putMarkerEndpoints();
            redrawMarkers(Double.valueOf(Math.floor((double) mMap.getCameraPosition().zoom)).intValue());
        } else if (!current.isNull() && !floor.equals(current.getFloor())) {
            current.setFloor(floor);
            current.deleteFromMap();
            cleanMarkerEndpointsZoom();
        }
    }

    /**
     * Sets an available floor range
     */
    public void setFloorRange() {
        floorRange = Arrays.asList(currentNavPath.keySet().toArray(new String[currentNavPath.keySet().size()]));
        Collections.sort(floorRange);
        if (!floorRange.get(0).equals(from)){
            Collections.sort(floorRange, new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    return -lhs.compareTo(rhs);
                }
            });
        }
    }

    /**
     * Converts drawable to a bitmap resource
     * @param drawable
     * @param size - size in pixels
     * @return bitmap object
     */
    public BitmapDescriptor convertDrawable(int drawable, int size) {
        BitmapDescriptor icon;
        Drawable shape;

        Bitmap markerBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(markerBitmap);
        shape = activity.getResources().getDrawable(drawable);
        if (shape != null) {
            shape.setBounds(0, 0, markerBitmap.getWidth(), markerBitmap.getHeight());
            shape.draw(canvas);
        }
        icon = BitmapDescriptorFactory.fromBitmap(markerBitmap);

        return icon;
    }

    /**
     * Cleans collection of endpoints
     */
    public void cleanMarkerEndpointsZoom() {
        if (!(markerEndpointsZoom.size() == 0)) {
            int first = 16;
            int last = 20;
            for (int i = first; i < last; i++) {
                if (markerEndpointsZoom.get(i) != null) {
                    for (Marker marker: markerEndpointsZoom.get(i)){
                        marker.remove();
                    }
                }
            }
            markerEndpointsZoom.clear();
        }

    }

}
