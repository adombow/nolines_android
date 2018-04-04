package com.nolines.nolines.api.service;

import com.nolines.nolines.api.models.Ticket;

/**
 * Created by Andrew on 2018-03-21.
 */

public interface Updateable {
    void onRidesUpdate();
    void onGuestUpdate();
    void onTicketCreated(Ticket ticket, int status_code);
}

