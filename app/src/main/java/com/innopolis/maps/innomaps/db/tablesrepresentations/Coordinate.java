package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/4/16.
 */
@DatabaseTable(tableName = "Coordinates")
public class Coordinate {
    @DatabaseField(unique = true)
    private int id;
    @DatabaseField(uniqueCombo = true)
    private double latitude;
    @DatabaseField(uniqueCombo = true)
    private double longitude;
    @DatabaseField(uniqueCombo = true)
    private int floor;
    @DatabaseField
    private int type_id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String description;
    @DatabaseField
    private Date modified = null;

    public Coordinate(int id, double latitude, double longitude, int floor, int type_id, String name, String description, String modifiedStr) throws ParseException {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.floor = floor;
        this.type_id = type_id;
        this.name = name;
        this.description = description;
        this.modified = Constants.serverDateFormat.parse(modifiedStr);
    }

    public Coordinate(int id, double latitude, double longitude, int floor, int type_id, String name, String description, Date modified) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.floor = floor;
        this.type_id = type_id;
        this.name = name;
        this.description = description;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public Coordinate() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getFloor() {
        return floor;
    }

    public int getType_id() {
        return type_id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getModified() {
        return Constants.serverDateFormat.format(modified);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Coordinate))
            return false;

        Coordinate that = (Coordinate) o;

        if (getId() != that.getId())
            return false;
        if (Double.compare(that.getLatitude(), getLatitude()) != 0)
            return false;
        if (Double.compare(that.getLongitude(), getLongitude()) != 0)
            return false;
        if (getFloor() != that.getFloor())
            return false;
        if (getType_id() != that.getType_id())
            return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        return getModified() != null ? getModified().equals(that.getModified()) : that.getModified() == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getId();
        temp = Double.doubleToLongBits(getLatitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getFloor();
        result = 31 * result + getType_id();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getModified() != null ? getModified().hashCode() : 0);
        return result;
    }
}
