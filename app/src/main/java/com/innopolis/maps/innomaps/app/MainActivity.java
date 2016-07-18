package com.innopolis.maps.innomaps.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.DatabaseSync;
import com.innopolis.maps.innomaps.utils.AnalyticsTrackers;

import java.util.Date;


public class MainActivity extends MainActivityLogic
        implements NavigationView.OnNavigationItemSelectedListener {

    private static MainActivity mInstance;
    SharedPreferences sPref, prefs = null;
    ProgressDialog progressdialog;
    public static final int Progress_Dialog_Progress = 0;


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

        prefs = getSharedPreferences("firstRun", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            new FirstRunProgressDialog().execute();
        }

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

        // Do first run stuff here then set 'firstrun' as false

        new FirstRunProgressDialog().execute();
        sPref = getSharedPreferences(Constants.SYNC, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(Constants.LAST + Constants.TYPES + Constants.SYNC_DATE, Constants.DEFAULT_SYNC_DATE);
        ed.putString(Constants.LAST + Constants.MAP_UNITS + Constants.SYNC_DATE, Constants.DEFAULT_SYNC_DATE);
        ed.putString(Constants.LAST + Constants.EVENTS + Constants.SYNC_DATE, Constants.DEFAULT_SYNC_DATE);
        ed.putString(Constants.LAST + Constants.ASSIGNMENTS + Constants.SYNC_DATE, Constants.DEFAULT_SYNC_DATE);
        ed.putString(Constants.LAST + Constants.GENERAL + Constants.SYNC_DATE, Constants.DEFAULT_SYNC_DATE);
        ed.apply();

        // TODO: move first sync on whole forFirstRun method to activity where will be message like "Please, wait until the data will be loaded"
        firstDatabaseSync();
        // using the following line to edit/commit prefs
        prefs.edit().putBoolean("firstrun", false).apply();

    }

    void firstDatabaseSync() {
        DatabaseSync databaseSync = new DatabaseSync(this.getApplicationContext());
        databaseSync.performGeneralSyncWithServer();
        databaseSync.saveLastSyncDate(new Date(), DatabaseSync.syncTypes.GENERAL);
        Log.d(Constants.SYNC, Constants.SYNC_FINISHED_ON + com.innopolis.maps.innomaps.network.Constants.serverDateFormat.format(new Date()));
    }


    public class FirstRunProgressDialog extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            showDialog(Progress_Dialog_Progress);
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                forFirstRun();
                synchronized (this) {
                    int counter = 0;
                    while (counter < 25) {
                        this.wait(100);
                        counter++;
                        publishProgress("" + counter * 4);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onProgressUpdate(String... progress) {

            progressdialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {

            dismissDialog(Progress_Dialog_Progress);


            Toast.makeText(MainActivity.this, "Data Downloaded Successfully", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case Progress_Dialog_Progress:

                progressdialog = new ProgressDialog(MainActivity.this);
                progressdialog.setMessage("Downloading Data From Server...");
                progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressdialog.setCancelable(false);
                progressdialog.show();
                return progressdialog;

            default:

                return null;
        }
    }


}
