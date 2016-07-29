package com.innopolis.maps.innomaps.maps;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.CustomScrollView;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.app.SearchableItem;
import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventCreatorAppointmentDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventCreatorDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventScheduleDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreator;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreatorAppointment;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventFavorable;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventSchedule;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.innopolis.maps.innomaps.events.Event;
import com.innopolis.maps.innomaps.events.MapBottomEventListAdapter;
import com.innopolis.maps.innomaps.events.TelegramOpenDialog;
import com.innopolis.maps.innomaps.network.NetworkController;
import com.innopolis.maps.innomaps.network.clientservercommunicationclasses.ClosestCoordinateWithDistance;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;
import java.util.regex.Pattern;


public class BottomSheet extends Fragment {
    protected BottomSheetBehavior mBottomSheetBehavior;
    protected GoogleMap map;
    protected List<LatLngFlr> latLngFlrList; //

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
    TextView typePoi;
    LinearLayout durationLayout;
    LinearLayout startLayout;

    List<Marker> markerList; //to store markers on map
    RadioGroup floorPicker;

    protected LatLngFlr closest = null;

    public void inSearchBottomList(SearchableItem item) {
        ((RadioButton) floorPicker.getChildAt(5 - item.getCoordinate().getFloor())).setChecked(true);

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
        typePoi = (TextView) scrollView.findViewById(R.id.typePoi);

        if (relatedLayout.getChildCount() != 0) {
            relatedLayout.removeView(relatedLayout.getChildAt(0));
        }

        idPoi.setText(String.format(Locale.ENGLISH, "%d", item.getId()));

        if (item.getType() == SearchableItem.SearchableItemType.STAIRS || item.getType() == SearchableItem.SearchableItemType.ELEVATOR)
            typePoi.setText(Constants.COORDINATE);
        else if (item.getType() == SearchableItem.SearchableItemType.EVENT)
            typePoi.setText(Constants.EVENT);
        else
            typePoi.setText(Constants.ROOM);

        if (item.getType() == SearchableItem.SearchableItemType.EVENT) {
            typeEvent(item.getId());
        } else {
            typeEventNon(item);
        }

        locationText.setText(StringUtils.join(locationArray, ", "));
        if (scrollView.getVisibility() == View.GONE) {
            scrollView.setVisibility(View.VISIBLE);
        }
        Utils.hideKeyboard(getActivity());
    }


