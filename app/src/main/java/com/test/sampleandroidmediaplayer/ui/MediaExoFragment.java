package com.test.sampleandroidmediaplayer.ui;

import android.app.Fragment;
import android.media.MediaCodec;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.util.PlayerControl;
import com.test.sampleandroidmediaplayer.R;
import com.test.sampleandroidmediaplayer.events.BusProvider;
import com.test.sampleandroidmediaplayer.media.HlsRendererBuilder;
import com.test.sampleandroidmediaplayer.media.IRendererBuilder;
import com.test.sampleandroidmediaplayer.media.VisualizerAudioTrackRenderer;
import com.test.sampleandroidmediaplayer.models.Episode;
import com.test.sampleandroidmediaplayer.models.MockEpisode;
import com.test.sampleandroidmediaplayer.ui.widget.StateFrameLayout;
import com.test.sampleandroidmediaplayer.ui.widget.VisualizerView;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MediaExoFragment extends Fragment implements
        ExoPlayer.Listener,
        MediaCodecAudioTrackRenderer.EventListener,
        MediaCodecVideoTrackRenderer.EventListener,
        VisualizerAudioTrackRenderer.OnAudioSessionIdCallBack,
        IRendererBuilder.IRendererBuilderCallback {
    
    private static final String TAG = MediaExoFragment.class.getSimpleName();

    @InjectView(R.id.state_frame_layout)
    StateFrameLayout stateFrameLayout;
    @InjectView(R.id.linearLayout)
    LinearLayout mLinearLayout;

    IRendererBuilder builder;

    private Episode episode;

    private MediaController mMediaController;

    private ExoPlayer player;

    private Handler mainHandler;

    private long playerPosition = 0L;

    private int audioSessionId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        episode = new MockEpisode();
        mMediaController = new MediaController(getActivity());
        mainHandler = new Handler(getActivity().getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);
        View view = inflater.inflate(R.layout.fragment_media_exo, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        // プレーヤーのセットアップ
        setupPlayer();

        // 画面タッチでコントローラー表示
        stateFrameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MediaExoFragment.this.toggleControlsVisibility();
                return false;
            }
        });
    }

    /**
     * プレイヤーをセットアップします。
     */
    private void setupPlayer() {
        // プレイヤーの初期化
        if(player != null) {
            playerPosition = player.getCurrentPosition();
            player.release();
        }
        player = ExoPlayer.Factory.newInstance(2, 1000, 5000);
        player.addListener(this);
        player.seekTo(playerPosition);

        // コントローラーの設定
        mMediaController.setMediaPlayer(new PlayerControl(player));
        mLinearLayout.removeView(mMediaController);
        mMediaController.setAnchorView(mLinearLayout);
        mMediaController.setEnabled(true);

        // レンダラー生成開始 -> レンダー生成が完了したらonRenderersが呼ばれる
        builder = getBuilder();
        builder.buildRenderers(this);
    }

    @Override
    public void onRenderers(MediaCodecVideoTrackRenderer videoRenderer, MediaCodecAudioTrackRenderer audioRenderer) {
        Log.d(TAG, "onRenderers called.");
        player.prepare(videoRenderer,audioRenderer);
    }

    @Override
    public void onRenderersError(Exception e) {
        // レンダラーの生成に失敗
        Log.d(TAG, "IRendererBuilder.onRenderersError called.");
        e.printStackTrace();
    }

    /**
     * コントローラーの表示/非表示を切り替え
     */
    private void toggleControlsVisibility() {
        if(mMediaController.isShowing()) {
            mMediaController.hide();
        }else {
            mMediaController.show(0);
        }
    }

    public Handler getMainHandler() {
        return this.mainHandler;
    }

    private IRendererBuilder getBuilder() {
        return new HlsRendererBuilder(this,"android",episode.getEnclosure().toString(), episode.getEpisodeId());
    }

    @Override
    public void onPause() {
        super.onPause();
        if(player != null) {
            playerPosition = player.getCurrentPosition();
            player.release();
            player = null;
        }
        releaseVisualizer();
    }

    @Override
    public void onDestroyView() {
        BusProvider.getInstance().unregister(this);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.d(TAG, "onPlayerStateChanged called. playWhenReady=" + playWhenReady + ", playbackState=" + playbackState);
        if(playbackState == ExoPlayer.STATE_READY && mVisualizer != null) {
            // seekしたらなぜかVisualizerがうまく機能しなくなるので再設定する。
            Log.d(TAG, "visualizer reset.");
            mVisualizer.release();
            setupAudioVisualizer(audioSessionId);
        }
    }

    @Override
    public void onPlayWhenReadyCommitted() {
        Log.d(TAG, "onPlayWhenReadyCommitted called.");
        // Do nothing
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.e(TAG, "onPlayerError called. e=" + error.toString());
        error.printStackTrace();
        Toast.makeText(getActivity(),"プレイヤーエラーが発生しました。再構築します。",Toast.LENGTH_SHORT).show();
        releaseVisualizer();
        setupPlayer();
    }

    @Override
    public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
        Log.e(TAG, "onAudioTrackInitializationError called. e=" + e.toString());
        e.printStackTrace();
    }

    @Override
    public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {
        Log.e(TAG, "onDecoderInitializationError called. e=" + e.toString());
        e.printStackTrace();
    }

    @Override
    public void onCryptoError(MediaCodec.CryptoException e) {
        Log.e(TAG, "onCryptoError called.e=" + e.toString());
        e.printStackTrace();
    }

    @Override
    public void onDroppedFrames(int count, long elapsed) {
        Log.d(TAG, "onDroppedFrames called.");
    }

    @Override
    public void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
        Log.d(TAG, "onVideoSizeChanged called.");
        // Do nothing
    }

    @Override
    public void onDrawnToSurface(Surface surface) {
        Log.d(TAG, "onDrawnToSurface called.");
        // Do nothing
    }


    private Visualizer mVisualizer;
    @InjectView(R.id.visualizer_view)
    public VisualizerView mVisualizerView;
