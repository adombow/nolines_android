package com.nolines.nolines.api.models;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.nolines.nolines.R;
import com.nolines.nolines.api.service.AlarmReceiver;
import com.nolines.nolines.api.service.NoLinesClient;
import com.nolines.nolines.api.service.TicketAlarmProcessor;
import com.nolines.nolines.api.service.Updateable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Andrew on 2018-03-20.
 */

/*
 Singleton class for a Global ticket list
 */
public class GuestHolder {
    private static final String TAG = "GuestHolder";

    private List<Updateable> listeners = new ArrayList<Updateable>();

    private static class Holder{
        private static final GuestHolder INSTANCE = new GuestHolder();
    }

    private Guest guest;
    private static WeakReference<Context> mContext;

    private NoLinesClient client;

    private GuestHolder() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.get());

        Retrofit.Builder builder;
        try{
            builder = new Retrofit.Builder()
                    .baseUrl(prefs.getString(mContext.get().getString(R.string.pref_key_server_url),
                            mContext.get().getString(R.string.pref_server_url_remote)))
                    .addConverterFactory(GsonConverterFactory.create());
        } catch(IllegalArgumentException e) {
            builder = new Retrofit.Builder()
                    .baseUrl(mContext.get().getString(R.string.pref_server_url_remote))
                    .addConverterFactory(GsonConverterFactory.create());
        }

        Retrofit retrofit = builder.build();

        client = retrofit.create(NoLinesClient.class);
    }

    public static GuestHolder getInstance(Context context) {
        mContext = new WeakReference<Context>(context);
        return Holder.INSTANCE;
    }

    public Guest getGuestObject() { return this.guest; }
    public void refreshGuest() { getGuest(); }

    private void getGuest(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.get());

        Call<Guest> call = client.getGuest(Integer.parseInt(prefs.getString(mContext.get().getString(R.string.pref_key_user_id), "0")));

        call.enqueue(new Callback<Guest>() {
            @Override
            public void onResponse(Call<Guest> call, Response<Guest> response) {
                Log.i(TAG, "Response Received");
                guest = response.body();
                for(Updateable listener : listeners){
                    try{
                        listener.onGuestUpdate();
                    } catch(Throwable t){
                    }
                }
            }

            @Override
            public void onFailure(Call<Guest> call, Throwable t) {
                Toast.makeText(mContext.get(),"Network Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cancelTicket(final int ticket_id){

        Call<Void> call = client.cancelTicket(ticket_id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "Response Received");
                if(response.raw().code() == 200){
                    getGuest();
                }
                AlarmManager alarmManager = (AlarmManager) mContext.get()
                        .getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(mContext.get().getApplicationContext(),
                        AlarmReceiver.class);
                for(int i = 0; i < 4; i++){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            mContext.get().getApplicationContext(),
                            TicketAlarmProcessor.getUniqueID(ticket_id, i),
                            intent, 0);
                    alarmManager.cancel(pendingIntent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(mContext.get(),"Network Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registerListener(Updateable listener){
        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    public void unregisterListener(Updateable listener){
        if(listener != null && listeners.contains(listener))
            listeners.remove(listeners.indexOf(listener));
    }
}
