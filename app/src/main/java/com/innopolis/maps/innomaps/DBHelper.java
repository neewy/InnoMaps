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
        if (areFavourite) {
            String[] favourite = new String[]{"1"};
            cursor = database.query(DBHelper.TABLE1, null, "checked=?", favourite, null, null, null);
        } else {
            cursor = database.query(DBHelper.TABLE1, null, null, null, null, null, null);
        }
        if (cursor.moveToFirst()) {
            int summary, htmlLink, start, end, location, id, checked;
            summary = cursor.getColumnIndex(DBHelper.COLUMN_SUMMARY);
            htmlLink = cursor.getColumnIndex(DBHelper.COLUMN_LINK);
            start = cursor.getColumnIndex(DBHelper.COLUMN_START);
            end = cursor.getColumnIndex(DBHelper.COLUMN_END);
            location = cursor.getColumnIndex(DBHelper.COLUMN_LOCATION);
            id = cursor.getColumnIndex(DBHelper.COLUMN_EVENT_ID);
            checked = cursor.getColumnIndex(DBHelper.COLUMN_FAV);
            do {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(DBHelper.COLUMN_SUMMARY, cursor.getString(summary));
                item.put(DBHelper.COLUMN_LINK, cursor.getString(htmlLink));
                item.put(DBHelper.COLUMN_START, cursor.getString(start));
                item.put(DBHelper.COLUMN_END, cursor.getString(end));
                item.put(DBHelper.COLUMN_LOCATION, cursor.getString(location));
                item.put(DBHelper.COLUMN_EVENT_ID, cursor.getString(id));
                item.put(DBHelper.COLUMN_FAV, cursor.getString(checked));
                String[] whereArgs = new String[]{cursor.getString(summary)};
                Cursor cursor1 = database.query(DBHelper.TABLE2, null, "summary=?", whereArgs, null, null, null);
                cursor1.moveToFirst();
                int description = cursor1.getColumnIndex("description");
                int creator_name = cursor1.getColumnIndex("creator_name");
                item.put("description", cursor1.getString(description));
                item.put("creator_name", cursor1.getString(creator_name));
                long timeLeft;
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    timeLeft = (d.getTime() - s.parse(cursor.getString(start)).getTime()) / (24 * 60 * 60 * 1000);
                } catch (ParseException e) {
                    timeLeft = 0;
                }
                item.put("timeLeft", Long.toString(timeLeft) + " days");
                list.add(item);
                cursor1.close();
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
    }

    protected static void readEventsLog(SQLiteDatabase database) {
        Cursor cursor = database.query(DBHelper.TABLE1, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int summary, htmlLink, start, end, location, id;
            summary = cursor.getColumnIndex(DBHelper.COLUMN_SUMMARY);
            htmlLink = cursor.getColumnIndex(DBHelper.COLUMN_LINK);
            start = cursor.getColumnIndex(DBHelper.COLUMN_START);
            end = cursor.getColumnIndex(DBHelper.COLUMN_END);
            location = cursor.getColumnIndex(DBHelper.COLUMN_LOCATION);
            id = cursor.getColumnIndex(DBHelper.COLUMN_EVENT_ID);
            do {
                Log.d("mLog", "summary = " + cursor.getString(summary) +
                        ", htmlLink = " + cursor.getString(htmlLink) +
                        ", start = " + cursor.getString(start) +
                        ", end = " + cursor.getString(end) +
                        ", location = " + cursor.getString(location) +
                        ", id = " + cursor.getString(id));
            } while (cursor.moveToNext());
        } else
            Log.d("mLog", "0 rows");
        cursor.close();
        database.close();
    }

    protected void readEventsTypesLog(SQLiteDatabase database) {
        Cursor cursor = database.query(DBHelper.TABLE2, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int summary, description, creator_name, creator_email, telegram;
            summary = cursor.getColumnIndex(DBHelper.COLUMN_SUMMARY);
            description = cursor.getColumnIndex("description");
            creator_name = cursor.getColumnIndex("creator_name");
            creator_email = cursor.getColumnIndex("creator_email");
            telegram = cursor.getColumnIndex("telegram");
            do {
                Log.d("mLog", "summary = " + cursor.getString(summary) +
                        ", description = " + cursor.getString(description) +
                        ", creator_name = " + cursor.getString(creator_name) +
                        ", creator_email = " + cursor.getString(creator_email) +
                        ", telegram = " + cursor.getString(telegram));
            } while (cursor.moveToNext());
        } else
            Log.d("mLog", "0 rows");
        cursor.close();
        database.close();
    }

    protected static void insertEvent(SQLiteDatabase database, String summary, String htmlLink, String start, String end, String location, String id, String checked) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_SUMMARY, summary);
        cv.put(DBHelper.COLUMN_LINK, htmlLink);
        cv.put(DBHelper.COLUMN_START, start);
        cv.put(DBHelper.COLUMN_END, end);
        cv.put(DBHelper.COLUMN_LOCATION, location);
        cv.put(DBHelper.COLUMN_EVENT_ID, id);
        cv.put(DBHelper.COLUMN_FAV, checked);
        database.insert(DBHelper.TABLE1, null, cv);
    }

    protected static void insertEventType(SQLiteDatabase database, String summary, String description, String creator_name, String creator_email) {
        String[] whereArgs = new String[]{summary};
        Cursor cursor = database.query(DBHelper.TABLE2, null, "summary=?", whereArgs, null, null, null);
        if (cursor.getCount() == 0) {

            ContentValues cv = new ContentValues();
            cv.put(DBHelper.COLUMN_SUMMARY, summary);
            cv.put("description", description);
            cv.put("creator_name", creator_name);
            cv.put("creator_email", creator_email);

            Pattern pattern = Pattern.compile("/^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$/");
            Matcher matcher = pattern.matcher(description);
            if (matcher.find()) {
                String telegram = matcher.group(1);
                cv.put("telegram", telegram);
            }

            database.insert(DBHelper.TABLE2, null, cv);
        }
    }
}