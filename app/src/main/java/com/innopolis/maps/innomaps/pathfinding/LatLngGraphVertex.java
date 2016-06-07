package com.innopolis.maps.innomaps.pathfinding;

import com.google.android.gms.maps.model.LatLng;

/**
 * Implements custom graph vertex for JGraphT based on DefaultEdge class.
 * Necessary for export and import functions.
 * Created by luckychess on 2/28/16.
 */
public class LatLngGraphVertex {
    private JGraphTWrapper.GraphElementType graphVertexType;
    private LatLng vertex;
    private int vertexId;

    public LatLngGraphVertex(LatLng vertex, int vertexId, JGraphTWrapper.GraphElementType graphVertexType) {
        this.vertex = vertex;
        this.vertexId = vertexId;
        this.graphVertexType = graphVertexType;
    }

    public LatLngGraphVertex(LatLngGraphVertex vertex) {
        this.vertex = vertex.getVertex();
        this.vertexId = vertex.getVertexId();
    }

    public LatLng getVertex() {
        return vertex;
    }

    public int getVertexId() {
        return vertexId;
    }

    public JGraphTWrapper.GraphElementType getGraphVertexType() {
        return graphVertexType;
    }

    @Override
    public boolean equals(Object o) {
        return getClass() == o.getClass() && ((LatLngGraphVertex)o).getVertex().equals(getVertex());
    }

    @Override
    public int hashCode() {
        return vertex.hashCode();
    }
}
