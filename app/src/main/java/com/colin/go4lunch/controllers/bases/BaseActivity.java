package com.colin.go4lunch.controllers.bases;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getActivityLayout();
    protected abstract void configureActivity();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getActivityLayout());
        ButterKnife.bind(this);
        this.configureActivity();
    }

    @Nullable
    public FirebaseUser getCurrentUser() { return FirebaseAuth.getInstance().getCurrentUser(); }

    public Boolean isCurrentUserLogged() { return (this.getCurrentUser() != null); }
}
