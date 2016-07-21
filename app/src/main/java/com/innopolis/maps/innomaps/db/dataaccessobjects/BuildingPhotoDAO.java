package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingPhoto;
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
 * Created by alnedorezov on 7/8/16.
 */
public class BuildingPhotoDAO implements Crud {

    private DatabaseHelper helper;

    public BuildingPhotoDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        BuildingPhoto buildingPhoto = (BuildingPhoto) item;
        try {
            index = helper.getBuildingPhotoDao().create(buildingPhoto);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingPhotoDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        BuildingPhoto buildingPhoto = (BuildingPhoto) item;

        try {
            helper.getBuildingPhotoDao().update(buildingPhoto);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingPhotoDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        BuildingPhoto buildingPhoto = (BuildingPhoto) item;

        try {
            DeleteBuilder<BuildingPhoto, Integer> db = helper.getBuildingPhotoDao().deleteBuilder();
            db.where().eq(Constants.BUILDING_ID, buildingPhoto.getBuilding_id()).and().eq(Constants.PHOTO_ID, buildingPhoto.getPhoto_id());
            PreparedDelete<BuildingPhoto> preparedDelete = db.prepare();
            helper.getBuildingPhotoDao().delete(preparedDelete);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingPhotoDAO.class.getSimpleName());
        }

        return index;

    }

    public Object findByIds(int buildingId, int photoId) {

        BuildingPhoto buildingPhoto = null;
        try {
            QueryBuilder<BuildingPhoto, Integer> qb = helper.getBuildingPhotoDao().queryBuilder();
            qb.where().eq(Constants.BUILDING_ID, buildingId).and().eq(Constants.PHOTO_ID, photoId);
            PreparedQuery<BuildingPhoto> pc = qb.prepare();
            buildingPhoto = helper.getBuildingPhotoDao().query(pc).get(0);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingPhotoDAO.class.getSimpleName());
        }
        return buildingPhoto;
    }

    @Override
    public List<?> findAll() {

        List<BuildingPhoto> items = new ArrayList<>();

        try {
            items = helper.getBuildingPhotoDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingPhotoDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        BuildingPhoto buildingPhoto = (BuildingPhoto) item;
        try {
            QueryBuilder<BuildingPhoto, Integer> qb = helper.getBuildingPhotoDao().queryBuilder();
            qb.where().eq(Constants.BUILDING_ID, buildingPhoto.getBuilding_id()).and().eq(Constants.PHOTO_ID, buildingPhoto.getPhoto_id());
            PreparedQuery<BuildingPhoto> pc = qb.prepare();
            if (helper.getBuildingPhotoDao().query(pc).size() > 0) {
                UpdateBuilder<BuildingPhoto, Integer> ub = helper.getBuildingPhotoDao().updateBuilder();
                Date newCreated = com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(buildingPhoto.getCreated());
                ub.updateColumnValue(Constants.CREATED, newCreated);
                PreparedUpdate<BuildingPhoto> preparedUpdate = ub.prepare();
                index = helper.getBuildingPhotoDao().update(preparedUpdate);
            }
            else
                index = helper.getBuildingPhotoDao().create(buildingPhoto);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingPhotoDAO.class.getSimpleName());
        } catch (ParseException e) {
            Log.e(Constants.DAO_ERROR, e.getMessage(), e.fillInStackTrace());
        }

        return index;
    }
}

