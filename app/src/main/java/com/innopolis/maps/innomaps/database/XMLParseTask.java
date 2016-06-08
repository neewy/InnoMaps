package com.innopolis.maps.innomaps.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.utils.Utils;

import static com.innopolis.maps.innomaps.database.TableFields.EMPTY;
import static com.innopolis.maps.innomaps.database.TableFields.POI;

public class XMLParseTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private SQLiteDatabase database;
    private SharedPreferences sPref;

    public XMLParseTask(Context context, SQLiteDatabase database, SharedPreferences sPref) {
        this.context = context;
        this.database = database;
        this.sPref = sPref;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String md5 = Utils.doGetRequest(Utils.restServerUrl + context.getString(R.string.graph_md5_url));
        String savedText = sPref.getString(context.getString(R.string.XmlUpdated), EMPTY);
        if (!savedText.equals(md5)) {
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(context.getString(R.string.XmlUpdated), md5);
            database.execSQL(SQLQueries.delete(POI));
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
