package com.nexttapp.cara.controller;

import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.pwittchen.reactivenetwork.library.Connectivity;
import com.github.pwittchen.reactivenetwork.library.ReactiveNetwork;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nexttapp.cara.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by kimnguyen on 2016-12-11.
 */

public abstract class BaseController extends Fragment implements OnMapReadyCallback {

    protected String TAG = "[BASE CONTROLLER]";

    protected ViewGroup rootView;
    protected long waitUpdate = 5000;
//    protected String orderID = "";
    private CountDownTimer timer;
    protected GoogleMap map = null;
    protected BitmapDescriptor selectIcon = null;
    protected boolean firstRun = true;
    protected Marker currentMarker = null;
    protected boolean lastNetworkState = true;

    private Subscription networkMonitor;
    private SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM, yyyy hh:mm:ss a");

    protected abstract int getLayoutID();
    protected abstract void bindView();
    protected abstract void render();
    protected void getTracking() {}
    protected void resumeRender() {}
    protected void onNextWorkStatus(boolean enable) {}


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int id = getLayoutID();
        if (id == 0) return new View(getContext());
        rootView = (ViewGroup) inflater.inflate(id, container, false);
        return rootView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (firstRun) {
            bindView();
            render();
//            networkCheck();
        } else {
            resumeRender();
        }
        firstRun = false;
    }

    protected String getDate() {
        return DATE_FORMAT.format(new Date());
    }

    protected void getMap(int id) {
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(id);
        if (mapFragment != null) {
            Log.e(TAG,"FOund map fragment");
            mapFragment.getMapAsync(this);
        }

        selectIcon = BitmapDescriptorFactory.fromResource(R.drawable.selected_bp_pin_smaller);
    }

    protected void startUpdate() {
        if (timer == null) {
            timer = new CountDownTimer(waitUpdate, waitUpdate) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    getTracking();
                }
            };
            timer.start();
        } else {
            timer.start();
        }
    }

    protected void stopUpdate() {
        if (timer != null) timer.cancel();
    }

    protected void zoomToLocation(LatLng point) {
        if (map == null) return;
        //Log.e(TAG, "Zoom in location " + point.toString());
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 12.0f));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 14.0f));
    }

    protected void createMaker(LatLng pos) {
        if (pos == null) return;
        MarkerOptions op = new MarkerOptions();
        op.position(pos);
        currentMarker = map.addMarker(op);
        currentMarker.setIcon(selectIcon);

    }

//    @Override
//    public void onPause() {
//        super.onPause();
//    }

    protected void updateMaker(LatLng pos) {
        if (pos == null) return;
        if (currentMarker == null) {
            createMaker(pos);
        } else {
            currentMarker.setPosition(pos);
        }
        zoomToLocation(pos);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (networkMonitor != null) {
            networkMonitor.unsubscribe();
            networkMonitor = null;
        }
    }

    protected void networkCheck() {
//        ReactiveNetwork.observeInternetConnectivity()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Boolean>() {
//                    @Override public void call(Boolean isConnectedToInternet) {
//                        // do something with isConnectedToInternet value
//                        Log.e(TAG,"Connect to internet");
//                    }
//                });
        networkMonitor = ReactiveNetwork.observeNetworkConnectivity(getContext().getApplicationContext())
                .subscribeOn(Schedulers.io())
//                .filter(Connectivity.hasState(NetworkInfo.State.CONNECTED))
//                .filter(Connectivity.hasType(ConnectivityManager.TYPE_WIFI))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Connectivity>() {
                    @Override public void call(Connectivity connectivity) {
                        // do something
                        Log.e(TAG, "Connection " + connectivity.getState());
                        onNextWorkStatus(connectivity.getState() == NetworkInfo.State.CONNECTED);
                    }
                });

    }
}
