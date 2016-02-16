package com.innopolis.maps.innomaps.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.innopolis.maps.innomaps.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;


public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7; //in order to execute onUpdate() the number should be increased

    private static final String DATABASE_NAME = "eventsDB";

    public static final String TABLE1 = "events";
    public static final String TABLE2 = "event_type";
    public static final String TABLE3 = "location";

    public static final String COLUMN_ID = "_id"; //Primary key (as stated in Android guidelines)

    /* Events table */
    public static final String COLUMN_SUMMARY = "summary"; //just a title
    public static final String COLUMN_LINK = "htmlLink"; //calendar link
    public static final String COLUMN_START = "start"; //start date
    public static final String COLUMN_END = "end"; //end date
    public static final String COLUMN_EVENT_ID = "eventID"; //unique field
    public static final String COLUMN_FAV = "checked"; //is the event favourite

    /* Event_type table */
    public static final String COLUMN_DESCRIPTION = "description"; //detailed description
    public static final String COLUMN_CREATOR_NAME = "creator_name"; //the person, who created the event
    public static final String COLUMN_CREATOR_EMAIL = "creator_email"; //his or her gmail
    public static final String COLUMN_TELEGRAM_LOGIN = "telegram_login"; //telegram link available
    public static final String COLUMN_TELEGRAM_GROUP = "telegram_group"; //telegram link available

    /* Location table */
    public static final String COLUMN_BUILDING = "building";
    public static final String COLUMN_FLOOR = "floor";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_LATITIDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE events (_id INTEGER PRIMARY KEY, summary TEXT, htmlLink TEXT, start TEXT, end TEXT, eventID TEXT, checked TEXT)");
        db.execSQL("CREATE TABLE event_type (_id INTEGER PRIMARY KEY, summary TEXT, description TEXT, creator_name TEXT, creator_email TEXT, telegram_login TEXT, telegram_group TEXT)");
        db.execSQL("CREATE TABLE location (_id INTEGER PRIMARY KEY, eventID TEXT, building TEXT, floor TEXT, room TEXT, latitude TEXT, longitude TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE3);
        onCreate(db);
    }


    /**
     * Puts all the events in the List, supplied as first argument
     *
     * @param list         - where to put data, elements are HashMap<String, String>
     * @param database     - where to get data from
     * @param areFavourite - whether to put marked events or all of them
     */
    protected static void readEvents(List list, SQLiteDatabase database, boolean areFavourite) {
        SimpleDateFormat formatter = new SimpleDateFormat();
        Date d = new Date();
        Cursor cursor;
        String sqlQuery = "select events.summary,htmlLink,start,end,events.eventID as eventID,"
                + " description,creator_name,creator_email,telegram_login,telegram_group, checked,"
                + " building,floor,room,latitude,longitude"
                + " from events "
                + "inner join event_type on events.summary=event_type.summary  "
                + "inner join location on events.eventID=location.eventID";
        if (areFavourite) sqlQuery += " WHERE checked=1 ";
        cursor = database.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            int summary, htmlLink, start, end, eventID, checked;
            int description, creator_name, creator_email, telegram_login, telegram_group;
            int building, floor, room, latitude, longitude;

            summary = cursor.getColumnIndex(DBHelper.COLUMN_SUMMARY);
            htmlLink = cursor.getColumnIndex(DBHelper.COLUMN_LINK);
            start = cursor.getColumnIndex(DBHelper.COLUMN_START);
            end = cursor.getColumnIndex(DBHelper.COLUMN_END);
            eventID = cursor.getColumnIndex(DBHelper.COLUMN_EVENT_ID);
            checked = cursor.getColumnIndex(DBHelper.COLUMN_FAV);
            description = cursor.getColumnIndex(DBHelper.COLUMN_DESCRIPTION);
            creator_name = cursor.getColumnIndex(DBHelper.COLUMN_CREATOR_NAME);
            creator_email = cursor.getColumnIndex(DBHelper.COLUMN_CREATOR_EMAIL);
            telegram_login = cursor.getColumnIndex(DBHelper.COLUMN_TELEGRAM_LOGIN);
            telegram_group = cursor.getColumnIndex(DBHelper.COLUMN_TELEGRAM_GROUP);

            building = cursor.getColumnIndex(DBHelper.COLUMN_BUILDING);
            floor = cursor.getColumnIndex(DBHelper.COLUMN_FLOOR);
            room = cursor.getColumnIndex(DBHelper.COLUMN_ROOM);
            latitude = cursor.getColumnIndex(DBHelper.COLUMN_LATITIDE);
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
                item.put(DBHelper.COLUMN_TELEGRAM_LOGIN, cursor.getString(telegram_login));
                item.put(DBHelper.COLUMN_TELEGRAM_GROUP, cursor.getString(telegram_group));
                item.put(DBHelper.COLUMN_BUILDING, cursor.getString(building));
                item.put(DBHelper.COLUMN_FLOOR, cursor.getString(floor));
                item.put(DBHelper.COLUMN_ROOM, cursor.getString(room));
                item.put(DBHelper.COLUMN_LATITIDE, cursor.getString(latitude));
                item.put(DBHelper.COLUMN_LONGITUDE, cursor.getString(longitude));
                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
    }

    /**
     * Inserts single event into database, stored on user device
     * @param database - the name of database to put data
     * @param summary - summary JSON field
     * @param htmlLink - htmllink JSON field
     * @param start - start date
     * @param end - end date
     * @param eventID - unique number to identify single event
     * @param checked - whether it is favourite or not
     */
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

    /**
     * Inserts event type into database, stored on user device
     * @param database - the name of database to put data
     * @param summary - summary JSON field
     * @param description - description JSON field
     * @param creator_name - the name of person, who created the event
     * @param creator_email - his or her email
     */
    protected static void insertEventType(SQLiteDatabase database, String summary, String description, String creator_name, String creator_email) {
        String[] whereArgs = new String[]{summary};
        Cursor cursor = database.query(DBHelper.TABLE2, null, "summary=?", whereArgs, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.COLUMN_SUMMARY, summary);
            cv.put(DBHelper.COLUMN_DESCRIPTION, description);
            cv.put(DBHelper.COLUMN_CREATOR_NAME, creator_name);
            cv.put(DBHelper.COLUMN_CREATOR_EMAIL, creator_email);
            Matcher telLogMatch = Utils.telLogPattern.matcher(description);
            if (telLogMatch.find())
                cv.put(DBHelper.COLUMN_TELEGRAM_LOGIN, telLogMatch.group());
            else
                cv.put(DBHelper.COLUMN_TELEGRAM_LOGIN, "null");
            Matcher telGroupMatch = Utils.telGroupPattern.matcher(description);
            if (telGroupMatch.find())
                cv.put(DBHelper.COLUMN_TELEGRAM_GROUP, telGroupMatch.group());
            else
                cv.put(DBHelper.COLUMN_TELEGRAM_GROUP, "null");
            database.insert(DBHelper.TABLE2, null, cv);
        }
    }

    /**
     * Inserts location of a single event, both geographic position and relative one (building/floor/room)
     * @param database - the name of database to put data
     * @param location - location JSON field
     * @param eventID - unique number to identify single event
     */
    protected static void insertLocation(SQLiteDatabase database, String location, String eventID) {
        String[] whereArgs = new String[]{eventID};
        Cursor cursor = database.query(DBHelper.TABLE3, null, "eventID=?", whereArgs, null, null, null);
        ContentValues cv = new ContentValues();
        if (cursor.getCount() == 0) {
            String locationMass[] = location.split("/");
            cv.put(DBHelper.COLUMN_EVENT_ID, eventID);
            if (locationMass.length > 0) {
                cv.put(DBHelper.COLUMN_BUILDING, locationMass[0]);
            } else {
                cv.put(DBHelper.COLUMN_BUILDING, "null");
            }
            if (locationMass.length > 1) {
                cv.put(DBHelper.COLUMN_FLOOR, locationMass[1]);
            } else {
                cv.put(DBHelper.COLUMN_FLOOR, "null");
            }
            if (locationMass.length > 2) {
                cv.put(DBHelper.COLUMN_ROOM, locationMass[2]);
            } else {
                cv.put(DBHelper.COLUMN_ROOM, "null");
            }
            cv.put(DBHelper.COLUMN_LATITIDE, "55.752071");
            cv.put(DBHelper.COLUMN_LONGITUDE, "48.741831");
            database.insert(DBHelper.TABLE3, null, cv);
        }
    }
}