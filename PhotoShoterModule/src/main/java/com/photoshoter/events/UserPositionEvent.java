package com.photoshoter.events;

import android.location.Location;

import com.photoshoter.models.User;

/**
 * Created by yoman on 24.04.2014.
 */
public class UserPositionEvent {
    private User user;

    public UserPositionEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "UserPositionEvent{" +
                "user=" + user +
                '}';
    }
}
