package com.photoshoter.models;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
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

    private Map<String, Marker> markerMap = new HashMap<String, Marker>();
    private Map<String, Bitmap> markerIcon = new HashMap<String, Bitmap>();


    public void markerMapAddItems(Map<String, Marker> map) {

        for (Map.Entry<String, Marker> entry : map.entrySet()) {
            String key = entry.getKey();
            Marker value = entry.getValue();
            markerMap.put(key, value);
        }
    }

    public void markerIconMapAddItems(Map<String, Bitmap> map) {
        for (Map.Entry<String, Bitmap> entry : map.entrySet()) {
            String key = entry.getKey();
            Bitmap value = entry.getValue();
            markerIcon.put(key, value);
        }
    }

    public Map<String, Marker> returnMarkerMap() {
        return markerMap;
    }

    public Map<String, Bitmap> returnMarkerIconMap() {
        return markerIcon;
    }

    public void clearMarkerMap() {
        markerMap.clear();
    }

    public void clearMarkerIconMap() {
        markerIcon.clear();
    }

    public boolean isMarkerMapEmpty() {
        if (markerMap.size() > 0)
            return false;
        return true;
    }

    public boolean isMarkerIconMapEmpty() {
        if (markerIcon.size() > 0)
            return false;
        return true;
    }

}
