package com.innopolis.maps.innomaps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class to find and show route between 2 points on a map.
 * Created by luckychess on 1/31/16.
 */
public class PathFinder {
    LatLng from;
    LatLng to;

    public PathFinder(LatLng from, LatLng to) {
        this.from = from;
        this.to = to;
    }
}
