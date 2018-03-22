package com.nolines.nolines.api.models;

import android.content.Context;

import com.nolines.nolines.api.service.Updateable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew on 2018-03-20.
 */

/*
 Singleton class for a Global ticket list
 */
public class TicketsHolder {

    private List<Updateable> listeners = new ArrayList<Updateable>();

    private static class Holder{
        private static final TicketsHolder INSTANCE = new TicketsHolder();
    }

    private List<Ticket> tickets;
    private static WeakReference<Context> mContext;

    private TicketsHolder() { getTickets(); }
    public static TicketsHolder getInstance(Context context) {
        mContext = new WeakReference<Context>(context);
        return Holder.INSTANCE;
    }

    public Ticket getItem(int pos) { return this.tickets.get(pos); }
    public void refreshTickets() { getTickets(); }
    public boolean isEmpty() { return tickets == null || tickets.isEmpty(); }
    public int numTickets() { return tickets.size(); }

    private void getTickets(){

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
