package com.innopolis.maps.innomaps.app;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.database.TableFields;

import java.util.List;

import static com.innopolis.maps.innomaps.database.TableFields.BUILDING;
import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.ID;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.POI;
import static com.innopolis.maps.innomaps.database.TableFields.POI_NAME;
import static com.innopolis.maps.innomaps.database.TableFields.TYPE;


public class MarkersAdapter extends BottomSheet {
    protected static final int ALL_FILTER = 2;
    private static final int WC_FILTER = 0;
    private static final int FOOD_FILTER = 1;
    private static final int EVENTS_FILTER = 3;
    private static final int OTHER_FILTER = 4;
    static SQLiteDatabase database;
    MapView mapView;
    List<Marker> markers;
    List<Integer> filterList;

    protected void isMarkerSorted(int floor) {
        int filter = filterList.get(0);
        if (filter == WC_FILTER) {
            makeWcMarkers(floor);
        } else if (filter == FOOD_FILTER) {
            makeFoodMarkers(floor);
        } else if (filter == ALL_FILTER) {
            makeAllMarkers(floor);
        } else if (filter == EVENTS_FILTER) {
            makeAllMarkers(floor);
        } else if (filter == OTHER_FILTER) {
            makeOtherMarkers(floor);
        }
    }

    private void makeWcMarkers(int floor) {
        String numFloor = String.valueOf(floor) + "floor";

        String sqlQuery = "SELECT * FROM " + POI + " WHERE " + FLOOR + "=?" + " AND " + TYPE + " like 'wc'";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{numFloor});
        refreshMarkers(cursor);

    }

    private void makeFoodMarkers(int floor) {
        String numFloor = String.valueOf(floor) + "floor";

        String sqlQuery = "SELECT * FROM " + POI + " WHERE " + FLOOR + "= ?" + " AND " + TYPE + " = 'food'";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{numFloor});
        refreshMarkers(cursor);

    }

    private void makeOtherMarkers(int floor) {
        String selection = FLOOR + " = ? AND (" + TYPE + " = ? or " + TYPE + " = ?)";
        String[] selectionArgs = {floor + "floor", "wc", "food"};
        Cursor cursor = database.query(POI, null, selection, selectionArgs, null, null, null);
        refreshMarkers(cursor);

    }

    protected void makeAllMarkers(int floor) {
        String selection = FLOOR + " = ? AND (" + TYPE + " = ? or " + TYPE + " = ? or " + TYPE + " = ?)";
        String[] selectionArgs = {floor + "floor", "room", "wc", "food"};
        Cursor cursor = database.query(POI, null, selection, selectionArgs, null, null, null);
        refreshMarkers(cursor);

    }

    private void refreshMarkers(Cursor cursor) {
        if (markers != null) {
            for (Marker marker : markers) {
                marker.remove();
            }
            markers.clear();
        }

        if (cursor.moveToFirst()) {
            do {
                String room = cursor.getString(cursor.getColumnIndex(POI_NAME));
                String type = cursor.getString(cursor.getColumnIndex(TYPE));
                String latitude = cursor.getString(cursor.getColumnIndex(LATITUDE));
                String longitude = cursor.getString(cursor.getColumnIndex(LONGITUDE));
                setMarkersRoom(room, type, latitude, longitude);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void setMarkersRoom(String room, String type, String latitude, String longitude) {

        float center = 0.5f;
        final Marker markersRoom = map.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                        .icon(iconBitmapAdapter(type))
                        .title(room)
                        .anchor(center, center)

        );

        markers.add(markersRoom);

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                for (Marker marker : markers) {
                    if (Math.abs(marker.getPosition().latitude - latLng.latitude) < 0.05 && Math.abs(marker.getPosition().longitude - latLng.longitude) < 0.05) {
                        searchMarker(marker);
                        break;
                    }
                }

            }

        });
    }

    public BitmapDescriptor iconBitmapAdapter(String type) {
        int src;
        BitmapDescriptor icon;
        Drawable shape;
        int px;

        switch (type) {
            case "room":
                src = R.drawable.ic_room;
                px = 15;
                break;

            case "wc":
                src = R.drawable.wc;
                px = 30;
                break;

            case "food":
                src = R.drawable.ic_food;
                px = 30;
                break;

            default:
                src = R.drawable.ic_duck;
                px = 30;
                break;
        }

        Bitmap markerBitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(markerBitmap);
        shape = getResources().getDrawable(src);
        if (shape != null) {
            shape.setBounds(0, 0, markerBitmap.getWidth(), markerBitmap.getHeight());
            shape.draw(canvas);
        }
        icon = BitmapDescriptorFactory.fromBitmap(markerBitmap);

        return icon;

    }

    private void searchMarker(Marker marker) {
        String room = marker.getTitle();
        String sqlQuery = "SELECT * FROM " + POI + " WHERE " + TableFields.POI_NAME + " like '" + room.replaceAll("'", "''") + "';";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{});

        if (cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndex(ID));
            String type = cursor.getString(cursor.getColumnIndex(TYPE));
            String building = cursor.getString(cursor.getColumnIndex(BUILDING));
            String floor = cursor.getString(cursor.getColumnIndex(FLOOR));

            SearchableItem item = new SearchableItem();
            item.setBuilding(building);
            item.setType(type);
            item.setFloor(floor);
            item.setRoom(room);
            item.setId(id);
            inSearchBottomList(item, mapView);
        }
        cursor.close();
    }
}
