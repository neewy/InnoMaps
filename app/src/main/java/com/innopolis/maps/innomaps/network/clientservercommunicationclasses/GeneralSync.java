package com.innopolis.maps.innomaps.network.clientservercommunicationclasses;

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

import java.util.List;

/**
 * Created by alnedorezov on 7/14/16.
 */

public class GeneralSync {
    private List<CoordinateType> coordinateTypes;
    private List<EdgeType> edgeTypes;
    private List<RoomType> roomTypes;
    private List<Coordinate> coordinates;
    private List<Edge> edges;
    private List<Street> streets;
    private List<Building> buildings;
    private List<Room> rooms;
    private List<Photo> photos;
    private List<BuildingPhoto> buildingPhotos;
    private List<RoomPhoto> roomPhotos;
    private List<BuildingFloorOverlay> buildingFloorOverlays;
    private List<EventCreator> eventCreators;
    private List<Event> events;
    private List<EventSchedule> eventSchedules;
    private List<EventCreatorAppointment> eventCreatorAppointments;

    private GeneralSync(List<CoordinateType> coordinateTypes, List<EdgeType> edgeTypes, List<RoomType> roomTypes,
                        List<Coordinate> coordinates, List<Edge> edges, List<Street> streets, List<Building> buildings,
                        List<Room> rooms, List<Photo> photos, List<BuildingPhoto> buildingPhotos, List<RoomPhoto> roomPhotos,
                        List<BuildingFloorOverlay> buildingFloorOverlays, List<EventCreator> eventCreators, List<Event> events,
                        List<EventSchedule> eventSchedules, List<EventCreatorAppointment> eventCreatorAppointments) {
        this.coordinateTypes = coordinateTypes;
        this.edgeTypes = edgeTypes;
        this.roomTypes = roomTypes;
        this.coordinates = coordinates;
        this.edges = edges;
        this.streets = streets;
        this.buildings = buildings;
        this.rooms = rooms;
        this.photos = photos;
        this.buildingPhotos = buildingPhotos;
        this.roomPhotos = roomPhotos;
        this.buildingFloorOverlays = buildingFloorOverlays;
        this.eventCreators = eventCreators;
        this.events = events;
        this.eventSchedules = eventSchedules;
        this.eventCreatorAppointments = eventCreatorAppointments;
    }

    // For deserialization with Jackson
    public GeneralSync() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public static class GeneralSyncBuilder {
        private List<CoordinateType> coordinateTypes;
        private List<EdgeType> edgeTypes;
        private List<RoomType> roomTypes;
        private List<Coordinate> coordinates;
        private List<Edge> edges;
        private List<Street> streets;
        private List<Building> buildings;
        private List<Room> rooms;
        private List<Photo> photos;
        private List<BuildingPhoto> buildingPhotos;
        private List<RoomPhoto> roomPhotos;
        private List<BuildingFloorOverlay> buildingFloorOverlays;
        private List<EventCreator> eventCreators;
        private List<Event> events;
        private List<EventSchedule> eventSchedules;
        private List<EventCreatorAppointment> eventCreatorAppointments;

        public GeneralSyncBuilder() {
            // Empty constructor for builder class
        }

        public GeneralSyncBuilder setTypes(List<CoordinateType> coordinateTypes, List<EdgeType> edgeTypes, List<RoomType> roomTypes) {
            this.coordinateTypes = coordinateTypes;
            this.edgeTypes = edgeTypes;
            this.roomTypes = roomTypes;
            return this;
        }

        public GeneralSyncBuilder setMapUnits(List<Coordinate> coordinates, List<Edge> edges, List<Street> streets, List<Building> buildings,
                                              List<Room> rooms, List<Photo> photos, List<BuildingFloorOverlay> buildingFloorOverlays) {
            this.coordinates = coordinates;
            this.edges = edges;
            this.streets = streets;
            this.buildings = buildings;
            this.rooms = rooms;
            this.photos = photos;
            this.buildingFloorOverlays = buildingFloorOverlays;
            return this;
        }

        public GeneralSyncBuilder setEvents(List<EventCreator> eventCreators, List<Event> events, List<EventSchedule> eventSchedules) {
            this.eventCreators = eventCreators;
            this.events = events;
            this.eventSchedules = eventSchedules;
            return this;
        }

        public GeneralSyncBuilder setAssignments(List<BuildingPhoto> buildingPhotos, List<RoomPhoto> roomPhotos, List<EventCreatorAppointment> eventCreatorAppointments) {
            this.buildingPhotos = buildingPhotos;
            this.roomPhotos = roomPhotos;
            this.eventCreatorAppointments = eventCreatorAppointments;
            return this;
        }

        public GeneralSync build() {
            return new GeneralSync(coordinateTypes, edgeTypes, roomTypes, coordinates, edges, streets, buildings, rooms, photos,
                    buildingPhotos, roomPhotos, buildingFloorOverlays, eventCreators, events, eventSchedules, eventCreatorAppointments);
        }
    }

