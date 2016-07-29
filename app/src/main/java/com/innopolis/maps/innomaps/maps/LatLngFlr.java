package com.innopolis.maps.innomaps.maps;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by alnedorezov on 6/15/16.
 */
public class LatLngFlr extends LatLng {

    private int floor;

    public LatLngFlr(double latitude, double longitude, int floor) {
        super(latitude, longitude);
        this.floor = floor;
    }

    // For deserialization with Jackson
    public LatLngFlr() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public double getLatitude() {
        return super.getLatitude();
    }

    public double getLongitude() {
        return super.getLongitude();
    }

    public int getFloor() {
        return floor;
    }

    @JsonIgnore
    public LatLng getLatLng() {
        return new LatLng(getLatitude(), getLongitude());
    }

    @JsonIgnore
    public com.google.android.gms.maps.model.LatLng getAndroidGMSLatLng() {
        return new com.google.android.gms.maps.model.LatLng(getLatitude(), getLongitude());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof LatLngFlr))
            return false;
        if (!super.equals(o))
            return false;

        LatLngFlr latLngFlr = (LatLngFlr) o;

        return getFloor() == latLngFlr.getFloor();

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getFloor();
        return result;
    }

    @Override
    public String toString() {
        return String.format("coordinates: (%1$s, %2$s, %3$s)", getLatitude(), getLongitude(), getFloor());
    }
}
