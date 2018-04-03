package com.nolines.nolines;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nolines.nolines.api.models.Ride;
import com.nolines.nolines.api.models.RideWindow;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Timothy Leung on 4/1/2018.
 */

public class RideWindowDialog extends DialogFragment {
    @BindView(R.id.button1) Button reserveButton;
    @BindView(R.id.button2) Button cancelButton;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.subtitle) TextView subtitle;
    @BindView(R.id.details) TextView details;
    @BindView(R.id.card_image) ImageView rideImage;

    private int window_id;
    private int ride_id;

    public int getWindow_id() {
        return window_id;
    }

    public int getRide_id() {
        return ride_id;
    }

    public interface RideWindowDialogListener {
        public void onDialogPositiveClick(int rideIndex,int windowIndex);
    }

    RideWindowDialogListener mListener;

    public static RideWindowDialog newInstance(RideWindowDialogListener mListener, Ride ride, RideWindow rideWindow, String mDate){
        RideWindowDialog dialog = new RideWindowDialog();
        dialog.mListener = mListener;

        Bundle args = new Bundle();
        args.putString("ride_name",ride.getName());

        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df1.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getDefault());
        Date windowTime;

        try {
            windowTime = df1.parse(rideWindow.getStartTime());
            args.putString("window_time",formatter.format(windowTime));
        }
        catch(Exception e){

        }

        args.putString("date",mDate);
        args.putInt("window_id",rideWindow.getId());
        args.putInt("ride_id",ride.getId());
        args.putString("image_url",ride.getPhotoURL());

        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_ride_window_confirmation, null);
        ButterKnife.bind(this,view);

        Bundle args = getArguments();

        this.title.setText(args.getString("ride_name"));
        this.subtitle.setText(args.getString("date"));
        this.details.setText(args.getString("window_time"));
        this.ride_id = args.getInt("ride_id");
        this.window_id = args.getInt("window_id");
        Picasso.get().load(args.getString("image_url")).into(this.rideImage);

        builder.setView(view);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mListener.onDialogNegativeClick((RideWindowDialog) getParentFragment());
                getDialog().dismiss();
            }
        });

        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
                mListener.onDialogPositiveClick(ride_id,window_id);
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
