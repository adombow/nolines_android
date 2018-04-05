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

import com.nolines.nolines.R;
import com.nolines.nolines.api.models.GuestHolder;
import com.nolines.nolines.api.models.RideWindow;
import com.nolines.nolines.api.models.Ticket;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class TicketAlarmProcessor extends IntentService{

    public static final int timeToAlert = 120;//300; //Time before the event to send an alert for (in seconds)

    private static final String RIDE_NOTIFICATION_CHANNEL_ID = "com.nolines.nolines.RIDE_NOTIFICATIONS";

    private static final String ACTION_SEND_RIDE_NOTIFICATION = "com.nolines.nolines.api.service.action.SEND_RIDE_NOTIFICATION";

    public static final String NOTIFICATION_BUNDLE = "com.nolines.nolines.NOTIFICATION_BUNDLE";
    public static final String NOTIFICATION_RIDE_NAME = "com.nolines.nolines.NOTIFICATION_RIDE_NAME";
    public static final String NOTIFICATION_TICKET_START_TIME = "com.nolines.nolines.NOTIFICATION_TICKET_START_TIME";
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
    public static void startActionSendRideNotification(Context context, String rideName,
                                                       String ticketStartTime, int ticketId,
                                                       NotificationType type) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.getBoolean(context.getString(R.string.pref_key_notifications_enable), true)) {
            Intent intent = new Intent(context, TicketAlarmProcessor.class);

            Bundle extras = new Bundle();
            extras.putString(NOTIFICATION_RIDE_NAME, rideName);
            extras.putString(NOTIFICATION_TICKET_START_TIME, ticketStartTime);
            extras.putInt(NOTIFICATION_TICKET_ID, ticketId);
            extras.putSerializable(NOTIFICATION_TYPE, type);

            intent.putExtra(NOTIFICATION_BUNDLE, extras);
            intent.setAction(ACTION_SEND_RIDE_NOTIFICATION);
            context.startService(intent);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_RIDE_NOTIFICATION.equals(action)) {
                Bundle extras = intent.getBundleExtra(NOTIFICATION_BUNDLE);
                if(extras == null){
                    handleActionSendRideNotification(null, null, 0, NotificationType.CLOSE);
                }else{
                    handleActionSendRideNotification(
                            extras.getString(NOTIFICATION_RIDE_NAME),
                            extras.getString(NOTIFICATION_TICKET_START_TIME),
                            extras.getInt(NOTIFICATION_TICKET_ID),
                            (NotificationType) extras.getSerializable(NOTIFICATION_TYPE));
                }
            }
        }
    }

    /**
     * Handle action Check Tickets in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSendRideNotification(String rideName, String ticketStartTime,
                                                  int ticketId, NotificationType type) {
        //if shared preferences notification toggle is off, don't send notification

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
        //resultIntent.putExtra(NOTIFICATION_RIDE_NAME, rideID);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        //TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        //stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        //PendingIntent resultPendingIntent =
                //stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        String notificationTitle = getString(R.string.notification_title, "Test");
        String notificationText = getString(R.string.notification_generic);
        NotificationType nextType = null;
        if(rideName != null && type != null){
            notificationTitle = getString(R.string.notification_title, rideName);
            switch(type){
                case PRE_OPEN:
                    //notificationText = getResources().getQuantityString(R.plurals.pre_window_open, timeToAlert/60, timeToAlert/60);
                    notificationText = "Your ride window opens in " + timeToAlert/60 + "minutes";
                    nextType = NotificationType.OPEN;
                    break;
                case OPEN:
                    notificationText = getString(R.string.window_open);
                    nextType = NotificationType.PRE_CLOSE;
                    break;
                case PRE_CLOSE:
                    //notificationText = getResources().getQuantityString(R.plurals.pre_window_close, timeToAlert/60, timeToAlert/60);
                    notificationText = "Your ride window closes in " + timeToAlert/60 + "minutes";
                    nextType = NotificationType.CLOSE;
                    break;
                case CLOSE:
                    notificationText = getString(R.string.window_close);
                    nextType = null;
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

        if(nextType != null){
            setTicketNotification(rideName, ticketStartTime, ticketId, this, nextType);
        }
    }

    public static void setTicketNotification(String rideName, String ticketStartTime,
                                             int ticketId, Context context, NotificationType type){
        Intent ticketProcessorReceiver = new Intent(context.getApplicationContext(), AlarmReceiver.class);

        Bundle extras = new Bundle();
        extras.putString(NOTIFICATION_RIDE_NAME, rideName);
        extras.putString(NOTIFICATION_TICKET_START_TIME, ticketStartTime);
        extras.putInt(NOTIFICATION_TICKET_ID, ticketId);
        extras.putSerializable(NOTIFICATION_TYPE, type);
        ticketProcessorReceiver.putExtra(NOTIFICATION_BUNDLE, extras);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

        DateFormat df = new SimpleDateFormat(Ticket.dateFormat);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar cal = Calendar.getInstance();
        try{
            Date date = df.parse(ticketStartTime);
            df.setTimeZone(TimeZone.getDefault());
            cal.setTime(df.parse(df.format(date)));
        } catch (ParseException e) {
        }

        long startTimeMillis = cal.getTimeInMillis();
        long triggerInSec = 0;
        int idOffset = 0;
        switch(type){
            case PRE_OPEN:
                idOffset = 0;
                triggerInSec = startTimeMillis - (long) (TicketAlarmProcessor.timeToAlert*1000);
                break;
            case OPEN:
                idOffset = 1;
                triggerInSec = startTimeMillis;
                break;
            case PRE_CLOSE:
                idOffset = 2;
                triggerInSec = startTimeMillis + (long) ((RideWindow.windowLength - TicketAlarmProcessor.timeToAlert)*1000);
                break;
            case CLOSE:
                idOffset = 3;
                triggerInSec = startTimeMillis + (long) (RideWindow.windowLength*1000);
                break;
            default:
                break;
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                getUniqueID(ticketId, idOffset), ticketProcessorReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerInSec, pendingIntent);
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

