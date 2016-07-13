package com.innopolis.maps.innomaps.network.clientservercommunicationclasses;

import java.util.List;

/**
 * Created by alnedorezov on 7/13/16.
 */

public class MapUnitsSync {
    private List<Integer> coordinateIds;
    private List<Integer> edgeIds;
    private List<Integer> streetIds;
    private List<Integer> buildingIds;
    private List<Integer> roomIds;
    private List<Integer> photoIds;
    private List<Integer> buildingFloorOverlayIds;

    public MapUnitsSync(List<Integer> coordinateIds, List<Integer> edgeIds, List<Integer> streetIds, List<Integer> buildingIds,
                        List<Integer> roomIds, List<Integer> photoIds, List<Integer> buildingFloorOverlayIds) {
        this.coordinateIds = coordinateIds;
        this.edgeIds = edgeIds;
        this.streetIds = streetIds;
        this.buildingIds = buildingIds;
        this.roomIds = roomIds;
        this.photoIds = photoIds;
        this.buildingFloorOverlayIds = buildingFloorOverlayIds;
    }

    // For deserialization with Jackson
    public MapUnitsSync() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public void addCoordinateId(Integer coordinateId) {
        this.coordinateIds.add(coordinateId);
    }

    public void setCoordinateId(int index, Integer coordinateId) {
        this.coordinateIds.set(index, coordinateId);
    }

    public Integer getCoordinateId(int index) {
        return coordinateIds.get(index);
    }

    public void removeCoordinateId(int index) {
        this.coordinateIds.remove(index);
    }

    public List<Integer> getCoordinateIds() {
        return coordinateIds;
    }


    public void addEdgeId(Integer edgeId) {
        this.edgeIds.add(edgeId);
    }

    public void setEdgeId(int index, Integer edgeId) {
        this.edgeIds.set(index, edgeId);
    }

    public Integer getEdgeId(int index) {
        return edgeIds.get(index);
    }

    public void removeEdgeId(int index) {
        this.edgeIds.remove(index);
    }

    public List<Integer> getEdgeIds() {
        return edgeIds;
    }


    public void addStreetId(Integer streetId) {
        this.streetIds.add(streetId);
    }

    public void setStreetId(int index, Integer streetId) {
        this.streetIds.set(index, streetId);
    }

    public Integer getStreetId(int index) {
        return streetIds.get(index);
    }

    public void removeStreetId(int index) {
        this.streetIds.remove(index);
    }

    public List<Integer> getStreetIds() {
        return streetIds;
    }


    public void addBuildingId(Integer buildingId) {
        this.buildingIds.add(buildingId);
    }

    public void setBuildingId(int index, Integer buildingId) {
        this.buildingIds.set(index, buildingId);
    }

    public Integer getBuildingId(int index) {
        return buildingIds.get(index);
    }

    public void removeBuildingId(int index) {
        this.buildingIds.remove(index);
    }

    public List<Integer> getBuildingIds() {
        return buildingIds;
    }


    public void addRoomId(Integer roomId) {
        this.roomIds.add(roomId);
    }

    public void setRoomId(int index, Integer roomId) {
        this.roomIds.set(index, roomId);
    }

    public Integer getRoomId(int index) {
        return roomIds.get(index);
    }

    public void removeRoomId(int index) {
        this.roomIds.remove(index);
    }

    public List<Integer> getRoomIds() {
        return roomIds;
    }


    public void addPhotoId(Integer photoId) {
        this.photoIds.add(photoId);
    }

    public void setPhotoId(int index, Integer photoId) {
        this.photoIds.set(index, photoId);
    }

    public Integer getPhotoId(int index) {
        return photoIds.get(index);
    }

    public void removePhotoId(int index) {
        this.photoIds.remove(index);
    }

    public List<Integer> getPhotoIds() {
        return photoIds;
    }


    public void addBuildingFloorOverlayId(Integer buildingFloorOverlayId) {
        this.buildingFloorOverlayIds.add(buildingFloorOverlayId);
    }

    public void setBuildingFloorOverlayId(int index, Integer buildingFloorOverlayId) {
        this.buildingFloorOverlayIds.set(index, buildingFloorOverlayId);
    }

    public Integer getBuildingFloorOverlayId(int index) {
        return buildingFloorOverlayIds.get(index);
    }

    public void removeBuildingFloorOverlayId(int index) {
        this.buildingFloorOverlayIds.remove(index);
    }

    public List<Integer> getBuildingFloorOverlayIds() {
        return buildingFloorOverlayIds;
    }
}