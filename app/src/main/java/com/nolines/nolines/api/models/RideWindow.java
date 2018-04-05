package com.nolines.nolines.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by timot on 3/22/2018.
 */

public class RideWindow {
    private int id;
    @SerializedName("start_time")
    private String startTime;
    private int sold;
    private int limit;

    public static int windowLength = 120;//3600 //length of ride window (in seconds)

    public int getId() {
        return id;
    }

    public String getIdString(){
        String s = Integer.toString(id);

        return s;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getSold() {
        return sold;
    }

    public int getLimit() {
        return limit;
    }
}
