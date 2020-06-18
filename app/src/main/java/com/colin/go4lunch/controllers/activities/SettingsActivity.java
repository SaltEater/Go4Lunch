package com.colin.go4lunch.controllers.activities;

import androidx.appcompat.widget.Toolbar;
import com.colin.go4lunch.R;
import com.colin.go4lunch.controllers.bases.BaseActivity;
import com.colin.go4lunch.controllers.fragments.SettingsFragment;
import butterknife.BindView;

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.activity_setting_toolbar_include)
    Toolbar mToolbar;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected int getActivityLayout() {
        return R.layout.settings_activity;
    }

    @Override
    protected void configureActivity() {
        this.configureToolbar();
        updateActivity();
    }

    protected void updateActivity() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_settings_fragment_container, SettingsFragment.newInstance())
                .commit();
    }

    private void configureToolbar() {
        mToolbar.setTitle(getString(R.string.title_activity_settings));
        setSupportActionBar(mToolbar);
    }

}