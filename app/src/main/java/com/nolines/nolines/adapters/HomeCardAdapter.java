package com.nolines.nolines.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nolines.nolines.HomeFragment.OnListFragmentInteractionListener;
import com.nolines.nolines.R;
import com.nolines.nolines.api.service.TicketAlarmProcessor;
import com.nolines.nolines.dummy.DummyContent.DummyItem;
import com.nolines.nolines.viewmodels.MapCard;
import com.nolines.nolines.viewmodels.ReservationCard;
import com.nolines.nolines.viewmodels.ServiceTestCard;
import com.nolines.nolines.viewmodels.ShopCard;
import com.nolines.nolines.viewmodels.WelcomeCard;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class HomeCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> mCards;
    private final OnListFragmentInteractionListener mListener;

    public final int WELCOME_CARD = 0;
    public final int MAP_CARD = 1;
    public final int RESERVATION_CARD = 2;
    public final int SERVICE_TEST_CARD = 3;
    public final int SHOP_CARD = 4;

    public HomeCardAdapter(List<Object> cards, OnListFragmentInteractionListener listener) {
        mCards = cards;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;


        switch (viewType) {
            case WELCOME_CARD:
                view = inflater.inflate(R.layout.card_welcome, parent, false);
                viewHolder = new WelcomeCardViewHolder(view);
                break;
            case SERVICE_TEST_CARD:
                view = inflater.inflate(R.layout.card_notification_test, parent, false);
                viewHolder = new ServiceTestCardViewHolder(view);
                break;
            case MAP_CARD:
                view = inflater.inflate(R.layout.card_map, parent, false);
                viewHolder = new MapCardViewHolder(view);
                break;
            case RESERVATION_CARD:
                view = inflater.inflate(R.layout.card_reservations, parent, false);
                viewHolder = new ReservationCardViewHolder(view);
                break;
            case SHOP_CARD:
                view = inflater.inflate(R.layout.card_shop, parent, false);
                viewHolder = new ShopCardViewHolder(view);
                break;
            default:
                view = inflater.inflate(R.layout.card_default, parent, false);
                viewHolder = new WelcomeCardViewHolder(view);
                break;
        }

        final int fViewType = viewType;

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onHomeCardClicked(fViewType);
                }
            }
        });

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
                vh1.title.setText("Welcome " + card.getName()+"!");

                int ticketCount = card.getTicketCount();

                if(ticketCount == 1){
                    vh1.subtitle.setText("You have one reservation today.");
                }
                else if (ticketCount > 1){
                    vh1.subtitle.setText("You have " +ticketCount + " reservations today.");
                }
                else{
                    vh1.subtitle.setText("You have no reservations for today.");
                }

                break;
            case SERVICE_TEST_CARD:
                ServiceTestCardViewHolder vh3 = (ServiceTestCardViewHolder) viewHolder;
                ServiceTestCard stCard = (ServiceTestCard) mCards.get(position);
                vh3.textView.setText(stCard.getDescription());
                vh3.button.setText(stCard.getButtonText());
                vh3.imageView.setImageResource(R.drawable.blueprint);
                vh3.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "Notification sent!", Toast.LENGTH_SHORT).show();

                        //GuestHolder guest = GuestHolder.getInstance(v.getContext());
                        //TicketAlarmProcessor.setNotificationsForTicket(guest.getGuestObject().getTickets().get(0), v.getContext());

                        TicketAlarmProcessor.startActionCheckTickets(v.getContext(), -1, TicketAlarmProcessor.NotificationType.CLOSE);
                    }
                });
                break;
            case MAP_CARD:
                MapCardViewHolder vh = (MapCardViewHolder) viewHolder;
                vh.title.setText("Lost?");
                vh.subtitle.setText("We can provide you with directions to your next attraction!");

                vh.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != mListener) {
                            // Notify the active callbacks interface (the activity, if the
                            // fragment is attached to one) that an item has been selected.
                            mListener.onHomeCardClicked(MAP_CARD);
                        }
                    }
                });
                break;
            case RESERVATION_CARD:
                ReservationCardViewHolder vh4 = (ReservationCardViewHolder) viewHolder;

                vh4.title.setText("Tired of waiting in line?");
                vh4.subtitle.setText("Make a NoLines reservation to reserve your spot in line.");
                vh4.imageView.setImageResource(R.drawable.header_lines);


                vh4.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (null != mListener) {
                            // Notify the active callbacks interface (the activity, if the
                            // fragment is attached to one) that an item has been selected.
                            mListener.onHomeCardClicked(RESERVATION_CARD);
                        }
                    }
                });
                break;
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
        } else if (card instanceof ReservationCard) {
            return RESERVATION_CARD;
        } else if(card instanceof MapCard){
            return MAP_CARD;
        } else if(card instanceof ShopCard){
            return SHOP_CARD;
        }

        return -1;
    }

    public void updateHomeCardList(List<Object> cards){
        mCards = cards;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }

    public class WelcomeCardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title) TextView title;
        @BindView(R.id.subtitle) TextView subtitle;

        public WelcomeCardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    public class ServiceTestCardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.welcome_cardview) CardView cardView;
        @BindView(R.id.title) TextView textView;
        @BindView(R.id.welcome_card_button) Button button;
        @BindView(R.id.card_image) ImageView imageView;

        public ServiceTestCardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    public class ReservationCardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.welcome_cardview) CardView cardView;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.subtitle) TextView subtitle;
        @BindView(R.id.welcome_card_button) Button button;
        @BindView(R.id.card_image) ImageView imageView;

        public ReservationCardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }

    public class MapCardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.welcome_cardview) CardView cardView;
        @BindView(R.id.title) TextView title;
        @BindView(R.id.subtitle) TextView subtitle;
        @BindView(R.id.welcome_card_button) Button button;

        public MapCardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }


    public class ShopCardViewHolder extends RecyclerView.ViewHolder {
        public ShopCardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