    protected void typeEvent(int eventScheduleId) {

        EventDAO eventDAO = new EventDAO(this.getContext());
        EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(this.getContext());
        EventCreatorDAO eventCreatorDAO = new EventCreatorDAO(this.getContext());
        CoordinateDAO coordinateDAO = new CoordinateDAO(this.getContext());
        EventCreatorAppointmentDAO eventCreatorAppointmentDAO = new EventCreatorAppointmentDAO(this.getContext());

        durationLayout.setVisibility(View.VISIBLE);
        startLayout.setVisibility(View.VISIBLE);

        EventSchedule eventSchedule = (EventSchedule) eventScheduleDAO.findById(eventScheduleId);
        EventFavorable event = (EventFavorable) eventDAO.findById(eventSchedule.getEvent_id());
        Coordinate eventCoordinate = (Coordinate) coordinateDAO.findById(eventSchedule.getLocation_id());
        if (event != null && eventSchedule != null) {
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

            String description = event.getDescription();
            if (!Constants.EMPTY_STRING.equals(description))
                description += Constants.NEW_LINE + eventSchedule.getComment();
            else
                description += eventSchedule.getComment();

            String linksToEventCreatorsTelegramAccountsOrGroups = Constants.EMPTY_STRING;
            boolean groupLinkAdded = false;
            if (null != event.getLink() && !Constants.EMPTY_STRING.equals(event.getLink())) {
                linksToEventCreatorsTelegramAccountsOrGroups += Constants.GROUP_LINK;
                linksToEventCreatorsTelegramAccountsOrGroups += event.getLink();
                linksToEventCreatorsTelegramAccountsOrGroups += Constants.SPACE;
                groupLinkAdded = true;
            }

            List<EventCreatorAppointment> eventCreatorAppointments = eventCreatorAppointmentDAO.findByEventId(event.getId());

            for (int i = 0; i < eventCreatorAppointments.size(); i++) {
                EventCreator eventCreator = (EventCreator) eventCreatorDAO.findById(eventCreatorAppointments.get(i).getEvent_creator_id());
                if (groupLinkAdded && i == 0)
                    linksToEventCreatorsTelegramAccountsOrGroups += Constants.NEW_LINE;
                if (i == 0)
                    linksToEventCreatorsTelegramAccountsOrGroups += Constants.CONTACT;

                linksToEventCreatorsTelegramAccountsOrGroups += Constants.AT_SIGN;
                linksToEventCreatorsTelegramAccountsOrGroups += eventCreator.getTelegram_username();
                linksToEventCreatorsTelegramAccountsOrGroups += Constants.SPACE;
            }
            if (!Constants.EMPTY_STRING.equals(linksToEventCreatorsTelegramAccountsOrGroups) && !Constants.EMPTY_STRING.equals(description))
                linksToEventCreatorsTelegramAccountsOrGroups = Constants.NEW_LINE + linksToEventCreatorsTelegramAccountsOrGroups;

            descriptionText.setText(description + linksToEventCreatorsTelegramAccountsOrGroups).addLinks(links).build();
            relatedLayout.addView(descriptionText);
            LatLngFlr place = new LatLngFlr(eventCoordinate.getLatitude(), eventCoordinate.getLongitude(), eventCoordinate.getFloor());
            pinMarker(place, false);
            map.animateCamera(CameraUpdateFactory.newLatLng(place.getAndroidGMSLatLng()));
        }

        headerText.setText(event.getName());
        Date startDate = null;
        try {
            startDate = com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(eventSchedule.getStart_datetime());
        } catch (ParseException e) {
            Log.e(getContext().getString(R.string.maps), getContext().getString(R.string.time_parse_exception), e);
        }
        startText.setText(Utils.commonTime.format(startDate));
        durationText.setText(Utils.prettyTime.format(startDate));
        setPeekHeight(Constants.EVENT);
    }


    public void typeEventNon(SearchableItem item) {

        CoordinateDAO coordinateDAO = new CoordinateDAO(this.getContext());
        RoomDAO roomDAO = new RoomDAO(this.getContext());
        EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(this.getContext());
        EventDAO eventDAO = new EventDAO(this.getContext());

        Coordinate coordinate;
        if (item.getType() == SearchableItem.SearchableItemType.ELEVATOR || item.getType() == SearchableItem.SearchableItemType.STAIRS) // then id is coordinateId
            coordinate = (Coordinate) coordinateDAO.findById(item.getId());
        else { // then id is roomId
            Room room = (Room) roomDAO.findById(item.getId());
            coordinate = (Coordinate) coordinateDAO.findById(room.getCoordinate_id());
        }

        durationLayout.setVisibility(View.GONE);
        startLayout.setVisibility(View.GONE);

        // TODO: Discuss whether or not to keep events.Event class or rewrite everything based on db.tablesrepresentations.EventFavorable
        // (events.Event is good for GUI, but have many seemingly unassigned parameters)
        List<Event> events = new LinkedList<>();

        List<EventSchedule> eventSchedules = eventScheduleDAO.findUpcomingAndOngoingScheduledEventsInSpecifiedLocation(coordinate.getId());
        for (EventSchedule eventSchedule : eventSchedules) {
            Event eventForGUI = new Event();
            EventFavorable eventFavorable = (EventFavorable) eventDAO.findById(eventSchedule.getEvent_id());
            eventForGUI.setSummary(eventFavorable.getName());
            try {
                eventForGUI.setStart(com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(eventSchedule.getStart_datetime()));
            } catch (ParseException e) {
                Log.e(getContext().getString(R.string.maps), getContext().getString(R.string.date_parse_exception), e);
            }
            eventForGUI.setEventID(eventFavorable.getId());
            eventForGUI.setEventScheduleId(eventSchedule.getId());
            events.add(eventForGUI);
        }
        if (events.size() == 0) {
            TextView noEvents = new TextView(getContext());
            noEvents.setText(R.string.no_events);
            relatedLayout.addView(noEvents);
        } else {
            final ListView eventList = new ListView(getContext());
            eventList.setAdapter(new MapBottomEventListAdapter(getContext(), events, getActivity()));
            relatedLayout.addView(eventList);
        }
        headerText.setText(item.getName());
        LatLngFlr place = new LatLngFlr(coordinate.getLatitude(), coordinate.getLongitude(), coordinate.getFloor());
        pinMarker(place, false);
        map.animateCamera(CameraUpdateFactory.newLatLng(place.getAndroidGMSLatLng()));
        setPeekHeight(Constants.POI);
    }


