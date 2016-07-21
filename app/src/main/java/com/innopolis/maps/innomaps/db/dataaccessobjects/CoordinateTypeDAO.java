package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.CoordinateType;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class CoordinateTypeDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public CoordinateTypeDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        CoordinateType coordinateType = (CoordinateType) item;
        try {
            index = helper.getCoordinateTypeDao().create(coordinateType);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    CoordinateTypeDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        CoordinateType coordinateType = (CoordinateType) item;

        try {
            helper.getCoordinateTypeDao().update(coordinateType);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    CoordinateTypeDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        CoordinateType coordinateType = (CoordinateType) item;

        try {
            helper.getCoordinateTypeDao().delete(coordinateType);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    CoordinateTypeDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        CoordinateType coordinateType = null;
        try {
            coordinateType = helper.getCoordinateTypeDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    CoordinateTypeDAO.class.getSimpleName());
        }
        return coordinateType;
    }

    @Override
    public List<?> findAll() {

        List<CoordinateType> items = new ArrayList<>();

        try {
            items = helper.getCoordinateTypeDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    CoordinateTypeDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public Object getObjectWithMaxId() {
        CoordinateType coordinateType = null;
        try {
            QueryBuilder<CoordinateType, Integer> qBuilder = helper.getCoordinateTypeDao().queryBuilder();
            qBuilder.orderBy("id", false); // false for descending order
            qBuilder.limit(1);
            coordinateType = helper.getCoordinateTypeDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    PhotoDAO.class.getSimpleName());
        }
        return coordinateType;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        CoordinateType coordinateType = (CoordinateType) item;
        try {
            if (helper.getCoordinateTypeDao().idExists(coordinateType.getId())) {
                if (helper.getCoordinateTypeDao().queryForId(coordinateType.getId()).equals(coordinateType))
                    index = coordinateType.getId();
                else
                    index = helper.getCoordinateTypeDao().update(coordinateType);
            } else
                index = helper.getCoordinateTypeDao().create(coordinateType);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    CoordinateTypeDAO.class.getSimpleName());
        }

        return index;
    }
}
