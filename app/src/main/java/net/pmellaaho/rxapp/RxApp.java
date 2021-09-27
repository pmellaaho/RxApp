package net.pmellaaho.rxapp;

import android.app.Application;

import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

@HiltAndroidApp
public class RxApp extends Application {

    private static RxApp sInstance;
    public static RxApp get() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = (RxApp) getApplicationContext();
        Timber.plant(new Timber.DebugTree());
    }
}
