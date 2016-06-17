package com.innopolis.maps.innomaps.maps;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableTextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.CustomScrollView;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.app.SearchableItem;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.database.SQLQueries;
import com.innopolis.maps.innomaps.events.Event;
import com.innopolis.maps.innomaps.events.MapBottomEventListAdapter;
import com.innopolis.maps.innomaps.events.TelegramOpenDialog;
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.network.clientServerCommunicationClasses.ClosestCoordinateWithDistance;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static com.innopolis.maps.innomaps.database.TableFields.DESCRIPTION;
import static com.innopolis.maps.innomaps.database.TableFields.EMPTY;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT;
import static com.innopolis.maps.innomaps.database.TableFields.EVENTS;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_ID;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_POI;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT_TYPE;
import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.POI;
import static com.innopolis.maps.innomaps.database.TableFields.POI_ID;
import static com.innopolis.maps.innomaps.database.TableFields.POI_NAME;
import static com.innopolis.maps.innomaps.database.TableFields.START;
import static com.innopolis.maps.innomaps.database.TableFields.SUMMARY;
import static com.innopolis.maps.innomaps.database.TableFields.SUMMARY_EQUAL;
import static com.innopolis.maps.innomaps.database.TableFields._ID;


public class BottomSheet extends Fragment {
    protected BottomSheetBehavior mBottomSheetBehavior;
    protected GoogleMap map;
    protected HashMap<String, String> latLngMap;

    private double closestDistance;

    /*Bottom element, that is shown when search item is clicked*/
    CustomScrollView scrollView;

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

    public void inSearchBottomList(SearchableItem item, String text) {
        ((RadioButton) floorPicker.getChildAt(5 - Integer.parseInt(item.getFloor().substring(0, 1)))).setChecked(true);

        List<String> location = new LinkedList<>();
        if (item.getBuilding() != null) location.add(item.getBuilding());
        if (item.getFloor() != null) location.add(item.getFloor());
        if (item.getRoom() != null) location.add(item.getRoom());
        String[] locationArray = new String[location.size()];

        for (int i = 0; i < location.size(); i++) {
            locationArray[i] = location.get(i);
        }

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
            typeEvent(text);
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
        String sqlQuery = SQLQueries.innerJoinDouble(EVENTS, EVENT_POI, POI, EVENT_ID, EVENT_ID, _ID, POI_ID) +
                SQLQueries.where(EVENTS, SUMMARY_EQUAL);

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

            Cursor cursor_type = MarkersAdapter.database.query(EVENT_TYPE, null, SUMMARY_EQUAL, new String[]{summary}, null, null, null);
            if (cursor_type.moveToFirst()) {
                description = cursor_type.getString(cursor_type.getColumnIndex(DESCRIPTION));
            }
            cursor_type.close();
            cursor.close();
            LinkableTextView descriptionText = new LinkableTextView(getContext());

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

            if (relatedLayout.getChildCount() != 0) {
                relatedLayout.removeView(relatedLayout.getChildAt(0));
            }

            descriptionText.setTextSize(16);
            descriptionText.setPadding(0, 20, 10, 10);
            descriptionText.setText(description).addLinks(links).build();
            relatedLayout.addView(descriptionText);
            try {
                startDate = Utils.googleTimeFormat.parse(startDateText);
            } catch (ParseException e) {
                Log.e(getContext().getString(R.string.maps), getContext().getString(R.string.time_parse_exception), e);
            }
            LatLng place = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            pinMarker(place, false);
            map.animateCamera(CameraUpdateFactory.newLatLng(place));
        }

