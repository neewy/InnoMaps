package com.innopolis.maps.innomaps.database;

import static com.innopolis.maps.innomaps.database.TableTypeFields.ATTRIBUTE_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.BUILDING_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.CHECKED_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.CREATE_TABLE;
import static com.innopolis.maps.innomaps.database.TableTypeFields.CREATOR_EMAIL_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.CREATOR_NAME_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.DESCRIPTION_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.END_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.EVENT_ID_INTEGER;
import static com.innopolis.maps.innomaps.database.TableTypeFields.FLOOR_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.ID_INTEGER;
import static com.innopolis.maps.innomaps.database.TableTypeFields.LATITUDE_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.LINK_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.LONGITUDE_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.POI_ID_INTEGER;
import static com.innopolis.maps.innomaps.database.TableTypeFields.POI_NAME_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.ROOM_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.START_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.SUMMARY_TEXT;
import static com.innopolis.maps.innomaps.database.TableTypeFields.TYPE_TEXT;


public class DBTables{

    public static abstract class TableColumns {

        public static String TABLE_EVENTS_CREATE = CREATE_TABLE + TableFields.EVENTS + " ("
                + ID_INTEGER + SUMMARY_TEXT + LINK_TEXT + START_TEXT + END_TEXT
                + EVENT_ID_INTEGER + CHECKED_TEXT;

        public static String TABLE_EVENT_TYPE_CREATE = CREATE_TABLE + TableFields.EVENT_TYPE + " ("
                + ID_INTEGER + SUMMARY_TEXT + DESCRIPTION_TEXT + CREATOR_NAME_TEXT
                + CREATOR_EMAIL_TEXT;

        public static String TABLE_EVENT_POI_CREATE = CREATE_TABLE + TableFields.EVENT_POI + " ("
                + ID_INTEGER + EVENT_ID_INTEGER + POI_ID_INTEGER;

        public static String TABLE_POI_CREATE = CREATE_TABLE + TableFields.POI + " ("
                + ID_INTEGER + POI_NAME_TEXT + BUILDING_TEXT + FLOOR_TEXT + ROOM_TEXT
                + LATITUDE_TEXT + LONGITUDE_TEXT + TYPE_TEXT + ATTRIBUTE_TEXT;

    }

    public static String createTable(String string) {
        return removeLastChar(string) + ")";
    }


    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 2);
    }


}

