package com.innopolis.maps.innomaps.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.innopolis.maps.innomaps.utils.Utils;

import java.io.FileOutputStream;

import static com.innopolis.maps.innomaps.database.TableFields.NULL;

/**
 * Downloads graph from server (if necessary)
 * Created by luckychess on 4/17/16.
 */
public class GraphLoader extends AsyncTask<Void, Void, String> {
    private Context context;
    private SQLiteDatabase database;
    private SharedPreferences sPref;

    public GraphLoader(Context context, SQLiteDatabase database, SharedPreferences sPref) {
        this.context = context;
        this.database = database;
        this.sPref = sPref;
    }

    @Override
    protected String doInBackground(Void... params) {
        return Utils.doGetRequest(Utils.restServerUrl + "/innomaps/graphml/md5?floor=9");
    }

    @Override
    protected void onPostExecute(String result) {
        if (graphUpdated(result)) {
            new DownloadGraph().execute();
        } else {
            new JsonParseTask(database, sPref).execute();
        }
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

            new XMLParseTask(context, database, sPref).execute();
        }
    }

    protected boolean graphUpdated(String hashKey) {
        String savedText = sPref.getString("graph_md5", NULL);
        if (savedText.equals(hashKey)) {
            return false;
        } else {
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("graph_md5", hashKey);
            ed.apply();
            return true;
        }
    }
}
