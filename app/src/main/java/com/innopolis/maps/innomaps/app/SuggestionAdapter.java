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

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class SuggestionAdapter extends ArrayAdapter<SearchableItem> {

    public List<SearchableItem> items;
    private ArrayFilter mFilter;

    public SuggestionAdapter(Context context, @LayoutRes int resource, @NonNull List<SearchableItem> objects) {
        super(context, resource, objects);
        this.items = new LinkedList<>(objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchableItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.complete_row, null);
        }
        List<String> location = new LinkedList<>();
        if (item.getBuilding() != null) location.add(item.getBuilding());
        if (item.getFloor() != null) location.add(item.getFloor());
        if (item.getRoom() != null) location.add(item.getRoom());
        String[] locationText  = new String[location.size()];
        ((CheckedTextView) convertView.findViewById(R.id.name))
                .setText(item.getName());
        ((TextView) convertView.findViewById(R.id.location))
                .setText(StringUtils.join((String[]) location.toArray(locationText), ", "));
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public SearchableItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    public void refresh(List<SearchableItem> newData) {
        this.items = new LinkedList<>(newData);
        notifyDataSetChanged();
    }

    public int getCount() {
        return items.size();
    }

    private class ArrayFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            List<SearchableItem> resList = new LinkedList<>();
            for (SearchableItem item: items){
                if (prefix != null && (item.getName().toLowerCase().contains(prefix.toString().toLowerCase()) ||
                        item.getBuilding().toLowerCase().contains(prefix.toString().toLowerCase()) ||
                        item.getFloor().toLowerCase().contains(prefix.toString().toLowerCase()) ||
                        item.getRoom().toLowerCase().contains(prefix.toString().toLowerCase()))) {
                    resList.add(item);
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