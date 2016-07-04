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
    @DatabaseField(generatedId = true, unique = true)
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
        if (this == o) {
            return true;
        } else if (!(o instanceof Coordinate)) {
            return false;
        } else {
            Coordinate var2 = (Coordinate) o;
            return getId() == var2.getId() &&
                    Double.doubleToLongBits(getLatitude()) == Double.doubleToLongBits(var2.getLatitude()) &&
                    Double.doubleToLongBits(getLongitude()) == Double.doubleToLongBits(var2.getLongitude()) &&
                    getFloor() == var2.getFloor() &&
                    getType_id() == var2.getType_id() &&
                    getName().equals(var2.getName()) &&
                    getDescription().equals(var2.getDescription()) &&
                    getModified().equals(var2.getModified());
        }
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + floor;
        result = 31 * result + type_id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        return result;
    }
}
