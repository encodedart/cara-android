package com.nexttapp.cara;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.nexttapp.cara.api.APIManager;

/**
 * Created by kimnguyen on 2016-12-11.
 */

public class Config {

    public static APIManager api;
    public static String orderID = "";
    public static String orderDate = "";
    public static int curView = 0;

    public static boolean hasPermission(final Context context, String permission) {
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            //Logger.e("No audio permission");
            return false;
        }
        return true;
    }
}
