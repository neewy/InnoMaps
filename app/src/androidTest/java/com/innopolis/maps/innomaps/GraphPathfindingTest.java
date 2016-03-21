package com.innopolis.maps.innomaps;

import com.google.android.gms.maps.model.LatLng;
import com.innopolis.maps.innomaps.pathfinding.JGraphTWrapper;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for graph related activity.
 * Created by luckychess on 3/19/16.
 */

public class GraphPathfindingTest {

    JGraphTWrapper graph;

    @Before
    public void setUp() {
        graph = new JGraphTWrapper(null);
        assertNotNull(graph);
    }

    @Test
    public void singleNodeGraphTest() {
        LatLng vertex = new LatLng(55.75351526583595, 48.74356482177973);
        graph.addVertex(vertex);
        ArrayList<LatLng> path = graph.shortestPath(vertex, vertex);
        assertTrue(path == null);
    }

    @Test
    public void graphPathfindTest() throws Exception {
        String url = Utils.restServerUrl + "/innomaps/graphml/loadmap?floor=1";
        String result = Utils.doGetRequest(url);
        assertTrue(!result.equals(""));
        graph.importGraphML(IOUtils.toInputStream(result));
        LatLng start = new LatLng(55.75351526583595, 48.74356482177973);
        LatLng finish = new LatLng(55.75421676452847, 48.74331135302782);
        ArrayList<LatLng> path = graph.shortestPath(start, finish);

        assertTrue(path.size() == 15);
        assertTrue(path.get(0).equals(start));
        assertTrue(path.get(1).equals(new LatLng(55.75348224716271, 48.74349173158407)));
        assertTrue(path.get(2).equals(new LatLng(55.75357602012165, 48.74336734414101)));
        assertTrue(path.get(3).equals(new LatLng(55.753604887808095, 48.743326775729656)));
        assertTrue(path.get(4).equals(new LatLng(55.7536246989531, 48.74329425394535)));
        assertTrue(path.get(5).equals(new LatLng(55.75365149115225, 48.74325066804886)));
        assertTrue(path.get(6).equals(new LatLng(55.75367035888707, 48.74322418123484)));
        assertTrue(path.get(7).equals(new LatLng(55.75372054701731, 48.743225522339344)));
        assertTrue(path.get(8).equals(new LatLng(55.7537752633263, 48.74335091561079)));
        assertTrue(path.get(9).equals(new LatLng(55.753846771766135, 48.74340523034334)));
        assertTrue(path.get(10).equals(new LatLng(55.753936015624575, 48.743435740470886)));
        assertTrue(path.get(11).equals(new LatLng(55.7540992200384, 48.7434970960021)));
        assertTrue(path.get(12).equals(new LatLng(55.75410431426925, 48.74345451593399)));
        assertTrue(path.get(13).equals(new LatLng(55.754117710206394, 48.743341863155365)));
        assertTrue(path.get(14).equals(finish));
    }
}