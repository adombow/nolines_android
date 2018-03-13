package com.nolines.nolines.Web;

import java.util.ArrayList;

/**
 * Created by Andrew on 2018-03-13.
 */

public class Guest {

    private String name;
    private ArrayList<Ticket> tickets;

    public Guest(String name) {
        this.name = name;
        this.tickets = new ArrayList<Ticket>();
    }

    public String getName(){ return this.name; }
    public Ticket getTicket(int index){
        return this.tickets.get(index);
    }
}
