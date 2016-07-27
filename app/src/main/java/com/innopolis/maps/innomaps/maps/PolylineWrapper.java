package com.innopolis.maps.innomaps.maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class PolylineWrapper {

    int floor;
    Polyline polyline;
    PolylineOptions polylineOptions;

    public PolylineWrapper() {
    }

    public PolylineWrapper(int floor, Polyline polyline, PolylineOptions polylineOptions) {
        this.floor = floor;
        this.polyline = polyline;
        this.polylineOptions = polylineOptions;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(GoogleMap map, PolylineOptions options) {
        this.polylineOptions = options;
        this.polyline = map.addPolyline(options);
    }

    public PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public void setPolylineOptions(PolylineOptions polylineOptions) {
        this.polylineOptions = polylineOptions;
    }

    public void deleteFromMap() {
        polyline.remove();
    }

    public boolean isEmpty() {
        return polyline == null;
    }

    public boolean isNull() {
        return polyline == null & polylineOptions == null;
    }
}
