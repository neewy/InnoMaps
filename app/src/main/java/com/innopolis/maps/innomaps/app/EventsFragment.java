package com.innopolis.maps.innomaps.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.collect.Collections2;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.Duration;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class EventsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    Context context;
    ListView listView;
    ArrayList<Event> list = new ArrayList<>(); //for storing entries
    EventsAdapter adapter; //to populate list above
    SwipeRefreshLayout swipeRefreshLayout;
    DBHelper dbHelper;
    SQLiteDatabase database;
    SharedPreferences sPref; //to store md5 hash of loaded file
    String hashPref;
    String updatedPref;
    ActionBar mActionBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
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
            list.clear();
            DBHelper.readEvents(list, database, false);
            Collections.sort(list);
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
            swipeRefreshLayout.setRefreshing(true);
            new ParseTask().execute();
        } else if (!Utils.isNetworkAvailable(context) && !hashPref.equals("")) {
            list.clear();
            Toast.makeText(context, "You are offline. Showing last events", Toast.LENGTH_SHORT).show();
            DBHelper.readEvents(list, database, false);
            Collections.sort(list);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<Event> filteredList = new ArrayList<Event>(list);
        ArrayList<Event> origin = new ArrayList<Event>(list);
        switch (item.getItemId()) {
            case R.id.action_today:
                adapter.events.clear();
                for (Event event: Collections2.filter(filteredList,Event.isToday))
                    adapter.events.add(event);
                adapter.notifyDataSetChanged();
                list = new ArrayList<>(origin);
                return true;
            case R.id.action_tomorrow:
                adapter.events.clear();
                for (Event event: Collections2.filter(filteredList,Event.isTomorrow))
                    adapter.events.add(event);
                adapter.notifyDataSetChanged();
                list = new ArrayList<>(origin);
                return true;
            case R.id.action_this_week:
                adapter.events.clear();
                for (Event event: Collections2.filter(filteredList,Event.isThisWeek))
                    adapter.events.add(event);
                adapter.notifyDataSetChanged();
                list = new ArrayList<>(origin);
                return true;
            default:
                break;
        }

        return false;
    }

    /**
     * This class extends AsyncTask in order to do networking in separate thread (other than UI thread)
     */
    private class ParseTask extends AsyncTask<Void, Void, String> {

        String resultJson = "";
        Date date;
        DateFormat dateFormat;

        @Override
        protected String doInBackground(Void... params) {

            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date = new Date();
            Utils.shiftDate(date);
            return Utils.doGetRequest(
                    "https://www.googleapis.com/calendar/v3/calendars/hvtusnfmqbg9u2p5rnc1rvhdfg@group.calendar.google.com/events?timeMin="
                            + dateFormat.format(date)
                            + "T10%3A00%3A00-07%3A00&orderby=updated&sortorder=descending&futureevents=true&alt=json&key=AIzaSyDli8qeotu4TGaEs5VKSWy15CDyl4cgZ-o");
        }

        /**
         * Checks whether the JSON file was updated or not
         *
         * @param hashKey - md5 hash
         * @return true in case the JSON was updated
         */
        protected boolean jsonUpdated(String hashKey) {
            String savedText = sPref.getString("hash", "");
            if (savedText.equals(hashKey)) {
                return false;
            } else {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("hash", hashKey);
                database.execSQL("delete from " + DBHelper.TABLE1);
                database.execSQL("delete from " + DBHelper.TABLE2);
                database.execSQL("delete from " + DBHelper.TABLE3);
                ed.apply();
                return true;
            }
        }

        protected boolean weekUpdated() {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.clear(Calendar.MINUTE);
            cal.clear(Calendar.SECOND);
            cal.clear(Calendar.MILLISECOND);
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek()); //the starting date of current week
            Date updatedDate = null;
            try {
                updatedDate = Utils.googleTimeFormat.parse(sPref.getString("lastUpdate", ""));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            /*If it's null or old - update the database*/
            if (updatedDate == null || !updatedDate.equals(cal.getTime())) {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("lastUpdate", Utils.googleTimeFormat.format(cal.getTime()));
                database.execSQL("delete from " + DBHelper.TABLE1);
                database.execSQL("delete from " + DBHelper.TABLE2);
                database.execSQL("delete from " + DBHelper.TABLE3);
                ed.apply();
                return true;
            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(String strJson) {
            JSONObject dataJsonObj;
            String md5 = new String(Hex.encodeHex(DigestUtils.md5(resultJson)));
            try {
                dataJsonObj = new JSONObject(strJson);
                list.clear();
                if (jsonUpdated(md5) | weekUpdated()) {
                    getEventsListLive(dataJsonObj);
                } else {
                    DBHelper.readEvents(list, database, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Collections.sort(list);
            adapter.notifyDataSetChanged();
            database.close();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private ArrayList<Event> getEventsListLive(JSONObject dataJsonObj) throws JSONException {
        return getEventsList(dataJsonObj, database);
    }

    public ArrayList<Event> getEventsList(JSONObject dataJsonObj, SQLiteDatabase db) throws JSONException {
        JSONArray events = dataJsonObj.getJSONArray("items");
        for (int i = 0; i < events.length(); i++) {
            JSONObject jsonEvent = events.getJSONObject(i);
            String summary = "", htmlLink = "", start = "", end = "",
                    location = "", eventID = "", description = "",
                    creator_name = "", creator_email = "", checked = "0", //initializing db fields
                    recurrence = "";
            Iterator<String> iter = jsonEvent.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                switch (key) {
                    case "summary":
                        summary = jsonEvent.getString("summary");
                        break;
                    case "htmlLink":
                        htmlLink = jsonEvent.getString("htmlLink");
                        break;
                    case "start":
                        start = jsonEvent.getJSONObject("start").getString("dateTime");
                        break;
                    case "end":
                        end = jsonEvent.getJSONObject("end").getString("dateTime");
                        break;
                    case "location":
                        location = jsonEvent.getString("location");
                        break;
                    case "id":
                        eventID = jsonEvent.getString("id");
                        break;
                    case "description":
                        description = jsonEvent.getString("description");
                        break;
                    case "creator":
                        creator_name = jsonEvent.getJSONObject("creator").getString("displayName");
                        creator_email = jsonEvent.getJSONObject("creator").getString("email");
                        break;
                    /*Field that tells how often does the event repeats*/
                    case "recurrence":
                        recurrence = jsonEvent.getJSONArray("recurrence").getString(0).replace("RRULE:", "");
                        break;
                }
            }

            DateTime currentDate = new DateTime(new Date().getTime());
            RecurrenceRule rule = null;
            try {
                rule = new RecurrenceRule(recurrence);
            } catch (InvalidRecurrenceRuleException e) {
                e.printStackTrace();
            }

            DateTime startDate = null;
            DateTime endDate;
            Long durationTime = null;

            try {
                startDate = new DateTime(Utils.googleTimeFormat.parse(start).getTime());
                endDate = new DateTime(Utils.googleTimeFormat.parse(end).getTime());
                durationTime = TimeUnit.MILLISECONDS.toMinutes(endDate.getTimestamp() - startDate.getTimestamp());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            RecurrenceRuleIterator it = rule.iterator(startDate);

            int maxInstances = 100; // limit instances for rules that recur forever
            int weekCount = 0;

            /*Variable weekCount stand for two event, occurring after today's date
            * the duration is the same - so there is no need to look for end date,
            * we can get it with the duration*/
            while (it.hasNext() && (maxInstances-- > 0) && weekCount < 2) {
                DateTime nextInstance = it.nextDateTime();
                if (nextInstance.after(currentDate)) {
                    weekCount++;
                    String finalStartDate = Utils.googleTimeFormat.format(new Date(nextInstance.getTimestamp()));
                    String finalEndDate = Utils.googleTimeFormat.format(new Date(nextInstance.addDuration(new Duration(1, 0, 0, durationTime.intValue(), 0)).getTimestamp()));
                    DBHelper.insertEvent(db, summary, htmlLink, finalStartDate, finalEndDate, eventID + "_" + weekCount, checked);
                    DBHelper.insertEventType(db, summary, description, creator_name, creator_email);
                    DBHelper.insertLocation(db, location, eventID + "_" + weekCount);
                }
            }
        }
        DBHelper.readEvents(list, db, false);
        return list;
    }
}
