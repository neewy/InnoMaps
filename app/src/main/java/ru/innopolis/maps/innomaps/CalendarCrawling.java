package ru.innopolis.maps.innomaps;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;

public class CalendarCrawling extends ListActivity {

    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> eventsList;

    private static String url_all_events = "https://www.googleapis.com/calendar/v3/calendars/" +
            "hvtusnfmqbg9u2p5rnc1rvhdfg@group.calendar.google.com/events?singleEvents=true&" +
            "key=AIzaSyDli8qeotu4TGaEs5VKSWy15CDyl4cgZ-o";

    // JSON Node names
    private static final String TAG_KIND = "kind";
    private static final String TAG_ETAG = "etag";
    private static final String TAG_ID = "id";
    private static final String TAG_STATUS = "status";
    private static final String TAG_HTML_LINK = "htmlLink";
    private static final String TAG_CREATED = "created";
    private static final String TAG_UPDATED = "updated";
    private static final String TAG_SUMMARY = "summary";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_CREATOR = "creator";
    private static final String TAG_ORGANIZER = "organizer";
    private static final String TAG_START = "start";
    private static final String TAG_END = "end";

    private static final String TAG_EMAIL = "email";
    private static final String TAG_NAME = "displayName";

    // list of events
    JSONArray events = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_events);

        // Hashmap for ListView
        eventsList = new ArrayList<>();

        new LoadAllEvents().execute();
        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // get values from selected ListItem
                String eid = ((TextView) view.findViewById(R.id.eid)).getText()
                        .toString();

                // start intent showing new Activity
                Intent in = new Intent(getApplicationContext(), EditOrderActivity.class);
                // eid to next Activity
                in.putExtra(TAG_UPDATED, eid);

                // start new Activity
                startActivityForResult(in, 100);
            }
        });

    }


    // Edit Event Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            // 100 means user edit or deleted event => restart screen
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    /**
     * Background Async Task for loading all events by HTTP. Before loading background
     * thread it show Progress dialog and then gets all events from URL
     * */
    class LoadAllEvents extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressDialog pDialog = new ProgressDialog(CalendarCrawling.this);
            pDialog.setMessage("Loading. Please, wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // thing that keeps param
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting json from URL
            JSONObject json = jParser.makeHttpRequest(url_all_events, "GET", params);

            Log.d("All Products: ", json.toString());

            try {
                // get first record as server is alive
                String kind = json.getString(TAG_KIND);

                if (kind.contentEquals("calendar#events")) {
                    // get array from events
                    events = json.getJSONArray(TAG_KIND);

                    // iter all events
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject c = events.getJSONObject(i);
                        String id = c.getString(TAG_ID);
                        String link = c.getString(TAG_HTML_LINK);
                        String created = c.getString(TAG_CREATED);
                        String updated = c.getString(TAG_UPDATED);
                        String summary = c.getString(TAG_SUMMARY);
                        String description = c.getString(TAG_DESCRIPTION);
                        String creator = c.getString(TAG_CREATOR);
                        String email = c.getString(TAG_EMAIL);
                        String name = c.getString(TAG_NAME);
                        String start = c.getString(TAG_START);
                        String end = c.getString(TAG_END);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_ID, id);
                        map.put(TAG_HTML_LINK, link);
                        map.put(TAG_CREATED, created);
                        map.put(TAG_UPDATED, updated);
                        map.put(TAG_SUMMARY, summary);
                        map.put(TAG_DESCRIPTION, description);
                        map.put(TAG_CREATOR, creator);
                        map.put(TAG_EMAIL, email);
                        map.put(TAG_NAME, name);
                        map.put(TAG_START, start);
                        map.put(TAG_END, end);

                        eventsList.add(map);
                    }
                } else {
                    // else if no events start new Add Event Activity
                    Intent i = new Intent(getApplicationContext(),
                            NewEventActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Update JSON in ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            CalendarCrawling.this, eventsList,
                            R.layout.list_item, new String[] {TAG_UPDATED,
                            TAG_SUMMARY},
                            new int[] { R.id.eid, R.id.summary });
                    setListAdapter(adapter);
                }
            });
        }

    }
}