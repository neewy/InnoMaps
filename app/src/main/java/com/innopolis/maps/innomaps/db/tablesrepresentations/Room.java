package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/4/16.
 */
@DatabaseTable(tableName = "Rooms")
public class Room {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField
    private Integer number;
    @DatabaseField
    private int building_id;
    @DatabaseField(unique = true)
    private int coordinate_id;
    @DatabaseField
    private int type_id;
    @DatabaseField
    private Date modified = null;

    public Room(int id, Integer number, int building_id, int coordinate_id, int type_id, String modifiedStr) throws ParseException {
        this.id = id;
        this.number = number;
        this.building_id = building_id;
        this.coordinate_id = coordinate_id;
        this.type_id = type_id;
        this.modified = Constants.serverDateFormat.parse(modifiedStr);
    }

    public Room(int id, Integer number, int building_id, int coordinate_id, int type_id, Date modified) {
        this.id = id;
        this.number = number;
        this.building_id = building_id;
        this.coordinate_id = coordinate_id;
        this.type_id = type_id;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public Room() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public Integer getNumber() {
        return number;
    }

    public int getBuilding_id() {
        return building_id;
    }

    public int getCoordinate_id() {
        return coordinate_id;
    }

    public int getType_id() {
        return type_id;
    }

    public String getModified() {
        return Constants.serverDateFormat.format(modified);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Room))
            return false;

        Room room = (Room) o;

        if (getId() != room.getId())
            return false;
        if (getBuilding_id() != room.getBuilding_id())
            return false;
        if (getCoordinate_id() != room.getCoordinate_id())
            return false;
        if (getType_id() != room.getType_id())
            return false;
        if (getNumber() != null ? !getNumber().equals(room.getNumber()) : room.getNumber() != null)
            return false;
        return getModified() != null ? getModified().equals(room.getModified()) : room.getModified() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getNumber() != null ? getNumber().hashCode() : 0);
        result = 31 * result + getBuilding_id();
        result = 31 * result + getCoordinate_id();
        result = 31 * result + getType_id();
        result = 31 * result + (getModified() != null ? getModified().hashCode() : 0);
        return result;
    }
}
