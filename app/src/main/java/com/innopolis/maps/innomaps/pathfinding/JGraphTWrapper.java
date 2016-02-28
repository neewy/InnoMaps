package com.innopolis.maps.innomaps.pathfinding;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.Graph;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;
import org.jgrapht.ext.GraphMLExporter;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.TransformerConfigurationException;

/**
 * Wrapper for JGraphT library. Creating graphs, adding vertices and edges, searching for
 * the shortest paths and so on.
 */
public class JGraphTWrapper {

    private class VertexIdProvider<V> implements org.jgrapht.ext.VertexNameProvider<V> {

        /**
         * Returns a unique name for a vertex. This is useful when exporting a a
         * graph, as it ensures that all vertices are assigned simple, consistent
         * names.
         *
         * @param vertex the vertex to be named
         * @return the name of the vertex
         */
        @Override
        public String getVertexName(V vertex) {
            LatLngGraphVertex v = (LatLngGraphVertex)vertex;
            return String.valueOf(v.getVertexId());
        }
    }

    private class VertexNameProvider<V> implements org.jgrapht.ext.VertexNameProvider<V> {

        /**
         * Returns a unique name for a vertex. This is useful when exporting a a
         * graph, as it ensures that all vertices are assigned simple, consistent
         * names.
         *
         * @param vertex the vertex to be named
         * @return the name of the vertex
         */
        @Override
        public String getVertexName(V vertex) {
            return String.valueOf(((LatLngGraphVertex) vertex).getVertex().latitude) +
                    " " + String.valueOf(((LatLngGraphVertex) vertex).getVertex().longitude);
        }
    }

    private class EdgeIdProvider<E> implements org.jgrapht.ext.EdgeNameProvider<E> {

        /**
         * Returns a unique name for an edge. This is useful when exporting a graph,
         * as it ensures that all edges are assigned simple, consistent names.
         *
         * @param edge the edge to be named
         * @return the name of the edge
         */
        @Override
        public String getEdgeName(E edge) {
            return ((LatLngGraphEdge)edge).getEdgeType().toString();
        }
    }

    private SimpleGraph<LatLngGraphVertex, LatLngGraphEdge> graph;
    private Context context;
    private int currentVertexId;
    private HashMap<LatLng, Integer> vertices;
    private HashMap<Integer, LatLng> verticesMap;

    public JGraphTWrapper(Context context) {
        graph = new SimpleGraph<>(LatLngGraphEdge.class);
        currentVertexId = 0;
        vertices = new HashMap<>();
        verticesMap = new HashMap<>();
        this.context = context;
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
        vertices.put(v, id);
        verticesMap.put(id, v);
    }

    /**
     * Adds new edge of given type.
     * @param v1 - vertex edge begins
     * @param v2 - vertex edge ends
     * @param edgeType - edge type (see LatLngGraphEdge.EdgeType)
     */
    public void addEdge(LatLng v1, LatLng v2, LatLngGraphEdge.EdgeType edgeType) {
        LatLngGraphVertex vTemp1 = new LatLngGraphVertex(v1, vertices.get(v1));
        LatLngGraphVertex vTemp2 = new LatLngGraphVertex(v2, vertices.get(v2));
        graph.addEdge(vTemp1, vTemp2, new LatLngGraphEdge(edgeType));
    }

    private void addEdgeByIds(LatLngGraphEdge.EdgeType edgeType, int from, int to) {
        LatLngGraphVertex vTemp1 = new LatLngGraphVertex(verticesMap.get(from), from);
        LatLngGraphVertex vTemp2 = new LatLngGraphVertex(verticesMap.get(to), to);
        graph.addEdge(vTemp1, vTemp2, new LatLngGraphEdge(edgeType));
    }

    /**
     * Shortest path using all edges.
     * @param v1 - start LatLng
     * @param v2 - end LatLng
     * @return sequential list of LatLng objects
     */
    public ArrayList<LatLng> shortestPath(LatLng v1, LatLng v2) {
        return shortestPathForGraph(v1, v2, graph);
    }

    private ArrayList<LatLng> shortestPathForGraph(LatLng v1, LatLng v2, Graph<LatLngGraphVertex, LatLngGraphEdge> g) {
        LatLngGraphVertex vTemp1 = new LatLngGraphVertex(v1, vertices.get(v1));
        LatLngGraphVertex vTemp2 = new LatLngGraphVertex(v2, vertices.get(v2));
        List<LatLngGraphEdge> foundPath = DijkstraShortestPath.findPathBetween(g, vTemp1, vTemp2);
        ArrayList<LatLng> pointsList = new ArrayList<>();
        for (LatLngGraphEdge edge :foundPath) {
            pointsList.add((edge).getV1().getVertex());
        }
        pointsList.add(((foundPath.get(foundPath.size() - 1))).getV2().getVertex());
        return pointsList;
    }

    /**
     * Shortest path with only default edges.
     * @param v1 - start LatLng
     * @param v2 - end LatLng
     * @return sequential list of LatLng objects
     */
    public ArrayList<LatLng> defaultShortestPath(LatLng v1, LatLng v2) {
        Set<LatLngGraphEdge> oldEdges = graph.edgeSet();
        Set<LatLngGraphEdge> defaultEdges = new HashSet<>();
        for (LatLngGraphEdge edge : oldEdges) {
            if (edge.getEdgeType() == LatLngGraphEdge.EdgeType.DEFAULT) {
                defaultEdges.add(edge);
            }
        }
        UndirectedSubgraph<LatLngGraphVertex, LatLngGraphEdge> defaultEdgesGraph = new UndirectedSubgraph<>(graph, null, defaultEdges);
        return shortestPathForGraph(v1, v2, defaultEdgesGraph);
    }

    /**
     * Stores graph into the file using GraphML format.
     * @param filename - exported file name
     */
    public void exportGraphML(String filename) {
        VertexIdProvider<LatLngGraphVertex> vertexIdProvider = new VertexIdProvider<>();
        VertexNameProvider<LatLngGraphVertex> vertexNameProvider = new VertexNameProvider<>();
        EdgeIdProvider<LatLngGraphEdge> edgeIdProvider = new EdgeIdProvider<>();

        GraphMLExporter<LatLngGraphVertex, LatLngGraphEdge> graphMLExporter = new GraphMLExporter<>(vertexIdProvider,
                vertexNameProvider, edgeIdProvider, null);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            try {
                graphMLExporter.export(outputStreamWriter, graph);
            } catch (SAXException | TransformerConfigurationException e) {
                e.printStackTrace();
            }
            outputStreamWriter.close();
            Log.d("Graph Lib", "File saved successfully");
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     * Imports graph from the file of GraphML format. Doesn't return anything but if import was
     * successful internal graph object will be replaced by the imported one.
     * @param filename - import file name
     */
    public void importGraphML(String filename) throws XmlPullParserException, FileNotFoundException {
        File file = new File(context.getFilesDir(), filename);
        BufferedReader br = new BufferedReader(new FileReader(file));

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(br);
        int eventType = xpp.getEventType();

        graph = new SimpleGraph<>(LatLngGraphEdge.class);
        currentVertexId = 0;
        vertices = new HashMap<>();
        verticesMap = new HashMap<>();
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
                        Log.i("graph edge", "type = " + type + " source = " + from + " target = " + to);
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
                        addEdgeByIds(edgeType, from, to);
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
                    Log.i("graph", "text: " + xpp.getText());
                    addVertexWithId(new LatLng(Double.valueOf(coords[0]), Double.valueOf(coords[1])), id);
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
}