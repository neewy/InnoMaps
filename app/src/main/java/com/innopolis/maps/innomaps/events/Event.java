package com.innopolis.maps.innomaps.events;

import android.support.annotation.NonNull;

import com.google.common.base.Predicate;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

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


    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() { return floor; }

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
    public int compareTo(@NonNull Event another) {
        return this.start.compareTo(another.getStart());
    }

    public static Predicate<Event> isToday = new Predicate<Event>() {
        public boolean apply(Event event) {
            Calendar c = new GregorianCalendar();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            Date today = c.getTime();
            c.add(Calendar.DATE, 1);
            Date tomorrow = c.getTime();
            return (event.getStart().after(today) && event.getEnd().before(tomorrow));
        }
    };

    public static Predicate<Event> isTomorrow = new Predicate<Event>() {
        public boolean apply(Event event) {
            Calendar c = new GregorianCalendar();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
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

    public static Comparator<Event> summaryComparator = new Comparator<Event>() {
        @Override
        public int compare(Event lhs, Event rhs) {
            return lhs.getSummary().compareTo(rhs.getSummary());
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (!summary.equals(event.summary)) return false;
        if (htmlLink != null ? !htmlLink.equals(event.htmlLink) : event.htmlLink != null)
            return false;
        if (!start.equals(event.start)) return false;
        if (!end.equals(event.end)) return false;
        if (eventID != null ? !eventID.equals(event.eventID) : event.eventID != null) return false;
        if (checked != null ? !checked.equals(event.checked) : event.checked != null) return false;
        if (description != null ? !description.equals(event.description) : event.description != null)
            return false;
        if (creatorName != null ? !creatorName.equals(event.creatorName) : event.creatorName != null)
            return false;
        if (creatorEmail != null ? !creatorEmail.equals(event.creatorEmail) : event.creatorEmail != null)
            return false;
        if (telegramLogin != null ? !telegramLogin.equals(event.telegramLogin) : event.telegramLogin != null)
            return false;
        if (telegramGroup != null ? !telegramGroup.equals(event.telegramGroup) : event.telegramGroup != null)
            return false;
        if (building != null ? !building.equals(event.building) : event.building != null)
            return false;
        if (floor != null ? !floor.equals(event.floor) : event.floor != null) return false;
        if (room != null ? !room.equals(event.room) : event.room != null) return false;
        if (latitude != null ? !latitude.equals(event.latitude) : event.latitude != null)
            return false;
        return !(longitude != null ? !longitude.equals(event.longitude) : event.longitude != null);

    }

    @Override
    public int hashCode() {
        int result = summary.hashCode();
        result = 31 * result + (htmlLink != null ? htmlLink.hashCode() : 0);
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + (eventID != null ? eventID.hashCode() : 0);
        result = 31 * result + (checked != null ? checked.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (creatorName != null ? creatorName.hashCode() : 0);
        result = 31 * result + (creatorEmail != null ? creatorEmail.hashCode() : 0);
        result = 31 * result + (telegramLogin != null ? telegramLogin.hashCode() : 0);
        result = 31 * result + (telegramGroup != null ? telegramGroup.hashCode() : 0);
        result = 31 * result + (building != null ? building.hashCode() : 0);
        result = 31 * result + (floor != null ? floor.hashCode() : 0);
        result = 31 * result + (room != null ? room.hashCode() : 0);
        result = 31 * result + (latitude != null ? latitude.hashCode() : 0);
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        return result;
    }
}
