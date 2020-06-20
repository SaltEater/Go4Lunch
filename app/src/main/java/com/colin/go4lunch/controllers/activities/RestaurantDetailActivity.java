package com.colin.go4lunch.controllers.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.colin.go4lunch.BuildConfig;
import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.bases.BaseActivity;
import com.colin.go4lunch.controllers.fragments.SettingsFragment;
import com.colin.go4lunch.models.FormattedPlace;
import com.colin.go4lunch.models.User;
import com.colin.go4lunch.models.googleplacedetails.MainPlaceDetails;
import com.colin.go4lunch.models.googleplacedetails.Result;
import com.colin.go4lunch.utils.GoogleApiStream;
import com.colin.go4lunch.utils.NetworkUtils;
import com.colin.go4lunch.utils.NotifyWorker;
import com.colin.go4lunch.utils.UserHelper;
import com.colin.go4lunch.views.WorkmateAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import static android.Manifest.permission.CALL_PHONE;

public class RestaurantDetailActivity extends BaseActivity {
    @BindView(R.id.activity_details_restaurant_img)
    ImageView restaurantImage;

    @BindView(R.id.activity_details_restaurant_name)
    TextView restaurantName;

    @BindView(R.id.activity_details_restaurant_address)
    TextView restaurantAddress;

    @BindView(R.id.activity_details_restaurant_rate_1_star)
    ImageView restaurantRatingStar1;

    @BindView(R.id.activity_details_restaurant_rate_2_stars)
    ImageView restaurantRatingStar2;

    @BindView(R.id.activity_details_restaurant_rate_3_stars)
    ImageView restaurantRatingStar3;

    @BindView(R.id.activity_details_restaurant_floating_btn)
    FloatingActionButton fab;

    @BindView(R.id.activity_details_restaurant_recycler_view_container)
    RecyclerView recyclerView;


    private FormattedPlace place;
    private static final String TAG = "DetailActivity";

    public static final String[] perms = {CALL_PHONE};

    public static final int UNDEFINED_LIKED_PLACE = -1;
    public static final int PLACE_LIKED = 1;
    public static final int PLACE_NOT_LIKED = 0;
    public static final int RC_CALL = 792;

    private int likedPlace = UNDEFINED_LIKED_PLACE;

    private String dataMissing = null;

