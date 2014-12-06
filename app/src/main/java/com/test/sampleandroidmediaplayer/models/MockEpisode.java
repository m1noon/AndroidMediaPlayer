package com.test.sampleandroidmediaplayer.models;

import android.net.Uri;

public class MockEpisode extends Episode {

    private static final String TAG = MockEpisode.class.getSimpleName();

    private static String id = "hoge";
    private static String title = "サンプルタイトル";
    private static String description = "説明文";
    private static Uri link = Uri.parse("http://hoge.com");
    private static Uri enclosure = Uri.parse(
            "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8");
    private static String pubDate = "Sat, 08 Nov 2014 00:00:00 -0800";
    private static String duration = "00:00";

    public MockEpisode() {
        super(
                id,
                title,
                description,
                link,
                pubDate,
                enclosure,
                null
        );
    }
}
