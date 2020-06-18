package com.colin.go4lunch.views;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.colin.go4lunch.BuildConfig;
import com.colin.go4lunch.R;
import com.colin.go4lunch.models.FormattedPlace;
import com.colin.go4lunch.utils.UserHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import static butterknife.ButterKnife.bind;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantHolder> implements Filterable {
    private ArrayList<FormattedPlace> placesDisplayed;
    private ArrayList<FormattedPlace> places;
    private Geocoder geocoder;
    private Context context;
    private OnRestaurantListener onRestaurantListener;

    public RestaurantAdapter(ArrayList<FormattedPlace> places, Context context, OnRestaurantListener onRestaurantListener) {
        this.places = places;
        this.placesDisplayed = new ArrayList<>(places);
        this.geocoder = new Geocoder(context, Locale.getDefault());
        this.context = context;
        this.onRestaurantListener = onRestaurantListener;
    }

    @NonNull
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_list_view, parent, false);
        return new RestaurantHolder(v, onRestaurantListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantHolder holder, int position) {
        FormattedPlace place = placesDisplayed.get(position);
        Glide.with(context)
                .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + place.getPhotoReference()+"&key=" + BuildConfig.google_maps_api)
                .into(holder.restaurantImage);
        UserHelper.getUsersInterestedByPlace(place.getId()).addOnSuccessListener(queryDocumentSnapshots -> {
            String nbWorkmatesValue = "(" + queryDocumentSnapshots.size() + ")";
            holder.nbWorkmates.setText(nbWorkmatesValue);
        });

        if (place.getAddress() == null) {
            try {
                List<Address> addresses = geocoder.getFromLocation(place.getLocationLatitude(), place.getLocationLongitude(), 1);
               place.setAddress((addresses.get(0).getAddressLine(0)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        holder.restaurantAddress.setText(place.getAddress());
        String distance = place.getDistance()+"m";
        holder.restaurantDistance.setText(distance);

        holder.restaurantName.setText(place.getName());
        if (place.getIsOpenNow() != null)
            holder.restaurantAperture.setText(place.getIsOpenNow());

        configStars(holder, place);
    }

    private void configStars(RestaurantHolder holder, FormattedPlace place) {
        int rating = (int) place.getRating();
        switch (rating) {
            case 1 :
                holder.ratingStar1.setVisibility(View.VISIBLE);
                holder.ratingStar2.setVisibility(View.INVISIBLE);
                holder.ratingStar3.setVisibility(View.INVISIBLE);
                break;
            case 2 :
                holder.ratingStar1.setVisibility(View.VISIBLE);
                holder.ratingStar2.setVisibility(View.VISIBLE);
                holder.ratingStar3.setVisibility(View.INVISIBLE);
                break;
            case 3 :
                holder.ratingStar1.setVisibility(View.VISIBLE);
                holder.ratingStar2.setVisibility(View.VISIBLE);
                holder.ratingStar3.setVisibility(View.VISIBLE);
                break;
            default:
                holder.ratingStar1.setVisibility(View.INVISIBLE);
                holder.ratingStar2.setVisibility(View.INVISIBLE);
                holder.ratingStar3.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return placesDisplayed.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<FormattedPlace> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(places);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (FormattedPlace formattedPlace : places) {
                    if (formattedPlace.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(formattedPlace);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            placesDisplayed.clear();
            placesDisplayed.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

    public void updatePlacesDisplayed() {
        placesDisplayed = new ArrayList<>(places);
    }

    public interface OnRestaurantListener {
        void onRestaurantClick(int position);
    }

}

class RestaurantHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
    @BindView(R.id.rate_1_star)
    ImageView ratingStar1;
    @BindView(R.id.rate_2_stars)
    ImageView ratingStar2;
    @BindView(R.id.rate_3_stars)
    ImageView ratingStar3;

    private RestaurantAdapter.OnRestaurantListener onRestaurantListener;

    RestaurantHolder(@NonNull View itemView, RestaurantAdapter.OnRestaurantListener onRestaurantListener) {
        super(itemView);
        bind(this, itemView);
        this.onRestaurantListener = onRestaurantListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onRestaurantListener.onRestaurantClick(getAdapterPosition());
    }
}



