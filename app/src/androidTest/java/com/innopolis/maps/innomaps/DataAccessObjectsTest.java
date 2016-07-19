package com.innopolis.maps.innomaps;

import android.test.AndroidTestCase;

import com.innopolis.maps.innomaps.db.dataaccessobjects.BuildingDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.BuildingFloorOverlayDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.BuildingPhotoDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateTypeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EdgeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EdgeTypeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventCreatorAppointmentDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventCreatorDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventScheduleDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.PhotoDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomPhotoDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomTypeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.StreetDAO;
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
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventFavorable;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventSchedule;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Photo;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomPhoto;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Street;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

/**
 * Created by alnedorezov on 7/12/16.
 */

public class DataAccessObjectsTest extends AndroidTestCase {
    String modifiedDateTime;
    String universityDescription;

    @Before
    public void setUp() throws Exception {
        // If tests do not pass, increment DATABASE_VERSION in db/Constants.java or delete current db version through adb shell
        modifiedDateTime = "2015-01-02 03:04:05.6";
        universityDescription = "Specializing in the field " +
                "of modern information technologies, Innopolis University is not only one of Russia’s youngest universities," +
                " but also the new city’s intellectual center.\n" +
                "The teaching staff consists of leading Russian and foreign IT specialists and robotic science.\n" +
                "Driven by the demands of both business and industry, the educational programs are committed to producing" +
                " a high-quality stream of professionals for companies located in Innopolis.";
    }

