package com.innopolis.maps.innomaps.maps;

/**
 * Created by alnedorezov on 6/7/16.
 */
public class LatLng {

    public double latitude;
    public double longitude;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // For deserialization with Jackson
    public LatLng() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int hashCode() {
        byte var2 = 1;
        long var3 = Double.doubleToLongBits(getLatitude());
        int var5 = 31 * var2 + (int) (var3 ^ var3 >>> 32);
        var3 = Double.doubleToLongBits(getLongitude());
        var5 = 31 * var5 + (int) (var3 ^ var3 >>> 32);
        return var5;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof LatLng)) {
            return false;
        } else {
            LatLng var2 = (LatLng) o;
            return Double.doubleToLongBits(getLatitude()) == Double.doubleToLongBits(var2.getLatitude()) &&
                    Double.doubleToLongBits(getLongitude()) == Double.doubleToLongBits(var2.getLongitude());
        }
    }

    public String toString() {
        return String.format("coordinates: (%1$s, %2$s)", getLatitude(), getLongitude());
    }
}
