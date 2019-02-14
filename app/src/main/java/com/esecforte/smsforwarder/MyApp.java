package com.esecforte.smsforwarder;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class MyApp extends Application {
    private static MyApp instance;

    public static Context getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        Fabric.with(this, new Crashlytics());
    }
}
