package com.innopolis.maps.innomaps.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.innopolis.maps.innomaps.utils.Utils;

import static com.innopolis.maps.innomaps.database.TableFields.NULL;
import static com.innopolis.maps.innomaps.database.TableFields.POI;

/**
 * Created by neewy on 21.04.16.
 */
public class XMLParseTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private SQLiteDatabase database;
    private SharedPreferences sPref;
    private final static String DELETE = "delete from ";

    public XMLParseTask(Context context, SQLiteDatabase database, SharedPreferences sPref) {
        this.context = context;
        this.database = database;
        this.sPref = sPref;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String md5 = Utils.doGetRequest(Utils.restServerUrl + "/innomaps/graphml/md5?floor=9");
        String savedText = sPref.getString("XmlUpdated", NULL);
        if (!savedText.equals(md5)) {
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString("XmlUpdated", md5);
            database.execSQL(DELETE + POI);
            ed.apply();
            return true;
        } else {
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean updated) {
        if (updated) {
            HandleXML handleXML = new HandleXML(context);
            DBHelper.insertPois(database, handleXML.parseXml());
        }
        new JsonParseTask(database, sPref).execute();
    }



}
