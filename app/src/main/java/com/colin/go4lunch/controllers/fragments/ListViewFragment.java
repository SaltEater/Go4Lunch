package com.colin.go4lunch.controllers.fragments;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.bases.BaseFragment;
import com.colin.go4lunch.models.FormattedPlace;
import com.colin.go4lunch.views.RestaurantAdapter;
import java.util.ArrayList;
import butterknife.BindView;

public class ListViewFragment extends BaseFragment {
    @BindView(R.id.restaurant_recycler_view)
    RecyclerView recyclerView;

    private ArrayList<FormattedPlace> places;
    private RestaurantAdapter adapter;
    private RestaurantAdapter.OnRestaurantListener onRestaurantListener;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_list_view;
    }

    @Override
    protected void configureFragment() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RestaurantAdapter(places, getContext(), onRestaurantListener);
        recyclerView.setAdapter(adapter);
    }

    private ListViewFragment(ArrayList<FormattedPlace> places, RestaurantAdapter.OnRestaurantListener onRestaurantListener) {
        this.places = places;
        this.onRestaurantListener = onRestaurantListener;
    }

    public static ListViewFragment newInstance(ArrayList<FormattedPlace> places, RestaurantAdapter.OnRestaurantListener onRestaurantListener) {
        return new ListViewFragment(places, onRestaurantListener);
    }

    public void updateList() {
        if (adapter != null){
            adapter.updatePlacesDisplayed();
            adapter.notifyDataSetChanged();
        }
    }

    public RestaurantAdapter getAdapter() {
        return adapter;
    }
}
