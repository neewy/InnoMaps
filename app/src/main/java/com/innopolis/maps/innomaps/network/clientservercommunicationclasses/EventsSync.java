package com.innopolis.maps.innomaps.network.clientservercommunicationclasses;

import java.util.List;

/**
 * Created by alnedorezov on 7/14/16.
 */

public class EventsSync {
    private List<Integer> eventCreatorIds;
    private List<Integer> eventIds;
    private List<Integer> eventScheduleIds;

    public EventsSync(List<Integer> eventCreatorIds, List<Integer> eventIds, List<Integer> eventScheduleIds) {
        this.eventCreatorIds = eventCreatorIds;
        this.eventIds = eventIds;
        this.eventScheduleIds = eventScheduleIds;
    }

    // For deserialization with Jackson
    public EventsSync() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public void addEventCreatorId(Integer eventCreatorId) {
        this.eventCreatorIds.add(eventCreatorId);
    }

    public void setEventCreatorId(int index, Integer eventCreatorId) {
        this.eventCreatorIds.set(index, eventCreatorId);
    }

    public Integer getEventCreatorId(int index) {
        return eventCreatorIds.get(index);
    }

    public void removeEventCreatorId(int index) {
        this.eventCreatorIds.remove(index);
    }

    public List<Integer> getEventCreatorIds() {
        return eventCreatorIds;
    }


    public void addEventId(Integer eventId) {
        this.eventIds.add(eventId);
    }

    public void setEventId(int index, Integer eventId) {
        this.eventIds.set(index, eventId);
    }

    public Integer getEventId(int index) {
        return eventIds.get(index);
    }

    public void removeEventId(int index) {
        this.eventIds.remove(index);
    }

    public List<Integer> getEventIds() {
        return eventIds;
    }


    public void addEventScheduleId(Integer eventScheduleId) {
        this.eventScheduleIds.add(eventScheduleId);
    }

    public void setEventScheduleId(int index, Integer eventScheduleId) {
        this.eventScheduleIds.set(index, eventScheduleId);
    }

    public Integer getEventScheduleId(int index) {
        return eventScheduleIds.get(index);
    }

    public void removeEventScheduleId(int index) {
        this.eventScheduleIds.remove(index);
    }

    public List<Integer> getEventScheduleIds() {
        return eventScheduleIds;
    }
}