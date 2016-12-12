package com.nexttapp.cara.controller;

import android.Manifest;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nexttapp.cara.Config;
import com.nexttapp.cara.LocationManager;
import com.nexttapp.cara.R;
import com.nexttapp.cara.models.SubmitLocationResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kimnguyen on 2016-12-11.
 */

public class Driver extends BaseController {

    @BindView(R.id.driver_info_view)
    protected View infoView;
    @BindView(R.id.driver_view_map)
    protected View mapView;
    @BindView(R.id.driver_order_view)
    protected View inputView;
    @BindView(R.id.driver_warning_txt)
    protected TextView warning;

    @BindView(R.id.driver_input_error)
    protected TextView inputError;
    @BindView(R.id.driver_order_input)
    protected EditText orderNum;
    @BindView(R.id.drvier_location_txt)
    protected TextView locationTxt;
    @BindView(R.id.driver_date_info)
    protected TextView  dateTxt;
    @BindView(R.id.driver_error)
    protected TextView networkError;

    private boolean checkOrder = false;
    private LocationManager locationManager;
    private boolean isMapShow = false;
    private LatLng curLoc;

    private double lat = 43.849869;
    private double lon = -79.510986;

    @Override
    protected int getLayoutID() {
        return R.layout.view_driver;
    }

    @Override
    protected void bindView() {
        ButterKnife.bind(this, rootView);
    }

    @Override
    protected void onNextWorkStatus(boolean enable) {
        if (enable == lastNetworkState) return;

        if (enable) {
            networkError.setVisibility(View.GONE);
        } else {
            networkError.setVisibility(View.VISIBLE);
        }
        lastNetworkState = enable;
    }

    @Override
    protected void render() {
        networkError.setVisibility(View.GONE);
        networkCheck();
        showInput();
        Dexter.checkPermission(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                locationManager = new LocationManager(getContext());
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                failPermission();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                failPermission();
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void onPause() {
        super.onPause();
//        stopUpdate();
    }

    private void showInput() {
        orderID = "";
        checkOrder = false;
        isMapShow = false;
        orderNum.setEnabled(true);
        orderNum.setText("");
        inputView.setVisibility(View.VISIBLE);
        mapView.setVisibility(View.GONE);
        infoView.setVisibility(View.GONE);
        warning.setVisibility(View.GONE);

        getMap(R.id.driver_google_map);
    }

    private void failPermission() {
        showInput();
        inputView.setVisibility(View.GONE);
        warning.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.driver_tracking_btn)
    public void onOrderEntered() {
        if (checkOrder) return;
        String num = orderNum.getText().toString();
        if (num == null || num.isEmpty()) {
            inputError.setText("Please enter order number.");
            inputError.setVisibility(View.VISIBLE);
            return;
        }

        inputError.setVisibility(View.GONE);
        checkOrder = true;
        orderNum.setEnabled(false);
        orderID = num;
        getTracking();
    }

    @OnClick(R.id.driver_new_tracking)
    public void onNewTracking() {
        stopUpdate();
        showInput();
    }

    private void updateMap(SubmitLocationResponse rsp) {
        if (!isMapShow) {
            isMapShow = true;
            inputView.setVisibility(View.GONE);
            mapView.setVisibility(View.VISIBLE);
            infoView.setVisibility(View.VISIBLE);
            warning.setVisibility(View.GONE);
        }

        if (curLoc == null) return;
        String t = "Location: " + String.format("%.6f",curLoc.latitude) + "," + String.format("%.6f",curLoc.longitude);
        locationTxt.setText(t);
        dateTxt.setText(getDate());

        updateMaker(curLoc);
//        zoomToLocation(curLoc);
    }

    @Override
    protected void getTracking() {
        super.getTracking();
        if (locationManager == null) {
            stopUpdate();
            failPermission();
        }
        curLoc = locationManager.getCurrentLocation();
//        curLoc = new LatLng(lat,lon);
//        lat += 0.0008;
//        lon += 0.0006;

        if (curLoc != null) {
            Config.api.submitLocale(orderID, curLoc.latitude, curLoc.longitude, submitLocationResponseCallback);
        }

    }

    private Callback<SubmitLocationResponse> submitLocationResponseCallback = new Callback<SubmitLocationResponse>() {
        @Override
        public void onResponse(Call<SubmitLocationResponse> call, Response<SubmitLocationResponse> response) {
            if (response != null && response.body() != null) updateMap(response.body());
            startUpdate();
        }

        @Override
        public void onFailure(Call<SubmitLocationResponse> call, Throwable t) {
            stopUpdate();
        }
    };
}