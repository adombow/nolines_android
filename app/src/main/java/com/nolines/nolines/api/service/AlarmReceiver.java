package com.nolines.nolines.api.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        Bundle extras = intent.getExtras();
        if(extras == null){
            TicketAlarmProcessor.startActionCheckTickets(context,
                    -1, TicketAlarmProcessor.NotificationType.CLOSE);
        }else{
            TicketAlarmProcessor.startActionCheckTickets(context,
                    extras.getInt(TicketAlarmProcessor.NOTIFICATION_TICKET_ID),
                    (TicketAlarmProcessor.NotificationType) extras.getSerializable(TicketAlarmProcessor.NOTIFICATION_TYPE));
        }
    }
}
