package com.innopolis.maps.innomaps.app;

import android.content.Context;

import com.google.common.base.Predicate;
import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.dataaccessobjects.BuildingAuxiliaryCoordinateDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.BuildingDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateTypeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventScheduleDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomTypeDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Building;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingAuxiliaryCoordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.CoordinateType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventFavorable;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventSchedule;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomType;
import com.innopolis.maps.innomaps.maps.LatLngFlr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchableItem implements Comparable<SearchableItem> {
    public enum SearchableItemType {
        DOOR, ELEVATOR, STAIRS, ROOM, FOOD, WC, CLINIC, READING, LIBRARY, EASTER_EGG, EVENT, DEFAULT
    }

    public String name;
    public SearchableItemType type;
    public int id;
    public String building;
    public String floor;
    public String room;
    public LatLngFlr coordinate;

    private static CoordinateTypeDAO coordinateTypeDAO;
    private static RoomTypeDAO roomTypeDAO;
    private static CoordinateDAO coordinateDAO;
    private static RoomDAO roomDAO;
    private static BuildingDAO buildingDAO;
    private static BuildingAuxiliaryCoordinateDAO buildingAuxiliaryCoordinateDAO;
    private static EventDAO eventDAO;
    private static EventScheduleDAO eventScheduleDAO;

    public SearchableItem(Context context) {
        initializeDAOs(context);
    }

    private static void initializeDAOs(Context context) {
        coordinateTypeDAO = new CoordinateTypeDAO(context);
        roomTypeDAO = new RoomTypeDAO(context);
        coordinateDAO = new CoordinateDAO(context);
        roomDAO = new RoomDAO(context);
        buildingDAO = new BuildingDAO(context);
        buildingAuxiliaryCoordinateDAO = new BuildingAuxiliaryCoordinateDAO(context);
        eventDAO = new EventDAO(context);
        eventScheduleDAO = new EventScheduleDAO(context);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SearchableItemType getType() {
        return type;
    }

    public void setType(SearchableItemType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public LatLngFlr getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(LatLngFlr coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public int compareTo(SearchableItem another) {
        return this.getName().compareTo(another.getName());
    }

    public static void addEvents(List<SearchableItem> items, Context context) {
        initializeDAOs(context);
        List<EventSchedule> eventSchedules = eventScheduleDAO.findUpcomingAndOngoingScheduledEvents();
        for (EventSchedule eventSchedule : eventSchedules) {
            EventFavorable event = (EventFavorable) eventDAO.findById(eventSchedule.getEvent_id());
            SearchableItem searchableItem = new SearchableItem(context);
            searchableItem.setName(event.getName());
            searchableItem.setType(SearchableItemType.EVENT);
            Coordinate eventsCoordinate;
            if (eventSchedule.getLocation_id() != null) {
                searchableItem.setId(eventSchedule.getId());
                eventsCoordinate = (Coordinate) coordinateDAO.findById(eventSchedule.getLocation_id());
                searchableItem.setBuilding(getBuildingNameForEvent(eventsCoordinate.getId()));
                searchableItem.setFloor(Integer.toString(eventsCoordinate.getFloor()) + Constants.SPACE + Constants.FLOOR_LOWERCASE);
                if (eventsCoordinate.getType_id() == 3 /*if type is ROOM*/ && null != eventsCoordinate.getName() && !Constants.EMPTY_STRING.equals(eventsCoordinate.getName()))
                    searchableItem.setRoom(eventsCoordinate.getName());
                else
                    searchableItem.setRoom(null);
                searchableItem.setCoordinate(new LatLngFlr(eventsCoordinate.getLatitude(), eventsCoordinate.getLongitude(), eventsCoordinate.getFloor()));


                items.add(searchableItem);
            }
        }
    }

    // Add Rooms
    public static void addPois(List<SearchableItem> items, Context context) {
        initializeDAOs(context);
        HashMap<Integer, SearchableItemType> coordinateTypesMap = new HashMap<>();
        HashMap<Integer, SearchableItemType> roomTypesMap = new HashMap<>();
        HashMap<SearchableItemType, Integer> inverseRoomTypesMap = new HashMap<>();
        List<CoordinateType> coordinateTypes = (List<CoordinateType>) coordinateTypeDAO.findAll();
        List<RoomType> roomTypes = (List<RoomType>) roomTypeDAO.findAll();
        for (CoordinateType coordinateType : coordinateTypes) {
            coordinateTypesMap.put(coordinateType.getId(), determineSearchableItemType(coordinateType.getName()));
        }
        for (RoomType roomType : roomTypes) {
            roomTypesMap.put(roomType.getId(), determineSearchableItemType(roomType.getName()));
            inverseRoomTypesMap.put(roomTypesMap.get(roomType.getId()), roomType.getId());
        }

        List<Integer> ignoredRoomTypes = new ArrayList<>();
        ignoredRoomTypes.add(inverseRoomTypesMap.get(SearchableItemType.DOOR));
        List<Room> rooms = roomDAO.findRoomsExceptWithFollowingTypes(ignoredRoomTypes);
        List<Coordinate> coordinates = getCoordinatesOfStairsAndElevators(context);

        for (Room room : rooms) {
            SearchableItem searchableItem = new SearchableItem(context);
            Coordinate roomsCoordinate = (Coordinate) coordinateDAO.findById(room.getCoordinate_id());
            searchableItem.setName(getRoomsName(room, roomsCoordinate));

            searchableItem.setType(roomTypesMap.get(room.getType_id()));
            searchableItem.setId(room.getId());
            searchableItem.setBuilding(getBuildingNameForRoom(room.getBuilding_id()));
            searchableItem.setFloor(Integer.toString(roomsCoordinate.getFloor()) + Constants.SPACE + Constants.FLOOR_LOWERCASE);
            searchableItem.setRoom(searchableItem.getName());
            searchableItem.setCoordinate(new LatLngFlr(roomsCoordinate.getLatitude(), roomsCoordinate.getLongitude(), roomsCoordinate.getFloor()));

            if (searchableItem.getName() != null)
                items.add(searchableItem);
        }

        for (Coordinate coordinate : coordinates) {
            SearchableItem searchableItem = new SearchableItem(context);
            if (null != coordinate.getName() && !Constants.EMPTY_STRING.equals(coordinate.getName()))
                searchableItem.setName(coordinate.getName());
            else {
                searchableItem.setName(null);
            }

            searchableItem.setType(coordinateTypesMap.get(coordinate.getType_id()));
            searchableItem.setId(coordinate.getId());
            searchableItem.setBuilding(getBuildingNameForCoordinate(coordinate.getId()));
            searchableItem.setFloor(Integer.toString(coordinate.getFloor()) + Constants.SPACE + Constants.FLOOR_LOWERCASE);
            searchableItem.setRoom(searchableItem.getName());
            searchableItem.setCoordinate(new LatLngFlr(coordinate.getLatitude(), coordinate.getLongitude(), coordinate.getFloor()));

            if (searchableItem.getName() != null)
                items.add(searchableItem);
        }
    }

    public static SearchableItemType determineSearchableItemType(String typeStr) {
        switch (typeStr) {
            case Constants.DOOR:
                return SearchableItemType.DOOR;
            case Constants.STAIRS:
                return SearchableItemType.STAIRS;
            case Constants.ELEVATOR:
                return SearchableItemType.ELEVATOR;
            case Constants.ROOM_CAPITAL_CASE:
                return SearchableItemType.ROOM;
            case Constants.FOOD:
                return SearchableItemType.FOOD;
            case Constants.WC:
                return SearchableItemType.WC;
            case Constants.CLINIC:
                return SearchableItemType.CLINIC;
            case Constants.READING:
                return SearchableItemType.READING;
            case Constants.LIBRARY:
                return SearchableItemType.LIBRARY;
            case Constants.EASTER_EGG:
                return SearchableItemType.EASTER_EGG;
            case Constants.EVENT_CAPITAL_CASE:
                return SearchableItemType.EVENT;
            default:
                return SearchableItemType.DEFAULT;
        }
    }

    private static String getBuildingNameForRoom(int buildingId) {
        Building roomsBuilding = (Building) buildingDAO.findById(buildingId);
        Coordinate buildingsCoordinate = (Coordinate) coordinateDAO.findById(roomsBuilding.getCoordinate_id());
        if (Constants.EMPTY_STRING.equals(buildingsCoordinate.getName()))
            return null;
        else
            return buildingsCoordinate.getName();
    }

    private static String getBuildingNameForCoordinate(int coordinateId) {
        BuildingAuxiliaryCoordinate buildingAuxiliaryCoordinateWithSpecifiedCoordinateId = buildingAuxiliaryCoordinateDAO.getFirstRecordByCoordinateId(coordinateId);
        if (buildingAuxiliaryCoordinateWithSpecifiedCoordinateId == null)
            return null;
        else {
            Building coordinatesBuilding = (Building) buildingDAO.findById(buildingAuxiliaryCoordinateWithSpecifiedCoordinateId.getBuilding_id());
            Coordinate buildingsCoordinate = (Coordinate) coordinateDAO.findById(coordinatesBuilding.getCoordinate_id());
            return buildingsCoordinate.getName();
        }
    }

    private static String getBuildingNameForEvent(int coordinateId) {
        Room room = roomDAO.getFirstRecordByCoordinateId(coordinateId);
        if (room == null)
            return getBuildingNameForCoordinate(coordinateId);
        else {
            return getBuildingNameForRoom(room.getBuilding_id());
        }
    }

    public static String getRoomsName(Room room, Coordinate roomsCoordinate) {
        String roomsName;
        if (null != roomsCoordinate.getName() && !Constants.EMPTY_STRING.equals(roomsCoordinate.getName()))
            roomsName = roomsCoordinate.getName();
        else if (null != room.getNumber())
            roomsName = Constants.ROOM_STARTING_FROM_CAPITAL_LETTER + Constants.SPACE + Integer.toString(room.getNumber());
        else
            roomsName = null;

        return roomsName;
    }

    public static Predicate<SearchableItem> isWc = new Predicate<SearchableItem>() {
        @Override
        public boolean apply(SearchableItem input) {
            return input.getType() == SearchableItemType.WC;
        }
    };

    public static List<Coordinate> getCoordinatesOfStairsAndElevators(Context context) {
        initializeDAOs(context);
        List<Coordinate> stairsAndElevatorsCoordinates = new ArrayList<>();
        List<CoordinateType> coordinateTypes = (List<CoordinateType>) coordinateTypeDAO.findAll();
        HashMap<SearchableItemType, Integer> coordinateTypesMap = new HashMap<>();
        for (CoordinateType coordinateType : coordinateTypes) {
            coordinateTypesMap.put(determineSearchableItemType(coordinateType.getName()), coordinateType.getId());
        }
        if (coordinateTypesMap.get(SearchableItemType.STAIRS) != null)
            stairsAndElevatorsCoordinates.addAll(coordinateDAO.getCoordinatesByTypeId(coordinateTypesMap.get(SearchableItemType.STAIRS)));
        if (coordinateTypesMap.get(SearchableItemType.ELEVATOR) != null)
            stairsAndElevatorsCoordinates.addAll(coordinateDAO.getCoordinatesByTypeId(coordinateTypesMap.get(SearchableItemType.ELEVATOR)));

        return stairsAndElevatorsCoordinates;
    }

    public static Predicate<SearchableItem> isFood = new Predicate<SearchableItem>() {
        @Override
        public boolean apply(SearchableItem input) {
            return input.getType() == SearchableItemType.FOOD;
        }
    };

    public static Predicate<SearchableItem> isEvent = new Predicate<SearchableItem>() {
        @Override
        public boolean apply(SearchableItem input) {
            return input.getType() == SearchableItemType.EVENT;
        }
    };

    public static Predicate<SearchableItem> isOther = new Predicate<SearchableItem>() {
        @Override
        public boolean apply(SearchableItem input) {
            Boolean res;
            res = !(input.getType() == SearchableItemType.WC || input.getType() == SearchableItemType.FOOD || input.getType() == SearchableItemType.EVENT);
            return res;
        }
    };

}
