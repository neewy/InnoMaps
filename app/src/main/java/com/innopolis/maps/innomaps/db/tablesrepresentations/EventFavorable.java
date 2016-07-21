package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/19/16.
 */

@DatabaseTable(tableName = "Events")
public class EventFavorable {
    @DatabaseField(id = true, unique = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String description;
    @DatabaseField
    private String link;
    @DatabaseField(unique = true)
    private String gcals_event_id; // Google calendar's event id or null
    @DatabaseField
    private Date modified = null;
    @DatabaseField
    private boolean favourite;

    public EventFavorable(int id, String name, String description, String link, String gcals_event_id, String modifiedStr, boolean favourite) throws ParseException {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.gcals_event_id = gcals_event_id;
        this.modified = Constants.serverDateFormat.parse(modifiedStr);
        this.favourite = favourite;
    }

    public EventFavorable(int id, String name, String description, String link, String gcals_event_id, Date modified, boolean favourite) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.gcals_event_id = gcals_event_id;
        this.modified = modified;
        this.favourite = favourite;
    }

    public EventFavorable(Event event, boolean favourite) {
        this.id = event.getId();
        this.name = event.getName();
        this.description = event.getDescription();
        this.link = event.getLink();
        this.gcals_event_id = event.getGcals_event_id();
        this.modified = event.getModifiedDate();
        this.favourite = favourite;
    }

    // For deserialization with Jackson
    public EventFavorable() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getGcals_event_id() {
        return gcals_event_id;
    }

    public Date getModified() {
        return modified;
    }

    public boolean isFavourite() {
        return favourite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EventFavorable))
            return false;

        EventFavorable that = (EventFavorable) o;

        if (getId() != that.getId())
            return false;
        if (isFavourite() != that.isFavourite())
            return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        if (getLink() != null ? !getLink().equals(that.getLink()) : that.getLink() != null)
            return false;
        if (getGcals_event_id() != null ? !getGcals_event_id().equals(that.getGcals_event_id()) : that.getGcals_event_id() != null)
            return false;
        return getModified() != null ? getModified().equals(that.getModified()) : that.getModified() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getLink() != null ? getLink().hashCode() : 0);
        result = 31 * result + (getGcals_event_id() != null ? getGcals_event_id().hashCode() : 0);
        result = 31 * result + (getModified() != null ? getModified().hashCode() : 0);
        result = 31 * result + (isFavourite() ? 1 : 0);
        return result;
    }
}