    protected void clearMarkerList() {
        if (markerList != null && markerList.size() > 0) {
            for (Marker marker : markerList) {
                marker.remove();
            }
            markerList.clear();
        }
    }


    public void pinMarker(LatLngFlr latLngFlr, boolean firstTimeFlag) {
        clearMarkerList();
        MarkerOptions markerOptions = new MarkerOptions();
        String title = findClosestPOI(latLngFlr).firstKey();
        markerOptions.title(title);
        if (title != null && !"".equals(title) && firstTimeFlag) {
            boolean found = false;
            for (SearchableItem item : MainActivity.searchItems) {
                if (item.getName().toLowerCase().contains(title.toLowerCase()) && item.getCoordinate().equals(latLngFlr)) {
                    inSearchBottomList(item);
                    scrollView.setVisibility(View.VISIBLE);
                    found = true;
                }
            }
            if (!found)
                scrollView.setVisibility(View.GONE);
        } else {
            scrollView.setVisibility(View.GONE);
        }
        markerOptions.position(closest == null || closestDistance > 0.012 ? latLngFlr.getAndroidGMSLatLng() : closest.getAndroidGMSLatLng());
        Marker marker = map.addMarker(markerOptions);
        marker.showInfoWindow();
        markerList.add(marker);
    }

    // TODO: Fix. Works incorrectly, usually takes point from the first floor if it is not a room
    // It will work correctly when we will move to 3D coordinates and new database structure
    private TreeMap<String, LatLngFlr> findClosestPOI(LatLngFlr latLngFlr) {
        TreeMap<String, LatLngFlr> result = new TreeMap<>();
        if (latLngFlrList != null) {
            NetworkController networkController = new NetworkController();
            CoordinateDAO coordinateDAO = new CoordinateDAO(this.getContext());

            ClosestCoordinateWithDistance closestCoordinateWithDistance = null;

            // TODO: Find out if we need to get rid of latLngFlrList and check if latLng!=null instead
            if (!latLngFlrList.isEmpty()) {
                closestCoordinateWithDistance = networkController.findClosestPointFromGraph(latLngFlr.getLatitude(), latLngFlr.getLongitude(), latLngFlr.getFloor());
                closestDistance = closestCoordinateWithDistance.getDistance();
            } else
                closestDistance = Double.MAX_VALUE;

            if (closestDistance < 0.012) {
                closest = new LatLngFlr(closestCoordinateWithDistance.getCoordinate().getLatitude(),
                        closestCoordinateWithDistance.getCoordinate().getLongitude(), closestCoordinateWithDistance.getCoordinate().getFloor());
                Log.d(getContext().getString(R.string.distance), Constants.EMPTY_STRING + closestDistance);
                Coordinate closestCoordinate = coordinateDAO.findCoordinateByLatLngFlr(closest);
                if (null != closestCoordinate) {
                    String coordinateName = closestCoordinate.getName();
                    result.put(coordinateName, closest);
                    return result;
                }
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
                if (type.equals(Constants.EMPTY_STRING)) {
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
