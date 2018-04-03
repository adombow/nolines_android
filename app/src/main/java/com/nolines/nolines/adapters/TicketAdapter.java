package com.nolines.nolines.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nolines.nolines.R;
import com.nolines.nolines.TicketFragment.OnListFragmentInteractionListener;
import com.nolines.nolines.api.models.Ticket;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Ticket} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final List<Ticket> mTickets;
    private final OnListFragmentInteractionListener mListener;
    private Context mContext;

    public TicketAdapter(List<Ticket> items, OnListFragmentInteractionListener listener, Context context) {
        mTickets = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public TicketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TicketViewHolder holder, final int position) {
        holder.title.setText(mTickets.get(position).getRide().getName());
        holder.details.setText(mTickets.get(position).getRide().getRideType());

        Calendar cal = mTickets.get(position).getLocalDatetimeFromTime();
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getDefault());

        String startTime = formatter.format(cal.getTime());
        cal.add(Calendar.HOUR, 1);
        String endTime = formatter.format(cal.getTime());

        String windowText = mContext.getString(R.string.ride_window_text, startTime, endTime);
        holder.rideWindow.setText(windowText);
        Picasso.get().load(mTickets.get(position).getRide().getPhotoURL()).into(holder.rideImage);

        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(mTickets.get(position));
                }
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return mTickets.size();
    }

    public class TicketViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card) CardView cv;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.details) TextView details;
        @BindView(R.id.rideWindow) TextView rideWindow;

        @BindView(R.id.card_image) ImageView rideImage;

        public TicketViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
