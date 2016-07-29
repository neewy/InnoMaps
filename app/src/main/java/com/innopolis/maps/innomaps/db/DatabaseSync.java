package com.innopolis.maps.innomaps.db;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.innopolis.maps.innomaps.app.MainActivity;
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
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventFavorable;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventSchedule;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Photo;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomPhoto;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Street;
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.EventsSync;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.GeneralSync;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.MapUnitsSync;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.TypesSync;
import com.innopolis.maps.innomaps.utils.Utils;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by alnedorezov on 7/11/16.
 */
public class DatabaseSync extends IntentService {

    private static Context context;
    private SharedPreferences sPref;
    private NetworkController networkController;

    public enum syncTypes {
        TYPES, MAP_UNITS, EVENTS, ASSIGNMENTS, GENERAL
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public DatabaseSync(Context context) {
        super("DatabaseSync");
        DatabaseSync.context = context;
    }

    public DatabaseSync() {
        super("DatabaseSync");
        DatabaseSync.context = MainActivity.getMainActivityContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (context != null && Utils.isNetworkAvailable(context)) {
            try {
                performSyncWithServer();
                Log.d(Constants.SYNC, Constants.SYNC_FINISHED_ON + com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(new Date()));
            } catch (ParseException e) {
                Log.e(Constants.SYNC_ERROR, e.getMessage(), e.fillInStackTrace());
            }
        }
    }

    public void performSyncWithServer() throws ParseException {
        synchronizeTypes();
        synchronizeMapUnits();
        synchronizeEvents();
        synchronizeAssignments();
        deleteRecordsDeletedFromServerDatabase();

        saveLastSyncDate(new Date(), syncTypes.GENERAL);
    }

