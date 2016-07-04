package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by alnedorezov on 7/4/16.
 */
@DatabaseTable(tableName = "Photos")
public class Photo {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField(unique = true)
    private String url;

    public Photo(int id, String url) {
        this.id = id;
        this.url = url;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Photo))
            return false;

        Photo photo = (Photo) o;

        if (getId() != photo.getId())
            return false;
        return getUrl() != null ? getUrl().equals(photo.getUrl()) : photo.getUrl() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getUrl() != null ? getUrl().hashCode() : 0);
        return result;
    }
}