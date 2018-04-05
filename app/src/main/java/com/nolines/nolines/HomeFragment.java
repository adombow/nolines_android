package com.nolines.nolines;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nolines.nolines.adapters.HomeCardAdapter;
import com.nolines.nolines.api.models.Guest;
import com.nolines.nolines.api.models.GuestHolder;
import com.nolines.nolines.api.models.Ticket;
import com.nolines.nolines.api.service.Updateable;
import com.nolines.nolines.dummy.DummyContent;
import com.nolines.nolines.dummy.DummyContent.DummyItem;
import com.nolines.nolines.viewmodels.MapCard;
import com.nolines.nolines.viewmodels.ReservationCard;
import com.nolines.nolines.viewmodels.ServiceTestCard;
import com.nolines.nolines.viewmodels.ShopCard;
import com.nolines.nolines.viewmodels.WelcomeCard;

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
public class HomeFragment extends Fragment implements Updateable{

    @BindView(R.id.home_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private MainActivity appCompatActivity;

    private OnListFragmentInteractionListener mListener;
    private GuestHolder guest;
    private HomeCardAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static HomeFragment newInstance(int columnCount) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homecard_list, container, false);
        ButterKnife.bind(this,view);

        toolbar.setTitle("NoLines");
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).setupActionBarDrawerToggle(toolbar);

        // Set the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        guest = GuestHolder.getInstance(this.getContext());
        guest.registerListener(this);
        guest.refreshGuest();
        List<Object> list = new ArrayList<>();

        if(guest.getGuestObject() != null) {
            Guest guest_obj = guest.getGuestObject();
            Calendar calendar = Calendar.getInstance();
            list.add(new WelcomeCard(guest_obj.getName(), guest_obj.getTicketsForDate(calendar).size()));
        }
        list.add(new ReservationCard());
        list.add(new MapCard());
        list.add(new ShopCard());
        list.add(new ServiceTestCard("Notification Service Test", "Send Notification"));

        recyclerView.setAdapter(new HomeCardAdapter(list, mListener));
        return view;
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
        guest.unregisterListener(this);
    }

    @Override
    public void onPause(){
        super.onPause();
        guest.unregisterListener(this);
    }

    @Override
    public void onRidesUpdate() {

    }

    @Override
    public void onGuestUpdate() {
        List<Object> list = new ArrayList<>();
        if(guest.getGuestObject() != null) {
            Guest guest_obj = guest.getGuestObject();
            Calendar calendar = Calendar.getInstance();
            list.add(new WelcomeCard(guest_obj.getName(), guest_obj.getTicketsForDate(calendar).size()));
        }
        list.add(new ReservationCard());
        list.add(new MapCard());
        list.add(new ShopCard());
        list.add(new ServiceTestCard("Notification Service Test", "Send Notification"));


        if(mAdapter == null) {
            mAdapter = new HomeCardAdapter(list, mListener);
            recyclerView.setAdapter(mAdapter);
        }else{
            mAdapter.updateHomeCardList(list);
        }
    }

    @Override
    public void onTicketCreated(Ticket ticket, int status_code){

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
        void onHomeCardClicked(int viewType);
    }
}
