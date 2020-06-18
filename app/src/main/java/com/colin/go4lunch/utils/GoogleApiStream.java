package com.colin.go4lunch.utils;

import android.content.Context;
import android.location.Location;

import com.colin.go4lunch.R;
import com.colin.go4lunch.models.FormattedPlace;
import com.colin.go4lunch.models.googleplacedetails.MainPlaceDetails;
import com.colin.go4lunch.models.googleplacesearch.MainGooglePlaceAPI;
import com.colin.go4lunch.models.googleplacesearch.Result;
import java.util.concurrent.TimeUnit;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GoogleApiStream {
    private static final String PLACE_TYPE = "restaurant";

    public static Observable<MainGooglePlaceAPI> streamGetNearbyPlaces(String key, String location, int radius) {
        GoogleApiService googleApiService = GoogleApiService.googleApiService;
        return googleApiService.getNearbyPlaces(key, location, radius, PLACE_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(20, TimeUnit.SECONDS);
    }

    public static Observable<MainPlaceDetails> streamGetPlaceDetails(String key, String placeId, String fields) {
        GoogleApiService googleApiService = GoogleApiService.googleApiService;
        return googleApiService.getPlaceDetails(key, placeId, fields)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(20, TimeUnit.SECONDS);
    }

    public static FormattedPlace convertResults(Result result, Location location, Context context) {
        final Location placeLocation = new Location("placeLocation");
        placeLocation.setLatitude(result.getGeometry().getLocation().getLat());
        placeLocation.setLongitude(result.getGeometry().getLocation().getLng());

        int distance = (int) location.distanceTo(placeLocation);
        String isOpenNow = "";
        if (result.getOpeningHours() != null && result.getOpeningHours().getOpenNow() != null) {
            isOpenNow = (result.getOpeningHours().getOpenNow())? context.getString(R.string.open): context.getString(R.string.closed);
        }
        return new FormattedPlace(
                result.getPlaceId(),
                result.getName(),
                result.getGeometry().getLocation().getLat(),
                result.getGeometry().getLocation().getLng(),
                (result.getRating() != null) ? (result.getRating()*3/5) : 0,
                (result.getPhotos() != null && result.getPhotos().size() != 0) ? result.getPhotos().get(0).getPhotoReference() : null,
                distance,
                isOpenNow
        );
    }

}