    private void synchronizeTypes() throws ParseException {
        networkController = new NetworkController();
        TypesSync receivedTypesSync = networkController.getTypesModifiedOnOrAfterDate(loadLastSyncDate(syncTypes.TYPES));

        if (receivedTypesSync.getCoordinateTypeIds() != null && !receivedTypesSync.getCoordinateTypeIds().isEmpty()) {
            try {
                addNewCoordinateTypes(receivedTypesSync.getCoordinateTypeIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        if (receivedTypesSync.getEdgeTypeIds() != null && !receivedTypesSync.getEdgeTypeIds().isEmpty()) {
            try {
                addNewEdgeTypes(receivedTypesSync.getEdgeTypeIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        if (receivedTypesSync.getRoomTypeIds() != null && !receivedTypesSync.getRoomTypeIds().isEmpty()) {
            try {
                addNewRoomTypes(receivedTypesSync.getRoomTypeIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        saveLastSyncDate(new Date(), syncTypes.TYPES);
    }

    private void synchronizeMapUnits() throws ParseException {
        networkController = new NetworkController();
        MapUnitsSync receivedMapUnits = networkController.getMapUnitsModifiedOnOrAfterDate(loadLastSyncDate(syncTypes.MAP_UNITS));

        if (receivedMapUnits.getCoordinateIds() != null && !receivedMapUnits.getCoordinateIds().isEmpty()) {
            try {
                addNewCoordinates(receivedMapUnits.getCoordinateIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        if (receivedMapUnits.getEdgeIds() != null && !receivedMapUnits.getEdgeIds().isEmpty()) {
            try {
                addNewEdges(receivedMapUnits.getEdgeIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        if (receivedMapUnits.getStreetIds() != null && !receivedMapUnits.getStreetIds().isEmpty()) {
            try {
                addNewStreets(receivedMapUnits.getStreetIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        if (receivedMapUnits.getBuildingIds() != null && !receivedMapUnits.getBuildingIds().isEmpty()) {
            try {
                addNewBuildings(receivedMapUnits.getBuildingIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        if (receivedMapUnits.getRoomIds() != null && !receivedMapUnits.getRoomIds().isEmpty()) {
            try {
                addNewRooms(receivedMapUnits.getRoomIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        if (receivedMapUnits.getPhotoIds() != null && !receivedMapUnits.getPhotoIds().isEmpty()) {
            try {
                addNewPhotos(receivedMapUnits.getPhotoIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        if (receivedMapUnits.getBuildingFloorOverlayIds() != null && !receivedMapUnits.getBuildingFloorOverlayIds().isEmpty()) {
            try {
                addNewBuildingFloorOverlays(receivedMapUnits.getBuildingFloorOverlayIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        saveLastSyncDate(new Date(), syncTypes.MAP_UNITS);
    }

    private void synchronizeEvents() throws ParseException {
        networkController = new NetworkController();
        EventsSync receivedEventsSync = networkController.getEventsModifiedOnOrAfterDate(loadLastSyncDate(syncTypes.EVENTS));

        if (receivedEventsSync.getEventCreatorIds() != null && !receivedEventsSync.getEventCreatorIds().isEmpty()) {
            try {
                addNewEventCreators(receivedEventsSync.getEventCreatorIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        if (receivedEventsSync.getEventIds() != null && !receivedEventsSync.getEventIds().isEmpty()) {
            try {
                addNewEvents(receivedEventsSync.getEventIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        if (receivedEventsSync.getEventScheduleIds() != null && !receivedEventsSync.getEventScheduleIds().isEmpty()) {
            try {
                addNewEventSchedules(receivedEventsSync.getEventScheduleIds());
            } catch (ParseException e) {
                Log.d(Constants.SYNC_ERROR, Arrays.toString(e.getStackTrace()));
                e.printStackTrace();
            }
        }

        saveLastSyncDate(new Date(), syncTypes.EVENTS);
    }

    private void synchronizeAssignments() throws ParseException {
        networkController = new NetworkController();
        BuildingPhotoDAO buildingPhotoDAO = new BuildingPhotoDAO(context);
        RoomPhotoDAO roomPhotoDAO = new RoomPhotoDAO(context);
        EventCreatorAppointmentDAO eventCreatorAppointmentDAO = new EventCreatorAppointmentDAO(context);
        BuildingAuxiliaryCoordinateDAO buildingAuxiliaryCoordinateDAO = new BuildingAuxiliaryCoordinateDAO(context);
        Date syncDate = loadLastSyncDate(syncTypes.ASSIGNMENTS);

        List<BuildingPhoto> buildingPhotos = (List<BuildingPhoto>) networkController.getBuildingPhotosCreatedOnOrAfterDate(syncDate);
        List<RoomPhoto> roomPhotos = (List<RoomPhoto>) networkController.getRoomPhotosCreatedOnOrAfterDate(syncDate);
        List<EventCreatorAppointment> eventCreatorAppointments = (List<EventCreatorAppointment>) networkController.getEventCreatorAppointmentsCreatedOnOrAfterDate(syncDate);
        List<BuildingAuxiliaryCoordinate> buildingAuxiliaryCoordinates =
                (List<BuildingAuxiliaryCoordinate>) networkController.getBuildingAuxiliaryCoordinatesCreatedOnOrAfterDate(syncDate);

        for (int i = 0; i < buildingPhotos.size(); i++) {
            buildingPhotoDAO.createOrUpdateIfExists(buildingPhotos.get(i));
        }
        for (int i = 0; i < roomPhotos.size(); i++) {
            roomPhotoDAO.createOrUpdateIfExists(roomPhotos.get(i));
        }
        for (int i = 0; i < eventCreatorAppointments.size(); i++) {
            eventCreatorAppointmentDAO.createOrUpdateIfExists(eventCreatorAppointments.get(i));
        }
        for (int i = 0; i < buildingAuxiliaryCoordinates.size(); i++) {
            buildingAuxiliaryCoordinateDAO.createOrUpdateIfExists(buildingAuxiliaryCoordinates.get(i));
        }

        saveLastSyncDate(new Date(), syncTypes.ASSIGNMENTS);
    }

    private void deleteRecordsDeletedFromServerDatabase() {
        networkController = new NetworkController();
        GeneralSync generalData = networkController.getGeneralData();

        deleteCoordinateTypesDeletedFromTheServerDatabase(generalData.getCoordinateTypes());
        deleteEdgeTypesDeletedFromTheServerDatabase(generalData.getEdgeTypes());
        deleteRoomTypesDeletedFromTheServerDatabase(generalData.getRoomTypes());
        deleteCoordinatesDeletedFromTheServerDatabase(generalData.getCoordinates());
        deleteEdgesDeletedFromTheServerDatabase(generalData.getEdges());
        deleteStreetsDeletedFromTheServerDatabase(generalData.getStreets());
        deleteBuildingsDeletedFromTheServerDatabase(generalData.getBuildings());
        deleteRoomsDeletedFromTheServerDatabase(generalData.getRooms());
        deletePhotosDeletedFromTheServerDatabase(generalData.getPhotos());
        deleteBuildingPhotosDeletedFromTheServerDatabase(generalData.getBuildingPhotos());
        deleteRoomPhotosDeletedFromTheServerDatabase(generalData.getRoomPhotos());
        deleteBuildingFloorOverlaysDeletedFromTheServerDatabase(generalData.getBuildingFloorOverlays());
        deleteEventCreatorsDeletedFromTheServerDatabase(generalData.getEventCreators());
        deleteEventsDeletedFromTheServerDatabase(generalData.getEvents());
        deleteEventSchedulesDeletedFromTheServerDatabase(generalData.getEventSchedules());
        deleteEventCreatorAppointmentsDeletedFromTheServerDatabase(generalData.getEventCreatorAppointments());
        deleteBuildingAuxiliaryCoordinatesDeletedFromTheServerDatabase(generalData.getBuildingAuxiliaryCoordinates());
    }

    private void addNewCoordinateTypes(List<Integer> coordinateTypeIds) throws ParseException {
        CoordinateTypeDAO coordinateTypeDAO = new CoordinateTypeDAO(context);
        networkController = new NetworkController();
        for (Integer coordinateTypeId : coordinateTypeIds) {
            if (coordinateTypeId != null) {
                CoordinateType newCoordinateType = networkController.getCoordinateTypeById(coordinateTypeId);
                coordinateTypeDAO.createOrUpdateIfExists(newCoordinateType);
            }
        }
    }

    private void addNewEdgeTypes(List<Integer> edgeTypeIds) throws ParseException {
        EdgeTypeDAO edgeTypeDAO = new EdgeTypeDAO(context);
        networkController = new NetworkController();
        for (Integer edgeTypeId : edgeTypeIds) {
            if (edgeTypeId != null) {
                EdgeType newEdgeType = networkController.getEdgeTypeById(edgeTypeId);
                edgeTypeDAO.createOrUpdateIfExists(newEdgeType);
            }
        }
    }

    private void addNewRoomTypes(List<Integer> roomTypeIds) throws ParseException {
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO(context);
        networkController = new NetworkController();
        for (Integer roomTypeId : roomTypeIds) {
            if (roomTypeId != null) {
                RoomType newRoomType = networkController.getRoomTypeById(roomTypeId);
                roomTypeDAO.createOrUpdateIfExists(newRoomType);
            }
        }
    }

    private void addNewCoordinates(List<Integer> coordinateIds) throws ParseException {
        CoordinateDAO coordinateDAO = new CoordinateDAO(context);
        networkController = new NetworkController();
        for (Integer coordinateId : coordinateIds) {
            if (coordinateId != null) {
                Coordinate newCoordinate = networkController.getCoordinateById(coordinateId);
                coordinateDAO.createOrUpdateIfExists(newCoordinate);
            }
        }
    }

    private void addNewEdges(List<Integer> edgeIds) throws ParseException {
        EdgeDAO edgeDAO = new EdgeDAO(context);
        networkController = new NetworkController();
        for (Integer edgeId : edgeIds) {
            if (edgeId != null) {
                Edge newEdge = networkController.getEdgeById(edgeId);
                edgeDAO.createOrUpdateIfExists(newEdge);
            }
        }
    }

    private void addNewStreets(List<Integer> streetIds) throws ParseException {
        StreetDAO streetDAO = new StreetDAO(context);
        networkController = new NetworkController();
        for (Integer streetId : streetIds) {
            if (streetId != null) {
                Street newStreet = networkController.getStreetById(streetId);
                streetDAO.createOrUpdateIfExists(newStreet);
            }
        }
    }

    private void addNewBuildings(List<Integer> buildingIds) throws ParseException {
        BuildingDAO buildingDAO = new BuildingDAO(context);
        networkController = new NetworkController();
        for (Integer buildingId : buildingIds) {
            if (buildingId != null) {
                Building newBuilding = networkController.getBuildingById(buildingId);
                buildingDAO.createOrUpdateIfExists(newBuilding);
            }
        }
    }

    private void addNewRooms(List<Integer> roomIds) throws ParseException {
        RoomDAO roomDAO = new RoomDAO(context);
        networkController = new NetworkController();
        for (Integer roomId : roomIds) {
            if (roomId != null) {
                Room newRoom = networkController.getRoomById(roomId);
                roomDAO.createOrUpdateIfExists(newRoom);
            }
        }
    }

    private void addNewPhotos(List<Integer> photoIds) throws ParseException {
        PhotoDAO photoDAO = new PhotoDAO(context);
        networkController = new NetworkController();
        for (Integer photoId : photoIds) {
            if (photoId != null) {
                Photo newPhoto = networkController.getPhotoById(photoId);
                photoDAO.createOrUpdateIfExists(newPhoto);
            }
        }
    }

    private void addNewBuildingFloorOverlays(List<Integer> buildingFloorOverlayIds) throws ParseException {
        BuildingFloorOverlayDAO buildingFloorOverlayDAO = new BuildingFloorOverlayDAO(context);
        networkController = new NetworkController();
        for (Integer buildingFloorOverlayId : buildingFloorOverlayIds) {
            if (buildingFloorOverlayId != null) {
                BuildingFloorOverlay newBuildingFloorOverlay = networkController.getBuildingFloorOverlayById(buildingFloorOverlayId);
                buildingFloorOverlayDAO.createOrUpdateIfExists(newBuildingFloorOverlay);
            }
        }
    }

    private void addNewEventCreators(List<Integer> eventCreatorIds) throws ParseException {
        EventCreatorDAO eventCreatorDAO = new EventCreatorDAO(context);
        networkController = new NetworkController();
        for (Integer eventCreatorId : eventCreatorIds) {
            if (eventCreatorId != null) {
                EventCreator newEventCreator = networkController.getEventCreatorById(eventCreatorId);
                eventCreatorDAO.createOrUpdateIfExists(newEventCreator);
            }
        }
    }

    private void addNewEvents(List<Integer> eventIds) throws ParseException {
        EventDAO eventDAO = new EventDAO(context);
        networkController = new NetworkController();
        for (Integer eventId : eventIds) {
            if (eventId != null) {
                Event newEvent = networkController.getEventById(eventId);
                eventDAO.createOrUpdateIfExists(newEvent);
            }
        }
    }

    private void addNewEventSchedules(List<Integer> eventScheduleIds) throws ParseException {
        EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(context);
        networkController = new NetworkController();
        for (Integer eventScheduleId : eventScheduleIds) {
            if (eventScheduleId != null) {
                EventSchedule newEventSchedule = networkController.getEventScheduleById(eventScheduleId);
                eventScheduleDAO.createOrUpdateIfExists(newEventSchedule);
            }
        }
    }

    private void deleteCoordinateTypesDeletedFromTheServerDatabase(List<CoordinateType> coordinateTypesOnTheServer) {
        CoordinateTypeDAO coordinateTypeDAO = new CoordinateTypeDAO(context);
        List<CoordinateType> coordinateTypes = (List<CoordinateType>) coordinateTypeDAO.findAll();
        // Find CoordinateTypes that exists in the local db but not on the server
        for (CoordinateType coordinateType : coordinateTypesOnTheServer)
            coordinateTypes.remove((CoordinateType) coordinateTypeDAO.findById(coordinateType.getId()));

        // Delete CoordinateTypes that exists in the local db but not on the server
        for (CoordinateType coordinateType : coordinateTypes)
            coordinateTypeDAO.delete(coordinateType);
    }

    private void deleteEdgeTypesDeletedFromTheServerDatabase(List<EdgeType> edgeTypesOnTheServer) {
        EdgeTypeDAO edgeTypeDAO = new EdgeTypeDAO(context);
        List<EdgeType> edgeTypes = (List<EdgeType>) edgeTypeDAO.findAll();
        // Find EdgeTypes that exists in the local db but not on the server
        for (EdgeType edgeType : edgeTypesOnTheServer)
            edgeTypes.remove((EdgeType) edgeTypeDAO.findById(edgeType.getId()));

        // Delete EdgeTypes that exists in the local db but not on the server
        for (EdgeType edgeType : edgeTypes)
            edgeTypeDAO.delete(edgeType);
    }

    private void deleteRoomTypesDeletedFromTheServerDatabase(List<RoomType> roomTypesOnTheServer) {
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO(context);
        List<RoomType> roomTypes = (List<RoomType>) roomTypeDAO.findAll();
        // Find RoomTypes that exists in the local db but not on the server
        for (RoomType roomType : roomTypesOnTheServer)
            roomTypes.remove((RoomType) roomTypeDAO.findById(roomType.getId()));

        // Delete RoomTypes that exists in the local db but not on the server
        for (RoomType roomType : roomTypes)
            roomTypeDAO.delete(roomType);
    }

    private void deleteCoordinatesDeletedFromTheServerDatabase(List<Coordinate> coordinatesOnTheServer) {
        CoordinateDAO coordinateDAO = new CoordinateDAO(context);
        List<Coordinate> coordinates = (List<Coordinate>) coordinateDAO.findAll();
        // Find Coordinates that exists in the local db but not on the server
        for (Coordinate coordinate : coordinatesOnTheServer)
            coordinates.remove((Coordinate) coordinateDAO.findById(coordinate.getId()));

        // Delete Coordinates that exists in the local db but not on the server
        for (Coordinate coordinate : coordinates)
            coordinateDAO.delete(coordinate);
    }

    private void deleteEdgesDeletedFromTheServerDatabase(List<Edge> edgesOnTheServer) {
        EdgeDAO edgeDAO = new EdgeDAO(context);
        List<Edge> edges = (List<Edge>) edgeDAO.findAll();
        // Find Edges that exists in the local db but not on the server
        for (Edge edge : edgesOnTheServer)
            edges.remove((Edge) edgeDAO.findById(edge.getId()));

        // Delete Edges that exists in the local db but not on the server
        for (Edge edge : edges)
            edgeDAO.delete(edge);
    }

    private void deleteStreetsDeletedFromTheServerDatabase(List<Street> streetsOnTheServer) {
        StreetDAO streetDAO = new StreetDAO(context);
        List<Street> streets = (List<Street>) streetDAO.findAll();
        // Find Streets that exists in the local db but not on the server
        for (Street street : streetsOnTheServer)
            streets.remove((Street) streetDAO.findById(street.getId()));

        // Delete Streets that exists in the local db but not on the server
        for (Street street : streets)
            streetDAO.delete(street);
    }


    private void deleteBuildingsDeletedFromTheServerDatabase(List<Building> buildingsOnTheServer) {
        BuildingDAO buildingDAO = new BuildingDAO(context);
        List<Building> buildings = (List<Building>) buildingDAO.findAll();
        // Find Buildings that exists in the local db but not on the server
        for (Building building : buildingsOnTheServer)
            buildings.remove((Building) buildingDAO.findById(building.getId()));

        // Delete Buildings that exists in the local db but not on the server
        for (Building building : buildings)
            buildingDAO.delete(building);
    }

    private void deleteRoomsDeletedFromTheServerDatabase(List<Room> roomsOnTheServer) {
        RoomDAO roomDAO = new RoomDAO(context);
        List<Room> rooms = (List<Room>) roomDAO.findAll();
        // Find Rooms that exists in the local db but not on the server
        for (Room room : roomsOnTheServer)
            rooms.remove((Room) roomDAO.findById(room.getId()));

        // Delete Rooms that exists in the local db but not on the server
        for (Room room : rooms)
            roomDAO.delete(room);
    }

    private void deletePhotosDeletedFromTheServerDatabase(List<Photo> photosOnTheServer) {
        PhotoDAO photoDAO = new PhotoDAO(context);
        List<Photo> photos = (List<Photo>) photoDAO.findAll();
        // Find Photos that exists in the local db but not on the server
        for (Photo photo : photosOnTheServer)
            photos.remove((Photo) photoDAO.findById(photo.getId()));

        // Delete Photos that exists in the local db but not on the server
        for (Photo photo : photos)
            photoDAO.delete(photo);
    }

    private void deleteBuildingPhotosDeletedFromTheServerDatabase(List<BuildingPhoto> buildingPhotosOnTheServer) {
        BuildingPhotoDAO buildingPhotoDAO = new BuildingPhotoDAO(context);
        List<BuildingPhoto> buildingPhotos = (List<BuildingPhoto>) buildingPhotoDAO.findAll();
        // Find BuildingPhotos that exists in the local db but not on the server
        for (BuildingPhoto buildingPhoto : buildingPhotosOnTheServer)
            buildingPhotos.remove((BuildingPhoto) buildingPhotoDAO.findByIds(buildingPhoto.getBuilding_id(), buildingPhoto.getPhoto_id()));

        // Delete BuildingPhotos that exists in the local db but not on the server
        for (BuildingPhoto buildingPhoto : buildingPhotos)
            buildingPhotoDAO.delete(buildingPhoto);
    }

    private void deleteRoomPhotosDeletedFromTheServerDatabase(List<RoomPhoto> roomPhotosOnTheServer) {
        RoomPhotoDAO roomPhotoDAO = new RoomPhotoDAO(context);
        List<RoomPhoto> roomPhotos = (List<RoomPhoto>) roomPhotoDAO.findAll();
        // Find RoomPhotos that exists in the local db but not on the server
        for (RoomPhoto roomPhoto : roomPhotosOnTheServer)
            roomPhotos.remove((RoomPhoto) roomPhotoDAO.findByIds(roomPhoto.getRoom_id(), roomPhoto.getPhoto_id()));

        // Delete RoomPhotos that exists in the local db but not on the server
        for (RoomPhoto roomPhoto : roomPhotos)
            roomPhotoDAO.delete(roomPhoto);
    }

    private void deleteBuildingFloorOverlaysDeletedFromTheServerDatabase(List<BuildingFloorOverlay> buildingFloorOverlaysOnTheServer) {
        BuildingFloorOverlayDAO buildingFloorOverlayDAO = new BuildingFloorOverlayDAO(context);
        List<BuildingFloorOverlay> buildingFloorOverlays = (List<BuildingFloorOverlay>) buildingFloorOverlayDAO.findAll();
        // Find BuildingFloorOverlays that exists in the local db but not on the server
        for (BuildingFloorOverlay buildingFloorOverlay : buildingFloorOverlaysOnTheServer)
            buildingFloorOverlays.remove((BuildingFloorOverlay) buildingFloorOverlayDAO.findById(buildingFloorOverlay.getId()));

        // Delete BuildingFloorOverlays that exists in the local db but not on the server
        for (BuildingFloorOverlay buildingFloorOverlay : buildingFloorOverlays)
            buildingFloorOverlayDAO.delete(buildingFloorOverlay);
    }

    private void deleteEventCreatorsDeletedFromTheServerDatabase(List<EventCreator> eventCreatorsOnTheServer) {
        EventCreatorDAO eventCreatorDAO = new EventCreatorDAO(context);
        List<EventCreator> eventCreators = (List<EventCreator>) eventCreatorDAO.findAll();
        // Find EventCreators that exists in the local db but not on the server
        for (EventCreator eventCreator : eventCreatorsOnTheServer)
            eventCreators.remove((EventCreator) eventCreatorDAO.findById(eventCreator.getId()));

        // Delete EventCreators that exists in the local db but not on the server
        for (EventCreator eventCreator : eventCreators)
            eventCreatorDAO.delete(eventCreator);
    }

    private void deleteEventsDeletedFromTheServerDatabase(List<Event> eventsOnTheServer) {
        EventDAO eventDAO = new EventDAO(context);
        List<EventFavorable> events = (List<EventFavorable>) eventDAO.findAll();
        // Find Events that exists in the local db but not on the server
        for (Event event : eventsOnTheServer)
            events.remove((EventFavorable) eventDAO.findById(event.getId()));

        // Delete Events that exists in the local db but not on the server
        for (EventFavorable event : events)
            eventDAO.delete(event);
    }

    private void deleteEventSchedulesDeletedFromTheServerDatabase(List<EventSchedule> eventSchedulesOnTheServer) {
        EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(context);
        List<EventSchedule> eventSchedules = (List<EventSchedule>) eventScheduleDAO.findAll();
        // Find EventSchedules that exists in the local db but not on the server
        for (EventSchedule eventSchedule : eventSchedulesOnTheServer)
            eventSchedules.remove((EventSchedule) eventScheduleDAO.findById(eventSchedule.getId()));

        // Delete EventSchedules that exists in the local db but not on the server
        for (EventSchedule eventSchedule : eventSchedules)
            eventScheduleDAO.delete(eventSchedule);
    }

    private void deleteEventCreatorAppointmentsDeletedFromTheServerDatabase(List<EventCreatorAppointment> eventCreatorAppointmentsOnTheServer) {
        EventCreatorAppointmentDAO eventCreatorAppointmentDAO = new EventCreatorAppointmentDAO(context);
        List<EventCreatorAppointment> eventCreatorAppointments = (List<EventCreatorAppointment>) eventCreatorAppointmentDAO.findAll();
        // Find EventCreatorAppointments that exists in the local db but not on the server
        for (EventCreatorAppointment eventCreatorAppointment : eventCreatorAppointmentsOnTheServer)
            eventCreatorAppointments.remove((EventCreatorAppointment) eventCreatorAppointmentDAO.findByIds(eventCreatorAppointment.getEvent_id(),
                    eventCreatorAppointment.getEvent_creator_id()));

        // Delete EventCreatorAppointments that exists in the local db but not on the server
        for (EventCreatorAppointment eventCreatorAppointment : eventCreatorAppointments)
            eventCreatorAppointmentDAO.delete(eventCreatorAppointment);
    }

    private void deleteBuildingAuxiliaryCoordinatesDeletedFromTheServerDatabase(List<BuildingAuxiliaryCoordinate> buildingAuxiliaryCoordinatesOnTheServer) {
        BuildingAuxiliaryCoordinateDAO buildingAuxiliaryCoordinateDAO = new BuildingAuxiliaryCoordinateDAO(context);
        List<BuildingAuxiliaryCoordinate> buildingAuxiliaryCoordinates = (List<BuildingAuxiliaryCoordinate>) buildingAuxiliaryCoordinateDAO.findAll();
        // Find BuildingAuxiliaryCoordinates that exists in the local db but not on the server
        for (BuildingAuxiliaryCoordinate buildingAuxiliaryCoordinate : buildingAuxiliaryCoordinatesOnTheServer)
            buildingAuxiliaryCoordinates.remove((BuildingAuxiliaryCoordinate) buildingAuxiliaryCoordinateDAO.findByIds(buildingAuxiliaryCoordinate.getBuilding_id(),
                    buildingAuxiliaryCoordinate.getCoordinate_id()));

        // Delete BuildingAuxiliaryCoordinates that exists in the local db but not on the server
        for (BuildingAuxiliaryCoordinate buildingAuxiliaryCoordinate : buildingAuxiliaryCoordinates)
            buildingAuxiliaryCoordinateDAO.delete(buildingAuxiliaryCoordinate);
    }

    public void performGeneralSyncWithServer() {
        networkController = new NetworkController();
        GeneralSync generalData = networkController.getGeneralData();

        CoordinateTypeDAO coordinateTypeDAO = new CoordinateTypeDAO(context);
        EdgeTypeDAO edgeTypeDAO = new EdgeTypeDAO(context);
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO(context);
        CoordinateDAO coordinateDAO = new CoordinateDAO(context);
        EdgeDAO edgeDAO = new EdgeDAO(context);
        StreetDAO streetDAO = new StreetDAO(context);
        BuildingDAO buildingDAO = new BuildingDAO(context);
        RoomDAO roomDAO = new RoomDAO(context);
        PhotoDAO photoDAO = new PhotoDAO(context);
        BuildingPhotoDAO buildingPhotoDAO = new BuildingPhotoDAO(context);
        RoomPhotoDAO roomPhotoDAO = new RoomPhotoDAO(context);
        BuildingFloorOverlayDAO buildingFloorOverlayDAO = new BuildingFloorOverlayDAO(context);
        EventCreatorDAO eventCreatorDAO = new EventCreatorDAO(context);
        EventDAO eventDAO = new EventDAO(context);
        EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(context);
        EventCreatorAppointmentDAO eventCreatorAppointmentDAO = new EventCreatorAppointmentDAO(context);
        BuildingAuxiliaryCoordinateDAO buildingAuxiliaryCoordinateDAO = new BuildingAuxiliaryCoordinateDAO(context);

        for (int i = 0; i < generalData.getCoordinateTypes().size(); i++) {
            coordinateTypeDAO.createOrUpdateIfExists(generalData.getCoordinateType(i));
        }
        for (int i = 0; i < generalData.getEdgeTypes().size(); i++) {
            edgeTypeDAO.createOrUpdateIfExists(generalData.getEdgeType(i));
        }
        for (int i = 0; i < generalData.getRoomTypes().size(); i++) {
            roomTypeDAO.createOrUpdateIfExists(generalData.getRoomType(i));
        }
        for (int i = 0; i < generalData.getCoordinates().size(); i++) {
            coordinateDAO.createOrUpdateIfExists(generalData.getCoordinate(i));
        }
        for (int i = 0; i < generalData.getEdges().size(); i++) {
            edgeDAO.createOrUpdateIfExists(generalData.getEdge(i));
        }
        for (int i = 0; i < generalData.getStreets().size(); i++) {
            streetDAO.createOrUpdateIfExists(generalData.getStreet(i));
        }
        for (int i = 0; i < generalData.getBuildings().size(); i++) {
            buildingDAO.createOrUpdateIfExists(generalData.getBuilding(i));
        }
        for (int i = 0; i < generalData.getRooms().size(); i++) {
            roomDAO.createOrUpdateIfExists(generalData.getRoom(i));
        }
        for (int i = 0; i < generalData.getPhotos().size(); i++) {
            photoDAO.createOrUpdateIfExists(generalData.getPhoto(i));
        }
        for (int i = 0; i < generalData.getBuildingPhotos().size(); i++) {
            buildingPhotoDAO.createOrUpdateIfExists(generalData.getBuildingPhoto(i));
        }
        for (int i = 0; i < generalData.getRoomPhotos().size(); i++) {
            roomPhotoDAO.createOrUpdateIfExists(generalData.getRoomPhoto(i));
        }
        for (int i = 0; i < generalData.getBuildingFloorOverlays().size(); i++) {
            buildingFloorOverlayDAO.createOrUpdateIfExists(generalData.getBuildingFloorOverlay(i));
        }
        for (int i = 0; i < generalData.getEventCreators().size(); i++) {
            eventCreatorDAO.createOrUpdateIfExists(generalData.getEventCreator(i));
        }
        for (int i = 0; i < generalData.getEvents().size(); i++) {
            eventDAO.createOrUpdateIfExists(generalData.getEvent(i));
        }
        for (int i = 0; i < generalData.getEventSchedules().size(); i++) {
            eventScheduleDAO.createOrUpdateIfExists(generalData.getEventSchedule(i));
        }
        for (int i = 0; i < generalData.getEventCreatorAppointments().size(); i++) {
            eventCreatorAppointmentDAO.createOrUpdateIfExists(generalData.getEventCreatorAppointment(i));
        }
        for (int i = 0; i < generalData.getBuildingAuxiliaryCoordinates().size(); i++) {
            buildingAuxiliaryCoordinateDAO.createOrUpdateIfExists(generalData.getBuildingAuxiliaryCoordinate(i));
        }

        saveLastSyncDate(new Date(), syncTypes.GENERAL);
    }

    public void performGeneralSyncWithServerAndDeleteRecordsDeletedFromServerDatabase() {
        performGeneralSyncWithServer();
        deleteRecordsDeletedFromServerDatabase();
    }

    public void saveLastSyncDate(Date lastSyncDate, syncTypes type) {
        sPref = context.getSharedPreferences(Constants.SYNC, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();

        switch (type) {
            case TYPES:
                ed.putString(Constants.LAST + Constants.TYPES + Constants.SYNC_DATE, com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(lastSyncDate));
                break;
            case MAP_UNITS:
                ed.putString(Constants.LAST + Constants.MAP_UNITS + Constants.SYNC_DATE, com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(lastSyncDate));
                break;
            case EVENTS:
                ed.putString(Constants.LAST + Constants.EVENTS + Constants.SYNC_DATE, com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(lastSyncDate));
                break;
            case ASSIGNMENTS:
                ed.putString(Constants.LAST + Constants.ASSIGNMENTS + Constants.SYNC_DATE, com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(lastSyncDate));
                break;
            case GENERAL:
                ed.putString(Constants.LAST + Constants.TYPES + Constants.SYNC_DATE, com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(lastSyncDate));
                ed.putString(Constants.LAST + Constants.MAP_UNITS + Constants.SYNC_DATE, com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(lastSyncDate));
                ed.putString(Constants.LAST + Constants.EVENTS + Constants.SYNC_DATE, com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(lastSyncDate));
                ed.putString(Constants.LAST + Constants.ASSIGNMENTS + Constants.SYNC_DATE, com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(lastSyncDate));
                ed.putString(Constants.LAST + Constants.GENERAL + Constants.SYNC_DATE, com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(lastSyncDate));
                break;
            default:
                ed.putString(Constants.LAST + Constants.GENERAL + Constants.SYNC_DATE, com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(lastSyncDate));
                break;
        }

        ed.apply();
    }

    private Date loadLastSyncDate(syncTypes type) throws ParseException {
        sPref = context.getSharedPreferences(Constants.SYNC, MODE_PRIVATE);

        String lastSyncDate;

        switch (type) {
            case TYPES:
                lastSyncDate = sPref.getString(Constants.LAST + Constants.TYPES + Constants.SYNC_DATE, "");
                break;
            case MAP_UNITS:
                lastSyncDate = sPref.getString(Constants.LAST + Constants.MAP_UNITS + Constants.SYNC_DATE, "");
                break;
            case EVENTS:
                lastSyncDate = sPref.getString(Constants.LAST + Constants.EVENTS + Constants.SYNC_DATE, "");
                break;
            case ASSIGNMENTS:
                lastSyncDate = sPref.getString(Constants.LAST + Constants.ASSIGNMENTS + Constants.SYNC_DATE, "");
                break;
            default:
                lastSyncDate = Constants.DEFAULT_SYNC_DATE;
                break;
        }

        if (Constants.EMPTY_STRING.equals(lastSyncDate))
            lastSyncDate = Constants.DEFAULT_SYNC_DATE;

        return com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(lastSyncDate);
    }

}