    public void addCoordinateType(CoordinateType coordinateType) {
        this.coordinateTypes.add(coordinateType);
    }

    public void setCoordinateType(int index, CoordinateType coordinateType) {
        this.coordinateTypes.set(index, coordinateType);
    }

    public CoordinateType getCoordinateType(int index) {
        return this.coordinateTypes.get(index);
    }

    public void removeCoordinateType(int index) {
        this.coordinateTypes.remove(index);
    }

    public List<CoordinateType> getCoordinateTypes() {
        return coordinateTypes;
    }


    public void addEdgeType(EdgeType edgeType) {
        this.edgeTypes.add(edgeType);
    }

    public void setEdgeType(int index, EdgeType edgeType) {
        this.edgeTypes.set(index, edgeType);
    }

    public EdgeType getEdgeType(int index) {
        return this.edgeTypes.get(index);
    }

    public void removeEdgeType(int index) {
        this.edgeTypes.remove(index);
    }

    public List<EdgeType> getEdgeTypes() {
        return edgeTypes;
    }


    public void addRoomType(RoomType roomType) {
        this.roomTypes.add(roomType);
    }

    public void setRoomType(int index, RoomType roomType) {
        this.roomTypes.set(index, roomType);
    }

    public RoomType getRoomType(int index) {
        return this.roomTypes.get(index);
    }

    public void removeRoomType(int index) {
        this.roomTypes.remove(index);
    }

    public List<RoomType> getRoomTypes() {
        return roomTypes;
    }


    public void addCoordinate(Coordinate coordinate) {
        this.coordinates.add(coordinate);
    }

    public void setCoordinate(int index, Coordinate coordinate) {
        this.coordinates.set(index, coordinate);
    }

    public Coordinate getCoordinate(int index) {
        return this.coordinates.get(index);
    }

    public void removeCoordinate(int index) {
        this.coordinates.remove(index);
    }

    public List<Coordinate> getCoordinates() {
        return coordinates;
    }


    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    public void setEdge(int index, Edge edge) {
        this.edges.set(index, edge);
    }

    public Edge getEdge(int index) {
        return this.edges.get(index);
    }

    public void removeEdge(int index) {
        this.edges.remove(index);
    }

    public List<Edge> getEdges() {
        return edges;
    }


    public void addStreet(Street street) {
        this.streets.add(street);
    }

    public void setStreet(int index, Street street) {
        this.streets.set(index, street);
    }

    public Street getStreet(int index) {
        return this.streets.get(index);
    }

    public void removeStreet(int index) {
        this.streets.remove(index);
    }

    public List<Street> getStreets() {
        return streets;
    }


    public void addBuilding(Building building) {
        this.buildings.add(building);
    }

    public void setBuilding(int index, Building building) {
        this.buildings.set(index, building);
    }

    public Building getBuilding(int index) {
        return this.buildings.get(index);
    }

    public void removeBuilding(int index) {
        this.buildings.remove(index);
    }

    public List<Building> getBuildings() {
        return buildings;
    }


    public void addRoom(Room room) {
        this.rooms.add(room);
    }

    public void setRoom(int index, Room room) {
        this.rooms.set(index, room);
    }

    public Room getRoom(int index) {
        return this.rooms.get(index);
    }

    public void removeRoom(int index) {
        this.rooms.remove(index);
    }

    public List<Room> getRooms() {
        return rooms;
    }


    public void addPhoto(Photo photo) {
        this.photos.add(photo);
    }

    public void setPhoto(int index, Photo photo) {
        this.photos.set(index, photo);
    }

