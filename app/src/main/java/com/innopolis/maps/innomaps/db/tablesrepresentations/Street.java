package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 7/4/16.
 */
@DatabaseTable(tableName = "Streets")
public class Street {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(unique = true)
    private String name;

    public Street(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // For deserialization with Jackson
    public Street() {
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
        if (!(o instanceof Street))
            return false;

        Street street = (Street) o;

        if (getId() != street.getId())
            return false;
        return getName() != null ? getName().equals(street.getName()) : street.getName() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }
}