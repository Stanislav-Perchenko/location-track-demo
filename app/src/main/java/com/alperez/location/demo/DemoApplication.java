package com.alperez.location.demo;

import android.app.Application;
import android.content.Context;

/**
 * Created by stanislav.perchenko on 10.08.2020 at 19:49.
 */
public class DemoApplication extends Application {
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        DemoApplication.appContext = this;
    }

    public static Context getStaticAppContext() {
        return appContext;
    }

}
