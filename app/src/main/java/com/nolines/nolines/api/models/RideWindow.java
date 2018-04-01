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
    @SerializedName("end_time")
    private String endTime;

    private Date startDate;

    public RideWindow(int id, String startTime, String endTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
