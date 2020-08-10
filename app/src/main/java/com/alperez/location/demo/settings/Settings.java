package com.alperez.location.demo.settings;

import com.alperez.location.track.IActivateLocationSetting;
import com.google.auto.value.AutoValue;

/**
 * Created by stanislav.perchenko on 11/11/2019, 11:33 AM.
 */
@AutoValue
public abstract class Settings implements IActivateLocationSetting {

    public abstract boolean useLocationTracking();
    public abstract boolean useFailNetworking();


    abstract Builder toBuilder();

    public Settings withUseLocationTracking(boolean useLocationTracking) {
        return toBuilder().setUseLocationTracking(useLocationTracking).build();
    }
    public Settings withUseFailNetworking(boolean use) {
        return toBuilder().setUseFailNetworking(use).build();
    }


    public static Builder builder() {
        return new AutoValue_Settings.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setUseLocationTracking(boolean useLocationTracking);
        public abstract Builder setUseFailNetworking(boolean useFailNetworking);

        public abstract Settings build();
    }

}
