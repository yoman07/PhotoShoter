package com.photoshoter.events;

/**
 * Created by yoman on 25.04.2014.
 */
public class UserDisconnectEvent {
    private String fbId;

    public UserDisconnectEvent(String fbId) {
        this.fbId = fbId;
    }

    public String getFbId() {
        return fbId;
    }
}
