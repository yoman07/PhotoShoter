package com.photoshoter;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by yoman on 28.04.2014.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, getResources().getString(R.string.PARSE_APPLICATION_ID), getResources().getString(R.string.PARSE_CLIENT_KEY));
        ParseFacebookUtils.initialize(getResources().getString(R.string.app_id));
    }
}
