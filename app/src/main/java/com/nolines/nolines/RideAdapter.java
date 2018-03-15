package com.nolines.nolines;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nolines.nolines.RideFragment.OnListFragmentInteractionListener;
import com.nolines.nolines.Web.Ride;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Ride} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private final List<Ride> mRides;
    private final RideFragment.OnListFragmentInteractionListener mListener;

    public RideAdapter(List<Ride> rides, OnListFragmentInteractionListener listener) {
        mRides = rides;
        mListener = listener;
    }

    @Override
    public RideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RideViewHolder holder, final int position) {
        holder.rideImage.setImageResource(mRides.get(position).getPhotoID());
        holder.rideName.setText(mRides.get(position).getName());
        String waitTimeString = "Wait time: " + Integer.toString(mRides.get(position).getWait_time());
        holder.rideWaitTime.setText(waitTimeString);

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ride currentRide = mRides.get(position);
                Context context = view.getContext();
                Intent intent = new Intent(context, RideWindowActivity.class);
                intent.putExtra("RideName", currentRide.getName());
                context.startActivity(intent);
                mListener.onListFragmentInteraction(mRides.get(position));
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mRides.size();
    }

    public class RideViewHolder extends RecyclerView.ViewHolder {
        public final CardView cv;
        public final TextView rideName;
        public final TextView rideWaitTime;
        public final ImageView rideImage;

        public RideViewHolder(View view) {
            super(view);
            cv = view.findViewById(R.id.cv);
            rideName = view.findViewById(R.id.ride_name);
            rideWaitTime = view.findViewById(R.id.ride_wait);
            rideImage = view.findViewById(R.id.ride_photo);
        }
    }
}
