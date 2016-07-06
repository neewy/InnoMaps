package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/6/16.
 */
@DatabaseTable(tableName = "Event_creators_appointments")
public class EventCreatorAppointment {
    @DatabaseField(uniqueCombo = true)
    private int event_id;
    @DatabaseField(uniqueCombo = true)
    private int event_creator_id;
    @DatabaseField
    private Date created = null;

    public EventCreatorAppointment(int event_id, int event_creator_id, String createdStr) throws ParseException {
        this.event_id = event_id;
        this.event_creator_id = event_creator_id;
        this.created = Constants.serverDateFormat.parse(createdStr);
    }

    public EventCreatorAppointment(int event_id, int event_creator_id, Date created) {
        this.event_id = event_id;
        this.event_creator_id = event_creator_id;
        this.created = created;
    }

    public EventCreatorAppointment() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getEvent_id() {
        return event_id;
    }

    public int getEvent_creator_id() {
        return event_creator_id;
    }

    public String getCreated() {
        return Constants.serverDateFormat.format(created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EventCreatorAppointment))
            return false;

        EventCreatorAppointment that = (EventCreatorAppointment) o;

        if (getEvent_id() != that.getEvent_id())
            return false;
        if (getEvent_creator_id() != that.getEvent_creator_id())
            return false;
        return getCreated() != null ? getCreated().equals(that.getCreated()) : that.getCreated() == null;

    }

    @Override
    public int hashCode() {
        int result = getEvent_id();
        result = 31 * result + getEvent_creator_id();
        result = 31 * result + (getCreated() != null ? getCreated().hashCode() : 0);
        return result;
    }
}