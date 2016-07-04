package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 7/4/16.
 */
@DatabaseTable(tableName = "Coordinate_types")
public class CoordinateType {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(unique = true)
    private String name;

    public CoordinateType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // For deserialization with Jackson
    public CoordinateType() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}