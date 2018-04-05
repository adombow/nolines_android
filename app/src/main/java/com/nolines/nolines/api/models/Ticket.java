package com.nolines.nolines.api.models;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by timot on 3/14/2018.
 */

public class Ticket implements Serializable{

    private int id;
    private String time;
    private Ride ride;

    public static final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public Ticket(int id, String time, Ride ride) {
        this.id = id;
        this.time = time;
        this.ride = ride;
    }

    public int getId() { return id; }

    public String getTime() { return time; }

    public Ride getRide() {
        return ride;
    }

    public Calendar getLocalDatetimeFromTime(){
        DateFormat df = new SimpleDateFormat(Ticket.dateFormat);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar cal = Calendar.getInstance();

        try{
            Date date = df.parse(this.time);
            df.setTimeZone(TimeZone.getDefault());
            cal.setTime(df.parse(df.format(date)));
        } catch (ParseException e) {
        }
        return cal;
    }

    /**
     * Converts the UTC time string in this ticket to a string containing only the time portion
     * (none of the date) converted to local time and in the format timeFormat
     * @return The time portion of the UTC time string from this ticket in local time
     */
    public String getLocalTimeStringFromTime(String timeFormat){
        DateFormat df = new SimpleDateFormat(Ticket.dateFormat);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
        formatter.setTimeZone(TimeZone.getDefault());

        String time;
        try{
            time = formatter.format(df.parse(this.time));
        } catch (ParseException e) {
            time = "";
        }
        return time;
    }
}
