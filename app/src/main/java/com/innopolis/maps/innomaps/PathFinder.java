package com.innopolis.maps.innomaps;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to find and show route between 2 points on a map.
 * Created by luckychess on 1/31/16.
 */
public class PathFinder {
    private LatLng from;
    private LatLng to;
    private GoogleMap map;
    private Polyline currentPath;

    public PathFinder(GoogleMap map, LatLng from, LatLng to) {
        this.map = map;
        this.from = from;
        this.to = to;
    }

    public void setLatLng(LatLng from, LatLng to) {
        this.from = from;
        this.to = to;
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
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
            drawPath(path);
        }
    }

    public void findPath() {
        if (currentPath != null) {
            currentPath.remove();
        }
        new ParseTask().execute();
    }

    /**
     * Taken from http://stackoverflow.com/questions/14702621
     *
     * @param path: json answer returned by the direction API
     */
    public void drawPath(String path) {
        try {
            final JSONObject json = new JSONObject(path);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            currentPath = map.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(12)
                            .color(Color.parseColor("#05b1fb"))     // Google maps blue color
                            .geodesic(true)
            );
        } catch (JSONException e) {
            Log.e("Exception in drawPath", e.toString());
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
