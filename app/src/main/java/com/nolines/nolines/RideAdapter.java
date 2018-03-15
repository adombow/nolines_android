package com.nolines.nolines;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

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

    public RideAdapter(List<Ride> rides) {
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
        //holder.rideImage.setImageResource(mRides.get(position).getPhotoID());
        holder.rideName.setText(mRides.get(position).getName());
        holder.rideWaitTime.setText(Integer.toString(mRides.get(position).getWaitTime()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ride currentRide = mRides.get(position);

                holder.toggleCardViewHeight(500);
                /*
                Context context = view.getContext();
                Intent intent = new Intent(context, PickTicketTimeActivity.class);
                intent.putExtra("RideName", currentRide.getName());
                context.startActivity(intent);*/
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
        @BindView(R.id.cardview) CardView cardView;
        @BindView(R.id.ride_name) TextView rideName;
        @BindView(R.id.ride_wait) TextView rideWaitTime;
        @BindView(R.id.ride_photo) ImageView rideImage;

        int minHeight;

        public RideViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

            cardView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    cardView.getViewTreeObserver().removeOnPreDrawListener(this);
                    minHeight= cardView.getHeight();
                    ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                    layoutParams.height = minHeight;
                    cardView.setLayoutParams(layoutParams);

                    return true;
                }
            });

        }

        private void toggleCardViewHeight(int height) {

            if (cardView.getHeight() == minHeight) {
                // expand
                expandView(height); //'height' is the height of screen which we have measured already.

            } else {
                // collapse
                collapseView();

            }
        }

        public void collapseView() {

            ValueAnimator anim = ValueAnimator.ofInt(cardView.getMeasuredHeightAndState(),
                    minHeight);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                    layoutParams.height = val;
                    cardView.setLayoutParams(layoutParams);

                }
            });
            anim.start();
        }
        public void expandView(int height) {

            ValueAnimator anim = ValueAnimator.ofInt(cardView.getMeasuredHeightAndState(),
                    height);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                    layoutParams.height = val;
                    cardView.setLayoutParams(layoutParams);
                }
            });
            anim.start();

        }
    }
}
