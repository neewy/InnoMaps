package com.innopolis.maps.innomaps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "eventsDB";
    public static final String TABLE1 = "events";
    public static final String TABLE2 = "event_type";
    public static final String TABLE3 = "location";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SUMMARY = "summary";
    public static final String COLUMN_LINK = "htmlLink";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";

    public static final String COLUMN_LOCATION = "location";

    public static final String COLUMN_EVENT_ID = "eventID";
    public static final String COLUMN_FAV = "checked";

    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CREATOR_NAME = "creator_name";
    public static final String COLUMN_CREATOR_EMAIL = "creator_email";
    public static final String COLUMN_TELEGRAM = "telegram";

    public static final String COLUMN_BUILDING = "building";
    public static final String COLUMN_FLOOR = "floor";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_LATITIDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE events (_id INTEGER PRIMARY KEY, summary TEXT, htmlLink TEXT, start TEXT, end TEXT, eventID TEXT, checked TEXT)");
        db.execSQL("CREATE TABLE event_type (_id INTEGER PRIMARY KEY, summary TEXT, description TEXT, creator_name TEXT, creator_email TEXT, telegram TEXT)");
        db.execSQL("CREATE TABLE location (_id INTEGER PRIMARY KEY, eventID TEXT, building TEXT, floor TEXT, room TEXT, latitude TEXT, longitude TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE3);
        onCreate(db);
    }

    protected static void readEvents(List list, SQLiteDatabase database, boolean areFavourite) {
        SimpleDateFormat formatter = new SimpleDateFormat();
        Date d = new Date();
        Cursor cursor = null;
        String sqlQuery = "select events.summary,htmlLink,start,end,events.eventID as eventID,"
                + " description,creator_name,creator_email,telegram, checked,"
                + " building,floor,room,latitude,longitude"
                + " from events "
                + "inner join event_type on events.summary=event_type.summary  "
                + "inner join location on events.eventID=location.eventID";
        if (areFavourite) {
            sqlQuery += " WHERE checked=1 ";

        }
        cursor = database.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            int summary, htmlLink, start, end, eventID, checked;
            int description, creator_name, creator_email, telegram;
            int building, floor, room, lititude, longitude;

            summary = cursor.getColumnIndex(DBHelper.COLUMN_SUMMARY);
            htmlLink = cursor.getColumnIndex(DBHelper.COLUMN_LINK);
            start = cursor.getColumnIndex(DBHelper.COLUMN_START);
            end = cursor.getColumnIndex(DBHelper.COLUMN_END);
            eventID = cursor.getColumnIndex(DBHelper.COLUMN_EVENT_ID);
            checked = cursor.getColumnIndex(DBHelper.COLUMN_FAV);
            description = cursor.getColumnIndex(DBHelper.COLUMN_DESCRIPTION);
            creator_name = cursor.getColumnIndex(DBHelper.COLUMN_CREATOR_NAME);
            creator_email = cursor.getColumnIndex(DBHelper.COLUMN_CREATOR_EMAIL);
            telegram = cursor.getColumnIndex(DBHelper.COLUMN_TELEGRAM);

            building = cursor.getColumnIndex(DBHelper.COLUMN_BUILDING);
            floor = cursor.getColumnIndex(DBHelper.COLUMN_FLOOR);
            room = cursor.getColumnIndex(DBHelper.COLUMN_ROOM);
            lititude = cursor.getColumnIndex(DBHelper.COLUMN_LATITIDE);
            longitude = cursor.getColumnIndex(DBHelper.COLUMN_LONGITUDE);


            do {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(DBHelper.COLUMN_SUMMARY, cursor.getString(summary));
                item.put(DBHelper.COLUMN_LINK, cursor.getString(htmlLink));
                item.put(DBHelper.COLUMN_START, cursor.getString(start));
                item.put(DBHelper.COLUMN_END, cursor.getString(end));
                item.put(DBHelper.COLUMN_EVENT_ID, cursor.getString(eventID));
                item.put(DBHelper.COLUMN_FAV, cursor.getString(checked));
                item.put(DBHelper.COLUMN_DESCRIPTION, cursor.getString(description));
                item.put(DBHelper.COLUMN_CREATOR_NAME, cursor.getString(creator_name));
                item.put(DBHelper.COLUMN_CREATOR_EMAIL, cursor.getString(creator_email));
                item.put(DBHelper.COLUMN_TELEGRAM, cursor.getString(telegram));
                item.put(DBHelper.COLUMN_BUILDING, cursor.getString(building));
                item.put(DBHelper.COLUMN_FLOOR, cursor.getString(floor));
                item.put(DBHelper.COLUMN_ROOM, cursor.getString(room));
                item.put(DBHelper.COLUMN_LATITIDE, cursor.getString(lititude));
                item.put(DBHelper.COLUMN_LONGITUDE, cursor.getString(longitude));

                long timeLeft;
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    timeLeft = (d.getTime() - s.parse(cursor.getString(start)).getTime()) / (24 * 60 * 60 * 1000);
                } catch (ParseException e) {
                    timeLeft = 0;
                }
                item.put("timeLeft", Long.toString(timeLeft) + " days");
                list.add(item);
                //cursor1.close();
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
    }

    protected static void insertEvent(SQLiteDatabase database, String summary, String htmlLink, String start, String end, String eventID, String checked) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_SUMMARY, summary);
        cv.put(DBHelper.COLUMN_LINK, htmlLink);
        cv.put(DBHelper.COLUMN_START, start);
        cv.put(DBHelper.COLUMN_END, end);
        cv.put(DBHelper.COLUMN_EVENT_ID, eventID);
        cv.put(DBHelper.COLUMN_FAV, checked);
        database.insert(DBHelper.TABLE1, null, cv);
    }

    protected static void insertEventType(SQLiteDatabase database, String summary, String description, String creator_name, String creator_email) {
        String[] whereArgs = new String[]{summary};
        Cursor cursor = database.query(DBHelper.TABLE2, null, "summary=?", whereArgs, null, null, null);
        if (cursor.getCount() == 0) {

            ContentValues cv = new ContentValues();
            cv.put(DBHelper.COLUMN_SUMMARY, summary);
            cv.put(DBHelper.COLUMN_DESCRIPTION, description);
            cv.put(DBHelper.COLUMN_CREATOR_NAME, creator_name);
            cv.put(DBHelper.COLUMN_CREATOR_EMAIL, creator_email);

            String telegr[] = description.split("https://");
            if (telegr.length > 1) cv.put(DBHelper.COLUMN_TELEGRAM, telegr[1]);

            database.insert(DBHelper.TABLE2, null, cv);
        }
    }

    protected static void insertLocation(SQLiteDatabase database, String location, String eventID) {
        String[] whereArgs = new String[]{eventID};
        Cursor cursor = database.query(DBHelper.TABLE3, null, "eventID=?", whereArgs, null, null, null);
        ContentValues cv = new ContentValues();
        if (cursor.getCount() == 0) {

            String locationMass[] = location.split("https://");
            cv.put(DBHelper.COLUMN_EVENT_ID, eventID);
            if (locationMass.length == 3) {
                cv.put(DBHelper.COLUMN_BUILDING, locationMass[0]);
                cv.put(DBHelper.COLUMN_FLOOR, locationMass[1]);
                cv.put(DBHelper.COLUMN_ROOM, locationMass[2]);
                cv.put(DBHelper.COLUMN_LATITIDE, "55.752071");
                cv.put(DBHelper.COLUMN_LONGITUDE, "48.741831");
            } else {
                cv.put(DBHelper.COLUMN_BUILDING, location);
            }
            database.insert(DBHelper.TABLE3, null, cv);
        }

    }
}