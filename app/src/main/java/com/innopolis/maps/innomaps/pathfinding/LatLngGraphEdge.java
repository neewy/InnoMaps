package com.innopolis.maps.innomaps.pathfinding;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Implements custom graph edge for JGraphT based on DefaultEdge class.
 * Because of some internal JGraphT structure it's necessary to make this class public.
 * Created by luckychess on 2/22/16.
 */
public class LatLngGraphEdge extends DefaultWeightedEdge {
    public enum EdgeType {
        DEFAULT, ELEVATOR, STAIRS
    }

    EdgeType edgeType;

    public LatLngGraphEdge(EdgeType edgeType) {
        this.edgeType = edgeType;
    }

    public LatLngGraphVertex getV1() {
        return (LatLngGraphVertex) getSource();
    }

    public LatLngGraphVertex getV2() {
        return (LatLngGraphVertex) getTarget();
    }

    public EdgeType getEdgeType() {
        return edgeType;
    }

    @Override
    public boolean equals(Object o) {
        return getClass() == o.getClass() &&
                ((LatLngGraphEdge)o).getEdgeType().equals(edgeType) &&
                ((LatLngGraphEdge) o).getSource() == getSource() &&
                ((LatLngGraphEdge) o).getTarget() == getTarget();
    }
}