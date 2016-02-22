package com.innopolis.maps.innomaps.app;

import com.google.android.gms.maps.model.LatLng;
import com.innopolis.maps.innomaps.utils.LatLngGraphEdge;

import org.jgrapht.graph.*;
import org.jgrapht.alg.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for JGraphT library. Creating graphs, adding vertices and edges, searching for
 * the shortest paths and so on.
 */
public class JGraphTWrapper {
    private SimpleGraph<LatLng, LatLngGraphEdge> graph;

    public JGraphTWrapper() {
        graph = new SimpleGraph<>(LatLngGraphEdge.class);
    }

    public void addVertex(LatLng v) {
        graph.addVertex(v);
    }

    public void addEdge(LatLng v1, LatLng v2) {
        graph.addEdge(v1, v2);
    }

    public ArrayList<LatLng> findShortestPath(LatLng v1, LatLng v2) {
        List<LatLngGraphEdge> foundPath = DijkstraShortestPath.findPathBetween(graph, v1, v2);
        ArrayList<LatLng> pointsList = new ArrayList<>();
        for (LatLngGraphEdge edge :foundPath) {
            pointsList.add((edge).getV1());
        }
        pointsList.add(((foundPath.get(foundPath.size()-1))).getV2());
        return pointsList;
    }
}