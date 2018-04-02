package com.nolines.nolines.api.models;

import android.content.Context;

import com.nolines.nolines.api.service.Updateable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        this.guest = new Guest("Andrew");
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
