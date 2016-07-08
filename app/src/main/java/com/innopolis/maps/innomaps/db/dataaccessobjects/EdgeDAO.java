package com.innopolis.maps.innomaps.db.dataaccessobjects;

import android.content.Context;
import android.util.Log;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseHelper;
import com.innopolis.maps.innomaps.db.DatabaseManager;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Edge;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alnedorezov on 7/8/16.
 */
public class EdgeDAO implements ExtendedCrud {

    private DatabaseHelper helper;

    public EdgeDAO(Context context) {
        DatabaseManager.setHelper(context);
        helper = DatabaseManager.getHelper();
    }

    @Override
    public int create(Object item) {

        int index = -1;

        Edge edge = (Edge) item;
        try {
            index = helper.getEdgeDao().create(edge);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EdgeDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int update(Object item) {

        int index = -1;

        Edge edge = (Edge) item;

        try {
            helper.getEdgeDao().update(edge);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EdgeDAO.class.getSimpleName());
        }

        return index;
    }

    @Override
    public int delete(Object item) {

        int index = -1;

        Edge edge = (Edge) item;

        try {
            helper.getEdgeDao().delete(edge);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EdgeDAO.class.getSimpleName());
        }

        return index;

    }

    @Override
    public Object findById(int id) {

        Edge edge = null;
        try {
            edge = helper.getEdgeDao().queryForId(id);
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EdgeDAO.class.getSimpleName());
        }
        return edge;
    }

    @Override
    public List<?> findAll() {

        List<Edge> items = new ArrayList<>();

        try {
            items = helper.getEdgeDao().queryForAll();
        } catch (SQLException e) {
            Log.d(Constants.DAO_ERROR, Constants.SQL_EXCEPTION_IN + Constants.SPACE +
                    EdgeDAO.class.getSimpleName());
        }

        return items;
    }
}
