package com.innopolis.maps.innomaps.network.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingFloorOverlay;
import com.innopolis.maps.innomaps.network.Constants;
import com.innopolis.maps.innomaps.network.NetworkController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by alnedorezov on 7/6/16.
 */
public class GetBuildingFloorOverlayByIdTask extends AsyncTask<String, Void, BuildingFloorOverlay> {
    @Override
    protected BuildingFloorOverlay doInBackground(String... params) {
        try {
            String urlString = getURL(params[0]);
            return deserializeCoordinate(urlString);
        } catch (IOException e) {
            Log.e(Constants.LOG, e.getMessage());
        }
        return null;
    }

    private BuildingFloorOverlay deserializeCoordinate(String urlString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String response;
        try {
            response = NetworkController.establishGetConnection(urlString);
            mapper.setDateFormat(Constants.serverDateFormat);
            return mapper.readValue(response, BuildingFloorOverlay.class);
        } catch (UnsupportedEncodingException | IllegalStateException | NullPointerException e) {
            Log.e(Constants.LOG, e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }

    // parameter id
    protected String getURL(String id) {
        return Constants.CONNECTION_PROTOCOL + Constants.COLON_AND_TWO_SLASHES + Constants.IP + Constants.COLON + Constants.PORT +
                Constants.SLASH_RESOURCES_SLASH + Constants.BUILDING + Constants.FLOOR + Constants.OVERLAY + Constants.QUESTION_MARK_ID_EQUALS + id;
    }
}