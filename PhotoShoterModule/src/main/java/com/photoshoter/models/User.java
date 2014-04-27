package com.photoshoter.models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by balu on 2014-04-24.
 */
public class User {

    private String fbId;
    private Location location;

    public User(String fbId) {
        this.fbId = fbId;
    }

    public User(String fbId, Location location) {
        this.fbId = fbId;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public LatLng getLatLng() {
        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        return newLatLng;
    }

    public String getFbId() {
        return fbId;
    }

    @Override
    public String toString() {
        return "User{" +
                "fbId='" + fbId + '\'' +
                ", location=" + location +
                '}';
    }
}
