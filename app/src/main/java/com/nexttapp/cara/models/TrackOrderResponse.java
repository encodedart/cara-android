package com.nexttapp.cara.models;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.io.Serializable;

/**
 * Created by kimnguyen on 2016-12-11.
 */

@JsonObject
public class TrackOrderResponse implements Serializable {

    @JsonField(name = "latitude")
    public double lat;
    @JsonField(name = "longitude")
    public double lon;
}
