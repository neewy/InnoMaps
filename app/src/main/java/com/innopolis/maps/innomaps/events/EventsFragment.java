package com.innopolis.maps.innomaps.events;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventCreatorAppointmentDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventCreatorDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventScheduleDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreator;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreatorAppointment;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventFavorable;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventSchedule;
import com.innopolis.maps.innomaps.maps.LatLngFlr;
import com.innopolis.maps.innomaps.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.innopolis.maps.innomaps.database.TableFields.EMPTY;

public class EventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    Context context;
    ListView listView;
    protected final List<SearchableItem> eventNames = new ArrayList<>(); //for search list
    List<Event> list = new ArrayList<>(); //for storing entries
    EventsAdapter adapter; //to populate list above
    SwipeRefreshLayout swipeRefreshLayout;

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

        if (!hashPref.equals(EMPTY)) {
            adapter.events.clear();
            list = readEvents(getContext(), false);

            adapter.events = list;
            Collections.sort(adapter.events);
            adapter.notifyDataSetChanged();
        } else {
            onRefresh();
        }
    }

    @Override
    public void onRefresh() {
        updateFilters(readEvents(getContext(), false));

        if (Utils.isNetworkAvailable(context)) {
            adapter.events.clear();
            swipeRefreshLayout.setRefreshing(true);
            adapter.events = readEvents(getContext(), false);
            Collections.sort(adapter.events);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        } else if (!Utils.isNetworkAvailable(context) && !hashPref.equals(EMPTY)) {
            adapter.events.clear();
            Toast.makeText(context, R.string.offline_message, Toast.LENGTH_SHORT).show();
            adapter.events = readEvents(getContext(), false);
            Collections.sort(adapter.events);
            adapter.notifyDataSetChanged();
        } else if (!Utils.isNetworkAvailable(context) && hashPref.equals(EMPTY)) {
            Toast.makeText(context, R.string.internet_connect, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.events_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        this.menu = menu;
        updateFilters(readEvents(getContext(), false));
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
                SearchableItem.addEvents(eventNames, getContext());
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
        List<Event> filteredList = readEvents(getContext(), false);
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


    /**
     * Returns the list with events
     *
     * @param areFavourite - whether to put marked events or all of them
     */
    public static List<Event> readEvents(Context context, boolean areFavourite) {
        List<Event> events = new ArrayList<>();
        final EventDAO eventDAO = new EventDAO(context);
        EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(context);
        CoordinateDAO coordinateDAO = new CoordinateDAO(context);
        EventCreatorAppointmentDAO eventCreatorAppointmentDAO = new EventCreatorAppointmentDAO(context);
        EventCreatorDAO eventCreatorDAO = new EventCreatorDAO(context);

        List<EventSchedule> eventSchedules = eventScheduleDAO.findUpcomingAndOngoingScheduledEvents();
        for (EventSchedule eventSchedule : eventSchedules) {
            EventFavorable event = (EventFavorable) eventDAO.findById(eventSchedule.getEvent_id());
            Coordinate eventsCoordinate;
            if (eventSchedule.getLocation_id() != null && (!areFavourite || event.isFavourite() == areFavourite)) {
                eventsCoordinate = (Coordinate) coordinateDAO.findById(eventSchedule.getLocation_id());

                Event eventForGUI = new Event();
                eventForGUI.setSummary(event.getName());
                eventForGUI.setHtmlLink(event.getLink());
                try {
                    eventForGUI.setStart(com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(eventSchedule.getStart_datetime()));
                    eventForGUI.setEnd(com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(eventSchedule.getEnd_datetime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                eventForGUI.setEventID(event.getId());
                eventForGUI.setEventScheduleId(eventSchedule.getId());
                eventForGUI.setChecked(event.isFavourite());
                String description = event.getDescription();
                if (!Constants.EMPTY_STRING.equals(description))
                    description += Constants.NEW_LINE + eventSchedule.getComment();
                else
                    description += eventSchedule.getComment();
                eventForGUI.setDescription(description);
                List<EventCreatorAppointment> eventCreatorAppointments = eventCreatorAppointmentDAO.findByEventId(event.getId());
                if (!eventCreatorAppointments.isEmpty()) {
                    EventCreator eventCreator = (EventCreator) eventCreatorDAO.findById(eventCreatorAppointments.get(0).getEvent_creator_id());
                    eventForGUI.setCreatorName(eventCreator.getName());
                    eventForGUI.setCreatorEmail(eventCreator.getEmail());
                } else {
                    eventForGUI.setCreatorName(null);
                    eventForGUI.setCreatorEmail(null);
                }
                eventForGUI.setBuilding(DetailedEvent.getBuildingNameForEvent(eventsCoordinate.getId(), context));
                eventForGUI.setFloorStr(Integer.toString(eventsCoordinate.getFloor()) + Constants.SPACE + Constants.FLOOR);
                if (eventsCoordinate.getType_id() == 3 /*if type is ROOM*/ && null != eventsCoordinate.getName() && !Constants.EMPTY_STRING.equals(eventsCoordinate.getName()))
                    eventForGUI.setRoom(eventsCoordinate.getName());
                else
                    eventForGUI.setRoom(null);
                eventForGUI.setCoordinateLatLngFlr(new LatLngFlr(eventsCoordinate.getLatitude(), eventsCoordinate.getLongitude(), eventsCoordinate.getFloor()));

                Date date = new Date();
                //if the date exceeds current date – we don't store it
                if (eventForGUI.getStart().before(date)) {
                    eventScheduleDAO.delete(eventSchedule);
                } else {
                    events.add(eventForGUI);
                }
            }
        }


        return events;
    }
}
