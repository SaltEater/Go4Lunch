package com.colin.go4lunch.controllers.fragments;

import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.activities.MainActivity;
import com.colin.go4lunch.controllers.bases.BaseFragment;
import com.colin.go4lunch.models.Restaurant;
import com.colin.go4lunch.utils.PlaceUtils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.OnClick;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapFragment extends BaseFragment implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    private GoogleMap mGMap;

    private FusedLocationProviderClient fusedLocationClient;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    private final String[] perms = {ACCESS_FINE_LOCATION};
    private static final int RC_LOCATION = 1001;

    private static final String TAG = "MAP_FRAGMENT";

    // ----------------------
    // PLACES
    // ----------------------

    private void findPlaces() {
        if (ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            PlaceUtils.fetchFindCurrentPlaceResponseTask(Arrays.asList( Place.Field.ID, Place.Field.LAT_LNG, Place.Field.TYPES)).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    assert response != null;
                    ArrayList<String> placesId = new ArrayList<>();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Place place = placeLikelihood.getPlace();
                        assert place.getTypes() != null;
                        if (place.getTypes().contains(Place.Type.RESTAURANT)) {
                            addMarker(place.getLatLng());
                            placesId.add(place.getId());
                            /*PlaceUtils.fetchPlaceResponseTask(place.getId(), Arrays.asList(
                                    Place.Field.ID,
                                    Place.Field.NAME,
                                    Place.Field.PHOTO_METADATAS,
                                    Place.Field.LAT_LNG,
                                    Place.Field.TYPES,
                                    Place.Field.ADDRESS,
                                    Place.Field.OPENING_HOURS)).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                                @Override
                                public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                                    ((MainActivity) getActivity()).updateListViewFragment(new Restaurant(fetchPlaceResponse.getPlace()));
                                }
                            });*/
                        }
                    }
                    ((MainActivity) getActivity()).updateListViewFragment(placesId);
                }
            });
        }
    }



    // ----------------------
    // PERMISSIONS
    // ----------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        configureFragment();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        new AppSettingsDialog.Builder(this).build().show();
    }

    // ----------------------
    // INITIALISATION
    // ----------------------

    @Override
    protected void configureFragment() {
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            configureMapApi();
        } else {
            EasyPermissions.requestPermissions(this, "Nous avons besoin d'avoir accès à la localisation", RC_LOCATION, perms);
        }
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_map;
    }


    // ----------------------
    // MAPS
    // ----------------------

    @OnClick(R.id.fragment_map_floating_action_btn)
    private void onClickFloatingActionBtn() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mGMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                            findPlaces();
                        }
                    }
                });
    }

    private void configureMapApi() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map_map_view);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGMap = googleMap;
        mGMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGMap.setIndoorEnabled(true);
        mGMap.setMyLocationEnabled(true);
        mGMap.getUiSettings().setMyLocationButtonEnabled(false);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mGMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                        }
                    }
                });
    }

    private void addMarker(LatLng position) {
        System.out.println("OUTPUT : Marker added");
        mGMap.addMarker(new MarkerOptions().position(position));
                //.title(title));
        //.setIcon(BitmapDescriptorFactory.fromResource(icon));
    }


}
