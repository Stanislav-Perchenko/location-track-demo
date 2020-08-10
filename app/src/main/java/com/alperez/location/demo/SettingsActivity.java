package com.alperez.location.demo;

import androidx.annotation.Nullable;

import com.alperez.location.demo.base.BaseToolbarActivity;
import com.alperez.location.demo.base.Layout;

/**
 * Created by stanislav.perchenko on 10.08.2020 at 19:06.
 */
@Layout(R.layout.activity_settings)
public class SettingsActivity extends BaseToolbarActivity {


    @Nullable
    @Override
    protected Integer getToolbarResId() {
        return null;
    }

    @Nullable
    @Override
    protected String getScreenTitle() {
        return null;
    }

    @Nullable
    @Override
    protected String getScreenSubTitle() {
        return null;
    }
}
