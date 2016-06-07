package com.innopolis.maps.innomaps.app;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Predicate;
import com.innopolis.maps.innomaps.events.Event;

import java.util.HashMap;
import java.util.List;

import static com.innopolis.maps.innomaps.database.TableFields.ATTR;
import static com.innopolis.maps.innomaps.database.TableFields.BUILDING;
import static com.innopolis.maps.innomaps.database.TableFields.FLOOR;
import static com.innopolis.maps.innomaps.database.TableFields.POI_NAME;
import static com.innopolis.maps.innomaps.database.TableFields.ROOM;
import static com.innopolis.maps.innomaps.database.TableFields.TYPE;
import static com.innopolis.maps.innomaps.database.TableFields.LATITUDE;
import static com.innopolis.maps.innomaps.database.TableFields.LONGITUDE;

public class SearchableItem implements Comparable<SearchableItem> {
    public String name;
    public String type;
    public String id;
    public String building;
    public String floor;
    public String room;
    public LatLng coordinate;

    public SearchableItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public LatLng getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(LatLng coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public int compareTo(SearchableItem another) {
        return this.getName().compareTo(another.getName());
    }

    public static void addEvents(List<SearchableItem> items, List<Event> events) {
        for (Event event : events) {
            SearchableItem searchableItem = new SearchableItem();
            searchableItem.setName(event.getSummary());
            searchableItem.setType("event");
            searchableItem.setId(event.getEventID());
            searchableItem.setBuilding(event.getBuilding());
            searchableItem.setFloor(event.getFloor());
            searchableItem.setRoom(event.getRoom());
            searchableItem.setCoordinate(new LatLng(Double.parseDouble(event.getLatitude()), Double.parseDouble(event.getLongitude())));
            items.add(searchableItem);
        }
    }

    // TODO: Extract string constants
    public static void addPois(List<SearchableItem> items, List<HashMap<String, String>> pois) {
        for (HashMap<String, String> poi : pois) {
            SearchableItem searchableItem = new SearchableItem();
            if (poi.get("number") != null) {
                searchableItem.setName(poi.get("number"));
            } else {
                if (poi.get(POI_NAME).toLowerCase().contains("wc")) {
                    searchableItem.setName(poi.get(POI_NAME) + " | " + poi.get(ATTR));
                } else {
                    searchableItem.setName(poi.get(POI_NAME));
                }
            }
            searchableItem.setType(poi.get(TYPE));
            searchableItem.setId(poi.get("_id"));
            searchableItem.setBuilding(poi.get(BUILDING));
            searchableItem.setFloor(poi.get(FLOOR));
            searchableItem.setRoom(poi.get(ROOM));
            searchableItem.setCoordinate(new LatLng(Double.parseDouble(poi.get(LATITUDE)), Double.parseDouble(poi.get(LONGITUDE))));
            items.add(searchableItem);
        }
    }

    public static Predicate<SearchableItem> isWc = new Predicate<SearchableItem>() {
        @Override
        public boolean apply(SearchableItem input) {
            return input.getType().toLowerCase().equals("wc");
        }
    };

    public static Predicate<SearchableItem> isFood = new Predicate<SearchableItem>() {
        @Override
        public boolean apply(SearchableItem input) {
            return input.getType().toLowerCase().equals("food");
        }
    };

    public static Predicate<SearchableItem> isEvent = new Predicate<SearchableItem>() {
        @Override
        public boolean apply(SearchableItem input) {
            return input.getType().toLowerCase().equals("event");
        }
    };

    public static Predicate<SearchableItem> isOther = new Predicate<SearchableItem>() {
        @Override
        public boolean apply(SearchableItem input) {
            Boolean res;
            res = !(input.getType().toLowerCase().equals("event") || input.getType().toLowerCase().equals("food") || input.getType().toLowerCase().equals("wc"));
            return res;
        }
    };

}
