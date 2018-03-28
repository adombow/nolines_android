package com.nolines.nolines.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nolines.nolines.R;
import com.nolines.nolines.TicketFragment.OnListFragmentInteractionListener;
import com.nolines.nolines.api.models.Ticket;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Ticket} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private final List<Ticket> mTickets;
    private final OnListFragmentInteractionListener mListener;

    public TicketAdapter(List<Ticket> items, OnListFragmentInteractionListener listener) {
        mTickets = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ticket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.rideName.setText(mTickets.get(position).getRideName());
        holder.startTime.setText(mTickets.get(position).getStartTime());
        holder.end_time.setText(mTickets.get(position).getEndTime());

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
    public int getItemCount() {
        return mTickets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final CardView cv;
        public final TextView rideName;
        public final TextView startTime;
        public final TextView end_time;

        public ViewHolder(View view) {
            super(view);
            rideName = (TextView) view.findViewById(R.id.ride);
            startTime = (TextView) view.findViewById(R.id.start_time);
            end_time = (TextView) view.findViewById(R.id.end_time);
            cv = (CardView) view.findViewById(R.id.cv);
        }
    }
}
