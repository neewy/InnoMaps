package com.innopolis.maps.innomaps;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;

import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.database.DBUpdater;
import com.innopolis.maps.innomaps.events.Event;
import com.innopolis.maps.innomaps.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;

@RunWith(AndroidJUnit4.class)
public class ApplicationTest extends InstrumentationTestCase {

    DBUpdater dbUpdater;
    Context context;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = new RenamingDelegatingContext(getTargetContext(), "test_");
        dbUpdater = new DBUpdater(context);
        assertNotNull(dbUpdater);
    }

    @After
    @Override
    public void tearDown() throws Exception {
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

        int insertedEvents = dbUpdater.populateDB(dataJsonObj);
        List<Event> events = DBHelper.readEvents(context, false);

        assertTrue("The number of events inserted into database (" + insertedEvents
                + ") differs from the number of events in the list"
                + " (" + events.size() + ")", events.size() == insertedEvents);

    }

    @Test
    public void JSONParsingCorrect() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(1420070400);
        String urlString = "https://www.googleapis.com/calendar/v3/calendars/hvtusnfmqbg9u2p5rnc1rvhdfg@group.calendar.google.com"
                + "/events?timeMin="
                + dateFormat.format(date)
                + "T10%3A00%3A00-07%3A00&futureevents=true"
                + "&alt=json&key=AIzaSyDli8qeotu4TGaEs5VKSWy15CDyl4cgZ-o";
        String res = Utils.doGetRequest(urlString);

        assertTrue("You haven't got any events. Wrong url or internet problems?", !res.equals(""));

        JSONObject dataJsonObj = new JSONObject(res);
        assertTrue("JSON Object is empty", dataJsonObj.length() != 0);

        JSONArray jsonObjects = dataJsonObj.getJSONArray("items");
        assertTrue("Events not found", jsonObjects != null);

        int insertedEvents = dbUpdater.populateDB(dataJsonObj);
        List<Event> events = DBHelper.readEvents(context, false);

        assertTrue("The number of events inserted into database (" + insertedEvents
                + ") differs from the number of events in the list"
                + " (" + events.size() + ")", events.size() == insertedEvents);
    }
}
