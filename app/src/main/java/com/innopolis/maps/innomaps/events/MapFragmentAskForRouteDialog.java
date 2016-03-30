package com.innopolis.maps.innomaps.events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.MapsFragment;
import com.innopolis.maps.innomaps.database.DBHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.POI_NAME;

public class MapFragmentAskForRouteDialog extends DialogFragment {

    String sourceFloor;
    String sourceRoom;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DBHelper dbHelper = new DBHelper(getContext());
        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        List<String> floors = new LinkedList<>();
        final Bundle arguments = getArguments();
        final String type = arguments.getString("type");
        final HashMap<String, List<String>> roomsMap = new LinkedHashMap<>();
        Cursor floorCursor = database.rawQuery("SELECT DISTINCT floor FROM poi", null);
        if (floorCursor.moveToFirst()) {
            do {
                String floor = floorCursor.getString(floorCursor.getColumnIndex(FLOOR));
                floors.add(floor);
            } while(floorCursor.moveToNext());
        }
        for (String floor: floors) {
            Cursor roomCursor = database.rawQuery("SELECT * FROM poi WHERE type like '%room%' and floor like '%" + floor + "%'", null);
            List<String> rooms = new ArrayList<>();
            if (roomCursor.moveToFirst()) {
                do {
                    String room = roomCursor.getString(roomCursor.getColumnIndex(POI_NAME));
                    rooms.add(room);
                } while(roomCursor.moveToNext());
            }
            Collections.sort(rooms);
            roomsMap.put(floor, rooms);
        }


        final Spinner floorSpinner = new AppCompatSpinner(getContext());
        final Spinner roomSpinner = new AppCompatSpinner(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, floors);
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
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.addView(floorSpinner);
        linearLayout.addView(roomSpinner);
        linearLayout.setPadding(25, 25, 25, 25);
        return new AlertDialog.Builder(getContext())
                .setTitle("Find route")
                .setMessage("Please specify your location")
                .setView(linearLayout)
                .setPositiveButton("Route", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String source = arguments.getString("dialogSource");
                        if (source.equals("MapsFragment")) {
                            String latitudeDest = "", longitudeDest = "";
                            Cursor cursorDest;
                            if (type.equals("event")) {
                                String[] destination = arguments.get("destination").toString().split(", ");
                                cursorDest = database.rawQuery("SELECT * FROM poi WHERE building LIKE '%" + destination[0] + "%' and floor LIKE '%" + destination[1] + "%' and room LIKE '%" + destination[2] + "%'", null);
                            } else {
                                String idPoi = arguments.getString("id");
                                cursorDest = database.rawQuery("SELECT * FROM poi WHERE _id LIKE '" + idPoi + "'", null);
                            }
                            if (cursorDest.moveToFirst()) {
                                latitudeDest = cursorDest.getString(cursorDest.getColumnIndex(LATITUDE));
                                longitudeDest = cursorDest.getString(cursorDest.getColumnIndex(LONGITUDE));
                            }
                            cursorDest.close();
                            String latitudeSource = "", longitudeSource = "";
                            Cursor cursorSource = database.rawQuery("SELECT * FROM poi WHERE floor LIKE '%" + sourceFloor + "%' and name LIKE '%" + sourceRoom + "%'", null);
                            if (cursorSource.moveToFirst()) {
                                latitudeSource = cursorSource.getString(cursorSource.getColumnIndex(LATITUDE));
                                longitudeSource = cursorSource.getString(cursorSource.getColumnIndex(LONGITUDE));
                            }
                            MapsFragment maps = (MapsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("Maps");
                            maps.showRoute(new LatLng(Double.parseDouble(latitudeSource), Double.parseDouble(longitudeSource)),
                                    new LatLng(Double.parseDouble(latitudeDest), Double.parseDouble(longitudeDest)));

                        } else if (source.equals("DetailedEvent")) {
                            String latitudeDest = "", longitudeDest = "";
                            Cursor cursorDest;
                            String[] destination = arguments.get("destination").toString().split(", ");
                            cursorDest = database.rawQuery("SELECT * FROM poi WHERE building LIKE '%" + destination[0] + "%' and floor LIKE '%" + destination[1] + "%' and room LIKE '%" + destination[2] + "%'", null);
                            if (cursorDest.moveToFirst()) {
                                latitudeDest = cursorDest.getString(cursorDest.getColumnIndex(LATITUDE));
                                longitudeDest = cursorDest.getString(cursorDest.getColumnIndex(LONGITUDE));
                            }
                            cursorDest.close();
                            String latitudeSource = "", longitudeSource = "";
                            Cursor cursorSource = database.rawQuery("SELECT * FROM poi WHERE floor LIKE '%" + sourceFloor + "%' and name LIKE '%" + sourceRoom + "%'", null);
                            if (cursorSource.moveToFirst()) {
                                latitudeSource = cursorSource.getString(cursorSource.getColumnIndex(LATITUDE));
                                longitudeSource = cursorSource.getString(cursorSource.getColumnIndex(LONGITUDE));
                            }
                            MapsFragment maps = (MapsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("Maps");
                            maps.showRoute(new LatLng(Double.parseDouble(latitudeSource), Double.parseDouble(longitudeSource)),
                                    new LatLng(Double.parseDouble(latitudeDest), Double.parseDouble(longitudeDest)));
                            getActivity().getSupportFragmentManager().popBackStackImmediate("Maps", 0);
                            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Maps");
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

}
