package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreator;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class EventCreatorDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public EventCreatorDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        EventCreator eventCreator = (EventCreator) item;
        try {
            index = helper.getEventCreatorDao().create(eventCreator);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        EventCreator eventCreator = (EventCreator) item;

        try {
            helper.getEventCreatorDao().update(eventCreator);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        EventCreator eventCreator = (EventCreator) item;

        try {
            helper.getEventCreatorDao().delete(eventCreator);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        EventCreator eventCreator = null;
        try {
            eventCreator = helper.getEventCreatorDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorDAO.class.getSimpleName());
        }
        return eventCreator;
    }

    @Override
    public List<?> findAll() {

        List<EventCreator> items = new ArrayList<>();

        try {
            items = helper.getEventCreatorDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public Object getObjectWithMaxId() {
        EventCreator eventCreator = null;
        try {
            QueryBuilder<EventCreator, Integer> qBuilder = helper.getEventCreatorDao().queryBuilder();
            qBuilder.orderBy("id", false); // false for descending order
            qBuilder.limit(1);
            eventCreator = helper.getEventCreatorDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    PhotoDAO.class.getSimpleName());
        }
        return eventCreator;
    }
}
