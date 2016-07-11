package com.innopolis.maps.innomaps.network.clientservercommunicationclasses;


import java.util.List;

/**
 * Created by alnedorezov on 7/11/16.
 */
public class TypesSync {
    private List<Integer> coordinateTypeIds;
    private List<Integer> edgeTypeIds;
    private List<Integer> roomTypeIds;

    public TypesSync(List<Integer> coordinateTypeIds, List<Integer> edgeTypeIds, List<Integer> roomTypeIds) {
        this.coordinateTypeIds = coordinateTypeIds;
        this.edgeTypeIds = edgeTypeIds;
        this.roomTypeIds = roomTypeIds;
    }

    // For deserialization with Jackson
    public TypesSync() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public void addCoordinateTypeId(Integer coordinateTypeId) {
        this.coordinateTypeIds.add(coordinateTypeId);
    }

    public void setCoordinateTypeId(int index, Integer coordinateTypeId) {
        this.coordinateTypeIds.set(index, coordinateTypeId);
    }

    public Integer getCoordinateTypeId(int index) {
        return coordinateTypeIds.get(index);
    }

    public void removeCoordinateTypeId(int index) {
        this.coordinateTypeIds.remove(index);
    }

    public List<Integer> getCoordinateTypeIds() {
        return coordinateTypeIds;
    }


    public void addEdgeTypeId(Integer edgeTypeId) {
        this.edgeTypeIds.add(edgeTypeId);
    }

    public void setEdgeTypeId(int index, Integer edgeTypeId) {
        this.edgeTypeIds.set(index, edgeTypeId);
    }

    public Integer getEdgeTypeId(int index) {
        return edgeTypeIds.get(index);
    }

    public void removeEdgeTypeId(int index) {
        this.edgeTypeIds.remove(index);
    }

    public List<Integer> getEdgeTypeIds() {
        return edgeTypeIds;
    }


    public void addRoomTypeId(Integer roomTypeId) {
        this.roomTypeIds.add(roomTypeId);
    }

    public void setRoomTypeId(int index, Integer roomTypeId) {
        this.roomTypeIds.set(index, roomTypeId);
    }

    public Integer getRoomTypeId(int index) {
        return roomTypeIds.get(index);
    }

    public void removeRoomTypeId(int index) {
        this.roomTypeIds.remove(index);
    }

    public List<Integer> getRoomTypeIds() {
        return roomTypeIds;
    }
}

