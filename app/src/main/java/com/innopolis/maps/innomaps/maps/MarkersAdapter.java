package com.innopolis.maps.innomaps.maps;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.app.SearchableItem;
import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateTypeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventScheduleDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomTypeDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.CoordinateType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventFavorable;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventSchedule;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomType;

import java.util.ArrayList;
import java.util.List;


/**
 * This class is responsible for drawing markers that show
 * names and numbers of rooms. In addition it contains adapters for graphic
 */

public class MarkersAdapter extends BottomSheet {
    MapView mapView;
    List<Marker> markers; //store all markers
    List<Integer> filterList; //to store elements after choosing filter

    RoomDAO roomDAO = new RoomDAO(MainActivity.getMainActivityContext());
    RoomTypeDAO roomTypeDAO = new RoomTypeDAO(MainActivity.getMainActivityContext());
    CoordinateDAO coordinateDAO = new CoordinateDAO(MainActivity.getMainActivityContext());
    CoordinateTypeDAO coordinateTypeDAO = new CoordinateTypeDAO(MainActivity.getMainActivityContext());
    EventDAO eventDAO = new EventDAO(MainActivity.getMainActivityContext());
    EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(MainActivity.getMainActivityContext());


    /**
     * Switch markers by id of optionMenu under toolbar
     *
     * @param floor
     */

    protected void isMarkerSorted(int floor) {
        int filter = filterList.get(0);
        if (filter == Constants.WC_FILTER) {
            makeWcMarkers(floor);
        } else if (filter == Constants.FOOD_FILTER) {
            makeFoodMarkers(floor);
        } else if (filter == Constants.ALL_FILTER) {
            makeAllMarkers(floor);
        } else if (filter == Constants.EVENTS_FILTER) {
            makeEventsMarkers(floor);
        } else if (filter == Constants.OTHER_FILTER) {
            makeOtherMarkers(floor);
        }
    }


    /**
     * Filters markers on map and shows wc
     *
     * @param floor
     */
    private void makeWcMarkers(int floor) {
        List<MarkerForRoom> markersForRooms = new ArrayList<>();
        RoomType roomType = roomTypeDAO.findRoomTypeByName(Constants.WC);
        List<Integer> typeIds = new ArrayList<>();
        if (null != roomType)
            typeIds.add(roomType.getId());
        List<Room> rooms = roomDAO.findRoomsWithFollowingTypesAndFloor(typeIds, floor);
        for (Room room : rooms) {
            Coordinate roomsCoordinate = (Coordinate) coordinateDAO.findById(room.getCoordinate_id());
            String roomsName = SearchableItem.getRoomsName(room, roomsCoordinate);
            if (null != roomsName)
                markersForRooms.add(new MarkerForRoom(roomsName, Constants.WC, roomsCoordinate.getLatitude(), roomsCoordinate.getLongitude()));
        }

        refreshMarkers(markersForRooms);
    }

    /**
     * Filters markers on map and shows only food places
     *
     * @param floor
     */
    private void makeFoodMarkers(int floor) {
        List<MarkerForRoom> markersForRooms = new ArrayList<>();
        RoomType roomType = roomTypeDAO.findRoomTypeByName(Constants.FOOD);
        List<Integer> typeIds = new ArrayList<>();
        if (null != roomType)
            typeIds.add(roomType.getId());
        List<Room> rooms = roomDAO.findRoomsWithFollowingTypesAndFloor(typeIds, floor);
        for (Room room : rooms) {
            Coordinate roomsCoordinate = (Coordinate) coordinateDAO.findById(room.getCoordinate_id());
            String roomsName = SearchableItem.getRoomsName(room, roomsCoordinate);
            if (null != roomsName)
                markersForRooms.add(new MarkerForRoom(roomsName, Constants.FOOD, roomsCoordinate.getLatitude(), roomsCoordinate.getLongitude()));
        }

        refreshMarkers(markersForRooms);
    }

