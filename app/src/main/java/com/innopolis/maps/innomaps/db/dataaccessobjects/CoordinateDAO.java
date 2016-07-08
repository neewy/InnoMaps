package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/7/16.
 */
public class CoordinateDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public CoordinateDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        Coordinate coordinate = (Coordinate) item;
        try {
            index = helper.getCoordinateDao().create(coordinate);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    CoordinateDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        Coordinate coordinate = (Coordinate) item;

        try {
            helper.getCoordinateDao().update(coordinate);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    CoordinateDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        Coordinate coordinate = (Coordinate) item;

        try {
            helper.getCoordinateDao().delete(coordinate);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    CoordinateDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        Coordinate coordinate = null;
        try {
            coordinate = helper.getCoordinateDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    CoordinateDAO.class.getSimpleName());
        }
        return coordinate;
    }

    @Override
    public List<?> findAll() {

        List<Coordinate> items = new ArrayList<>();

        try {
            items = helper.getCoordinateDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    CoordinateDAO.class.getSimpleName());
        }

        return items;
    }
}
