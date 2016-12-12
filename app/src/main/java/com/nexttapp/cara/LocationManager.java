package com.nexttapp.cara;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by kimnguyen on 2016-12-11.
 */

public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final Context context;
    private GoogleApiClient mGoogleApiClient;
    private boolean isConnected = false;

    public LocationManager(final Context context) {
        this.context = context;
        connectToGoogle();
    }

    private void connectToGoogle() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Nullable
    public LatLng getCurrentLocation() {
        if (!isConnected) {
            connectToGoogle();
            return null;
        }

        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LatLng l = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            return l;
        } catch (SecurityException e) {
            //this should not happens, we already check for permission granted
            return null;
        }
//        if (Helper.hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
//
//        } else {
//            return null;
//        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        isConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        isConnected = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        isConnected = false;
    }
}
