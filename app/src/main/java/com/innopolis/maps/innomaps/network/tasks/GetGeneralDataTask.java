package com.innopolis.maps.innomaps.network.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innopolis.maps.innomaps.network.Constants;
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.GeneralSync;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by alnedorezov on 7/14/16.
 */
public class GetGeneralDataTask extends AsyncTask<String, Void, GeneralSync> {
    @Override
    protected GeneralSync doInBackground(String... params) {
        try {
            String urlString = getURL();
            return deserializeCoordinate(urlString);
        } catch (IOException e) {
            Log.e(Constants.LOG, e.getMessage());
        }
        return null;
    }

    private GeneralSync deserializeCoordinate(String urlString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String response;
        try {
            response = NetworkController.establishGetConnection(urlString);
            mapper.setDateFormat(Constants.serverDateFormat);
            return mapper.readValue(response, GeneralSync.class);
        } catch (UnsupportedEncodingException | IllegalStateException | NullPointerException e) {
            Log.e(Constants.LOG, e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }

    protected String getURL() {
        return Constants.CONNECTION_PROTOCOL + Constants.COLON_AND_TWO_SLASHES + Constants.IP + Constants.COLON + Constants.PORT +
                Constants.SLASH_RESOURCES_SLASH + Constants.SYNC + Constants.SLASH + Constants.GENERAL;
    }
}