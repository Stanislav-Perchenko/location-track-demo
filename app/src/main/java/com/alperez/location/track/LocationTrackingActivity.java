package com.alperez.location.track;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;

import com.alperez.location.demo.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.snackbar.Snackbar;

/**
 * Created by stanislav.perchenko on 11/13/2019, 1:14 PM.
 */
public abstract class LocationTrackingActivity extends AppCompatActivity {
    public static final int REQUEST_PERMISSION_LOCATION = 1101;
    public static final int REQUEST_CHECK_SETTINGS = 1102;

    private Location mLocation;
    private boolean needLocationTracking;
    private boolean locationTracking;

    protected abstract LiveData<? extends IActivateLocationSetting> getActivateSetting();
    protected abstract String getLocationPermissionRationaleText();
    protected abstract LocationRequest getLocationRequest();
    protected abstract void onStartTrackingLocation();
    protected abstract void onStopTrackingLocation();
    protected abstract void onLocationUpdated(Location location);
    protected abstract View getSnackbarContainerView();

    private LiveData<? extends IActivateLocationSetting> settLiveData;
    private boolean isActivityResumed;
    private Snackbar locationPermissionSnack;


    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for Location events.
     */
    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            onLocationUpdated(mLocation = locationResult.getLastLocation());
            //TODO updateLocationUI();
        }
    };

    @Nullable
    protected Location getCurrentLocation() {
        return mLocation;
    }

    protected boolean isNeedLocationTracking() {
        return needLocationTracking;
    }

    protected boolean isLocationTracking() {
        return locationTracking;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationRequest = getLocationRequest();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        settLiveData = getActivateSetting();
        settLiveData.observe(this, sett -> {
            needLocationTracking = sett.useLocationTracking();
            if (isActivityResumed && needLocationTracking) {
                startTrackingLocationCheckPerm();
            } else if (isActivityResumed && !needLocationTracking) {
                stopTrackingLocation();
            }
        });
    }


    @Override
    protected void onResume() {
        isActivityResumed = true;
        super.onResume();
        if (needLocationTracking) {
            startTrackingLocationCheckPerm();
        }
    }

    @Override
    protected void onPause() {
        isActivityResumed = false;
        super.onPause();
        stopTrackingLocation();
    }

    @Override
    public void onBackPressed() {
        if (locationPermissionSnack != null) {
            locationPermissionSnack.dismiss();
            locationPermissionSnack = null;
        } else {
            super.onBackPressed();
        }
    }

    private void startTrackingLocationCheckPerm() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startTrackingLocationWithPermission();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            locationPermissionSnack = showSnackbar(getSnackbarContainerView(), getLocationPermissionRationaleText(), android.R.string.ok, (v) -> {
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

    public static Snackbar showSnackbar(View container, final String mainTextString, final int actionStringId, View.OnClickListener listener) {
        Context ctx = container.getContext();
        Snackbar sb = Snackbar.make(container, mainTextString, Snackbar.LENGTH_INDEFINITE)
                .setAction(ctx.getString(actionStringId), listener);
        sb.show();
        return sb;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            // Do nothing here. The onResume() will do the job.
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @SuppressLint("MissingPermission")
    private void startTrackingLocationWithPermission() {
        if (locationTracking) return;

        LocationSettingsRequest lsr = (new LocationSettingsRequest.Builder())
                .addLocationRequest(mLocationRequest)
                .build();


        // Begin by checking if the device has the necessary location settings.
        LocationServices.getSettingsClient(this).checkLocationSettings(lsr)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    //noinspection MissingPermission
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    locationTracking = true;
                    onStartTrackingLocation();
                    //TODO updateUI();
                })
                .addOnFailureListener(this, e -> {
                    int s_code = ((ApiException) e).getStatusCode();
                    if ((s_code == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) && (e instanceof ResolvableApiException)) {
                        try {
                            ((ResolvableApiException) e).startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException ex) {
                            // Ignore
                        }
                    } else if (s_code == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                        Toast.makeText(this, R.string.err_location_settings_unresolvable, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void stopTrackingLocation() {
        if (locationTracking) return;

        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, task -> {
                    locationTracking = false;
                    onStopTrackingLocation();
                });
    }
}
