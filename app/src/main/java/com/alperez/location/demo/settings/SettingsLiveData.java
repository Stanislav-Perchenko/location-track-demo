package com.alperez.location.demo.settings;

import android.os.Looper;

import androidx.lifecycle.MutableLiveData;

/**
 * Created by stanislav.perchenko on 11/11/2019, 12:13 PM.
 */
class SettingsLiveData extends MutableLiveData<Settings> {
    private final SettingsHolder.OnSettingsUpdateListener listener = new SettingsHolder.OnSettingsUpdateListener() {
        private Settings settings;

        @Override
        public void onUpdated(Settings settings) {
            boolean needUpd;
            synchronized (this) {
                needUpd = !settings.equals(this.settings);
                this.settings = settings;
            }

            if (needUpd && (Thread.currentThread() == Looper.getMainLooper().getThread())) {
                SettingsLiveData.this.setValue(settings);
            } else if (needUpd) {
                SettingsLiveData.this.postValue(settings);
            }
        }
    };

    @Override
    protected void onActive() {
        SettingsHolder.getInstance().requestUpdates(listener);
    }

    @Override
    protected void onInactive() {
        SettingsHolder.getInstance().removeUpdates(listener);
    }
}