    /**
     * Filters markers on map and shows markers like "library" or "clinic"
     *
     * @param floor
     */
    private void makeOtherMarkers(int floor) {
        List<MarkerForRoom> markersForRooms = new ArrayList<>();
        List<Integer> typeIds = new ArrayList<>();
        RoomType roomType = roomTypeDAO.findRoomTypeByName(Constants.DOOR);
        if (null != roomType)
            typeIds.add(roomType.getId());
        roomType = roomTypeDAO.findRoomTypeByName(Constants.WC);
        if (null != roomType)
            typeIds.add(roomType.getId());
        roomType = roomTypeDAO.findRoomTypeByName(Constants.FOOD);
        if (null != roomType)
            typeIds.add(roomType.getId());
        List<Room> rooms = roomDAO.findRoomsOnFloorExceptWithFollowingTypes(typeIds, floor);
        for (Room room : rooms) {
            Coordinate roomsCoordinate = (Coordinate) coordinateDAO.findById(room.getCoordinate_id());
            String roomsName = SearchableItem.getRoomsName(room, roomsCoordinate);
            RoomType roomsType = (RoomType) roomTypeDAO.findById(room.getType_id());
            if (null != roomsName)
                markersForRooms.add(new MarkerForRoom(roomsName, roomsType.getName(), roomsCoordinate.getLatitude(), roomsCoordinate.getLongitude()));
        }
        List<Coordinate> coordinates = getCoordinatesOfStairsAndElevatorsForFloor(floor);
        for (Coordinate coordinate : coordinates) {
            if (null != coordinate.getName() && !Constants.EMPTY_STRING.equals(coordinate.getName())) {
                CoordinateType coordinateType = (CoordinateType) coordinateTypeDAO.findById(coordinate.getType_id());
                markersForRooms.add(new MarkerForRoom(coordinate.getName(), coordinateType.getName(), coordinate.getLatitude(), coordinate.getLongitude()));
            }
        }

        refreshMarkers(markersForRooms);
    }


    /**
     * Filters markers on map and shows all markers
     *
     * @param floor
     */
    protected void makeAllMarkers(int floor) {
        List<MarkerForRoom> markersForRooms = new ArrayList<>();
        RoomType roomType = roomTypeDAO.findRoomTypeByName(Constants.DOOR);
        List<Integer> typeIds = new ArrayList<>();
        if (null != roomType)
            typeIds.add(roomType.getId());
        List<Room> rooms = roomDAO.findRoomsOnFloorExceptWithFollowingTypes(typeIds, floor);
        for (Room room : rooms) {
            Coordinate roomsCoordinate = (Coordinate) coordinateDAO.findById(room.getCoordinate_id());
            String roomsName = SearchableItem.getRoomsName(room, roomsCoordinate);
            RoomType roomsType = (RoomType) roomTypeDAO.findById(room.getType_id());
            if (null != roomsName)
                markersForRooms.add(new MarkerForRoom(roomsName, roomsType.getName(), roomsCoordinate.getLatitude(), roomsCoordinate.getLongitude()));
        }
        List<Coordinate> coordinates = getCoordinatesOfStairsAndElevatorsForFloor(floor);
        for (Coordinate coordinate : coordinates) {
            if (null != coordinate.getName() && !Constants.EMPTY_STRING.equals(coordinate.getName())) {
                CoordinateType coordinateType = (CoordinateType) coordinateTypeDAO.findById(coordinate.getType_id());
                markersForRooms.add(new MarkerForRoom(coordinate.getName(), coordinateType.getName(), coordinate.getLatitude(), coordinate.getLongitude()));
            }
        }

        refreshMarkers(markersForRooms);
    }


    /**
     * Filters markers on map and shows only rooms with events
     *
     * @param floor
     */
    protected void makeEventsMarkers(int floor) {
        List<MarkerForRoom> markersForRooms = new ArrayList<>();

        List<EventSchedule> eventSchedules = eventScheduleDAO.findUpcomingAndOngoingScheduledEventsOnTheSpecifiedFloor(floor);
        for (EventSchedule eventSchedule : eventSchedules) {
            if (eventSchedule.getLocation_id() != null) {
                EventFavorable event = (EventFavorable) eventDAO.findById(eventSchedule.getEvent_id());
                Coordinate eventsCoordinate = (Coordinate) coordinateDAO.findById(eventSchedule.getLocation_id());
                markersForRooms.add(new MarkerForRoom(event.getName(), Constants.EVENT_CAPITAL_CASE, eventsCoordinate.getLatitude(), eventsCoordinate.getLongitude()));
            }
        }

        refreshMarkers(markersForRooms);
    }


