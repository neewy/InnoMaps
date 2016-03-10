package com.innopolis.maps.innomaps.app;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.TextView;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.events.Event;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SuggestionAdapter extends ArrayAdapter<Event> {

    private List<Event> items;
    private ArrayFilter mFilter;

    public SuggestionAdapter(Context context, @LayoutRes int resource, @NonNull List<Event> objects) {
        super(context, resource, objects);
        this.items = new ArrayList<>(objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.complete_row, null);
        }
        String[] locationText = {event.getBuilding(), event.getFloor(), event.getRoom()};
        ((CheckedTextView) convertView.findViewById(R.id.name))
                .setText(event.getSummary());
        ((TextView) convertView.findViewById(R.id.location))
                .setText(StringUtils.join(Utils.clean(locationText), ", "));
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Event getItem(int position) {
        Event item = items.get(position);
        return item;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    public void refresh(List<Event> newData) {
        this.items = new ArrayList<Event>(newData);
        notifyDataSetChanged();
    }

    public int getCount() {
        return items.size();
    }

    private class ArrayFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            List<Event> resList = new ArrayList<>();
            for (Event event: items){
                if (prefix != null && (event.getSummary().toLowerCase().contains(prefix.toString().toLowerCase()) ||
                        event.getBuilding().toLowerCase().contains(prefix.toString().toLowerCase()) ||
                        event.getFloor().toLowerCase().contains(prefix.toString().toLowerCase()) ||
                        event.getRoom().toLowerCase().contains(prefix.toString().toLowerCase()))) {
                    resList.add(event);
                }
            }
            results.values = resList;
            results.count = resList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
        }
    }
}