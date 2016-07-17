package com.innopolis.maps.innomaps.db;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.innopolis.maps.innomaps.app.MainActivity;
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
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventSchedule;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Photo;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomPhoto;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Street;
import com.innopolis.maps.innomaps.network.InternetAccessChecker;
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.EventsSync;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.GeneralSync;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.MapUnitsSync;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.TypesSync;

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
        DatabaseSync.context = MainActivity.getInstance().getApplicationContext();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Handler h = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == 1) { // code if connected
                    try {
                        performSyncWithServer();
                    } catch (ParseException e) {
                        Log.e(Constants.LOG, e.getMessage(), e.fillInStackTrace());
                    }
                    saveLastSyncDate(new Date(), syncTypes.GENERAL);
                    Log.d(Constants.SYNC, Constants.SYNC_FINISHED_ON + com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(new Date()));
                }
            }
        };

        InternetAccessChecker.isNetworkAvailable(h, Constants.INTERNET_CHECK_TIMEOUT, getApplicationContext());

        while (true) {
            final Handler finalHandler = h;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Actions to do after SYNCHRONIZATION_INTERVAL
                    InternetAccessChecker.isNetworkAvailable(finalHandler, Constants.INTERNET_CHECK_TIMEOUT, getApplicationContext());
                }
            }, Constants.SYNCHRONIZATION_INTERVAL);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void performSyncWithServer() throws ParseException {
        synchronizeTypes();
        synchronizeMapUnits();
        synchronizeEvents();
        synchronizeAssignments();
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
        Date syncDate = loadLastSyncDate(syncTypes.ASSIGNMENTS);

        List<BuildingPhoto> buildingPhotos = (List<BuildingPhoto>) networkController.getBuildingPhotosCreatedOnOrAfterDate(syncDate);
        List<RoomPhoto> roomPhotos = (List<RoomPhoto>) networkController.getRoomPhotosCreatedOnOrAfterDate(syncDate);
        List<EventCreatorAppointment> eventCreatorAppointments = (List<EventCreatorAppointment>) networkController.getEventCreatorAppointmentsCreatedOnOrAfterDate(syncDate);

        for (int i = 0; i < buildingPhotos.size(); i++) {
            buildingPhotoDAO.create(buildingPhotos.get(i));
        }
        for (int i = 0; i < roomPhotos.size(); i++) {
            roomPhotoDAO.create(roomPhotos.get(i));
        }
        for (int i = 0; i < eventCreatorAppointments.size(); i++) {
            eventCreatorAppointmentDAO.create(eventCreatorAppointments.get(i));
        }

        saveLastSyncDate(new Date(), syncTypes.ASSIGNMENTS);
    }

    private void addNewCoordinateTypes(List<Integer> coordinateTypeIds) throws ParseException {
        CoordinateTypeDAO coordinateTypeDAO = new CoordinateTypeDAO(context);
        networkController = new NetworkController();
        for (Integer coordinateTypeId : coordinateTypeIds) {
            if (coordinateTypeId != null) {
                CoordinateType newCoordinateType = networkController.getCoordinateTypeById(coordinateTypeId);
                coordinateTypeDAO.create(newCoordinateType);
            }
        }
    }

    private void addNewEdgeTypes(List<Integer> edgeTypeIds) throws ParseException {
        EdgeTypeDAO edgeTypeDAO = new EdgeTypeDAO(context);
        networkController = new NetworkController();
        for (Integer edgeTypeId : edgeTypeIds) {
            if (edgeTypeId != null) {
                EdgeType newEdgeType = networkController.getEdgeTypeById(edgeTypeId);
                edgeTypeDAO.create(newEdgeType);
            }
        }
    }

    private void addNewRoomTypes(List<Integer> roomTypeIds) throws ParseException {
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO(context);
        networkController = new NetworkController();
        for (Integer roomTypeId : roomTypeIds) {
            if (roomTypeId != null) {
                RoomType newRoomType = networkController.getRoomTypeById(roomTypeId);
                roomTypeDAO.create(newRoomType);
            }
        }
    }

    private void addNewCoordinates(List<Integer> coordinateIds) throws ParseException {
        CoordinateDAO coordinateDAO = new CoordinateDAO(context);
        networkController = new NetworkController();
        for (Integer coordinateId : coordinateIds) {
            if (coordinateId != null) {
                Coordinate newCoordinate = networkController.getCoordinateById(coordinateId);
                coordinateDAO.create(newCoordinate);
            }
        }
    }

    private void addNewEdges(List<Integer> edgeIds) throws ParseException {
        EdgeDAO edgeDAO = new EdgeDAO(context);
        networkController = new NetworkController();
        for (Integer edgeId : edgeIds) {
            if (edgeId != null) {
                Edge newEdge = networkController.getEdgeById(edgeId);
                edgeDAO.create(newEdge);
            }
        }
    }

    private void addNewStreets(List<Integer> streetIds) throws ParseException {
        StreetDAO streetDAO = new StreetDAO(context);
        networkController = new NetworkController();
        for (Integer streetId : streetIds) {
            if (streetId != null) {
                Street newStreet = networkController.getStreetById(streetId);
                streetDAO.create(newStreet);
            }
        }
    }

    private void addNewBuildings(List<Integer> buildingIds) throws ParseException {
        BuildingDAO buildingDAO = new BuildingDAO(context);
        networkController = new NetworkController();
        for (Integer buildingId : buildingIds) {
            if (buildingId != null) {
                Building newBuilding = networkController.getBuildingById(buildingId);
                buildingDAO.create(newBuilding);
            }
        }
    }

    private void addNewRooms(List<Integer> roomIds) throws ParseException {
        RoomDAO roomDAO = new RoomDAO(context);
        networkController = new NetworkController();
        for (Integer roomId : roomIds) {
            if (roomId != null) {
                Room newRoom = networkController.getRoomById(roomId);
                roomDAO.create(newRoom);
            }
        }
    }

    private void addNewPhotos(List<Integer> photoIds) throws ParseException {
        PhotoDAO photoDAO = new PhotoDAO(context);
        networkController = new NetworkController();
        for (Integer photoId : photoIds) {
            if (photoId != null) {
                Photo newPhoto = networkController.getPhotoById(photoId);
                photoDAO.create(newPhoto);
            }
        }
    }

    private void addNewBuildingFloorOverlays(List<Integer> buildingFloorOverlayIds) throws ParseException {
        BuildingFloorOverlayDAO buildingFloorOverlayDAO = new BuildingFloorOverlayDAO(context);
        networkController = new NetworkController();
        for (Integer buildingFloorOverlayId : buildingFloorOverlayIds) {
            if (buildingFloorOverlayId != null) {
                BuildingFloorOverlay newBuildingFloorOverlay = networkController.getBuildingFloorOverlayById(buildingFloorOverlayId);
                buildingFloorOverlayDAO.create(newBuildingFloorOverlay);
            }
        }
    }

    private void addNewEventCreators(List<Integer> eventCreatorIds) throws ParseException {
        EventCreatorDAO eventCreatorDAO = new EventCreatorDAO(context);
        networkController = new NetworkController();
        for (Integer eventCreatorId : eventCreatorIds) {
            if (eventCreatorId != null) {
                EventCreator newEventCreator = networkController.getEventCreatorById(eventCreatorId);
                eventCreatorDAO.create(newEventCreator);
            }
        }
    }

    private void addNewEvents(List<Integer> eventIds) throws ParseException {
        EventDAO eventDAO = new EventDAO(context);
        networkController = new NetworkController();
        for (Integer eventId : eventIds) {
            if (eventId != null) {
                Event newEvent = networkController.getEventById(eventId);
                eventDAO.create(newEvent);
            }
        }
    }

    private void addNewEventSchedules(List<Integer> eventScheduleIds) throws ParseException {
        EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(context);
        networkController = new NetworkController();
        for (Integer eventScheduleId : eventScheduleIds) {
            if (eventScheduleId != null) {
                EventSchedule newEventSchedule = networkController.getEventScheduleById(eventScheduleId);
                eventScheduleDAO.create(newEventSchedule);
            }
        }
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

        for (int i = 0; i < generalData.getCoordinateTypes().size(); i++) {
            coordinateTypeDAO.create(generalData.getCoordinateType(i));
        }
        for (int i = 0; i < generalData.getEdgeTypes().size(); i++) {
            edgeTypeDAO.create(generalData.getEdgeType(i));
        }
        for (int i = 0; i < generalData.getRoomTypes().size(); i++) {
            roomTypeDAO.create(generalData.getRoomType(i));
        }
        for (int i = 0; i < generalData.getCoordinates().size(); i++) {
            coordinateDAO.create(generalData.getCoordinate(i));
        }
        for (int i = 0; i < generalData.getEdges().size(); i++) {
            edgeDAO.create(generalData.getEdge(i));
        }
        for (int i = 0; i < generalData.getStreets().size(); i++) {
            streetDAO.create(generalData.getStreet(i));
        }
        for (int i = 0; i < generalData.getBuildings().size(); i++) {
            buildingDAO.create(generalData.getBuilding(i));
        }
        for (int i = 0; i < generalData.getRooms().size(); i++) {
            roomDAO.create(generalData.getRoom(i));
        }
        for (int i = 0; i < generalData.getPhotos().size(); i++) {
            photoDAO.create(generalData.getPhoto(i));
        }
        for (int i = 0; i < generalData.getBuildingPhotos().size(); i++) {
            buildingPhotoDAO.create(generalData.getBuildingPhoto(i));
        }
        for (int i = 0; i < generalData.getRoomPhotos().size(); i++) {
            roomPhotoDAO.create(generalData.getRoomPhoto(i));
        }
        for (int i = 0; i < generalData.getBuildingFloorOverlays().size(); i++) {
            buildingFloorOverlayDAO.create(generalData.getBuildingFloorOverlay(i));
        }
        for (int i = 0; i < generalData.getEventCreators().size(); i++) {
            eventCreatorDAO.create(generalData.getEventCreator(i));
        }
        for (int i = 0; i < generalData.getEvents().size(); i++) {
            eventDAO.create(generalData.getEvent(i));
        }
        for (int i = 0; i < generalData.getEventSchedules().size(); i++) {
            eventScheduleDAO.create(generalData.getEventSchedule(i));
        }
        for (int i = 0; i < generalData.getEventCreatorAppointments().size(); i++) {
            eventCreatorAppointmentDAO.create(generalData.getEventCreatorAppointment(i));
        }

        saveLastSyncDate(new Date(), syncTypes.GENERAL);
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

        if ("".equals(lastSyncDate))
            lastSyncDate = Constants.DEFAULT_SYNC_DATE;

        return com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(lastSyncDate);
    }

}