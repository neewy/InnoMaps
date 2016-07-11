package com.innopolis.maps.innomaps;

import android.test.AndroidTestCase;

import com.innopolis.maps.innomaps.db.Constants;
import com.innopolis.maps.innomaps.db.DatabaseSync;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateTypeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.EdgeTypeDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.RoomTypeDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.CoordinateType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.EdgeType;
import com.innopolis.maps.innomaps.db.tablesrepresentations.RoomType;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by alnedorezov on 7/7/16.
 */
public class DatabaseSyncTest extends AndroidTestCase {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testTypesSync() throws ParseException {
        DatabaseSync databaseSync = new DatabaseSync(this.getContext());
        databaseSync.saveLastSyncDate(com.innopolis.maps.innomaps.network.Constants.serverDateFormat.parse(Constants.DEFAULT_SYNC_DATE));
        CoordinateTypeDAO coordinateTypeDAO = new CoordinateTypeDAO(this.getContext());
        EdgeTypeDAO edgeTypeDAO = new EdgeTypeDAO(this.getContext());
        RoomTypeDAO roomTypeDAO = new RoomTypeDAO(this.getContext());
        databaseSync.performSyncWithServer();
        databaseSync.saveLastSyncDate(new Date());

        List<CoordinateType> coordinateTypes = (List<CoordinateType>) coordinateTypeDAO.findAll();
        List<EdgeType> edgeTypes = (List<EdgeType>) edgeTypeDAO.findAll();
        List<RoomType> roomTypes = (List<RoomType>) roomTypeDAO.findAll();

        assertTrue(coordinateTypes.size() >= 11);
        assertTrue(edgeTypes.size() >= 2);
        assertTrue(roomTypes.size() >= 7);
    }
}
