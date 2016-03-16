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
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;


public class ClickEventListTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public ClickEventListTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @SuppressWarnings("unchecked")
    public void testClickforEventList() {
        String[] namedAction = {"longClick()", "scrollTo()", "doubleClick()"};
        openDrawer(R.id.drawer_layout);
        onView(withText("Events")).perform(click());
        ListView listEvent = (ListView) getActivity().findViewById(R.id.eventList);
        Preconditions.checkNotNull(listEvent, "EventList is null");
        for (int i = 0; i < 5/*listEvent.getCount() - 1*/; i++) {
            for (int j = 0; j < 3; j++) {
                try {
                    switch (j) {
                        case 3: {
                            onData(anything()).inAdapterView(withId(R.id.eventList)).atPosition(i).perform(longClick());
                            break;
                        }
                        case 1: {
                            onData(anything()).inAdapterView(withId(R.id.eventList)).atPosition(i).perform(scrollTo());
                            break;
                        }
                        case 2: {
                            onData(anything()).inAdapterView(withId(R.id.eventList)).atPosition(i).perform(doubleClick());
                            break;
                        }
                        case 0: {
                            onData(anything()).inAdapterView(withId(R.id.eventList)).atPosition(i).perform(click());

                            break;
                        }
                    }
                    Thread.sleep(200);
                    Espresso.pressBack();
                } catch (Exception e) {
                    Log.d("MyTestLog", "Error action" + namedAction[j] + " is not exist for The list item" + i);
                }
            }
        }
    }

}