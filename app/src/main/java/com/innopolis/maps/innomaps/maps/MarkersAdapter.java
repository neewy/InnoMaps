package com.innopolis.maps.innomaps.maps;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.innopolis.maps.innomaps.R;
import com.innopolis.maps.innomaps.app.SearchableItem;
import com.innopolis.maps.innomaps.database.TableFields;

import java.util.List;

import static com.innopolis.maps.innomaps.database.TableFields.BUILDING;
import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields._ID;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.POI;
import static com.innopolis.maps.innomaps.database.TableFields.POI_NAME;
import static com.innopolis.maps.innomaps.database.TableFields.ROOM;
import static com.innopolis.maps.innomaps.database.TableFields.TYPE;



/**
 * This class is responsible for drawing markers that show
 * names and numbers of rooms. In addition it contains adapters for graphic
 *
 */

public class MarkersAdapter extends BottomSheet {
    protected static final int ALL_FILTER = 2;
    private static final int WC_FILTER = 0;
    private static final int FOOD_FILTER = 1;
    private static final int EVENTS_FILTER = 3;
    private static final int OTHER_FILTER = 4;
    static SQLiteDatabase database;
    MapView mapView;
    List<Marker> markers; //store all markers
    List<Integer> filterList; //to store elements after choosing filter



    /**
     * Switch markers by id of optionMenu under toolbar
     * @param floor
     */

    protected void isMarkerSorted(int floor) {
        int filter = filterList.get(0);
        if (filter == WC_FILTER) {
            makeWcMarkers(floor);
        } else if (filter == FOOD_FILTER) {
            makeFoodMarkers(floor);
        } else if (filter == ALL_FILTER) {
            makeAllMarkers(floor);
        } else if (filter == EVENTS_FILTER) {
            makeEventsMarkers(floor);
        } else if (filter == OTHER_FILTER) {
            makeOtherMarkers(floor);
        }
    }


    /**
     * Filters markers on map and shows wc
     * @param floor
     */
    private void makeWcMarkers(int floor) {
        String numFloor = String.valueOf(floor) + "floor";

        String sqlQuery = "SELECT * FROM " + POI + " WHERE " + FLOOR + "=?" + " AND " + TYPE + " like 'wc'";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{numFloor});
        refreshMarkers(cursor);

    }

    /**
     * Filters markers on map and shows only food places
     * @param floor
     */
    private void makeFoodMarkers(int floor) {
        String numFloor = String.valueOf(floor) + "floor";

        String sqlQuery = "SELECT * FROM " + POI + " WHERE " + FLOOR + "= ?" + " AND " + TYPE + " = 'food'";
        Cursor cursor = database.rawQuery(sqlQuery, new String[]{numFloor});
        refreshMarkers(cursor);

    }

    /**
     * Filters markers on map and shows markers like "library" or "clinic"
     * @param floor
     */
    private void makeOtherMarkers(int floor) {
        String selection = FLOOR + " = ? AND (" + TYPE + " != ? and "
                + TYPE + " != ? and " + TYPE + " != ? and " + TYPE + " != ? and " +
                TYPE + " != ? and " + TYPE + " != ? and " + TYPE + " != ?)";
        String[] selectionArgs = {floor + "floor", "room", "food", "stairs", "elevator", "wc door", "wc", "door"};
        Cursor cursor = database.query(POI, null, selection, selectionArgs, null, null, null);
        refreshMarkers(cursor);

    }


    /**
     * Filters markers on map and shows all markers
     * @param floor
     */
    protected void makeAllMarkers(int floor) {
        String selection = FLOOR + " = ? AND (" + TYPE + " = ? or " + TYPE + " = ? or " + TYPE + " = ? or " + TYPE + " = ? or " + TYPE + " = ? or " + TYPE + " = ?)";
        String[] selectionArgs = {floor + "floor", "room", "wc", "food", "library", "clinic", "reading"};
        Cursor cursor = database.query(POI, null, selection, selectionArgs, null, null, null);
        refreshMarkers(cursor);

    }


    /**
     * Filters markers on map and shows only rooms with events
     * @param floor
     */
    protected void makeEventsMarkers(int floor) {
        String selection = "SELECT * FROM poi " +
                "LEFT OUTER JOIN event_poi on event_poi.poi_id = poi._id " +
                "LEFT OUTER JOIN events on events.eventID = event_poi.eventID " +
                "INNER JOIN event_type on event_type._id = events._id " +
                "WHERE poi.floor = ? AND " +
                "poi.type = ?";
        String[] selectionArgs = {floor + "floor", ROOM};
        Cursor cursor = database.rawQuery(selection, selectionArgs);
        refreshMarkers(cursor);

    }


    /**
     * Clears markers and finds new
     * @param cursor - keep and search info in db table
     */
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


    /**
     * Puts markers with custom icons on the map
     * Params are info in db table
     * @param room
     * @param type
     * @param latitude
     * @param longitude
     */
    private void setMarkersRoom(String room, String type, String latitude, String longitude) {

        float center = 0.5f;
        final Marker markersRoom = map.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                .icon(iconBitmapAdapter(type))
                .title(room)
                .anchor(center, center)

        );

        markers.add(markersRoom);
    }



    /**
     * Switches icons by type
     * @param type - type of room
     * @return BitmapDescriptor
     */
    public BitmapDescriptor iconBitmapAdapter(String type) {
        int src;
        BitmapDescriptor icon;
        int px;
        int px_large = 30;
        int px_small = 15;

        switch (type) {
            case "room":
                src = R.drawable.ic_room;
                px = px_small;
                break;

            case "wc":
                src = R.drawable.wc;
                px = px_large;
                break;

            case "food":
                src = R.drawable.ic_food;
                px = px_large;
                break;

            case "clinic":
                src = R.drawable.ic_clinic;
                px = px_large;
                break;

            case "library":

            case "reading":
                src = R.drawable.ic_library;
                px = px_large;
                break;

            case "easter egg":
                src = R.drawable.ic_egg;
                px = px_large;
                break;

            default:
                src = R.drawable.ic_duck;
                px = 30;
                break;
        }


        icon = converterDrawable(src, px);
        return icon;

    }


    /**
     * Converts drawable to a bitmap resource
     * @param src
     * @param size - size in pixels
     * @return bitmap object
     */

    public BitmapDescriptor converterDrawable(int src, int size) {
        BitmapDescriptor icon;
        Drawable shape;
        Bitmap markerBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(markerBitmap);
        shape = ContextCompat.getDrawable(getActivity(), src);
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
            String id = cursor.getString(cursor.getColumnIndex(_ID));
            String type = cursor.getString(cursor.getColumnIndex(TYPE));
            String building = cursor.getString(cursor.getColumnIndex(BUILDING));
            String floor = cursor.getString(cursor.getColumnIndex(FLOOR));

            SearchableItem item = new SearchableItem();
            item.setBuilding(building);
            item.setType(type);
            item.setFloor(floor);
            item.setRoom(room);
            item.setId(id);
//            inSearchBottomList(item, mapView);
        }
        cursor.close();
    }
}
