package com.innopolis.maps.innomaps.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.innopolis.maps.innomaps.database.DBHelper;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
            if (!s.equals("null")) {
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

    public static List<String> getEventNames(SQLiteDatabase database) {
        List<String> namesRes = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.TABLE2, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                namesRes.add(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_SUMMARY)));
            } while (cursor.moveToNext());
        }
        return namesRes;
    }
}