    public Photo getPhoto(int index) {
        return this.photos.get(index);
    }

    public void removePhoto(int index) {
        this.photos.remove(index);
    }

    public List<Photo> getPhotos() {
        return photos;
    }


    public void addBuildingPhoto(BuildingPhoto buildingPhoto) {
        this.buildingPhotos.add(buildingPhoto);
    }

    public void setBuildingPhoto(int index, BuildingPhoto buildingPhoto) {
        this.buildingPhotos.set(index, buildingPhoto);
    }

    public BuildingPhoto getBuildingPhoto(int index) {
        return this.buildingPhotos.get(index);
    }

    public void removeBuildingPhoto(int index) {
        this.buildingPhotos.remove(index);
    }

    public List<BuildingPhoto> getBuildingPhotos() {
        return buildingPhotos;
    }


    public void addRoomPhoto(RoomPhoto roomPhoto) {
        this.roomPhotos.add(roomPhoto);
    }

    public void setRoomPhoto(int index, RoomPhoto roomPhoto) {
        this.roomPhotos.set(index, roomPhoto);
    }

    public RoomPhoto getRoomPhoto(int index) {
        return this.roomPhotos.get(index);
    }

    public void removeRoomPhoto(int index) {
        this.roomPhotos.remove(index);
    }

    public List<RoomPhoto> getRoomPhotos() {
        return roomPhotos;
    }


    public void addBuildingFloorOverlay(BuildingFloorOverlay buildingFloorOverlay) {
        this.buildingFloorOverlays.add(buildingFloorOverlay);
    }

    public void setBuildingFloorOverlay(int index, BuildingFloorOverlay buildingFloorOverlay) {
        this.buildingFloorOverlays.set(index, buildingFloorOverlay);
    }

    public BuildingFloorOverlay getBuildingFloorOverlay(int index) {
        return this.buildingFloorOverlays.get(index);
    }

    public void removeBuildingFloorOverlay(int index) {
        this.buildingFloorOverlays.remove(index);
    }

    public List<BuildingFloorOverlay> getBuildingFloorOverlays() {
        return buildingFloorOverlays;
    }


    public void addEventCreator(EventCreator eventCreator) {
        this.eventCreators.add(eventCreator);
    }

    public void setEventCreator(int index, EventCreator eventCreator) {
        this.eventCreators.set(index, eventCreator);
    }

    public EventCreator getEventCreator(int index) {
        return this.eventCreators.get(index);
    }

    public void removeEventCreator(int index) {
        this.eventCreators.remove(index);
    }

    public List<EventCreator> getEventCreators() {
        return eventCreators;
    }


    public void addEvent(Event event) {
        this.events.add(event);
    }

    public void setEvent(int index, Event event) {
        this.events.set(index, event);
    }

    public Event getEvent(int index) {
        return this.events.get(index);
    }

    public void removeEvent(int index) {
        this.events.remove(index);
    }

    public List<Event> getEvents() {
        return events;
    }


    public void addEventSchedule(EventSchedule eventSchedule) {
        this.eventSchedules.add(eventSchedule);
    }

    public void setEventSchedule(int index, EventSchedule eventSchedule) {
        this.eventSchedules.set(index, eventSchedule);
    }

    public EventSchedule getEventSchedule(int index) {
        return this.eventSchedules.get(index);
    }

    public void removeEventSchedule(int index) {
        this.eventSchedules.remove(index);
    }

    public List<EventSchedule> getEventSchedules() {
        return eventSchedules;
    }


    public void addEventCreatorAppointment(EventCreatorAppointment eventCreatorAppointment) {
        this.eventCreatorAppointments.add(eventCreatorAppointment);
    }

    public void setEventCreatorAppointment(int index, EventCreatorAppointment eventCreatorAppointment) {
        this.eventCreatorAppointments.set(index, eventCreatorAppointment);
    }

    public EventCreatorAppointment getEventCreatorAppointment(int index) {
        return this.eventCreatorAppointments.get(index);
    }

    public void removeEventCreatorAppointment(int index) {
        this.eventCreatorAppointments.remove(index);
    }

    public List<EventCreatorAppointment> getEventCreatorAppointments() {
        return eventCreatorAppointments;
    }
}