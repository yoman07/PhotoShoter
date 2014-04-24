package com.photoshoter.events;

import android.location.Location;

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

    @Override
    public String toString() {
        return "MyPositionEvent{" +
                "location=" + location +
                '}';
    }
}
