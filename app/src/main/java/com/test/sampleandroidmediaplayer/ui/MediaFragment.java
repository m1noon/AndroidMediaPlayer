package com.test.sampleandroidmediaplayer.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.test.sampleandroidmediaplayer.R;
import com.test.sampleandroidmediaplayer.events.BusProvider;
import com.test.sampleandroidmediaplayer.events.MediaPlayStartEvent;
import com.test.sampleandroidmediaplayer.events.ReceivePauseActionEvent;
import com.test.sampleandroidmediaplayer.events.ReceiveResumeActionEvent;
import com.test.sampleandroidmediaplayer.listener.LoadListener;
import com.test.sampleandroidmediaplayer.listener.OnPlayerSeekListener;
import com.test.sampleandroidmediaplayer.media.PodcastPlayer;
import com.test.sampleandroidmediaplayer.models.Episode;
import com.test.sampleandroidmediaplayer.notifications.PodcastPlayerNotification;
import com.test.sampleandroidmediaplayer.tools.OnContextExecutor;
import com.test.sampleandroidmediaplayer.ui.widget.StateFrameLayout;
import com.test.sampleandroidmediaplayer.utils.DateUtils;
import com.test.sampleandroidmediaplayer.utils.UiAnimations;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MediaFragment extends Fragment {
    
    private static final String TAG = MediaFragment.class.getSimpleName();

    @InjectView(R.id.episode_detail_header_cover)
    View mediaStartButtonOnImageCover;
    @InjectView(R.id.media_current_time)
    TextView mediaCurrentTimeTextView;
    @InjectView(R.id.media_duration)
    TextView mediaDurationTextView;
    @InjectView(R.id.media_play_and_pause_button)
    CheckBox mediaPlayAndPauseButton;
    @InjectView(R.id.media_seekbar)
    SeekBar seekBar;
    @InjectView(R.id.state_frame_layout)
    StateFrameLayout stateFrameLayout;

    private Episode episode;
    private LoadListener loadListener;
    private OnContextExecutor onContextExecutor = new OnContextExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);
        View view = inflater.inflate(R.layout.fragment_media, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        BusProvider.getInstance().unregister(this);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    public void setup(final Episode episode) {
        if (episode == null) {
            getActivity().finish();
            return;
        }

        this.episode = episode;

        loadListener = new LoadListener() {
            @Override
            public void showProgress() {
                stateFrameLayout.showProgress();
            }

            @Override
            public void showError() {
            }

            @Override
            public void showContent() {
                stateFrameLayout.showContent();
            }
        };

        setupMediaPlayAndPauseButton(episode);
        setupSeekBar(episode);
    }

    private void setupMediaPlayAndPauseButton(final Episode episode) {
        if (episode.getEnclosure() == null) {
            return;
        }

        PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
        if (podcastPlayer.isPlayingEpisode(episode)
                && (podcastPlayer.isPlaying() || podcastPlayer.isPaused())) {
            mediaStartButtonOnImageCover.setVisibility(View.GONE);

            if (podcastPlayer.isPlaying()) {
                mediaPlayAndPauseButton.setChecked(true);
            } else {
                mediaPlayAndPauseButton.setChecked(false);
            }
        } else {
            if (podcastPlayer.isPlaying()) {
                mediaStartButtonOnImageCover.setVisibility(View.VISIBLE);
                mediaStartButtonOnImageCover.setAlpha(1);
            }
        }

        mediaPlayAndPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayAndPauseButton.setChecked(!mediaPlayAndPauseButton.isChecked());

                if (!mediaPlayAndPauseButton.isChecked()) {
                    if (shouldRestart(episode)) {
                        restart(episode);
                        mediaPlayAndPauseButton.setChecked(!mediaPlayAndPauseButton.isChecked());
                    } else {
                        start(episode);
                        mediaPlayAndPauseButton.setChecked(!mediaPlayAndPauseButton.isChecked());
                    }
                } else {
                    pause(episode);
                }
            }
        });
    }

    private void start(final Episode episode) {
        if (shouldRestart(episode)) {
            restart(episode);
            return;
        }

        loadListener.showProgress();
        mediaPlayAndPauseButton.setEnabled(false);

        final PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
        podcastPlayer.start(getActivity(), episode, new PodcastPlayer.StateChangedListener() {
            @Override
            public void onStart() {
                if (getActivity() == null) {
                    pause(episode);
                } else {
                    loadListener.showContent();
                    UiAnimations.fadeOut(mediaStartButtonOnImageCover, 300, 1000);

                    seekBar.setEnabled(true);
                    mediaPlayAndPauseButton.setEnabled(true);
                    PodcastPlayerNotification.notify(getActivity(), episode);
                    BusProvider.getInstance()
                            .post(new MediaPlayStartEvent(episode.getEpisodeId()));
                }
            }
        });
    }

    private boolean shouldRestart(Episode episode) {
        PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
        return (podcastPlayer.isPlayingEpisode(episode)
                && (podcastPlayer.isPlaying() || podcastPlayer.isPaused()));
    }

    private void restart(Episode episode) {
        PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
        podcastPlayer.restart();
        seekBar.setEnabled(true);
        PodcastPlayerNotification.notify(getActivity(), episode);
    }

    private void pause(final Episode episode) {
        mediaPlayAndPauseButton.setChecked(!mediaPlayAndPauseButton.isChecked());

        final PodcastPlayer podcastPlayer = PodcastPlayer.getInstance();
        podcastPlayer.pause();
        seekBar.setEnabled(false);
        PodcastPlayerNotification.notify(
                getActivity(),
                episode,
                PodcastPlayer.getInstance().getCurrentPosition(),
                PodcastPlayer.getInstance().getDuration()
        );
    }

    private void setupSeekBar(final Episode episode) {
        mediaDurationTextView.setText(getResources().getString(R.string.default_duration));

        if (PodcastPlayer.getInstance().isPlaying()) {
            updateCurrentTime(PodcastPlayer.getInstance().getCurrentPosition());
        } else {
            updateCurrentTime(0);
        }

        seekBar.setOnSeekBarChangeListener(new OnPlayerSeekListener());
        seekBar.setMax(0);
        if (PodcastPlayer.getInstance().isPlayingEpisode(episode)) {
            seekBar.setEnabled(true);
        } else {
            seekBar.setEnabled(false);
        }

        PodcastPlayer.getInstance().setCurrentTimeListener(
                new PodcastPlayer.CurrentTimeListener() {
                    @Override
                    public void onTick(int currentPosition, int duration) {
                        if (PodcastPlayer.getInstance().isPlayingEpisode(episode)) {
                            Log.d(TAG, "onTick called. currentPosition=" + currentPosition + ", duration="+duration);

                            updateCurrentTime(currentPosition);
                            PodcastPlayerNotification
                                    .notify(getActivity(), episode, currentPosition, duration);

                            if (duration > 0 && mediaDurationTextView != null) {
                                mediaDurationTextView
                                        .setText(DateUtils.formatCurrentTime(duration));
                            }

                            if (seekBar != null) {
                                seekBar.setMax(duration);
                            }

                        } else {
                            updateCurrentTime(0);
                        }
                    }
                }
        );
    }

    private void updateCurrentTime(int currentPosition) {
        if (mediaCurrentTimeTextView == null || seekBar == null) {
            return;
        }
        mediaCurrentTimeTextView.setText(DateUtils.formatCurrentTime(currentPosition));
        seekBar.setProgress(currentPosition);
    }

    @Subscribe
    public void onReceivePauseAction(ReceivePauseActionEvent event) {
        onContextExecutor.execute(getActivity(), new Runnable() {
            @Override
            public void run() {
                mediaPlayAndPauseButton.setChecked(false);
            }
        });
    }

    @Subscribe
    public void onReceivePauseAction(ReceiveResumeActionEvent event) {
        onContextExecutor.execute(getActivity(), new Runnable() {
            @Override
            public void run() {
                mediaPlayAndPauseButton.setChecked(true);
            }
        });
    }


}
