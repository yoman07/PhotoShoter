package com.photoshoter.models;

import com.parse.ParseUser;

/**
 * Created by yoman on 27.04.2014.
 */
public class MyUserData {
    public static String getUserId() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String userId = (String) currentUser.get("fb_id");
        return userId;
    }
}
