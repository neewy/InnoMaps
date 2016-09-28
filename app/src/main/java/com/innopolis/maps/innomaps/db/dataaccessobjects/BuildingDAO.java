package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Building;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class BuildingDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public BuildingDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        Building building = (Building) item;
        try {
            index = helper.getBuildingDao().create(building);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        Building building = (Building) item;

        try {
            helper.getBuildingDao().update(building);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        Building building = (Building) item;

        try {
            helper.getBuildingDao().delete(building);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        Building building = null;
        try {
            building = helper.getBuildingDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingDAO.class.getSimpleName());
        }
        return building;
    }

    @Override
    public List<?> findAll() {

        List<Building> items = new ArrayList<>();

        try {
            items = helper.getBuildingDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public Object getObjectWithMaxId() {
        Building building = null;
        try {
            QueryBuilder<Building, Integer> qBuilder = helper.getBuildingDao().queryBuilder();
            qBuilder.orderBy(Constants.ID, false); // false for descending order
            qBuilder.limit(1);
            building = helper.getBuildingDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingDAO.class.getSimpleName());
        }
        return building;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        Building building = (Building) item;
        try {
            if (helper.getBuildingDao().idExists(building.getId())) {
                if (helper.getBuildingDao().queryForId(building.getId()).equals(building))
                    index = building.getId();
                else
                    index = helper.getBuildingDao().update(building);
            } else
                index = helper.getBuildingDao().create(building);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingDAO.class.getSimpleName());
        }

        return index;
    }
}
