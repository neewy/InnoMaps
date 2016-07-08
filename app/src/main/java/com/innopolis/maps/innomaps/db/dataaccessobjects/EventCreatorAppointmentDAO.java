package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreatorAppointment;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class EventCreatorAppointmentDAO implements Crud {

    private DatabaseHelper helper;

    public EventCreatorAppointmentDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        EventCreatorAppointment eventCreatorAppointment = (EventCreatorAppointment) item;
        try {
            index = helper.getEventCreatorAppointmentDao().create(eventCreatorAppointment);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorAppointmentDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        EventCreatorAppointment eventCreatorAppointment = (EventCreatorAppointment) item;

        try {
            helper.getEventCreatorAppointmentDao().update(eventCreatorAppointment);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorAppointmentDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        EventCreatorAppointment eventCreatorAppointment = (EventCreatorAppointment) item;

        try {
            helper.getEventCreatorAppointmentDao().delete(eventCreatorAppointment);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorAppointmentDAO.class.getSimpleName());
        }

        return index;

    }

    public Object findByIds(int eventId, int eventCreatorId) {

        EventCreatorAppointment eventCreatorAppointment = null;
        try {
            QueryBuilder<EventCreatorAppointment, Integer> qb = helper.getEventCreatorAppointmentDao().queryBuilder();
            qb.where().eq("event_id", eventId).and().eq("event_creator_id", eventCreatorId);
            PreparedQuery<EventCreatorAppointment> pc = qb.prepare();
            eventCreatorAppointment = helper.getEventCreatorAppointmentDao().query(pc).get(0);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorAppointmentDAO.class.getSimpleName());
        }
        return eventCreatorAppointment;
    }

    @Override
    public List<?> findAll() {

        List<EventCreatorAppointment> items = new ArrayList<>();

        try {
            items = helper.getEventCreatorAppointmentDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventCreatorAppointmentDAO.class.getSimpleName());
        }

        return items;
    }
}

