package com.nolines.nolines.api.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by timot on 3/14/2018.
 */

public class Guest {

    private String name;
    private ArrayList<Ticket> tickets;

    public Guest(String name) {
        this.name = name;
        this.tickets = new ArrayList<Ticket>();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, 2);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        c2.add(Calendar.MINUTE, 4);
        this.tickets.add(new Ticket(c.getTime().toString(), c2.getTime().toString(), new Ride("Rollercoaster", 1)));
    }

    public String getName(){ return this.name; }
    public List<Ticket> getTickets(){ return this.tickets; }
}
