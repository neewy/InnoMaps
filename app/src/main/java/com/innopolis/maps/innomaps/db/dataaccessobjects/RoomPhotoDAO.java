package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomPhoto;
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
public class RoomPhotoDAO implements Crud {

    private DatabaseHelper helper;

    public RoomPhotoDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        RoomPhoto roomPhoto = (RoomPhoto) item;
        try {
            index = helper.getRoomPhotoDao().create(roomPhoto);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomPhotoDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        RoomPhoto roomPhoto = (RoomPhoto) item;

        try {
            helper.getRoomPhotoDao().update(roomPhoto);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomPhotoDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        RoomPhoto roomPhoto = (RoomPhoto) item;

        try {
            DeleteBuilder<RoomPhoto, Integer> db = helper.getRoomPhotoDao().deleteBuilder();
            db.where().eq(Constants.ROOM_ID, roomPhoto.getRoom_id()).and().eq(Constants.PHOTO_ID, roomPhoto.getPhoto_id());
            PreparedDelete<RoomPhoto> preparedDelete = db.prepare();
            helper.getRoomPhotoDao().delete(preparedDelete);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomPhotoDAO.class.getSimpleName());
        }

        return index;

    }

    public Object findByIds(int roomId, int photoId) {

        RoomPhoto roomPhoto = null;
        try {
            QueryBuilder<RoomPhoto, Integer> qb = helper.getRoomPhotoDao().queryBuilder();
            qb.where().eq(Constants.ROOM_ID, roomId).and().eq(Constants.PHOTO_ID, photoId);
            PreparedQuery<RoomPhoto> pc = qb.prepare();
            roomPhoto = helper.getRoomPhotoDao().query(pc).get(0);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomPhotoDAO.class.getSimpleName());
        }
        return roomPhoto;
    }

    @Override
    public List<?> findAll() {

        List<RoomPhoto> items = new ArrayList<>();

        try {
            items = helper.getRoomPhotoDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomPhotoDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        RoomPhoto roomPhoto = (RoomPhoto) item;
        try {
            QueryBuilder<RoomPhoto, Integer> qb = helper.getRoomPhotoDao().queryBuilder();
            qb.where().eq(Constants.ROOM_ID, roomPhoto.getRoom_id()).and().eq(Constants.PHOTO_ID, roomPhoto.getPhoto_id());
            PreparedQuery<RoomPhoto> pc = qb.prepare();
            if (helper.getRoomPhotoDao().query(pc).size() > 0) {
                if(helper.getRoomPhotoDao().query(pc).get(0).equals(roomPhoto))
                    index = 0;
                else {
                    UpdateBuilder<RoomPhoto, Integer> ub = helper.getRoomPhotoDao().updateBuilder();
                    Date newCreated = com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(roomPhoto.getCreated());
                    ub.updateColumnValue(Constants.CREATED, newCreated);
                    PreparedUpdate<RoomPhoto> preparedUpdate = ub.prepare();
                    index = helper.getRoomPhotoDao().update(preparedUpdate);
                }
            } else
                index = helper.getRoomPhotoDao().create(roomPhoto);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    RoomPhotoDAO.class.getSimpleName());
        } catch (ParseException e) {
            Log.e(Constants.DAO_ERROR, e.getMessage(), e.fillInStackTrace());
        }

        return index;
    }
}


