package com.photoshoter.models;

import android.location.Location;

/**
 * Created by balu on 2014-04-24.
 */
public class User {

    private String nick;
    private Location location;

    public User(String nick) {
        this.nick = nick;
    }

    public User(String nick, Location location) {
        this.nick = nick;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public String getNick() {
        return nick;
    }

    @Override
    public String toString() {
        return "User{" +
                "nick='" + nick + '\'' +
                ", location=" + location +
                '}';
    }
}
