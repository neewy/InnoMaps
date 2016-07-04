package com.innopolis.maps.innomaps.network;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.innopolis.maps.innomaps.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.innopolis.maps.innomaps.network.Constants.CONNECTION;
import static com.innopolis.maps.innomaps.network.Constants.USER_AGENT;

public class InternetAccessChecker {

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL(context.getString(R.string.google_url));
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty(USER_AGENT, context.getString(R.string.test));
                urlc.setRequestProperty(CONNECTION, context.getString(R.string.close));
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                Log.i(context.getString(R.string.warning), context.getString(R.string.connection_error), e);
                return false;
            }
        }

        return false;

    }

    public static void isNetworkAvailable(final Handler handler, final int timeout, final Context context) {
        // ask fo message '0' (not connected) or '1' (connected) on 'handler'
        // the answer must be send before before within the 'timeout' (in milliseconds)
        final int connected = 1;
        final int disconnected = 0;

        new Thread() {
            private boolean responded = false;

            @Override
            public void run() {
                // set 'responded' to TRUE if is able to connect with google mobile (responds fast)
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            responded = isConnected(context);
                        } catch (Exception e) {
                        }
                    }
                }.start();

                try {
                    int waited = 0;
                    while (!responded && (waited < timeout)) {
                        sleep(100);
                        if (!responded) {
                            waited += 100;
                        }
                    }
                } catch (InterruptedException e) {
                } // do nothing
                finally {
                    if (!responded) {
                        handler.sendEmptyMessage(disconnected);
                    } else {
                        handler.sendEmptyMessage(connected);
                    }
                }
            }
        }.start();
    }
}
