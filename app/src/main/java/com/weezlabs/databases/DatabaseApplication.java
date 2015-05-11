package com.weezlabs.databases;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Andrey Bondarenko on 11.05.15.
 */
public class DatabaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
