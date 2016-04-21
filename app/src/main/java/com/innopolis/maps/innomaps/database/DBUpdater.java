package com.innopolis.maps.innomaps.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class DBUpdater {

    private SQLiteDatabase database;
    private SharedPreferences sPref;

    public DBUpdater(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        sPref = PreferenceManager.getDefaultSharedPreferences(context);
        GraphLoader graphLoader = new GraphLoader(context, database, sPref);
        graphLoader.execute();
    }

    public void updateEvents() {
        new JsonParseTask(database, sPref).execute();
    }
}
