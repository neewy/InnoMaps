package com.innopolis.maps.innomaps.app;

import com.google.common.base.Predicate;

import java.util.Calendar;
import java.util.Date;

public class Event implements Comparable<Event> {

    private String summary;
    private String htmlLink;
    private Date start;
    private Date end;
    private String eventID;
    private String checked;
    private String description;
    private String creatorName;
    private String creatorEmail;
    private String telegramLogin;
    private String telegramGroup;
    private String building;
    private String floor;
    private String room;
    private String latitude;
    private String longitude;

    public Event() {
    }

    public Event(String summary, String htmlLink, Date start,
                 Date end, String eventID, String checked,
                 String description, String creatorName, String creatorEmail,
                 String telegramLogin, String telegramGroup, String building,
                 String floor, String room, String latitude, String longitude) {
        this.summary = summary;
        this.htmlLink = htmlLink;
        this.start = start;
        this.end = end;
        this.eventID = eventID;
        this.checked = checked;
        this.description = description;
        this.creatorName = creatorName;
        this.creatorEmail = creatorEmail;
        this.telegramLogin = telegramLogin;
        this.telegramGroup = telegramGroup;
        this.building = building;
        this.floor = floor;
        this.room = room;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getHtmlLink() {
        return htmlLink;
    }

    public void setHtmlLink(String htmlLink) {
        this.htmlLink = htmlLink;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getChecked() {
        return checked;
    }

    public void setChecked(String checked) {
        this.checked = checked;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getTelegramLogin() {
        return telegramLogin;
    }

    public void setTelegramLogin(String telegramLogin) {
        this.telegramLogin = telegramLogin;
    }

    public String getTelegramGroup() {
        return telegramGroup;
    }

    public void setTelegramGroup(String telegramGroup) {
        this.telegramGroup = telegramGroup;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public int compareTo(Event another) {
        return this.start.compareTo(another.getStart());
    }

    public static Predicate<Event> isToday = new Predicate<Event>() {
        public boolean apply(Event event) {
            Date today = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(today);
            c.add(Calendar.DATE, 1);
            Date tomorrow = c.getTime();
            return (event.getStart().after(today) && event.getEnd().before(tomorrow));
        }
    };

    public static Predicate<Event> isTomorrow = new Predicate<Event>() {
        public boolean apply(Event event) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);
            Date tomorrow = c.getTime();
            c.add(Calendar.DATE, 1);
            Date afterTomorrow = c.getTime();
            return (event.getStart().after(tomorrow) && event.getEnd().before(afterTomorrow));
        }
    };

    public static Predicate<Event> isThisWeek = new Predicate<Event>() {
        public boolean apply(Event event) {
            Date today = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(today);
            c.add(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()+6);
            Date endOfWeek = c.getTime();
            return (event.getStart().after(today) && event.getEnd().before(endOfWeek));
        }
    };

}
