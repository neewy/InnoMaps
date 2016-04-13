package com.innopolis.maps.innomaps;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.RelativeLayout;

import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.maps.MapsFragment;
import com.innopolis.maps.innomaps.utils.Utils;

public class FloorPickerTest extends ActivityInstrumentationTestCase2<MainActivity>{

    MainActivity activity;
    Fragment fragment;

    public FloorPickerTest() {
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

    @SmallTest
    public void testFloorPicker() throws Exception {
        assertTrue(((RelativeLayout.LayoutParams) activity.findViewById(R.id.floorPicker).getLayoutParams()).bottomMargin == (int) Utils.convertDpToPixel(74, activity));
    }
}
