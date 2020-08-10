package com.alperez.location.demo.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.alperez.location.demo.DemoApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by stanislav.perchenko on 11/11/2019, 11:49 AM.
 */
public class SettingsHolder {

    private static SettingsHolder INSTANCE;

    public static SettingsHolder getInstance() {
        if (INSTANCE == null) {
            synchronized (SettingsHolder.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SettingsHolder(DemoApplication.getStaticAppContext());
                }
            }
        }
        return INSTANCE;
    }


    private static final String PREF_HAS_SETTINGS = "has_settings";
    private static final String PREF_USE_LOCATION_TRACKING = "use_location_tracking";
    private static final String PREF_USE_FAIL_NET = "use_fail_networking";

    private final SharedPreferences sPref;
    private Settings mSettings;

    private SettingsHolder(Context ctx) {
        sPref = ctx.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        boolean hasSettings = sPref.getBoolean(PREF_HAS_SETTINGS, false);
        if (hasSettings) {
            mSettings = Settings.builder()
                    .setUseLocationTracking(sPref.getBoolean(PREF_USE_LOCATION_TRACKING, true))
                    .setUseFailNetworking(sPref.getBoolean(PREF_USE_FAIL_NET, false))
                    .build();
        } else {
            mSettings = Settings.builder()
                    .setUseLocationTracking(true)
                    .setUseFailNetworking(false)
                    .build();
            saveSettings();
        }
    }

    public Settings getSettings() {
        return mSettings;
    }

    public LiveData<Settings> getSettingsLiveData() {
        return new SettingsLiveData();
    }

    public void setSettings(@NonNull Settings settings) {
        assert (settings != null);
        mSettings = settings;
        saveSettings();

        List<OnSettingsUpdateListener> obss;
        synchronized (mObservers) {
            obss = new ArrayList<>(mObservers.size());
            obss.addAll(mObservers);
        }

        for (OnSettingsUpdateListener obs : obss)  obs.onUpdated(mSettings);
    }

    private void saveSettings() {
        sPref.edit()
                .putBoolean(PREF_HAS_SETTINGS, true)
                .putBoolean(PREF_USE_LOCATION_TRACKING, mSettings.useLocationTracking())
                .putBoolean(PREF_USE_FAIL_NET, mSettings.useFailNetworking())
                .apply();
    }

    /**********************************************************************************************/
    /*****************************  Data  update observer  ****************************************/
    /**********************************************************************************************/

    public interface OnSettingsUpdateListener {
        void onUpdated(Settings settings);
    }

    private final Set<OnSettingsUpdateListener> mObservers = new HashSet<>();

    void requestUpdates(OnSettingsUpdateListener l) {
        boolean upd;
        synchronized (mObservers) {
            upd = mObservers.add(l);
        }
        if (upd) {
            l.onUpdated(mSettings);
        }
    }

    void removeUpdates(OnSettingsUpdateListener l) {
        synchronized (mObservers) {
            mObservers.remove(l);
        }
    }

}
