package com.innopolis.maps.innomaps;

import android.test.AndroidTestCase;

import com.innopolis.maps.innomaps.db.dataaccessobjects.CoordinateDAO;
import com.innopolis.maps.innomaps.db.tablesrepresentations.Coordinate;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.List;

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

        List<Coordinate> coordinatesFromMobileDatabase;

        Coordinate newCoordinate = new Coordinate(1, 55.7541793, 48.744085, 1, 2, "Innopolis University", universityDescription, modifiedDateTime);
        coordinateDAO.create(newCoordinate);

        coordinatesFromMobileDatabase = (List<Coordinate>) coordinateDAO.findAll();

        assertEquals(newCoordinate, coordinatesFromMobileDatabase.get(0));
    }
}
