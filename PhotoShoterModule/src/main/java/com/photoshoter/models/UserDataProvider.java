package com.photoshoter.models;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by balu on 2014-04-25.
 */
public class UserDataProvider {

    private static UserDataProvider instance;

    public static UserDataProvider getInstance() {
        if (instance == null) {
            instance = new UserDataProvider();
        }
        return instance;
    }

    private Map<String, User> users = new LinkedHashMap<String, User>();
    private Map<String, Bitmap> markerIcon = new HashMap<String, Bitmap>();

    public void addUser(User user) {
        if (!isUserInDataProvider(user)) {
            String fb_id = user.getFbId();
            users.put(user.getFbId(), user);
        }
    }

    public void removeUser(User user) {
        String fb_id = user.getFbId();
        if (isUserInDataProvider(user)) {
            users.remove(fb_id);
        }
    }

    public void removeUser(String fb_id) {
        if (isUserInDataProvider(fb_id)) {
            users.remove(fb_id);
        }
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public boolean isUserInDataProvider(User user) {
        if (users.get(user.getFbId()) != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isUserInDataProvider(String user) {
        return users.containsKey(user);
    }

    public Bitmap getMarkerIcon(String fb_id) {
        if (isMarkerIconStored(fb_id)) {
            return markerIcon.get(fb_id);
        } else {
            return null;
        }
    }

    public boolean isMarkerIconStored(String fb_id) {
        return (markerIcon.containsKey(fb_id));
    }

    public void addMarkerIcon(String fb_id, Bitmap icon) {
        markerIcon.put(fb_id, icon);
    }


}
