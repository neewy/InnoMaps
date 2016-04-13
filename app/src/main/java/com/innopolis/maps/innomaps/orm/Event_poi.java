package com.innopolis.maps.innomaps.orm;

import com.orm.SugarRecord;

/**
 * Created by User on 13.04.2016.
 */
public class Event_poi extends SugarRecord {

    int id;
    int eventID;
    int poiId;

    public Event_poi() {
    }

    public Event_poi(int id, int eventID, int poiId) {
        this.id = id;
        this.eventID = eventID;
        this.poiId = poiId;
    }
}
