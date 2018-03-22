package com.nolines.nolines.api.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 2018-03-20.
 */

/*
 Singleton class for a Global ticket list
 */
public class TicketsHolder {

    private static class Holder{
        private static final TicketsHolder INSTANCE = new TicketsHolder();
    }

    private List<Ticket> tickets;

    private TicketsHolder() {tickets = new ArrayList<Ticket>();}
    public static TicketsHolder getInstance() {return Holder.INSTANCE;}

    public void addItem(Ticket ticket) {this.tickets.add(ticket);}
    public void removeItem(int pos) {this.tickets.remove(pos);}
    public Ticket getItem(int pos) {return this.tickets.get(pos);}
}
