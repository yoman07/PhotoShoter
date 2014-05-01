package com.photoshoter.models;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by balu on 2014-04-28.
 */
public class UserDataProvider {

    private static UserDataProvider instance;
    //Maping user FB id to user first name
    private Map<String, String> userMap = new HashMap<String, String>();
    //hold reference to received bitmap - temporary inefficient solution - lack of time
    private Uri currentPhoto;
    private String senderID;
    //lock for image receiving
    private boolean lock = false;

    public static UserDataProvider getInstance() {
        if (instance == null) {
            instance = new UserDataProvider();
        }
        return instance;
    }

    public void addUser(String fb_id, String name) {
        userMap.put(fb_id, name);
    }

    public String getUserName(String fb_id) {
        return userMap.get(fb_id);
    }

    public boolean isUserInUserMap(String fb_id) {
        return userMap.containsKey(fb_id);
    }

    public void holdReceivedBitmap(String senderID, Uri currentPhoto) {
        this.senderID = senderID;
        this.currentPhoto = currentPhoto;
    }

    public String getIdOfPhotoSender() {
        return senderID;
    }

    public Uri getCurrentPhoto() {
        return currentPhoto;
    }

    public void removeCurrenPhoto() {
        currentPhoto = null;
        senderID = null;
    }

    public void setLock() {
        lock = true;
    }

    public void releaseLock() {
        lock = false;
    }

    public boolean isLock() {
        return lock;
    }


}
