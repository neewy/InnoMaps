package com.innopolis.maps.innomaps.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.innopolis.maps.innomaps.events.Event;
import com.innopolis.maps.innomaps.utils.Utils;

import java.text.ParseException;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;

import static com.innopolis.maps.innomaps.database.TableFields.*;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7; //in order to execute onUpdate() the number should be increased

    private static final String DATABASE_NAME = "eventsDB";

    private static final String DROP = "DROP TABLE IF EXISTS ";




    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBHelper(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBTables.createTable(DBTables.TableColumns.TABLE_EVENTS_CREATE));
        db.execSQL(DBTables.createTable(DBTables.TableColumns.TABLE_EVENT_TYPE_CREATE));
        db.execSQL(DBTables.createTable(DBTables.TableColumns.TABLE_LOCATION_CREATE));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP + EVENTS);
        db.execSQL(DROP + EVENT_TYPE);
        db.execSQL(DROP + LOCATION);
        onCreate(db);
    }


    /**
     * Puts all the events in the List, supplied as first argument
     *
     * @param list         - where to put data, elements are Event
     * @param database     - where to get data from
     * @param areFavourite - whether to put marked events or all of them
     */
    public static void readEvents(List list, SQLiteDatabase database, boolean areFavourite) {
        Cursor cursor;
        String selectQuery = "select events.summary,htmlLink,start,end,events.eventID as eventID,"
                + " description,creator_name,creator_email,telegram_login,telegram_group, checked,"
                + " building,floor,room,latitude,longitude"
                + " from events "
                + "inner join event_type on events.summary=event_type.summary  "
                + "inner join location on events.eventID=location.eventID";
        if (areFavourite) selectQuery += " WHERE checked=1 ";
        cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            int summary, htmlLink, start, end, eventID, checked;
            int description, creator_name, creator_email, telegram_login, telegram_group;
            int building, floor, room, latitude, longitude;

            summary = cursor.getColumnIndex(SUMMARY);
            htmlLink = cursor.getColumnIndex(LINK);
            start = cursor.getColumnIndex(START);
            end = cursor.getColumnIndex(END);
            eventID = cursor.getColumnIndex(EVENT_ID);
            checked = cursor.getColumnIndex(FAV);
            description = cursor.getColumnIndex(DESCRIPTION);
            creator_name = cursor.getColumnIndex(CREATOR_NAME);
            creator_email = cursor.getColumnIndex(CREATOR_EMAIL);
            telegram_login = cursor.getColumnIndex(TELEGRAM_CONTACT);
            telegram_group = cursor.getColumnIndex(TELEGRAM_GROUP);

            building = cursor.getColumnIndex(BUILDING);
            floor = cursor.getColumnIndex(FLOOR);
            room = cursor.getColumnIndex(ROOM);
            latitude = cursor.getColumnIndex(LATITUDE);
            longitude = cursor.getColumnIndex(LONGITUDE);

            do {
                Event event = new Event();
                event.setSummary(cursor.getString(summary));
                event.setHtmlLink(cursor.getString(htmlLink));
                try {
                    event.setStart(Utils.googleTimeFormat.parse(cursor.getString(start)));
                    event.setEnd(Utils.googleTimeFormat.parse(cursor.getString(end)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                event.setEventID(cursor.getString(eventID));
                event.setChecked(cursor.getString(checked));
                event.setDescription(cursor.getString(description));
                event.setCreatorName(cursor.getString(creator_name));
                event.setCreatorEmail(cursor.getString(creator_email));
                event.setTelegramLogin(cursor.getString(telegram_login));
                event.setTelegramGroup(cursor.getString(telegram_group));
                event.setBuilding(cursor.getString(building));
                event.setFloor(cursor.getString(floor));
                event.setRoom(cursor.getString(room));
                event.setLatitude(cursor.getString(latitude));
                event.setLongitude(cursor.getString(longitude));
                list.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
    }

    /**
     * Inserts single event into database, stored on user device
     *
     * @param database - the name of database to put data
     * @param summary  - summary JSON field
     * @param htmlLink - htmllink JSON field
     * @param start    - start date
     * @param end      - end date
     * @param eventID  - unique number to identify single event
     * @param checked  - whether it is favourite or not
     */
    public static void insertEvent(SQLiteDatabase database, String summary, String htmlLink, String start, String end, String eventID, String checked) {
        ContentValues cv = new ContentValues();
        cv.put(SUMMARY, summary);
        cv.put(LINK, htmlLink);
        cv.put(START, start);
        cv.put(END, end);
        cv.put(EVENT_ID, eventID);
        cv.put(FAV, checked);
        database.insert(EVENTS, null, cv);
    }

    /**
     * Inserts event type into database, stored on user device
     *
     * @param database      - the name of database to put data
     * @param summary       - summary JSON field
     * @param description   - description JSON field
     * @param creator_name  - the name of person, who created the event
     * @param creator_email - his or her email
     */
    public static void insertEventType(SQLiteDatabase database, String summary, String description, String creator_name, String creator_email) {
        String[] whereArgs = new String[]{summary};
        Cursor cursor = database.query(EVENT_TYPE, null, "summary=?", whereArgs, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put(SUMMARY, summary);
            cv.put(CREATOR_NAME, creator_name);
            cv.put(CREATOR_EMAIL, creator_email);
            Matcher telLogMatch = Utils.telLogPattern.matcher(description);
            String telegramGroup, telegramLogin = "";
            if (telLogMatch.find()) {
                telegramLogin = telLogMatch.group();
                cv.put(TELEGRAM_CONTACT, telegramLogin);
                description = description.replace(telegramLogin, "");
            } else {
                cv.put(TELEGRAM_CONTACT, NULL);
            }
            Matcher telGroupMatch = Utils.telGroupPattern.matcher(description);
            if (telGroupMatch.find()) {
                telegramGroup = telGroupMatch.group();
                cv.put(TELEGRAM_GROUP, telegramGroup);
                description = description.replace(telegramGroup, "");
            } else {
                cv.put(TELEGRAM_GROUP, NULL);
            }
            cv.put(DESCRIPTION, description);
            database.insert(EVENT_TYPE, null, cv);
        }
    }

    /**
     * Inserts location of a single event, both geographic position and relative one (building/floor/room)
     *
     * @param database - the name of database to put data
     * @param location - location JSON field
     * @param eventID  - unique number to identify single event
     */
    public static void insertLocation(SQLiteDatabase database, String location, String eventID) {
        String[] whereArgs = new String[]{eventID};
        Cursor cursor = database.query(LOCATION, null, "eventID=?", whereArgs, null, null, null);
        ContentValues cv = new ContentValues();
        if (cursor.getCount() == 0) {
            String locationMass[] = location.split("/");
            cv.put(EVENT_ID, eventID);
            if (locationMass.length > 0) {
                cv.put(BUILDING, locationMass[0]);
            } else {
                cv.put(BUILDING, NULL);
            }
            if (locationMass.length > 1) {
                cv.put(FLOOR, locationMass[1]);
            } else {
                cv.put(FLOOR, NULL);
            }
            if (locationMass.length > 2) {
                cv.put(ROOM, locationMass[2]);
            } else {
                cv.put(ROOM, NULL);
            }
            Random random = new Random();
            Double latitude = 55.7520 + random.nextDouble() * 0.01;
            Double longitude = 48.7418 + random.nextDouble() * 0.01;
            cv.put(LATITUDE, latitude.toString());
            cv.put(LONGITUDE, longitude.toString());
            database.insert(LOCATION, null, cv);
        }
    }
}