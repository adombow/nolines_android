package com.nolines.nolines;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.nolines.nolines.api.models.Ride;

import java.util.ArrayList;
import java.util.List;

public class PickTicketTimeActivity extends AppCompatActivity
        implements RideWindowFragment.OnListFragmentInteractionListener {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_ticket_time);

        textView = findViewById(R.id.rideNameText);

        Intent intent = getIntent();
        String rideName = intent.getStringExtra("RideName");
        textView.setText(rideName);
    }

    @Override
    public void onListFragmentInteraction(String rideWindow){

    }
}
