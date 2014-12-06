package com.test.sampleandroidmediaplayer.listener;

import android.content.Context;
import android.widget.SeekBar;

import com.test.sampleandroidmediaplayer.media.PodcastPlayer;


public class OnPlayerSeekListener implements SeekBar.OnSeekBarChangeListener {


    public static boolean seeking = false;

    public static Object LOCK = new Object();


    Context mContext;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        synchronized (LOCK) {
            seeking = true;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (!PodcastPlayer.getInstance().isPlaying()) {
            return;
        }
//        PodcastPlayer.getInstance().pause();
        // ここでPlayerの再生バッファを消したい。
//        AudioManager audioManager = (AudioManager)mContext.getSystemService(mContext.AUDIO_SERVICE);

//        PodcastPlayer.getInstance().pause();
        PodcastPlayer.getInstance().seekTo(seekBar.getProgress());
    }

    public static void seekFinished() {
        synchronized (LOCK) {
            seeking = false;
        }
    }
}
