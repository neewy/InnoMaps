package com.innopolis.maps.innomaps.maps;

/**
 * Implements custom graph vertex for JGraphT based on DefaultEdge class.
 * Necessary for export and import functions.
 * Created by alnedorezov on 7/4/16.
 */
public class LatLngFlrGraphVertex {
    private LatLngGraphVertex.GraphElementType graphVertexType;
    private LatLngFlr vertex;
    private int vertexId;

    public LatLngFlrGraphVertex(LatLngFlr vertex, int vertexId, LatLngGraphVertex.GraphElementType graphVertexType) {
        this.vertex = vertex;
        this.vertexId = vertexId;
        this.graphVertexType = graphVertexType;
    }

    public LatLngFlrGraphVertex(LatLngFlrGraphVertex vertex) {
        this.vertex = vertex.getVertex();
        this.vertexId = vertex.getVertexId();
    }

    // For deserialization with Jackson
    public LatLngFlrGraphVertex() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public LatLngFlr getVertex() {
        return vertex;
    }

    public int getVertexId() {
        return vertexId;
    }

    public LatLngGraphVertex.GraphElementType getGraphVertexType() {
        return graphVertexType;
    }

    @Override
    public boolean equals(Object o) {
        return getClass() == o.getClass() && ((LatLngGraphVertex) o).getVertex().equals(getVertex());
    }

    @Override
    public int hashCode() {
        return vertex.hashCode();
    }
}
