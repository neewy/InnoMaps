package com.innopolis.maps.innomaps.orm;


import com.orm.SugarRecord;



public class Poi extends SugarRecord {

    int id;
    String name;
    String building;
    String floor;
    String room;
    String latitude;
    String longitude;
    String type;
    String attr;

    public Poi() {
    }

    public Poi(int id, String name, String building, String floor, String room, String latitude, String longitude, String type, String attr) {
        this.id = id;
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.room = room;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.attr = attr;
    }
}
