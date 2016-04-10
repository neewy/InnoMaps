package com.innopolis.maps.innomaps.maps;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableTextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.SearchableItem;
import com.innopolis.maps.innomaps.events.Event;
import com.innopolis.maps.innomaps.events.MapBottomEventListAdapter;
import com.innopolis.maps.innomaps.events.TelegramOpenDialog;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static com.innopolis.maps.innomaps.database.TableFields.DESCRIPTION;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_ID;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_TYPE;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.POI;
import static com.innopolis.maps.innomaps.database.TableFields.POI_NAME;
import static com.innopolis.maps.innomaps.database.TableFields.START;
import static com.innopolis.maps.innomaps.database.TableFields.SUMMARY;


public class BottomSheet extends Fragment {
    protected BottomSheetBehavior mBottomSheetBehavior;
    protected GoogleMap map;
    protected HashMap<String, String> latLngMap;

    private double distance, closestDistance;

    /*Bottom element, that is shown when search item is clicked*/
    NestedScrollView scrollView;

    /*These components are the part of scrollview elements*/
    TextView headerText;
    TextView locationText;
    TextView startText;
    TextView durationText;
    FrameLayout relatedLayout;
    TextView idPoi;
    LinearLayout durationLayout;
    LinearLayout startLayout;

    List<Marker> markerList; //to store markers on map
    RadioGroup floorPicker;

    protected LatLng closest = null;

    public void inSearchBottomList(SearchableItem item, View view) {
        ((RadioButton) floorPicker.getChildAt(5 - Integer.parseInt(item.getFloor().substring(0, 1)))).setChecked(true);
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        List<String> location = new LinkedList<>();
        if (item.getBuilding() != null) location.add(item.getBuilding());
        if (item.getFloor() != null) location.add(item.getFloor());
        if (item.getRoom() != null) location.add(item.getRoom());
        String[] locationArray = new String[location.size()];

        for (int i = 0; i < location.size(); i++) {
            locationArray[i] = location.get(i);
        }

        CheckedTextView text = (CheckedTextView) view.findViewById(R.id.name);
        headerText = (TextView) scrollView.findViewById(R.id.headerText);
        locationText = (TextView) scrollView.findViewById(R.id.locationText);
        startText = (TextView) scrollView.findViewById(R.id.startText);
        durationText = (TextView) scrollView.findViewById(R.id.durationText);
        relatedLayout = (FrameLayout) scrollView.findViewById(R.id.relatedLayout);
        idPoi = (TextView) scrollView.findViewById(R.id.idPoi);

        if (relatedLayout.getChildCount() != 0) {
            relatedLayout.removeView(relatedLayout.getChildAt(0));
        }

        if (item.getType().equals(EVENT)) {
            typeEvent(text.getText().toString());
            idPoi.setText(EVENT);
        } else {
            idPoi.setText(item.getId());
            typeEventNon(item.getId());
        }
        locationText.setText(StringUtils.join(locationArray, ", "));
        if (scrollView.getVisibility() == View.GONE) {
            scrollView.setVisibility(View.VISIBLE);
        }
        Utils.hideKeyboard(getActivity());
    }


    protected void typeEvent(String summary) {

        FrameLayout relatedLayout = (FrameLayout) scrollView.findViewById(R.id.relatedLayout);
        FloatingActionButton fab = (FloatingActionButton) scrollView.findViewById(R.id.goto_fab);

        String sqlQuery = "SELECT * FROM events INNER JOIN event_poi ON events.eventID = event_poi.eventID INNER JOIN poi ON event_poi.poi_id = poi._id WHERE events.summary=?";

        String name = "", latitude = "", longitude = "", startDateText = "", description = "";
        Date startDate = null;

        Cursor cursor = MarkersAdapter.database.rawQuery(sqlQuery, new String[]{summary});

        durationLayout.setVisibility(View.VISIBLE);
        startLayout.setVisibility(View.VISIBLE);

        if (cursor.moveToFirst()) {
            latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
            longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
            startDateText = cursor.getString(cursor.getColumnIndex(START));
            name = cursor.getString(cursor.getColumnIndex(SUMMARY));

            Cursor cursor_type = MarkersAdapter.database.query(EVENT_TYPE, null, "summary=?", new String[]{summary}, null, null, null);
            if (cursor_type.moveToFirst()) {
                description = cursor_type.getString(cursor_type.getColumnIndex(DESCRIPTION));
            }
            LinkableTextView descriptionText = new LinkableTextView(getContext());
            Link linkUsername = new Link(Pattern.compile("(@\\w+)"))
                    .setUnderlined(false)
                    .setTextColor(Color.parseColor("#D00000"))
                    .setTextStyle(Link.TextStyle.BOLD)
                    .setClickListener(new Link.OnClickListener() {
                        @Override
                        public void onClick(String text) {
                                    DialogFragment newFragment = new TelegramOpenDialog();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("dialogText", text);
                                    newFragment.setArguments(bundle);
                                    newFragment.show(getActivity().getSupportFragmentManager(), "Telegram");
                        }
                    });
            descriptionText.setTextSize(16);
            descriptionText.setPadding(0, 20, 10, 10);
            descriptionText.setText(description).addLink(linkUsername).build();
            relatedLayout.addView(descriptionText);
            try {
                startDate = Utils.googleTimeFormat.parse(startDateText);
            } catch (ParseException e) {
                Log.e("Maps", "Time parse exception", e);
            }
            LatLng place = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            pinMarker(place);
            map.animateCamera(CameraUpdateFactory.newLatLng(place));
        }

        headerText.setText(name);
        startText.setText(Utils.commonTime.format(startDate));
        durationText.setText(Utils.prettyTime.format(startDate));
        mBottomSheetBehavior.setPeekHeight(headerText.getLayout().getHeight() + fab.getHeight() + (int) getResources().getDisplayMetrics().density * 42);

    }