    @Test
    public void testWritingNewCoordinateDataToMobileDB() throws ParseException {
        CoordinateDAO coordinateDAO = new CoordinateDAO(this.getContext());

        Coordinate coordinateFromMobileDatabaseWithMaxId;

        Coordinate newCoordinate = new Coordinate(1, 55.75417935, 48.7440855, 1, 2, "Innopolis University", universityDescription, modifiedDateTime);
        coordinateDAO.create(newCoordinate);

        coordinateFromMobileDatabaseWithMaxId = (Coordinate) coordinateDAO.getObjectWithMaxId();

        assertEquals(newCoordinate, coordinateFromMobileDatabaseWithMaxId);
        coordinateDAO.delete(newCoordinate);
        assertFalse(coordinateDAO.findAll().size() > 0 && newCoordinate == coordinateDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewBuildingDataToMobileDB() throws ParseException {
        BuildingDAO buildingDAO = new BuildingDAO(this.getContext());

        Building buildingFromMobileDatabaseWithMaxId;

        Building newBuilding = new Building(1, String.valueOf(1), 100, universityDescription, 1, 1, modifiedDateTime);
        buildingDAO.create(newBuilding);

        buildingFromMobileDatabaseWithMaxId = (Building) buildingDAO.getObjectWithMaxId();

        assertEquals(newBuilding, buildingFromMobileDatabaseWithMaxId);
        buildingDAO.delete(newBuilding);
        assertFalse(buildingDAO.findAll().size() > 0 && newBuilding == buildingDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewBuildingFloorOverlayDataToMobileDB() throws ParseException {
        BuildingFloorOverlayDAO buildingFloorOverlayDAO = new BuildingFloorOverlayDAO(this.getContext());

        BuildingFloorOverlay buildingFloorOverlayFromMobileDatabaseWithMaxId;

        BuildingFloorOverlay newBuildingFloorOverlay = new BuildingFloorOverlay(1, 1212121, 1313132, 3, 4.0, 5.0, 6.0, 7.0, modifiedDateTime);
        buildingFloorOverlayDAO.create(newBuildingFloorOverlay);

        buildingFloorOverlayFromMobileDatabaseWithMaxId = (BuildingFloorOverlay) buildingFloorOverlayDAO.getObjectWithMaxId();

        assertEquals(newBuildingFloorOverlay, buildingFloorOverlayFromMobileDatabaseWithMaxId);
        buildingFloorOverlayDAO.delete(newBuildingFloorOverlay);
        assertFalse(buildingFloorOverlayDAO.findAll().size() > 0 && newBuildingFloorOverlay == buildingFloorOverlayDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewBuildingPhotoDataToMobileDB() throws ParseException {
        BuildingPhotoDAO buildingPhotoDAO = new BuildingPhotoDAO(this.getContext());

        BuildingPhoto buildingPhotoFromMobileDatabase;

        BuildingPhoto newBuildingPhoto = new BuildingPhoto(1, 1231321, modifiedDateTime);
        buildingPhotoDAO.create(newBuildingPhoto);

        buildingPhotoFromMobileDatabase = (BuildingPhoto) buildingPhotoDAO.findByIds(1, 1231321);

        assertEquals(newBuildingPhoto, buildingPhotoFromMobileDatabase);
        buildingPhotoDAO.delete(newBuildingPhoto);
        assertTrue(buildingPhotoDAO.findAll().size() == 0 || buildingPhotoDAO.findByIds(1, 1231321) == null);
    }

    @Test
    public void testWritingNewCoordinateTypeDataToMobileDB() throws ParseException {
        CoordinateTypeDAO coordinateTypeDAO = new CoordinateTypeDAO(this.getContext());

        CoordinateType coordinateTypeFromMobileDatabaseWithMaxId;

        CoordinateType newCoordinateType = new CoordinateType(1, "DEMO", modifiedDateTime);
        coordinateTypeDAO.create(newCoordinateType);

        coordinateTypeFromMobileDatabaseWithMaxId = (CoordinateType) coordinateTypeDAO.getObjectWithMaxId();

        assertEquals(newCoordinateType, coordinateTypeFromMobileDatabaseWithMaxId);
        coordinateTypeDAO.delete(newCoordinateType);
        assertFalse(coordinateTypeDAO.findAll().size() > 0 && newCoordinateType == coordinateTypeDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewEdgeDataToMobileDB() throws ParseException {
        EdgeDAO edgeDAO = new EdgeDAO(this.getContext());

        Edge edgeFromMobileDatabaseWithMaxId;

        Edge newEdge = new Edge(1, 1, 112, 33, modifiedDateTime);
        edgeDAO.create(newEdge);

        edgeFromMobileDatabaseWithMaxId = (Edge) edgeDAO.getObjectWithMaxId();

        assertEquals(newEdge, edgeFromMobileDatabaseWithMaxId);
        edgeDAO.delete(newEdge);
        assertFalse(edgeDAO.findAll().size() > 0 && newEdge == edgeDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewEdgeTypeDataToMobileDB() throws ParseException {
        EdgeTypeDAO edgeTypeDAO = new EdgeTypeDAO(this.getContext());

        EdgeType edgeTypeFromMobileDatabaseWithMaxId;

        EdgeType newEdgeType = new EdgeType(1, "DEMO", modifiedDateTime);
        edgeTypeDAO.create(newEdgeType);

        edgeTypeFromMobileDatabaseWithMaxId = (EdgeType) edgeTypeDAO.getObjectWithMaxId();

        assertEquals(newEdgeType, edgeTypeFromMobileDatabaseWithMaxId);
        edgeTypeDAO.delete(newEdgeType);
        assertFalse(edgeTypeDAO.findAll().size() > 0 && newEdgeType == edgeTypeDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewEventCreatorAppointDataToMobileDB() throws ParseException {
        EventCreatorAppointmentDAO eventCreatorAppointmentDAO = new EventCreatorAppointmentDAO(this.getContext());

        EventCreatorAppointment eventCreatorAppointmentFromMobileDatabase;

        EventCreatorAppointment newEventCreatorAppointment = new EventCreatorAppointment(1, 1231321, modifiedDateTime);
        eventCreatorAppointmentDAO.create(newEventCreatorAppointment);

        eventCreatorAppointmentFromMobileDatabase = (EventCreatorAppointment) eventCreatorAppointmentDAO.findByIds(1, 1231321);

        assertEquals(newEventCreatorAppointment, eventCreatorAppointmentFromMobileDatabase);
        eventCreatorAppointmentDAO.delete(newEventCreatorAppointment);
        assertTrue(eventCreatorAppointmentDAO.findAll().size() == 0 || eventCreatorAppointmentDAO.findByIds(1, 1231321) == null);
    }

    @Test
    public void testWritingNewEventCreatorDataToMobileDB() throws ParseException {
        EventCreatorDAO eventCreatorDAO = new EventCreatorDAO(this.getContext());

        EventCreator eventCreatorFromMobileDatabaseWithMaxId;

        EventCreator newEventCreator = new EventCreator(1, "Chuvak", "gagaga@googlopochta.lol", "whatistelegramunywhay", modifiedDateTime);
        eventCreatorDAO.create(newEventCreator);

        eventCreatorFromMobileDatabaseWithMaxId = (EventCreator) eventCreatorDAO.getObjectWithMaxId();

        assertEquals(newEventCreator, eventCreatorFromMobileDatabaseWithMaxId);
        eventCreatorDAO.delete(newEventCreator);
        assertFalse(eventCreatorDAO.findAll().size() > 0 && newEventCreator == eventCreatorDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewEventDataToMobileDB() throws ParseException {
        EventDAO eventDAO = new EventDAO(this.getContext());

        EventFavorable eventFromMobileDatabaseWithMaxId;

        Event newEvent = new Event(1, "Poedanie Vafelek Na Skorost", "you will love it", "", null, modifiedDateTime);
        eventDAO.create(newEvent);

        eventFromMobileDatabaseWithMaxId = (EventFavorable) eventDAO.getObjectWithMaxId();
        EventFavorable newFavourableEvent = new EventFavorable(newEvent, false);

        assertEquals(newFavourableEvent, eventFromMobileDatabaseWithMaxId);
        eventDAO.delete(newFavourableEvent);
        assertFalse(eventDAO.findAll().size() > 0 && newFavourableEvent == eventDAO.getObjectWithMaxId());

        newFavourableEvent = new EventFavorable(2, "Poedanie Vafelek Na Skorost", "you will love it", "", null, modifiedDateTime, true);
        eventDAO.create(newFavourableEvent);

        eventFromMobileDatabaseWithMaxId = (EventFavorable) eventDAO.getObjectWithMaxId();

        assertEquals(newFavourableEvent, eventFromMobileDatabaseWithMaxId);
        eventDAO.delete(newFavourableEvent);
        assertFalse(eventDAO.findAll().size() > 0 && newFavourableEvent == eventDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewEventScheduleDataToMobileDB() throws ParseException {
        EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(this.getContext());

        EventSchedule eventScheduleFromMobileDatabaseWithMaxId;

        EventSchedule newEventSchedule = new EventSchedule(1, "2016-07-19 01:23:46.7", "2016-07-19 02:23:46.7", 24, "", 1, modifiedDateTime);
        eventScheduleDAO.create(newEventSchedule);

        eventScheduleFromMobileDatabaseWithMaxId = (EventSchedule) eventScheduleDAO.getObjectWithMaxId();

        assertEquals(newEventSchedule, eventScheduleFromMobileDatabaseWithMaxId);
        eventScheduleDAO.delete(newEventSchedule);
        assertFalse(eventScheduleDAO.findAll().size() > 0 && newEventSchedule == eventScheduleDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewPhotoDataToMobileDB() throws ParseException {
        PhotoDAO photoDAO = new PhotoDAO(this.getContext());

        Photo photoFromMobileDatabaseWithMaxId;

        Photo newPhoto = new Photo(1, "http://www.djpurviswoodfloors.co.uk/Images/laminate2.gif", modifiedDateTime);
        photoDAO.create(newPhoto);

        photoFromMobileDatabaseWithMaxId = (Photo) photoDAO.getObjectWithMaxId();

        assertEquals(newPhoto, photoFromMobileDatabaseWithMaxId);
        photoDAO.delete(newPhoto);
        assertFalse(photoDAO.findAll().size() > 0 && newPhoto == photoDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewRoomDataToMobileDB() throws ParseException {
        RoomDAO roomDAO = new RoomDAO(this.getContext());

        Room roomFromMobileDatabaseWithMaxId;

        Room newRoom = new Room(1, null, 1, 23432443, 6, modifiedDateTime);
        roomDAO.create(newRoom);

        roomFromMobileDatabaseWithMaxId = (Room) roomDAO.getObjectWithMaxId();

        assertEquals(newRoom, roomFromMobileDatabaseWithMaxId);
        roomDAO.delete(newRoom);
        assertFalse(roomDAO.findAll().size() > 0 && newRoom == roomDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewRoomPhotoDataToMobileDB() throws ParseException {
        RoomPhotoDAO roomPhotoDAO = new RoomPhotoDAO(this.getContext());

        RoomPhoto roomPhotoFromMobileDatabase;

        RoomPhoto newRoomPhoto = new RoomPhoto(1, 1231321, modifiedDateTime);
        roomPhotoDAO.create(newRoomPhoto);

        roomPhotoFromMobileDatabase = (RoomPhoto) roomPhotoDAO.findByIds(1, 1231321);

        assertEquals(newRoomPhoto, roomPhotoFromMobileDatabase);
        roomPhotoDAO.delete(newRoomPhoto);
        assertTrue(roomPhotoDAO.findAll().size() == 0 || roomPhotoDAO.findByIds(1, 1231321) == null);
    }

    @Test
    public void testWritingNewRoomTypeDataToMobileDB() throws ParseException {
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO(this.getContext());

        RoomType roomTypeFromMobileDatabaseWithMaxId;

        RoomType newRoomType = new RoomType(1, "DEMO", modifiedDateTime);
        roomTypeDAO.create(newRoomType);

        roomTypeFromMobileDatabaseWithMaxId = (RoomType) roomTypeDAO.getObjectWithMaxId();

        assertEquals(newRoomType, roomTypeFromMobileDatabaseWithMaxId);
        roomTypeDAO.delete(newRoomType);
        assertFalse(roomTypeDAO.findAll().size() > 0 && newRoomType == roomTypeDAO.getObjectWithMaxId());
    }

    @Test
    public void testWritingNewStreetDataToMobileDB() throws ParseException {
        StreetDAO streetDAO = new StreetDAO(this.getContext());

        Street streetFromMobileDatabaseWithMaxId;

        Street newStreet = new Street(1, "DEMO", modifiedDateTime);
        streetDAO.create(newStreet);

        streetFromMobileDatabaseWithMaxId = (Street) streetDAO.getObjectWithMaxId();

        assertEquals(newStreet, streetFromMobileDatabaseWithMaxId);
        streetDAO.delete(newStreet);
        assertFalse(streetDAO.findAll().size() > 0 && newStreet == streetDAO.getObjectWithMaxId());
    }
}
