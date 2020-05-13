package com.colin.go4lunch.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.activities.AuthActivity;
import com.colin.go4lunch.controllers.activities.MainActivity;
import com.colin.go4lunch.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;

public abstract class Authentication {
    public static final String TAG = "AUTHENTICATION";
    public static final int RC_SIGN_IN = 1234;

    public Authentication(){}

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static Boolean isCurrentUserLogged() {
        return getCurrentUser() != null;
    }

    public static void logout(Context context) {
        AuthUI.getInstance().signOut(context).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent intent = new Intent(context, AuthActivity.class);
                ((Activity) context).finish();
                context.startActivity(intent);
            }
        });
    }

    public static Intent startSignInActivity() {
        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.AppTheme)
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(
                        Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()//,new AuthUI.IdpConfig.FacebookBuilder().build()
                        ))
                .build();
    }

    public static void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, Context context) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                registerUser(getCurrentUser(), context);
            } else {
                if (response == null)
                    System.out.println("OUTPUT : "+TAG +  " : Authentication canceled");
                else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK)
                    System.out.println("OUTPUT : "+TAG +  " : No network found error");
                else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR)
                    System.out.println("OUTPUT : "+TAG +  " : Unknown error");
                else
                    System.out.println("OUTPUT : "+TAG + " : Authentication failed");
            }
        }
    }

    public static void registerUser(FirebaseUser user, Context context) {
        UserHelper.getUser(user.getUid()).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.getResult().exists()) {
                    UserHelper.createUser(user);
                }
                context.startActivity(new Intent(context, MainActivity.class));
            }
        });
    }
}
