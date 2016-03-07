package com.innopolis.maps.innomaps;

import android.test.ActivityInstrumentationTestCase2;

import com.innopolis.maps.innomaps.app.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.closeDrawer;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

////////////////////////////////////////////////////////////////////////////////



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
    @SuppressWarnings("unchecked")
    public void testOpenDrawer_clickItem() {
        openDrawer(R.id.drawer_layout);
        // Click an item in the drawer.
        int rowIndex = 2;

       // String rowContents = MainActivity.DRAWER_CONTENTS[rowIndex];

        String rowContents = "Events";
        onView(withText("Events")).perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //onData(withId(R.id.drawer_layout)).atPosition(2).perform(click());
       // onData(hasToString(startsWith("Events")))
        //        .inAdapterView(withId(R.id.drawer_layout))
         //       .perform(click());
        //onData(allOf(is(instanceOf(SimpleCursorAdapter.class)))).check(matches(isDisplayed()));
        //onData(allOf(is(instanceOf(String.class)).atPosition(2).perform(click()));
       // onData(allOf(withId(R.id.nav_event  ), is(rowContents))).perform(click());
       // onData(allOf(is(instanceOf(String.class)), is(rowContents))).perform(click());
       // onView(withText(R.id.nav_event)).check(doesNotExist());
       // onData(allOf(withId(R.id.menu_item_text), is(rowContents))).perform(click());
       //onData(withText("Events")).perform(click());
       // onData(anything()).inAdapterView(withContentDescription("Events")).atPosition(2).perform(click());
        //onData(allOf(is(instanceOf(String.class)), is("Events"))).perform(click());
        // clicking the item should close the drawer.
        //onView(withId(R.id.drawer_layout)).check(matches(isClosed()));
        // The text view will now display "You picked: Pickle"
        //onView(withText("Hello world!")).check(matches(isDisplayed()));
    }


}
