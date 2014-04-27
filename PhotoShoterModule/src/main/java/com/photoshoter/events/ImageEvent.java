package com.photoshoter.events;

import android.location.Location;

/**
 * Created by yoman on 27.04.2014.
 */
public class ImageEvent {
    private String senderId;
    private String base64image;
    private Location location;


    public ImageEvent(String senderId, Location location, String base64image) {
        this.senderId = senderId;
        this.base64image = base64image;
        this.location = location;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getBase64image() {
        return base64image;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "ImageEvent{" +
                "senderId='" + senderId + '\'' +
                ", base64image='" + base64image + '\'' +
                ", location=" + location +
                '}';
    }
}
