package com.innopolis.maps.innomaps.events;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableTextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.database.SQLQueries;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import xyz.hanks.library.SmallBang;

import static com.innopolis.maps.innomaps.database.TableFields.BUILDING;
import static com.innopolis.maps.innomaps.database.TableFields.DESCRIPTION;
import static com.innopolis.maps.innomaps.database.TableFields.END;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT;
import static com.innopolis.maps.innomaps.database.TableFields.EVENTS;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_ID;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_ID_EQUAL;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_POI;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_TYPE;
import static com.innopolis.maps.innomaps.database.TableFields.FAV;
import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LINK;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.POI;
import static com.innopolis.maps.innomaps.database.TableFields.POI_ID;
import static com.innopolis.maps.innomaps.database.TableFields.ROOM;
import static com.innopolis.maps.innomaps.database.TableFields.START;
import static com.innopolis.maps.innomaps.database.TableFields.SUMMARY;
import static com.innopolis.maps.innomaps.database.TableFields.SUMMARY_EQUAL;
import static com.innopolis.maps.innomaps.database.TableFields.TYPE;
import static com.innopolis.maps.innomaps.database.TableFields._ID;


public class DetailedEvent extends Fragment {

    Context context;
    DBHelper dbHelper;
    SQLiteDatabase database;

    TextView eventName;
    TextView timeLeft;
    TextView location;
    TextView dateTime;
    LinkableTextView description;
    TextView duration;
    TextView noEventText;

    private static GoogleMap mMap;
    private static UiSettings mSettings;
    static SupportMapFragment mSupportMapFragment;
    private GroundOverlay imageOverlay;


