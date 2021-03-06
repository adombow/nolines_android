package com.nolines.nolines.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.nolines.nolines.R;
import com.nolines.nolines.RideFragment;
import com.nolines.nolines.api.models.RideWindow;
import com.nolines.nolines.api.models.Ticket;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * {@link RecyclerView.Adapter} that can display a {@link RideWindow} and makes a call to the
 * specified {@link RideFragment.OnListFragmentInteractionListener}.
 */
public class RideWindowAdapter extends RecyclerView.Adapter<RideWindowAdapter.RideWindowViewHolder> {

    private final List<RideWindow> mRideWindows;
    RecyclerViewClickListener mListener;


    public RideWindowAdapter(List<RideWindow> rideWindows, RecyclerViewClickListener listener) {
        mRideWindows = rideWindows;
        mListener = listener;
    }


    @Override
    public RideWindowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ride_window, parent, false);
        return new RideWindowViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(final RideWindowViewHolder holder, final int position) {
        RideWindow rideWindow = mRideWindows.get(position);

        Calendar cal = rideWindow.getLocalDatetimeFromTime();
        cal.add(Calendar.HOUR, 1);
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getDefault());

        String startTime = formatter.format(cal.getTime());

        holder.button1.setText(startTime);
        holder.button1.setClickable(false);

    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mRideWindows.size();
    }

    public class RideWindowViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        private RecyclerViewClickListener mListener;

        @BindView(R.id.button1) Button button1;


        public RideWindowViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            ButterKnife.bind(this, view);
            mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
}
