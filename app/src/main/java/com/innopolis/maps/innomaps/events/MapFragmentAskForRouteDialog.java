package com.innopolis.maps.innomaps.events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.google.android.gms.maps.model.LatLng;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.MainActivity;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.database.SQLQueries;
import com.innopolis.maps.innomaps.maps.MapsFragment;
import com.innopolis.maps.innomaps.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static com.innopolis.maps.innomaps.database.SQLQueries.roomCursorQuery;
import static com.innopolis.maps.innomaps.database.SQLQueries.selectAllLike;
import static com.innopolis.maps.innomaps.database.SQLQueries.selectDistinct;
import static com.innopolis.maps.innomaps.database.SQLQueries.sourceEqualsDetailedEvent;
import static com.innopolis.maps.innomaps.database.SQLQueries.typeEqualsEventNone;
import static com.innopolis.maps.innomaps.database.TableFields.EMPTY;
import static com.innopolis.maps.innomaps.database.TableFields.EVENT;
import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.ID;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.NAME;
import static com.innopolis.maps.innomaps.database.TableFields.POI;
import static com.innopolis.maps.innomaps.database.TableFields.POI_NAME;
import static com.innopolis.maps.innomaps.database.TableFields.ROOM;
import static com.innopolis.maps.innomaps.database.TableFields.TYPE;
import static com.innopolis.maps.innomaps.database.TableFields._ID;

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

    String latitudeDest, longitudeDest;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        setHasOptionsMenu(true);
        currentLocation = sPref.getString(activity.getString(R.string.current_location), EMPTY);
        currentLocationType = sPref.getString(activity.getString(R.string.current_location_type), EMPTY);
        activity = (MainActivity) getActivity();
    }

    @NonNull
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        maps = (MapsFragment) getActivity().getSupportFragmentManager().findFragmentByTag(activity.getString(R.string.maps));

        view = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.route_dialog, null);
        floorSpinner = (AppCompatSpinner) view.findViewById(R.id.floorSpinner);
        roomSpinner = (AppCompatSpinner) view.findViewById(R.id.roomSpinner);
        mapSelect = (AppCompatButton) view.findViewById(R.id.mapSelect);
        qrSelect = (AppCompatButton) view.findViewById(R.id.qrSelect);

        DBHelper dbHelper = new DBHelper(getContext());
        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        final Bundle arguments = getArguments();

        source = arguments.getString(activity.getString(R.string.dialogSource));
        type = arguments.getString(TYPE);

        setDestination(arguments, database);

        List<String> floors = new LinkedList<>();
        final HashMap<String, List<String>> roomsMap = new LinkedHashMap<>();

        /* Populating roomsMap, where every key is the floor and the value is corresponding rooms */
        Cursor floorCursor = database.rawQuery(selectDistinct(POI, FLOOR), null);
        if (floorCursor.moveToFirst()) {
            do {
                String floor = floorCursor.getString(floorCursor.getColumnIndex(FLOOR));
                floors.add(floor);
            } while (floorCursor.moveToNext());
        }
        floorCursor.close();

        for (String floor : floors) {
            Cursor roomCursor = database.rawQuery(roomCursorQuery(POI, TYPE, ROOM, FLOOR, floor), null);
            List<String> rooms = new ArrayList<>();
            if (roomCursor.moveToFirst()) {
                do {
                    String room = roomCursor.getString(roomCursor.getColumnIndex(POI_NAME));
                    rooms.add(room);
                } while (roomCursor.moveToNext());
            }
            roomCursor.close();
            Collections.sort(rooms);
            roomsMap.put(floor, rooms);
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_row, floors);
        adapter.setDropDownViewResource(R.layout.spinner_row);
        floorSpinner.setAdapter(adapter);

        floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.row);
                ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_row, roomsMap.get(textView.getText()));
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
        saveUserDestination(arguments, database);


        mapSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (source.equals(activity.getString(R.string.detailed_event))) {
                    displayMapsFragment();
                }
                maps.allowSelection(MapFragmentAskForRouteDialog.this.getDialog(), new LatLng(Double.parseDouble(latitudeDest), Double.parseDouble(longitudeDest)));
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

                        String latitudeSource = "", longitudeSource = "";
                        Cursor cursorSource = database.rawQuery(roomCursorQuery(POI, FLOOR, sourceFloor, NAME, sourceRoom), null);
                        if (cursorSource.moveToFirst()) {
                            latitudeSource = cursorSource.getString(cursorSource.getColumnIndex(LATITUDE));
                            longitudeSource = cursorSource.getString(cursorSource.getColumnIndex(LONGITUDE));
                        }
                        cursorSource.close();

                        if (source.equals(activity.getString(R.string.detailed_event))) {
                            displayMapsFragment();
                        }

                        maps.showRoute(new LatLng(Double.parseDouble(latitudeSource), Double.parseDouble(longitudeSource)),
                                new LatLng(Double.parseDouble(latitudeDest), Double.parseDouble(longitudeDest)));

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

    private void setDestination(Bundle arguments, SQLiteDatabase database) {
        Cursor cursorDest = null;
        String[] destination = arguments.getString(activity.getString(R.string.destination)).split(", ");

        if (source.equals(activity.getString(R.string.maps_fragment))) {
            if (type.equals(EVENT)) {
                cursorDest = database.rawQuery(SQLQueries.typeEqualsEvent(destination), null);
            } else {
                String idPoi = arguments.getString(ID);
                cursorDest = database.rawQuery(typeEqualsEventNone(idPoi), null);
            }
        } else if (source.equals(activity.getString(R.string.detailed_event))) {
            cursorDest = database.rawQuery(sourceEqualsDetailedEvent(destination), null);
        }

        if (cursorDest.moveToFirst()) {
            latitudeDest = cursorDest.getString(cursorDest.getColumnIndex(LATITUDE));
            longitudeDest = cursorDest.getString(cursorDest.getColumnIndex(LONGITUDE));
        }
        cursorDest.close();

    }

    public void saveUserDestination(Bundle arguments, SQLiteDatabase database) {
        SharedPreferences.Editor ed = sPref.edit();
        if (currentLocationType.equals(EMPTY)) {
            ed.putString(activity.getString(R.string.current_location_type), type);
            if (type.equals(EVENT)) {
                ed.putString(activity.getString(R.string.current_location), arguments.getString(activity.getString(R.string.destination)));
            } else {
                ed.putString(activity.getString(R.string.current_location), arguments.getString(ID));
            }
            ed.apply();
        } else {
            if (currentLocationType.equals(EVENT)) {
                String[] currentLocationArray = currentLocation.split(", ");
                Utils.selectSpinnerItemByValue(floorSpinner, currentLocationArray[1]);
                ed.putString(activity.getString(R.string.current_location), arguments.getString(activity.getString(R.string.destination)));
                //Utils.selectSpinnerItemByValue(roomSpinner, currentLocationArray[2]);
            } else {
                Cursor cursor = database.rawQuery(selectAllLike(POI, _ID, currentLocation), null);
                String floor = null;
                //String room = null;
                if (cursor.moveToFirst()) {
                    floor = cursor.getString(cursor.getColumnIndex(FLOOR));
                    //room = cursor.getString(cursor.getColumnIndex(ROOM));
                }
                if (floor != null) Utils.selectSpinnerItemByValue(floorSpinner, floor);
                //if (room != null) Utils.selectSpinnerItemByValue(roomSpinner, room);
                ed.putString(activity.getString(R.string.current_location), arguments.getString(ID));
                cursor.close();
            }
            ed.apply();
        }
    }

    public void displayMapsFragment() {
        activity.getSupportFragmentManager().popBackStackImmediate(activity.getString(R.string.maps), 0);
        activity.getSupportActionBar().setTitle(activity.getString(R.string.maps));
        activity.setActivityDrawerToggle();
        activity.highlightItemDrawer(activity.getString(R.string.maps));
    }
}
