package com.innopolis.maps.innomaps.database;


public final class TableFields {

    public static final String ID = "id"; //ID string without underscore

    /* Events table */
    public static final String SUMMARY = "summary"; //just a title

    /* Event_type table */
    public static final String FLOOR = "floor";
    public static final String ROOM = "room";

    /* POI (points of interest) table
    * same as above plus below */

    /*for Json*/
    public static final String EMPTY = "";


    /*table names*/
    public static final String POI = "poi";


    /*misc*/
    public static final String EVENT = "event";
    public static final String NULL_STRING = "null";

    public static final String WC_CAPITAL = "WC";
    public static final String FOOD_CAPITAL = "Food";
    public static final String EVENTS_CAPITAL = "Events";
    public static final String ALL_CAPITAL = "All";
    public static final String OTHER_CAPITAL = "Other";


    public static final int ALL_FILTER = 2;
    public static final int WC_FILTER = 0;
    public static final int FOOD_FILTER = 1;
    public static final int EVENTS_FILTER = 3;
    public static final int OTHER_FILTER = 4;


}
