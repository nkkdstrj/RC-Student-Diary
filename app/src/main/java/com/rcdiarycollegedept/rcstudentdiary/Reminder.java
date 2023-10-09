package com.rcdiarycollegedept.rcstudentdiary;

public class Reminder {
    private String id; // Unique identifier for the reminder
    private String date;
    private String eventName;
    private String eventTime;

    public Reminder() {
        // Default constructor required for Firebase
    }

    public Reminder(String id, String date, String eventName, String eventTime) {
        this.id = id;
        this.date = date;
        this.eventName = eventName;
        this.eventTime = eventTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
}
