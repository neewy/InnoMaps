package com.innopolis.maps.innomaps.db.dataaccessobjects;

import java.util.List;

/**
 * Created by alnedorezov on 7/7/16.
 */
public interface Crud {
    public int create(Object item);

    public int update(Object item);

    public int delete(Object item);

    public Object findById(int id);

    public List findAll();
}
