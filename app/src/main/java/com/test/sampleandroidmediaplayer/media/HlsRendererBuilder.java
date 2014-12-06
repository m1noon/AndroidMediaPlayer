package com.test.sampleandroidmediaplayer.media;

import android.media.MediaCodec;
import android.util.Log;

import com.google.android.exoplayer.MediaCodecUtil;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.hls.HlsChunkSource;
import com.google.android.exoplayer.hls.HlsPlaylist;
import com.google.android.exoplayer.hls.HlsPlaylistParser;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.upstream.UriDataSource;
import com.google.android.exoplayer.util.ManifestFetcher;
import com.google.android.exoplayer.util.MimeTypes;
import com.test.sampleandroidmediaplayer.ui.MediaExoFragment;

import java.io.IOException;

/**
 * HLS用Rendererの生成を行うクラス。<br/>
 *
 * Created by mino-hiroki on 2014/12/02.
 */
public class HlsRendererBuilder implements IRendererBuilder, ManifestFetcher.ManifestCallback<HlsPlaylist>{
    private static final String TAG = HlsRendererBuilder.class.getSimpleName();

    private IRendererBuilderCallback mCallback;

    private final MediaExoFragment fragment;
    private final String userAgent;
    private final String url;
    private final String contentId;

    // HlsChunkSourceのバッファー設定
    private final long TARGET_BUFFER_DURATION_MS = 15000;
    private final long MIN_BUFFER_DURATION_TO_SWITCH_UP_MS = 5000;
    private final long MAX_BUFFER_DURATION_TO_SWITCH_DOWN_MS = 7500;


    public HlsRendererBuilder(MediaExoFragment fragment, String userAgent, String url, String contentId) {
        this.fragment = fragment;
        this.userAgent = userAgent;
        this.url = url;
        this.contentId = contentId;
    }

    /**
     * レンダラーを生成します。<br/>
     *
     * @param callback
     */
    @Override
    public void buildRenderers(IRendererBuilderCallback callback) {
        this.mCallback = callback;
        HlsPlaylistParser parser = new HlsPlaylistParser();
        ManifestFetcher<HlsPlaylist> playlistFetcher =
                new ManifestFetcher<HlsPlaylist>(parser, contentId, url, userAgent);
        // 第二引数にロードの完了後コールバック関数として自身を設定 → onManifest(String,HlsPlayList)が呼ばれる。
        playlistFetcher.singleLoad(fragment.getActivity().getMainLooper(), this);
    }

    @Override
    public void onManifest(String contentId, HlsPlaylist manifest) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        DataSource dataSource = new UriDataSource(userAgent, bandwidthMeter);
        boolean adaptiveDecoder = MediaCodecUtil.getDecoderInfo(MimeTypes.VIDEO_H264, false).adaptive;
        Log.d(TAG, "adaptiveDecoder = " + adaptiveDecoder);
        HlsChunkSource chunkSource = new HlsChunkSource(dataSource, url, manifest, bandwidthMeter, null,
                adaptiveDecoder ? HlsChunkSource.ADAPTIVE_MODE_SPLICE : HlsChunkSource.ADAPTIVE_MODE_NONE,
                TARGET_BUFFER_DURATION_MS, MIN_BUFFER_DURATION_TO_SWITCH_UP_MS, MAX_BUFFER_DURATION_TO_SWITCH_DOWN_MS
                );
        HlsSampleSource sampleSource = new HlsSampleSource(chunkSource, true, 2);
        MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(sampleSource,
                MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT, 0, fragment.getMainHandler(),
                fragment, 50);
//        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,fragment.getMainHandler(), fragment);
        VisualizerAudioTrackRenderer audioRenderer = new VisualizerAudioTrackRenderer(sampleSource,fragment.getMainHandler(), fragment);
        audioRenderer.setOnAudioSessionIdCallBack(fragment);
        
        mCallback.onRenderers(videoRenderer, audioRenderer);
    }

    @Override
    public void onManifestError(String contentId, IOException e) {
        mCallback.onRenderersError(e);
    }
}