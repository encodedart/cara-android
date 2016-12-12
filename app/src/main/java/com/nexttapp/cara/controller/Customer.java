package com.nexttapp.cara.controller;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nexttapp.cara.Config;
import com.nexttapp.cara.R;
import com.nexttapp.cara.models.CreateOrderResponse;
import com.nexttapp.cara.models.TrackOrderResponse;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kimnguyen on 2016-12-11.
 */

public class Customer extends BaseController  {

    @BindView(R.id.place_order_btn)
    protected TextView placeOrder;
    @BindView(R.id.user_info_view)
    protected View userInfoView;
    @BindView(R.id.view_map)
    protected View mapView;
    @BindView(R.id.cust_order_txt)
    protected TextView orderNum;
    @BindView(R.id.cust_order_date_txt)
    protected TextView orderDate;
    @BindView(R.id.cust_update_wait)
    protected TextView wait;
    @BindView(R.id.cust_error)
    protected TextView networkError;

//    @BindView(R.id.google_map)


    private boolean gettingOrder = false;
    private TrackOrderResponse tracking;
    private boolean isGettingOrder = true;


    @OnClick(R.id.cust_new_order)
    public void onNewOrder() {
        stopUpdate();
        setPlaceOrder();
    }

    @Override
    protected int getLayoutID() {
        TAG ="[CUSTOMER]";
        return R.layout.view_customer;
    }

    @Override
    protected void bindView() {
        ButterKnife.bind(this, rootView);
    }

    @Override
    protected void render() {
        setPlaceOrder();
        getMap(R.id.google_map);
        networkError.setVisibility(View.GONE);
        networkCheck();
    }

    @Override
    protected void onNextWorkStatus(boolean enable) {
        if (lastNetworkState == enable) return;
        if (enable) {
            //resume show
            networkError.setVisibility(View.GONE);
//            if (isGettingOrder) {
//                setPlaceOrder();
//            } else {
//                renderTracking();
//            }
        } else {
//            placeOrder.setVisibility(View.GONE);
//            wait.setVisibility(View.GONE);
//            userInfoView.setVisibility(View.GONE);
//            mapView.setVisibility(View.GONE);
            networkError.setVisibility(View.VISIBLE);
        }
        lastNetworkState = enable;

    }

    @Override
    protected void resumeRender() {
        super.resumeRender();
        if (!orderID.isEmpty()) getTracking();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopUpdate();
    }

    private void setPlaceOrder() {
        orderID = "";
        isGettingOrder = true;
        placeOrder.setVisibility(View.VISIBLE);
        wait.setVisibility(View.GONE);
        userInfoView.setVisibility(View.GONE);
        mapView.setVisibility(View.GONE);
        gettingOrder = false;
    }

    private void renderTracking() {
        if (orderID.isEmpty()) {
            setPlaceOrder();
            return;
        }

        isGettingOrder = false;

        placeOrder.setVisibility(View.GONE);
        mapView.setVisibility(View.VISIBLE);
        userInfoView.setVisibility(View.VISIBLE);
        wait.setVisibility(View.VISIBLE);

        orderNum.setText("Order: #" + orderID);
        orderDate.setText(getDate());

        getTracking();
        startUpdate();
    }


    @Override
    protected void getTracking() {
        Config.api.getOrderTracking(orderID, trackOrderResponseCallback);
    }

    private void updateTracking() {
        if (tracking == null) return;
        if (tracking.lat == 0.0 && tracking.lon == 0.0) return;

        Log.e(TAG,tracking.lat + "," + tracking.lon);

        if (wait.getVisibility() == View.VISIBLE) {
            wait.setVisibility(View.GONE);
        }

        LatLng loc = new LatLng(tracking.lat, tracking.lon);

        updateMaker(loc);
    }

    @OnClick(R.id.place_order_btn)
    public void onPlaceOrderClicked() {
        if (gettingOrder) return;
        gettingOrder = true;
        Config.api.getOrder(createOrderResponseCallback);
    }

    private Callback<TrackOrderResponse> trackOrderResponseCallback = new Callback<TrackOrderResponse>() {
        @Override
        public void onResponse(Call<TrackOrderResponse> call, Response<TrackOrderResponse> response) {

            if (response != null && response.body() != null) {
                tracking = response.body();
//                tracking.lat = lat;
//                tracking.lon = lon;
                startUpdate();
                updateTracking();
//                lat += 0.0008;
//                lon += 0.0006;
            }
        }

        @Override
        public void onFailure(Call<TrackOrderResponse> call, Throwable t) {
            startUpdate();
        }
    };

    private Callback<CreateOrderResponse> createOrderResponseCallback = new Callback<CreateOrderResponse>() {
        @Override
        public void onResponse(Call<CreateOrderResponse> call, Response<CreateOrderResponse> response) {
            if (response != null && response.body() != null) {
                orderID = response.body().orderID;
                renderTracking();
            }
        }

        @Override
        public void onFailure(Call<CreateOrderResponse> call, Throwable t) {
            gettingOrder = false;
        }
    };


}