    /**
     * Clears markers and finds new
     */
    private void refreshMarkers(List<MarkerForRoom> markersForRooms) {
        if (markers != null) {
            for (Marker marker : markers) {
                marker.remove();
            }
            markers.clear();
        }

        for (MarkerForRoom markersRoom : markersForRooms) {
            setMarkerForRoom(markersRoom);
        }
    }


    /**
     * Puts markers with custom icons on the map
     * Params are info in db table
     */
    private void setMarkerForRoom(MarkerForRoom marker) {

        float center = 0.5f;
        final Marker markersRoom = map.addMarker(new MarkerOptions()
                .position(new LatLng(marker.getLatitude(), marker.getLongitude()))
                .icon(iconBitmapAdapter(marker.getType()))
                .title(marker.title)
                .anchor(center, center)

        );

        markers.add(markersRoom);
    }


    /**
     * Switches icons by type
     *
     * @param type - type of room
     * @return BitmapDescriptor
     */
    public BitmapDescriptor iconBitmapAdapter(String type) {
        int src;
        BitmapDescriptor icon;
        int px;
        int px_large = 30;
        int px_small = 15;

        switch (type) {
            case Constants.EVENT_CAPITAL_CASE:

            case Constants.STAIRS:

            case Constants.ELEVATOR:

            case Constants.ROOM_CAPITAL_CASE:
                src = R.drawable.ic_room;
                px = px_small;
                break;

            case Constants.WC:
                src = R.drawable.wc;
                px = px_large;
                break;

            case Constants.FOOD:
                src = R.drawable.ic_food;
                px = px_large;
                break;

            case Constants.CLINIC:
                src = R.drawable.ic_clinic;
                px = px_large;
                break;

            case Constants.LIBRARY:

            case Constants.READING:
                src = R.drawable.ic_library;
                px = px_large;
                break;

            case Constants.EASTER_EGG:
                src = R.drawable.ic_egg;
                px = px_large;
                break;

            default:
                src = R.drawable.ic_duck;
                px = 30;
                break;
        }


        icon = converterDrawable(src, px);
        return icon;

    }


    /**
     * Converts drawable to a bitmap resource
     *
     * @param src
     * @param size - size in pixels
     * @return bitmap object
     */

    public BitmapDescriptor converterDrawable(int src, int size) {
        BitmapDescriptor icon;
        Drawable shape;
        Bitmap markerBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(markerBitmap);
        shape = ContextCompat.getDrawable(getActivity(), src);
        if (shape != null) {
            shape.setBounds(0, 0, markerBitmap.getWidth(), markerBitmap.getHeight());
            shape.draw(canvas);
        }
        icon = BitmapDescriptorFactory.fromBitmap(markerBitmap);
        return icon;
    }

    private static class MarkerForRoom {
        private String title;
        private String type;
        private Double latitude;
        private Double longitude;

        public MarkerForRoom(String title, String type, Double latitude, Double longitude) {
            this.title = title;
            this.type = type;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }
    }

    public List<Coordinate> getCoordinatesOfStairsAndElevatorsForFloor(int floor) {
        List<Coordinate> stairsAndElevatorsCoordinates = new ArrayList<>();
        CoordinateType coordinateType;
        coordinateType = coordinateTypeDAO.findCoordinateTypeByName(Constants.STAIRS);
        if (null != coordinateType)
            stairsAndElevatorsCoordinates.addAll(coordinateDAO.getCoordinatesByTypeIdAndFloor(coordinateType.getId(), floor));
        coordinateType = coordinateTypeDAO.findCoordinateTypeByName(Constants.ELEVATOR);
        if (null != coordinateType)
            stairsAndElevatorsCoordinates.addAll(coordinateDAO.getCoordinatesByTypeIdAndFloor(coordinateType.getId(), floor));

        return stairsAndElevatorsCoordinates;
    }
}
