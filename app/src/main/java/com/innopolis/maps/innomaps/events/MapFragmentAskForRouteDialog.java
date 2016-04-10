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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.maps.MapsFragment;
import com.innopolis.maps.innomaps.database.DBHelper;
import com.innopolis.maps.innomaps.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.NULL;
import static com.innopolis.maps.innomaps.database.TableFields.POI_NAME;

public class MapFragmentAskForRouteDialog extends DialogFragment {

    MapsFragment maps;

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
        currentLocation = sPref.getString("currentLocation", NULL);
        currentLocationType = sPref.getString("currentLocationType", NULL);
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        maps = (MapsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("Maps");

        view = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.route_dialog, null);
        floorSpinner = (AppCompatSpinner) view.findViewById(R.id.floorSpinner);
        roomSpinner = (AppCompatSpinner) view.findViewById(R.id.roomSpinner);
        mapSelect = (AppCompatButton) view.findViewById(R.id.mapSelect);
        qrSelect = (AppCompatButton) view.findViewById(R.id.qrSelect);

        DBHelper dbHelper = new DBHelper(getContext());
        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        final Bundle arguments = getArguments();

        source = arguments.getString("dialogSource");
        type = arguments.getString("type");

        setDestination(arguments, database);

        List<String> floors = new LinkedList<>();
        final HashMap<String, List<String>> roomsMap = new LinkedHashMap<>();

        /* Populating roomsMap, where every key is the floor and the value is corresponding rooms */
        Cursor floorCursor = database.rawQuery("SELECT DISTINCT floor FROM poi", null);
        if (floorCursor.moveToFirst()) {
            do {
                String floor = floorCursor.getString(floorCursor.getColumnIndex(FLOOR));
                floors.add(floor);
            } while (floorCursor.moveToNext());
        }

        for (String floor : floors) {
            Cursor roomCursor = database.rawQuery("SELECT * FROM poi WHERE type like '%room%' and floor like '%" + floor + "%'", null);
            List<String> rooms = new ArrayList<>();
            if (roomCursor.moveToFirst()) {
                do {
                    String room = roomCursor.getString(roomCursor.getColumnIndex(POI_NAME));
                    rooms.add(room);
                } while (roomCursor.moveToNext());
            }
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
                ArrayAdapter<String> roomAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, roomsMap.get(textView.getText()));
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
                maps.allowSelection(MapFragmentAskForRouteDialog.this.getDialog(), new LatLng(Double.parseDouble(latitudeDest), Double.parseDouble(longitudeDest)));
            }
        });

        qrSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maps.openQrScanner(MapFragmentAskForRouteDialog.this.getDialog(), new LatLng(Double.parseDouble(latitudeDest), Double.parseDouble(longitudeDest)));
            }
        });

        return new AlertDialog.Builder(getContext())
                .setTitle("Find route")
                .setMessage("Please specify your location")
                .setView(view)
                .setPositiveButton("Route", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String source = arguments.getString("dialogSource");

                        String latitudeSource = "", longitudeSource = "";
                        Cursor cursorSource = database.rawQuery("SELECT * FROM poi WHERE floor LIKE '%" + sourceFloor + "%' and name LIKE '%" + sourceRoom + "%'", null);
                        if (cursorSource.moveToFirst()) {
                            latitudeSource = cursorSource.getString(cursorSource.getColumnIndex(LATITUDE));
                            longitudeSource = cursorSource.getString(cursorSource.getColumnIndex(LONGITUDE));
                        }

                        maps.showRoute(new LatLng(Double.parseDouble(latitudeSource), Double.parseDouble(longitudeSource)),
                                new LatLng(Double.parseDouble(latitudeDest), Double.parseDouble(longitudeDest)));

                        if (source.equals("DetailedEvent")) {
                            maps.showRoute(new LatLng(Double.parseDouble(latitudeSource), Double.parseDouble(longitudeSource)),
                                    new LatLng(Double.parseDouble(latitudeDest), Double.parseDouble(longitudeDest)));
                            getActivity().getSupportFragmentManager().popBackStackImmediate("Maps", 0);
                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Maps");
                        }

                        MapFragmentAskForRouteDialog.this.getDialog().cancel();
                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MapFragmentAskForRouteDialog.this.getDialog().cancel();
                    }
                })
                .create();
    }

    private void setDestination(Bundle arguments, SQLiteDatabase database) {
        Cursor cursorDest = null;
        String[] destination = arguments.getString("destination").split(", ");

        if (source.equals("MapsFragment")) {
            if (type.equals("event")) {
                cursorDest = database.rawQuery("SELECT * FROM poi WHERE building LIKE '%" + destination[0] + "%' and floor LIKE '%" + destination[1] + "%' and room LIKE '%" + destination[2] + "%'", null);
            } else {
                String idPoi = arguments.getString("id");
                cursorDest = database.rawQuery("SELECT * FROM poi WHERE _id LIKE '" + idPoi + "'", null);
            }
        } else if (source.equals("DetailedEvent")) {
            cursorDest = database.rawQuery("SELECT * FROM poi WHERE building LIKE '%" + destination[0] + "%' and floor LIKE '%" + destination[1] + "%' and room LIKE '%" + destination[2] + "%'", null);
        }

        if (cursorDest.moveToFirst()) {
            latitudeDest = cursorDest.getString(cursorDest.getColumnIndex(LATITUDE));
            longitudeDest = cursorDest.getString(cursorDest.getColumnIndex(LONGITUDE));
        }
        cursorDest.close();

    }

    public void saveUserDestination(Bundle arguments, SQLiteDatabase database) {
        SharedPreferences.Editor ed = sPref.edit();
        if (currentLocationType.equals(NULL)) {
            ed.putString("currentLocationType", type);
            if (type.equals("event")) {
                ed.putString("currentLocation", arguments.getString("destination"));
            } else {
                ed.putString("currentLocation", arguments.getString("id"));
            }
            ed.apply();
        } else {
            if (currentLocationType.equals("event")) {
                String[] currentLocationArray = currentLocation.split(", ");
                Utils.selectSpinnerItemByValue(floorSpinner, currentLocationArray[1]);
                ed.putString("currentLocation", arguments.getString("destination"));
                //Utils.selectSpinnerItemByValue(roomSpinner, currentLocationArray[2]);
            } else {
                Cursor cursor = database.rawQuery("SELECT * FROM poi WHERE _id LIKE '" + currentLocation + "'", null);
                String floor = null;
                //String room = null;
                if (cursor.moveToFirst()) {
                    floor = cursor.getString(cursor.getColumnIndex(FLOOR));
                    //room = cursor.getString(cursor.getColumnIndex(ROOM));
                }
                if (floor != null) Utils.selectSpinnerItemByValue(floorSpinner, floor);
                //if (room != null) Utils.selectSpinnerItemByValue(roomSpinner, room);
                ed.putString("currentLocation", arguments.getString("id"));
                cursor.close();
            }
            ed.apply();
        }
    }
}