    public void typeEventNon(String poi_id) {

        FrameLayout relatedLayout = (FrameLayout) scrollView.findViewById(R.id.relatedLayout);
        FloatingActionButton fab = (FloatingActionButton) scrollView.findViewById(R.id.goto_fab);

        String sqlQuery = "SELECT * FROM poi LEFT OUTER JOIN event_poi on event_poi.poi_id = poi._id LEFT OUTER JOIN events on events.eventID = event_poi.eventID WHERE poi._id=?";

        Cursor cursor = MarkersAdapter.database.rawQuery(sqlQuery, new String[]{poi_id});
        String poi_name, latitude, longitude;

        durationLayout.setVisibility(View.GONE);
        startLayout.setVisibility(View.GONE);

        if (cursor.moveToFirst()) {
            poi_name = cursor.getString(cursor.getColumnIndex(POI_NAME));
            latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
            longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
            headerText.setText(poi_name);
            List<Event> events = new LinkedList<>();
            do {
                Event event = new Event();
                event.setSummary(cursor.getString(cursor.getColumnIndex(SUMMARY)));
                try {
                    if (cursor.getString(cursor.getColumnIndex(START)) != null) {
                        event.setStart(Utils.googleTimeFormat.parse(cursor.getString(cursor.getColumnIndex(START))));
                    }
                } catch (ParseException e) {
                    Log.e("Maps", "Date parse exception", e);
                }
                event.setEventID(cursor.getString(cursor.getColumnIndex(EVENT_ID)));
                if (event.getEventID() != null) {
                    events.add(event);
                }
            } while (cursor.moveToNext());
            if (events.size() == 0) {
                TextView noEvents = new TextView(getContext());
                noEvents.setText("There are no events");
                relatedLayout.addView(noEvents);
            } else {
                ListView eventList = new ListView(getContext());
                eventList.setAdapter(new MapBottomEventListAdapter(getContext(), events));
                relatedLayout.addView(eventList);
            }
            LatLng place = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            pinMarker(place);
            map.animateCamera(CameraUpdateFactory.newLatLng(place));
            mBottomSheetBehavior.setPeekHeight(headerText.getLayout().getHeight() + fab.getHeight() + (int) getResources().getDisplayMetrics().density * 42);
        }
    }


    protected void clearMarkerList() {
        if (markerList != null && markerList.size() > 0) {
            markerList.get(0).remove();
            markerList.clear();
        }
    }


    public void pinMarker(LatLng latLng) {
        clearMarkerList();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(findClosestPOI(latLng).firstKey());
        markerOptions.position(closest == null || closestDistance > 0.012 ? latLng : closest);
        Marker marker = map.addMarker(markerOptions);
        marker.showInfoWindow();
        markerList.add(marker);
    }

    private TreeMap<String, LatLng> findClosestPOI(LatLng latLng) {
        TreeMap<String, LatLng> result = new TreeMap<>();
        if (latLngMap != null) {
            Iterator iterator = latLngMap.entrySet().iterator();
            closestDistance = Double.MAX_VALUE;
            String lat = "", lng = "";
            while (iterator.hasNext()) {
                Map.Entry pair = (Map.Entry) iterator.next();
                distance = Utils.haversine(latLng.latitude, latLng.longitude, Double.parseDouble(pair.getKey().toString()), Double.parseDouble(pair.getValue().toString()));
                if (distance < closestDistance) {
                    closestDistance = distance;
                    lat = pair.getKey().toString();
                    lng = pair.getValue().toString();
                }
            }
            if (closestDistance < 0.012){
                closest = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                Log.d("DISTANCE: ", ""+closestDistance);
                String sqlQuery = "SELECT " + POI_NAME + " FROM " + POI + " WHERE " + LATITUDE + "=?" + " AND " + LONGITUDE + "=?";
                Cursor cursor = MarkersAdapter.database.rawQuery(sqlQuery, new String[]{lat, lng});
                cursor.moveToFirst();
                result.put(cursor.getString(cursor.getColumnIndex(POI_NAME)), closest);
                return result;
            }
        }
        closest = null;
        result.put("", null);
        return result;
    }
}
