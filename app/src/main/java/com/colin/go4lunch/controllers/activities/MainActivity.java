package com.colin.go4lunch.controllers.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.bases.BaseActivity;
import com.colin.go4lunch.controllers.fragments.ListViewFragment;
import com.colin.go4lunch.controllers.fragments.MapFragment;
import com.colin.go4lunch.controllers.fragments.WorkmateFragment;
import com.colin.go4lunch.models.Restaurant;
import com.colin.go4lunch.models.User;
import com.colin.go4lunch.utils.Authentication;
import com.colin.go4lunch.utils.PlaceUtils;
import com.colin.go4lunch.utils.UserHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    private MapFragment mapFragment = MapFragment.newInstance();
    private ListViewFragment listViewFragment = ListViewFragment.newInstance();
    private WorkmateFragment workmateFragment = WorkmateFragment.newInstance();

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void configureActivity() {
        updateUI(Authentication.getCurrentUser());
        configureToolbar();
        configureDrawerLayout();
        configureNavigationView();
        configureBottomNavigationView();
        PlaceUtils.configPlaces(this);
    }

    // ---------------------
    // CONFIGURATION
    // ---------------------

    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void configureBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapFragment).commit();
    }

    private void configureToolbar() {
        setSupportActionBar(toolbar);
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
            UserHelper.getUser(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User currentUser = documentSnapshot.toObject(User.class);
                    assert currentUser != null;
                    name.setText(currentUser.getName());
                    email.setText(currentUser.getEmail());
                    if ((currentUser.getPhoto() != null)) {
                        Glide.with(getApplicationContext())
                                .load(currentUser.getPhoto())
                                .circleCrop()
                                .into(profilePicture);
                    } else {
                        profilePicture.setImageResource(R.mipmap.ic_launcher_round);

                    }
                }
            });
        }
    }

    /*public void updateListViewFragment(ArrayList<Restaurant> restaurants) {
        listViewFragment.updateList(restaurants);
    }*/
    public void updateListViewFragment(Restaurant restaurant) {
        listViewFragment.updateList(restaurant);
    }

    public void updateListViewFragment(ArrayList<String> placesId) {
        listViewFragment.updateList(placesId);
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
                // TODO : implémenter le clic sur "YOUR LUNCH"
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_logout:
                Authentication.logout(this);
                startActivity(new Intent(this, AuthActivity.class));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_map_view:
                            selectedFragment = mapFragment;
                            break;
                        case R.id.nav_list_view:
                            selectedFragment = listViewFragment;
                            break;
                        case R.id.nav_workmates:
                            selectedFragment = workmateFragment;
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };
}
