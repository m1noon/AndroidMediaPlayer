package com.test.sampleandroidmediaplayer;


import com.test.sampleandroidmediaplayer.notifications.PodcastPlayerNotification;

import android.app.Application;

public class MainApplication extends Application {

    private static MainApplication instance;

    public MainApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupActivityLifecycleObserver();
    }

    @Override
    public void onTerminate() {
        ActivityLifecycleObserver.terminate(this);
        super.onTerminate();
    }

    private void setupActivityLifecycleObserver() {
        ActivityLifecycleObserver
                .initialize(this, new ActivityLifecycleObserver.OnActivityStoppedListener() {
                    @Override
                    public void onAllStop() {
                        PodcastPlayerNotification.setIsInBackground(true);
                    }
                });
    }

    public static MainApplication getInstance() {
        return instance;
    }
}
