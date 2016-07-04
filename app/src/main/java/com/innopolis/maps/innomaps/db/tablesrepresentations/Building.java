package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/4/16.
 */
@DatabaseTable(tableName = "Buildings")
public class Building {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(uniqueCombo = true)
    private String number;
    @DatabaseField(uniqueCombo = true)
    private Integer block;
    @DatabaseField
    private String description;
    @DatabaseField(unique = true) // there can be no two buildings on the same spot
    private int coordinate_id;
    @DatabaseField(uniqueCombo = true)
    private int street_id;
    @DatabaseField
    private Date modified = null;

    public Building(int id, String number, Integer block, String description, int coordinate_id, int street_id, String modifiedStr) throws ParseException {
        this.id = id;
        this.number = number;
        this.block = block;
        this.description = description;
        this.coordinate_id = coordinate_id;
        this.street_id = street_id;
        this.modified = Constants.serverDateFormat.parse(modifiedStr);
    }

    public Building(int id, String number, Integer block, String description, int coordinate_id, int street_id, Date modified) {
        this.id = id;
        this.number = number;
        this.block = block;
        this.description = description;
        this.coordinate_id = coordinate_id;
        this.street_id = street_id;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public Building() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public Integer getBlock() {
        return block;
    }

    public String getDescription() {
        return description;
    }

    public int getCoordinate_id() {
        return coordinate_id;
    }

    public int getStreet_id() {
        return street_id;
    }

    public String getModified() {
        return Constants.serverDateFormat.format(modified);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Building))
            return false;

        Building building = (Building) o;

        if (getId() != building.getId())
            return false;
        if (getCoordinate_id() != building.getCoordinate_id())
            return false;
        if (getStreet_id() != building.getStreet_id())
            return false;
        if (getNumber() != null ? !getNumber().equals(building.getNumber()) : building.getNumber() != null)
            return false;
        if (getBlock() != null ? !getBlock().equals(building.getBlock()) : building.getBlock() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(building.getDescription()) : building.getDescription() != null)
            return false;
        return getModified() != null ? getModified().equals(building.getModified()) : building.getModified() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getNumber() != null ? getNumber().hashCode() : 0);
        result = 31 * result + (getBlock() != null ? getBlock().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + getCoordinate_id();
        result = 31 * result + getStreet_id();
        result = 31 * result + (getModified() != null ? getModified().hashCode() : 0);
        return result;
    }
}