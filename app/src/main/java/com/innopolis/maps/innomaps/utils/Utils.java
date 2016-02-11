package com.innopolis.maps.innomaps.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by Nikolay on 04.02.2016.
 * Some helper methods which my be used from any place
 */
public class Utils {

    private final static long MILLISECONDS_PER_8DAY = 1000L * 60 * 60 * 24 * 8; //considering 8 days to be most actual date boundaries

    //shift the given Date by exactly 8 days.
    public static void shiftDate(Date d) {
        long time = d.getTime();
        time -= MILLISECONDS_PER_8DAY;
        d.setTime(time);
    }

    //check whether the network is available
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String doGetRequest(String urlString) {

        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        }

        HttpURLConnection urlConnection;
        BufferedReader reader;
        String result = "";

        try {
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
}
