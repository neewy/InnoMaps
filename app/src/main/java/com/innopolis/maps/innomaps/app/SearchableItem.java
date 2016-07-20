package com.innopolis.maps.innomaps.app;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Predicate;
import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.dataaccessobjects.BuildingDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateTypeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomTypeDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Building;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.CoordinateType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomType;
import com.innopolis.maps.innomaps.events.Event;
import com.innopolis.maps.innomaps.maps.LatLngFlr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.innopolis.maps.innomaps.database.TableFields.ATTR;
import static com.innopolis.maps.innomaps.database.TableFields.BUILDING;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT;
import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.FOOD;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.NUMBER;
import static com.innopolis.maps.innomaps.database.TableFields.POI_NAME;
import static com.innopolis.maps.innomaps.database.TableFields.ROOM;
import static com.innopolis.maps.innomaps.database.TableFields.TYPE;
import static com.innopolis.maps.innomaps.database.TableFields.WC;
import static com.innopolis.maps.innomaps.database.TableFields._ID;

public class SearchableItem implements Comparable<SearchableItem> {
    public enum SearchableItemType {
        ELEVATOR, STAIRS, ROOM, FOOD, WC, CLINIC, READING, LIBRARY, EASTER_EGG, EVENT, DEFAULT
    }

    public String name;
    public SearchableItemType type;
    public String id;
    public String building;
    public String floor;
    public String room;
    public LatLngFlr coordinate;

    private static Context context;
    private static CoordinateTypeDAO coordinateTypeDAO;
    private static RoomTypeDAO roomTypeDAO;
    private static CoordinateDAO coordinateDAO;
    private static RoomDAO roomDAO;
    private static BuildingDAO buildingDAO;

