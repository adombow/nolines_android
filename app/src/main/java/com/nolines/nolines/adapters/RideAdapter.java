package com.nolines.nolines.adapters;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nolines.nolines.R;
import com.nolines.nolines.RideFragment.OnListFragmentInteractionListener;
import com.nolines.nolines.api.models.Ride;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Ride} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private final List<Ride> mRides;
    private final OnListFragmentInteractionListener mListener;

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
        //Picasso.get().load(mRides.get(position).getPhotoURL()).into(holder.rideImage);
        holder.rideName.setText(mRides.get(position).getName());
        holder.rideWaitTime.setText(Integer.toString(mRides.get(position).getWaitTime()));

        holder.button1.setText("1");
        holder.button2.setText("2");
        holder.button3.setText("3");
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
        @BindView(R.id.ride_cardview) CardView cardView;
        @BindView(R.id.ride_name) TextView rideName;
        @BindView(R.id.ride_wait) TextView rideWaitTime;
        @BindView(R.id.ride_photo) ImageView rideImage;

        @BindView(R.id.button1) Button button1;
        @BindView(R.id.button2) Button button2;
        @BindView(R.id.button3) Button button3;

        public RideViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }


    }
}
