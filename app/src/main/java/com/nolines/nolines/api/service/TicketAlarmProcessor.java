package com.nolines.nolines.api.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.nolines.nolines.MapsActivity;
import com.nolines.nolines.R;
import com.nolines.nolines.api.models.Guest;
import com.nolines.nolines.api.models.GuestHolder;
import com.nolines.nolines.api.models.Ride;
import com.nolines.nolines.api.models.Ticket;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class TicketAlarmProcessor extends IntentService {

    public static final int timeToAlert = 90; //Time before the event to send an alert for (in seconds)

    private static final String ACTION_CHECK_TICKETS = "com.nolines.nolines.api.service.action.CHECK_TICKETS";
    public static final String NOTIFICATION_TICKET_ID = "com.nolines.nolines.NOTIFICATION_TICKET_ID";

    public TicketAlarmProcessor() {
        super("TicketAlarmProcessor");
    }

    /**
     * Starts this service to perform action CHECK_TICKETS with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionCheckTickets(Context context, int rideID) {
        Intent intent = new Intent(context, TicketAlarmProcessor.class);
        intent.putExtra(NOTIFICATION_TICKET_ID, rideID);
        intent.setAction(ACTION_CHECK_TICKETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHECK_TICKETS.equals(action)) {
                Bundle extras = intent.getExtras();
                if(extras == null){
                    handleActionCheckTickets(0);
                }else{
                    handleActionCheckTickets(extras.getInt(TicketAlarmProcessor.NOTIFICATION_TICKET_ID));
                }
            }
        }
    }

    /**
     * Handle action Check Tickets in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCheckTickets(int rideID) {
        //if shared preferences notification toggle is off, don't send notification
        Guest guest = GuestHolder.getInstance(this).getGuestObject();
        Ticket ticket = null;
        if(rideID > 0){
            for(Ticket t : guest.getTickets()){
                if(t.getRide().getId() == rideID)
                    ticket = t;
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);

            NotificationChannel channel = new NotificationChannel("default",
                    name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, MapsActivity.class);
        resultIntent.putExtra(NOTIFICATION_TICKET_ID, rideID);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        String notificationTitle = getString(R.string.notification_title);
        String notificationText = getString(R.string.notification_generic);
        if(ticket != null){
            notificationTitle = getString(R.string.notification_title, ticket.getRide().getName());
            switch(ticket.getNotificationsLeft()){
                case 4:
                    notificationText = getResources().getQuantityString(R.plurals.pre_window_open, timeToAlert/60, timeToAlert/60);
                    break;
                case 3:
                    notificationText = getString(R.string.window_open);
                    break;
                case 2:
                    notificationText = getResources().getQuantityString(R.plurals.pre_window_close, timeToAlert/60, timeToAlert/60);
                    break;
                case 1:
                    notificationText = getString(R.string.window_close);
                    break;
                default:
                    break;
            }
            ticket.reduceNotificationsLeft();
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(new long[] {500, 400, 200, 500, 1000, 400})
                .setLights(Color.CYAN, 3000, 3000)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        int notificationId =  (int)System.currentTimeMillis();
        notificationManager.notify("com.nolines.nolines", notificationId, mBuilder.build());
    }

    private static void setTicketNotificationsForGuest(Context context){
        GuestHolder guest = GuestHolder.getInstance(context);
        Intent ticketProcessorReceiver = new Intent(context, AlarmReceiver.class);
        for(Ticket ticket : guest.getGuestObject().getTickets()){
            setNotificationsForTicket(ticket, ticketProcessorReceiver, context);
        }
    }

    /**
     * Sets an alarmmanager for ticket to send notifications when the ticket's ride window
     * opens and closes and a period before each as well.
     **/
    public static void setNotificationsForTicket(Ticket ticket, Context context) {
        setNotificationsForTicket(ticket, null, context);
    }

    private static void setNotificationsForTicket(Ticket ticket, Intent intent, Context context){
        Intent ticketProcessorReceiver = intent;
        if(ticketProcessorReceiver == null)
            ticketProcessorReceiver = new Intent(context, AlarmReceiver.class);
        ticketProcessorReceiver.putExtra(NOTIFICATION_TICKET_ID, ticket.getRide().getId());

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

        //Get the start and end times of the ticket in seconds
        long startTime = getTimeInMillis(ticket.getStartTime())/1000;
        long endTime = getTimeInMillis(ticket.getEndTime())/1000;

        int timeUntilStart = (int)(startTime - new Date().getTime()/1000);
        //if there are more than timeToAlert seconds until the ride window opens then add
        //an alert for it at timeToAlert seconds before the window opens and when the window opens
        if(timeUntilStart > 60){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    ticket.hashCode(), ticketProcessorReceiver, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, startTime * 1000, pendingIntent);

            if(timeUntilStart > TicketAlarmProcessor.timeToAlert) {
                pendingIntent = PendingIntent.getBroadcast(context,
                        ticket.hashCode() + 1, ticketProcessorReceiver, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, (startTime - TicketAlarmProcessor.timeToAlert) * 1000, pendingIntent);
            }
        }

        int timeUntilEnd = (int)(endTime - new Date().getTime()/1000);
        //if there are more than timeToAlert seconds until the ride window closes then add
        //an alert for it at timeToAlert seconds before the window closes and when the window closes
        if(timeUntilEnd > 60) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    ticket.hashCode() + 2, ticketProcessorReceiver, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, endTime * 1000, pendingIntent);

            if (timeUntilEnd > TicketAlarmProcessor.timeToAlert) {
                pendingIntent = PendingIntent.getBroadcast(context,
                        ticket.hashCode() + 3, ticketProcessorReceiver, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, (endTime - TicketAlarmProcessor.timeToAlert) * 1000, pendingIntent);
            }
        }
    }

    //TODO: Localise so that this works for any region
    //Gets the time in milliseconds of the string time
    private static long getTimeInMillis(String time){
        DateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        long millisTime;
        try{
            millisTime = df.parse(time).getTime();
        } catch (ParseException e){
            millisTime = -1;
        }
        return millisTime;
    }
}

