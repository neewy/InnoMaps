package com.innopolis.maps.innomaps.orm;

import com.orm.SugarRecord;


public class Event_type extends SugarRecord {
    int id;
    String summary;
    String description;
    String creatorName;
    String creatorEmail;

    public Event_type() {
    }

    public Event_type(int id, String summary, String description, String creatorName, String creatorEmail) {
        this.id = id;
        this.summary = summary;
        this.description = description;
        this.creatorName = creatorName;
        this.creatorEmail = creatorEmail;
    }
}
