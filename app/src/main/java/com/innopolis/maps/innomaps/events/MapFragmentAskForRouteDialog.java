package com.innopolis.maps.innomaps.events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.app.SearchableItem;
import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EventScheduleDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventSchedule;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.innopolis.maps.innomaps.maps.LatLngFlr;
import com.innopolis.maps.innomaps.maps.MapsFragment;
import com.innopolis.maps.innomaps.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MapFragmentAskForRouteDialog extends DialogFragment {

    MapsFragment maps;
    MainActivity activity;
    RelativeLayout view;
    AppCompatSpinner floorSpinner;
    AppCompatSpinner roomSpinner;

    AppCompatButton mapSelect;
    AppCompatButton qrSelect;

    String sourceFloor;
    String sourceRoom;

    SharedPreferences sPref;
    String currentLocation;
    String currentLocationType;

    String source;
    String type;
    int id;

    LatLngFlr destination;

    private static List<SearchableItem> roomsAsSearchableItems;
    private static HashMap<String, SearchableItem> roomNameSearchableItemHashMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        setHasOptionsMenu(true);
        activity = (MainActivity) getActivity();
        currentLocation = sPref.getString(activity.getString(R.string.current_location), Constants.EMPTY_STRING);
        currentLocationType = sPref.getString(activity.getString(R.string.current_location_type), Constants.EMPTY_STRING);
    }

    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        maps = (MapsFragment) getActivity().getSupportFragmentManager().findFragmentByTag(activity.getString(R.string.maps));

        view = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.route_dialog, null);
        floorSpinner = (AppCompatSpinner) view.findViewById(R.id.floorSpinner);
        roomSpinner = (AppCompatSpinner) view.findViewById(R.id.roomSpinner);
        mapSelect = (AppCompatButton) view.findViewById(R.id.mapSelect);
        qrSelect = (AppCompatButton) view.findViewById(R.id.qrSelect);

        setRoomsFromSearchableItems();

        final Bundle arguments = getArguments();

        source = arguments.getString(activity.getString(R.string.dialogSource));
        type = arguments.getString(Constants.TYPE);
        id = Integer.parseInt(arguments.getString(Constants.ID));

        setDestination(getContext());

        RoomDAO roomDAO = new RoomDAO(getContext());

        List<Integer> floors;

        // TODO: Rewrite method to work with buildings, at the moment we only have university building which has id = 1
        floors = roomDAO.getFloorsListForBuilding(1);

        /* Populating roomsMap, where every key is the floor and the value is corresponding rooms */

        final HashMap<Integer, List<String>> roomsMap = populateRoomsHashMap(floors);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_row, floors);
        adapter.setDropDownViewResource(R.layout.spinner_row);
        floorSpinner.setAdapter(adapter);

        floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.row);
                ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_row, roomsMap.get(Integer.parseInt(textView.getText().toString())));
                roomAdapter.setDropDownViewResource(R.layout.spinner_row);
                sourceFloor = textView.getText().toString();
                roomSpinner.setAdapter(roomAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //not yet implemented
            }
        });

        roomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.row);
                sourceRoom = textView.getText().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //not yet implemented
            }
        });

        /*Saving user destination as his source location on next search
        * Room saving doesn't work due to SDK bug */
        saveUserDestination(arguments, getContext());


        mapSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (source.equals(activity.getString(R.string.detailed_event))) {
                    displayMapsFragment();
                }
                maps.allowSelection(MapFragmentAskForRouteDialog.this.getDialog(), destination);
            }
        });

        qrSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maps.openQrScanner(MapFragmentAskForRouteDialog.this.getDialog());
            }
        });

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.find_route)
                .setMessage(R.string.specify_location_dialog)
                .setView(view)
                .setPositiveButton(R.string.route, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (source.equals(activity.getString(R.string.detailed_event))) {
                            displayMapsFragment();
                        }

                        LatLngFlr start = roomNameSearchableItemHashMap.get(sourceRoom).getCoordinate();

                        maps.showRoute(start, destination);

                        MapFragmentAskForRouteDialog.this.getDialog().cancel();
                    }

                })
                .setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MapFragmentAskForRouteDialog.this.getDialog().cancel();
                    }
                })
                .create();
    }

    private void setDestination(Context context) {
        EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(context);
        RoomDAO roomDAO = new RoomDAO(context);
        CoordinateDAO coordinateDAO = new CoordinateDAO(context);

        Coordinate coordinate = null;

        switch (type) {
            case Constants.EVENT:
                EventSchedule eventSchedule = (EventSchedule) eventScheduleDAO.findById(id);
                coordinate = (Coordinate) coordinateDAO.findById(eventSchedule.getLocation_id());
                break;
            case Constants.ROOM:
                Room room = (Room) roomDAO.findById(id);
                coordinate = (Coordinate) coordinateDAO.findById(room.getCoordinate_id());
                break;
            case Constants.COORDINATE:
                coordinate = (Coordinate) coordinateDAO.findById(id);
                break;
        }

        if (null != coordinate)
            destination = new LatLngFlr(coordinate.getLatitude(), coordinate.getLongitude(), coordinate.getFloor());
    }

    public void saveUserDestination(Bundle arguments, Context context) {
        SharedPreferences.Editor ed = sPref.edit();
        EventScheduleDAO eventScheduleDAO = new EventScheduleDAO(context);
        RoomDAO roomDAO = new RoomDAO(context);
        CoordinateDAO coordinateDAO = new CoordinateDAO(context);

        if (currentLocationType.equals(Constants.EMPTY_STRING)) {
            ed.putString(activity.getString(R.string.current_location_type), type);
            ed.putString(activity.getString(R.string.current_location), arguments.getString(Constants.ID));
            ed.apply();
        } else {
            Coordinate coordinate;
            int prevId = Integer.parseInt(sPref.getString(activity.getString(R.string.current_location), Constants.EMPTY_STRING));
            switch (currentLocationType) {
                case Constants.EVENT:
                    EventSchedule eventSchedule = (EventSchedule) eventScheduleDAO.findById(prevId);
                    coordinate = (Coordinate) coordinateDAO.findById(eventSchedule.getLocation_id());
                    break;
                case Constants.ROOM:
                    Room room = (Room) roomDAO.findById(prevId);
                    coordinate = (Coordinate) coordinateDAO.findById(room.getCoordinate_id());
                    break;
                default: // Coordinate
                    coordinate = (Coordinate) coordinateDAO.findById(prevId);
                    break;
            }

            int floor = coordinate.getFloor();
            Utils.selectSpinnerItemByValue(floorSpinner, floor);
            ed.putString(activity.getString(R.string.current_location_type), type);
            ed.putString(activity.getString(R.string.current_location), arguments.getString(Constants.ID));
            ed.apply();
        }
    }

    public void displayMapsFragment() {
        activity.getSupportFragmentManager().popBackStackImmediate(activity.getString(R.string.maps), 0);
        activity.getSupportActionBar().setTitle(activity.getString(R.string.maps));
        activity.setActivityDrawerToggle();
        activity.highlightItemDrawer(activity.getString(R.string.maps));
    }

    private static HashMap<Integer, List<String>> populateRoomsHashMap(List<Integer> floors) {
        HashMap<Integer, List<String>> roomsMap = new LinkedHashMap<>();
        for (Integer floor : floors) {
            roomsMap.put(floor, new ArrayList<String>());
        }
        for (SearchableItem searchableItem : roomsAsSearchableItems) {
            roomsMap.get(searchableItem.getCoordinate().getFloor()).add(searchableItem.getName());
        }

        for (Integer floor : floors) {
            List<String> rooms = roomsMap.get(floor);
            Collections.sort(rooms);
            roomsMap.put(floor, rooms);
        }

        return roomsMap;
    }

    private static void setRoomsFromSearchableItems() {
        roomsAsSearchableItems = new ArrayList<>();
        roomNameSearchableItemHashMap = new LinkedHashMap<>();
        for (SearchableItem searchableItem : MainActivity.searchItems) {
            if (searchableItem.getType() != SearchableItem.SearchableItemType.EVENT && // Taking only rooms
                    searchableItem.getType() != SearchableItem.SearchableItemType.STAIRS &&
                    searchableItem.getType() != SearchableItem.SearchableItemType.ELEVATOR) {
                roomsAsSearchableItems.add(searchableItem);
                roomNameSearchableItemHashMap.put(searchableItem.getName(), searchableItem);
            }
        }
    }
}
