package com.innopolis.maps.innomaps.events;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.database.TableFields;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;

import xyz.hanks.library.SmallBang;


public class EventsAdapter extends BaseAdapter {

    public ArrayList<Event> events;
    private Context context;
    private LayoutInflater lInflater;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Activity activity;
    private FragmentManager fm;

    public EventsAdapter(Context context, FragmentManager fm, ArrayList<Event> events, Activity activity) {
        this.context = context;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.events = events;
        this.activity = activity;
        this.fm = fm;
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

        final Event event = events.get(position);

        TextView timeLeft = (TextView) view.findViewById(R.id.timeLeft);
        TextView nameEvent = (TextView) view.findViewById(R.id.nameEvent);
        TextView location = (TextView) view.findViewById(R.id.location);
        TextView dateTime = (TextView) view.findViewById(R.id.dateTime);
        final CheckBox favCheckBox = (CheckBox) view.findViewById(R.id.favCheckBox);

        nameEvent.setText(event.getSummary());
        String[] locationText = new String[3];
        locationText[0] = (event.getBuilding() != null) ? event.getBuilding() : "null";
        locationText[1] = (event.getFloor() != null) ? event.getFloor() : "null";
        locationText[2] = (event.getRoom() != null) ? event.getRoom() : "null";
        location.setText(StringUtils.join(Utils.clean(locationText), ", "));
        Date startTime = event.getStart();
        if (startTime != null) {
            dateTime.setText(Utils.commonTime.format(startTime));
            timeLeft.setText(Utils.prettyTime.format(startTime));
        }
        if (event.getChecked().equals("1")) {
            favCheckBox.setChecked(true);
        } else {
            favCheckBox.setChecked(false);
        }
        final SmallBang mSmallBang = SmallBang.attach2Window(activity);

        final View finalView = view;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((SwipeRefreshLayout) finalView.getParent().getParent()).isRefreshing()) {
                    Fragment fragment = new DetailedEvent();
                    Bundle bundle = new Bundle();
                    bundle.putString("eventID", event.getEventID());
                    fragment.setArguments(bundle);
                    DrawerLayout drawer = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
                    Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    toggle.setDrawerIndicatorEnabled(false);
                    toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            activity.onBackPressed();
                        }
                    });
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.replace(R.id.content_frame, fragment, "Detailed").addToBackStack("Detailed");
                    ft.commit();
                }
            }

        });
        favCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmallBang.bang(favCheckBox);
                String isFav = (favCheckBox.isChecked()) ? "1" : "0";
                String eventID = event.getEventID();
                ContentValues cv = new ContentValues();
                dbHelper = new DBHelper(context);
                database = dbHelper.getWritableDatabase();
                cv.put(TableFields.FAV, isFav);
                database.update(TableFields.EVENTS, cv, "eventID = ?", new String[]{eventID});
                dbHelper.close();
            }
        });
        return view;
    }

    Event getEventRow(int position) {
        return ((Event) getItem(position));
    }

}
