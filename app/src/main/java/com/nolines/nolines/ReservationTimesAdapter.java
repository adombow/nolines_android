package com.nolines.nolines;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nolines.nolines.RideWindowFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link String} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ReservationTimesAdapter extends RecyclerView.Adapter<ReservationTimesAdapter.RideWindowViewHolder> {

    private final List<String> mRideWindows;
    private final RideWindowFragment.OnListFragmentInteractionListener mListener;

    public ReservationTimesAdapter(List<String> items, OnListFragmentInteractionListener listener) {
        mRideWindows = items;
        mListener = listener;
    }

    @Override
    public RideWindowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ridewindow, parent, false);
        return new RideWindowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RideWindowViewHolder holder, final int position) {
        holder.rideWindow.setText(mRideWindows.get(position));

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedWindow = mRideWindows.get(position);
                //Send the current selection to the server
                Snackbar.make(v, selectedWindow, Snackbar.LENGTH_SHORT).show();
                //end the current fragment
                //((Activity)v.getContext()).finish();
                mListener.onListFragmentInteraction(mRideWindows.get(position));
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mRideWindows.size();
    }

    public class RideWindowViewHolder extends RecyclerView.ViewHolder {
        public final CardView cv;
        public final TextView rideWindow;

        public RideWindowViewHolder(View view) {
            super(view);
            cv = view.findViewById(R.id.cv);
            rideWindow = view.findViewById(R.id.ride_window);
        }
    }
}
