package com.nolines.nolines.api.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.nolines.nolines.api.service.NoLinesClient;
import com.nolines.nolines.api.service.Updateable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
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
 Singleton class for a Global ride list
 */
public class RidesHolder {
    private static final String TAG = "RidesHolder";

    private List<Updateable> listeners = new ArrayList<Updateable>();

    private static class Holder{
        private static final RidesHolder INSTANCE = new RidesHolder();
    }

    private List<Ride> rides;
    private static WeakReference<Context> mContext;

    private RidesHolder() { getRides(); }
    public static RidesHolder getInstance(Context context) {
        mContext = new WeakReference<Context>(context);
        return Holder.INSTANCE;
    }

    public Ride getItem(int pos) { return this.rides.get(pos); }
    public void refreshRides() { getRides(); }
    public boolean isEmpty() { return rides == null || rides.isEmpty(); }
    public int numRides() { return rides.size(); }
    public List<Ride> getRideList(){ return rides;}

    private void getRides(){

        /* Modify Later to put baseUrl globally*/
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://nolines-production.herokuapp.com/")
                //.baseUrl("http://128.189.92.71:3001/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        NoLinesClient client = retrofit.create(NoLinesClient.class);
        Call<List<Ride>> call = client.getRides();

        call.enqueue(new Callback<List<Ride>>() {
            @Override
            public void onResponse(Call<List<Ride>> call, Response<List<Ride>> response) {
                Log.i(TAG, "Repsonse Recieved");
                rides = response.body();
                for(Updateable listener : listeners){
                    try{
                        listener.onRidesUpdate();
                    } catch(Throwable t){
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Ride>> call, Throwable t) {
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
