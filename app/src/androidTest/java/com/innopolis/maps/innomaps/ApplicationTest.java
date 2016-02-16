package com.innopolis.maps.innomaps;

import android.database.sqlite.SQLiteDatabase;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.innopolis.maps.innomaps.app.DBHelper;
import com.innopolis.maps.innomaps.app.Events;
import com.innopolis.maps.innomaps.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.support.test.InstrumentationRegistry.getTargetContext;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest extends InstrumentationTestCase {

    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Events e;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        dbHelper = new DBHelper(new RenamingDelegatingContext(getTargetContext(), "test_"), "testdb");
        assertNotNull(dbHelper);
        database = dbHelper.getWritableDatabase();
        assertNotNull(database);
        e = new Events();
    }

    @After
    @Override
    public void tearDown() throws Exception {
        database.close();
        dbHelper.close();
        super.tearDown();
    }

    @Test
    public void JSONParsingCorrectToday() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        Utils.shiftDate(date);
        String urlString = "https://www.googleapis.com/calendar/v3/calendars/hvtusnfmqbg9u2p5rnc1rvhdfg@group.calendar.google.com"
                + "/events?timeMin="
                + dateFormat.format(date)
                + "T10%3A00%3A00-07%3A00&orderby=updated&sortorder=descending&futureevents=true"
                + "&alt=json&key=AIzaSyDli8qeotu4TGaEs5VKSWy15CDyl4cgZ-o";
        String res = Utils.doGetRequest(urlString);

        assertTrue("You haven't got any events. Wrong url or internet problems?", !res.equals(""));

        JSONObject dataJsonObj = new JSONObject(res);
        assertTrue("JSON Object is empty", dataJsonObj.length() != 0);

        JSONArray jsonObjects = dataJsonObj.getJSONArray("items");
        assertTrue("Events not found", jsonObjects != null);

        ArrayList<HashMap<String, String>> events = e.getEventsList(dataJsonObj, database);
        assertTrue("The number of JSON objects (" + jsonObjects.length()
                + ") differs from the number of events in the list"
                + " (" + events.size() + ")", events.size() == jsonObjects.length());

    }

    @Test
    public void JSONParsingCorrect() throws Exception {
        String urlString = "https://www.googleapis.com/calendar/v3/calendars/hvtusnfmqbg9u2p5rnc1rvhdfg@group.calendar.google.com"
                + "/events"
                + "?T10%3A00%3A00-07%3A00&futureevents=true"
                + "&alt=json&key=AIzaSyDli8qeotu4TGaEs5VKSWy15CDyl4cgZ-o";
        String res = Utils.doGetRequest(urlString);

        assertTrue("You haven't got any events. Wrong url or internet problems?", !res.equals(""));

        JSONObject dataJsonObj = new JSONObject(res);
        assertTrue("JSON Object is empty", dataJsonObj.length() != 0);

        JSONArray jsonObjects = dataJsonObj.getJSONArray("items");
        assertTrue("Events not found", jsonObjects != null);

        ArrayList<HashMap<String, String>> events = e.getEventsList(dataJsonObj, database);
        assertTrue("The number of JSON objects (" + jsonObjects.length()
                + ") differs from the number of events in the list"
                + " (" + events.size() + ")", events.size() == jsonObjects.length());
    }
}
