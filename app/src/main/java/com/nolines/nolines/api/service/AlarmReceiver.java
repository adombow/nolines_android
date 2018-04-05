package com.nolines.nolines.api.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nolines.nolines.api.models.Ticket;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Bundle extras = intent.getBundleExtra(TicketAlarmProcessor.NOTIFICATION_BUNDLE);

        if(extras == null){
            TicketAlarmProcessor.startActionSendRideNotification(context,
                    null, null, 0, TicketAlarmProcessor.NotificationType.CLOSE);
        }else{
            TicketAlarmProcessor.NotificationType nType = (TicketAlarmProcessor.NotificationType)
                    extras.getSerializable(TicketAlarmProcessor.NOTIFICATION_TYPE);
            String rideName = extras.getString(TicketAlarmProcessor.NOTIFICATION_RIDE_NAME);
            int ticketId = extras.getInt(TicketAlarmProcessor.NOTIFICATION_TICKET_ID);
            String startTime = extras.getString(TicketAlarmProcessor.NOTIFICATION_TICKET_START_TIME);
            TicketAlarmProcessor.startActionSendRideNotification(context, rideName, startTime, ticketId, nType);
        }
    }
}
