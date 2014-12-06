package com.test.sampleandroidmediaplayer.media;

import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;

/**
 * Rendererの生成クラスが実装するインターフェースの定義
 *
 * Created by mino-hiroki on 2014/12/02.
 */
public interface IRendererBuilder {

    void buildRenderers(IRendererBuilderCallback callback);


    public static interface IRendererBuilderCallback {
        public void onRenderers(MediaCodecVideoTrackRenderer videoRenderer,
                                MediaCodecAudioTrackRenderer audioRenderer);

        public void onRenderersError(Exception e);
    }
}
