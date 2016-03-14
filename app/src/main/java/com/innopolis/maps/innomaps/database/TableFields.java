package com.innopolis.maps.innomaps.database;


public final class TableFields {

    public static final String ID = "id"; //Primary key (as stated in Android guidelines)

    /* Events table */
    public static final String SUMMARY = "summary"; //just a title
    public static final String LINK = "htmlLink"; //calendar link
    public static final String START = "start"; //start date
    public static final String END = "end"; //end date
    public static final String EVENT_ID = "eventID"; //unique field
    public static final String FAV = "checked"; //is the event favourite

    /* Event_type table */
    public static final String DESCRIPTION = "description"; //detailed description
    public static final String CREATOR_NAME = "creator_name"; //the person, who created the event
    public static final String CREATOR_EMAIL = "creator_email"; //his or her gmail

    /* Location table */
    public static final String BUILDING = "building";
    public static final String FLOOR = "floor";
    public static final String ROOM = "room";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    /* POI (points of interest) table
    * same as above plus below */
    public static final String POI_NAME = "name";
    public static final String TYPE = "type";
    public static final String ATTR = "attr";

    /*for Json*/
    public static final String NULL = "";
    public static final String DATETIME = "dateTime";
    public static final String CREATOR = "creator";
    public static final String DISPLAY_NAME = "displayName";
    public static final String EMAIL = "email";
    public static final String RECURRENCE = "recurrence";
    public static final String RRULE = "RRULE:";
    public static final String LAST_UPDATE = "lastUpdate";
    public static final String HASH = "hash";



    /*table names*/
    public static final String EVENTS = "events";
    public static final String EVENT_TYPE = "event_type";
    public static final String LOCATION = "location";
    public static final String EVENT_POI = "event_poi";
    public static final String POI = "poi";

}
