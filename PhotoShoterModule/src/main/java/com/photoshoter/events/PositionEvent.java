package com.photoshoter.events;

import android.location.Location;

/**
 * Created by yoman on 24.04.2014.
 */
public class PositionEvent {
    private Location location;

    public PositionEvent(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "PositionEvent{" +
                "location=" + location +
                '}';
    }
}
