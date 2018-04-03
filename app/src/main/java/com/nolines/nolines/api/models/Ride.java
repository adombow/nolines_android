package com.nolines.nolines.api.models;

import android.support.annotation.IntDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
    @SerializedName("picture_url")
    private String photoURL;

    @SerializedName("window_date")
    private String windowDate;

    @SerializedName("morning_windows")
    private List<RideWindow> morningWindows;
    @SerializedName("afternoon_windows")
    private List<RideWindow> afternoonWindows;
    @SerializedName("evening_windows")
    private List<RideWindow> eveningWindows;

    public final static int MORNING = 0;
    public final static int AFTERNOON = 1;
    public final static int EVENING = 2;

    @IntDef({MORNING, AFTERNOON, EVENING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TimeFrame {}

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

    public String getPhotoURL() {
        return photoURL;
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

    public String getWindowDate() {
        return windowDate;
    }

    public List<RideWindow> getRideWindows(@TimeFrame int timeframe) {

        switch (timeframe){
            case MORNING:
                return morningWindows;
            case AFTERNOON:
                return afternoonWindows;
            case EVENING:
                return eveningWindows;
            default:
                return morningWindows;
        }
    }

    public Ride(String name, int id){
        this.name = name;
        this.id = id;
    }
}