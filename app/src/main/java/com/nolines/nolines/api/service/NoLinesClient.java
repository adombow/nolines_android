package com.nolines.nolines.api.service;

import com.nolines.nolines.api.models.Ride;
import com.nolines.nolines.api.models.RideWindow;
import com.nolines.nolines.api.models.Ticket;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by timot on 3/14/2018.
 */

public interface NoLinesClient {

    @GET("/rides/")
    Call<List<Ride>> getRides(@Query("date") String date);

    @POST("/tickets")
    Call<Ticket> createTicket(@Field("ride_window_id") String ride_window_id, @Field("guest_id") String guest_id, @Field("date") String date);

}
