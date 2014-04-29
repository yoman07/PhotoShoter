package com.photoshoter.location;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by balu on 2014-04-25.
 */
public class MarkerHolder {


    private static MarkerHolder instance;


    public static MarkerHolder getInstance() {
        if (instance == null) {
            instance = new MarkerHolder();
        }
        return instance;
    }

    private Map<String, Marker> markerMap = new HashMap<String, Marker>();
    private Map<String, Bitmap> markerIcon = new HashMap<String, Bitmap>();

    private Location myLocation = null;

    public void setMyLocation(Location loc) {
        myLocation = loc;
    }

    public Location getMyLocation() {
        return myLocation;
    }

    public void markerMapAddItems(Map<String, Marker> map) {
        markerMap.putAll(map);
    }

    public void markerIconMapAddItems(Map<String, Bitmap> map) {
        markerIcon.putAll(map);
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

    public Bitmap getMarkerIcon(String fb_id) {
        return markerIcon.get(fb_id);
    }

}
