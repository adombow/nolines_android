package com.nolines.nolines.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nolines.nolines.HomeFragment.OnListFragmentInteractionListener;
import com.nolines.nolines.R;
import com.nolines.nolines.api.models.GuestHolder;
import com.nolines.nolines.api.models.Ride;
import com.nolines.nolines.api.models.Ticket;
import com.nolines.nolines.api.service.TicketAlarmProcessor;
import com.nolines.nolines.dummy.DummyContent.DummyItem;
import com.nolines.nolines.viewmodels.ServiceTestCard;
import com.nolines.nolines.viewmodels.WelcomeCard;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class HomeCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Object> mCards;
    private final OnListFragmentInteractionListener mListener;

    private final int WELCOME_CARD = 0;
    private final int RIDE_CARD = 0;
    private final int SERVICE_TEST_CARD = 3;

    public HomeCardAdapter(List<Object> cards, OnListFragmentInteractionListener listener) {
        mCards = cards;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case WELCOME_CARD:
                View v1 = inflater.inflate(R.layout.fragment_welcome_card, parent, false);
                viewHolder = new WelcomeCardViewHolder(v1);
                break;
            case SERVICE_TEST_CARD:
                View v2 = inflater.inflate(R.layout.fragment_welcome_card, parent, false);
                viewHolder = new ServiceTestCardViewHolder(v2);
                break;
            default:
                View vd = inflater.inflate(R.layout.fragment_welcome_card, parent, false);
                viewHolder = new WelcomeCardViewHolder(vd);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        /*holder.mItem = cards.get(position);
        holder.mIdView.setText(cards.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });*/
        switch (viewHolder.getItemViewType()) {
            case WELCOME_CARD:
                WelcomeCardViewHolder vh1 = (WelcomeCardViewHolder) viewHolder;
                WelcomeCard card = (WelcomeCard) mCards.get(position);
                ((WelcomeCardViewHolder) viewHolder).textView.setText(card.getName());

                break;
            case SERVICE_TEST_CARD:
                ServiceTestCardViewHolder vh3 = (ServiceTestCardViewHolder) viewHolder;
                ServiceTestCard stCard = (ServiceTestCard) mCards.get(position);
                vh3.textView.setText(stCard.getDescription());
                vh3.button.setText(stCard.getButtonText());
                vh3.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "Notification sent!", Toast.LENGTH_SHORT).show();

                        //GuestHolder guest = GuestHolder.getInstance(v.getContext());
                        //TicketAlarmProcessor.setNotificationsForTicket(guest.getGuestObject().getTickets().get(0), v.getContext());

                        TicketAlarmProcessor.startActionCheckTickets(v.getContext(), -1, TicketAlarmProcessor.NotificationType.CLOSE);
                    }
                });
            default:
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object card = mCards.get(position);
        if (card instanceof WelcomeCard) {
            return WELCOME_CARD;
        } else if(card instanceof ServiceTestCard){
            return SERVICE_TEST_CARD;
        }

        return -1;
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    public class WelcomeCardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.welcome_cardview) CardView cardView;
        @BindView(R.id.title) TextView textView;

        public WelcomeCardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    public class ServiceTestCardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.welcome_cardview) CardView cardView;
        @BindView(R.id.title) TextView textView;
        @BindView(R.id.welcome_card_button) Button button;

        public ServiceTestCardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
