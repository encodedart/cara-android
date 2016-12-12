package com.nexttapp.cara.api;

import com.nexttapp.cara.models.CreateOrderResponse;
import com.nexttapp.cara.models.SubmitLocationResponse;
import com.nexttapp.cara.models.TrackOrderResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by kimnguyen on 2016-12-11.
 */

public interface EndPoint {

    @GET("/createOrder")
    Call<CreateOrderResponse> createOrderAPI();

    @GET("/submitLocation")
    Call<SubmitLocationResponse> submitDriverLocationAPI(@Query("orderId") String orderId, @Query("latitude") double lat, @Query("longitude") double lon);

    @GET("/trackOrder")
    Call<TrackOrderResponse> trackOrderAPI(@Query("orderId") String orderID);
}
