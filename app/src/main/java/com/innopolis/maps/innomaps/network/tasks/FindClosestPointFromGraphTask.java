package com.innopolis.maps.innomaps.network.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innopolis.maps.innomaps.network.Constants;
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.network.clientServerCommunicationClasses.ClosestCoordinateWithDistance;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by alnedorezov on 6/15/16.
 */
public class FindClosestPointFromGraphTask extends AsyncTask<String, Void, ClosestCoordinateWithDistance> {
    @Override
    protected ClosestCoordinateWithDistance doInBackground(String... params) {
        try {
            String urlString = getURL(params[0], params[1], params[2]);
            return deserializeClosestCoordinateWithDistance(urlString);
        } catch (IOException e) {
            Log.e(Constants.LOG, e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }

    private ClosestCoordinateWithDistance deserializeClosestCoordinateWithDistance(String urlString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String response;
        try {
            response = NetworkController.establishGetConnection(urlString);
            return mapper.readValue(response, ClosestCoordinateWithDistance.class);
        } catch (UnsupportedEncodingException | IllegalStateException | NullPointerException e) {
            Log.e(Constants.LOG, e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }

    //parameter id
    protected String getURL(String latitude, String longitude, String floor) {
        return Constants.CONNECTION_PROTOCOL + Constants.COLON_AND_TWO_SLASHES + Constants.IP + Constants.COLON + Constants.PORT +
                Constants.SLASH_RESOURCES_SLASH + Constants.CLOSEST_POINT_FROM_GRAPH + Constants.QUESTION_MARK +
                Constants.LATITUDE + Constants.PARAMETER_EQUALS_CHAR + latitude +
                Constants.PARAMETER_DELIMITER + Constants.LONGITUDE + Constants.PARAMETER_EQUALS_CHAR + longitude +
                Constants.PARAMETER_DELIMITER + Constants.FLOOR + Constants.PARAMETER_EQUALS_CHAR + floor;
    }
}