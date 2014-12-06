package com.test.sampleandroidmediaplayer.services;

import com.test.sampleandroidmediaplayer.events.BusProvider;
import com.test.sampleandroidmediaplayer.notifications.PodcastPlayerNotification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PodcastPlayerService extends Service {

    @Override
    public void onCreate() {
        BusProvider.getInstance().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PodcastPlayerNotification.handleAction(this, intent.getAction());
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
    }
}
