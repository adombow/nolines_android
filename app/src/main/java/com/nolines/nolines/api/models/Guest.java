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
    private List<Ticket> tickets;

    public Guest(String name) {
        this.name = name;
        this.tickets = new ArrayList<Ticket>();
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, 2);
        SimpleDateFormat df = new SimpleDateFormat(Ticket.dateFormat);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String startTime = df.format(c.getTime());
        this.tickets.add(new Ticket(0, startTime, new Ride("Rollercoaster", 1)));
    }

    public String getName(){ return this.name; }
    public List<Ticket> getTickets(){

        if(tickets == null){
            tickets = new ArrayList<>();
        }

        return tickets;
    }
    /**
     * Gets a list of all of this guest's tickets that are available for the date represented by testDate
     * @return List of all the tickets with start times set for testDate and an empty
     * list if none are available.
     */
    public List<Ticket> getTicketsForDate(Calendar testDate){
        List<Ticket> datedTickets = new ArrayList<Ticket>();

        for(Ticket ticket : tickets){
            //Look through all of the guest's tickets and check if they have any for the selected day
            if (ticket.getTime() != null) {
                Calendar ticketDate = ticket.getLocalDatetimeFromTime();
                if (ticketDate.get(Calendar.YEAR) == testDate.get(Calendar.YEAR) &&
                        ticketDate.get(Calendar.MONTH) == testDate.get(Calendar.MONTH) &&
                        ticketDate.get(Calendar.DATE) == testDate.get(Calendar.DATE)) {
                    datedTickets.add(ticket);
                }
            }
        }
        return datedTickets;
    }
}
