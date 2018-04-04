package com.nolines.nolines.api.service;

import com.nolines.nolines.api.models.Guest;
import com.nolines.nolines.api.models.Ride;
import com.nolines.nolines.api.models.RideWindow;
import com.nolines.nolines.api.models.Ticket;
import com.nolines.nolines.api.models.TicketRequest;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    @DELETE("/tickets/{id}")
    Call<Void> cancelTicket(@Path("id")int ticket_id);

    @GET("/rides/")
    Call<List<Ride>> getRides(@Query("date") String date);

    @Headers("Content-Type: application/json")
    @POST("/tickets")
    Call<Ticket> requestTicket(@Body TicketRequest request);

    @GET("/rides/{ride_id}/windows")
    Call<List<RideWindow>> getRideWindows(@Path("ride_id")int ride_id);

    @Headers("Accept: application/json")
    @GET("/guests/{guest_id}")
    Call<Guest> getGuest(@Path("guest_id")int guest_id);
}
