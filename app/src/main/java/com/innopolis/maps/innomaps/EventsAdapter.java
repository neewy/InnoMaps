package com.innopolis.maps.innomaps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import xyz.hanks.library.SmallBang;

/**
 * Created by Nikolay on 04.02.2016.
 */
public class EventsAdapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<HashMap<String, String>> events;
    DBHelper dbHelper;
    SQLiteDatabase database;
    Activity activity;

    public EventsAdapter(Context ctx, ArrayList<HashMap<String, String>> events, Activity activity) {
        this.ctx = ctx;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.events = events;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.single_event, parent, false);
        }

        final HashMap<String, String> values = events.get(position);

        TextView timeLeft = (TextView) view.findViewById(R.id.timeLeft);
        TextView nameEvent = (TextView) view.findViewById(R.id.nameEvent);
        TextView creator = (TextView) view.findViewById(R.id.creator);
        TextView descEvent = (TextView) view.findViewById(R.id.descEvent);
        final CheckBox favCheckBox = (CheckBox) view.findViewById(R.id.favCheckBox);

        timeLeft.setText(values.get("timeLeft"));
        nameEvent.setText(values.get("summary"));
        creator.setText(values.get("creator_name"));
        descEvent.setText(values.get("creator_name"));
        if (values.get("checked").equals("1")) {
            favCheckBox.setChecked(true);
        } else {
            favCheckBox.setChecked(false);
        }
        final SmallBang mSmallBang = SmallBang.attach2Window(activity);
        final View finalView = view;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, DetailedEvent.class);
                i.putExtra("eventID",values.get(DBHelper.COLUMN_EVENT_ID));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(i);
            }

        });
        favCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmallBang.bang(favCheckBox);
                String isFav = (favCheckBox.isChecked()) ? "1" : "0";
                String eventID = values.get(DBHelper.COLUMN_EVENT_ID);
                ContentValues cv = new ContentValues();
                dbHelper = new DBHelper(ctx);
                database = dbHelper.getWritableDatabase();
                cv.put(DBHelper.COLUMN_FAV, isFav);
                int updCount = database.update(DBHelper.TABLE1, cv, "eventID = ?",
                        new String[]{eventID});
                //Log.d("Checked!", "updated rows count = " + updCount + " | " + eventID + " | " + isFav);
                dbHelper.close();
            }
        });

        return view;
    }

    HashMap<String, String> getEventRow(int position) {
        return ((HashMap<String, String>) getItem(position));
    }

}