//    private final float VISUALIZER_HEIGHT_DIP = 200f;

    /**
     * ビジュアライズ用音声データをキャプチャするリスナー
     */
    private final Visualizer.OnDataCaptureListener dataCaptureListener = new Visualizer.OnDataCaptureListener() {
        @Override
        public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
            if(mVisualizerView != null) {
                mVisualizerView.updateVisualizer(waveform);
            }
        }

        @Override
        public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {/* do nothing */}
    };

    /**
     * 音声のビジュアライズ機能をセットする。
     *
     * @param audioSessionId
     */
    public void setupAudioVisualizer(int audioSessionId) {
        // VisualizerViewの設定 → xmlで定義することに変更したためコメントアウト
//        if(mVisualizerView == null) {
//            mVisualizerView = new VisualizerView(getActivity());
//            mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
//            mLinearLayout.addView(mVisualizerView);
//        }

        // Visualizerの設定
        mVisualizer = new Visualizer(audioSessionId);
        mVisualizer.setEnabled(false);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                dataCaptureListener ,
                Visualizer.getMaxCaptureRate() / 2,
                true ,
                false);
        mVisualizer.setEnabled(true);
    }

    /**
     * 音声のビジュアライズに用いるAudioSessionIdが決定した時に呼び出される。<br/>
     * audioSessionIdを元に{@link android.media.audiofx.Visualizer}を初期化する。<br/>
     *
     * @param audioSessionId
     */
    @Override
    public void onAudioSessionId(final int audioSessionId) {
        Log.d(TAG, "onAudioSessionId called.");
        this.audioSessionId = audioSessionId;
        setupAudioVisualizer(this.audioSessionId);
    }

    /**
     * VisualserとVisualizerViewを開放する。<br/>
     * これは画面がバックグラウンドに移行し処理を中断するたときや、
     * 何らかの障害が発生した場合に再構築するために行う必要がある。
     */
    private void releaseVisualizer() {
        if(mVisualizer != null) {
            mVisualizer.release();
            mVisualizer = null;
        }
    }
}