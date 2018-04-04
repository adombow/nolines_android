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

    private String window_id;

    public String getWindow_id() {
        return window_id;
    }

    public interface RideWindowDialogListener {
        void onDialogPositiveClick(String window_id);
    }

    RideWindowDialogListener mListener;

    public static RideWindowDialog newInstance(RideWindowDialogListener mListener, Ride ride, RideWindow rideWindow, String mDate){
        RideWindowDialog dialog = new RideWindowDialog();
        dialog.mListener = mListener;

        Bundle args = new Bundle();
        args.putString("ride_name",ride.getName());

        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df1.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try {
            date = df1.parse(ride.getWindowDate());
            args.putString("window_time",DateFormat.getTimeInstance(DateFormat.SHORT).format(date.getTime()));

        }
        catch(Exception e){}

        args.putString("date",mDate);
        args.putString("window_id",rideWindow.getIdString());
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
        this.window_id = args.getString("window_id");
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
                mListener.onDialogPositiveClick(window_id);
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
