package com.innopolis.maps.innomaps.app;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.innopolis.maps.innomaps.R;

import java.util.ArrayList;
import java.util.List;


public class FavouriteFragment extends EventsFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.events, container, false);
        listView = (ListView) view.findViewById(R.id.eventList);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        this.adapter = new EventsAdapter(context, getActivity().getSupportFragmentManager(), list, getActivity());
        listView.setAdapter(this.adapter);

        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        dbHelper = new DBHelper(context);
                                        database = dbHelper.getWritableDatabase();
                                        onRefresh();
                                    }
                                }
        );
        onRefresh();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.events_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        final List<String> favouriteNames = new ArrayList<>();
        for (Event event: list) {
            favouriteNames.add(event.getSummary());
        }

        searchBox = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchBox.setAdapter(new SuggestionAdapter<String>(getContext(), R.layout.complete_row, favouriteNames));

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                favouriteNames.clear();
                for (Event event : adapter.events) {
                    if (event.getSummary().toLowerCase().contains(s.toString().toLowerCase())) {
                        favouriteNames.add(event.getSummary());
                    }
                }
                ((SuggestionAdapter) searchBox.getAdapter()).refresh(favouriteNames);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Event> filteredList = new ArrayList<Event>(list);
                ArrayList<Event> origin = new ArrayList<Event>(list);

                final CheckedTextView text = (CheckedTextView) view.findViewById(R.id.text1);

                Predicate<Event> predicate = new Predicate<Event>() {
                    @Override
                    public boolean apply(Event event) {
                        return event.getSummary().equals(text.getText());
                    }
                };

                adapter.events.clear();
                for (Event event : Collections2.filter(filteredList, predicate)) {
                    adapter.events.add(event);
                }

                adapter.notifyDataSetChanged();
                list = new ArrayList<>(origin);
            }
        });
    }

    @Override
    public void onRefresh() {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();

        list.clear();
        DBHelper.readEvents(list, database, true);
        adapter.events = list;
        adapter.notifyDataSetChanged();
        database.close();
        swipeRefreshLayout.setRefreshing(false);
    }
}
