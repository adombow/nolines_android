package com.nolines.nolines.api.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by timot on 3/14/2018.
 */

public class Guest {

    private int id;
    private String name;
    private ArrayList<Ticket> tickets;

    public Guest(String name) {
        this.name = name;
        this.tickets = new ArrayList<Ticket>();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, 2);
        SimpleDateFormat df = new SimpleDateFormat(Ticket.dateFormat);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String startTime = df.format(c.getTime());
        this.tickets.add(new Ticket(startTime, new Ride("Rollercoaster", 1)));
    }

    public String getName(){ return this.name; }
    public List<Ticket> getTickets(){ return this.tickets; }
}
