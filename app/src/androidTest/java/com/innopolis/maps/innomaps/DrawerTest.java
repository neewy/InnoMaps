package com.innopolis.maps.innomaps;

import android.test.ActivityInstrumentationTestCase2;

import com.innopolis.maps.innomaps.app.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.closeDrawer;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


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

// Drawer should not be open to start.
        onView(withId(R.id.drawer_layout)).check(matches(isClosed()));

        // onView(withId(R.id.buttonChoose)).perform(click());
        //onView(withId(R.id.textViewInfo)).check(matches(withText("hello")));
        openDrawer(R.id.drawer_layout);
        //closeDrawer(R.id.drawer_layout);
        // The drawer should now be open.
        onView(withId(R.id.drawer_layout)).check(matches(isOpen()));
        closeDrawer(R.id.drawer_layout);

    }
}
