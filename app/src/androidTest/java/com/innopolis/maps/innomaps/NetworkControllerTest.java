package com.innopolis.maps.innomaps;

import android.test.AndroidTestCase;

import com.innopolis.maps.innomaps.db.tablesrepresentations.Building;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingFloorOverlay;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingPhoto;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.CoordinateType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Edge;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EdgeType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Event;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreator;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreatorAppointment;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventSchedule;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Photo;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomPhoto;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Street;
import com.innopolis.maps.innomaps.maps.LatLngFlr;
import com.innopolis.maps.innomaps.network.Constants;
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.ClosestCoordinateWithDistance;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.TypesSync;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 6/15/16.
 */
public class NetworkControllerTest extends AndroidTestCase {

    NetworkController networkController;
    double testLatitude, testLongitude;
    int testFloor;
    String modifiedDateTime;
    String universityDescription;

    @Before
    public void setUp() throws Exception {
        networkController = new NetworkController();
        testLatitude = 55.75304847354006;
        testLongitude = 48.7436814921;
        testFloor = 2;
        modifiedDateTime = "2016-02-03 04:05:06.7";
        universityDescription = "Specializing in the field " +
                "of modern information technologies, Innopolis University is not only one of Russia’s youngest universities," +
                " but also the new city’s intellectual center.\n" +
                "The teaching staff consists of leading Russian and foreign IT specialists and robotic science.\n" +
                "Driven by the demands of both business and industry, the educational programs are committed to producing" +
                " a high-quality stream of professionals for companies located in Innopolis.";
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
        // Coordinate for Innopolis University are taken from Google Maps
        // Coordinate_type=2 (DAFAULT)
        Coordinate coordinateWithId1 = new Coordinate(1, 55.7541793, 48.744085, 1, 2, "Innopolis University", universityDescription, modifiedDateTime);

        Coordinate receivedCoordinate = networkController.getCoordinateById(1);

        assertEquals(coordinateWithId1, receivedCoordinate);
    }

    @Test
    public void testGetCoordinateTypeById() throws ParseException {
        // Coordinate_type=2 (DAFAULT)
        CoordinateType coordinateTypeWithId2 = new CoordinateType(2, "DEFAULT", modifiedDateTime);

        CoordinateType receivedCoordinateType = networkController.getCoordinateTypeById(2);

        assertEquals(coordinateTypeWithId2, receivedCoordinateType);
    }

    @Test
    public void testGetEdgeTypeById() throws ParseException {
        // Edge_type=1 (DAFAULT)
        EdgeType edgeTypeWithId1 = new EdgeType(1, "DEFAULT", modifiedDateTime);

        EdgeType receivedEdgeType = networkController.getEdgeTypeById(1);

        assertEquals(edgeTypeWithId1, receivedEdgeType);
    }

    @Test
    public void testGetEdgeById() throws ParseException {
        // Edge_type=1 (DAFAULT)
        Edge edgeWithId1 = new Edge(1, 1, 112, 33, modifiedDateTime);

        Edge receivedEdge = networkController.getEdgeById(1);

        assertEquals(edgeWithId1, receivedEdge);
    }

    @Test
    public void testGetRoomTypeById() throws ParseException {
        // Room_type=1 (ROOM)
        RoomType roomTypeWithId1 = new RoomType(1, "ROOM", modifiedDateTime);

        RoomType receivedRoomType = networkController.getRoomTypeById(1);

        assertEquals(roomTypeWithId1, receivedRoomType);
    }

    @Test
    public void testGetStreetById() throws ParseException {
        Street streetWithId1 = new Street(1, "Universitetskaya", modifiedDateTime);

        Street receivedStreet = networkController.getStreetById(1);

        assertEquals(streetWithId1, receivedStreet);
    }

    @Test
    public void testGetBuildingById() throws ParseException {
        Building buildingWithId1 = new Building(1, String.valueOf(1), null, universityDescription, 1, 1, modifiedDateTime);

        Building receivedBuilding = networkController.getBuildingById(1);

        assertEquals(buildingWithId1, receivedBuilding);
    }

    @Test
    public void testGetRoomById() throws ParseException {
        Room roomWithId1 = new Room(1, null, 1, 2, 6, modifiedDateTime);

        Room receivedRoom = networkController.getRoomById(1);

        assertEquals(roomWithId1, receivedRoom);
    }

