package com.nolines.nolines.api.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.nolines.nolines.MapsFragment;
import com.nolines.nolines.R;
import com.nolines.nolines.api.models.Guest;
import com.nolines.nolines.api.models.GuestHolder;
import com.nolines.nolines.api.models.Ticket;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class TicketAlarmProcessor extends IntentService {

    public static final int timeToAlert = 300; //Time before the event to send an alert for (in seconds)

    private static final String RIDE_NOTIFICATION_CHANNEL_ID = "com.nolines.nolines.RIDE_NOTIFICATIONS";

    private static final String ACTION_CHECK_TICKETS = "com.nolines.nolines.api.service.action.CHECK_TICKETS";
    public static final String NOTIFICATION_TICKET_ID = "com.nolines.nolines.NOTIFICATION_TICKET_ID";
    public static final String NOTIFICATION_TYPE = "com.nolines.nolines.NOTIFICATION_TYPE";

    public enum NotificationType{
        PRE_OPEN, OPEN, PRE_CLOSE, CLOSE
    }

    public TicketAlarmProcessor() {
        super("TicketAlarmProcessor");
    }

    /**
     * Starts this service to perform action CHECK_TICKETS with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionCheckTickets(Context context, int rideID, NotificationType type) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.getBoolean(context.getString(R.string.pref_key_notifications_enable), true)) {
            Intent intent = new Intent(context, TicketAlarmProcessor.class);
            intent.putExtra(NOTIFICATION_TICKET_ID, rideID);
            intent.putExtra(NOTIFICATION_TYPE, type);
            intent.setAction(ACTION_CHECK_TICKETS);
            context.startService(intent);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHECK_TICKETS.equals(action)) {
                Bundle extras = intent.getExtras();
                if(extras == null){
                    handleActionCheckTickets(-1, NotificationType.CLOSE);
                }else{
                    handleActionCheckTickets(extras.getInt(NOTIFICATION_TICKET_ID),
                            (NotificationType) extras.getSerializable(NOTIFICATION_TYPE));
                }
            }
        }
    }

    /**
     * Handle action Check Tickets in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCheckTickets(int rideID, NotificationType type) {
        //if shared preferences notification toggle is off, don't send notification
        Guest guest = GuestHolder.getInstance(this).getGuestObject();
        Ticket ticket = null;
        if(rideID > 0){
            for(Ticket t : guest.getTickets()){
                if(t.getRide().getId() == rideID)
                    ticket = t;
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long vibratePattern[];
        if(prefs.getBoolean(this.getString(R.string.pref_key_notifications_vibrate), true)){
            vibratePattern = new long[]{500L, 400L, 200L, 400L, 200L, 400L, 200L};
        }else{
            vibratePattern = new long[]{0L};
        }
        Uri notificationSound = Uri.parse(prefs.getString(getString(R.string.pref_key_notifications_ringtone),
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(RIDE_NOTIFICATION_CHANNEL_ID, name, importance);

            //Set the channel options
            mChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.CYAN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(vibratePattern);
            mChannel.setShowBadge(true);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mChannel.setSound(notificationSound, audioAttributes);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            try{
                notificationManager.createNotificationChannel(mChannel);
            } catch(NullPointerException e){
            }
        }

        // Create an Intent for the activity you want to start
        //Intent resultIntent = new Intent(this, MapsFragment.class);
        //resultIntent.putExtra(NOTIFICATION_TICKET_ID, rideID);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        //PendingIntent resultPendingIntent =
                //stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        String notificationTitle = getString(R.string.notification_title, "Test");
        String notificationText = getString(R.string.notification_generic);
        if(ticket != null){
            notificationTitle = getString(R.string.notification_title, ticket.getRide().getName());
            switch(type){
                case PRE_OPEN:
                    notificationText = getResources().getQuantityString(R.plurals.pre_window_open, timeToAlert/60, timeToAlert/60);
                    break;
                case OPEN:
                    notificationText = getString(R.string.window_open);
                    break;
                case PRE_CLOSE:
                    notificationText = getResources().getQuantityString(R.plurals.pre_window_close, timeToAlert/60, timeToAlert/60);
                    break;
                case CLOSE:
                    notificationText = getString(R.string.window_close);
                    break;
                default:
                    break;
            }
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, RIDE_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                //.setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setVibrate(vibratePattern)
                .setLights(Color.CYAN, 3000, 3000)
                .setSound(notificationSound);

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
            ticketProcessorReceiver = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        ticketProcessorReceiver.putExtra(NOTIFICATION_TICKET_ID, ticket.getRide().getId());

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

        //Get the start and end times of the ticket in seconds
        long startTime = ticket.getLocalDatetimeFromTime().getTimeInMillis()/1000;
        long endTime = startTime + 360; //Ride windows are 1 hour

        int timeUntilStart = (int)(startTime - new Date().getTime()/1000);
        //if there are more than timeToAlert seconds until the ride window opens then add
        //an alert for it at timeToAlert seconds before the window opens and when the window opens
        if(timeUntilStart > 60){
            ticketProcessorReceiver.putExtra(NOTIFICATION_TYPE, NotificationType.OPEN);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                    getUniqueID(ticket.getId(), 0), ticketProcessorReceiver, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, startTime * 1000, pendingIntent);

            if(timeUntilStart > TicketAlarmProcessor.timeToAlert) {
                ticketProcessorReceiver.putExtra(NOTIFICATION_TYPE, NotificationType.PRE_OPEN);
                pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                        getUniqueID(ticket.getId(), 1), ticketProcessorReceiver, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, (startTime - TicketAlarmProcessor.timeToAlert) * 1000, pendingIntent);
            }
        }

        int timeUntilEnd = (int)(endTime - new Date().getTime()/1000);
        //if there are more than timeToAlert seconds until the ride window closes then add
        //an alert for it at timeToAlert seconds before the window closes and when the window closes
        if(timeUntilEnd > 60) {
            ticketProcessorReceiver.putExtra(NOTIFICATION_TYPE, NotificationType.CLOSE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                    getUniqueID(ticket.getId(), 2), ticketProcessorReceiver, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, endTime * 1000, pendingIntent);

            if (timeUntilEnd > TicketAlarmProcessor.timeToAlert) {
                ticketProcessorReceiver.putExtra(NOTIFICATION_TYPE, NotificationType.PRE_CLOSE);
                pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                        getUniqueID(ticket.getId(), 3), ticketProcessorReceiver, 0);
                alarmManager.set(AlarmManager.RTC_WAKEUP, (endTime - TicketAlarmProcessor.timeToAlert) * 1000, pendingIntent);
            }
        }
    }

    /**
     * Gets a unique number result from a pair of numbers.  Result is only the same if
     * the number pair of a and b is exactly the same, including ordering.
     * @param a the first number
     * @param b the second number
     * @return A unique number only obtainable if the
     * same 2 numbers are input again in the same order
     */
    public static int getUniqueID(int a, int b){
        //Cantor pairing function to get unique number from a number pair
        //returns different results for each ordering (i.e. a,b or b,a)
        return ((a+b)*(a+b+1))/2+b;
    }
}

