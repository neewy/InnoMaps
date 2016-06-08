package com.innopolis.maps.innomaps.database;


import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.POI;
import static com.innopolis.maps.innomaps.database.TableFields.TYPE;

public class SQLQueries {

    public static final String READ_EVENTS_QUERY = "select events.summary,htmlLink," +
            "start,end,events.eventID as eventID,"
            + " description,creator_name,creator_email, checked,"
            + " building,floor,room,latitude,longitude"
            + " from events "
            + "inner join event_type on events.summary=event_type.summary "
            + "inner join event_poi on events.eventID=event_poi.eventID "
            + "inner join poi on event_poi.poi_id = poi._id ";

    public static final String IF_FAVOURITE_QUERY = " WHERE checked=1 ";
    public static final String DELETE = "DELETE FROM ";
    public static final String WHERE = " WHERE ";
    public static final String LIKE = " LIKE ";
    public static final String NOT_LIKE = " NOT LIKE ";
    public static final String SELECT_ALL = "SELECT * FROM ";
    public static String select = "SELECT %s FROM ";
    public static String selectDistinct = "SELECT DISTINCT %s FROM ";
    public static final String AND = " AND ";
    public static final String IS_NOT_NULL = " IS NOT NULL ";
    public static final String DROP = "DROP TABLE IF EXISTS ";
    public static final String INNER_JOIN = " INNER JOIN ";
    public static final String LEFT_OUTER_JOIN = " LEFT OUTER JOIN ";
    public static final String ON = " ON ";

    public static String deleteLikeQuery(String table, String row, String like) {
        return DELETE + table + WHERE + row + LIKE + "'" + like + "'";
    }

    public static String delete(String table) {
        return DELETE + table;
    }

    public static String selectWhereNotNull(String table, String row) {
        return SELECT_ALL + table + WHERE + notNull(row);
    }

    public static String selectAll(String table) {
        return SELECT_ALL + table;
    }

    public static String selectAllLike(String table, String row, String like) {
        return SELECT_ALL + table + WHERE + likeExact(row, like);
    }

    public static String selectDistinct(String table, String row) {
        return String.format(selectDistinct, row) + table;
    }

    public static String readRoomTypeQuery(String table, String room, String type) {
        return selectWhereNotNull(table, room) + AND + like(type, room);
    }

    public static String readOtherTypesQuery(String table, String type, String room, String attr, String door) {
        return selectWhereNotNull(table, type) + AND + notNull(attr) + AND + notLike(type, door) +
                AND + notLike(type, room);
    }

    public static String cursorSelectBuilding(String table, String building, String[] locationArray) {
        return selectAll(table) + WHERE + like(building, locationArray[0]);
    }

    public static String cursorSelectFloor(String table, String building, String floor, String[] locationArray) {
        return cursorSelectBuilding(table, building, locationArray) + AND + like(floor, locationArray[1]);
    }

    public static String cursorSelectRoom(String table, String building, String floor, String room, String[] locationArray) {
        return cursorSelectFloor(table, building, floor, locationArray) + AND + like(room, locationArray[2]);
    }


    public static String notNull(String row) {
        return row + IS_NOT_NULL;
    }

    public static String like(String row, String like) {
        return row + LIKE + "'%" + like + "%'";
    }

    public static String likeExact(String row, String like) {
        return row + LIKE + "'" + like + "'";
    }

    public static String where(String table, String row) {
        return WHERE + rowInTable(table, row);
    }

    public static String notLike(String row, String like) {
        return row + NOT_LIKE + "'%" + like + "%'";
    }

    public static String drop(String table) {
        return DROP + table;
    }

    public static String rowInTable(String table, String row) {
        return table + "." + row;
    }

    public static String innerJoin(String table1, String table2, String row1, String row2) {
        return INNER_JOIN + table2 + ON + rowInTable(table2, row2) + " = " + rowInTable(table1, row1);
    }

