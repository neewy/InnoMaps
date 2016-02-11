package com.innopolis.maps.innomaps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Nikolay on 05.02.2016.
 */
public class DetailedEvent extends android.support.v4.app.Fragment {


    static Context context;
    DBHelper dbHelper;
    SQLiteDatabase database;

    TextView eventName;
    TextView timeLeft;
    TextView description;
    TextView organizer;

    private GoogleMap mMap;
    private UiSettings mSettings;
    SupportMapFragment mSupportMapFragment;

    String summary, htmlLink, start, end, descriptionStr, creator, telegram, eventID, latitude, longitude;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.event_desc, container, false);
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        eventName = (TextView) view.findViewById(R.id.eventName);
        timeLeft = (TextView) view.findViewById(R.id.timeLeft);
        description = (TextView) view.findViewById(R.id.description);
        organizer = (TextView) view.findViewById(R.id.organizer);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            eventID = bundle.getString("eventID", "");
        }
        Cursor cursor = database.query(DBHelper.TABLE1, null, "eventID=?", new String[]{eventID}, null, null, null);
        cursor.moveToFirst();
        do {
            int summary, htmlLink, start, end, location;
            summary = cursor.getColumnIndex(DBHelper.COLUMN_SUMMARY);
            htmlLink = cursor.getColumnIndex(DBHelper.COLUMN_LINK);
            start = cursor.getColumnIndex(DBHelper.COLUMN_START);
            end = cursor.getColumnIndex(DBHelper.COLUMN_END);
            this.summary = cursor.getString(summary);
            this.htmlLink = cursor.getString(htmlLink);
            this.start = cursor.getString(start);
            this.end = cursor.getString(end);
            String[] summaryArgs = new String[]{cursor.getString(summary)};
            Cursor cursor1 = database.query(DBHelper.TABLE2, null, "summary=?", summaryArgs, null, null, null);
            cursor1.moveToFirst();
            int description = cursor1.getColumnIndex("description");
            int creator_name = cursor1.getColumnIndex("creator_name");
            int telegram = cursor1.getColumnIndex("telegram");
            this.descriptionStr = cursor1.getString(description);
            this.creator = cursor1.getString(creator_name);
            this.telegram = cursor1.getString(telegram);
            cursor1.close();
        } while (cursor.moveToNext());
        cursor.close();
        String[] eventIDArgs = new String[]{eventID};
        Cursor locationC = database.query(DBHelper.TABLE3, null, "eventID=?", eventIDArgs, null, null, null);
        if (locationC.moveToFirst()) {
            latitude = locationC.getString(locationC.getColumnIndex(DBHelper.COLUMN_LATITIDE));
            longitude = locationC.getString(locationC.getColumnIndex(DBHelper.COLUMN_LONGITUDE));
        }
        database.close();
        eventName.setText(summary);
        timeLeft.setText(start);
        description.setText(descriptionStr);
        organizer.setText(creator);
        initializeMap(latitude, longitude);
        return view;
    }


    private void initializeMap(final String latitude, final String longitude) {
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapDesc);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapWrapper, mSupportMapFragment).commit();
        }
        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;

                    mSettings = mMap.getUiSettings();
                    mSettings.setMyLocationButtonEnabled(false);
                    mSettings.setZoomControlsEnabled(true);
                    mMap.setMyLocationEnabled(true);
                    if (latitude != null && longitude != null) {
                        LatLng position = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
                        mMap.addMarker(new MarkerOptions().position(position).title(summary));
                    }
                }
            });
        }
    }
}
