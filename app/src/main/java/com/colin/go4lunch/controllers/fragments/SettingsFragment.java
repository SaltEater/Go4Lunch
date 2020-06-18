package com.colin.go4lunch.controllers.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.colin.go4lunch.R;
import com.colin.go4lunch.models.User;
import com.colin.go4lunch.utils.NotifyWorker;
import com.colin.go4lunch.utils.UserHelper;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    public SettingsFragment() {
        super();
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwitchPreferenceCompat switchPreferenceCompat = findPreference("notifications");
        if (switchPreferenceCompat != null) {
            switchPreferenceCompat.setChecked(PreferenceManager.getDefaultSharedPreferences(requireContext()).getBoolean(getString(R.string.notification_parameter), false));
            switchPreferenceCompat.setOnPreferenceChangeListener((preference, newValue) -> {
                SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
                preferences.putBoolean(getString(R.string.notification_parameter), (boolean) newValue).apply();
                return true;
            });
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (getString(R.string.notification_parameter).equals(key))
            this.configureNotification(sharedPreferences.getBoolean(getString(R.string.notification_parameter), false));
    }

    private void configureNotification(final Boolean isEnabled) {
        UserHelper.getUser(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addOnSuccessListener(success -> {
                    User user = success.toObject(User.class);
                    if (user != null && user.getSelectedPlaceId().equals(""))
                        return;

                    if (isEnabled) {
                        Constraints constraints = new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build();

                        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                                .setConstraints(constraints)
                                .setInitialDelay(getDeltaCalendar(Calendar.getInstance(), configureMidDayCalendar()), TimeUnit.MILLISECONDS)
                                .build();
                        WorkManager.getInstance(requireContext()).enqueue(request);
                    }
                    else
                        WorkManager.getInstance(requireContext()).cancelAllWork();
                });
    }

    public static long getDeltaCalendar(Calendar calendarNow, Calendar calendarMidDay) {
        long delta = calendarMidDay.getTimeInMillis() - calendarNow.getTimeInMillis();
        if (delta < 0) {
            calendarMidDay.set(Calendar.DAY_OF_MONTH, calendarMidDay.get(Calendar.DAY_OF_MONTH) + 1);
            delta = calendarMidDay.getTimeInMillis() - calendarNow.getTimeInMillis();
        }
        Log.d("DeltaCalendar", "Time to the next notification in minutes : " + delta / 1000 / 60);
        return delta;
    }

    public static Calendar configureMidDayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
}