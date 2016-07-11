package com.innopolis.maps.innomaps;

import android.test.AndroidTestCase;

import com.innopolis.maps.innomaps.db.dataaccessobjects.BuildingDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.BuildingFloorOverlayDAO;
import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Building;
import com.innopolis.maps.innomaps.db.tablesrepresentations.BuildingFloorOverlay;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;

/**
 * Created by alnedorezov on 7/7/16.
 */
public class DatabaseSyncTest extends AndroidTestCase {

    String modifiedDateTime;
    String universityDescription;

    @Before
    public void setUp() throws Exception {
        modifiedDateTime = "2016-02-03 04:05:06.7";
        universityDescription = "Specializing in the field " +
                "of modern information technologies, Innopolis University is not only one of Russia’s youngest universities," +
                " but also the new city’s intellectual center.\n" +
                "The teaching staff consists of leading Russian and foreign IT specialists and robotic science.\n" +
                "Driven by the demands of both business and industry, the educational programs are committed to producing" +
                " a high-quality stream of professionals for companies located in Innopolis.";
    }

    @Test
    public void testWritingNewCoordinateDataToMobileDB() throws ParseException {
        CoordinateDAO coordinateDAO = new CoordinateDAO(this.getContext());

        Coordinate coordinateFromMobileDatabaseWithMaxId;

        Coordinate newCoordinate = new Coordinate(1, 55.7541793, 48.744085, 1, 2, "Innopolis University", universityDescription, modifiedDateTime);
        coordinateDAO.create(newCoordinate);

        coordinateFromMobileDatabaseWithMaxId = (Coordinate) coordinateDAO.getObjectWithMaxId();

        assertEquals(newCoordinate, coordinateFromMobileDatabaseWithMaxId);
    }

    @Test
    public void testWritingNewBuildingDataToMobileDB() throws ParseException {
        BuildingDAO buildingDAO = new BuildingDAO(this.getContext());

        Building buildingFromMobileDatabaseWithMaxId;

        Building newBuilding = new Building(1, String.valueOf(1), null, universityDescription, 1, 1, modifiedDateTime);
        buildingDAO.create(newBuilding);

        buildingFromMobileDatabaseWithMaxId = (Building) buildingDAO.getObjectWithMaxId();

        assertEquals(newBuilding, buildingFromMobileDatabaseWithMaxId);
    }

    @Test
    public void testWritingNewBuildingFloorOverlayDataToMobileDB() throws ParseException {
        BuildingFloorOverlayDAO buildingFloorOverlayDAO = new BuildingFloorOverlayDAO(this.getContext());

        BuildingFloorOverlay buildingFloorOverlayFromMobileDatabaseWithMaxId;

        BuildingFloorOverlay newBuildingFloorOverlay = new BuildingFloorOverlay(1, 1, 2, 3, 4.0, 5.0, 6.0, 7.0, "2016-06-30 00:12:19.56");
        buildingFloorOverlayDAO.create(newBuildingFloorOverlay);

        buildingFloorOverlayFromMobileDatabaseWithMaxId = (BuildingFloorOverlay) buildingFloorOverlayDAO.getObjectWithMaxId();

        assertEquals(newBuildingFloorOverlay, buildingFloorOverlayFromMobileDatabaseWithMaxId);
    }
}
