package com.nolines.nolines.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.nolines.nolines.R;
import com.nolines.nolines.RideFragment;
import com.nolines.nolines.api.models.RideWindow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * {@link RecyclerView.Adapter} that can display a {@link RideWindow} and makes a call to the
 * specified {@link RideFragment.OnListFragmentInteractionListener}.
 */
public class RideWindowAdapter extends RecyclerView.Adapter<RideWindowAdapter.RideWindowViewHolder> {

    private final List<RideWindow> mRideWindows;
    private RecyclerView.RecyclerListener mListener;


    public RideWindowAdapter(List<RideWindow> rideWindows, RecyclerViewClickListener listener) {
        mRideWindows = rideWindows;
        mListener = listener;
    }


    @Override
    public RideWindowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ride_window, parent, false);
        return new RideWindowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RideWindowViewHolder holder, final int position) {
        RideWindow rideWindow = mRideWindows.get(position);


        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        Date date;

        try {
           date = df1.parse(rideWindow.getStartTime());
           holder.button1.setText(formatter.format(date));
        }
        catch(Exception e){

        }

        holder.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 RideWindow rideWindow1 = mRideWindows.get(position);



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

        @BindView(R.id.button1) Button button1;


        public RideWindowViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}
