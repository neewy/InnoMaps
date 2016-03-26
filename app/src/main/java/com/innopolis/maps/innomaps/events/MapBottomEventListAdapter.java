package com.innopolis.maps.innomaps.events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.utils.Utils;

import java.util.List;

public class MapBottomEventListAdapter extends BaseAdapter {

    public List<Event> events;
    private Context context;
    private LayoutInflater lInflater;

    public MapBottomEventListAdapter(Context context, List<Event> events) {
        this.context = context;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.events = events;
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

        Event event = events.get(position);
        TextView mapEventName = (TextView) view.findViewById(R.id.mapEventName);
        TextView mapEventStart = (TextView) view.findViewById(R.id.mapEventStart);
        mapEventName.setText(event.getSummary());
        mapEventStart.setText(Utils.commonTime.format(event.getStart()));
        return view;
    }

    Event getEventRow(int position) {
        return ((Event) getItem(position));
    }
}
