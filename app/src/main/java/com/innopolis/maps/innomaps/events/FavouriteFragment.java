package com.innopolis.maps.innomaps.events;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.app.SearchableItem;
import com.innopolis.maps.innomaps.app.SuggestionAdapter;
import com.innopolis.maps.innomaps.database.DBHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class FavouriteFragment extends EventsFragment {

    protected final List<SearchableItem> favouriteNames = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.favourite, container, false);
        listView = (ListView) view.findViewById(R.id.eventListFav);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fav_swipe_refresh_layout);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        swipeRefreshLayout.setOnRefreshListener(this);

        listView.setEmptyView(view.findViewById(R.id.empty_fav));

        this.adapter = new EventsAdapter(context, getActivity().getSupportFragmentManager(), list, getActivity());
        listView.setAdapter(this.adapter);

        onRefresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.events_menu, menu);
        this.menu = menu;
        updateFilters(DBHelper.readEvents(getContext(), true));
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        final List<SearchableItem> adapterList = new ArrayList<>(favouriteNames);
        searchBox = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchBox.setAdapter(new SuggestionAdapter(getContext(), R.layout.complete_row, favouriteNames));

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterList.clear();
                for (SearchableItem item : favouriteNames) {
                    if (item.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                        adapterList.add(item);
                    }
                }
                ((SuggestionAdapter) searchBox.getAdapter()).refresh(adapterList);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                favouriteNames.clear();
                SearchableItem.addEvents(favouriteNames, DBHelper.readUniqueEvents(getContext(), true), getContext());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Event> filteredList = new ArrayList<>(list);
                ArrayList<Event> origin = new ArrayList<>(list);

                final CheckedTextView text = (CheckedTextView) view.findViewById(R.id.name);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        List<Event> filteredList = DBHelper.readEvents(getContext(), true);
        item.setChecked(!item.isChecked());
        boolean addToEvents = item.isChecked();
        switch (item.getItemId()) {
            case R.id.action_today:
                Collection<Event> today = Collections2.filter(filteredList, Event.isToday);
                if (today.isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_events_today, Toast.LENGTH_LONG).show();
                    return true;
                }
                for (Event event : today) {
                    if (addToEvents && !adapter.events.contains(event))
                        adapter.events.add(event);
                    else
                        adapter.events.remove(event);
                }
                Collections.sort(adapter.events);
                break;
            case R.id.action_tomorrow:
                Collection<Event> tomorrow = Collections2.filter(filteredList, Event.isTomorrow);
                if (tomorrow.isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_events_tomorrow, Toast.LENGTH_LONG).show();
                    return true;
                }
                for (Event event : tomorrow) {
                    if (addToEvents && !adapter.events.contains(event))
                        adapter.events.add(event);
                    else
                        adapter.events.remove(event);
                }
                Collections.sort(adapter.events);
                break;
            case R.id.action_this_week:
                Collection<Event> thisWeek = Collections2.filter(filteredList, Event.isThisWeek);
                if (thisWeek.isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_events_week, Toast.LENGTH_LONG).show();
                    return true;
                }
                for (Event event : thisWeek) {
                    if (addToEvents && !adapter.events.contains(event))
                        adapter.events.add(event);
                    else
                        adapter.events.remove(event);
                }
                Collections.sort(adapter.events);
                break;
        }
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void onRefresh() {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        updateFilters(DBHelper.readEvents(getContext(), true));

        list.clear();
        list = DBHelper.readEvents(getContext(), true);
        adapter.events = list;
        adapter.notifyDataSetChanged();
        database.close();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MainActivity.getInstance().trackScreenView(context.getString(R.string.favourite_fragment));
    }
}