    String summary, htmlLink, start, end, descriptionStr,
            eventID, building, floor, room, latitude, longitude, checked;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.detailed_menu_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_share:
                actionShare();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void actionShare() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType(context.getString(R.string.text_plain));
        i.putExtra(Intent.EXTRA_SUBJECT, eventName.getText());
        i.putExtra(Intent.EXTRA_TEXT, (String.format(context.getString(R.string.share_sms),
                eventName.getText(), dateTime.getText())));
        startActivity(i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.event_details);
        context = getActivity().getApplicationContext();
        View view = inflater.inflate(R.layout.detailed_event, container, false);
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        eventName = (TextView) view.findViewById(R.id.eventName_part);
        timeLeft = (TextView) view.findViewById(R.id.timeLeft);
        location = (TextView) view.findViewById(R.id.location);
        dateTime = (TextView) view.findViewById(R.id.dateTime);
        description = (LinkableTextView) view.findViewById(R.id.description);
        noEventText = (TextView) view.findViewById(R.id.noEventTextView);
        duration = (TextView) view.findViewById(R.id.duration);
        final CheckBox favCheckBox = (CheckBox) view.findViewById(R.id.favCheckBox);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String NULL = "";
            eventID = bundle.getString(EVENT_ID, NULL);
        }
        final Cursor cursor = database.query(EVENTS, null, EVENT_ID_EQUAL, new String[]{eventID}, null, null, null);
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
            Cursor cursor1 = database.query(EVENT_TYPE, null, SUMMARY_EQUAL, summaryArgs, null, null, null);
            cursor1.moveToFirst();
            int description = cursor1.getColumnIndex(DESCRIPTION);
            this.descriptionStr = cursor1.getString(description);


            cursor1.close();
        } while (cursor.moveToNext());
        cursor.close();
        Cursor locationC = database.rawQuery(SQLQueries.innerJoinLike(POI, EVENT_POI, _ID, POI_ID, EVENT_ID, eventID), null);
        if (locationC.moveToFirst()) {
            building = locationC.getString(locationC.getColumnIndex(BUILDING));
            floor = locationC.getString(locationC.getColumnIndex(FLOOR));
            room = locationC.getString(locationC.getColumnIndex(ROOM));
            latitude = locationC.getString(locationC.getColumnIndex(LATITUDE));
            longitude = locationC.getString(locationC.getColumnIndex(LONGITUDE));
        }
        database.close();
        locationC.close();

        Link.OnClickListener telegramLinkListener = new Link.OnClickListener() {
            @Override
            public void onClick(String text) {
                DialogFragment newFragment = new TelegramOpenDialog();
                Bundle bundle = new Bundle();
                bundle.putString(getContext().getString(R.string.dialog_text), text);
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), getContext().getString(R.string.telegram));
            }
        };

        Link linkUsername = new Link(Pattern.compile("(@\\w+)"))
                .setUnderlined(false)
                .setTextColor(Color.RED)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(telegramLinkListener);
        Link linkGroup = new Link(Pattern.compile("(https?://telegram\\.[\\S]+)"))
                .setUnderlined(false)
                .setTextColor(Color.BLUE)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(telegramLinkListener);


        List<Link> links = new ArrayList<>();
        links.add(linkUsername);
        links.add(linkGroup);


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
        duration.setText(String.format(context.getString(R.string.duration_text), String.valueOf(durationTime)));

        if (this.descriptionStr.length() != 0) {
            description
                    .setText(descriptionStr)
                    .addLinks(links)
                    .build();
        } else noEventText.setVisibility(View.VISIBLE);

        if (checked.equals("1")) {
            favCheckBox.setChecked(true);
        } else {
            favCheckBox.setChecked(false);
        }

        final SmallBang mSmallBang;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSmallBang = SmallBang.attach2Window(getActivity());
        } else {
            mSmallBang = null;
        }

        favCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SmallBang mSmallBang;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mSmallBang = SmallBang.attach2Window(getActivity());
                    mSmallBang.bang(favCheckBox);
                }
                String isFav = (favCheckBox.isChecked()) ? "1" : "0";
                ContentValues cv = new ContentValues();
                dbHelper = new DBHelper(context);
                database = dbHelper.getWritableDatabase();
                cv.put(FAV, isFav);
                database.update(EVENTS, cv, EVENT_ID_EQUAL, new String[]{eventID});
                dbHelper.close();
            }
        });


        FloatingActionButton fabButton = (FloatingActionButton) view.findViewById(R.id.fabButton);
        fabButton.bringToFront();

        fabButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new MapFragmentAskForRouteDialog();
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.dialogSource), context.getString(R.string.detailed_event));
                bundle.putString(TYPE, EVENT);
                bundle.putString(getString(R.string.destination), location.getText().toString());
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), context.getString(R.string.FindRoute));

            }
        });
        initializeMap(latitude, longitude);

        return view;
    }


    public void initializeMap(final String latitude, final String longitude) {
        final LatLng[] southWestAndNorthEast = new LatLng[2];
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
                    mSettings.setMapToolbarEnabled(false);
                    mSettings.setMyLocationButtonEnabled(false);


                    if (latitude != null && longitude != null) {
                        LatLng position = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 19));
                        mMap.addMarker(new MarkerOptions().position(position).title(room));
                        switch (floor) {
                            case "1floor":
                                southWestAndNorthEast[0] = new LatLng(55.752533, 48.742492);
                                southWestAndNorthEast[1] = new LatLng(55.754656, 48.744589);
                                putOverlayToMap(southWestAndNorthEast[0], southWestAndNorthEast[1], BitmapDescriptorFactory.fromResource(R.raw.ai6_floor1));
                                break;
                            case "2floor":
                                southWestAndNorthEast[0] = new LatLng(55.752828, 48.742661);
                                southWestAndNorthEast[1] = new LatLng(55.754597, 48.744469);
                                putOverlayToMap(southWestAndNorthEast[0], southWestAndNorthEast[1], BitmapDescriptorFactory.fromResource(R.raw.ai6_floor2));
                                break;
                            case "3floor":
                                southWestAndNorthEast[0] = new LatLng(55.752875, 48.742739);
                                southWestAndNorthEast[1] = new LatLng(55.754572, 48.744467);
                                putOverlayToMap(southWestAndNorthEast[0], southWestAndNorthEast[1], BitmapDescriptorFactory.fromResource(R.raw.ai6_floor3));
                                break;
                            case "4floor":
                                southWestAndNorthEast[0] = new LatLng(55.752789, 48.742711);
                                southWestAndNorthEast[1] = new LatLng(55.754578, 48.744569);
                                putOverlayToMap(southWestAndNorthEast[0], southWestAndNorthEast[1], BitmapDescriptorFactory.fromResource(R.raw.ai6_floor4));
                                break;
                            case "5floor":
                                southWestAndNorthEast[0] = new LatLng(55.752808, 48.743497);
                                southWestAndNorthEast[1] = new LatLng(55.753383, 48.744519);
                                putOverlayToMap(southWestAndNorthEast[0], southWestAndNorthEast[1], BitmapDescriptorFactory.fromResource(R.raw.ai6_floor5));
                                break;
                        }
                    }

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            DialogFragment newFragment = new MapFragmentAskForRouteDialog();
                            Bundle bundle = new Bundle();
                            bundle.putString(SUMMARY, summary);
                            newFragment.setArguments(bundle);
                            newFragment.show(getActivity().getSupportFragmentManager(), context.getString(R.string.FindRoute));
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        MainActivity.getInstance().trackScreenView(context.getString(R.string.detailed_event_fragment));
    }

    private void putOverlayToMap(LatLng southWest, LatLng northEast, BitmapDescriptor bitmapDescriptor) {
        if (imageOverlay != null) {
            imageOverlay.remove();
        }
        LatLngBounds latLngBounds;
        GroundOverlayOptions groundOverlayOptions;
        latLngBounds = new LatLngBounds(southWest, northEast);
        groundOverlayOptions = new GroundOverlayOptions();
        groundOverlayOptions.positionFromBounds(latLngBounds);
        groundOverlayOptions.image(bitmapDescriptor);
        imageOverlay = mMap.addGroundOverlay(groundOverlayOptions);
    }


}
