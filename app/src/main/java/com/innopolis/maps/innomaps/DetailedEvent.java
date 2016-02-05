package com.innopolis.maps.innomaps;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Nikolay on 05.02.2016.
 */
public class DetailedEvent extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase database;

    TextView eventName;
    TextView timeLeft;
    TextView location;
    TextView description;
    TextView organizer;

    String summary, htmlLink, start, end, locationStr, descriptionStr, creator, telegram;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_desc);
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        Bundle extras = getIntent().getExtras();
        eventName = (TextView) findViewById(R.id.eventName);
        timeLeft = (TextView) findViewById(R.id.timeLeft);
        location = (TextView) findViewById(R.id.location);
        description = (TextView) findViewById(R.id.description);
        organizer = (TextView) findViewById(R.id.organizer);
        String eventID = extras.get("eventID").toString();
        Cursor cursor = database.query(DBHelper.TABLE1, null, "eventID=?", new String[]{eventID}, null, null, null);
        cursor.moveToFirst();
        do {
            int summary, htmlLink, start, end, location;
            summary = cursor.getColumnIndex(DBHelper.COLUMN_SUMMARY);
            htmlLink = cursor.getColumnIndex(DBHelper.COLUMN_LINK);
            start = cursor.getColumnIndex(DBHelper.COLUMN_START);
            end = cursor.getColumnIndex(DBHelper.COLUMN_END);
            location = cursor.getColumnIndex(DBHelper.COLUMN_LOCATION);
            this.summary = cursor.getString(summary);
            this.htmlLink = cursor.getString(htmlLink);
            this.start = cursor.getString(start);
            this.end = cursor.getString(end);
            this.locationStr = cursor.getString(location);
            String[] whereArgs = new String[]{cursor.getString(summary)};
            Cursor cursor1 = database.query(DBHelper.TABLE2, null, "summary=?", whereArgs, null, null, null);
            cursor1.moveToFirst();
            int description = cursor1.getColumnIndex("description");
            int creator_name = cursor1.getColumnIndex("creator_name");
            int telegram = cursor1.getColumnIndex("telegram");
            this.descriptionStr = cursor1.getString(description);
            this.creator = cursor1.getString(creator_name);
            this.telegram = cursor1.getString(telegram);
            cursor1.close();
        }
        while (cursor.moveToNext());
        cursor.close();
        database.close();
        eventName.setText(summary);
        timeLeft.setText(start);
        location.setText(locationStr);
        description.setText(descriptionStr);
        organizer.setText(creator);
    }
}
