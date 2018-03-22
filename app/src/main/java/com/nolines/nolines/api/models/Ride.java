package com.nolines.nolines.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by timot on 3/14/2018.
 */

public class Ride {
    private int id;
    private String name;
    private Double lat;
    private Double lon;
    @SerializedName("wait_time")
    private int waitTime;
    @SerializedName("max_speed")
    private String maxSpeed;
    @SerializedName("ride_type")
    private String rideType;
    private int photoID;
    @SerializedName("windows")
    private List<RideWindow> RideWindows;


    public String getName() {
        return name;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public int getPhotoID() {
        return photoID;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public String getRideType() {
        return rideType;
    }

    public int getId() {
        return id;
    }

    public List<RideWindow> getRideWindows() {
        return RideWindows;
    }

    public void setRideWindows(List<RideWindow> rideWindows) {
        RideWindows = rideWindows;
    }
}
