package com.innopolis.maps.innomaps.network;


import java.text.SimpleDateFormat;
import java.util.Locale;

public final class Constants {
    public static final String LOG = "Exception";
    public static final String CONNECTION_PROTOCOL = "https";
    public static final String IP = "188.130.155.224";
    public static final String PORT = "9000";
    public static final char PARAMETER_DELIMITER = '&';
    public static final char PARAMETER_EQUALS_CHAR = '=';
    public static final String ENCODING = "UTF-8";
    public static final String COLON_AND_TWO_SLASHES = "://";
    public static final char COLON = ':';
    public static final char QUESTION_MARK = '?';
    public static final String SLASH_RESOURCES_SLASH = "/resources/";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String FLOOR = "floor";
    public static final String CLOSEST_POINT_FROM_GRAPH = "closestPointFromGraph";
    public static final String FLOOR_CALCULATION_ERROR = "The error arises on calculation floor for the following coordinates:";

    public static final String USER_AGENT = "User-Agent";
    public static final String CONNECTION = "Connection";
    public static final String POST = "POST";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";

    public static final String QUESTION_MARK_ID_EQUALS = "?id=";
    public static final String COORDINATE = "coordinate";
    public static final String TYPE = "type";
    public static final String EDGE = "edge";
    public static final String ROOM = "room";
    public static final String STREET = "street";
    public static final String BUILDING = "building";
    public static final String PHOTO = "photo";
    public static final String CREATOR = "creator";
    public static final String EVENT = "event";
    public static final String SCHEDULE = "schedule";
    public static final String FLOOR_OVERLAY = "flooroverlay";
    public static final String APPOINTMENT = "appointment";
    public static final char S_LOWERCASE = 's';
    public static final String CREATED_AFTER_DATE = "createdAfterDate";
    public static final String SHORTEST_PATH_URL = "%1$s://%2$s:%3$s/resources/shortestPath";

    public static final char SPACE = ' ';
    public static final String ULR_ENCODED_SPACE = "%20";
    public static final String URL_ENCODED_COLON = "%3A";

    public static final String VERTEX_ONE_LAT = "vertexOneLatitude";
    public static final String VERTEX_ONE_LNG = "vertexOneLongitude";
    public static final String VERTEX_ONE_FLR = "vertexOneFloor";
    public static final String VERTEX_TWO_LAT = "vertexTwoLatitude";
    public static final String VERTEX_TWO_LNG = "vertexTwoLongitude";
    public static final String VERTEX_TWO_FLR = "vertexTwoFloor";

    public static final SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss.S", Locale.ENGLISH);

    // Utility classes, which are a collection of static members, are not meant to be instantiated
    private Constants() {
        // Even abstract utility classes, which can be extended, should not have public constructors.
        // Java adds an implicit public constructor to every class which does not define at least one explicitly.
        // Hence, at least one non-public constructor should be defined
    }
}
