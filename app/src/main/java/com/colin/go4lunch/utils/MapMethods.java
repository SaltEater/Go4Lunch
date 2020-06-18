package com.colin.go4lunch.utils;

import android.location.Location;

public interface MapMethods {
    void getNearbyPlaces(Location location);
    void onMarkerClick(String placeId);
}
