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

    public int getId() {
        return id;
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