    private Disposable disposable;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_restaurant_detail;
    }

    @Override
    protected void configureActivity() {
        if (getIntent() != null)
            place = (FormattedPlace) getIntent().getSerializableExtra("FormattedPlace");
        updateUI();
        configView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (disposable != null)
            disposable.dispose();
    }

    // ----------------------
    // VIEWS
    // ----------------------

    public void updateUI() {
        checkData();
        if (!dataMissing.equals(""))
            getDetails();
        else
            configView();
        configFab();
        configRecyclerView();
    }

    // ----------------------
    // ONCLICK
    // ----------------------

    @OnClick(R.id.activity_details_restaurant_floating_btn)
    void onClickFloatingActionButton() {
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null) {
                if (user.getSelectedPlaceId().equals(place.getId())) {
                    UserHelper.updateString("selectedPlaceId", "", user.getId())
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "SelectedPlaceId value updated"));
                    UserHelper.updateString("selectedPlaceName", "", user.getId())
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "SelectedPlaceName value updated"));
                    fab.setImageResource(R.drawable.ic_add);
                    configureNotificationWorker(false);
                } else {
                    UserHelper.updateString("selectedPlaceId", place.getId(), user.getId())
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "SelectedPlaceId value updated"));
                    UserHelper.updateString("selectedPlaceName", place.getName(), user.getId())
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "SelectedPlaceName value updated"));
                    fab.setImageResource(R.drawable.ic_check_circle);
                    configureNotificationWorker(true);
                }
            }
        });
    }

    @OnClick(R.id.activity_details_restaurant_container_call)
    void onClickCall() {
        if (place.getPhoneNumber() != null && !place.getPhoneNumber().equals("")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + place.getPhoneNumber()));
                startActivity(callIntent);
            } else {
                ActivityCompat.requestPermissions(this, perms, RC_CALL);
            }

        }
    }

    @OnClick(R.id.activity_details_restaurant_container_rate)
    void onClickLike() {
        if (likedPlace == UNDEFINED_LIKED_PLACE) {
            UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    if (user.getLikedPlaces().contains(place.getId())) {
                        UserHelper.deleteLikedPlace(place.getId(), user.getId())
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Restaurant like successfully removed"));
                        likedPlace = PLACE_NOT_LIKED;
                    } else {
                        UserHelper.addLikedPlace(place.getId(), user.getId())
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Restaurant successfully liked"));
                        likedPlace = PLACE_LIKED;
                    }
                }
            });
        } else {
            if (likedPlace == PLACE_NOT_LIKED) {
                UserHelper.addLikedPlace(place.getId(), getCurrentUser().getUid())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Restaurant successfully liked"));
                likedPlace = PLACE_LIKED;
            } else {
                UserHelper.deleteLikedPlace(place.getId(), getCurrentUser().getUid())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Restaurant like successfully removed"));
                likedPlace = PLACE_NOT_LIKED;
            }
        }
    }

    @OnClick(R.id.activity_details_restaurant_container_website)
    void onClickWebsite() {
        if (place.getWebsite() != null && !place.getWebsite().equals("")) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(place.getWebsite()));
            startActivity(i);
        }
    }

    // ----------------------
    // CONFIGURATIONS
    // ----------------------

    private void configView() {
        configStars();
        configText();
        configPhoto();
    }

    private void configRecyclerView() {
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(UserHelper.getUsersInterestedByPlaceQuery(place.getId()), User.class)
                .setLifecycleOwner(this)
                .build();
        WorkmateAdapter adapter = new WorkmateAdapter(options, this, WorkmateAdapter.DETAIL_ACTIVITY_VERSION);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void configFab() {
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            if (user != null && user.getSelectedPlaceId().equals(place.getId()))
                fab.setImageResource(R.drawable.ic_check_circle);
            else
                fab.setImageResource(R.drawable.ic_add);
        });
    }

    private void configText() {
        restaurantName.setText(place.getName());
        restaurantAddress.setText(place.getAddress());
    }

    private void configPhoto() {
        NetworkUtils.configGooglePhoto(this, place.getPhotoReference(), restaurantImage);
    }

    public void configStarsVisibility(int state1, int state2, int state3) {
        restaurantRatingStar1.setVisibility(state1);
        restaurantRatingStar2.setVisibility(state2);
        restaurantRatingStar3.setVisibility(state3);
    }

    private void configStars() {
        int rating = (int) place.getRating();
        final int visible = View.VISIBLE;
        final int invisible = View.INVISIBLE;
        switch (rating) {
            case 1:
                configStarsVisibility(visible, invisible, invisible);
                break;
            case 2:
                configStarsVisibility(visible, visible, invisible);
                break;
            case 3:
                configStarsVisibility(visible, visible, visible);
                break;
            default:
                configStarsVisibility(invisible, invisible, invisible);
        }
    }

    // ----------------------
    // UTILS
    // ----------------------

    private void checkData() {
        dataMissing = "";
        if (place.getPhotoReference() == null || place.getPhotoReference().equals(""))
            dataMissing += "photos,";
        if (place.getWebsite() == null || place.getWebsite().equals(""))
            dataMissing += "website,";
        if (place.getPhoneNumber() == null || place.getPhoneNumber().equals(""))
            dataMissing += "formatted_phone_number,";
        if (place.getRating() == -1)
            dataMissing += "rating,";
        if (place.getAddress() == null || place.getAddress().equals(""))
            dataMissing += "formatted_address,";

        if (dataMissing.length() != 0)
            dataMissing = dataMissing.substring(0, dataMissing.length() - 1);


    }

    public void getDetails() {
        disposable = GoogleApiStream.streamGetPlaceDetails(BuildConfig.google_maps_api,
                place.getId(), dataMissing)
                .subscribeWith(new DisposableObserver<MainPlaceDetails>() {
                    @Override
                    public void onNext(MainPlaceDetails mainPlaceDetails) {
                        Result result = mainPlaceDetails.getResult();
                        updatePlace(result);
                        configView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Something went wrong with getDetails", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "getDetails finished");
                    }
                });
    }

    private void updatePlace(Result result) {
        if (result.getFormattedAddress() != null)
            place.setAddress(result.getFormattedAddress());
        if (result.getRating() != null)
            place.setRating(result.getRating() * 3 / 5);
        if (result.getPhotos() != null && result.getPhotos().size() != 0)
            place.setPhotoReference(result.getPhotos().get(0).getPhotoReference());
        if (result.getWebsite() != null)
            place.setWebsite(result.getWebsite());
        if (result.getFormattedPhoneNumber() != null)
            place.setPhoneNumber(result.getFormattedPhoneNumber());
    }

    // ----------------------
    // NOTIFICATIONS
    // ----------------------

    private void configureNotificationWorker(Boolean isEnabled) {
        WorkManager.getInstance(this).cancelAllWork();
        if (isEnabled) {
            Data data = new Data.Builder()
                    .putString("placeName", place.getName())
                    .putString("placeId", place.getId())
                    .putString("placeAddress", place.getAddress())
                    .build();

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                    .setConstraints(constraints)
                    .setInputData(data)
                    .setInitialDelay(SettingsFragment.getDeltaCalendar(Calendar.getInstance(), SettingsFragment.configureMidDayCalendar()), TimeUnit.MILLISECONDS)
                    .build();
            WorkManager.getInstance(this).enqueue(request);
        }
    }


}
