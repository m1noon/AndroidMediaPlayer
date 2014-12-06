package com.test.sampleandroidmediaplayer.ui;

import android.media.MediaCodec;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;

import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.audio.AudioTrack;
import com.test.sampleandroidmediaplayer.R;


public class MainActivity extends ActionBarActivity implements MediaCodecAudioTrackRenderer.EventListener,MediaCodecVideoTrackRenderer.EventListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        MediaExoFragment mediaFragment = (MediaExoFragment) getFragmentManager()
//                .findFragmentById(R.id.fragment_media);
//        mediaFragment.setup(new MockEpisode());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
        Log.e(TAG, "onAudioTrackInitializationError caleld.");
    }

    @Override
    public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {
        Log.e(TAG, "onDecoderInitializationError called.");
    }

    @Override
    public void onCryptoError(MediaCodec.CryptoException e) {
        Log.e(TAG, "onCryptoError called.");
    }

    @Override
    public void onDroppedFrames(int count, long elapsed) {
        Log.d(TAG, "onDroppedFrames called.");
    }

    @Override
    public void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
        Log.d(TAG, "onVideoSizeChanged called.");
    }

    @Override
    public void onDrawnToSurface(Surface surface) {
        Log.d(TAG, "onDrawnToSurface called.");
    }
}
