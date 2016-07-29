package com.innopolis.maps.innomaps.events;

import android.support.annotation.NonNull;

import com.google.common.base.Predicate;
import com.innopolis.maps.innomaps.maps.LatLngFlr;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

public class Event implements Comparable<Event> {

    private String summary;
    private String htmlLink;
    private Date start;
    private Date end;
    private int eventID;
    private int eventScheduleId;
    private boolean checked;
    private String description;
    private String creatorName;
    private String creatorEmail;
    private String building;
    private String floorStr;
    private String room;
    private LatLngFlr coordinateLatLngFlr;

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

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
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

    public String getFloorStr() {
        return floorStr;
    }

    public void setFloorStr(String floorStr) {
        this.floorStr = floorStr;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public LatLngFlr getCoordinateLatLngFlr() {
        return coordinateLatLngFlr;
    }

    public void setCoordinateLatLngFlr(LatLngFlr coordinateLatLngFlr) {
        this.coordinateLatLngFlr = coordinateLatLngFlr;
    }

    public int getEventScheduleId() {
        return eventScheduleId;
    }

    public void setEventScheduleId(int eventScheduleId) {
        this.eventScheduleId = eventScheduleId;
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
            c.add(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
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
        if (!(o instanceof Event)) return false;

        Event event = (Event) o;

        if (getEventID() != event.getEventID()) return false;
        if (getEventScheduleId() != event.getEventScheduleId()) return false;
        if (isChecked() != event.isChecked()) return false;
        if (getSummary() != null ? !getSummary().equals(event.getSummary()) : event.getSummary() != null)
            return false;
        if (getHtmlLink() != null ? !getHtmlLink().equals(event.getHtmlLink()) : event.getHtmlLink() != null)
            return false;
        if (getStart() != null ? !getStart().equals(event.getStart()) : event.getStart() != null)
            return false;
        if (getEnd() != null ? !getEnd().equals(event.getEnd()) : event.getEnd() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(event.getDescription()) : event.getDescription() != null)
            return false;
        if (getCreatorName() != null ? !getCreatorName().equals(event.getCreatorName()) : event.getCreatorName() != null)
            return false;
        if (getCreatorEmail() != null ? !getCreatorEmail().equals(event.getCreatorEmail()) : event.getCreatorEmail() != null)
            return false;
        if (getBuilding() != null ? !getBuilding().equals(event.getBuilding()) : event.getBuilding() != null)
            return false;
        if (getFloorStr() != null ? !getFloorStr().equals(event.getFloorStr()) : event.getFloorStr() != null)
            return false;
        if (getRoom() != null ? !getRoom().equals(event.getRoom()) : event.getRoom() != null)
            return false;
        return coordinateLatLngFlr != null ? coordinateLatLngFlr.equals(event.coordinateLatLngFlr) : event.coordinateLatLngFlr == null;

    }

    @Override
    public int hashCode() {
        int result = getSummary() != null ? getSummary().hashCode() : 0;
        result = 31 * result + (getHtmlLink() != null ? getHtmlLink().hashCode() : 0);
        result = 31 * result + (getStart() != null ? getStart().hashCode() : 0);
        result = 31 * result + (getEnd() != null ? getEnd().hashCode() : 0);
        result = 31 * result + getEventID();
        result = 31 * result + getEventScheduleId();
        result = 31 * result + (isChecked() ? 1 : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getCreatorName() != null ? getCreatorName().hashCode() : 0);
        result = 31 * result + (getCreatorEmail() != null ? getCreatorEmail().hashCode() : 0);
        result = 31 * result + (getBuilding() != null ? getBuilding().hashCode() : 0);
        result = 31 * result + (getFloorStr() != null ? getFloorStr().hashCode() : 0);
        result = 31 * result + (getRoom() != null ? getRoom().hashCode() : 0);
        result = 31 * result + (coordinateLatLngFlr != null ? coordinateLatLngFlr.hashCode() : 0);
        return result;
    }
}
