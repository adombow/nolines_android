package com.nolines.nolines.api.models;

/**
 * Created by timot on 4/3/2018.
 */

public class TicketRequest {
    private String date;
    private String ride_window_id;
    private String guest_id;

    public TicketRequest(String ride_window_id, String guest_id, String date) {
        this.date = date;
        this.ride_window_id = ride_window_id;
        this.guest_id = guest_id;
    }
}
