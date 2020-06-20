package com.colin.go4lunch.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.colin.go4lunch.BuildConfig;

public abstract class NetworkUtils {

    public static void configGooglePhoto(Context context, String photoReference, ImageView image) {
        Glide.with(context)
                .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="
                        + photoReference + "&key=" + BuildConfig.google_maps_api)
                .centerCrop()
                .into(image);
    }
}
