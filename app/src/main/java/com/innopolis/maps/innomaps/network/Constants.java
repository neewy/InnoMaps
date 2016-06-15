package com.innopolis.maps.innomaps.network;


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
    char slash = '/';
    char question_mark = '?';
    String slash_resources_slash = "/resources/";
    String latitude = "latitude";
    String longitude = "longitude";
    String floor = "floor";
    String closestPointFromGraph = "closestPointFromGraph";
    String response_null = "Response equals null. Check if the server is running.";

    String USER_AGENT = "User-Agent";
    String CONNECTION = "Connection";
    String POST = "POST";
    String CONTENT_TYPE = "Content-Type";
    String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";

    String shortest_path_url = "%1$s://%2$s:%3$s/resources/shortestPath";

    String VERTEX_ONE_LAT = "vertexOneLatitude";
    String VERTEX_ONE_LNG = "vertexOneLongitude";
    String VERTEX_ONE_FLR = "vertexOneFloor";
    String VERTEX_TWO_LAT = "vertexTwoLatitude";
    String VERTEX_TWO_LNG = "vertexTwoLongitude";

}
