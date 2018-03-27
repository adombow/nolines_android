package com.nolines.nolines.api.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by timot on 3/14/2018.
 */

public class Guest {

    private String name;
    private ArrayList<Ticket> tickets;

    public Guest(String name) {
        this.name = name;
        this.tickets = new ArrayList<>();
    }

    public String getName(){ return this.name; }
    public List<Ticket> getTickets(){ return this.tickets; }
}
