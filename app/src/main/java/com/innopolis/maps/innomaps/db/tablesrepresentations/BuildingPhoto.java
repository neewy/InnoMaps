package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/4/16.
 */
@DatabaseTable(tableName = "Building_photos")
public class BuildingPhoto {
    @DatabaseField(uniqueCombo = true)
    private int building_id;
    @DatabaseField(uniqueCombo = true)
    private int photo_id;
    @DatabaseField
    private Date created = null;

    public BuildingPhoto(int building_id, int photo_id, String createdStr) throws ParseException {
        this.building_id = building_id;
        this.photo_id = photo_id;
        this.created = Constants.serverDateFormat.parse(createdStr);
    }

    public BuildingPhoto(int building_id, int photo_id, Date created) {
        this.building_id = building_id;
        this.photo_id = photo_id;
        this.created = created;
    }

    public BuildingPhoto() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getBuilding_id() {
        return building_id;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public String getCreated() {
        return Constants.serverDateFormat.format(created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BuildingPhoto))
            return false;

        BuildingPhoto that = (BuildingPhoto) o;

        if (getBuilding_id() != that.getBuilding_id())
            return false;
        if (getPhoto_id() != that.getPhoto_id())
            return false;
        return getCreated() != null ? getCreated().equals(that.getCreated()) : that.getCreated() == null;

    }

    @Override
    public int hashCode() {
        int result = getBuilding_id();
        result = 31 * result + getPhoto_id();
        result = 31 * result + (getCreated() != null ? getCreated().hashCode() : 0);
        return result;
    }
}
