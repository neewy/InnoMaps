package com.innopolis.maps.innomaps.events;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import xyz.hanks.library.SmallBang;

import static com.innopolis.maps.innomaps.database.TableFields.*;


public class DetailedEvent extends Fragment {

    Context context;
    DBHelper dbHelper;
    SQLiteDatabase database;

    TextView eventName;
    TextView timeLeft;
    TextView location;
    TextView dateTime;
    TextView description;
    TextView organizer;
    TextView duration;


    private static GoogleMap mMap;
    private static UiSettings mSettings;
    static SupportMapFragment mSupportMapFragment;


    final private String NULL = "";

    String contactChecked, linkChecked, summary, htmlLink, start, end, descriptionStr, creator,
            telegram, telegramContact, eventID, building, floor, room, latitude, longitude, checked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.detailed_menu_toolbar, menu);
        MenuItem item = menu.findItem(R.id.toolbar_share);
        ShareActionProvider shareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        //Consider changing content for relevant share information
        shareIntent.putExtra(Intent.EXTRA_TEXT, (eventName.getText() + " begins in " + dateTime.getText()+". Join us!"));
        shareAction.setShareIntent(shareIntent);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        final CheckBox favCheckBox = (CheckBox) view.findViewById(R.id.favCheckBox);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            eventID = bundle.getString("eventID", "");
        }
        final Cursor cursor = database.query(EVENTS, null, "eventID=?", new String[]{eventID}, null, null, null);
        cursor.moveToFirst();
        do {
            int summary, htmlLink, start, end, checked;
            summary = cursor.getColumnIndex(SUMMARY);
            htmlLink = cursor.getColumnIndex(LINK);
            start = cursor.getColumnIndex(START);
            end = cursor.getColumnIndex(END);
            checked = cursor.getColumnIndex(FAV);
            this.summary = cursor.getString(summary);
            this.htmlLink = cursor.getString(htmlLink);
            this.start = cursor.getString(start);
            this.end = cursor.getString(end);
            this.checked = cursor.getString(checked);
            String[] summaryArgs = new String[]{cursor.getString(summary)};
            Cursor cursor1 = database.query(EVENT_TYPE, null, "summary=?", summaryArgs, null, null, null);
            cursor1.moveToFirst();
            int description = cursor1.getColumnIndex("description");
            int creator_name = cursor1.getColumnIndex("creator_name");
            int telegram = cursor1.getColumnIndex(TELEGRAM_GROUP);
            int telegramContact = cursor1.getColumnIndex(TELEGRAM_CONTACT);
            this.descriptionStr = cursor1.getString(description);
            this.creator = cursor1.getString(creator_name);
            this.telegram = cursor1.getString(telegram);
            this.telegramContact = cursor1.getString(telegramContact);

            cursor1.close();
        } while (cursor.moveToNext());
        cursor.close();
        final String[] eventIDArgs = new String[]{eventID};
        Cursor locationC = database.query(LOCATION, null, "eventID=?", eventIDArgs, null, null, null);
        if (locationC.moveToFirst()) {
            building = locationC.getString(locationC.getColumnIndex(BUILDING));
            floor = locationC.getString(locationC.getColumnIndex(FLOOR));
            room = locationC.getString(locationC.getColumnIndex(ROOM));
            latitude = locationC.getString(locationC.getColumnIndex(LATITUDE));
            longitude = locationC.getString(locationC.getColumnIndex(LONGITUDE));
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

        if (!telegramContact.equals(NULL) || !telegram.equals(NULL)) {
            organizer.setTextColor(Color.BLUE);

            if (telegram.equals(NULL) && !telegramContact.equals(NULL)) {
                contactChecked = checkContact(telegramContact);
                SpannableString content = new SpannableString(contactChecked);
                telegramTransfer(content, contactChecked, contactChecked);

            } else if (!telegram.equals(NULL)) {
                SpannableString content = new SpannableString("Group link");
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                final String chatLink = "group of event";
                linkChecked = checkLink(telegram);
                organizer.setText(content);
                telegramTransfer(content, chatLink, linkChecked);
            }
        }

        if (checked.equals("1")) {
            favCheckBox.setChecked(true);
        } else {
            favCheckBox.setChecked(false);
        }
        final SmallBang mSmallBang = SmallBang.attach2Window(getActivity());

        favCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmallBang.bang(favCheckBox);
                String isFav = (favCheckBox.isChecked()) ? "1" : "0";
                ContentValues cv = new ContentValues();
                dbHelper = new DBHelper(context);
                database = dbHelper.getWritableDatabase();
                cv.put(FAV, isFav);
                database.update(EVENTS, cv, "eventID = ?", new String[]{eventID});
                dbHelper.close();
            }
        });


        FloatingActionButton fabButton = (FloatingActionButton) view.findViewById(R.id.fabButton);
        fabButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MapFragmentAskForRouteDialog();
                Bundle bundle = new Bundle();
                bundle.putString("summary", summary);
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), "FindRoute");

            }
        });
        initializeMap(latitude, longitude);
        return view;
    }


    private static String cutter(String string, int checkIndex) {
        String link = string.substring(0, checkIndex);
        return link;
    }


    private static String checkLink(String string) {
        int spaceIndex = string.indexOf(" ", 12); //except "Group chat: "
        int paragraphIndex = string.indexOf("\n");
        int commaIndex = string.indexOf(",");
        if (spaceIndex != -1) return cutter(string, spaceIndex);
        else if (paragraphIndex != -1) return cutter(string, paragraphIndex);
        else if (commaIndex != -1) return cutter(string, commaIndex);
        else return string;
    }


    private static String checkContact(String string) {
        String checkContact = string.substring(9);
        return checkContact;  //except "Contact: "
    }

    private void telegramTransfer(SpannableString content, final String dialogText, final String telegramLink) {
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        organizer.setText(content);
        organizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TelegramOpenDialog();
                Bundle bundle = new Bundle();
                bundle.putString("dialogText", dialogText);
                bundle.putString("dialogUrl", telegramLink);
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), "Telegram");
            }
        });
    }

    public void initializeMap(final String latitude, final String longitude) {
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
                    mMap.getUiSettings().setMapToolbarEnabled(false);
                    mSettings = mMap.getUiSettings();
                    mSettings.setMyLocationButtonEnabled(false);
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
