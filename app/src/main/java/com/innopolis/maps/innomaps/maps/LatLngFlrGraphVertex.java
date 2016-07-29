package com.innopolis.maps.innomaps.maps;

/**
 * Implements custom graph vertex for JGraphT based on DefaultEdge class.
 * Necessary for export and import functions.
 * Created by alnedorezov on 7/4/16.
 */
public class LatLngFlrGraphVertex {
    public enum GraphElementType {
        DEFAULT, ELEVATOR, STAIRS
    }

    private GraphElementType graphVertexType;
    private LatLngFlr vertex;
    private int vertexId;

    public LatLngFlrGraphVertex(LatLngFlr vertex, int vertexId, GraphElementType graphVertexType) {
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

    public GraphElementType getGraphVertexType() {
        return graphVertexType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LatLngFlrGraphVertex))
            return false;

        LatLngFlrGraphVertex that = (LatLngFlrGraphVertex) o;

        if (getVertexId() != that.getVertexId())
            return false;
        if (getGraphVertexType() != that.getGraphVertexType())
            return false;
        return getVertex() != null ? getVertex().equals(that.getVertex()) : that.getVertex() == null;

    }

    @Override
    public int hashCode() {
        int result = getGraphVertexType() != null ? getGraphVertexType().hashCode() : 0;
        result = 31 * result + (getVertex() != null ? getVertex().hashCode() : 0);
        result = 31 * result + getVertexId();
        return result;
    }
}
