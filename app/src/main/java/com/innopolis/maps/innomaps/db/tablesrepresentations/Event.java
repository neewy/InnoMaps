package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/6/16.
 */
@DatabaseTable(tableName = "Events")
public class Event {
    @DatabaseField(generatedId = true, unique = true)
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

    public Event(int id, String name, String description, String link, String gcals_event_id, String modifiedStr) throws ParseException {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.gcals_event_id = gcals_event_id;
        this.modified = Constants.serverDateFormat.parse(modifiedStr);
    }

    public Event(int id, String name, String description, String link, String gcals_event_id, Date modified) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.link = link;
        this.gcals_event_id = gcals_event_id;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public Event() {
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

    public String getModified() {
        return Constants.serverDateFormat.format(modified);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Event))
            return false;

        Event event = (Event) o;

        if (getId() != event.getId())
            return false;
        if (getName() != null ? !getName().equals(event.getName()) : event.getName() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(event.getDescription()) : event.getDescription() != null)
            return false;
        if (getLink() != null ? !getLink().equals(event.getLink()) : event.getLink() != null)
            return false;
        if (getGcals_event_id() != null ? !getGcals_event_id().equals(event.getGcals_event_id()) : event.getGcals_event_id() != null)
            return false;
        return getModified() != null ? getModified().equals(event.getModified()) : event.getModified() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getLink() != null ? getLink().hashCode() : 0);
        result = 31 * result + (getGcals_event_id() != null ? getGcals_event_id().hashCode() : 0);
        result = 31 * result + (getModified() != null ? getModified().hashCode() : 0);
        return result;
    }
}
