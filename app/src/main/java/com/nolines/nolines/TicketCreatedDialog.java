package com.nolines.nolines;

import butterknife.ButterKnife;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nolines.nolines.api.models.Ticket;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.BindView;


public class TicketCreatedDialog extends DialogFragment {
    @BindView(R.id.button1) Button myTicketButton;
    @BindView(R.id.button2) Button cancelButton;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.subtitle) TextView subtitle;
    @BindView(R.id.details) TextView details;
    @BindView(R.id.card_image) ImageView rideImage;

    public interface TicketCreatedDialogListener {
        void onGoToTickets();
    }

    TicketCreatedDialogListener mListener;

    public static TicketCreatedDialog newInstance(TicketCreatedDialogListener mListener, Ticket ticket, String title){
        TicketCreatedDialog dialog = new TicketCreatedDialog();
        dialog.mListener = mListener;

        Bundle args = new Bundle();
        args.putString("ride_name",ticket.getRide().getName());

        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df1.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date windowTime;

        try {
            windowTime = df1.parse(ticket.getTime());
            args.putString("date",DateFormat.getDateInstance(DateFormat.FULL).format(windowTime.getTime()));
        }
        catch(Exception e){

        }
        args.putString("title",title);
        args.putString("image_url",ticket.getRide().getPhotoURL());

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

        this.title.setText(args.getString("title"));
        this.subtitle.setText(args.getString("ride_name"));
        this.details.setText(args.getString("date"));
        Picasso.get().load(args.getString("image_url")).into(this.rideImage);

        builder.setView(view);

        cancelButton.setText(R.string.btn_dismiss);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDialog().dismiss();
            }
        });

        myTicketButton.setText(R.string.btn_my_tickets);
        myTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
                mListener.onGoToTickets();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
