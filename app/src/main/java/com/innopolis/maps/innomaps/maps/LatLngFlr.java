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

    @Override
    public int hashCode() {
        byte var2 = 1;
        long var3 = Double.doubleToLongBits(getLatitude());
        int var5 = 31 * var2 + (int) (var3 ^ var3 >>> 32);
        var3 = Double.doubleToLongBits(getLongitude());
        var5 = 31 * var5 + (int) (var3 ^ var3 >>> 32);
        var3 = getFloor();
        var5 = 31 * var5 + (int) (var3 ^ var3 >>> 32);
        return var5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof LatLng)) {
            return false;
        } else {
            LatLng var2 = (LatLng) o;
            return Double.doubleToLongBits(getLatitude()) == Double.doubleToLongBits(var2.getLatitude()) && Double.doubleToLongBits(getLongitude()) == Double.doubleToLongBits(var2.getLongitude());
        }
    }

    @Override
    public String toString() {
        return String.format("coordinates: (%1$s, %2$s, %3$s)", getLatitude(), getLongitude(), getFloor());
    }
}
