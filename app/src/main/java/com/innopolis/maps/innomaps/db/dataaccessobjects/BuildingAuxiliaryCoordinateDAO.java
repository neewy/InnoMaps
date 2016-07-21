package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingAuxiliaryCoordinate;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alnedorezov on 7/20/16.
 */
public class BuildingAuxiliaryCoordinateDAO implements Crud {

    private DatabaseHelper helper;

    public BuildingAuxiliaryCoordinateDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        BuildingAuxiliaryCoordinate buildingAuxiliaryCoordinate = (BuildingAuxiliaryCoordinate) item;
        try {
            index = helper.getBuildingAuxiliaryCoordinateDao().create(buildingAuxiliaryCoordinate);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingAuxiliaryCoordinateDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        BuildingAuxiliaryCoordinate buildingAuxiliaryCoordinate = (BuildingAuxiliaryCoordinate) item;

        try {
            helper.getBuildingAuxiliaryCoordinateDao().update(buildingAuxiliaryCoordinate);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingAuxiliaryCoordinateDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        BuildingAuxiliaryCoordinate buildingAuxiliaryCoordinate = (BuildingAuxiliaryCoordinate) item;

        try {
            DeleteBuilder<BuildingAuxiliaryCoordinate, Integer> db = helper.getBuildingAuxiliaryCoordinateDao().deleteBuilder();
            db.where().eq(Constants.BUILDING_ID, buildingAuxiliaryCoordinate.getBuilding_id()).and().eq(Constants.COORDINATE_ID, buildingAuxiliaryCoordinate.getCoordinate_id());
            PreparedDelete<BuildingAuxiliaryCoordinate> preparedDelete = db.prepare();
            helper.getBuildingAuxiliaryCoordinateDao().delete(preparedDelete);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingAuxiliaryCoordinateDAO.class.getSimpleName());
        }

        return index;

    }

    public Object findByIds(int buildingId, int coordinateId) {

        BuildingAuxiliaryCoordinate buildingAuxiliaryCoordinate = null;
        try {
            QueryBuilder<BuildingAuxiliaryCoordinate, Integer> qb = helper.getBuildingAuxiliaryCoordinateDao().queryBuilder();
            qb.where().eq(Constants.BUILDING_ID, buildingId).and().eq(Constants.COORDINATE_ID, coordinateId);
            PreparedQuery<BuildingAuxiliaryCoordinate> pc = qb.prepare();
            buildingAuxiliaryCoordinate = helper.getBuildingAuxiliaryCoordinateDao().query(pc).get(0);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingAuxiliaryCoordinateDAO.class.getSimpleName());
        }
        return buildingAuxiliaryCoordinate;
    }

    @Override
    public List<?> findAll() {

        List<BuildingAuxiliaryCoordinate> items = new ArrayList<>();

        try {
            items = helper.getBuildingAuxiliaryCoordinateDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingAuxiliaryCoordinateDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        BuildingAuxiliaryCoordinate buildingAuxiliaryCoordinate = (BuildingAuxiliaryCoordinate) item;
        try {
            QueryBuilder<BuildingAuxiliaryCoordinate, Integer> qb = helper.getBuildingAuxiliaryCoordinateDao().queryBuilder();
            qb.where().eq(Constants.BUILDING_ID, buildingAuxiliaryCoordinate.getBuilding_id()).and().eq(Constants.COORDINATE_ID, buildingAuxiliaryCoordinate.getCoordinate_id());
            PreparedQuery<BuildingAuxiliaryCoordinate> pc = qb.prepare();
            if (helper.getBuildingAuxiliaryCoordinateDao().query(pc).size() > 0) {
                UpdateBuilder<BuildingAuxiliaryCoordinate, Integer> ub = helper.getBuildingAuxiliaryCoordinateDao().updateBuilder();
                Date newCreated = com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(buildingAuxiliaryCoordinate.getCreated());
                ub.updateColumnValue(Constants.CREATED, newCreated);
                PreparedUpdate<BuildingAuxiliaryCoordinate> preparedUpdate = ub.prepare();
                index = helper.getBuildingAuxiliaryCoordinateDao().update(preparedUpdate);
            }
            else
                index = helper.getBuildingAuxiliaryCoordinateDao().create(buildingAuxiliaryCoordinate);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingAuxiliaryCoordinateDAO.class.getSimpleName());
        } catch (ParseException e) {
            Log.e(Constants.DAO_ERROR, e.getMessage(), e.fillInStackTrace());
        }

        return index;
    }
}