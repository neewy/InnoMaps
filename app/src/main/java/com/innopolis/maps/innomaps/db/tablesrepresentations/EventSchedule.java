package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/6/16.
 */
@DatabaseTable(tableName = "Event_schedules")
public class EventSchedule {
    @DatabaseField(id = true, unique = true)
    private int id;
    @DatabaseField(uniqueCombo = true)
    private Date start_datetime = null;
    @DatabaseField(uniqueCombo = true)
    private Date end_datetime = null;
    @DatabaseField(uniqueCombo = true)
    private Integer location_id;
    @DatabaseField
    private String comment;
    @DatabaseField
    private int event_id;
    @DatabaseField
    private Date modified = null;

    public EventSchedule(int id, String start_datetime_Str, String end_datetime_Str,
                         Integer location_id, String comment, int event_id, String modifiedStr) throws ParseException {
        this.id = id;
        this.start_datetime = Constants.serverDateFormat.parse(start_datetime_Str);
        this.end_datetime = Constants.serverDateFormat.parse(end_datetime_Str);
        this.location_id = location_id;
        this.comment = comment;
        this.event_id = event_id;
        this.modified = Constants.serverDateFormat.parse(modifiedStr);
    }

    public EventSchedule(int id, String start_datetime_Str, String end_datetime_Str,
                         Integer location_id, String comment, int event_id, Date modified) throws ParseException {
        this.id = id;
        this.start_datetime = Constants.serverDateFormat.parse(start_datetime_Str);
        this.end_datetime = Constants.serverDateFormat.parse(end_datetime_Str);
        this.location_id = location_id;
        this.comment = comment;
        this.event_id = event_id;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public EventSchedule() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getStart_datetime() {
        return Constants.serverDateFormat.format(start_datetime);
    }

    public String getEnd_datetime() {
        return Constants.serverDateFormat.format(end_datetime);
    }

    public Integer getLocation_id() {
        return location_id;
    }

    public String getComment() {
        return comment;
    }

    public int getEvent_id() {
        return event_id;
    }

    public String getModified() {
        return Constants.serverDateFormat.format(modified);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EventSchedule))
            return false;

        EventSchedule that = (EventSchedule) o;

        if (getId() != that.getId())
            return false;
        if (getEvent_id() != that.getEvent_id())
            return false;
        if (getStart_datetime() != null ? !getStart_datetime().equals(that.getStart_datetime()) : that.getStart_datetime() != null)
            return false;
        if (getEnd_datetime() != null ? !getEnd_datetime().equals(that.getEnd_datetime()) : that.getEnd_datetime() != null)
            return false;
        if (getLocation_id() != null ? !getLocation_id().equals(that.getLocation_id()) : that.getLocation_id() != null)
            return false;
        if (getComment() != null ? !getComment().equals(that.getComment()) : that.getComment() != null)
            return false;
        return getModified() != null ? getModified().equals(that.getModified()) : that.getModified() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getStart_datetime() != null ? getStart_datetime().hashCode() : 0);
        result = 31 * result + (getEnd_datetime() != null ? getEnd_datetime().hashCode() : 0);
        result = 31 * result + (getLocation_id() != null ? getLocation_id().hashCode() : 0);
        result = 31 * result + (getComment() != null ? getComment().hashCode() : 0);
        result = 31 * result + getEvent_id();
        result = 31 * result + (getModified() != null ? getModified().hashCode() : 0);
        return result;
    }
}
