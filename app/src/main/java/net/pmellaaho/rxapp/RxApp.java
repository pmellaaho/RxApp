package net.pmellaaho.rxapp;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import net.pmellaaho.rxapp.network.DaggerNetworkComponent;
import net.pmellaaho.rxapp.network.NetworkComponent;

import timber.log.Timber;

public class RxApp extends Application {

    private NetworkComponent mComponent = null;

    private RefWatcher mRefWatcher;
    public static RefWatcher getRefWatcher() {
        return RxApp.get().mRefWatcher;

    }
    private static RxApp sInstance;
    public static RxApp get() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (mComponent == null) {
            mComponent = DaggerNetworkComponent.create();
        }

        sInstance = (RxApp) getApplicationContext();
        mRefWatcher = LeakCanary.install(this);
        Timber.plant(new Timber.DebugTree());
    }

    public NetworkComponent component() {
        return mComponent;
    }

    // This allows providing mock NetworkComponent from test
    public void setComponent(NetworkComponent component) {
        mComponent = component;
    }
}
