package com.colin.go4lunch.controllers.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.colin.go4lunch.BuildConfig;
import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.bases.BaseActivity;
import com.colin.go4lunch.controllers.fragments.ListViewFragment;
import com.colin.go4lunch.controllers.fragments.MapFragment;
import com.colin.go4lunch.controllers.fragments.WorkmateFragment;
import com.colin.go4lunch.models.FormattedPlace;
import com.colin.go4lunch.models.User;
import com.colin.go4lunch.models.googleplacesearch.MainGooglePlaceAPI;
import com.colin.go4lunch.models.googleplacesearch.Result;
import com.colin.go4lunch.utils.MapMethods;
import com.colin.go4lunch.utils.GoogleApiStream;
import com.colin.go4lunch.utils.UserHelper;
import com.colin.go4lunch.views.RestaurantAdapter;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, RestaurantAdapter.OnRestaurantListener, MapMethods {

    public static final String TAG = "MainActivity";

    private static final int AUTOCOMPLETE_REQUEST_CODE = 8;
    List<Place.Field> autocompleteFields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG);

    @BindView(R.id.main_activity_layout)
    LinearLayout linearLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    private ListViewFragment listViewFragment;
    private MapFragment mapFragment;
    private WorkmateFragment workmateFragment;

    private Disposable disposable;

    private ArrayList<FormattedPlace> places = new ArrayList<>();

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void configureActivity() {
        configPlaces();
        configureToolbar();
        configureFragments();
        configureDrawerLayout();
        configureNavigationView();
        configureBottomNavigationView();
        updateUI(getCurrentUser());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (disposable != null)
            disposable.dispose();
    }
    // ---------------------
    // CONFIGURATION
    // ---------------------

    private void configPlaces() {
        Places.initialize(getApplicationContext(), BuildConfig.google_maps_api);
    }

    private void configureFragments() {
        listViewFragment = ListViewFragment.newInstance(places, this);
        mapFragment = MapFragment.newInstance(this);
        workmateFragment = WorkmateFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapFragment).commit();
    }

    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void configureBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private void configSearchView(MenuItem searchItem) {
        // Config SearchView expand
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // SearchView expands only if mapFragment is not visible
                return !mapFragment.isVisible();
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        // Config SearchView Action View
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (listViewFragment.isVisible())
                    listViewFragment.getAdapter().getFilter().filter(newText);
                return true;
            }
        });
    }

    private void configureToolbar() {
        setSupportActionBar(toolbar);
    }

    // -----------------------
    // TOOLBAR
    // -----------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.activity_main_menu_search);
       configSearchView(searchItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.activity_main_menu_search) {
            if (mapFragment.isVisible() && mapFragment.getLastLocation() != null)
                launchPlaceAutocompleteActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //-------------------------
    // VIEWS
    // ------------------------

    private void updateUI(FirebaseUser user) {
        View header = navigationView.getHeaderView(0);
        ImageView profilePicture = header.findViewById(R.id.nav_drawer_profile_picture);
        TextView name = header.findViewById(R.id.nav_drawer_name);
        TextView email = header.findViewById(R.id.nav_drawer_email);

        if (isCurrentUserLogged()) {
            UserHelper.getUser(user.getUid()).addOnSuccessListener(documentSnapshot -> {
                User currentUser = documentSnapshot.toObject(User.class);
                if (currentUser != null) {
                    name.setText(currentUser.getName());
                    email.setText(currentUser.getEmail());
                    if ((currentUser.getPhoto() != null))
                        Glide.with(getApplicationContext())
                                .load(currentUser.getPhoto())
                                .circleCrop()
                                .into(profilePicture);
                    else
                        profilePicture.setImageResource(R.mipmap.ic_launcher_round);
                }
            });
        }
    }

    // ------------------------
    // NAVIGATION
    // -----------------------

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_lunch:
                UserHelper.getUser(Objects.requireNonNull(getCurrentUser()).getUid()).addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null && !user.getSelectedPlaceId().equals("")) {
                        int index = findPlacesWithId(user.getSelectedPlaceId());
                        if (index != -1)
                            launchRestaurantDetailActivity(places.get(index));
                        else {
                            FormattedPlace place = new FormattedPlace();
                            place.setId(user.getSelectedPlaceId());
                            place.setName(user.getSelectedPlaceName());
                            launchRestaurantDetailActivity(place);
                        }
                    }
                });
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_logout:
                logout(this);
                startActivity(new Intent(this, AuthActivity.class));
                break;
            case R.id.nav_map_view:
                launchFragment(mapFragment);
                break;
            case R.id.nav_list_view:
                launchFragment(listViewFragment);
                break;
            case R.id.nav_workmates:
                launchFragment(workmateFragment);
                break;
            default:
                return false;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // ----------------------
    // GOOGLE API SERVICE
    // ----------------------

    @Override
    public void getNearbyPlaces(Location location) {
        Task<Location> lastLocation = mapFragment.getLastLocation();
        if (lastLocation != null) {
            lastLocation.addOnSuccessListener(userLocation ->
                    disposable = GoogleApiStream.streamGetNearbyPlaces(BuildConfig.google_maps_api,
                            location.getLatitude() + "," + location.getLongitude(),
                            100)
                            .subscribeWith(new DisposableObserver<MainGooglePlaceAPI>() {
                                @Override
                                public void onNext(MainGooglePlaceAPI mainGooglePlaceAPI) {
                                    List<Result> results = mainGooglePlaceAPI.getResults();
                                    places.clear();
                                    for (Result result : results) {
                                        places.add(GoogleApiStream.convertResults(result, userLocation, getApplicationContext()));
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, "Something went wrong with getNearbyPlaces", e);
                                }

                                @Override
                                public void onComplete() {
                                    Log.d(TAG, "getNearbyPlaces finished");
                                    mapFragment.removeMarkers();
                                    mapFragment.markPlaces(places);
                                    listViewFragment.updateList();
                                }
                            }));
        }
    }


    // ----------------------
    // LISTENERS
    // ----------------------

    @Override
    public void onRestaurantClick(int position) {
        FormattedPlace place = places.get(position);
        launchRestaurantDetailActivity(place);
    }

    @Override
    public void onMarkerClick(String placeId) {
        FormattedPlace place = places.get(findPlacesWithId(placeId));
        launchRestaurantDetailActivity(place);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && data != null) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                if (place.getLatLng() != null) {
                    Location location = new Location("");
                    location.reset();
                    location.setLatitude(place.getLatLng().latitude);
                    location.setLongitude(place.getLatLng().longitude);
                    mapFragment.moveCamera(location);
                    getNearbyPlaces(location);
                }
            }
        }

    }


    // ----------------------
    // UTILS
    // ----------------------

    private int findPlacesWithId(String placeId) {
        for (int i = 0; i < places.size(); i++) {
            if (places.get(i).getId().equals(placeId))
                return i;
        }
        return -1;
    }

    private void launchPlaceAutocompleteActivity() {
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, autocompleteFields)
                .setCountry("FR")
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void launchRestaurantDetailActivity(FormattedPlace place) {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("FormattedPlace", place);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void launchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    public void logout(Context context) {
        AuthUI.getInstance().signOut(context).addOnCompleteListener(task -> {
            Intent intent = new Intent(context, AuthActivity.class);
            ((Activity) context).finish();
            context.startActivity(intent);
        });
    }

}
