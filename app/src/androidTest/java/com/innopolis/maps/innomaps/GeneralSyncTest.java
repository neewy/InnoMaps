package com.innopolis.maps.innomaps;

import android.test.AndroidTestCase;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseSync;
import com.innopolis.maps.innomaps.db.dataaccessobjects.BuildingAuxiliaryCoordinateDAO;
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
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingAuxiliaryCoordinate;
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
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.GeneralSync;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by alnedorezov on 7/15/16.
 */
public class GeneralSyncTest extends AndroidTestCase {

    NetworkController networkController;
    DatabaseSync databaseSync;

    @Before
    public void setUp() throws Exception {
        // TODO: increment DATABASE_VERSION in db/Constants.java or delete current db version through adb shell before running the test
        networkController = new NetworkController();
        databaseSync = new DatabaseSync(this.getContext());
    }

    @Test
    public void testGeneralDataSync() throws ParseException {
        databaseSync.saveLastSyncDate(com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(Constants.DEFAULT_SYNC_DATE), DatabaseSync.syncTypes.GENERAL);

        CoordinateTypeDAO coordinateTypeDAO = new CoordinateTypeDAO(this.getContext());
        EdgeTypeDAO edgeTypeDAO = new EdgeTypeDAO(this.getContext());
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO(this.getContext());
        CoordinateDAO coordinateDAO = new CoordinateDAO(this.getContext());
        EdgeDAO edgeDAO = new EdgeDAO(this.getContext());
        StreetDAO streetDAO = new StreetDAO(this.getContext());
        BuildingDAO buildingDAO = new BuildingDAO(this.getContext());
        RoomDAO roomDAO = new RoomDAO(this.getContext());
        PhotoDAO photoDAO = new PhotoDAO(this.getContext());
        BuildingPhotoDAO buildingPhotoDAO = new BuildingPhotoDAO(this.getContext());
        RoomPhotoDAO roomPhotoDAO = new RoomPhotoDAO(this.getContext());
        BuildingFloorOverlayDAO buildingFloorOverlayDAO = new BuildingFloorOverlayDAO(this.getContext());
        EventCreatorDAO eventCreatorDAO = new EventCreatorDAO(this.getContext());
        EventDAO eventDAO = new EventDAO(this.getContext());
        EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(this.getContext());
        EventCreatorAppointmentDAO eventCreatorAppointmentDAO = new EventCreatorAppointmentDAO(this.getContext());
        BuildingAuxiliaryCoordinateDAO buildingAuxiliaryCoordinateDAO = new BuildingAuxiliaryCoordinateDAO(this.getContext());

        databaseSync.performGeneralSyncWithServer();
        databaseSync.saveLastSyncDate(new Date(), DatabaseSync.syncTypes.GENERAL);

        GeneralSync dataFromDatabase;
        GeneralSync.GeneralSyncBuilder generalSyncBuilder = new GeneralSync.GeneralSyncBuilder();

        generalSyncBuilder.setTypes((List<CoordinateType>) coordinateTypeDAO.findAll(), (List<EdgeType>) edgeTypeDAO.findAll(), (List<RoomType>) roomTypeDAO.findAll());
        generalSyncBuilder.setMapUnits((List<Coordinate>) coordinateDAO.findAll(), (List<Edge>) edgeDAO.findAll(), (List<Street>) streetDAO.findAll(),
                (List<Building>) buildingDAO.findAll(), (List<Room>) roomDAO.findAll(), (List<Photo>) photoDAO.findAll(), (List<BuildingFloorOverlay>) buildingFloorOverlayDAO.findAll());
        generalSyncBuilder.setEvents((List<EventCreator>) eventCreatorDAO.findAll(), (List<Event>) eventDAO.findAll(), (List<EventSchedule>) eventScheduleDAO.findAll());
        generalSyncBuilder.setAssignments((List<BuildingPhoto>) buildingPhotoDAO.findAll(), (List<RoomPhoto>) roomPhotoDAO.findAll(),
                (List<EventCreatorAppointment>) eventCreatorAppointmentDAO.findAll(), (List<BuildingAuxiliaryCoordinate>) buildingAuxiliaryCoordinateDAO.findAll());
        dataFromDatabase = generalSyncBuilder.build();


        assertTrue(dataFromDatabase.getCoordinateTypes().size() >= 11);
        assertTrue(dataFromDatabase.getEdgeTypes().size() >= 2);
        assertTrue(dataFromDatabase.getRoomTypes().size() >= 7);
        assertTrue(dataFromDatabase.getCoordinates().size() >= 709);
        assertTrue(dataFromDatabase.getEdges().size() >= 788);
        assertTrue(dataFromDatabase.getStreets().size() >= 1);
        assertTrue(dataFromDatabase.getBuildings().size() >= 1);
        assertTrue(dataFromDatabase.getRooms().size() >= 322);
        assertTrue(dataFromDatabase.getPhotos().size() > 0);
        assertTrue(dataFromDatabase.getBuildingFloorOverlays().size() > 0);
        assertTrue(dataFromDatabase.getEventCreators().size() >= 15);
        assertTrue(dataFromDatabase.getEvents().size() >= 17);
        assertTrue(dataFromDatabase.getEventSchedules().size() >= 50);
        assertTrue(dataFromDatabase.getBuildingPhotos().size() > 0);
        assertTrue(dataFromDatabase.getRoomPhotos().size() > 0);
        assertTrue(dataFromDatabase.getEventCreatorAppointments().size() > 20);
        assertTrue(dataFromDatabase.getBuildingAuxiliaryCoordinates().size() >= 187);
    }

}