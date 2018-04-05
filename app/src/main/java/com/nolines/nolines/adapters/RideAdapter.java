package com.nolines.nolines.adapters;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import android.support.annotation.IntDef;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nolines.nolines.R;
import com.nolines.nolines.RideFragment;
import com.nolines.nolines.RideFragment.OnListFragmentInteractionListener;
import com.nolines.nolines.RideWindowDialog;
import com.nolines.nolines.api.models.Ride;
import com.nolines.nolines.api.models.RideWindow;
import com.nolines.nolines.api.models.Ticket;
import com.squareup.picasso.Picasso;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nolines.nolines.api.models.Ride.MORNING;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Ride} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private List<Ride> mRides;

    private final Context mContext;
    private final RideFragment mFragment;
    private final RecyclerView.RecycledViewPool viewPool;

    @Ride.TimeFrame
    private int timeFrame = MORNING;

    public RideAdapter(List<Ride> rides, Context context, RideFragment fragment) {
        mRides = rides;
        mContext = context;
        mFragment = fragment;
        viewPool = new RecyclerView.RecycledViewPool();
    }

    public void updateRideList(List<Ride> rides){
        mRides.clear();
        mRides.addAll(rides);
        this.notifyDataSetChanged();
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
        Ride ride = mRides.get(position);

        Picasso.get().load(ride.getPhotoURL()).into(holder.rideImage);
        holder.rideName.setText(ride.getName());
        holder.subtitle.setText(ride.getRideType());


        // Set Ride details as window_date from request
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df1.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        String formattedDate = "";
        try {
            date = df1.parse(ride.getWindowDate());
            formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date.getTime());
        }
        catch(Exception e){}



        holder.details.setText(formattedDate);

        RecyclerViewClickListener recyclerViewClickListener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int window_position) {
                //Toast.makeText(mContext, "Position: " + window_position + " " + position, Toast.LENGTH_SHORT).show();
                Ride ride = mRides.get(position);

                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                df1.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date;
                String formattedDate = "";
                try {
                    date = df1.parse(ride.getWindowDate());
                    formattedDate = DateFormat.getDateInstance(DateFormat.FULL, Locale.CANADA).format(date.getTime());
                }
                catch(Exception e){}

                RideWindowDialog dialog = RideWindowDialog.newInstance(mFragment,ride, ride.getRideWindows(timeFrame).get(window_position), formattedDate);
                dialog.show(((Activity) mContext).getFragmentManager() , "NoticeDialogFragment");
            }
        };

        if(ride.getRideWindows(timeFrame) != null && !ride.getRideWindows(timeFrame).isEmpty()){
            holder.emptyRecyclerTextView.setVisibility(View.GONE);
            holder.windowRecyclerView.setVisibility(View.VISIBLE);
            holder.rideWindowAdapter = new RideWindowAdapter(ride.getRideWindows(timeFrame), recyclerViewClickListener);
            holder.windowRecyclerView.setRecycledViewPool(viewPool);
            holder.windowRecyclerView.setAdapter(holder.rideWindowAdapter);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

            holder.windowRecyclerView.setLayoutManager(mLayoutManager);
        }
        else {
            holder.windowRecyclerView.setVisibility(View.GONE);
            holder.emptyRecyclerTextView.setVisibility(View.VISIBLE);
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
        @BindView(R.id.window_recycler_view) RecyclerView windowRecyclerView;
        @BindView(R.id.emptyRecyclerTextView) TextView emptyRecyclerTextView;

        RideWindowAdapter rideWindowAdapter;

        public RideViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
