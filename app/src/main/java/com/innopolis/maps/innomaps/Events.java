package com.innopolis.maps.innomaps;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Events extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    static ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    SimpleAdapter adapter;
    FloatingActionButton getEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        listView = (ListView) findViewById(R.id.eventList);
        getEvents = (FloatingActionButton) findViewById(R.id.buttonEvents);
        getEvents.setOnClickListener(this);
        this.adapter = new SimpleAdapter(Events.this, list, R.layout.single_event, new String[]{"description", "summary", "updated"}, new int[]{R.id.descEvent, R.id.sumEvent, R.id.updatedEvent});
        listView.setAdapter(this.adapter);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(this, "Getting new events", Toast.LENGTH_SHORT).show();
        new ParseTask().execute();
    }

    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL("https://www.googleapis.com/calendar/v3/calendars/hvtusnfmqbg9u2p5rnc1rvhdfg@group.calendar.google.com/events?updatedMin=2016-1-18T10:00:00-07:00&orderby=updated&sortorder=descending&futureevents=true&alt=json&key=AIzaSyDli8qeotu4TGaEs5VKSWy15CDyl4cgZ-o");

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

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            //Log.d("TAG", strJson);

            JSONObject dataJsonObj = null;
            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray events = dataJsonObj.getJSONArray("items");

               /* JSONObject tst = events.getJSONObject(0);
                descriptionEvent = tst.getString("description");
                Log.d(LOG_TAG, "Description: " + descriptionEvent);
              */

                for (int i = 0; i < events.length(); i++) {
                    JSONObject jsonEvent = events.getJSONObject(i);
                    try {
                        HashMap<String, String> item = new HashMap<String, String>();
                        //item.put("id", jsonEvent.getString("id"));
                        item.put("description", jsonEvent.getString("description"));
                        item.put("summary", jsonEvent.getString("summary"));
                        item.put("updated", jsonEvent.getString("updated"));
                        list.add(item);
                        adapter.notifyDataSetChanged();
                    }
                    catch(JSONException e){
                        continue;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
