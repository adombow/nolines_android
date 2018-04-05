package com.nolines.nolines.api.models;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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

    public Calendar getLocalDatetimeFromTime(){
        DateFormat df = new SimpleDateFormat(Ticket.dateFormat);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar cal = Calendar.getInstance();

        try{
            Date date = df.parse(this.startTime);
            df.setTimeZone(TimeZone.getDefault());
            cal.setTime(df.parse(df.format(date)));
        } catch (ParseException e) {
        }
        return cal;
    }
}
