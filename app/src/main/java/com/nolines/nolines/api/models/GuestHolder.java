package com.nolines.nolines.api.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.nolines.nolines.api.service.NoLinesClient;
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

    private GuestHolder() { getGuest(); }
    public static GuestHolder getInstance(Context context) {
        mContext = new WeakReference<Context>(context);
        return Holder.INSTANCE;
    }

    public Guest getGuestObject() { return this.guest; }
    public void refreshGuest() { getGuest(); }

    private void getGuest(){

        /* Modify Later to put baseUrl globally*/
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://nolines-production.herokuapp.com/")
                //.baseUrl("http://128.189.92.71:3001/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        NoLinesClient client = retrofit.create(NoLinesClient.class);
        //TODO: Get guest id from sharedpreferences?
        Call<Guest> call = client.getGuest(1);

        call.enqueue(new Callback<Guest>() {
            @Override
            public void onResponse(Call<Guest> call, Response<Guest> response) {
                Log.i(TAG, "Repsonse Recieved");
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

    public void registerListener(Updateable listener){
        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    public void unregisterListener(Updateable listener){
        if(listener != null && listeners.contains(listener))
            listeners.remove(listeners.indexOf(listener));
    }
}
