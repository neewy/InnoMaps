package com.innopolis.maps.innomaps;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class Events extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {
    static Context context;
    ListView listView;

    static ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>(); //for storing entries
    EventsAdapter adapter; //to populate list from list above
    SwipeRefreshLayout swipeRefreshLayout;
    DBHelper dbHelper;
    SQLiteDatabase database;
    SharedPreferences sPref; //to store md5 hash of loaded file
    CheckBox favCheckBox;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.events, container, false); //changing the fragment
        listView = (ListView) view.findViewById(R.id.eventList);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        this.adapter = new EventsAdapter(context, getActivity().getSupportFragmentManager(), list, getActivity());
        listView.setAdapter(this.adapter);
        listView.setItemsCanFocus(true);
        CheckBox favCheckBox = (CheckBox) view.findViewById(R.id.favCheckBox);
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
        return view;
    }

    @Override
    public void onRefresh() {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String savedText = sPref.getString("updated", "");

        if (Utils.isNetworkAvailable(context)) {
            //Toast.makeText(context, "Getting new events", Toast.LENGTH_SHORT).show();
            try {
                new ParseTask().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else if (!Utils.isNetworkAvailable(context) && !savedText.equals("")) {
            list.clear();
            Toast.makeText(context, "You are offline. Showing last events", Toast.LENGTH_SHORT).show();
            DBHelper.readEvents(list, database, false);
            adapter.notifyDataSetChanged();
            database.close();
            swipeRefreshLayout.setRefreshing(false);
        } else if (!Utils.isNetworkAvailable(context) && savedText.equals("")) {
            Toast.makeText(context, "Connect to the internet", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            database.close();
        }
    }

    /**
     * This class extends AsyncTask in order to do networking in separate thread (other than UI thread)
     */
    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        Date date;
        DateFormat dateFormat;

        @Override
        protected String doInBackground(Void... params) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date = new Date();
            Utils.shiftDate(date);
            try {
                URL url = new URL("https://www.googleapis.com/calendar/v3/calendars/hvtusnfmqbg9u2p5rnc1rvhdfg@group.calendar.google.com/events?timeMin=" + dateFormat.format(date) + "T10%3A00%3A00-07%3A00&orderby=updated&sortorder=descending&futureevents=true&alt=json&key=AIzaSyDli8qeotu4TGaEs5VKSWy15CDyl4cgZ-o");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line);

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        /**
         * Checks whether the JSON file was updated or not
         *
         * @param updatedKey - md5 hash
         * @return true in case the JSON was updated
         */
        protected boolean jsonUpdated(String updatedKey) {
            sPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String savedText = sPref.getString("updated", "");
            if (savedText.equals(updatedKey)) {
                return false;
            } else {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putString("updated", updatedKey);
                database.execSQL("delete from " + DBHelper.TABLE1);
                ed.commit();
                return true;
            }
        }

        @Override
        protected void onPostExecute(String strJson) {
            JSONObject dataJsonObj = null;
            String md5 = new String(Hex.encodeHex(DigestUtils.md5(resultJson)));
            try {
                dataJsonObj = new JSONObject(strJson);
                if (jsonUpdated(md5)) {
                    list.clear();
                    JSONArray events = dataJsonObj.getJSONArray("items");
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject jsonEvent = events.getJSONObject(i);
                        String summary = "", htmlLink = "", start = "", end = "",
                                location = "", eventID = "", description = "",
                                creator_name = "", creator_email = "", checked = "0"; //initializing db fields
                        Iterator<String> iter = jsonEvent.keys();
                        while (iter.hasNext()) {
                            String key = iter.next().toString();
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
                            }
                        }
                        DBHelper.insertEvent(database, summary, htmlLink, start, end, eventID, checked);
                        DBHelper.insertEventType(database, summary, description, creator_name, creator_email);
                        DBHelper.insertLocation(database, location, eventID);
                    }
                    DBHelper.readEvents(list, database, false);
                } else {
                    list.clear();
                    //Toast.makeText(context, "You've got the last events", Toast.LENGTH_SHORT).show();
                    DBHelper.readEvents(list, database, false);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            database.close();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}

