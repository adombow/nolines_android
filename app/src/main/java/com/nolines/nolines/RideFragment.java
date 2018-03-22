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
import com.nolines.nolines.api.service.NoLinesClient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
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
public class RideFragment extends Fragment {
    private static  final String TAG = "RideFragment";
    private static final String ARG_COLUMN_COUNT = "column-count";

    private  OnListFragmentInteractionListener mListener;

    private RideAdapter mAdapter;

    private NoLinesClient client;

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

        initHTTPClient();
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

    private void initHTTPClient(){
        /* Modify Later to put baseUrl globally*/
        Retrofit.Builder builder = new Retrofit.Builder()
                //.baseUrl("http://nolines-production.herokuapp.com/")
                .baseUrl("http://128.189.90.85:3001/")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        client = retrofit.create(NoLinesClient.class);
    }

    private void getRides(){


        Call<List<Ride>> rideCall = client.getRides();
        rideCall.enqueue(new Callback<List<Ride>>() {

            @Override
            public void onResponse(Call<List<Ride>> call, Response<List<Ride>> response) {
                List<Ride> rides = response.body();

                mAdapter = new RideAdapter(rides);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<Ride>> call, Throwable t) {
                Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_SHORT).show();
            }
        });



    }


    private void getRides(int id){


    }
}
