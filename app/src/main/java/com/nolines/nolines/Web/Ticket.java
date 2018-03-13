package com.nolines.nolines.Web;

/**
 * Created by Andrew on 2018-03-13.
 */

public class Ticket {

    private String start_time;
    private String end_time;
    private String ride_name;
    private Double lat;
    private Double lon;
    private int alerts_left;
    private String alert_message;

    public Ticket(String start_time, String end_time, String ride_name, Double lat, Double lon, int alerts_left, String alert_message) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.ride_name = ride_name;
        this.lat = lat;
        this.lon = lon;
        this.alerts_left = alerts_left;
        this.alert_message = alert_message;
    }

    public String getStart_time(){return this.start_time;}
    public String getEnd_time(){return this.end_time;}
    public String getRide_name(){return this.ride_name;}
    public Double getLat(){return this.lat;}
    public Double getLon(){return this.lon;}
    public int getAlerts_left(){return this.alerts_left;}
    public String getAlert_message(){return this.alert_message;}
}
