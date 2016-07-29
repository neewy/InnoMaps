package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/6/16.
 */
@DatabaseTable(tableName = "Building_floor_overlays")
public class BuildingFloorOverlay {
    @DatabaseField(id = true, unique = true)
    private int id;
    @DatabaseField(uniqueCombo = true)
    private int building_id;
    @DatabaseField
    private int photo_id;
    @DatabaseField(uniqueCombo = true)
    private int floor;
    @DatabaseField
    private double southWestLatitude;
    @DatabaseField
    private double southWestLongitude;
    @DatabaseField
    private double northEastLatitude;
    @DatabaseField
    private double northEastLongitude;
    @DatabaseField
    private Date modified = null;

    public BuildingFloorOverlay(int id, int building_id, int photo_id, int floor, double southWestLatitude, double southWestLongitude,
                                double northEastLatitude, double northEastLongitude, String modifiedStr) throws ParseException {
        this.id = id;
        this.building_id = building_id;
        this.photo_id = photo_id;
        this.floor = floor;
        this.southWestLatitude = southWestLatitude;
        this.southWestLongitude = southWestLongitude;
        this.northEastLatitude = northEastLatitude;
        this.northEastLongitude = northEastLongitude;
        this.modified = Constants.serverDateFormat.parse(modifiedStr);
    }

    public BuildingFloorOverlay(int id, int building_id, int photo_id, int floor, double southWestLatitude, double southWestLongitude,
                                double northEastLatitude, double northEastLongitude, Date modified) {
        this.id = id;
        this.building_id = building_id;
        this.photo_id = photo_id;
        this.floor = floor;
        this.southWestLatitude = southWestLatitude;
        this.southWestLongitude = southWestLongitude;
        this.northEastLatitude = northEastLatitude;
        this.northEastLongitude = northEastLongitude;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public BuildingFloorOverlay() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public int getBuilding_id() {
        return building_id;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public int getFloor() {
        return floor;
    }

    public double getSouthWestLatitude() {
        return southWestLatitude;
    }

    public double getSouthWestLongitude() {
        return southWestLongitude;
    }

    public double getNorthEastLatitude() {
        return northEastLatitude;
    }

    public double getNorthEastLongitude() {
        return northEastLongitude;
    }

    public String getModified() {
        return Constants.serverDateFormat.format(modified);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BuildingFloorOverlay))
            return false;

        BuildingFloorOverlay that = (BuildingFloorOverlay) o;

        if (getId() != that.getId())
            return false;
        if (getBuilding_id() != that.getBuilding_id())
            return false;
        if (getPhoto_id() != that.getPhoto_id())
            return false;
        if (getFloor() != that.getFloor())
            return false;
        if (Double.compare(that.getSouthWestLatitude(), getSouthWestLatitude()) != 0)
            return false;
        if (Double.compare(that.getSouthWestLongitude(), getSouthWestLongitude()) != 0)
            return false;
        if (Double.compare(that.getNorthEastLatitude(), getNorthEastLatitude()) != 0)
            return false;
        if (Double.compare(that.getNorthEastLongitude(), getNorthEastLongitude()) != 0)
            return false;
        return getModified() != null ? getModified().equals(that.getModified()) : that.getModified() == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getId();
        result = 31 * result + getBuilding_id();
        result = 31 * result + getPhoto_id();
        result = 31 * result + getFloor();
        temp = Double.doubleToLongBits(getSouthWestLatitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getSouthWestLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getNorthEastLatitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getNorthEastLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (getModified() != null ? getModified().hashCode() : 0);
        return result;
    }
}
