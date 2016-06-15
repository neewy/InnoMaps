package com.innopolis.maps.innomaps;

import android.test.AndroidTestCase;

import com.innopolis.maps.innomaps.maps.LatLngFlr;
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.network.clientServerCommunicationClasses.ClosestCoordinateWithDistance;

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
        ClosestCoordinateWithDistance response = networkController.findClosestPointFromGraph(testLatitude, testLongitude, testFloor);
        double expectedLatitude, expectedLongitude, expectedDistance;
        int expectedFloor;
        expectedLatitude = 55.75304847354005;
        expectedLongitude = 48.7436814921;
        expectedFloor = 2;
        expectedDistance = 1.4210854715202004E-14;
        assertNotNull(response);
        assertEquals(expectedLatitude, response.getCoordinate().getLatitude());
        assertEquals(expectedLongitude, response.getCoordinate().getLongitude());
        assertEquals(expectedFloor, response.getCoordinate().getFloor());
        assertEquals(expectedDistance, response.getDistance());
    }
}
