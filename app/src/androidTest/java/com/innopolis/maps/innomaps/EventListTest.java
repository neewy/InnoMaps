package com.innopolis.maps.innomaps;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.core.deps.guava.base.Preconditions;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.innopolis.maps.innomaps.app.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;


public class EventListTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public EventListTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @SuppressWarnings("unchecked")
    public void testFavEvent() {
        openDrawer(R.id.drawer_layout);
        onView(withText("Events")).perform(click());
        try {
            ListView listEvent = (ListView) getActivity().findViewById(R.id.eventList);
            Preconditions.checkNotNull(listEvent, "EventList is null");
            for (int i = 0; i < 5/*listEvent.getCount() - 1*/; i++) {
                onData(anything()).inAdapterView(withId(R.id.eventList)).atPosition(i).perform(click());
                onView(withId(R.id.eventName)).check(matches(isDisplayed()));
                TextView textName = (TextView) getActivity().findViewById(R.id.eventName);
                fieldIsEmpty((String) textName.getText());
                onView(withId(R.id.favCheckBox)).perform(click());
                Espresso.pressBack();
                Thread.sleep(100);
            }
            openDrawer(R.id.drawer_layout);
            onView(withText("Favourite")).perform(click());
            ListView listFavEvent = (ListView) getActivity().findViewById(R.id.eventList);
            Preconditions.checkNotNull(listFavEvent, "EventFavList is null");
            if (listFavEvent.getCount() != 5/*listEvent.getCount()*/)
                onView(withId(R.id.eventName)).check(matches(isDisplayed()));
            else Log.d("MyTestLog", "listFavEvent.getCount()==listEvent.getCount()");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void fieldIsEmpty(String textName) {
        int[] idTextView = {R.id.description, R.id.timeLeft, R.id.location, R.id.dateTime, R.id.duration};
        String[] nameFiled = {"description", "timeLeft", "location", "dateTime", "duration"};
        for (int i = 0; i < idTextView.length; i++) {
            TextView textView = (TextView) getActivity().findViewById(idTextView[i]);
            if (textView.getText().length() == 0)
                Log.d("MyTestLog", "name Event " + textName + " field" + nameFiled[i] + " is Empty ");
        }
    }
}