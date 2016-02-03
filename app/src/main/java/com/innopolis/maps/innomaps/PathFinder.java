package com.innopolis.maps.innomaps;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Class to find and show route between 2 points on a map.
 * Created by luckychess on 1/31/16.
 */
public class PathFinder {
    LatLng from;
    LatLng to;

    public PathFinder(LatLng from, LatLng to) {
        this.from = from;
        this.to = to;
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // TODO: check about Internet premission
            // TODO: use existing class (Events.java)
            final URL url;
            HttpURLConnection urlConnection;
            BufferedReader reader;
            String result = "";

            try {
                url = new URL("https://maps.googleapis.com/maps/api/directions/json?origin="
                        + from.latitude + ","
                        + from.longitude + "&destination="
                        + to.latitude + ","
                        + to.longitude + "&key=" + "AIzaSyDli8qeotu4TGaEs5VKSWy15CDyl4cgZ-o");

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                result = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            Log.i("luckychess", strJson);
        }
    }

    public void findPath() {
        new ParseTask().execute();
    }
}
