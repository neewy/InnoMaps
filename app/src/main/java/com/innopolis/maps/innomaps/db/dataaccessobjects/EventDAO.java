package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Event;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventFavorable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class EventDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public EventDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        EventFavorable eventFavorable;
        try {
            if (item instanceof Event) {
                Event event = (Event) item;
                eventFavorable = new EventFavorable(event, false);
            } else
                eventFavorable = (EventFavorable) item;
            index = helper.getEventDao().create(eventFavorable);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        EventFavorable eventFavorable;

        try {
            if (item instanceof Event) {
                Event event = (Event) item;
                if (helper.getEventDao().idExists(event.getId()))
                    eventFavorable = new EventFavorable(event, helper.getEventDao().queryForId(event.getId()).isFavourite());
                else
                    eventFavorable = new EventFavorable(event, false);
            } else
                eventFavorable = (EventFavorable) item;
            index = helper.getEventDao().update(eventFavorable);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        EventFavorable eventFavorable;

        try {
            if (item instanceof Event) {
                Event event = (Event) item;
                eventFavorable = helper.getEventDao().queryForId(event.getId());
            } else
                eventFavorable = (EventFavorable) item;
            index = helper.getEventDao().delete(eventFavorable);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        EventFavorable event = null;
        try {
            event = helper.getEventDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventDAO.class.getSimpleName());
        }
        return event;
    }

    @Override
    public List<?> findAll() {

        List<EventFavorable> items = new ArrayList<>();

        try {
            items = helper.getEventDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public Object getObjectWithMaxId() {
        EventFavorable event = null;
        try {
            QueryBuilder<EventFavorable, Integer> qBuilder = helper.getEventDao().queryBuilder();
            qBuilder.orderBy("id", false); // false for descending order
            qBuilder.limit(1);
            event = helper.getEventDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    PhotoDAO.class.getSimpleName());
        }
        return event;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        Dao.CreateOrUpdateStatus createOrUpdateStatus;
        EventFavorable eventFavorable;
        try {
            if (item instanceof Event) {
                Event event = (Event) item;
                if (helper.getEventDao().idExists(event.getId()))
                    eventFavorable = new EventFavorable(event, helper.getEventDao().queryForId(event.getId()).isFavourite());
                else
                    eventFavorable = new EventFavorable(event, false);
            } else
                eventFavorable = (EventFavorable) item;
            createOrUpdateStatus = helper.getEventDao().createOrUpdate(eventFavorable);
            index = createOrUpdateStatus.getNumLinesChanged();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventDAO.class.getSimpleName());
        }

        return index;
    }
}
