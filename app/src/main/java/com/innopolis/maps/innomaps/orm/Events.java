package com.innopolis.maps.innomaps.orm;

import com.orm.SugarRecord;



public class Events extends SugarRecord {

    int id;
    String summary;
    String htmlLink;
    String start;
    String end;
    int eventID;
    String checked;

    public Events(){
    }

    public  Events(int id, String summary, String htmlLink, String start, String end, int eventID, String checked){
        this.id = id;
        this.summary = summary;
        this.htmlLink = htmlLink;
        this.start = start;
        this.end = end;
        this.eventID = eventID;
        this.checked = checked;

    }
}
