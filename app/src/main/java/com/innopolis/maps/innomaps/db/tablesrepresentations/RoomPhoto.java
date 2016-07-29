package com.innopolis.maps.innomaps.db.tablesrepresentations;

import com.innopolis.maps.innomaps.network.Constants;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by alnedorezov on 7/6/16.
 */
@DatabaseTable(tableName = "Room_photos")
public class RoomPhoto {
    @DatabaseField(uniqueCombo = true)
    private int room_id;
    @DatabaseField(uniqueCombo = true)
    private int photo_id;
    @DatabaseField
    private Date created = null;

    public RoomPhoto(int room_id, int photo_id, String createdStr) throws ParseException {
        this.room_id = room_id;
        this.photo_id = photo_id;
        this.created = Constants.serverDateFormat.parse(createdStr);
    }

    public RoomPhoto(int room_id, int photo_id, Date created) {
        this.room_id = room_id;
        this.photo_id = photo_id;
        this.created = created;
    }

    public RoomPhoto() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public int getRoom_id() {
        return room_id;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public String getCreated() {
        return Constants.serverDateFormat.format(created);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RoomPhoto))
            return false;

        RoomPhoto roomPhoto = (RoomPhoto) o;

        if (getRoom_id() != roomPhoto.getRoom_id())
            return false;
        if (getPhoto_id() != roomPhoto.getPhoto_id())
            return false;
        return getCreated() != null ? getCreated().equals(roomPhoto.getCreated()) : roomPhoto.getCreated() == null;

    }

    @Override
    public int hashCode() {
        int result = getRoom_id();
        result = 31 * result + getPhoto_id();
        result = 31 * result + (getCreated() != null ? getCreated().hashCode() : 0);
        return result;
    }
}
