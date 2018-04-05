package com.nolines.nolines.viewmodels;

/**
 * Created by timot on 3/27/2018.
 */

public class WelcomeCard {
    private String name;
    private int ticketCount;

    public WelcomeCard(String name, int ticketCount){
        this.name = name;
        this.ticketCount = ticketCount;
    }


    public String getName() {
        return name;
    }

    public int getTicketCount() {
        return ticketCount;
    }
}
