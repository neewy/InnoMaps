package com.innopolis.maps.innomaps.network.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.network.Constants;
import com.innopolis.maps.innomaps.network.NetworkController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

/**
 * Created by alnedorezov on 7/4/16.
 */
public class GetCoordinateByIdTask extends AsyncTask<String, Void, Coordinate> {
    @Override
    protected Coordinate doInBackground(String... params) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String urlString = getURL(params[0]);
            String response;
            try {
                response = NetworkController.establishGetConnection(urlString);
                mapper.setDateFormat(Constants.serverDateFormat);
                return mapper.readValue(response, Coordinate.class);
            } catch (UnsupportedEncodingException | IllegalStateException | NullPointerException e) {
                Log.e(Constants.LOG, e.getMessage(), e.fillInStackTrace());
            }
        } catch (IOException e) {
            Log.e(Constants.LOG, e.getMessage());
        }
        return null;
    }

    // parameter id
    protected String getURL(String id) {
        return Constants.CONNECTION_PROTOCOL + Constants.colon_and_two_slashes + Constants.IP + Constants.colon +
                Constants.PORT + Constants.slash_resources_slash + Constants.coordinate + Constants.question_mark_id_equals + id;
    }
}