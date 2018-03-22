package com.nolines.nolines;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nolines.nolines.api.models.Ride;
import com.nolines.nolines.api.models.RideWindow;
import com.nolines.nolines.api.models.RidesHolder;
import com.nolines.nolines.api.service.NoLinesClient;
import com.nolines.nolines.api.service.Updateable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    private NoLinesClient client;

    private RidesHolder rides;

    @BindView(R.id.ridelist) RecyclerView recyclerView;
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

        view.setBackgroundColor(Color.BLACK);

        ButterKnife.bind(this,view);

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
}
