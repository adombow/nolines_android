package com.nolines.nolines.api.models;

/**
 * Created by timot on 3/14/2018.
 */

public class Ticket {

    private String startTime; //dow mon dd hh:mm:ss tzn yyyy i.e. date.tostring()
    private String endTime; //dow mon dd hh:mm:ss tzn yyyy

    private int notificationsLeft;
    private Ride ride;

    public Ticket(String start_time, String end_time, Ride ride) {
        this.startTime = start_time;
        this.endTime = end_time;
        this.ride = ride;
        this.notificationsLeft = 4;
    }


    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Ride getRide() {
        return ride;
    }

    public int getNotificationsLeft() {
        return notificationsLeft;
    }

    public void reduceNotificationsLeft() {
        this.notificationsLeft--;
    }
}
