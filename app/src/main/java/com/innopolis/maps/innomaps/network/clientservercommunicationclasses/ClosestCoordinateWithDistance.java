package com.innopolis.maps.innomaps.network.clientservercommunicationclasses;

import com.innopolis.maps.innomaps.maps.LatLngFlr;

/**
 * Created by alnedorezov on 6/15/16.
 */
public class ClosestCoordinateWithDistance {
    private LatLngFlr coordinate;
    private double distance;

    public ClosestCoordinateWithDistance(LatLngFlr coordinate, double distance) {
        this.coordinate = coordinate;
        this.distance = distance;
    }

    // For deserialization with Jackson
    public ClosestCoordinateWithDistance() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public LatLngFlr getCoordinate() {
        return coordinate;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ClosestCoordinateWithDistance))
            return false;

        ClosestCoordinateWithDistance that = (ClosestCoordinateWithDistance) o;

        if (Double.compare(that.getDistance(), getDistance()) != 0)
            return false;
        return getCoordinate() != null ? getCoordinate().equals(that.getCoordinate()) : that.getCoordinate() == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getCoordinate() != null ? getCoordinate().hashCode() : 0;
        temp = Double.doubleToLongBits(getDistance());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
