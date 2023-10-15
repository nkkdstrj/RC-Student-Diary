package com.rcdiarycollegedept.rcstudentdiary;

import java.util.Date;

public class PublicReminder {
    private String eventName;
    private String eventTime;
    private String eventInfo;
    private Date date;

    public PublicReminder() {
        // Default constructor required for Firebase
    }

    public PublicReminder(String eventName, String eventTime, String eventInfo, Date date) {
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.eventInfo = eventInfo;
        this.date = date;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public String getEventInfo() {
        return eventInfo;
    }

    public Date getDate() {
        return date;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public void setEventInfo(String eventInfo) {
        this.eventInfo = eventInfo;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

