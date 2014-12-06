package com.test.sampleandroidmediaplayer.notifications;

import com.test.sampleandroidmediaplayer.ui.MainActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public final class NotificationDelegate {

    public static PendingIntent getLauchEpisodeDetailIntent(Context context, String episodeId) {
        return PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private NotificationDelegate() {
    }
}
