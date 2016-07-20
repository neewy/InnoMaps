package com.innopolis.maps.innomaps.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.innopolis.maps.innomaps.db.tablesrepresentations.Building;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingAuxiliaryCoordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingFloorOverlay;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingPhoto;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;
import com.innopolis.maps.innomaps.db.tablesrepresentations.CoordinateType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Edge;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EdgeType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreator;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventCreatorAppointment;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventFavorable;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EventSchedule;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Photo;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Room;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomPhoto;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Street;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by alnedorezov on 7/7/16.
 */

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = Constants.DATABASE_NAME;
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = Constants.DATABASE_VERSION;

    private Dao<CoordinateType, Integer> coordinateTypeDao = null;
    private Dao<EdgeType, Integer> edgeTypeDao = null;
    private Dao<RoomType, Integer> roomTypeDao = null;
    private Dao<Coordinate, Integer> coordinateDao = null;
    private Dao<Edge, Integer> edgeDao = null;
    private Dao<Street, Integer> streetDao = null;
    private Dao<Building, Integer> buildingDao = null;
    private Dao<Room, Integer> roomDao = null;
    private Dao<Photo, Integer> photoDao = null;
    private Dao<BuildingPhoto, Integer> buildingPhotoDao = null;
    private Dao<RoomPhoto, Integer> roomPhotoDao = null;
    private Dao<EventCreator, Integer> eventCreatorDao = null;
    private Dao<EventFavorable, Integer> eventDao = null;
    private Dao<EventSchedule, Integer> eventScheduleDao = null;
    private Dao<BuildingFloorOverlay, Integer> buildingFloorOverlayDao = null;
    private Dao<EventCreatorAppointment, Integer> eventCreatorAppointmentDao = null;
    private Dao<BuildingAuxiliaryCoordinate, Integer> buildingAuxiliaryCoordinateDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.d(Constants.DB_HELPER, Constants.ON_CREATE);
            // when tables are created and only then alter table fields that need to be altered
            TableUtils.createTable(connectionSource, CoordinateType.class);
            TableUtils.createTable(connectionSource, EdgeType.class);
            TableUtils.createTable(connectionSource, RoomType.class);
            TableUtils.createTable(connectionSource, Coordinate.class);
            TableUtils.createTable(connectionSource, Edge.class);
            TableUtils.createTable(connectionSource, Street.class);
            TableUtils.createTable(connectionSource, Building.class);
            TableUtils.createTable(connectionSource, Room.class);
            TableUtils.createTable(connectionSource, Photo.class);
            TableUtils.createTable(connectionSource, BuildingPhoto.class);
            TableUtils.createTable(connectionSource, RoomPhoto.class);
            TableUtils.createTable(connectionSource, EventCreator.class);
            TableUtils.createTable(connectionSource, EventFavorable.class);
            TableUtils.createTable(connectionSource, EventSchedule.class);
            TableUtils.createTable(connectionSource, BuildingFloorOverlay.class);
            TableUtils.createTable(connectionSource, EventCreatorAppointment.class);
            TableUtils.createTable(connectionSource, BuildingAuxiliaryCoordinate.class);
        } catch (SQLException e) {
            Log.d(Constants.DB_HELPER + Constants.UNDERSCORE + Constants.ERROR, Constants.CANNOT_CREATE_DATABASE, e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), Constants.ON_UPGRADE);

            TableUtils.dropTable(connectionSource, BuildingAuxiliaryCoordinate.class, true);
            TableUtils.dropTable(connectionSource, EventCreatorAppointment.class, true);
            TableUtils.dropTable(connectionSource, BuildingFloorOverlay.class, true);
            TableUtils.dropTable(connectionSource, EventSchedule.class, true);
            TableUtils.dropTable(connectionSource, EventFavorable.class, true);
            TableUtils.dropTable(connectionSource, EventCreator.class, true);
            TableUtils.dropTable(connectionSource, RoomPhoto.class, true);
            TableUtils.dropTable(connectionSource, BuildingPhoto.class, true);
            TableUtils.dropTable(connectionSource, Photo.class, true);
            TableUtils.dropTable(connectionSource, Room.class, true);
            TableUtils.dropTable(connectionSource, Building.class, true);
            TableUtils.dropTable(connectionSource, Street.class, true);
            TableUtils.dropTable(connectionSource, Edge.class, true);
            TableUtils.dropTable(connectionSource, Coordinate.class, true);
            TableUtils.dropTable(connectionSource, RoomType.class, true);
            TableUtils.dropTable(connectionSource, EdgeType.class, true);
            TableUtils.dropTable(connectionSource, CoordinateType.class, true);

            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), Constants.CANNOT_DROP_DATABASES, e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<CoordinateType, Integer> getCoordinateTypeDao() throws SQLException {
        if (coordinateTypeDao == null) {
            coordinateTypeDao = getDao(CoordinateType.class);
        }
        return coordinateTypeDao;
    }

    public Dao<EdgeType, Integer> getEdgeTypeDao() throws SQLException {
        if (edgeTypeDao == null) {
            edgeTypeDao = getDao(EdgeType.class);
        }
        return edgeTypeDao;
    }

    public Dao<RoomType, Integer> getRoomTypeDao() throws SQLException {
        if (roomTypeDao == null) {
            roomTypeDao = getDao(RoomType.class);
        }
        return roomTypeDao;
    }

    public Dao<Coordinate, Integer> getCoordinateDao() throws SQLException {
        if (coordinateDao == null) {
            coordinateDao = getDao(Coordinate.class);
        }
        return coordinateDao;
    }

    public Dao<Edge, Integer> getEdgeDao() throws SQLException {
        if (edgeDao == null) {
            edgeDao = getDao(Edge.class);
        }
        return edgeDao;
    }

    public Dao<Street, Integer> getStreetDao() throws SQLException {
        if (streetDao == null) {
            streetDao = getDao(Street.class);
        }
        return streetDao;
    }

    public Dao<Building, Integer> getBuildingDao() throws SQLException {
        if (buildingDao == null) {
            buildingDao = getDao(Building.class);
        }
        return buildingDao;
    }

    public Dao<Room, Integer> getRoomDao() throws SQLException {
        if (roomDao == null) {
            roomDao = getDao(Room.class);
        }
        return roomDao;
    }

    public Dao<Photo, Integer> getPhotoDao() throws SQLException {
        if (photoDao == null) {
            photoDao = getDao(Photo.class);
        }
        return photoDao;
    }

    public Dao<BuildingPhoto, Integer> getBuildingPhotoDao() throws SQLException {
        if (buildingPhotoDao == null) {
            buildingPhotoDao = getDao(BuildingPhoto.class);
        }
        return buildingPhotoDao;
    }

    public Dao<RoomPhoto, Integer> getRoomPhotoDao() throws SQLException {
        if (roomPhotoDao == null) {
            roomPhotoDao = getDao(RoomPhoto.class);
        }
        return roomPhotoDao;
    }

    public Dao<EventCreator, Integer> getEventCreatorDao() throws SQLException {
        if (eventCreatorDao == null) {
            eventCreatorDao = getDao(EventCreator.class);
        }
        return eventCreatorDao;
    }

    public Dao<EventFavorable, Integer> getEventDao() throws SQLException {
        if (eventDao == null) {
            eventDao = getDao(EventFavorable.class);
        }
        return eventDao;
    }

    public Dao<EventSchedule, Integer> getEventScheduleDao() throws SQLException {
        if (eventScheduleDao == null) {
            eventScheduleDao = getDao(EventSchedule.class);
        }
        return eventScheduleDao;
    }

    public Dao<BuildingFloorOverlay, Integer> getBuildingFloorOverlayDao() throws SQLException {
        if (buildingFloorOverlayDao == null) {
            buildingFloorOverlayDao = getDao(BuildingFloorOverlay.class);
        }
        return buildingFloorOverlayDao;
    }

    public Dao<EventCreatorAppointment, Integer> getEventCreatorAppointmentDao() throws SQLException {
        if (eventCreatorAppointmentDao == null) {
            eventCreatorAppointmentDao = getDao(EventCreatorAppointment.class);
        }
        return eventCreatorAppointmentDao;
    }

    public Dao<BuildingAuxiliaryCoordinate, Integer> getBuildingAuxiliaryCoordinateDao() throws SQLException {
        if (buildingAuxiliaryCoordinateDao == null) {
            buildingAuxiliaryCoordinateDao = getDao(BuildingAuxiliaryCoordinate.class);
        }
        return buildingAuxiliaryCoordinateDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        buildingAuxiliaryCoordinateDao = null;
        eventCreatorAppointmentDao = null;
        buildingFloorOverlayDao = null;
        eventScheduleDao = null;
        eventDao = null;
        eventCreatorDao = null;
        roomPhotoDao = null;
        buildingPhotoDao = null;
        photoDao = null;
        roomDao = null;
        buildingDao = null;
        streetDao = null;
        edgeDao = null;
        coordinateDao = null;
        roomTypeDao = null;
        edgeTypeDao = null;
        coordinateTypeDao = null;
    }
}
