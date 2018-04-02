package com.nolines.nolines.api.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import com.nolines.nolines.MapsActivity;
import com.nolines.nolines.R;
import com.nolines.nolines.api.models.Guest;
import com.nolines.nolines.api.models.GuestHolder;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class TicketAlarmProcessor extends IntentService {

    private static final String ACTION_CHECK_TICKETS = "com.nolines.nolines.api.service.action.CHECK_TICKETS";

    public TicketAlarmProcessor() {
        super("TicketAlarmProcessor");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionCheckTickets(Context context) {
        Intent intent = new Intent(context, TicketAlarmProcessor.class);
        intent.setAction(ACTION_CHECK_TICKETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHECK_TICKETS.equals(action)) {
                handleActionCheckTickets();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCheckTickets() {
        Guest guest = GuestHolder.getInstance(this).getGuestObject();

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
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "default")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Ride notification")
                .setContentText("Your ride time is almost here!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your ride time for the rollercoaster is beginning in 10 minutes!"))
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
}