    @Test
    public void testGetPhotoById() throws ParseException {
        Photo photoWithId1 = new Photo(1, "http://www.djpurviswoodfloors.co.uk/Images/laminate.gif", "2016-07-06 12:34:22.69");

        Photo receivedPhoto = networkController.getPhotoById(1);

        assertEquals(photoWithId1, receivedPhoto);
    }

    @Test
    public void testGetBuildingPhotosCreatedOnOrAfterDate() throws ParseException {
        BuildingPhoto requiredFirstBuildingPhotoInList = new BuildingPhoto(1, 2, "2016-07-04 23:28:37.363");

        Date date = Constants.serverDateFormat.parse("2016-07-04 22:48:03.001");
        BuildingPhoto receivedBuildingPhoto = networkController.getBuildingPhotosCreatedOnOrAfterDate(date).get(0);

        assertEquals(requiredFirstBuildingPhotoInList, receivedBuildingPhoto);
    }

    @Test
    public void testGetRoomPhotosCreatedOnOrAfterDate() throws ParseException {
        RoomPhoto requiredFirstRoomPhotoInList = new RoomPhoto(17, 1, "2016-07-06 18:33:44.291");

        Date date = Constants.serverDateFormat.parse("2016-07-06 18:25:10.267");
        RoomPhoto receivedRoomPhoto = networkController.getRoomPhotosCreatedOnOrAfterDate(date).get(0);

        assertEquals(requiredFirstRoomPhotoInList, receivedRoomPhoto);
    }

    @Test
    public void testGetEventCreatorById() throws ParseException {
        EventCreator eventCreatorWithId1 = new EventCreator(1, "GrishaTheTerrible", "gagaga@googlopochta.lol", "whatistelegramunywhay", "2016-07-01 11:26:42.481");

        EventCreator receivedEventCreator = networkController.getEventCreatorById(1);

        assertEquals(eventCreatorWithId1, receivedEventCreator);
    }

    @Test
    public void testGetEventById() throws ParseException {
        Event eventWithId1 = new Event(1, "Poedanie Pechenek Na Skorost", "you will love it", "", null, "2016-07-01 11:25:38.445");

        Event receivedEvent = networkController.getEventById(1);

        assertEquals(eventWithId1, receivedEvent);
    }

    @Test
    public void testGetEventScheduleById() throws ParseException {

        EventSchedule eventScheduleWithId1 = new EventSchedule(1, "2016-07-19 01:23:45.7", "2016-07-19 02:23:45.7", 24, "", 1, "2016-07-01 11:26:54.224");

        EventSchedule receivedEventSchedule = networkController.getEventScheduleById(1);

        assertEquals(eventScheduleWithId1, receivedEventSchedule);
    }

    @Test
    public void testGetBuildingFloorOverlayById() throws ParseException {

        BuildingFloorOverlay buildingFloorOverlayWithId1 = new BuildingFloorOverlay(1, 1, 2, 3, 4.0, 5.0, 6.0, 7.0, "2016-06-30 00:12:19.56");

        BuildingFloorOverlay receivedBuildingFloorOverlay = networkController.getBuildingFloorOverlayById(1);

        assertEquals(buildingFloorOverlayWithId1, receivedBuildingFloorOverlay);
    }

    @Test
    public void testGetEventCreatorAppointmentsCreatedOnOrAfterDate() throws ParseException {
        EventCreatorAppointment requiredFirstEventCreatorAppointmentInList = new EventCreatorAppointment(26, 8, "2016-07-06 21:35:20.159");

        Date date = Constants.serverDateFormat.parse("2016-07-06 21:35:20.001");
        EventCreatorAppointment receivedEventCreatorAppointment = networkController.getEventCreatorAppointmentsCreatedOnOrAfterDate(date).get(0);

        assertEquals(requiredFirstEventCreatorAppointmentInList, receivedEventCreatorAppointment);
    }

    @Test
    public void testTypesModifiedOnOrAfterDate() throws ParseException {
        Date date = Constants.serverDateFormat.parse(modifiedDateTime);
        TypesSync receivedTypesSync = networkController.getTypesModifiedOnOrAfterDate(date);

        assertEquals(Integer.valueOf(1), receivedTypesSync.getCoordinateTypeId(0));
        assertEquals(Integer.valueOf(1), receivedTypesSync.getEdgeTypeId(0));
        assertEquals(Integer.valueOf(1), receivedTypesSync.getRoomTypeId(0));
        assertTrue(receivedTypesSync.getCoordinateTypeIds().size() >= 11);
        assertTrue(receivedTypesSync.getEdgeTypeIds().size() >= 2);
        assertTrue(receivedTypesSync.getRoomTypeIds().size() >= 7);
    }
}
