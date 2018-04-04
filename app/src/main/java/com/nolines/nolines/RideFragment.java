package com.nolines.nolines;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.nolines.nolines.adapters.RideAdapter;
import com.nolines.nolines.api.models.Ride;
import com.nolines.nolines.api.models.RidesHolder;
import com.nolines.nolines.api.models.Ticket;
import com.nolines.nolines.api.service.Updateable;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class RideFragment extends Fragment implements Updateable, RideWindowDialog.RideWindowDialogListener, TicketCreatedDialog.TicketCreatedDialogListener {
    private static final String TAG = "RideFragment";
    private static final String ARG_COLUMN_COUNT = "column-count";

    private OnListFragmentInteractionListener mListener;

    private RideAdapter mAdapter;
    private RidesHolder rides;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.window_tabs)
    TabLayout tabLayout;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;

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
        getRides();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_list, container, false);

        ButterKnife.bind(this, view);

        setupRefreshLayout();
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
                        rides.calendar.set(Calendar.YEAR, year);
                        rides.calendar.set(Calendar.MONTH, monthOfYear);
                        rides.calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        swipeRefreshLayout.setRefreshing(true);
                        rides.refreshRides();
                    }
                }, rides.calendar.get(Calendar.YEAR), rides.calendar.get(Calendar.MONTH), rides.calendar.get(Calendar.DAY_OF_MONTH));
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
    public void onDestroy() {
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
    public void onRidesUpdate() {
        if (mAdapter == null) {
            mAdapter = new RideAdapter(rides.getRideList(), this.getContext(), this);
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.updateRideList(rides.getRideList());
        }

        swipeRefreshLayout.setRefreshing(false);
    }

    private void setupToolbar() {
        toolbar.setTitle("Reservations");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).setupActionBarDrawerToggle(toolbar);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.morning));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.afternoon));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.evening));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (mAdapter == null)
                    return;

                int position = tab.getPosition();

                switch (position) {
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

    private void setupRefreshLayout() {
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        rides.refreshRides();
                    }
                }
        );
    }

    @Override
    public void onGuestUpdate() {

    }

    @Override
    public void onTicketCreated(Ticket ticket, int status_code) {

        if(status_code == 200){
            TicketCreatedDialog dialog = TicketCreatedDialog.newInstance(this, ticket, getString(R.string.header_reservation_made));
            dialog.show(((Activity) this.getContext()).getFragmentManager(), "TicketCreated");
        }
        else {
            TicketErrorDialog dialog = new TicketErrorDialog();
            dialog.show(((Activity) this.getContext()).getFragmentManager(), "TicketError");

        }

        swipeRefreshLayout.setRefreshing(true);
        rides.refreshRides();
    }

    //Ticket Dialog
    @Override
    public void onGoToTickets() {
        Fragment fragment = null;
        Class fragmentClass = TicketFragment.class;

        if(fragmentClass != null){
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(fragment != null){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.fragment_container, fragment).commit();
        }
    }

    //Ride Window
    @Override
    public void onDialogPositiveClick(String window_id) {
        rides.requestTicket(window_id, "1");
    }
}
