package com.innopolis.maps.innomaps;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Events extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    ListView listView;
    static ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    DBHelper dbHelper;
    SQLiteDatabase database;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        listView = (ListView) findViewById(R.id.eventList);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        this.adapter = new SimpleAdapter(Events.this, list, R.layout.single_event, new String[]{"summary", "creator_name", "location", "timeLeft"}, new int[]{R.id.nameEvent, R.id.creator, R.id.descEvent, R.id.timeLeft});
        listView.setAdapter(this.adapter);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        dbHelper = new DBHelper(Events.this);
                                        database = dbHelper.getWritableDatabase();
                                        onRefresh();
                                    }
                                }
        );
    }

    @Override
    public void onRefresh() {
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString("updated", "");

        if (isNetworkAvailable()) {
            Toast.makeText(this, "Getting new events", Toast.LENGTH_SHORT).show();
            try {
                new ParseTask().execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else if (!isNetworkAvailable() && !savedText.equals("")) {
            Toast.makeText(this, "You are offline. Showing last events", Toast.LENGTH_SHORT).show();
            readEvents(list);
            adapter.notifyDataSetChanged();
            database.close();
            swipeRefreshLayout.setRefreshing(false);
        } else if (!isNetworkAvailable() && savedText.equals("")) {
            Toast.makeText(this, "Connect to the internet", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            database.close();
        }

    }

    protected void readEvents(List list) {
        SimpleDateFormat formatter = new SimpleDateFormat();
        Date d = new Date();
        Cursor cursor = database.query(DBHelper.TABLE1, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int summary, htmlLink, start, end, location, id;
            summary = cursor.getColumnIndex(DBHelper.COLUMN_SUMMARY);
            htmlLink = cursor.getColumnIndex(DBHelper.COLUMN_LINK);
            start = cursor.getColumnIndex(DBHelper.COLUMN_START);
            end = cursor.getColumnIndex(DBHelper.COLUMN_END);
            location = cursor.getColumnIndex(DBHelper.COLUMN_LOCATION);
            id = cursor.getColumnIndex(DBHelper.COLUMN_EVENT_ID);
            do {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(DBHelper.COLUMN_SUMMARY, cursor.getString(summary));
                item.put(DBHelper.COLUMN_LINK, cursor.getString(htmlLink));
                item.put(DBHelper.COLUMN_START, cursor.getString(start));
                item.put(DBHelper.COLUMN_END, cursor.getString(end));
                item.put(DBHelper.COLUMN_LOCATION, cursor.getString(location));
                item.put(DBHelper.COLUMN_EVENT_ID, cursor.getString(id));
                String[] whereArgs = new String[]{cursor.getString(summary)};
                Cursor cursor1 = database.query(DBHelper.TABLE2, null, "summary=?", whereArgs, null, null, null);
                cursor1.moveToFirst();
                int description = cursor1.getColumnIndex("description");
                int creator_name = cursor1.getColumnIndex("creator_name");
                item.put("description", cursor1.getString(description));
                item.put("creator_name", cursor1.getString(creator_name));
                long timeLeft;
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    timeLeft = (d.getTime() - s.parse(cursor.getString(start)).getTime()) / (24 * 60 * 60 * 1000);
                } catch (ParseException e) {
                    timeLeft = 0;
                }
                item.put("timeLeft", Long.toString(timeLeft) + " days");
                list.add(item);
            } while (cursor.moveToNext());
        } else
            Toast.makeText(Events.this, "No current events", Toast.LENGTH_SHORT).show();
        cursor.close();
    }

    protected void readEventsLog() {
        Cursor cursor = database.query(DBHelper.TABLE1, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int summary, htmlLink, start, end, location, id;
            summary = cursor.getColumnIndex(DBHelper.COLUMN_SUMMARY);
            htmlLink = cursor.getColumnIndex(DBHelper.COLUMN_LINK);
            start = cursor.getColumnIndex(DBHelper.COLUMN_START);
            end = cursor.getColumnIndex(DBHelper.COLUMN_END);
            location = cursor.getColumnIndex(DBHelper.COLUMN_LOCATION);
            id = cursor.getColumnIndex(DBHelper.COLUMN_EVENT_ID);
            do {
                Log.d("mLog", "summary = " + cursor.getString(summary) +
                        ", htmlLink = " + cursor.getString(htmlLink) +
                        ", start = " + cursor.getString(start) +
                        ", end = " + cursor.getString(end) +
                        ", location = " + cursor.getString(location) +
                        ", id = " + cursor.getString(id));
            } while (cursor.moveToNext());
        } else
            Log.d("mLog", "0 rows");

        cursor.close();
    }

    protected void readEventsTypesLog() {
        Cursor cursor = database.query(DBHelper.TABLE2, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int summary, description, creator_name, creator_email, telegram;
            summary = cursor.getColumnIndex(DBHelper.COLUMN_SUMMARY);
            description = cursor.getColumnIndex("description");
            creator_name = cursor.getColumnIndex("creator_name");
            creator_email = cursor.getColumnIndex("creator_email");
            telegram = cursor.getColumnIndex("telegram");
            do {
                Log.d("mLog", "summary = " + cursor.getString(summary) +
                        ", description = " + cursor.getString(description) +
                        ", creator_name = " + cursor.getString(creator_name) +
                        ", creator_email = " + cursor.getString(creator_email) +
                        ", telegram = " + cursor.getString(telegram));
            } while (cursor.moveToNext());
        } else
            Log.d("mLog", "0 rows");
        cursor.close();
    }

    protected void insertEvent(String summary, String htmlLink, String start, String end, String location, String id) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.COLUMN_SUMMARY, summary);
        cv.put(DBHelper.COLUMN_LINK, htmlLink);
        cv.put(DBHelper.COLUMN_START, start);
        cv.put(DBHelper.COLUMN_END, end);
        cv.put(DBHelper.COLUMN_LOCATION, location);
        cv.put(DBHelper.COLUMN_EVENT_ID, id);
        database.insert(DBHelper.TABLE1, null, cv);
    }

    protected void insertEventType(String summary, String description, String creator_name, String creator_email) {
        String[] whereArgs = new String[]{summary};
        Cursor cursor = database.query(DBHelper.TABLE2, null, "summary=?", whereArgs, null, null, null);
        if (cursor.getCount() == 0) {

            ContentValues cv = new ContentValues();
            cv.put(DBHelper.COLUMN_SUMMARY, summary);
            cv.put("description", description);
            cv.put("creator_name", creator_name);
            cv.put("creator_email", creator_email);

            Pattern pattern = Pattern.compile("/^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$/");
            Matcher matcher = pattern.matcher(description);
            if (matcher.find()) {
                String telegram = matcher.group(1);
                cv.put("telegram", telegram);
            }

            database.insert(DBHelper.TABLE2, null, cv);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        Date date;
        DateFormat dateFormat;
        private final static long MILLISECONDS_PER_8DAY = 1000L * 60 * 60 * 24 * 8;

        //shift the given Date by exactly 8 days.
        public void shiftDate(Date d) {
            long time = d.getTime();
            time -= MILLISECONDS_PER_8DAY;
            d.setTime(time);
        }

        @Override
        protected String doInBackground(Void... params) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date = new Date();
            shiftDate(date);
            try {
                URL url = new URL("https://www.googleapis.com/calendar/v3/calendars/hvtusnfmqbg9u2p5rnc1rvhdfg@group.calendar.google.com/events?timeMin=" + dateFormat.format(date) + "T10%3A00%3A00-07%3A00&orderby=updated&sortorder=descending&futureevents=true&alt=json&key=AIzaSyDli8qeotu4TGaEs5VKSWy15CDyl4cgZ-o");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return resultJson;
        }

        protected boolean jsonUpdated(String updatedKey) {
            sPref = getPreferences(MODE_PRIVATE);
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
            list.clear();
            JSONObject dataJsonObj = null;
            String md5 = new String(Hex.encodeHex(DigestUtils.md5(resultJson)));
            try {
                dataJsonObj = new JSONObject(strJson);
                if (jsonUpdated(md5)) {
                    JSONArray events = dataJsonObj.getJSONArray("items");
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject jsonEvent = events.getJSONObject(i);
                        String summary, htmlLink, start, end, location, id, description, creator_name, creator_email = null;
                        try {
                            summary = (jsonEvent.getString("summary") != null) ? jsonEvent.getString("summary") : "";
                        } catch (JSONException e) {
                            summary = "";
                        }
                        try {
                            htmlLink = (jsonEvent.getString("htmlLink") != null) ? jsonEvent.getString("htmlLink") : "";
                        } catch (JSONException e) {
                            htmlLink = "";
                        }
                        try {
                            start = (jsonEvent.getJSONObject("start").getString("dateTime") != null) ? jsonEvent.getJSONObject("start").getString("dateTime").substring(0, 16).replace('T', ' ') : "";
                        } catch (JSONException e) {
                            start = "";
                        }
                        try {
                            end = (jsonEvent.getJSONObject("end").getString("dateTime") != null) ? jsonEvent.getJSONObject("end").getString("dateTime").substring(0, 16).replace('T', ' ') : "";
                        } catch (JSONException e) {
                            end = "";
                        }
                        try {
                            location = (jsonEvent.getString("location") != null) ? jsonEvent.getString("location") : "";
                        } catch (JSONException e) {
                            location = "";
                        }
                        try {
                            id = (jsonEvent.getString("id") != null) ? jsonEvent.getString("id") : "";
                        } catch (JSONException e) {
                            id = "";
                        }
                        try {
                            description = (jsonEvent.getString("description") != null) ? jsonEvent.getString("description") : "";
                        } catch (JSONException e) {
                            description = "";
                        }
                        try {
                            creator_name = (jsonEvent.getJSONObject("creator").getString("displayName") != null) ? jsonEvent.getJSONObject("creator").getString("displayName") : "";
                        } catch (JSONException e) {
                            creator_name = "";
                        }
                        try {
                            creator_email = (jsonEvent.getJSONObject("creator").getString("email") != null) ? jsonEvent.getJSONObject("creator").getString("email") : "";
                        } catch (JSONException e) {
                            creator_email = "";
                        }
                        insertEvent(summary, htmlLink, start, end, location, id);
                        insertEventType(summary, description, creator_name, creator_email);
                    }
                    readEvents(list);
                } else {
                    Toast.makeText(Events.this, "You've got the last events", Toast.LENGTH_SHORT).show();
                    readEvents(list);
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
