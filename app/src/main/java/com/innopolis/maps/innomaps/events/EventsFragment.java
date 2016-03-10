package com.innopolis.maps.innomaps.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
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
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    Context context;
    ListView listView;
    List<Event> list = new ArrayList<>(); //for storing entries
    EventsAdapter adapter; //to populate list above
    SwipeRefreshLayout swipeRefreshLayout;

    DBHelper dbHelper;
    SQLiteDatabase database;
    SharedPreferences sPref; //to store md5 hash of loaded file

    String hashPref;
    String updatedPref;

    ActionBar mActionBar;
    SearchView searchView;
    SearchView.SearchAutoComplete searchBox;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.events, container, false); //changing the fragment

        listView = (ListView) view.findViewById(R.id.eventList);

        sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        hashPref = sPref.getString("hash", ""); //field, storing hash
        updatedPref = sPref.getString("lastUpdate", ""); //field, storing starting day of last week
        //the data were updated

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        this.adapter = new EventsAdapter(context, getActivity().getSupportFragmentManager(), list, getActivity());
        listView.setAdapter(this.adapter);
        listView.setItemsCanFocus(true);

        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();

        if (!hashPref.equals("")) {
            adapter.events.clear();
            adapter.events = DBHelper.readEvents(getContext(), false);
            Collections.sort(adapter.events);
            adapter.notifyDataSetChanged();
            database.close();
        } else {
            onRefresh();
        }

        return view;
    }

    @Override
    public void onRefresh() {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();

        if (Utils.isNetworkAvailable(context)) {
            adapter.events.clear();
            swipeRefreshLayout.setRefreshing(true);
            new com.innopolis.maps.innomaps.database.DBUpdater(context);
            adapter.events = DBHelper.readEvents(getContext(), false);
            Collections.sort(adapter.events);
            adapter.notifyDataSetChanged();
            database.close();
            swipeRefreshLayout.setRefreshing(false);
        } else if (!Utils.isNetworkAvailable(context) && !hashPref.equals("")) {
            adapter.events.clear();
            Toast.makeText(context, "You are offline. Showing last events", Toast.LENGTH_SHORT).show();
            adapter.events = DBHelper.readEvents(getContext(), false);
            Collections.sort(adapter.events);
            adapter.notifyDataSetChanged();
            database.close();
        } else if (!Utils.isNetworkAvailable(context) && hashPref.equals("")) {
            Toast.makeText(context, "Connect to the internet", Toast.LENGTH_SHORT).show();
            database.close();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.events_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchBox = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);

        searchBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<Event> filteredList = new ArrayList<Event>(list);
                ArrayList<Event> origin = new ArrayList<Event>(list);
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
        ArrayList<Event> filteredList = new ArrayList<Event>(list);
        ArrayList<Event> origin = new ArrayList<Event>(list);
        switch (item.getItemId()) {
            case R.id.action_today:
                Collection<Event> today = Collections2.filter(filteredList, Event.isToday);
                if (today.isEmpty()) {
                    Toast.makeText(getContext(), "No events today", Toast.LENGTH_LONG).show();
                    return true;
                }
                this.adapter.events.clear();
                for (Event event : today)
                    adapter.events.add(event);
                break;
            case R.id.action_tomorrow:
                Collection<Event> tomorrow = Collections2.filter(filteredList, Event.isTomorrow);
                if (tomorrow.isEmpty()) {
                    Toast.makeText(getContext(), "No events tomorrow", Toast.LENGTH_LONG).show();
                    return true;
                }
                adapter.events.clear();
                for (Event event : tomorrow)
                    adapter.events.add(event);
                break;
            case R.id.action_this_week:
                Collection<Event> thisWeek = Collections2.filter(filteredList, Event.isTomorrow);
                if (thisWeek.isEmpty()) {
                    Toast.makeText(getContext(), "No events this week", Toast.LENGTH_LONG).show();
                    return true;
                }
                adapter.events.clear();
                for (Event event : thisWeek)
                    adapter.events.add(event);
                break;
        }
        adapter.notifyDataSetChanged();
        list = new ArrayList<>(origin);
        return true;
    }
}
