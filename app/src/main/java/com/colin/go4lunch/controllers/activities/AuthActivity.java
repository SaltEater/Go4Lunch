package com.colin.go4lunch.controllers.activities;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.bases.BaseActivity;
import com.colin.go4lunch.utils.UserHelper;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import java.util.Objects;

public class AuthActivity extends BaseActivity {
    private static final String TAG = "Auth activity";
    public static final int RC_SIGN_IN = 1234;

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_auth;
    }

    @Override
    protected void configureActivity() { configureAuth();
        configPreferences();
    }

    private void configureAuth() {
        if (!isCurrentUserLogged())
            startActivityForResult(startSignInActivity(), RC_SIGN_IN);
        else
            startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                registerUser(Objects.requireNonNull(getCurrentUser()), this);
            } else {
                if (response == null)
                    Log.d(TAG," : Authentication canceled");
                else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK)
                    Log.d(TAG," : No network found error");
                else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR)
                    Log.d(TAG," : Unknown error");
                else
                    Log.d(TAG," : Authentication failed");
            }
        }
    }

    @Override
    public void onBackPressed() {
        configureAuth();
    }

    private void configPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.contains(getString(R.string.notification_parameter)))
            sharedPreferences.edit().putBoolean(getString(R.string.notification_parameter), false).apply();
    }

    public Intent startSignInActivity() {
        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.AppTheme)
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(
                        Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.TwitterBuilder().build()
                        ))
                .build();
    }

    public void registerUser(FirebaseUser user, Context context) {
        UserHelper.getUser(user.getUid()).addOnCompleteListener(task -> {
            if (task.getResult() != null && !task.getResult().exists())
                UserHelper.createUser(user).addOnSuccessListener(aVoid -> Log.d(TAG, "User created"));
            context.startActivity(new Intent(context, MainActivity.class));
        });
    }
}
