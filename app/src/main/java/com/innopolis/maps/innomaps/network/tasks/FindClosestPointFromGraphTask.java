package com.innopolis.maps.innomaps.network.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innopolis.maps.innomaps.maps.LatLngFlr;
import com.innopolis.maps.innomaps.network.Constants;
import com.innopolis.maps.innomaps.network.NetworkController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by alnedorezov on 6/15/16.
 */
public class FindClosestPointFromGraphTask extends AsyncTask<String, Void, LatLngFlr> {
    @Override
    protected LatLngFlr doInBackground(String... params) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String urlString = getURL(params[0], params[1], params[2]);
            String response;
            try {
                response = NetworkController.establishGetConnection(urlString);
                return mapper.readValue(response, LatLngFlr.class);
            } catch (UnsupportedEncodingException | IllegalStateException | NullPointerException e) {
                Log.e(Constants.LOG, e.getMessage(), e.fillInStackTrace());
            }
        } catch (IOException e) {
            Log.e(Constants.LOG, e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }

    //parameter id
    protected String getURL(String latitude, String longitude, String floor) {
        return Constants.CONNECTION_PROTOCOL + Constants.colon_and_two_slashes + Constants.IP + Constants.colon + Constants.PORT +
                Constants.slash_resources_slash + Constants.closestPointFromGraph + Constants.question_mark +
                Constants.latitude + Constants.PARAMETER_EQUALS_CHAR + latitude +
                Constants.PARAMETER_DELIMITER + Constants.longitude + Constants.PARAMETER_EQUALS_CHAR + longitude +
                Constants.PARAMETER_DELIMITER + Constants.floor + Constants.PARAMETER_EQUALS_CHAR + floor;
    }
}