package com.innopolis.maps.innomaps;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.core.deps.guava.base.Preconditions;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.ListView;

import com.innopolis.maps.innomaps.app.MainActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
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


    @SuppressWarnings("unchecked")
    public void testEvent() {
        openDrawer(R.id.drawer_layout);

        String rowContents = "Events";
        onView(withText(rowContents)).perform(click());
        try {

            ListView listEvent = (ListView) getActivity().findViewById(R.id.eventList);
            Preconditions.checkNotNull(listEvent, "EventList is null");
            for (int i = 0; i < listEvent.getCount() - 1; i++) {
                onData(anything()).inAdapterView(withId(R.id.eventList)).atPosition(i).perform(click());
                Thread.sleep(100);
                //Cheak empty field
                onView(withId(R.id.eventName)).check(matches(isDisplayed()));
                onView(withId(R.id.description)).check(matches(isDisplayed()));
                onView(withId(R.id.timeLeft)).check(matches(isDisplayed()));
                onView(withId(R.id.location)).check(matches(isDisplayed()));
                Thread.sleep(100);
                Espresso.pressBack();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void testOpenDrawer() {
        String[] rowContents = {"Events", "Maps", "Favourite"};
        String[] namedAction = {"longClick()", "scrollTo()", "doubleClick()"};
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
        openDrawer(R.id.drawer_layout);
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        for (int i = 0; i < rowContents.length; i++) {

            onView(withText(rowContents[i])).perform(click());

            for (int j = 0; j < 3; j++) {
                openDrawer(R.id.drawer_layout);
                try {
                    switch (j) {
                        case 0: {
                            onView(withText(rowContents[i])).perform(longClick());
                            break;
                        }
                        case 1: {
                            onView(withText(rowContents[i])).perform(scrollTo());
                            break;
                        }
                        case 2: {
                            onView(withText(rowContents[i])).perform(doubleClick());
                            break;
                        }
                    }

                    Thread.sleep(100);
                } catch (Exception e) {
                    Log.d("MyTestLog", "action " + namedAction[j] + " is not exist for " + rowContents[i]);
                    openDrawer(R.id.drawer_layout);
                }
            }

        }
    }

}

