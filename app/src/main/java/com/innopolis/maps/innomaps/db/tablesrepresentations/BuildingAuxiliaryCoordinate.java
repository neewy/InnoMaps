package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/20/16.
 */

@DatabaseTable(tableName = "Building_auxiliary_coordinates")
public class BuildingAuxiliaryCoordinate {
    @DatabaseField(uniqueCombo = true)
    private int building_id;
    @DatabaseField(uniqueCombo = true)
    private int coordinate_id;
    @DatabaseField
    private Date created = null;

    public BuildingAuxiliaryCoordinate(int building_id, int coordinate_id, String createdStr) throws ParseException {
        this.building_id = building_id;
        this.coordinate_id = coordinate_id;
        this.created = Constants.serverDateFormat.parse(createdStr);
    }

    public BuildingAuxiliaryCoordinate(int building_id, int coordinate_id, Date created) {
        this.building_id = building_id;
        this.coordinate_id = coordinate_id;
        this.created = created;
    }

    public BuildingAuxiliaryCoordinate() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getBuilding_id() {
        return building_id;
    }

    public int getCoordinate_id() {
        return coordinate_id;
    }

    public String getCreated() {
        return Constants.serverDateFormat.format(created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BuildingAuxiliaryCoordinate))
            return false;

        BuildingAuxiliaryCoordinate that = (BuildingAuxiliaryCoordinate) o;

        if (getBuilding_id() != that.getBuilding_id())
            return false;
        if (getCoordinate_id() != that.getCoordinate_id())
            return false;
        return getCreated() != null ? getCreated().equals(that.getCreated()) : that.getCreated() == null;

    }

    @Override
    public int hashCode() {
        int result = getBuilding_id();
        result = 31 * result + getCoordinate_id();
        result = 31 * result + (getCreated() != null ? getCreated().hashCode() : 0);
        return result;
    }
}