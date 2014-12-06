package com.test.sampleandroidmediaplayer.events;

public class MediaPlayStartEvent {
    private String episodeId;

    public String getEpisodeId() {
        return episodeId;
    }

    public MediaPlayStartEvent(String episodeId) {
        this.episodeId = episodeId;
    }
}
