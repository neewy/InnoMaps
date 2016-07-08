package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Building;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class BuildingDAO implements Crud {

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
                    Constants.BUILDING_STARTING_FROM_CAPITAL_LETTER + Constants.SPACE + Constants.DAO);
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
                    Constants.BUILDING_STARTING_FROM_CAPITAL_LETTER + Constants.SPACE + Constants.DAO);
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
                    Constants.BUILDING_STARTING_FROM_CAPITAL_LETTER + Constants.SPACE + Constants.DAO);
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
                    Constants.BUILDING_STARTING_FROM_CAPITAL_LETTER + Constants.SPACE + Constants.DAO);
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
                    Constants.BUILDING_STARTING_FROM_CAPITAL_LETTER + Constants.SPACE + Constants.DAO);
        }

        return items;
    }
}
