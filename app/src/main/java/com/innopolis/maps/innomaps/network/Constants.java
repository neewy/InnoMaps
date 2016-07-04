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
    String colon_and_two_slashes = "://";
    char colon = ':';
    char question_mark = '?';
    String slash_resources_slash = "/resources/";
    String latitude = "latitude";
    String longitude = "longitude";
    String floor = "floor";
    String closestPointFromGraph = "closestPointFromGraph";
    String floor_calculation_error = "The error arises on calculation floor for the following coordinates:";

    String USER_AGENT = "User-Agent";
    String CONNECTION = "Connection";
    String POST = "POST";
    String CONTENT_TYPE = "Content-Type";
    String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";

    String question_mark_id_equals = "?id=";
    String coordinate = "coordinate";
    String shortest_path_url = "%1$s://%2$s:%3$s/resources/shortestPath";

    String VERTEX_ONE_LAT = "vertexOneLatitude";
    String VERTEX_ONE_LNG = "vertexOneLongitude";
    String VERTEX_ONE_FLR = "vertexOneFloor";
    String VERTEX_TWO_LAT = "vertexTwoLatitude";
    String VERTEX_TWO_LNG = "vertexTwoLongitude";

    SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd H:mm:ss.S", Locale.ENGLISH);
}