        headerText.setText(name);
        startText.setText(Utils.commonTime.format(startDate));
        durationText.setText(Utils.prettyTime.format(startDate));
        setPeekHeight(EVENT);
    }


    public void typeEventNon(String poi_id) {

        String sqlQuery = SQLQueries.typeEventNoneQuery(POI, EVENT_POI, EVENTS, _ID, POI_ID, EVENT_ID, EVENT_ID);

        Cursor cursor = MarkersAdapter.database.rawQuery(sqlQuery, new String[]{poi_id});
        String poi_name, latitude, longitude;

        durationLayout.setVisibility(View.GONE);
        startLayout.setVisibility(View.GONE);

        if (cursor.moveToFirst()) {
            poi_name = cursor.getString(cursor.getColumnIndex(POI_NAME));
            latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
            longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
            List<Event> events = new LinkedList<>();
            do {
                Event event = new Event();
                event.setSummary(cursor.getString(cursor.getColumnIndex(SUMMARY)));
                try {
                    if (cursor.getString(cursor.getColumnIndex(START)) != null) {
                        event.setStart(Utils.googleTimeFormat.parse(cursor.getString(cursor.getColumnIndex(START))));
                    }
                } catch (ParseException e) {
                    Log.e(getContext().getString(R.string.maps), getContext().getString(R.string.date_parse_exception), e);
                }
                event.setEventID(cursor.getString(cursor.getColumnIndex(EVENT_ID)));
                if (event.getEventID() != null) {
                    events.add(event);
                }
            } while (cursor.moveToNext());
            if (events.size() == 0) {
                TextView noEvents = new TextView(getContext());
                noEvents.setText(R.string.no_events);
                relatedLayout.addView(noEvents);
            } else {
                final ListView eventList = new ListView(getContext());
                eventList.setAdapter(new MapBottomEventListAdapter(getContext(), events, getActivity()));
                relatedLayout.addView(eventList);
            }
            cursor.close();
            headerText.setText(poi_name);
            LatLng place = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
            pinMarker(place, false);
            map.animateCamera(CameraUpdateFactory.newLatLng(place));
            setPeekHeight(POI);
        }
    }


    protected void clearMarkerList() {
        if (markerList != null && markerList.size() > 0) {
            for (Marker marker : markerList) {
                marker.remove();
            }
            markerList.clear();
        }
    }


    public void pinMarker(LatLng latLng, boolean firstTimeFlag) {
        clearMarkerList();
        MarkerOptions markerOptions = new MarkerOptions();
        String title = findClosestPOI(latLng).firstKey();
        markerOptions.title(title);
        if (title != null && !"".equals(title) && firstTimeFlag) {
            boolean found = false;
            for (SearchableItem item : ((MainActivity) getActivity()).searchItems) {
                if (item.getName().toLowerCase().contains(title.toLowerCase()) && item.getCoordinate().equals(latLng)) {
                    inSearchBottomList(item, title);
                    scrollView.setVisibility(View.VISIBLE);
                    found = true;
                }
            }
            if (!found)
                scrollView.setVisibility(View.GONE);
        } else {
            scrollView.setVisibility(View.GONE);
        }
        markerOptions.position(closest == null || closestDistance > 0.012 ? latLng : closest);
        Marker marker = map.addMarker(markerOptions);
        marker.showInfoWindow();
        markerList.add(marker);
    }

    // TODO: Fix. Works incorrectly, takes point from the first floor if it is not a room
    private TreeMap<String, LatLng> findClosestPOI(LatLng latLng) {
        TreeMap<String, LatLng> result = new TreeMap<>();
        if (latLngMap != null) {
            NetworkController networkController = new NetworkController();
            DBHelper dbHelper = new DBHelper(getContext());
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            Cursor dbCursor = database.rawQuery(SQLQueries.selectFloorForCoordinate(latLng), null);
            int floor = 1;
            if (dbCursor.moveToFirst())
                floor = Integer.parseInt(dbCursor.getString(dbCursor.getColumnIndex(FLOOR)).substring(0, 1));
            dbCursor.close();

            ClosestCoordinateWithDistance closestCoordinateWithDistance = null;

            // TODO: Find out if we need to get rid of latLngMap and check if latLng!=null instead
            if (!latLngMap.entrySet().isEmpty()) {
                closestCoordinateWithDistance = networkController.findClosestPointFromGraph(latLng.latitude, latLng.longitude, floor);
                closestDistance = closestCoordinateWithDistance.getDistance();
            } else
                closestDistance = Double.MAX_VALUE;

            if (closestDistance < 0.012) {
                closest = new LatLng(closestCoordinateWithDistance.getCoordinate().getLatitude(), closestCoordinateWithDistance.getCoordinate().getLongitude());
                Log.d(getContext().getString(R.string.distance), EMPTY + closestDistance);
                String sqlQuery = SQLQueries.distanceQuery(POI, POI_NAME, LATITUDE, LONGITUDE);
                Cursor cursor = MarkersAdapter.database.rawQuery(sqlQuery,
                        new String[]{String.valueOf(closestCoordinateWithDistance.getCoordinate().getLatitude()),
                                String.valueOf(closestCoordinateWithDistance.getCoordinate().getLongitude())});
                cursor.moveToFirst();
                result.put(cursor.getString(cursor.getColumnIndex(POI_NAME)), closest);
                cursor.close();
                return result;
            }
        }

        closest = null;
        result.put("", null);
        return result;
    }

    private void setPeekHeight(final String type) {
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                int height;
                int relatedLayoutHeight = relatedLayout.getHeight();
                int durationLayoutHeight = durationLayout.getHeight();
                int startLayoutHeight = startLayout.getHeight();
                int locationTextHeight = locationText.getHeight();
                if (type.equals(EVENT)) {
                    height = scrollView.getHeight() - (relatedLayoutHeight + durationLayoutHeight + startLayoutHeight + locationTextHeight + (int) Utils.convertDpToPixel(32, getContext()));
                } else {
                    height = scrollView.getHeight() - (relatedLayoutHeight + locationTextHeight + (int) Utils.convertDpToPixel(32, getContext()));
                }
                mBottomSheetBehavior.setPeekHeight(height);
                setFloorPickerMargin(scrollView.getVisibility() != View.VISIBLE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
                    scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    scrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    protected void setFloorPickerMargin(boolean hiddenScrollView) {
        int peekHeight = (hiddenScrollView) ? (int) Utils.convertDpToPixel(54, getContext()) : mBottomSheetBehavior.getPeekHeight();
        RelativeLayout.LayoutParams rp = (RelativeLayout.LayoutParams) floorPicker.getLayoutParams();
        rp.setMargins((int) Utils.convertDpToPixel(10, getContext()), 0, 0, peekHeight + (int) Utils.convertDpToPixel(20, getContext()));
        floorPicker.setLayoutParams(rp);
    }

}
