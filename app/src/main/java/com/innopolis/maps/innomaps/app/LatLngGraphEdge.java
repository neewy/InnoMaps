package com.innopolis.maps.innomaps.app;

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.graph.DefaultEdge;

/**
 * Implements custom graph edge for JGraphT based on DefaultEdge class.
 * Because of some internal JGraphT structure it's necessary to make this class public.
 * Created by luckychess on 2/22/16.
 */
public class LatLngGraphEdge extends DefaultEdge {
    public enum EdgeType {
        DEFAULT, ELEVATOR, STAIRS
    }

    EdgeType edgeType;

    public LatLngGraphEdge(EdgeType edgeType) {
        this.edgeType = edgeType;
    }

    public LatLng getV1() {
        return (LatLng) getSource();
    }

    public LatLng getV2() {
        return (LatLng) getTarget();
    }

    public EdgeType getEdgeType() {
        return edgeType;
    }
}