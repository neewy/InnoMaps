package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingFloorOverlay;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class BuildingFloorOverlayDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public BuildingFloorOverlayDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        BuildingFloorOverlay buildingFloorOverlay = (BuildingFloorOverlay) item;
        try {
            index = helper.getBuildingFloorOverlayDao().create(buildingFloorOverlay);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingFloorOverlayDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        BuildingFloorOverlay buildingFloorOverlay = (BuildingFloorOverlay) item;

        try {
            helper.getBuildingFloorOverlayDao().update(buildingFloorOverlay);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingFloorOverlayDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        BuildingFloorOverlay buildingFloorOverlay = (BuildingFloorOverlay) item;

        try {
            helper.getBuildingFloorOverlayDao().delete(buildingFloorOverlay);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingFloorOverlayDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        BuildingFloorOverlay buildingFloorOverlay = null;
        try {
            buildingFloorOverlay = helper.getBuildingFloorOverlayDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingFloorOverlayDAO.class.getSimpleName());
        }
        return buildingFloorOverlay;
    }

    @Override
    public List<?> findAll() {

        List<BuildingFloorOverlay> items = new ArrayList<>();

        try {
            items = helper.getBuildingFloorOverlayDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingFloorOverlayDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public Object getObjectWithMaxId() {
        BuildingFloorOverlay buildingFloorOverlay = null;
        try {
            QueryBuilder<BuildingFloorOverlay, Integer> qBuilder = helper.getBuildingFloorOverlayDao().queryBuilder();
            qBuilder.orderBy("id", false); // false for descending order
            qBuilder.limit(1);
            buildingFloorOverlay = helper.getBuildingFloorOverlayDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    PhotoDAO.class.getSimpleName());
        }
        return buildingFloorOverlay;
    }
}
