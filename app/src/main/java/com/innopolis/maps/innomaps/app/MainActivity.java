package com.innopolis.maps.innomaps.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.DatabaseSync;
import com.innopolis.maps.innomaps.utils.AnalyticsTrackers;


public class MainActivity extends MainActivityLogic
        implements NavigationView.OnNavigationItemSelectedListener {

    private static MainActivity mInstance;
    SharedPreferences sPref, prefs = null;

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
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(maps);
        else
            Log.e("NullPointException: ", getResources().getString(R.string.supportedActionBarIsNull));
        DBHelper dbHelper = new DBHelper(MainActivity.this);
        database = dbHelper.getReadableDatabase();

        forFirstRun();

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

        //Start synchronization service
        Intent intent = new Intent(this, DatabaseSync.class);
        startService(intent);

        //Init the database manager
        DatabaseManager.setHelper(this);
    }

    public static synchronized MainActivity getInstance() {
        return mInstance != null ? mInstance : new MainActivity();
    }

    void forFirstRun() {
        prefs = getSharedPreferences("firstRun", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            sPref = getSharedPreferences(Constants.SYNC, MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(Constants.LAST + Constants.TYPES + Constants.SYNC_DATE, Constants.DEFAULT_SYNC_DATE);
            ed.putString(Constants.LAST + Constants.MAP_UNITS + Constants.SYNC_DATE, Constants.DEFAULT_SYNC_DATE);
            ed.putString(Constants.LAST + Constants.EVENTS + Constants.SYNC_DATE, Constants.DEFAULT_SYNC_DATE);
            ed.apply();
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).apply();
        }
    }

}
