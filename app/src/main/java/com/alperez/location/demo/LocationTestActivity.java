package com.alperez.location.demo;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alperez.location.demo.settings.SettingsHolder;
import com.alperez.location.track.IActivateLocationSetting;
import com.alperez.location.track.LocationTrackingActivity;
import com.google.android.gms.location.LocationRequest;

import java.text.DateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;

/**
 * Created by stanislav.perchenko on 10.08.2020 at 19:58.
 */
public class LocationTestActivity extends LocationTrackingActivity {

    private TextView vTxtState;
    private TextView vTxtProvider;
    private TextView vTxtLat;
    private TextView vTxtLon;
    private TextView vTxtAccur;
    private TextView vTxtTime;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_location);
        vTxtState = findViewById(R.id.txt_track_state);
        vTxtProvider = findViewById(R.id.txt_provider);
        vTxtLat = findViewById(R.id.txt_lat);
        vTxtLon = findViewById(R.id.txt_lon);
        vTxtAccur = findViewById(R.id.txt_accur);
        vTxtTime = findViewById(R.id.txt_time);
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar vTb = findViewById(R.id.toolbar);
        vTb.setTitleTextColor(getColor(R.color.text_white));
        setSupportActionBar(vTb);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Location tracking");
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected LiveData<? extends IActivateLocationSetting> getActivateSetting() {
        return SettingsHolder.getInstance().getSettingsLiveData();
    }

    @Override
    protected String getLocationPermissionRationaleText() {
        return getString(R.string.location_permission_rationale);
    }

    @Override
    protected LocationRequest getLocationRequest() {
        LocationRequest lr = new LocationRequest();
        lr.setInterval(10_000);
        lr.setFastestInterval(5_000);
        lr.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return lr;
    }

    @Override
    protected void onStartTrackingLocation() {
        vTxtState.setText("Tracking");
        vTxtState.setTextColor(getColor(R.color.app_color_green));
    }

    @Override
    protected void onStopTrackingLocation() {
        vTxtState.setText("Not tracking");
        vTxtState.setTextColor(getColor(R.color.text_light_gray));
    }

    @Override
    protected void onLocationUpdated(Location location) {
        vTxtProvider.setText("Location provider - "+location.getProvider());
        vTxtLat.setText(String.format("latitude = %.5f", location.getLatitude()));
        vTxtLon.setText(String.format("longitude = %.5f", location.getLongitude()));
        vTxtAccur.setText(String.format("accuracy = %.1f", location.getAccuracy()));
        vTxtTime.setText(DateFormat.getTimeInstance().format(new Date()));
    }

    @Override
    protected View getSnackbarContainerView() {
        return findViewById(R.id.activity_content);
    }
}
