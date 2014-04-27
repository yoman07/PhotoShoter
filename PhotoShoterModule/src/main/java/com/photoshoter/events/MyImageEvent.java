package com.photoshoter.events;

import android.location.Location;

import com.parse.codec.binary.Base64;

/**
 * Created by yoman on 27.04.2014.
 */
public class MyImageEvent {
    private String base64image;
    private Location location;

    public MyImageEvent(String base64image, Location location) throws IllegalArgumentException {
        boolean isBase64 = Base64.isBase64(base64image);
        if(isBase64) {
            this.base64image = base64image;
            this.location = location;
        } else {
            throw new IllegalArgumentException("Incorrect base64image format");
        }
    }

    public String getBase64image() {
        return base64image;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "MyImageEvent{" +
                "base64image='" + base64image + '\'' +
                ", location=" + location +
                '}';
    }
}
