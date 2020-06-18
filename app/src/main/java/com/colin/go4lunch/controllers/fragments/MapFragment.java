package com.colin.go4lunch.controllers.fragments;

import android.content.pm.PackageManager;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.bases.BaseFragment;
import com.colin.go4lunch.models.FormattedPlace;
import com.colin.go4lunch.utils.MapMethods;
import com.colin.go4lunch.utils.UserHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import butterknife.OnClick;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapFragment extends BaseFragment implements OnMapReadyCallback {

    private GoogleMap mGMap;

    private FusedLocationProviderClient fusedLocationClient;

    private MapMethods mapMethods;

    private ArrayList<Marker> markers = new ArrayList<>();

    public static MapFragment newInstance(MapMethods mapMethods) {
        return new MapFragment(mapMethods);
    }

    private MapFragment(MapMethods mapMethods) {
        this.mapMethods = mapMethods;
    }

    private final String[] perms = {ACCESS_FINE_LOCATION};
    private static final int RC_LOCATION = 1001;

    // ----------------------
    // PERMISSIONS
    // ----------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_LOCATION)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                configureMapApi();
    }

    // ----------------------
    // INITIALISATION
    // ----------------------

    @Override
    protected void configureFragment() {
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            configureMapApi();
        } else {
            requestPermissions(perms, RC_LOCATION);
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
    void onClickFloatingActionBtn() {
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    mGMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                    mapMethods.getNearbyPlaces(location);
                }
            });
        } else {
            requestPermissions(perms, RC_LOCATION);
        }
    }

    public Task<Location> getLastLocation() {
        if (fusedLocationClient != null && ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return fusedLocationClient.getLastLocation();
        return null;
    }

    private void configureMapApi() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map_map_view);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGMap = googleMap;
        mGMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGMap.setIndoorEnabled(true);
        mGMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (ContextCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            moveCamera(location);
                            mapMethods.getNearbyPlaces(location);
                        }
                    });
            mGMap.setOnMarkerClickListener(marker -> {
                mapMethods.onMarkerClick((String) marker.getTag());
                return true;
            });
        }
    }

    public void markPlaces(ArrayList<FormattedPlace> places) {
        for (int i = 0; i < places.size(); i++) {
            FormattedPlace place = places.get(i);
            UserHelper.getUsersInterestedByPlace(places.get(i).getId()).addOnSuccessListener(queryDocumentSnapshots -> {
                if (queryDocumentSnapshots.size() > 0) {
                    markers.add(mGMap.addMarker(new MarkerOptions().position(new LatLng(place.getLocationLatitude(), place.getLocationLongitude()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
                } else {
                    markers.add(mGMap.addMarker(new MarkerOptions().position(new LatLng(place.getLocationLatitude(), place.getLocationLongitude()))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));
                }
                markers.get(markers.size() - 1).setTag(place.getId());
            });
        }
    }

    public void removeMarkers() {
        for (int i = 0; i < markers.size(); i++)
            markers.get(i).remove();
        markers.clear();
    }

    public void moveCamera(Location location) {
        mGMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
    }


}
