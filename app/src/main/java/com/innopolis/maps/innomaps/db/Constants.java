package com.innopolis.maps.innomaps.db;

/**
 * Created by alnedorezov on 7/7/16.
 */
public final class Constants {
    public static final int DATABASE_VERSION = 10;
    public static final String DATABASE_NAME = "innomapsMobile" + DATABASE_VERSION + ".db";

    public static final String DAO_ERROR = "DAO_ERROR";
    public static final String SQL_EXCEPTION_IN = "SQL Exception in";
    public static final char SPACE = ' ';
    public static final char UNDERSCORE = '_';

    public static final String DB_HELPER = "DB_HELPER";
    public static final String ERROR = "ERROR";
    public static final String ON_CREATE = "onCreate";
    public static final String ON_UPGRADE = "onUpgrade";
    public static final String CANNOT_CREATE_DATABASE = "Can't create database";
    public static final String CANNOT_DROP_DATABASES = "Can't drop databases";

    public static final String BUILDING_ID = "building_id";
    public static final String PHOTO_ID = "photo_id";
    public static final String COORDINATE_ID = "coordinate_id";
    public static final String EVENT_ID = "event_id";
    public static final String EVENT_CREATOR_ID = "event_creator_id";
    public static final String ROOM_ID = "room_id";
    public static final String CREATED = "created";
    public static final String TYPE_ID = "type_id";
    public static final String ID = "id";
    public static final String END_DATETIME = "end_datetime";
    public static final String LOCATION_ID = "location_id";

    public static final String SYNC = "sync";
    public static final String LAST = "last";
    public static final String SYNC_DATE = "SyncDate";
    public static final String TYPES = "Types";
    public static final String MAP_UNITS = "MapUnits";
    public static final String EVENTS = "Events";
    public static final String ASSIGNMENTS = "Assignments";
    public static final String GENERAL = "GENERAL";

    public static final String LOG = "Exception";

    public static final String DEFAULT_SYNC_DATE = "2015-07-26 15:00:00.0";

    public static final String SYNC_ERROR = "SYNC_ERROR";

    public static final int INTERNET_CHECK_TIMEOUT = 2000;

    public static final int SYNCHRONIZATION_INTERVAL = 1000 * 60 * 2;

    public static final String SYNC_FINISHED_ON = "Synchronization finished on: ";

    // For SearchableItem
    public static final String ROOM_STARTING_FROM_CAPITAL_LETTER = "Room";
    public static final String FLOOR_LOWERCASE = "floor";
    public static final String EMPTY_STRING = "";

    // For BottomSheet
    public static final String NEW_LINE = "\n";
    public static final char AT_SIGN = '@';
    public static final String GROUP_LINK = "Group link: ";
    public static final String CONTACT = "Contact: ";

    // Utility classes, which are a collection of static members, are not meant to be instantiated
    private Constants() {
        // Even abstract utility classes, which can be extended, should not have public constructors.
        // Java adds an implicit public constructor to every class which does not define at least one explicitly.
        // Hence, at least one non-public constructor should be defined
    }
}
