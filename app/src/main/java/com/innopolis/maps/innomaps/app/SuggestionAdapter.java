package com.innopolis.maps.innomaps.app;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neewy on 3/2/16.
 */
public class SuggestionAdapter<T> extends ArrayAdapter<T> {

    private List<T> items;
    private List<T> filteredItems;
    private ArrayFilter mFilter;

    public SuggestionAdapter(Context context, @LayoutRes int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
        this.items = new ArrayList<>(objects);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public T getItem(int position) {
        T item = items.get(position);
        return item;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    public void refresh(List<T> newData) {
        this.items = new ArrayList<>(newData);
        notifyDataSetChanged();
    }

    public int getCount() {
        return items.size();
    }

    private class ArrayFilter extends Filter {


        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            List<T> resList = new ArrayList<>();
            for (T string: items){
                if (prefix != null && ((String) string).toLowerCase().contains(prefix.toString().toLowerCase())){
                    resList.add(string);
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