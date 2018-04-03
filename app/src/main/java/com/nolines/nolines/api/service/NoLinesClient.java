package com.nolines.nolines.api.service;

import com.nolines.nolines.api.models.Guest;
import com.nolines.nolines.api.models.Ride;
import com.nolines.nolines.api.models.RideWindow;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by timot on 3/14/2018.
 */

public interface NoLinesClient {

   /* @GET("/rides")
    Call<List<Ride>> getRides();*/

    @GET("/rides")
    Call<List<Ride>> getRides();

    @GET("/rides/{ride_id}/windows")
    Call<List<RideWindow>> getRideWindows(@Path("ride_id")int ride_id);

    @Headers("Accept: application/json")
    @GET("/guests/{guest_id}")
    Call<Guest> getGuest(@Path("guest_id")int guest_id);
}
