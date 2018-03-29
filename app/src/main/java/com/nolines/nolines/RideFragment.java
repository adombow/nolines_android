package com.nolines.nolines;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.nolines.nolines.adapters.RideAdapter;
import com.nolines.nolines.api.models.Ride;
import com.nolines.nolines.api.models.RidesHolder;
import com.nolines.nolines.api.service.Updateable;

import java.text.DateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RideFragment extends Fragment implements Updateable{
    private static final String TAG = "RideFragment";
    private static final String ARG_COLUMN_COUNT = "column-count";

    private  OnListFragmentInteractionListener mListener;

    private RideAdapter mAdapter;
    private RidesHolder rides;

    private Calendar calendar;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.window_tabs) TabLayout tabLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RideFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RideFragment newInstance(int columnCount) {
        RideFragment fragment = new RideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calendar = Calendar.getInstance();

        getRides();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_list, container, false);

        ButterKnife.bind(this,view);

        setupToolbar();
        setupTabs();

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ride, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.date:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String date = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());
                        Log.i(TAG,date);
                        //btn_dialog_7.setText(date);
                        //
                        // dateTextView.setText(date);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setCalendarViewShown(false);
                datePickerDialog.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        rides.unregisterListener(this);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Ride ride);
    }

    private void getRides() {
        rides = RidesHolder.getInstance(this.getActivity());
        rides.registerListener(this);
        rides.refreshRides();


    }
    @Override
    public void onRidesUpdate(){
        mAdapter = new RideAdapter(rides.getRideList(),mListener);
        recyclerView.setAdapter(mAdapter);
    }

    private void setupToolbar(){
        toolbar.setTitle("Reservations");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).setupActionBarDrawerToggle(toolbar);
    }

    private void setupTabs(){
        tabLayout.addTab(tabLayout.newTab().setText("Morning"));
        tabLayout.addTab(tabLayout.newTab().setText("Afternoon"));
        tabLayout.addTab(tabLayout.newTab().setText("Evening"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                switch(position){
                    case 0:
                        mAdapter.setTimeFrame(Ride.MORNING);
                        break;
                    case 1:
                        mAdapter.setTimeFrame(Ride.AFTERNOON);
                        break;
                    case 2:
                        mAdapter.setTimeFrame(Ride.EVENING);
                        break;
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
