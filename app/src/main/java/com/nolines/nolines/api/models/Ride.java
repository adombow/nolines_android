package com.nolines.nolines.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by timot on 3/14/2018.
 */

public class Ride {
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

    public Ride(String name, Double lat, Double lon, int wait_time, int photoID) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.waitTime = wait_time;
        this.photoID = photoID;
    }


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
}
