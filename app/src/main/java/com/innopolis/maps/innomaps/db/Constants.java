package com.innopolis.maps.innomaps.db;

/**
 * Created by alnedorezov on 7/7/16.
 */
public final class Constants {
    public static final String DATABASE_NAME = "innomapsMobile0002.db";

    public static final String DAO_ERROR = "DAO_ERROR";
    public static final String SQL_EXCEPTION_IN = "SQL Exception in";
    public static final String DAO = "DAO";
    public static final char SPACE = ' ';

    public static final String COORDINATE_STARTING_FROM_CAPITAL_LETTER = "Coordinate";
    public static final String BUILDING_STARTING_FROM_CAPITAL_LETTER = "Building";

    public static final String DB_HELPER_ERROR = "DB_HELPER_ERROR";
    public static final String ON_CREATE = "onCreate";
    public static final String ON_UPGRADE = "onUpgrade";
    public static final String CANNOT_CREATE_DATABASE = "Can't create database";
    public static final String CANNOT_DROP_DATABASES = "Can't drop databases";

    // Utility classes, which are a collection of static members, are not meant to be instantiated
    private Constants() {
        // Even abstract utility classes, which can be extended, should not have public constructors.
        // Java adds an implicit public constructor to every class which does not define at least one explicitly.
        // Hence, at least one non-public constructor should be defined
    }
}
