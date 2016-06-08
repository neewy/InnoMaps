package com.innopolis.maps.innomaps.events;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.innopolis.maps.innomaps.database.DBUpdater;
import com.innopolis.maps.innomaps.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.innopolis.maps.innomaps.database.TableFields.EMPTY;

public class EventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    Context context;
    ListView listView;
    protected final List<SearchableItem> eventNames = new ArrayList<>(); //for search list
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

    Menu menu;

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
        hashPref = sPref.getString(context.getString(R.string.hash), EMPTY); //field, storing hash
        updatedPref = sPref.getString(context.getString(R.string.last_update), EMPTY); //field, storing starting day of last week
        //the data were updated

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        swipeRefreshLayout.setOnRefreshListener(this);

        this.adapter = new EventsAdapter(context, getActivity().getSupportFragmentManager(), list, getActivity());
        listView.setAdapter(this.adapter);
        listView.setItemsCanFocus(true);

        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();

        if (!hashPref.equals(EMPTY)) {
            adapter.events.clear();
            list = DBHelper.readEvents(getContext(), false);
            adapter.events = list;
            Collections.sort(adapter.events);
            adapter.notifyDataSetChanged();
            database.close();
        } else {
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        updateFilters(DBHelper.readEvents(getContext(), false));

        if (Utils.isNetworkAvailable(context)) {
            adapter.events.clear();
            swipeRefreshLayout.setRefreshing(true);
            new DBUpdater(context).updateEvents();
            adapter.events = DBHelper.readEvents(getContext(), false);
            Collections.sort(adapter.events);
            adapter.notifyDataSetChanged();
            database.close();
            swipeRefreshLayout.setRefreshing(false);
        } else if (!Utils.isNetworkAvailable(context) && !hashPref.equals(EMPTY)) {
            adapter.events.clear();
            Toast.makeText(context, R.string.offline_message, Toast.LENGTH_SHORT).show();
            adapter.events = DBHelper.readEvents(getContext(), false);
            Collections.sort(adapter.events);
            adapter.notifyDataSetChanged();
            database.close();
        } else if (!Utils.isNetworkAvailable(context) && hashPref.equals(EMPTY)) {
            Toast.makeText(context, R.string.internet_connect, Toast.LENGTH_SHORT).show();
            database.close();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.events_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        this.menu = menu;
        updateFilters(DBHelper.readEvents(getContext(), false));
        final List<SearchableItem> adapterList = new ArrayList<>(eventNames);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchBox = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterList.clear();
                for (SearchableItem item : eventNames) {
                    if (item.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                        adapterList.add(item);
                    }
                }
                ((SuggestionAdapter) searchBox.getAdapter()).refresh(adapterList);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                eventNames.clear();
                SearchableItem.addEvents(eventNames, DBHelper.readUniqueEvents(getContext(), false));
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
                Utils.hideKeyboard(getActivity());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<Event> filteredList = DBHelper.readEvents(getContext(), false);
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
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        MainActivity.getInstance().trackScreenView(context.getString(R.string.events_fragment));
    }

    /**
     * Function, that updates visibility of menu items (filters)
     */
    protected void updateFilters(List<Event> filteredList) {
        if (menu != null) {
            for (int i = 0; i < menu.size(); i++) {
                switch (menu.getItem(i).getItemId()) {
                    case R.id.action_today:
                        menu.getItem(i).setChecked(true);
                        Collection<Event> today = Collections2.filter(filteredList, Event.isToday);
                        if (today.isEmpty()) {
                            menu.getItem(i).setVisible(false);
                        }
                        break;
                    case R.id.action_tomorrow:
                        menu.getItem(i).setChecked(true);
                        Collection<Event> tomorrow = Collections2.filter(filteredList, Event.isTomorrow);
                        if (tomorrow.isEmpty()) {
                            menu.getItem(i).setVisible(false);
                        }
                        break;
                    case R.id.action_this_week:
                        menu.getItem(i).setChecked(true);
                        Collection<Event> thisWeek = Collections2.filter(filteredList, Event.isThisWeek);
                        if (thisWeek.isEmpty()) {
                            menu.getItem(i).setVisible(false);
                        }
                        break;
                }
            }
        }
    }
}
