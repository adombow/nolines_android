package com.nolines.nolines.adapters;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
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
import com.nolines.nolines.api.models.RideWindow;
import com.squareup.picasso.Picasso;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nolines.nolines.api.models.Ride.AFTERNOON;
import static com.nolines.nolines.api.models.Ride.EVENING;
import static com.nolines.nolines.api.models.Ride.MORNING;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Ride} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private final List<Ride> mRides;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    private final RecyclerView.RecycledViewPool viewPool;

    @Ride.TimeFrame
    private int timeFrame = MORNING;

    public RideAdapter(List<Ride> rides, OnListFragmentInteractionListener listener, Context context) {
        mRides = rides;
        mListener = listener;
        mContext = context;

        viewPool = new RecyclerView.RecycledViewPool();
    }

    public void setTimeFrame(@Ride.TimeFrame int timeFrame){
        this.timeFrame = timeFrame;
    }

    @Override
    public RideViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ride, parent, false);


        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RideViewHolder holder, final int position) {
        Picasso.get().load(mRides.get(position).getPhotoURL()).into(holder.rideImage);
        holder.rideName.setText(mRides.get(position).getName());
        holder.subtitle.setText(mRides.get(position).getRideType());
        // holder.rideWaitTime.setText(Integer.toString(mRides.get(position).getWaitTime()));

        holder.rideWindowAdapter = new RideWindowAdapter(mRides.get(position).getRideWindows(timeFrame), mListener);
        holder.windowRecyclerView.setRecycledViewPool(viewPool);
        holder.windowRecyclerView.setAdapter(holder.rideWindowAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        holder.windowRecyclerView.setLayoutManager(mLayoutManager);
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

        @BindView(R.id.card) CardView cardView;
        @BindView(R.id.title) TextView rideName;
        @BindView(R.id.subtitle) TextView subtitle;
        @BindView(R.id.details) TextView details;
        @BindView(R.id.card_image) ImageView rideImage;
        @BindView(R.id.window_recycler_view) RecyclerView windowRecyclerView;

        RideWindowAdapter rideWindowAdapter;

        public RideViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }




    }
}
