package com.innopolis.maps.innomaps.database;


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
    public static final String AND = " AND ";
    public static final String IS_NOT_NULL = " IS NOT NULL ";
    public static final String DROP = "DROP TABLE IF EXISTS ";
    public static final String INNER_JOIN = " INNER JOIN";
    public static final String ON = " ON ";

    public static String deleteLikeQuery(String table, String row, String like) {
        String query = DELETE + table + WHERE + row + LIKE + "'" + like + "'";
        return query;
    }

    public static String delete(String table) {
        String query = DELETE + table;
        return query;
    }

    public static String selectWhereNotNull(String table, String row) {
        String query = SELECT_ALL + table + WHERE + notNull(row);
        return query;
    }

    public static String selectAll(String table) {
        String query = SELECT_ALL + table;
        return query;
    }

    public static String readRoomTypeQuery(String table, String room, String type) {
        String query = selectWhereNotNull(table, room) + AND + like(type, room);
        return query;
    }

    public static String readOtherTypesQuery(String table, String type, String room, String attr, String door) {
        String query = selectWhereNotNull(table, type) + AND + notNull(attr) + AND + notLike(type, door) +
                AND + notLike(type, room);
        return query;
    }

    public static String cursorSelectBuilding(String table, String building, String[] locationArray) {
        String query = selectAll(table) + WHERE + like(building, locationArray[0]);
        return query;
    }

    public static String cursorSelectFloor(String table, String building, String floor, String[] locationArray) {
        String query = cursorSelectBuilding(table, building, locationArray) + AND + like(floor, locationArray[1]);
        return query;
    }

    public static String cursorSelectRoom(String table, String building, String floor, String room, String[] locationArray) {
        String query = cursorSelectFloor(table, building, floor, locationArray) + AND + like(room, locationArray[2]);
        return query;
    }


    public static String notNull(String row) {
        String query = row + IS_NOT_NULL;
        return query;
    }

    public static String like(String row, String like) {
        String query = row + LIKE + "'%" + like + "%'";
        return query;
    }

    public static String notLike(String row, String like) {
        String query = row + NOT_LIKE + "'%" + like + "%'";
        return query;
    }

    public static String drop(String table) {
        String query = DROP + table;
        return query;
    }

    public static String rowInTable(String table, String row){
        String string = table + "." + row;
        return string;
    }

    public static String innerJoin(String table1, String table2, String row1, String row2){
        String query = selectAll(table1) + INNER_JOIN + table2 + ON + rowInTable(table2, row2) + " = " + rowInTable(table1, row1);
        return query;
    }

    public static String locationQuery(String table1, String table2, String row1, String row2, String like_row, String like_data){
        String query = innerJoin(table1, table2, row1, row2) + WHERE + like(rowInTable(table2, like_row), like_data);
    return query;
    }
}
