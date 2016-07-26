package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class RoomDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public RoomDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        Room room = (Room) item;
        try {
            index = helper.getRoomDao().create(room);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        Room room = (Room) item;

        try {
            helper.getRoomDao().update(room);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        Room room = (Room) item;

        try {
            helper.getRoomDao().delete(room);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        Room room = null;
        try {
            room = helper.getRoomDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomDAO.class.getSimpleName());
        }
        return room;
    }

    @Override
    public List<?> findAll() {

        List<Room> items = new ArrayList<>();

        try {
            items = helper.getRoomDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public Object getObjectWithMaxId() {
        Room room = null;
        try {
            QueryBuilder<Room, Integer> qBuilder = helper.getRoomDao().queryBuilder();
            qBuilder.orderBy(Constants.ID, false); // false for descending order
            qBuilder.limit(1);
            room = helper.getRoomDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomDAO.class.getSimpleName());
        }
        return room;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        Room room = (Room) item;
        try {
            if (helper.getRoomDao().idExists(room.getId())) {
                if (helper.getRoomDao().queryForId(room.getId()).equals(room))
                    index = room.getId();
                else
                    index = helper.getRoomDao().update(room);
            } else
                index = helper.getRoomDao().create(room);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomDAO.class.getSimpleName());
        }

        return index;
    }

    public Room getFirstRecordByCoordinateId(int coordinateId) {
        Room room = null;
        try {
            QueryBuilder<Room, Integer> qBuilder = helper.getRoomDao().queryBuilder();
            qBuilder.orderBy(Constants.COORDINATE_ID, false); // false for descending order
            qBuilder.limit(1);
            if (qBuilder.query().size() > 0)
                room = helper.getRoomDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomDAO.class.getSimpleName());
        }
        return room;
    }
}
