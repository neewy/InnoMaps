package com.innopolis.maps.innomaps.app;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
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

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends MainActivityLogic
        implements NavigationView.OnNavigationItemSelectedListener {

    private static MainActivity mInstance;
    private SharedPreferences sPref, prefs = null;
    private ProgressDialog progressdialog;
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

        int secondsToFirstSynchronization = 5;
        prefs = getSharedPreferences("firstRun", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            secondsToFirstSynchronization = 60;
            new FirstRunProgressDialog().execute();
        }

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

        //Init the database manager
        DatabaseManager.setHelper(this);

        //Start synchronization service
        int syncIntervalInSeconds = 150;
        startSynchronizationService(secondsToFirstSynchronization, syncIntervalInSeconds);
    }

    public static synchronized MainActivity getInstance() {
        return mInstance != null ? mInstance : new MainActivity();
    }

    void forFirstRun() {

        // Do first run stuff here then set 'firstrun' as false

        sPref = getSharedPreferences(Constants.SYNC, MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(Constants.LAST + Constants.TYPES + Constants.SYNC_DATE, Constants.DEFAULT_SYNC_DATE);
        ed.putString(Constants.LAST + Constants.MAP_UNITS + Constants.SYNC_DATE, Constants.DEFAULT_SYNC_DATE);
        ed.putString(Constants.LAST + Constants.EVENTS + Constants.SYNC_DATE, Constants.DEFAULT_SYNC_DATE);
        ed.putString(Constants.LAST + Constants.ASSIGNMENTS + Constants.SYNC_DATE, Constants.DEFAULT_SYNC_DATE);
        ed.putString(Constants.LAST + Constants.GENERAL + Constants.SYNC_DATE, Constants.DEFAULT_SYNC_DATE);
        ed.apply();


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
                        this.wait(25);
                        counter++;
                        publishProgress(Integer.toString(counter * 4));
                    }
                }
            } catch (InterruptedException e) {
                Log.e(Constants.LOG, e.getMessage(), e.fillInStackTrace());
            }
            return null;
        }


        protected void onProgressUpdate(String... progress) {

            progressdialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            firstDatabaseSync();

            dismissDialog(Progress_Dialog_Progress);


            Toast.makeText(MainActivity.this, "Data Downloaded Successfully", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == Progress_Dialog_Progress) {
            instantiateProgressDialog();
            return progressdialog;
        } else {
            return null;
        }
    }

    private void instantiateProgressDialog() {
        progressdialog = new ProgressDialog(MainActivity.this);
        progressdialog.setMessage("Downloading Data From Server...");
        progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressdialog.setCancelable(false);
        progressdialog.show();
    }

    private void startSynchronizationService(int secondsToFirstSynchronization, int syncIntervalInSeconds) {
        Intent myIntent = new Intent(this, DatabaseSync.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, secondsToFirstSynchronization); // first time
        long frequency = syncIntervalInSeconds * 1000; // in ms
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);
    }
}
