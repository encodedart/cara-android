package com.nexttapp.cara.api;

import com.github.aurae.retrofit2.LoganSquareConverterFactory;
import com.nexttapp.cara.models.CreateOrderResponse;
import com.nexttapp.cara.models.SubmitLocationResponse;
import com.nexttapp.cara.models.TrackOrderResponse;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by kimnguyen on 2016-12-11.
 */

public class APIManager {

    private Retrofit retrofit;
    private EndPoint endPoint;

    public APIManager() {
        configureHTTPService();

    }

    public void getOrder(Callback<CreateOrderResponse> response) {
        Call<CreateOrderResponse> call = endPoint.createOrderAPI();
        call.enqueue(response);
    }

    public void submitLocale(String orderID, double lat, double lon, Callback<SubmitLocationResponse> response) {
        Call<SubmitLocationResponse> call = endPoint.submitDriverLocationAPI(orderID, lat, lon);
        call.enqueue(response);
    }

    public void getOrderTracking(String orderID, Callback<TrackOrderResponse> response) {
        Call<TrackOrderResponse> call = endPoint.trackOrderAPI(orderID);
        call.enqueue(response);
    }

    protected void configureHTTPService() {

        //request logging
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient grmClient = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES).readTimeout(2, TimeUnit.MINUTES)
                .addInterceptor(logging)
                .build();
//                .addInterceptor(new Interceptor() {
//
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        Request request = chain.request().newBuilder()
//                                .addHeader("DisplayContext", "AndroidApp")
//                                .build();
//
//                        Response response = chain.proceed(request);
//                        return response;
//                    }
//                }).build();


        String url = "http://159.203.1.45/";
        retrofit = new Retrofit.Builder().baseUrl(url)
                .addConverterFactory(LoganSquareConverterFactory.create())
                .client(grmClient)
                .build();

        endPoint = retrofit.create(EndPoint.class);
    }


}
