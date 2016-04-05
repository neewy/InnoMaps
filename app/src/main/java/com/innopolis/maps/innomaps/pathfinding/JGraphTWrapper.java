package com.innopolis.maps.innomaps.pathfinding;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.innopolis.maps.innomaps.utils.Utils;

import org.jgrapht.Graph;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Wrapper for JGraphT library. Creating graphs, adding vertices and edges, searching for
 * the shortest paths and so on.
 */
public class JGraphTWrapper {

    private SimpleWeightedGraph<LatLngGraphVertex, LatLngGraphEdge> graph;
    private int currentVertexId;


    public JGraphTWrapper() {
        graph = new SimpleWeightedGraph<>(LatLngGraphEdge.class);
        currentVertexId = 0;
    }

    /**
     * Adds new vertex.
     * @param v - vertex to add
     */
    public void addVertex(LatLng v) {
        addVertexWithId(v, currentVertexId++);
    }

    private void addVertexWithId(LatLng v, int id) {
        LatLngGraphVertex vTemp = new LatLngGraphVertex(v, id);
        graph.addVertex(vTemp);
    }

    /**
     * Adds new edge of given type.
     * @param v1 - vertex edge begins
     * @param v2 - vertex edge ends
     * @param edgeType - edge type (see LatLngGraphEdge.EdgeType)
     */
    public void addEdge(LatLng v1, LatLng v2, LatLngGraphEdge.EdgeType edgeType) {
        LatLngGraphVertex gv1 = new LatLngGraphVertex(v1, 0);
        LatLngGraphVertex gv2 = new LatLngGraphVertex(v2, 0);
        graph.addEdge(gv1, gv2, new LatLngGraphEdge(edgeType));
        LatLngGraphEdge e = graph.getEdge(gv1, gv2);
        double penaltyWeight = (edgeType == LatLngGraphEdge.EdgeType.DEFAULT) ? 0.0 : 1.0;
        graph.setEdgeWeight(e, Utils.haversine(gv1.getVertex().latitude, gv1.getVertex().longitude,
                gv2.getVertex().latitude, gv2.getVertex().longitude) + penaltyWeight);
    }

    /**
     * Shortest path using all edges.
     * @param v1 - start LatLng
     * @param v2 - end LatLng
     * @return sequential list of LatLng objects
     */
    public ArrayList<LatLngGraphVertex> shortestPath(LatLng v1, LatLng v2) {
        return shortestPathForGraph(v1, v2, graph);
    }

    private ArrayList<LatLngGraphVertex> shortestPathForGraph(LatLng v1, LatLng v2, Graph<LatLngGraphVertex, LatLngGraphEdge> g) {
        LatLngGraphVertex vTemp1 = new LatLngGraphVertex(v1, 0);
        LatLngGraphVertex vTemp2 = new LatLngGraphVertex(v2, 0);

        DijkstraShortestPath<LatLngGraphVertex, LatLngGraphEdge> dijkstraPathFinder = new DijkstraShortestPath<>(g, vTemp1, vTemp2);
        List<LatLngGraphEdge> foundPath = dijkstraPathFinder.getPathEdgeList();
        if (foundPath == null || foundPath.size() == 0) {
            return null;
        }
        Log.d("graph", Double.toString(dijkstraPathFinder.getPathLength()));
        ArrayList<LatLngGraphVertex> pointsList = new ArrayList<>();
        LatLngGraphVertex testVertexFrom = foundPath.get(0).getV1();
        LatLngGraphVertex testVertexTo = foundPath.get(0).getV2();
        pointsList.add(testVertexFrom.equals(vTemp1) ?  testVertexFrom : testVertexTo);
        for (int i = 0; i < foundPath.size(); ++i) {
            testVertexFrom = foundPath.get(i).getV1();
            testVertexTo = foundPath.get(i).getV2();
            pointsList.add(pointsList.get(pointsList.size()-1).equals(testVertexFrom) ? testVertexTo : testVertexFrom);
        }
        return pointsList;
    }

    /**
     * Shortest path with only default edges.
     * @param v1 - start LatLng
     * @param v2 - end LatLng
     * @return sequential list of LatLng objects
     */
    public ArrayList<LatLngGraphVertex> defaultShortestPath(LatLng v1, LatLng v2) {
        Set<LatLngGraphEdge> oldEdges = graph.edgeSet();
        Set<LatLngGraphEdge> defaultEdges = new HashSet<>();
        for (LatLngGraphEdge edge : oldEdges) {
            if (edge.getEdgeType() == LatLngGraphEdge.EdgeType.DEFAULT) {
                defaultEdges.add(edge);
            }
        }
        UndirectedWeightedSubgraph<LatLngGraphVertex, LatLngGraphEdge> defaultEdgesGraph =
                new UndirectedWeightedSubgraph<>(graph, null, defaultEdges);
        return shortestPathForGraph(v1, v2, defaultEdgesGraph);
    }

    /**
     * Stores graph into the file using GraphML format.
     * @param filename - exported file name
     */
    public void exportGraphML(String filename) {
        //TODO: implement (since JGraphT export sucks)
    }

    /**
     * Imports graph from the file of GraphML format. Doesn't return anything but if import was
     * successful internal graph object will be replaced by the imported one.
     * @param inputStream - stream to read.
     */
    public void importGraphML(InputStream inputStream) throws XmlPullParserException, FileNotFoundException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(br);
        int eventType = xpp.getEventType();

        graph = new SimpleWeightedGraph<>(LatLngGraphEdge.class);
        currentVertexId = 0;
        HashMap<Integer, LatLng> verticesMap = new HashMap<>();
        int id = -1;
        boolean nodeDataFound = false;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                String tagName = xpp.getName();
                switch (tagName) {
                    case "node":
                        id = Integer.valueOf(xpp.getAttributeValue(null, "id"));
                        break;
                    case "edge":
                        int from = Integer.valueOf(xpp.getAttributeValue(null, "source"));
                        int to = Integer.valueOf(xpp.getAttributeValue(null, "target"));
                        String type = xpp.getAttributeValue(null, "id");
                        Log.d("graph edge", "type = " + type + " source = " + from + " target = " + to);
                        LatLngGraphEdge.EdgeType edgeType = LatLngGraphEdge.EdgeType.DEFAULT;
                        switch (type) {
                            case "ELEVATOR":
                                edgeType = LatLngGraphEdge.EdgeType.ELEVATOR;
                                break;
                            case "STAIRS":
                                edgeType = LatLngGraphEdge.EdgeType.STAIRS;
                                break;
                            case "DEFAULT":
                                edgeType = LatLngGraphEdge.EdgeType.DEFAULT;
                                break;
                        }
                        addEdge(verticesMap.get(from), verticesMap.get(to), edgeType);
                        break;
                    case "data":
                        if (id != -1) {
                            nodeDataFound = true;
                        }
                        break;
                }
            } else if(eventType == XmlPullParser.TEXT) {
                if (id != -1 && nodeDataFound) {
                    String[] coords = xpp.getText().split(" ");
                    Log.d("graph", "text: " + xpp.getText());
                    LatLng latLng = new LatLng(Double.valueOf(coords[0]), Double.valueOf(coords[1]));
                    addVertexWithId(latLng, id);
                    verticesMap.put(id, latLng);
                    id = -1;
                    nodeDataFound = false;
                }
            }

            try {
                eventType = xpp.next();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LatLngGraphVertex[] getVertices() {
        LatLngGraphVertex[] v = new LatLngGraphVertex[graph.vertexSet().size()];
        v = graph.vertexSet().toArray(v);
        return v;
    }
}