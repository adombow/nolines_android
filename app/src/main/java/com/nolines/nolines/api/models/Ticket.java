package com.nolines.nolines.api.models;

/**
 * Created by timot on 3/14/2018.
 */

public class Ticket {

    private String startTime;
    private String endTime;
    private String rideName;
    private Double lat;
    private Double lon;
    private int alertsLeft;
    private String alertsMessage;

    public Ticket(String start_time, String end_time, String ride_name, Double lat, Double lon, int alerts_left, String alert_message) {
        this.startTime = start_time;
        this.endTime = end_time;
        this.rideName = ride_name;
        this.lat = lat;
        this.lon = lon;
        this.alertsLeft = alerts_left;
        this.alertsMessage = alert_message;
    }


    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getRideName() {
        return rideName;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public int getAlertsLeft() {
        return alertsLeft;
    }

    public String getAlertsMessage() {
        return alertsMessage;
    }
}
