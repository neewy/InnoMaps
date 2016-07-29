package com.innopolis.maps.innomaps.events;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
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
import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventFavorable;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

import xyz.hanks.library.SmallBang;


public class EventsAdapter extends BaseAdapter {

    public List<Event> events;
    private Context context;
    private LayoutInflater lInflater;
    private Activity activity;
    private FragmentManager fm;

    public EventsAdapter(Context context, FragmentManager fm, List<Event> events, Activity activity) {
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
        final EventDAO eventDAO = new EventDAO(context);
        final EventFavorable eventFavorable = (EventFavorable) eventDAO.findById(event.getEventID());

        nameEvent.setText(event.getSummary());
        String[] locationText = new String[3];
        locationText[0] = (event.getBuilding() != null) ? event.getBuilding() : Constants.NULL_STRING;
        locationText[1] = (event.getFloorStr() != null) ? event.getFloorStr() : Constants.NULL_STRING;
        locationText[2] = (event.getRoom() != null) ? event.getRoom() : Constants.NULL_STRING;
        location.setText(StringUtils.join(Utils.clean(locationText), ", "));
        Date startTime = event.getStart();
        if (startTime != null) {
            dateTime.setText(Utils.commonTime.format(startTime));
            timeLeft.setText(Utils.prettyTime.format(startTime));
        }
        favCheckBox.setChecked(event.isChecked());


        final View finalView = view;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(finalView.getParent().getParent() instanceof SwipeRefreshLayout) || !((SwipeRefreshLayout) finalView.getParent().getParent()).isRefreshing()) {
                    Fragment fragment = new DetailedEvent();
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.EVENT_ID, event.getEventID());
                    bundle.putInt(Constants.EVENT_SCHEDULE_ID, event.getEventScheduleId());
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
                    ft.replace(R.id.content_frame, fragment, context.getString(R.string.detailed)).addToBackStack(context.getString(R.string.detailed));
                    ft.commit();
                }
            }

        });
        favCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SmallBang mSmallBang;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mSmallBang = SmallBang.attach2Window(activity);
                    mSmallBang.bang(favCheckBox);
                }
                event.setChecked(favCheckBox.isChecked());

                EventFavorable updatedEvent = new EventFavorable(eventFavorable.getId(), eventFavorable.getName(), eventFavorable.getDescription(),
                        eventFavorable.getLink(), eventFavorable.getGcals_event_id(), eventFavorable.getModified(), favCheckBox.isChecked());
                eventDAO.update(updatedEvent);
            }
        });
        return view;
    }


    Event getEventRow(int position) {
        return ((Event) getItem(position));
    }

}
