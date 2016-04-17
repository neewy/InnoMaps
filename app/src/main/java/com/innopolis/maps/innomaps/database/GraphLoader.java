package com.innopolis.maps.innomaps.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Downloads graph from server (if necessary)
 * Created by luckychess on 4/17/16.
 */
public class GraphLoader extends AsyncTask<Void, Void, String> {
    private Context context;

    public GraphLoader(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        return Utils.doGetRequest(Utils.restServerUrl + "/innomaps/graphml/md5?floor=9");
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("md5", "server md5: " + result);
        String filename = "9.md5";
        String storedMD5 = "";
        FileInputStream inputStream;
        try {
            inputStream = context.openFileInput(filename);
            storedMD5 = IOUtils.toString(inputStream);
            inputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("md5", "stored md5: " + storedMD5);
        if (storedMD5.equals(result)) return;

        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(result.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new DownloadGraph().execute();
    }

    private class DownloadGraph extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return Utils.doGetRequest(Utils.restServerUrl + "/innomaps/graphml/loadmap?floor=9");
        }

        @Override
        protected void onPostExecute(String result) {
            String filename = "9.xml";
            FileOutputStream outputStream;

            try {
                outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(result.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
