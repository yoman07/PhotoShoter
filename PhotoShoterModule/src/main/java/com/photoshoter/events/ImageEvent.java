package com.photoshoter.events;

import android.location.Location;

/**
 * Created by yoman on 27.04.2014.
 */
public class ImageEvent {
    private String fbId;
    private String base64image;
    private Location location;


    public ImageEvent(String fbId, Location location, String base64image) {
        this.fbId = fbId;
        this.base64image = base64image;
        this.location = location;
    }

    public String getFbId() {
        return fbId;
    }

    public String getBase64image() {
        return base64image;
    }

    public Location getLocation() {
        return location;
    }
}
