package com.test.sampleandroidmediaplayer.media;

import android.os.Handler;
import android.util.Log;

import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.drm.DrmSessionManager;

/**
 * {@link android.media.audiofx.Visualizer}との連携のため、AudioSessionIdを取得する機能を追加した音声用TrackRendererクラス。
 *
 * Created by mino-hiroki on 2014/12/05.
 */
public class VisualizerAudioTrackRenderer extends MediaCodecAudioTrackRenderer {
    private static final String TAG = VisualizerAudioTrackRenderer.class.getSimpleName();

    public VisualizerAudioTrackRenderer(SampleSource source) {
        super(source);
    }

    public VisualizerAudioTrackRenderer(SampleSource source, DrmSessionManager drmSessionManager, boolean playClearSamplesWithoutKeys) {
        super(source, drmSessionManager, playClearSamplesWithoutKeys);
    }

    public VisualizerAudioTrackRenderer(SampleSource source, Handler eventHandler, EventListener eventListener) {
        super(source, eventHandler, eventListener);
    }

    public VisualizerAudioTrackRenderer(SampleSource source, DrmSessionManager drmSessionManager, boolean playClearSamplesWithoutKeys, Handler eventHandler, EventListener eventListener) {
        super(source, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener);
    }

    public VisualizerAudioTrackRenderer(SampleSource source, float minBufferMultiplicationFactor, Handler eventHandler, EventListener eventListener) {
        super(source, minBufferMultiplicationFactor, eventHandler, eventListener);
    }

    public VisualizerAudioTrackRenderer(SampleSource source, DrmSessionManager drmSessionManager, boolean playClearSamplesWithoutKeys, float minBufferMultiplicationFactor, Handler eventHandler, EventListener eventListener) {
        super(source, drmSessionManager, playClearSamplesWithoutKeys, minBufferMultiplicationFactor, eventHandler, eventListener);
    }

    public VisualizerAudioTrackRenderer(SampleSource source, DrmSessionManager drmSessionManager, boolean playClearSamplesWithoutKeys, Handler eventHandler, EventListener eventListener, AudioTrack audioTrack) {
        super(source, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener, audioTrack);
    }

    private OnAudioSessionIdCallBack onAudioSessionIdCallBack;

    public void setOnAudioSessionIdCallBack(OnAudioSessionIdCallBack callback) {
        this.onAudioSessionIdCallBack = callback;
    }

    @Override
    protected void onAudioSessionId(final int audioSessionId) {
        Log.d(TAG, "onAudioSessionId called. audioSessionId=" + audioSessionId);
        if(onAudioSessionIdCallBack != null) {
            eventHandler.post(
                    new Runnable() {
                        @Override
                        public void run() {
                            onAudioSessionIdCallBack.onAudioSessionId(audioSessionId);
                        }
                    }
            );
        }
    }

    public static interface OnAudioSessionIdCallBack {
        public void onAudioSessionId(final int audioSessionId);
    }
}
