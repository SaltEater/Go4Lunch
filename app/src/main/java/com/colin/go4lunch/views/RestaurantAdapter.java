package com.colin.go4lunch.views;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.go4lunch.R;
import com.colin.go4lunch.models.Restaurant;
import com.colin.go4lunch.utils.PlaceUtils;

import java.util.ArrayList;

import butterknife.BindView;

import static butterknife.ButterKnife.bind;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantHolder> {
    private ArrayList<Restaurant> restaurants;

    public RestaurantAdapter(ArrayList<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_list_view, parent, false);
        return new RestaurantHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        PlaceUtils.getPhoto(restaurant.getPlace()).addOnSuccessListener((fetchPhotoResponse) -> {
            Bitmap bitmap = fetchPhotoResponse.getBitmap();
            holder.restaurantImage.setImageBitmap(bitmap);
        });
        String nbWorkmatesValue = "(" + restaurant.getNbWorkmates() + ")";
        holder.nbWorkmates.setText(nbWorkmatesValue);
        holder.restaurantAddress.setText(restaurant.getPlace().getAddress());
        holder.restaurantName.setText(restaurant.getPlace().getName());
        if (restaurant.getPlace().getOpeningHours() != null)
            holder.restaurantAperture.setText(restaurant.getPlace().getOpeningHours().toString());
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public class RestaurantHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.restaurant_img)
        ImageView restaurantImage;
        @BindView(R.id.restaurant_distance)
        TextView restaurantDistance;
        @BindView(R.id.restaurant_name)
        TextView restaurantName;
        @BindView(R.id.restaurant_address)
        TextView restaurantAddress;
        @BindView(R.id.restaurant_aperture)
        TextView restaurantAperture;
        @BindView(R.id.number_of_workmates_in_this_restaurant)
        TextView nbWorkmates;

        public RestaurantHolder(@NonNull View itemView) {
            super(itemView);
            bind(this, itemView);
        }
    }
}
