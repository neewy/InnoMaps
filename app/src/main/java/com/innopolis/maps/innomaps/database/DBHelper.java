package com.innopolis.maps.innomaps.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.innopolis.maps.innomaps.events.Event;
import com.innopolis.maps.innomaps.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.innopolis.maps.innomaps.database.SQLQueries.*;
import static com.innopolis.maps.innomaps.database.TableFields.ATTR;
import static com.innopolis.maps.innomaps.database.TableFields.BUILDING;
import static com.innopolis.maps.innomaps.database.TableFields.CREATOR_EMAIL;
import static com.innopolis.maps.innomaps.database.TableFields.CREATOR_NAME;
import static com.innopolis.maps.innomaps.database.TableFields.DATABASE_NAME;
import static com.innopolis.maps.innomaps.database.TableFields.DESCRIPTION;
import static com.innopolis.maps.innomaps.database.TableFields.DOOR;
import static com.innopolis.maps.innomaps.database.TableFields.END;
import static com.innopolis.maps.innomaps.database.TableFields.EVENTS;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_ID;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_POI;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_TYPE;
import static com.innopolis.maps.innomaps.database.TableFields.FAV;
import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.ID;
import static com.innopolis.maps.innomaps.database.TableFields.NUMBER;
import static com.innopolis.maps.innomaps.database.TableFields._ID;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LINK;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.POI;
import static com.innopolis.maps.innomaps.database.TableFields.POI_ID;
import static com.innopolis.maps.innomaps.database.TableFields.POI_NAME;
import static com.innopolis.maps.innomaps.database.TableFields.ROOM;
import static com.innopolis.maps.innomaps.database.TableFields.START;
import static com.innopolis.maps.innomaps.database.TableFields.SUMMARY;
import static com.innopolis.maps.innomaps.database.TableFields.TYPE;


public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 8; //in order to execute onUpdate() the number should be increased

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
        db.execSQL(DBTables.createTable(DBTables.TableColumns.TABLE_EVENT_POI_CREATE));
        db.execSQL(DBTables.createTable(DBTables.TableColumns.TABLE_POI_CREATE));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(drop(EVENTS));
        db.execSQL(drop(EVENT_TYPE));
        db.execSQL(drop(EVENT_POI));
        db.execSQL(drop(POI));
        onCreate(db);
    }


    /**
     * Returns the list with events
     *
     * @param areFavourite - whether to put marked events or all of them
     */
    public static List<Event> readEvents(Context context, boolean areFavourite) {
        List<Event> events = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;
        String selectQuery = READ_EVENTS_QUERY;
        if (areFavourite) selectQuery += IF_FAVOURITE_QUERY;
        cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            int summary, htmlLink, start, end, eventID, checked;
            int description, creator_name, creator_email;
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
                event.setBuilding(cursor.getString(building));
                event.setFloor(cursor.getString(floor));
                event.setRoom(cursor.getString(room));
                event.setLatitude(cursor.getString(latitude));
                event.setLongitude(cursor.getString(longitude));

                Date date = new Date();

                //if the date exceeds current date – we don't store it
                if (event.getStart().before(date)) {
                    database.execSQL(
                            SQLQueries.deleteLikeQuery(EVENTS, EVENT_ID, Integer.toString(eventID)));
                } else {
                    events.add(event);
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return events;
    }

    /**
     * Returns list of unique events (based on their names)
     *
     * @param areFavourite - whether to put marked events or all of them
     */
    public static List<Event> readUniqueEvents(Context context, boolean areFavourite) {
        List<Event> events = readEvents(context, areFavourite);
        Set<Event> eventSet = new TreeSet<>(Event.summaryComparator);
        eventSet.addAll(events);
        return new ArrayList<>(eventSet);
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
    public static void insertEvent(SQLiteDatabase database, String summary, String htmlLink,
                                   String start, String end, String eventID, String checked) {
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
    public static void insertEventType(SQLiteDatabase database, String summary, String description,
                                       String creator_name, String creator_email) {
        String[] whereArgs = new String[]{summary};
        Cursor cursor = database.query(EVENT_TYPE, null, "summary=?", whereArgs, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put(SUMMARY, summary);
            cv.put(CREATOR_NAME, creator_name);
            cv.put(CREATOR_EMAIL, creator_email);
            cv.put(DESCRIPTION, description);
            database.insert(EVENT_TYPE, null, cv);
        }
        cursor.close();
    }


    /**
     * Inserts points of interest into database
     *
     * @param database
     * @param pois
     * @return is the operation successful or not
     */
    public static void insertPois(SQLiteDatabase database, List<HashMap<String, String>> pois) {
        if (pois.size() == 0) return;
        for (int i = 0; i < pois.size(); i++) {
            HashMap<String, String> poi = pois.get(i);
            ContentValues cv = new ContentValues();
            cv.put(_ID, poi.get(ID));
            cv.put(POI_NAME, poi.get(POI_NAME));
            cv.put(BUILDING, poi.get(BUILDING));
            cv.put(FLOOR, poi.get(FLOOR));
            cv.put(ROOM, poi.get(NUMBER));
            cv.put(LATITUDE, poi.get(LATITUDE));
            cv.put(LONGITUDE, poi.get(LONGITUDE));
            cv.put(TYPE, poi.get(TYPE));
            cv.put(ATTR, poi.get(ATTR));
            database.insert(POI, null, cv);
        }
    }

    /**
     * Inserts new record into Event_Poi table
     *
     * @param database
     * @param eventID
     * @param poiID
     * @return
     */
    public static void insertEventPoi(SQLiteDatabase database, String eventID, String poiID) {
        ContentValues cv = new ContentValues();
        cv.put(EVENT_ID, eventID);
        cv.put(POI_ID, poiID);
        database.insert(EVENT_POI, null, cv);
    }


    /**
     * Returns the list of POI
     *
     * @param database
     * @return
     */
    public static List<HashMap<String, String>> readPois(SQLiteDatabase database, Integer type) {
        String query;
        if (type.equals(0)) query = readQueryConstructor(0);
        else query = readQueryConstructor(1);

        List<HashMap<String, String>> pois = new ArrayList<>();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> poi = new HashMap<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    poi.put(cursor.getColumnName(i), cursor.getString(i));
                }
                pois.add(poi);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return pois;
    }

    /**
     * Returns the list of all rooms POI
     * or the list of all POI that are not like a room
     */
    private static String readQueryConstructor(Integer type) {
        String query;
        if (type.equals(0)) query = readRoomTypeQuery(POI, ROOM, TYPE);
        else
            query = readOtherTypesQuery(POI, TYPE, ROOM, ATTR, DOOR);
        return query;
    }
}