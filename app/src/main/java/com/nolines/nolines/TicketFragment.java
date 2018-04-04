package com.nolines.nolines;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nolines.nolines.adapters.TicketAdapter;
import com.nolines.nolines.api.models.GuestHolder;
import com.nolines.nolines.api.models.Ticket;
import com.nolines.nolines.api.service.Updateable;
import com.nolines.nolines.helpers.RecyclerItemTouchHelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TicketFragment extends Fragment implements Updateable, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    private static final String TAG = "TicketFragment";
    private static final String ARG_COLUMN_COUNT = "column-count";

    private OnListFragmentInteractionListener mListener;

    private TicketAdapter mAdapter;
    private GuestHolder mGuest;

    private Calendar calendar;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.empty_recyclerview) TextView emptyRecycler;
    @BindView(R.id.swiperefresh) SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TicketFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TicketFragment newInstance(int columnCount) {
        TicketFragment fragment = new TicketFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calendar = Calendar.getInstance();

        getGuest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_list, container, false);

        ButterKnife.bind(this, view);

        setupRefreshLayout();
        setupToolbar();
        setHasOptionsMenu(true);


        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


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
        if(mGuest != null)
            setupAdapter();
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
        mGuest.unregisterListener(this);
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
        void onListFragmentInteraction(Ticket ticket);
    }

    private void getGuest() {
        mGuest = GuestHolder.getInstance(this.getActivity());
        mGuest.registerListener(this);
        mGuest.refreshGuest();
    }

    @Override
    public void onRidesUpdate(){
    }


    @Override
    public void onTicketCreated(Ticket ticket, int status_code){

    }

    @Override
    public void onGuestUpdate(){
        if(mGuest.getGuestObject().getTickets().isEmpty()){
            recyclerView.setVisibility(View.GONE);
            emptyRecycler.setVisibility(View.VISIBLE);
        } else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyRecycler.setVisibility(View.GONE);
            mAdapter = new TicketAdapter(mGuest.getGuestObject().getTickets(), mListener, this.getContext());
            recyclerView.setAdapter(mAdapter);
            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT,this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setupToolbar(){
        toolbar.setTitle("Your Reservations");
        toolbar.setTitle("Reservations");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).setupActionBarDrawerToggle(toolbar);
    }

    private void setupAdapter(){
        List<Ticket> datedTickets = new ArrayList<Ticket>();

        for(Ticket ticket : mGuest.getGuestObject().getTickets()){
            //Look through all of the guest's tickets and check if they have any for the selected day
            if (ticket.getTime() != null) {
                Calendar ticketDate = ticket.getLocalDatetimeFromTime();
                if (ticketDate.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                        ticketDate.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                        ticketDate.get(Calendar.DATE) == calendar.get(Calendar.DATE)) {
                    datedTickets.add(ticket);
                }
            }
        }
        if(datedTickets.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            emptyRecycler.setVisibility(View.VISIBLE);
        } else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyRecycler.setVisibility(View.GONE);
            mAdapter = new TicketAdapter(datedTickets, mListener, this.getContext());
            recyclerView.setAdapter(mAdapter);
            ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT,this);
            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        }
    }

    private void setupRefreshLayout(){
        swipeRefreshLayout.setRefreshing(true);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mGuest.refreshGuest();
                    }
                }
        );
    }

    /**
     * Swipe Callback
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof TicketAdapter.TicketViewHolder) {
            int ticket_id = mAdapter.getItem(viewHolder.getAdapterPosition()).getId();
            mAdapter.removeItem(viewHolder.getAdapterPosition());
            mGuest.cancelTicket(ticket_id);
            swipeRefreshLayout.setRefreshing(true);
        }
    }
}
