package com.colin.go4lunch.controllers.fragments;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.bases.BaseFragment;
import com.colin.go4lunch.models.Restaurant;
import com.colin.go4lunch.utils.PlaceUtils;
import com.colin.go4lunch.views.RestaurantAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;

public class ListViewFragment extends BaseFragment {
    @BindView(R.id.restaurant_recycler_view)
    RecyclerView recyclerView;

    private ArrayList<Restaurant> restaurants = new ArrayList<>();
    private RestaurantAdapter adapter = new RestaurantAdapter(restaurants);


    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_list_view;
    }

    @Override
    protected void configureFragment() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    public static ListViewFragment newInstance() {
        return new ListViewFragment();
    }

    /*public void updateList(ArrayList<Restaurant> restaurants) {
        this.restaurants.clear();
        this.restaurants.addAll(restaurants);
        adapter.notifyDataSetChanged();
    }*/

    public void updateList(Restaurant restaurant) {
        if (!restaurants.contains(restaurant)) {
            restaurants.add(restaurant);
            adapter.notifyDataSetChanged();
        }
    }

    public void updateList(ArrayList<String> placesId) {
        restaurants.clear();
        for (String id : placesId) {
            if (! containsId(id)) {
                PlaceUtils.fetchPlaceResponseTask(id, Arrays.asList(
                        Place.Field.ID,
                        Place.Field.NAME,
                        Place.Field.PHOTO_METADATAS,
                        Place.Field.LAT_LNG,
                        Place.Field.TYPES,
                        Place.Field.ADDRESS,
                        Place.Field.OPENING_HOURS)).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        updateList(new Restaurant(fetchPlaceResponse.getPlace()));
                    }
                });
            }
        }
    }

    private boolean containsId(String id) {
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getPlace().getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
