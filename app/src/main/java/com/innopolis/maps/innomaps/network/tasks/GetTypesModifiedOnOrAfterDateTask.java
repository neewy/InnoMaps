package com.innopolis.maps.innomaps.network.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innopolis.maps.innomaps.network.Constants;
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.TypesSync;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by alnedorezov on 7/11/16.
 */
public class GetTypesModifiedOnOrAfterDateTask extends AsyncTask<String, Void, TypesSync> {
    @Override
    protected TypesSync doInBackground(String... params) {
        try {
            String urlString = getURL(params[0]);
            return deserializeCoordinate(urlString);
        } catch (IOException e) {
            Log.e(Constants.LOG, e.getMessage());
        }
        return null;
    }

    private TypesSync deserializeCoordinate(String urlString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String response;
        try {
            response = NetworkController.establishGetConnection(urlString);
            mapper.setDateFormat(Constants.serverDateFormat);
            return mapper.readValue(response, TypesSync.class);
        } catch (UnsupportedEncodingException | IllegalStateException | NullPointerException e) {
            Log.e(Constants.LOG, e.getMessage(), e.fillInStackTrace());
        }
        return null;
    }

    protected String getURL(String date) {
        return Constants.CONNECTION_PROTOCOL + Constants.COLON_AND_TWO_SLASHES + Constants.IP + Constants.COLON + Constants.PORT +
                Constants.SLASH_RESOURCES_SLASH + Constants.SYNC + Constants.SLASH + Constants.TYPE + Constants.S_LOWERCASE +
                Constants.QUESTION_MARK + Constants.DATE + Constants.PARAMETER_EQUALS_CHAR + date;
    }
}