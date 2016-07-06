package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/4/16.
 */
@DatabaseTable(tableName = "Photos")
public class Photo {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(unique = true)
    private String url;
    @DatabaseField
    private Date modified = null;

    public Photo(int id, String url, String modifiedStr) throws ParseException {
        this.id = id;
        this.url = url;
        this.modified = Constants.serverDateFormat.parse(modifiedStr);
    }

    public Photo(int id, String url, Date modified) {
        this.id = id;
        this.url = url;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public Photo() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getModified() {
        return Constants.serverDateFormat.format(modified);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Photo))
            return false;

        Photo photo = (Photo) o;

        if (getId() != photo.getId())
            return false;
        if (getUrl() != null ? !getUrl().equals(photo.getUrl()) : photo.getUrl() != null)
            return false;
        return getModified() != null ? getModified().equals(photo.getModified()) : photo.getModified() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getUrl() != null ? getUrl().hashCode() : 0);
        result = 31 * result + (getModified() != null ? getModified().hashCode() : 0);
        return result;
    }
}