package com.photoshoter.models;

/**
 * Created by balu on 2014-04-25.
 */
public class UserDataProvider {

    static UserDataProvider instance;

    public static UserDataProvider getInstance() {
        if (instance == null) {
            instance = new UserDataProvider();
        }
        return instance;
    }


}
