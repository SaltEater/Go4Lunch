package com.colin.go4lunch.utils;

import android.app.Activity;
import android.graphics.Bitmap;

import com.colin.go4lunch.BuildConfig;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class PlaceUtils {

    private static String apiKey = BuildConfig.google_maps_api;
    private static PlacesClient placesClient;

    public static void configPlaces(Activity activity) {
        if (!Places.isInitialized()) {
            Places.initialize(activity, apiKey);
            placesClient = Places.createClient(activity);
        }
    }
    public static Task<FindCurrentPlaceResponse> fetchFindCurrentPlaceResponseTask(List<Place.Field> placeFields) {
        //Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.TYPES);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
        return placesClient.findCurrentPlace(request);
    }

    public static Task<FetchPlaceResponse> fetchPlaceResponseTask(String placeId, List<Place.Field> placeFields) {
        /*List<Place.Field> placeFields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.PHOTO_METADATAS,
                Place.Field.LAT_LNG,
                Place.Field.TYPES,
                Place.Field.ADDRESS,
                Place.Field.OPENING_HOURS);*/
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        return placesClient.fetchPlace(request);
    }

    public static Task<FetchPhotoResponse> getPhoto(Place place) {
        PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                .setMaxWidth(500)
                .setMaxHeight(300)
                .build();
        return placesClient.fetchPhoto(photoRequest);
    }





}
