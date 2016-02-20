package com.innopolis.maps.innomaps.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * Created by Nikolay on 05.02.2016.
 */
public class DetailedEvent extends android.support.v4.app.Fragment {


    static Context context;
    DBHelper dbHelper;
    SQLiteDatabase database;

    TextView eventName;
    TextView timeLeft;
    TextView location;
    TextView dateTime;
    TextView description;
    TextView organizer;
    TextView duration;
    private GoogleMap mMap;
    private UiSettings mSettings;
    SupportMapFragment mSupportMapFragment;

    String summary, htmlLink, start, end, descriptionStr, creator, telegram, eventID, building, floor, room, latitude, longitude, checked;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.event_desc, container, false);
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        eventName = (TextView) view.findViewById(R.id.eventName);
        timeLeft = (TextView) view.findViewById(R.id.timeLeft);
        location = (TextView) view.findViewById(R.id.location);
        dateTime = (TextView) view.findViewById(R.id.dateTime);
        description = (TextView) view.findViewById(R.id.description);
        description.setMovementMethod(new ScrollingMovementMethod());
        organizer = (TextView) view.findViewById(R.id.organizer);
        duration = (TextView) view.findViewById(R.id.duration);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            eventID = bundle.getString("eventID", "");
        }
        final Cursor cursor = database.query(DBHelper.TABLE1, null, "eventID=?", new String[]{eventID}, null, null, null);
        cursor.moveToFirst();
        do {
            int summary, htmlLink, start, end, checked;
            summary = cursor.getColumnIndex(DBHelper.COLUMN_SUMMARY);
            htmlLink = cursor.getColumnIndex(DBHelper.COLUMN_LINK);
            start = cursor.getColumnIndex(DBHelper.COLUMN_START);
            end = cursor.getColumnIndex(DBHelper.COLUMN_END);
            checked = cursor.getColumnIndex(DBHelper.COLUMN_FAV);
            this.summary = cursor.getString(summary);
            this.htmlLink = cursor.getString(htmlLink);
            this.start = cursor.getString(start);
            this.end = cursor.getString(end);
            this.checked = cursor.getString(checked);
            String[] summaryArgs = new String[]{cursor.getString(summary)};
            Cursor cursor1 = database.query(DBHelper.TABLE2, null, "summary=?", summaryArgs, null, null, null);
            cursor1.moveToFirst();
            int description = cursor1.getColumnIndex("description");
            int creator_name = cursor1.getColumnIndex("creator_name");
            int telegram = cursor1.getColumnIndex(DBHelper.COLUMN_TELEGRAM_GROUP);
            this.descriptionStr = cursor1.getString(description);
            this.creator = cursor1.getString(creator_name);
            this.telegram = cursor1.getString(telegram);
            cursor1.close();
        } while (cursor.moveToNext());
        cursor.close();
        final String[] eventIDArgs = new String[]{eventID};
        Cursor locationC = database.query(DBHelper.TABLE3, null, "eventID=?", eventIDArgs, null, null, null);
        if (locationC.moveToFirst()) {
            building = locationC.getString(locationC.getColumnIndex(DBHelper.COLUMN_BUILDING));
            floor = locationC.getString(locationC.getColumnIndex(DBHelper.COLUMN_FLOOR));
            room = locationC.getString(locationC.getColumnIndex(DBHelper.COLUMN_ROOM));
            latitude = locationC.getString(locationC.getColumnIndex(DBHelper.COLUMN_LATITIDE));
            longitude = locationC.getString(locationC.getColumnIndex(DBHelper.COLUMN_LONGITUDE));
        }
        database.close();

        eventName.setText(summary);
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = Utils.googleTimeFormat.parse(start);
            endDate = Utils.googleTimeFormat.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeLeft.setText(Utils.prettyTime.format(startDate));
        String[] locationText = {building, floor, room};
        location.setText(StringUtils.join(Utils.clean(locationText), ", "));
        dateTime.setText(Utils.commonTime.format(startDate));
        Long durationTime = TimeUnit.MILLISECONDS.toMinutes(endDate.getTime() - startDate.getTime());
        duration.setText("Duration: " + String.valueOf(durationTime) + "min");
        description.setText(descriptionStr);
        organizer.setText(creator);
        if (telegram != null) {
            organizer.setTextColor(Color.BLUE);
            SpannableString content = new SpannableString(creator);
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            organizer.setText(content);
            organizer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment newFragment = new TelegramOpenDialog();
                    Bundle bundle = new Bundle();
                    bundle.putString("dialogText", creator);
                    bundle.putString("dialogUrl", telegram);
                    newFragment.setArguments(bundle);
                    newFragment.show(getActivity().getSupportFragmentManager(), "Telegram");
                }
            });
        }
        FabSpeedDial fabButton = (FabSpeedDial) view.findViewById(R.id.fabButton);
        fabButton.bringToFront();
        fabButton.setMenuListener(new SimpleMenuListenerAdapter() {

            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                if (checked.equals("1")) {
                    navigationMenu.getItem(0).setTitle("Unfavourite");
                } else {
                    navigationMenu.getItem(0).setTitle("Favourite");
                }
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.action_fav) {
                    database = dbHelper.getWritableDatabase();
                    Boolean isFav = (checked.equals("1"));
                    ContentValues cv = new ContentValues();
                    String res = (isFav) ? "0" : "1";
                    checked = res;
                    cv.put(DBHelper.COLUMN_FAV, res);
                    database.update(DBHelper.TABLE1, cv, "eventID = ?", new String[]{eventID});
                    database.close();
                    return true;
                } else if (id == R.id.action_map) {
                    return true;
                } else if (id == R.id.action_share) {
                    return true;
                }
                return false;
            }
        });
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