    public static String innerJoinReverse(String table1, String table2, String row1, String row2) {
        return INNER_JOIN + table2 + ON + rowInTable(table1, row1) + " = " + rowInTable(table2, row2);
    }


    public static String innerJoinLike(String table1, String table2, String row1, String row2, String like_row, String like_data) {
        return selectAll(table1) + innerJoin(table1, table2, row1, row2) + WHERE + like(rowInTable(table2, like_row), like_data);
    }

    public static String innerJoinDouble(String table1, String table2, String table3, String row1, String row2_1, String row2_2, String row3) {
        return selectAll(table1) + innerJoinReverse(table1, table2, row1, row2_1) + innerJoinReverse(table2, table3, row2_2, row3);
    }

    public static String leftOuterJoin(String table1, String table2, String row1, String row2) {
        return LEFT_OUTER_JOIN + table1 + ON + rowInTable(table1, row1) + " = " + rowInTable(table2, row2);
    }

    public static String typeEventNoneQuery(String table1, String table2, String table3, String row1, String row2_1, String row2_2, String row3) {
        return selectAll(table1) + leftOuterJoin(table2, table1, row2_1, row1) + leftOuterJoin(table3, table2, row3, row2_2) + where(table1, row1) + "=?";
    }

    public static String distanceQuery(String table, String row, String lat, String lng) {
        return String.format(select, row) + table + WHERE + lat + "=?" + AND + lng + "=?";
    }

    public static String roomCursorQuery(String table, String type, String type_data, String floor, String floor_data) {
        return selectAll(table) + WHERE + like(type, type_data) + AND + like(floor, floor_data);
    }

    public static String typeEqualsEvent(String[] destination) {
        return "SELECT * FROM poi WHERE building LIKE '%" + destination[0] + "%' and floor LIKE '%" + destination[1] + "%' and room LIKE '%" + destination[2] + "%'";
    }

    public static String typeEqualsEventNone(String like) {
        return "SELECT * FROM poi WHERE _id LIKE '" + like + "'";
    }

    public static String sourceEqualsDetailedEvent(String[] destination) {
        return ("SELECT * FROM poi WHERE building LIKE '%" + destination[0] + "%' and floor LIKE '%" + destination[1] + "%' and room LIKE '%" + destination[2] + "%'");
    }

    public static String selectFloorPoiHashmapQuery() {
        return "SELECT " + LATITUDE + "," + LONGITUDE + "," + FLOOR + " FROM " + POI + WHERE + FLOOR + "=?";
    }

    public static String makeWcMarkersQuery() {
        return "SELECT * FROM " + POI + " WHERE " + FLOOR + "=?" + " AND " + TYPE + " like 'wc'";
    }

    public static String makeFoodMarkersQuery() {
        return "SELECT * FROM " + POI + " WHERE " + FLOOR + "= ?" + " AND " + TYPE + " = 'food'";
    }

    public static String makeOtherMarkersQuery() {
        return FLOOR + " = ? AND (" + TYPE + " != ? and "
                + TYPE + " != ? and " + TYPE + " != ? and " + TYPE + " != ? and " +
                TYPE + " != ? and " + TYPE + " != ? and " + TYPE + " != ?)";
    }

    public static String makeAllMarkersQuery() {
        return FLOOR + " = ? AND (" + TYPE + " = ? or " + TYPE + " = ? or " + TYPE + " = ? or " + TYPE + " = ? or " + TYPE + " = ? or " + TYPE + " = ?)";
    }

    public static String makeEventsMarkersQuery() {
        return "SELECT * FROM poi " +
                "LEFT OUTER JOIN event_poi on event_poi.poi_id = poi._id " +
                "LEFT OUTER JOIN events on events.eventID = event_poi.eventID " +
                "INNER JOIN event_type on event_type._id = events._id " +
                "WHERE poi.floor = ? AND " +
                "poi.type = ?";
    }


}
