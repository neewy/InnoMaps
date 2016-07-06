package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/6/16.
 */
@DatabaseTable(tableName = "Event_creators")
public class EventCreator {
    @DatabaseField(generatedId = true, unique = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField(unique = true)
    private String email;
    @DatabaseField
    private String telegram_username;
    @DatabaseField
    private Date modified = null;

    public EventCreator(int id, String name, String email, String telegram_username, String modifiedStr) throws ParseException {
        this.id = id;
        this.name = name;
        this.email = email;
        this.telegram_username = telegram_username;
        this.modified = Constants.serverDateFormat.parse(modifiedStr);
    }

    public EventCreator(int id, String name, String email, String telegram_username, Date modified) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.telegram_username = telegram_username;
        this.modified = modified;
    }

    // For deserialization with Jackson
    public EventCreator() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTelegram_username() {
        return telegram_username;
    }

    public String getModified() {
        return Constants.serverDateFormat.format(modified);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EventCreator))
            return false;

        EventCreator that = (EventCreator) o;

        if (getId() != that.getId())
            return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null)
            return false;
        if (getTelegram_username() != null ? !getTelegram_username().equals(that.getTelegram_username()) : that.getTelegram_username() != null)
            return false;
        return getModified() != null ? getModified().equals(that.getModified()) : that.getModified() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getTelegram_username() != null ? getTelegram_username().hashCode() : 0);
        result = 31 * result + (getModified() != null ? getModified().hashCode() : 0);
        return result;
    }
}
