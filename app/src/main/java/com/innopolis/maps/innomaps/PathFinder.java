package com.innopolis.maps.innomaps;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class to find and show route between 2 points on a map.
 * Created by luckychess on 1/31/16.
 */
public class PathFinder {
    LatLng from;
    LatLng to;
    MapsActivity activity;

    public PathFinder(MapsActivity activity, LatLng from, LatLng to) {
        this.activity = activity;
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
                        + to.longitude);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

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
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            activity.drawPath(path);
        }
    }

    public void findPath() {
        new ParseTask().execute();
    }
}
