package com.innopolis.maps.innomaps;

import com.innopolis.maps.innomaps.pathfinding.JGraphTWrapper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Testing how quick finding the path between each vertices pair on the floor will perform.
 * Created by luckychess on 3/21/16.
 */
public class GraphMapPerformanceTest {
    JGraphTWrapper graph;

    @Before
    public void setUp() {
        graph = new JGraphTWrapper(null);
        assertNotNull(graph);
    }

    @Test
    public void firstFloorTest() {
        
    }
}
