package com.innopolis.maps.innomaps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "eventsDB";
    public static final String TABLE1 = "events";
    public static final String TABLE2 = "event_type";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_LINK = "htmlLink";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_EVENT_ID = "eventID";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE events (_id INTEGER PRIMARY KEY, summary TEXT, htmlLink TEXT, start TEXT, end TEXT, location TEXT, eventID TEXT)");
        db.execSQL("CREATE TABLE event_type (_id INTEGER PRIMARY KEY, summary TEXT, description TEXT, creator_name TEXT, creator_email TEXT, telegram TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1);
        onCreate(db);
    }
}