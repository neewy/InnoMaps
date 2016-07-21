package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EdgeType;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class EdgeTypeDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public EdgeTypeDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        EdgeType edgeType = (EdgeType) item;
        try {
            index = helper.getEdgeTypeDao().create(edgeType);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EdgeTypeDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        EdgeType edgeType = (EdgeType) item;

        try {
            helper.getEdgeTypeDao().update(edgeType);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EdgeTypeDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        EdgeType edgeType = (EdgeType) item;

        try {
            helper.getEdgeTypeDao().delete(edgeType);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EdgeTypeDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        EdgeType edgeType = null;
        try {
            edgeType = helper.getEdgeTypeDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EdgeTypeDAO.class.getSimpleName());
        }
        return edgeType;
    }

    @Override
    public List<?> findAll() {

        List<EdgeType> items = new ArrayList<>();

        try {
            items = helper.getEdgeTypeDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EdgeTypeDAO.class.getSimpleName());
        }

        return items;
    }

    @Override
    public Object getObjectWithMaxId() {
        EdgeType edgeType = null;
        try {
            QueryBuilder<EdgeType, Integer> qBuilder = helper.getEdgeTypeDao().queryBuilder();
            qBuilder.orderBy("id", false); // false for descending order
            qBuilder.limit(1);
            edgeType = helper.getEdgeTypeDao().queryForId(qBuilder.query().get(0).getId());
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    PhotoDAO.class.getSimpleName());
        }
        return edgeType;
    }

    @Override
    public int createOrUpdateIfExists(Object item) {
        int index = -1;
        Dao.CreateOrUpdateStatus createOrUpdateStatus;
        EdgeType edgeType = (EdgeType) item;
        try {
            createOrUpdateStatus = helper.getEdgeTypeDao().createOrUpdate(edgeType);
            index = createOrUpdateStatus.getNumLinesChanged();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EdgeTypeDAO.class.getSimpleName());
        }

        return index;
    }
}
