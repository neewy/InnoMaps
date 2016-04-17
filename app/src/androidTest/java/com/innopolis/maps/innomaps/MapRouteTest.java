package com.innopolis.maps.innomaps;

import android.support.test.annotation.UiThreadTest;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;

import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.maps.MapsFragment;

import org.hamcrest.Matchers;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class MapRouteTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity activity;
    MapsFragment fragment;

    public MapRouteTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        fragment = new MapsFragment();
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }

    @Test
    @UiThreadTest
    public void testSearch() {
        onView(withId(R.id.search))
                .perform(click());
        onView(withId(android.support.v7.appcompat.R.id.search_src_text)).perform(typeText("CTF"));
        onView(withText("CTF"))
                .inRoot(withDecorView(not(Matchers.is(getActivity().getWindow().getDecorView()))))
                .perform(click());
        onView(withId(R.id.goto_fab)).perform(click());

        onView(withId(R.id.floorSpinner)).perform(click());
        onView(withText("3floor")).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.roomSpinner)).perform(click());
        onView(withText("320")).inRoot(isPlatformPopup()).perform(click());
        onView(withText("Route")).perform(click());

        //assertTrue(fragment.mapRoute.hasCurrentPath);
    }



}
