package com.innopolis.maps.innomaps.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Some helper methods which may be used from any place
 */
public class Utils {

    private final static long MILLISECONDS_PER_8DAY = 1000L * 60 * 60 * 24 * 8;
    //considering 8 days to be most actual date boundaries

    public static SimpleDateFormat hoursMinutes = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat commonTime = new SimpleDateFormat("dd/MM/yy HH:mm");
    public static SimpleDateFormat googleTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    public static PrettyTime prettyTime = new PrettyTime(new Locale("en"));

    private static final String TELEGRAM_LOGIN = "(Contact: @)((?:[a-zA-Z0-9_]+))";
    private static final String TELEGRAM_GROUP = "((Group link: https://)(telegram.me\\/)" +
            "(.*)(?:\\/[\\w\\.\\-]+)+)";    // Unix Path

    public static Pattern telLogPattern = Pattern.compile(TELEGRAM_LOGIN, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    public static Pattern telGroupPattern = Pattern.compile(TELEGRAM_GROUP, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    public static final String restServerUrl = "http://10.240.19.135:8000";

    public static String GOOGLE_MAP_API_FIRST_PART = "https://www.googleapis.com/calendar/v3/calendars/hvtusnfmqbg9u2p5rnc1rvhdfg@group.calendar.google.com/events?timeMin=";
    public static String GOOGLE_MAP_API_SECOND_PART = "T10%3A00%3A00-07%3A00&orderby=updated&sortorder=descending&futureevents=true&alt=json&key=AIzaSyDli8qeotu4TGaEs5VKSWy15CDyl4cgZ-o";
    public static String GOOGLE_MAP_API;
    public static String NULL = "null";

    public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    public static Date date = new Date();

    public static String getGoogleApi() {
        shiftDate(date);
        return GOOGLE_MAP_API = doGetRequest(GOOGLE_MAP_API_FIRST_PART
                + dateFormat.format(date)
                + GOOGLE_MAP_API_SECOND_PART);
    }

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

    public static String[] clean(final String[] v) {
        int r, w, n = r = w = v.length;
        while (r > 0) {
            final String s = v[--r];
            if (!s.equals(NULL)) {
                v[--w] = s;
            }
        }
        final String[] c = new String[n -= w];
        System.arraycopy(v, w, c, 0, n);
        return c;
    }

    public static boolean isAppAvailable(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
