package com.innopolis.maps.innomaps;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Date;

/**
 * Created by Nikolay on 04.02.2016.
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
    protected static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
