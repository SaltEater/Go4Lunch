package com.colin.go4lunch.controllers.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.bases.BaseActivity;
import com.colin.go4lunch.utils.Authentication;

public class AuthActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.activity_auth;
    }

    @Override
    protected void configureActivity() {
        configureAuth();
    }

    private void configureAuth() {
        if (!Authentication.isCurrentUserLogged())
            startActivityForResult(Authentication.startSignInActivity(), Authentication.RC_SIGN_IN);
        else
            startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Authentication.onActivityResult(requestCode, resultCode, data, this);
    }

}
