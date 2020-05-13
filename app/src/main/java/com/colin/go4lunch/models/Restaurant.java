package com.colin.go4lunch.models;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.type.LatLng;

import java.util.Arrays;
import java.util.List;

public class Restaurant {
    private int nbWorkmates;
    private Place place;

    public Restaurant(Place place) {
        this.place = place;
        nbWorkmates = 0;
    }

    public int getNbWorkmates() {
        return nbWorkmates;
    }

    public void setNbWorkmates(int nbWorkmates) {
        this.nbWorkmates = nbWorkmates;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }


}
