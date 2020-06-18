package com.colin.go4lunch.utils;

import com.colin.go4lunch.models.googleplacedetails.MainPlaceDetails;
import com.colin.go4lunch.models.googleplacesearch.MainGooglePlaceAPI;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiService {

    String baseUrl = "https://maps.googleapis.com/maps/api/place/";

    @GET("nearbysearch/json")
    Observable<MainGooglePlaceAPI> getNearbyPlaces(@Query("key") String key,
                                             @Query("location") String location,
                                             @Query("radius") int radius,
                                             @Query("type") String placeType);


    @GET("details/json")
    Observable<MainPlaceDetails> getPlaceDetails(@Query("key") String key,
                                           @Query("placeid") String id,
                                           @Query("fields") String filters);


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build();

    GoogleApiService googleApiService = retrofit.create(GoogleApiService.class);

}
