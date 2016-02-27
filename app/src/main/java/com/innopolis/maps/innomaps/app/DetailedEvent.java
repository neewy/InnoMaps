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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;


public class DetailedEvent extends android.support.v4.app.Fragment {

    Context mContext;
    DBHelper dbHelper;
    SQLiteDatabase database;

    private TextView eventName;
    private TextView timeLeft;
    private TextView location;
    private TextView dateTime;
    private TextView description;
    private TextView organizer;
    private TextView duration;
    private GoogleMap mMap;
    private UiSettings mSettings;
    private SupportMapFragment mSupportMapFragment;
    private ActionBar mActionBar;

    String contactChecked, linkChecked, summary, htmlLink, start, end, descriptionStr, creator,
            telegram, telegramContact, eventID, building, floor, room, latitude, longitude, checked;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        mActionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true); //back button
        mActionBar.setTitle("Detailed");

        mContext = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.event_desc, container, false);

        dbHelper = new DBHelper(mContext);
        database = dbHelper.getWritableDatabase();

        /*All fields that are presented on layout event_desc*/
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
            int telegramContact = cursor1.getColumnIndex(DBHelper.COLUMN_TELEGRAM_CONTACT);

            this.descriptionStr = cursor1.getString(description);
            this.creator = cursor1.getString(creator_name);
            this.telegram = cursor1.getString(telegram);
            this.telegramContact = cursor1.getString(telegramContact);

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
        locationC.close();
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

        timeLeft.setText(Utils.prettyTime.format(startDate)); //human-readable date

        /*Inserting text into location field*/
        String[] locationText = {building, floor, room};
        location.setText(StringUtils.join(Utils.clean(locationText), ", "));

        dateTime.setText(Utils.commonTime.format(startDate));
        Long durationTime = TimeUnit.MILLISECONDS.toMinutes(endDate.getTime() - startDate.getTime());
        duration.setText("Duration: " + String.valueOf(durationTime) + "min"); //human-readable duration
        description.setText(descriptionStr);

        if (!telegramContact.equals("null") || !telegram.equals("null")) {
            organizer.setTextColor(Color.BLUE);

            //TODO: @Telse <- to clear up the code (use methods for the same piece of code)
            if (telegram.equals("null") && !telegramContact.equals("null")) {
                final String contactLink = "Contact: ";
                contactChecked = contactCutter(telegramContact);

                /*This setting enables text to look like ordinary link*/
                SpannableString content = new SpannableString(contactLink + contactChecked);
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                organizer.setText(content);

                /*Event is trigerred when you click on creator's name*/
                organizer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment newFragment = new TelegramOpenDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("dialogText", contactChecked);
                        bundle.putString("dialogUrl", telegramContact);
                        newFragment.setArguments(bundle);
                        newFragment.show(getActivity().getSupportFragmentManager(), "Telegram");
                    }
                });
            } else if (!telegram.equals("null")) {
                SpannableString content = new SpannableString("Group link");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                linkChecked = checkLink(telegram);
                organizer.setText(content);
                organizer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogFragment newFragment = new TelegramOpenDialog();
                        Bundle bundle = new Bundle();
                        String chatLink = "group of event";
                        bundle.putString("dialogText", chatLink);
                        bundle.putString("dialogUrl", linkChecked);
                        newFragment.setArguments(bundle);
                        newFragment.show(getActivity().getSupportFragmentManager(), "Telegram");
                    }
                });

            }
        }

        /*Floating action button, that is located between map an description*/
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

    private static String cutter(String string, int index) {
        String link = string.substring(0, index);
        return link;
    }

    private static String checkLink(String string) {
        int spaceIndex = string.indexOf(" ", 12);
        int paragraphIndex = string.indexOf("\n");
        int commaIndex = string.indexOf(",");
        if (paragraphIndex != -1) return cutter(string, paragraphIndex);
        else if (spaceIndex != -1) return cutter(string, spaceIndex);
        else if (commaIndex != -1) return cutter(string, commaIndex);
        else return string;
    }

    private static String contactCutter(String string) {
        String checkedString = string.substring(9);
        return checkedString;
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
                    mSettings.setMapToolbarEnabled(false);
                    mMap.setMyLocationEnabled(true);
                    if (latitude != null && longitude != null) {
                        LatLng position = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
                        mMap.addMarker(new MarkerOptions().position(position).title(summary));
                    }
                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            DialogFragment newFragment = new MapFragmentAskForRouteDialog();
                            Bundle bundle = new Bundle();
                            bundle.putString("summary", summary);
                            newFragment.setArguments(bundle);
                            newFragment.show(getActivity().getSupportFragmentManager(), "FindRoute");
                        }
                    });
                }
            });
        }
    }


}
