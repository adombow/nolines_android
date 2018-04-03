package com.nolines.nolines.adapters;

import android.support.annotation.IntDef;
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

    @Ride.TimeFrame
    private int timeFrame = MORNING;

    public RideAdapter(List<Ride> rides, OnListFragmentInteractionListener listener) {
        mRides = rides;
        mListener = listener;
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
        //Picasso.get().load("https://seaworld.scdn3.secure.raxcdn.com/tampa/-/media/busch-gardens-tampa/listing-images/357x229/rides-and-kid-friendly-attractions/2017_buschgardenstampabay_rollercoaster_cheetah_hunt2_357x229.ashx?version=1_201705102808").into(holder.rideImage);
        holder.rideName.setText(mRides.get(position).getName());
        holder.subtitle.setText(mRides.get(position).getRideType());
        // holder.rideWaitTime.setText(Integer.toString(mRides.get(position).getWaitTime()));

        switch (timeFrame){

            case MORNING:
                holder.button1.setText("AM");
                holder.button2.setText("AM");
                holder.button3.setText("AM");
                break;
            case AFTERNOON:
                holder.button1.setText("PM");
                holder.button2.setText("PM");
                holder.button3.setText("PM");
                break;
            case EVENING:
                holder.button1.setText("EVENING");
                holder.button2.setText("EVENING");
                holder.button3.setText("EVENING");
                break;
        }

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

        @BindView(R.id.button1) Button button1;
        @BindView(R.id.button2) Button button2;
        @BindView(R.id.button3) Button button3;

        public RideViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
