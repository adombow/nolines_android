package com.nolines.nolines.api.service;

import com.nolines.nolines.api.models.Ride;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by timot on 3/14/2018.
 */

public interface NoLinesClient {

    @GET("/rides")
    Call<List<Ride>> getRides();
}
