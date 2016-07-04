package com.innopolis.maps.innomaps;

import android.test.AndroidTestCase;

import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.CoordinateType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Edge;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EdgeType;
import com.innopolis.maps.innomaps.maps.LatLngFlr;
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.network.clientServerCommunicationClasses.ClosestCoordinateWithDistance;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

/**
 * Created by alnedorezov on 6/15/16.
 */
public class NetworkControllerTest extends AndroidTestCase {

    NetworkController networkController;
    double testLatitude, testLongitude;
    int testFloor;
    String modifiedDateTime;

    @Before
    public void setUp() throws Exception {
        networkController = new NetworkController();
        testLatitude = 55.75304847354006;
        testLongitude = 48.7436814921;
        testFloor = 2;
        modifiedDateTime = "2016-02-03 04:05:06.7";
    }

    @Test
    public void testFindingClosestPointFromGraph() {
        ClosestCoordinateWithDistance response = networkController.findClosestPointFromGraph(testLatitude, testLongitude, testFloor);
        double expectedLatitude, expectedLongitude, expectedDistance;
        int expectedFloor;
        expectedLatitude = 55.75304847354005;
        expectedLongitude = 48.7436814921;
        expectedFloor = 2;
        expectedDistance = 1.5806213947811135E-12;
        ClosestCoordinateWithDistance expectedClosestCoordinateWithDistance =
                new ClosestCoordinateWithDistance(new LatLngFlr(expectedLatitude, expectedLongitude, expectedFloor), expectedDistance);
        assertNotNull(response);
        assertEquals(expectedClosestCoordinateWithDistance, response);
    }

    @Test
    public void testGetCoordinateId() throws ParseException {
        String IU_description = "Specializing in the field " +
                "of modern information technologies, Innopolis University is not only one of Russia’s youngest universities," +
                " but also the new city’s intellectual center.\n" +
                "The teaching staff consists of leading Russian and foreign IT specialists and robotic science.\n" +
                "Driven by the demands of both business and industry, the educational programs are committed to producing" +
                " a high-quality stream of professionals for companies located in Innopolis.";
        // Coordinate for Innopolis University are taken from Google Maps
        // Coordinate_type=2 (DAFAULT)
        Coordinate coordinateWithId1 = new Coordinate(1, 55.7541793, 48.744085, 1, 2, "Innopolis University", IU_description, modifiedDateTime);

        Coordinate reseivedCoordinate = networkController.getCoordinateById(1);

        assertEquals(coordinateWithId1, reseivedCoordinate);
    }

    @Test
    public void testGetCoordinateTypeById() throws ParseException {
        // Coordinate_type=2 (DAFAULT)
        CoordinateType coordinateTypeWithId2 = new CoordinateType(2, "DEFAULT");

        CoordinateType reseivedCoordinateType = networkController.getCoordinateTypeById(2);

        assertEquals(coordinateTypeWithId2, reseivedCoordinateType);
    }

    @Test
    public void testGetEdgeTypeById() throws ParseException {
        // Edge_type=1 (DAFAULT)
        EdgeType edgeTypeWithId1 = new EdgeType(1, "DEFAULT");

        EdgeType reseivedEdgeType = networkController.getEdgeTypeById(1);

        assertEquals(edgeTypeWithId1, reseivedEdgeType);
    }

    @Test
    public void testGetEdgeById() throws ParseException {
        // Edge_type=1 (DAFAULT)
        Edge edgeWithId1 = new Edge(1, 1, 112, 33, modifiedDateTime);

        Edge reseivedEdge = networkController.getEdgeById(1);

        assertEquals(edgeWithId1, reseivedEdge);
    }
}
