package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventSchedule;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class EventScheduleDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public EventScheduleDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        EventSchedule eventSchedule = (EventSchedule) item;
        try {
            index = helper.getEventScheduleDao().create(eventSchedule);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventScheduleDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        EventSchedule eventSchedule = (EventSchedule) item;

        try {
            helper.getEventScheduleDao().update(eventSchedule);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventScheduleDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        EventSchedule eventSchedule = (EventSchedule) item;

        try {
            helper.getEventScheduleDao().delete(eventSchedule);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventScheduleDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        EventSchedule eventSchedule = null;
        try {
            eventSchedule = helper.getEventScheduleDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventScheduleDAO.class.getSimpleName());
        }
        return eventSchedule;
    }

    @Override
    public List<?> findAll() {

        List<EventSchedule> items = new ArrayList<>();

        try {
            items = helper.getEventScheduleDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventScheduleDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public Object getObjectWithMaxId() {
        EventSchedule eventSchedule = null;
        try {
            QueryBuilder<EventSchedule, Integer> qBuilder = helper.getEventScheduleDao().queryBuilder();
            qBuilder.orderBy(Constants.ID, false); // false for descending order
            qBuilder.limit(1);
            eventSchedule = helper.getEventScheduleDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    PhotoDAO.class.getSimpleName());
        }
        return eventSchedule;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        EventSchedule eventSchedule = (EventSchedule) item;
        try {
            if (helper.getEventScheduleDao().idExists(eventSchedule.getId())) {
                if (helper.getEventScheduleDao().queryForId(eventSchedule.getId()).equals(eventSchedule))
                    index = eventSchedule.getId();
                else
                    index = helper.getEventScheduleDao().update(eventSchedule);
            } else
                index = helper.getEventScheduleDao().create(eventSchedule);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventScheduleDAO.class.getSimpleName());
        }

        return index;
    }

    public EventSchedule findByEventId(int eventId) {
        EventSchedule eventSchedule = null;
        try {
            QueryBuilder<EventSchedule, Integer> qb = helper.getEventScheduleDao().queryBuilder();
            qb.where().eq(Constants.EVENT_ID, eventId);
            PreparedQuery<EventSchedule> pc = qb.prepare();
            if (helper.getEventScheduleDao().query(pc).size() > 0)
                eventSchedule = helper.getEventScheduleDao().query(pc).get(0);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventScheduleDAO.class.getSimpleName());
        }
        return eventSchedule;
    }

    public List<EventSchedule> findUpcomingAndOngoingScheduledEvents() {

        List<EventSchedule> eventSchedules = new ArrayList<>();

        try {
            QueryBuilder<EventSchedule, Integer> qb = helper.getEventScheduleDao().queryBuilder();
            qb.where().gt(Constants.END_DATETIME, new Date());
            PreparedQuery<EventSchedule> pc = qb.prepare();
            if (helper.getEventScheduleDao().query(pc).size() > 0)
                eventSchedules = helper.getEventScheduleDao().query(pc);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventScheduleDAO.class.getSimpleName());
        }

        return eventSchedules;
    }

    public List<EventSchedule> findUpcomingAndOngoingScheduledEventsInSpecifiedLocation(int location_id) {

        List<EventSchedule> eventSchedules = new ArrayList<>();

        try {
            QueryBuilder<EventSchedule, Integer> qb = helper.getEventScheduleDao().queryBuilder();
            qb.where().gt(Constants.END_DATETIME, new Date()).and().eq(Constants.LOCATION_ID, location_id);
            PreparedQuery<EventSchedule> pc = qb.prepare();
            if (helper.getEventScheduleDao().query(pc).size() > 0)
                eventSchedules = helper.getEventScheduleDao().query(pc);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventScheduleDAO.class.getSimpleName());
        }

        return eventSchedules;
    }

    public List<EventSchedule> findUpcomingAndOngoingScheduledEventsOnTheSpecifiedFloor(int floor) {

        List<EventSchedule> eventSchedules = new ArrayList<>();

        try {
            List<Integer> coordinateIdsOnFloor = new ArrayList<>();
            QueryBuilder<Coordinate, Integer> queryBuilder = helper.getCoordinateDao().queryBuilder();
            queryBuilder.where().eq(Constants.FLOOR, floor);
            if (queryBuilder.query().size() > 0) {
                for (Coordinate coordinate : queryBuilder.query())
                    coordinateIdsOnFloor.add(coordinate.getId());
            }

            QueryBuilder<EventSchedule, Integer> qb = helper.getEventScheduleDao().queryBuilder();
            qb.where().gt(Constants.END_DATETIME, new Date()).and().in(Constants.LOCATION_ID, coordinateIdsOnFloor);
            PreparedQuery<EventSchedule> pc = qb.prepare();
            if (helper.getEventScheduleDao().query(pc).size() > 0)
                eventSchedules = helper.getEventScheduleDao().query(pc);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EventScheduleDAO.class.getSimpleName());
        }

        return eventSchedules;
    }

}