    public SearchableItem() {
        SearchableItem.context = MainActivity.getInstance().getApplicationContext();
        coordinateTypeDAO = new CoordinateTypeDAO(context);
        roomTypeDAO = new RoomTypeDAO(context);
        coordinateDAO = new CoordinateDAO(context);
        roomDAO = new RoomDAO(context);
        buildingDAO = new BuildingDAO(context);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public static void addEvents(List<SearchableItem> items, List<Event> events) {
        for (Event event : events) {
            SearchableItem searchableItem = new SearchableItem();
            searchableItem.setName(event.getSummary());
            searchableItem.setType(SearchableItemType.EVENT);
            searchableItem.setId(event.getEventID());
            searchableItem.setBuilding(event.getBuilding());
            searchableItem.setFloor(event.getFloor());
            searchableItem.setRoom(event.getRoom());
            searchableItem.setCoordinate(new LatLngFlr(Double.parseDouble(event.getLatitude()), Double.parseDouble(event.getLongitude()), Integer.parseInt(event.getFloor().substring(0, 1))));
            items.add(searchableItem);
        }
    }

    // Add Rooms
    public static void addPois(List<SearchableItem> items, List<HashMap<String, String>> pois) {
        /*
        HashMap<Integer, SearchableItemType> coordinateTypesMap = new HashMap<>();
        HashMap<Integer, SearchableItemType> roomTypesMap = new HashMap<>();
        List<CoordinateType> coordinateTypes = (List<CoordinateType>) coordinateTypeDAO.findAll();
        List<RoomType> roomTypes = (List<RoomType>) roomTypeDAO.findAll();
        for (CoordinateType coordinateType : coordinateTypes) {
            coordinateTypesMap.put(coordinateType.getId(), determineSearchableItemType(coordinateType.getName()));
        }
        for (RoomType roomType : roomTypes) {
            roomTypesMap.put(roomType.getId(), determineSearchableItemType(roomType.getName()));
        }

        List<Room> rooms = (List<Room>) roomDAO.findAll();
        List<Coordinate> coordinates = getCoordinatesOfStairsAndElevators();

        for(Room room: rooms) {
            SearchableItem searchableItem = new SearchableItem();
            boolean toAdd = true;
            Coordinate roomsCoordinate = (Coordinate) coordinateDAO.findById(room.getCoordinate_id());
            if(null != roomsCoordinate.getName() && !Constants.EMPTY_STRING.equals(roomsCoordinate.getName()))
                searchableItem.setName(roomsCoordinate.getName());
            else if(null != room.getNumber())
                searchableItem.setName(Constants.ROOM_STARTING_FROM_CAPITAL_LETTER + Constants.SPACE + Integer.toString(room.getNumber()));
            else {
                toAdd = false;
                searchableItem.setName(Constants.EMPTY_STRING);
            }

            searchableItem.setType(roomTypesMap.get(room.getType_id()));
            // TODO: Find out what really should be set as id and WHY
            searchableItem.setId(Integer.toString(room.getId()));
            searchableItem.setBuilding(getBuildingNameForRoom(room.getBuilding_id()));
            searchableItem.setFloor(Integer.toString(roomsCoordinate.getFloor()) + Constants.SPACE + Constants.FLOOR_LOWERCASE);
            searchableItem.setRoom(searchableItem.getName());
            searchableItem.setCoordinate(new LatLngFlr(roomsCoordinate.getLatitude(), roomsCoordinate.getLongitude(), roomsCoordinate.getFloor()));

            if(toAdd)
                items.add(searchableItem);
        }

        for(Coordinate coordinate: coordinates) {
            SearchableItem searchableItem = new SearchableItem();
            boolean toAdd = true;
            if(null != coordinate.getName() && !Constants.EMPTY_STRING.equals(coordinate.getName()))
                searchableItem.setName(coordinate.getName());
            else {
                toAdd = false;
                searchableItem.setName(Constants.EMPTY_STRING);
            }

            searchableItem.setType(coordinateTypesMap.get(coordinate.getType_id()));
            // TODO: Find out what really should be set as id and WHY
            searchableItem.setId(Integer.toString(coordinate.getId()));
            searchableItem.setBuilding(getBuildingNameForRoom(room.getBuilding_id()));
            searchableItem.setFloor(Integer.toString(roomsCoordinate.getFloor()) + Constants.SPACE + Constants.FLOOR_LOWERCASE);
            searchableItem.setRoom(searchableItem.getName());
            searchableItem.setCoordinate(new LatLngFlr(roomsCoordinate.getLatitude(), roomsCoordinate.getLongitude(), roomsCoordinate.getFloor()));

            if(toAdd)
                items.add(searchableItem);
        }
        */

        for (HashMap<String, String> poi : pois) {
            SearchableItem searchableItem = new SearchableItem();
            if (poi.get(NUMBER) != null) {
                searchableItem.setName(poi.get(NUMBER));
            } else {
                if (poi.get(POI_NAME).toLowerCase().contains(WC)) {
                    searchableItem.setName(poi.get(POI_NAME) + " | " + poi.get(ATTR));
                } else {
                    searchableItem.setName(poi.get(POI_NAME));
                }
            }
            searchableItem.setType(SearchableItemType.DEFAULT);
            searchableItem.setId(poi.get(_ID));
            searchableItem.setBuilding(poi.get(BUILDING));
            searchableItem.setFloor(poi.get(FLOOR));
            searchableItem.setRoom(poi.get(ROOM));
            searchableItem.setCoordinate(new LatLngFlr(Double.parseDouble(poi.get(LATITUDE)), Double.parseDouble(poi.get(LONGITUDE)), Integer.parseInt(poi.get(FLOOR).substring(0, 1))));
            items.add(searchableItem);
        }
    }

    public static SearchableItemType determineSearchableItemType(String typeStr) {
        switch (typeStr) {
            case "STAIRS":
                return SearchableItemType.STAIRS;
            case "ELEVATOR":
                return SearchableItemType.ELEVATOR;
            case "ROOM":
                return SearchableItemType.ROOM;
            case "FOOD":
                return SearchableItemType.FOOD;
            case "WC":
                return SearchableItemType.WC;
            case "CLINIC":
                return SearchableItemType.CLINIC;
            case "READING":
                return SearchableItemType.READING;
            case "LIBRARY":
                return SearchableItemType.LIBRARY;
            case "EASTER_EGG":
                return SearchableItemType.EASTER_EGG;
            case "EVENT":
                return SearchableItemType.EVENT;
            default:
                return SearchableItemType.DEFAULT;
        }
    }

    private static String getBuildingNameForRoom(int buildingId) {
        Building roomsBuilding = (Building) buildingDAO.findById(buildingId);
        Coordinate buildingsCoordinate = (Coordinate) coordinateDAO.findById(roomsBuilding.getCoordinate_id());
        return buildingsCoordinate.getName();
    }

    public static Predicate<SearchableItem> isWc = new Predicate<SearchableItem>() {
        @Override
        public boolean apply(SearchableItem input) {
            return input.getType() == SearchableItemType.WC;
        }
    };

    public static List<Coordinate> getCoordinatesOfStairsAndElevators() {
        List<Coordinate> stairsAndElevatorsCoordinates = new ArrayList<>();
        List<CoordinateType> coordinateTypes = (List<CoordinateType>) coordinateTypeDAO.findAll();
        HashMap<SearchableItemType, Integer> coordinateTypesMap = new HashMap<>();
        for (CoordinateType coordinateType : coordinateTypes) {
            coordinateTypesMap.put(determineSearchableItemType(coordinateType.getName()), coordinateType.getId());
        }
        stairsAndElevatorsCoordinates.addAll(coordinateDAO.getCoordinatesByTypeId(coordinateTypesMap.get(SearchableItemType.STAIRS)));
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
