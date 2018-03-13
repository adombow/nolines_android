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
public class MyRideRecyclerViewAdapter extends RecyclerView.Adapter<MyRideRecyclerViewAdapter.RideViewHolder> {

    private final List<Ride> mRides;

    public MyRideRecyclerViewAdapter(List<Ride> rides) {
        mRides = rides;
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
        holder.rideWaitTime.setText(Integer.toString(mRides.get(position).getWait_time()));

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ride currentRide = mRides.get(position);
                Context context = view.getContext();
                Intent intent = new Intent(context, PickTicketTimeActivity.class);
                intent.putExtra("RideName", currentRide.getName());
                context.startActivity(intent);
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
        CardView cv;
        TextView rideName;
        TextView rideWaitTime;
        ImageView rideImage;

        public RideViewHolder(View view) {
            super(view);
            cv = view.findViewById(R.id.cv);
            rideName = view.findViewById(R.id.ride_name);
            rideWaitTime = view.findViewById(R.id.ride_wait);
            rideImage = view.findViewById(R.id.ride_photo);
        }
    }
}
