package com.innopolis.maps.innomaps.network;


import java.text.SimpleDateFormat;
import java.util.Locale;

public interface Constants {
    String LOG = "Exception";
    String CONNECTION_PROTOCOL = "https";
    String IP = "188.130.155.224";
    String PORT = "9000";
    char PARAMETER_DELIMITER = '&';
    char PARAMETER_EQUALS_CHAR = '=';
    String ENCODING = "UTF-8";
    String COLON_AND_TWO_SLASHES = "://";
    char COLON = ':';
    char QUESTION_MARK = '?';
    String SLASH_RESOURCES_SLASH = "/resources/";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String FLOOR = "floor";
    String CLOSEST_POINT_FROM_GRAPH = "closestPointFromGraph";
    String FLOOR_CALCULATION_ERROR = "The error arises on calculation floor for the following coordinates:";

    String USER_AGENT = "User-Agent";
    String CONNECTION = "Connection";
    String POST = "POST";
    String CONTENT_TYPE = "Content-Type";
    String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";

    String QUESTION_MARK_ID_EQUALS = "?id=";
    String COORDINATE = "coordinate";
    String TYPE = "type";
    String EDGE = "edge";
    String ROOM = "room";
    String STREET = "street";
    String BUILDING = "building";
    String PHOTO = "photo";
    String CREATOR = "creator";
    String EVENT = "event";
    char S_LOWERCASE = 's';
    String CREATED_AFTER_DATE = "createdAfterDate";
    String SHORTEST_PATH_URL = "%1$s://%2$s:%3$s/resources/shortestPath";

    char SPACE = ' ';
    String ULR_ENCODED_SPACE = "%20";
    String URL_ENCODED_COLON = "%3A";

    String VERTEX_ONE_LAT = "vertexOneLatitude";
    String VERTEX_ONE_LNG = "vertexOneLongitude";
    String VERTEX_ONE_FLR = "vertexOneFloor";
    String VERTEX_TWO_LAT = "vertexTwoLatitude";
    String VERTEX_TWO_LNG = "vertexTwoLongitude";
    String VERTEX_TWO_FLR = "vertexTwoFloor";

    SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss.S", Locale.ENGLISH);
}
