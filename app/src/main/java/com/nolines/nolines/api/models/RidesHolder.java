package com.nolines.nolines.api.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.nolines.nolines.R;
import com.nolines.nolines.api.service.NoLinesClient;
import com.nolines.nolines.api.service.Updateable;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nolines.nolines.api.models.Ride.AFTERNOON;
import static com.nolines.nolines.api.models.Ride.EVENING;
import static com.nolines.nolines.api.models.Ride.MORNING;

/**
 * Created by Andrew on 2018-03-20.
 */

/*
 Singleton class for a Global ride list
 */
public class RidesHolder {
    private static final String TAG = "RidesHolder";

    private List<Updateable> listeners = new ArrayList<Updateable>();
    public Calendar calendar;

    private static class Holder{
        private static final RidesHolder INSTANCE = new RidesHolder();
    }

    private List<Ride> rides;
    private static WeakReference<Context> mContext;

    private RidesHolder() {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        getRides();
    }

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

        SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(mContext.get());

        Retrofit.Builder builder;
        try{
            builder = new Retrofit.Builder()
                    .baseUrl(prefs.getString(mContext.get().getString(R.string.pref_key_server_url),
                            mContext.get().getString(R.string.pref_server_url_remote)))
                    .addConverterFactory(GsonConverterFactory.create());
        } catch(IllegalArgumentException e){
            builder = new Retrofit.Builder()
                    .baseUrl(mContext.get().getString(R.string.pref_server_url_remote))
                    .addConverterFactory(GsonConverterFactory.create());
        }

        Retrofit retrofit = builder.build();

        NoLinesClient client = retrofit.create(NoLinesClient.class);

        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        //df1.setTimeZone(TimeZone.getTimeZone("UTC"));
        String requestDate = df1.format(calendar.getTime());

        Call<List<Ride>> call = client.getRides(requestDate);

        call.enqueue(new Callback<List<Ride>>() {
            @Override
            public void onResponse(Call<List<Ride>> call, Response<List<Ride>> response) {
                Log.i(TAG, "Response Received");
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
