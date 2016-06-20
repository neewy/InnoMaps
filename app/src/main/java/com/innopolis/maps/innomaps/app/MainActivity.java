package com.innopolis.maps.innomaps.app;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.utils.AnalyticsTrackers;


public class MainActivity extends MainActivityLogic
        implements NavigationView.OnNavigationItemSelectedListener {

    private static MainActivity mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeView();

        routeButtonClickListener();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mInstance = this;

        setToggle(toolbar);

        initializeNavigationView();
        initializeFragment();
        if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle(maps);
        else Log.e("NullPointException: ", getResources().getString(R.string.supportedActionBarIsNull));
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        database = dbHelper.getReadableDatabase();

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }

    public static synchronized MainActivity getInstance() {
        return mInstance!=null?mInstance:new MainActivity();
    }

}
