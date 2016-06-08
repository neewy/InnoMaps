package com.innopolis.maps.innomaps.events;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.database.TableFields;
import com.innopolis.maps.innomaps.utils.Utils;

import java.util.List;

import static com.innopolis.maps.innomaps.database.TableFields.*;

public class MapBottomEventListAdapter extends BaseAdapter {

    public List<Event> events;
    private LayoutInflater lInflater;
    private AppCompatActivity activity;

    public MapBottomEventListAdapter(Context context, List<Event> events, Activity activity) {
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.events = events;
        this.activity = (AppCompatActivity) activity;
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
            view = lInflater.inflate(R.layout.map_bottom_event_list, parent, false);
        }

        final Event event = events.get(position);
        TextView mapEventName = (TextView) view.findViewById(R.id.mapEventName);
        TextView mapEventStart = (TextView) view.findViewById(R.id.mapEventStart);

        mapEventName.setText(event.getSummary());
        mapEventName.setPaintFlags(mapEventName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mapEventStart.setText(Utils.commonTime.format(event.getStart()));
        mapEventStart.setPaintFlags(mapEventStart.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new DetailedEvent();
                Bundle bundle = new Bundle();
                bundle.putString(EVENT_ID, event.getEventID());
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
                FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.replace(R.id.content_frame, fragment, activity.getString(R.string.detailed)).addToBackStack(activity.getString(R.string.detailed));
                ft.commit();
            }
        });
        return view;
    }

    Event getEventRow(int position) {
        return ((Event) getItem(position));
    }
}
