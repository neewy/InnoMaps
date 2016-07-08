package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingPhoto;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
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
            helper.getBuildingPhotoDao().delete(buildingPhoto);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    BuildingPhotoDAO.class.getSimpleName());
        }

        return index;

    }

    public Object findByIds(int building_id, int photo_id) {

        BuildingPhoto buildingPhoto = null;
        try {
            QueryBuilder<BuildingPhoto, Integer> qb = helper.getBuildingPhotoDao().queryBuilder();
            qb.where().eq("building_id", building_id).and().eq("photo_id", photo_id);
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
}

