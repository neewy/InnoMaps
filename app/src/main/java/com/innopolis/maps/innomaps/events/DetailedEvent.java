package com.innopolis.maps.innomaps.events;

import android.content.Context;
import android.content.Intent;
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
import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.dataaccessobjects.BuildingAuxiliaryCoordinateDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.BuildingDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventCreatorAppointmentDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventCreatorDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventScheduleDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomTypeDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Building;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingAuxiliaryCoordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreator;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreatorAppointment;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventFavorable;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventSchedule;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomType;
import com.innopolis.maps.innomaps.maps.LatLngFlr;
import com.innopolis.maps.innomaps.utils.Utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import xyz.hanks.library.SmallBang;


public class DetailedEvent extends Fragment {

    Context context;

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


    String summary, htmlLink, start, end, descriptionStr, building, floorString, room;
    LatLngFlr coordinateLatLngFlr;
    int eventId, eventScheduleId;
    boolean checked;


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
            eventId = bundle.getInt(Constants.EVENT_ID);
            eventScheduleId = bundle.getInt(Constants.EVENT_SCHEDULE_ID);
        }

        final EventDAO eventDAO = new EventDAO(context);
        EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(context);
        CoordinateDAO coordinateDAO = new CoordinateDAO(context);
        EventCreatorAppointmentDAO eventCreatorAppointmentDAO = new EventCreatorAppointmentDAO(context);
        EventCreatorDAO eventCreatorDAO = new EventCreatorDAO(context);
        final EventFavorable event = (EventFavorable) eventDAO.findById(eventId);
        EventSchedule eventSchedule = (EventSchedule) eventScheduleDAO.findById(eventScheduleId);
        Coordinate eventsCoordinate = (Coordinate) coordinateDAO.findById(eventSchedule.getLocation_id());

        this.summary = event.getName();
        this.htmlLink = event.getLink();
        this.start = eventSchedule.getStart_datetime();
        this.end = eventSchedule.getEnd_datetime();
        this.checked = event.isFavourite();

        String eventDescription = event.getDescription();
        if (!Constants.EMPTY_STRING.equals(eventDescription))
            eventDescription += Constants.NEW_LINE + eventSchedule.getComment();
        else
            eventDescription += eventSchedule.getComment();

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
        if (!Constants.EMPTY_STRING.equals(linksToEventCreatorsTelegramAccountsOrGroups) && !Constants.EMPTY_STRING.equals(eventDescription))
            linksToEventCreatorsTelegramAccountsOrGroups = Constants.NEW_LINE + linksToEventCreatorsTelegramAccountsOrGroups;

        this.descriptionStr = eventDescription + linksToEventCreatorsTelegramAccountsOrGroups;

        building = getBuildingNameForEvent(eventsCoordinate.getId(), context);
        if (eventsCoordinate.getType_id() == 3 /*if type is ROOM*/ && null != eventsCoordinate.getName() && !Constants.EMPTY_STRING.equals(eventsCoordinate.getName()))
            room = eventsCoordinate.getName();
        else
            room = null;

        coordinateLatLngFlr = new LatLngFlr(eventsCoordinate.getLatitude(), eventsCoordinate.getLongitude(), eventsCoordinate.getFloor());
        floorString = Integer.toString(eventsCoordinate.getFloor()) + Constants.SPACE + Constants.FLOOR;

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
            startDate = com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(start);
            endDate = com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeLeft.setText(Utils.prettyTime.format(startDate));
        String[] locationText = {building, floorString, room};
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

        favCheckBox.setChecked(checked);

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
                EventFavorable updatedEvent = new EventFavorable(event.getId(), event.getName(), event.getDescription(), event.getLink(), event.getGcals_event_id(), event.getModified(), favCheckBox.isChecked());
                eventDAO.update(updatedEvent);
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
                bundle.putString(getString(R.string.type), Constants.EVENT);
                bundle.putString(getString(R.string.destination), location.getText().toString());
                bundle.putString(getString(R.string.id), Integer.toString(eventScheduleId));
                bundle.putInt(Constants.EVENT_ID, eventId);
                newFragment.setArguments(bundle);
                newFragment.show(getActivity().getSupportFragmentManager(), context.getString(R.string.FindRoute));

            }
        });
        initializeMap(coordinateLatLngFlr);

        return view;
    }


    public void initializeMap(final LatLngFlr coordinate) {
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


                    LatLng position = coordinate.getAndroidGMSLatLng();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 19));
                    mMap.addMarker(new MarkerOptions().position(position).title(room));
                    switch (coordinate.getFloor()) {
                        case 1:
                            southWestAndNorthEast[0] = new LatLng(55.752533, 48.742492);
                            southWestAndNorthEast[1] = new LatLng(55.754656, 48.744589);
                            putOverlayToMap(southWestAndNorthEast[0], southWestAndNorthEast[1], BitmapDescriptorFactory.fromResource(R.raw.ai6_floor1));
                            break;
                        case 2:
                            southWestAndNorthEast[0] = new LatLng(55.752828, 48.742661);
                            southWestAndNorthEast[1] = new LatLng(55.754597, 48.744469);
                            putOverlayToMap(southWestAndNorthEast[0], southWestAndNorthEast[1], BitmapDescriptorFactory.fromResource(R.raw.ai6_floor2));
                            break;
                        case 3:
                            southWestAndNorthEast[0] = new LatLng(55.752875, 48.742739);
                            southWestAndNorthEast[1] = new LatLng(55.754572, 48.744467);
                            putOverlayToMap(southWestAndNorthEast[0], southWestAndNorthEast[1], BitmapDescriptorFactory.fromResource(R.raw.ai6_floor3));
                            break;
                        case 4:
                            southWestAndNorthEast[0] = new LatLng(55.752789, 48.742711);
                            southWestAndNorthEast[1] = new LatLng(55.754578, 48.744569);
                            putOverlayToMap(southWestAndNorthEast[0], southWestAndNorthEast[1], BitmapDescriptorFactory.fromResource(R.raw.ai6_floor4));
                            break;
                        case 5:
                            southWestAndNorthEast[0] = new LatLng(55.752808, 48.743497);
                            southWestAndNorthEast[1] = new LatLng(55.753383, 48.744519);
                            putOverlayToMap(southWestAndNorthEast[0], southWestAndNorthEast[1], BitmapDescriptorFactory.fromResource(R.raw.ai6_floor5));
                            break;
                    }

                    mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            DialogFragment newFragment = new MapFragmentAskForRouteDialog();
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.SUMMARY, summary);
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


    private static String getBuildingNameForRoom(int buildingId, Context context) {
        BuildingDAO buildingDAO = new BuildingDAO(context);
        CoordinateDAO coordinateDAO = new CoordinateDAO(context);

        Building roomsBuilding = (Building) buildingDAO.findById(buildingId);
        Coordinate buildingsCoordinate = (Coordinate) coordinateDAO.findById(roomsBuilding.getCoordinate_id());
        if (Constants.EMPTY_STRING.equals(buildingsCoordinate.getName()))
            return null;
        else
            return buildingsCoordinate.getName();
    }

    private static String getBuildingNameForCoordinate(int coordinateId, Context context) {
        BuildingDAO buildingDAO = new BuildingDAO(context);
        CoordinateDAO coordinateDAO = new CoordinateDAO(context);
        BuildingAuxiliaryCoordinateDAO buildingAuxiliaryCoordinateDAO = new BuildingAuxiliaryCoordinateDAO(context);

        BuildingAuxiliaryCoordinate buildingAuxiliaryCoordinateWithSpecifiedCoordinateId = buildingAuxiliaryCoordinateDAO.getFirstRecordByCoordinateId(coordinateId);
        if (buildingAuxiliaryCoordinateWithSpecifiedCoordinateId == null)
            return null;
        else {
            Building coordinatesBuilding = (Building) buildingDAO.findById(buildingAuxiliaryCoordinateWithSpecifiedCoordinateId.getBuilding_id());
            Coordinate buildingsCoordinate = (Coordinate) coordinateDAO.findById(coordinatesBuilding.getCoordinate_id());
            return buildingsCoordinate.getName();
        }
    }

    public static String getBuildingNameForEvent(int coordinateId, Context context) {
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO(context);
        RoomDAO roomDAO = new RoomDAO(context);

        RoomType roomType = roomTypeDAO.findRoomTypeByName(Constants.DOOR);
        List<Integer> typeIds = new ArrayList<>();
        if (null != roomType)
            typeIds.add(roomType.getId());
        Room room = roomDAO.getFirstRecordByCoordinateIdExceptWithFollowingTypes(typeIds, coordinateId);
        if (room == null)
            return getBuildingNameForCoordinate(coordinateId, context);
        else {
            return getBuildingNameForRoom(room.getBuilding_id(), context);
        }
    }
}
