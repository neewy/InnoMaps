package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 7/4/16.
 */
@DatabaseTable(tableName = "Room_types")
public class RoomType {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(unique = true)
    private String name;

    public RoomType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // For deserialization with Jackson
    public RoomType() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RoomType))
            return false;

        RoomType roomType = (RoomType) o;

        if (getId() != roomType.getId())
            return false;
        return getName() != null ? getName().equals(roomType.getName()) : roomType.getName() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}