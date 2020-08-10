package com.alperez.location.demo.settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.alperez.location.demo.R;
import com.alperez.location.demo.base.BaseToolbarActivity;
import com.alperez.location.demo.base.Layout;
import com.alperez.utils.Toaster;
import com.google.android.material.snackbar.Snackbar;

/**
 * Created by stanislav.perchenko on 10.08.2020 at 19:06.
 */
@Layout(R.layout.activity_settings)
public class SettingsActivity extends BaseToolbarActivity {
    public static final int REQUEST_PERMISSION_LOCATION = 101;

    private Switch swUseLocationTracking;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        swUseLocationTracking = findViewById(R.id.sw_use_location_tracking);

        swUseLocationTracking.setChecked(SettingsHolder.getInstance().getSettings().useLocationTracking());

        swUseLocationTracking.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                setUseLocationTracking();
            } else {
                clearUseLocationTracking();
            }
        });


    }

    @Nullable
    @Override
    protected Integer getToolbarResId() {
        return R.id.toolbar;
    }

    @Nullable
    @Override
    protected String getScreenTitle() {
        return getString(R.string.screen_title_settings);
    }

    @Nullable
    @Override
    protected String getScreenSubTitle() {
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (locationPermissionSnack != null) {
            locationPermissionSnack.dismiss();
            locationPermissionSnack = null;
            swUseLocationTracking.setChecked(false);
        } else {
            super.onBackPressed();
        }
    }

    private void clearUseLocationTracking() {
        Settings s = SettingsHolder.getInstance().getSettings();
        SettingsHolder.getInstance().setSettings(s.withUseLocationTracking(false));
    }

    private Snackbar locationPermissionSnack;

    private void setUseLocationTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Settings s = SettingsHolder.getInstance().getSettings();
            SettingsHolder.getInstance().setSettings(s.withUseLocationTracking(true));
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            locationPermissionSnack = Toaster.showSnackbar(findViewById(R.id.activity_content), R.string.location_permission_rationale, android.R.string.ok, (v) -> {
                startLocationPermissionRequest();
                locationPermissionSnack = null;
            });
        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                setUseLocationTracking();
            } else {
                swUseLocationTracking.setChecked(false);
                Toaster.toastToUser(R.string.msg_location_permission_not_granted, Toast.LENGTH_LONG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
