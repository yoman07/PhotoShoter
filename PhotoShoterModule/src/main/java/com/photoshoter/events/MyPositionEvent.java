package com.photoshoter.events;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by yoman on 24.04.2014.
 */
public class MyPositionEvent {
    private Location location;

    public MyPositionEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public LatLng getLatLng() {
        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        return newLatLng;
    }

    @Override
    public String toString() {
        return "MyPositionEvent{" +
                "location=" + location +
                '}';
    }
}
