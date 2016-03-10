package com.innopolis.maps.innomaps;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.core.deps.guava.base.Preconditions;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.innopolis.maps.innomaps.app.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.closeDrawer;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;


public class DrawerTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public DrawerTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testEnterName() throws Exception {

        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));

        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        closeDrawer(R.id.drawer_layout);

    }

    @SuppressWarnings("unchecked")
    public void testOpenDrawer_clickItem() {
        openDrawer(R.id.drawer_layout);

        String rowContents = "Events";
        onView(withText(rowContents)).perform(click());
        try {

            ListView listEvent = (ListView) getActivity().findViewById(R.id.eventList);
            Preconditions.checkNotNull(listEvent, "EventList is null");
            for (int i = 0; i <listEvent.getCount()-1; i++) {
                onData(anything()).inAdapterView(withId(R.id.eventList)).atPosition(i).perform(click());
                Thread.sleep(100);
                //CHeak empty field
                onView(withId(R.id.eventName)).check(matches(isDisplayed()));
                onView(withId(R.id.description)).check(matches(isDisplayed()));
                onView(withId(R.id.timeLeft)).check(matches(isDisplayed()));
                onView(withId(R.id.location)).check(matches(isDisplayed()));
                onView(withId(R.id.organizer)).check(matches(isDisplayed()));
                Thread.sleep(100);
                Espresso.pressBack();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
