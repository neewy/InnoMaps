package com.innopolis.maps.innomaps;

import android.test.AndroidTestCase;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseSync;
import com.innopolis.maps.innomaps.db.dataaccessobjects.BuildingDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.BuildingFloorOverlayDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateTypeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EdgeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EdgeTypeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventCreatorDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventScheduleDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.PhotoDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomTypeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.StreetDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Building;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingFloorOverlay;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.CoordinateType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Edge;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EdgeType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Event;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreator;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventSchedule;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Photo;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Street;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by alnedorezov on 7/7/16.
 */
public class DatabaseSyncTest extends AndroidTestCase {

    DatabaseSync databaseSync;

    @Before
    public void setUp() throws Exception {
        databaseSync = new DatabaseSync(this.getContext());
    }

    @Test
    public void testTypesSync() throws ParseException {
        databaseSync.saveLastSyncDate(com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(Constants.DEFAULT_SYNC_DATE));
        CoordinateTypeDAO coordinateTypeDAO = new CoordinateTypeDAO(this.getContext());
        EdgeTypeDAO edgeTypeDAO = new EdgeTypeDAO(this.getContext());
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO(this.getContext());
        CoordinateDAO coordinateDAO = new CoordinateDAO(this.getContext());
        EdgeDAO edgeDAO = new EdgeDAO(this.getContext());
        StreetDAO streetDAO = new StreetDAO(this.getContext());
        BuildingDAO buildingDAO = new BuildingDAO(this.getContext());
        RoomDAO roomDAO = new RoomDAO(this.getContext());
        PhotoDAO photoDAO = new PhotoDAO(this.getContext());
        BuildingFloorOverlayDAO buildingFloorOverlayDAO = new BuildingFloorOverlayDAO(this.getContext());
        EventCreatorDAO eventCreatorDAO = new EventCreatorDAO(this.getContext());
        EventDAO eventDAO = new EventDAO(this.getContext());
        EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(this.getContext());
        databaseSync.performSyncWithServer();
        databaseSync.saveLastSyncDate(new Date());

        List<CoordinateType> coordinateTypes = (List<CoordinateType>) coordinateTypeDAO.findAll();
        List<EdgeType> edgeTypes = (List<EdgeType>) edgeTypeDAO.findAll();
        List<RoomType> roomTypes = (List<RoomType>) roomTypeDAO.findAll();
        List<Coordinate> coordinates = (List<Coordinate>) coordinateDAO.findAll();
        List<Edge> edges = (List<Edge>) edgeDAO.findAll();
        List<Street> streets = (List<Street>) streetDAO.findAll();
        List<Building> buildings = (List<Building>) buildingDAO.findAll();
        List<Room> rooms = (List<Room>) roomDAO.findAll();
        List<Photo> photos = (List<Photo>) photoDAO.findAll();
        List<BuildingFloorOverlay> buildingFloorOverlays = (List<BuildingFloorOverlay>) buildingFloorOverlayDAO.findAll();
        List<EventCreator> eventCreators = (List<EventCreator>) eventCreatorDAO.findAll();
        List<Event> events = (List<Event>) eventDAO.findAll();
        List<EventSchedule> eventSchedules = (List<EventSchedule>) eventScheduleDAO.findAll();

        assertTrue(coordinateTypes.size() >= 11);
        assertTrue(edgeTypes.size() >= 2);
        assertTrue(roomTypes.size() >= 7);
        assertTrue(coordinates.size() >= 709);
        assertTrue(edges.size() >= 788);
        assertTrue(streets.size() >= 1);
        assertTrue(buildings.size() >= 1);
        assertTrue(rooms.size() >= 322);
        assertTrue(photos.size() > 0);
        assertTrue(buildingFloorOverlays.size() > 0);
        assertTrue(eventCreators.size() >= 15);
        assertTrue(events.size() >= 17);
        assertTrue(eventSchedules.size() >= 50);
    }
}
