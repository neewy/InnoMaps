package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomType;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class RoomTypeDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public RoomTypeDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        RoomType roomType = (RoomType) item;
        try {
            index = helper.getRoomTypeDao().create(roomType);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomTypeDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        RoomType roomType = (RoomType) item;

        try {
            helper.getRoomTypeDao().update(roomType);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomTypeDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        RoomType roomType = (RoomType) item;

        try {
            helper.getRoomTypeDao().delete(roomType);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomTypeDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        RoomType roomType = null;
        try {
            roomType = helper.getRoomTypeDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomTypeDAO.class.getSimpleName());
        }
        return roomType;
    }

    @Override
    public List<?> findAll() {

        List<RoomType> items = new ArrayList<>();

        try {
            items = helper.getRoomTypeDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomTypeDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public Object getObjectWithMaxId() {
        RoomType roomType = null;
        try {
            QueryBuilder<RoomType, Integer> qBuilder = helper.getRoomTypeDao().queryBuilder();
            qBuilder.orderBy("id", false); // false for descending order
            qBuilder.limit(1);
            roomType = helper.getRoomTypeDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomTypeDAO.class.getSimpleName());
        }
        return roomType;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        RoomType roomType = (RoomType) item;
        try {
            if (helper.getRoomTypeDao().idExists(roomType.getId()))
                index = helper.getRoomTypeDao().update(roomType);
            else
                index = helper.getRoomTypeDao().create(roomType);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomTypeDAO.class.getSimpleName());
        }

        return index;
    }
}

