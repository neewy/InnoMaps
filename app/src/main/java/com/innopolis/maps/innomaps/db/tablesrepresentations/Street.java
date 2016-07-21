package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/4/16.
 */
@DatabaseTable(tableName = "Streets")
public class Street {
    @DatabaseField(id = true, unique = true)
    private int id;
    @DatabaseField(unique = true)
    private String name;
    @DatabaseField
    private Date modified = null;

    public Street(int id, String name, String modifiedStr) throws ParseException {
        this.id = id;
        this.name = name;
        this.modified = Constants.serverDateFormat.parse(modifiedStr);
    }

    public Street(int id, String name, Date modified) {
        this.id = id;
        this.name = name;
        this.modified = modified;
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

    public String getModified() {
        return Constants.serverDateFormat.format(modified);
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
        if (getName() != null ? !getName().equals(street.getName()) : street.getName() != null)
            return false;
        return getModified() != null ? getModified().equals(street.getModified()) : street.getModified() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getModified() != null ? getModified().hashCode() : 0);
        return result;
    }
}