package com.innopolis.maps.innomaps;

import android.test.AndroidTestCase;

import com.innopolis.maps.innomaps.maps.LatLngFlr;
import com.innopolis.maps.innomaps.network.NetworkController;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by alnedorezov on 6/15/16.
 */
public class NetworkControllerTest extends AndroidTestCase {

    NetworkController networkController;
    double testLatitude, testLongitude;
    int testFloor;

    @Before
    public void setUp() throws Exception {
        networkController = new NetworkController();
        testLatitude = 55.75304847354006;
        testLongitude = 48.7436814921;
        testFloor = 2;
    }

    @Test
    public void testFindingClosestPointFromGraph() {
        LatLngFlr response = networkController.findClosestPointFromGraph(testLatitude, testLongitude, testFloor);
        assertNotNull(response);
        assertEquals(55.75304847354005, response.getLatitude());
        assertEquals(48.7436814921, response.getLongitude());
        assertEquals(2, response.getFloor());
    }
}
