package com.test.sampleandroidmediaplayer.models;

import com.test.sampleandroidmediaplayer.utils.DateUtils;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Date;

public class Episode implements Parcelable {

    private static final String TAG = Episode.class.getSimpleName();

    private String id;
    private String title;
    private String description;
    private Uri link;
    private Date postedAt;
    private Uri enclosure;
    private String showNotes;
    private boolean isFavorited;
    private boolean hasPlayed;
    private String mediaLocalPath;

    public String getEpisodeId() {
        return id;
    }

    public void setEpisodeId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Uri getLink() {
        return link;
    }

    public void setLink(Uri link) {
        this.link = link;
    }

    public Date getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(Date postedAt) {
        this.postedAt = postedAt;
    }

    public String getPostedAtAsString() {
        return DateUtils.dateToString(postedAt);
    }

    public Uri getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(Uri enclosure) {
        this.enclosure = enclosure;
    }

    public String getShowNotes() {
        return showNotes;
    }

    public void setShowNotes(String showNotes) {
        this.showNotes = showNotes;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public void setIsFavorited(boolean isFavorited) {
        this.isFavorited = isFavorited;
    }

    public boolean hasPlayed() {
        return hasPlayed;
    }

    public void setHasPlayed(boolean hasPlayed) {
        this.hasPlayed = hasPlayed;
    }

    public String getMediaLocalPath() {
        return mediaLocalPath;
    }

    public void setMediaLocalPath(String mediaLocalPath) {
        this.mediaLocalPath = mediaLocalPath;
    }

    public void play() {
        hasPlayed = true;
    }

    public Episode() {
        super();
    }

    public boolean isSameEpisode(Episode episode) {
        if (TextUtils.isEmpty(title) || episode == null) {
            return false;
        }

        return (title.equals(episode.getTitle()));
    }

    public Episode(String id, String title, String description, Uri link, String pubDate,
            Uri enclosure, String showNotes) {
        super();
        this.id = id;
        this.title = title;
        this.description = description;
        this.link = link;
        this.postedAt = DateUtils.pubDateToDate(pubDate);
        this.enclosure = enclosure;
        this.showNotes = showNotes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(link.toString());
        dest.writeLong(postedAt.getTime());
        dest.writeString(enclosure == null ? "" : enclosure.toString());
        dest.writeString(showNotes);
        dest.writeInt(isFavorited ? 1 : 0);
        dest.writeInt(hasPlayed ? 1 : 0);
        dest.writeString(mediaLocalPath);
    }

    public static final Creator<Episode> CREATOR = new Creator<Episode>() {
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };

    private Episode(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        link = Uri.parse(in.readString());
        postedAt = new Date(in.readLong());
        String uriString = in.readString();
        enclosure = (TextUtils.isEmpty(uriString) ? null : Uri.parse(uriString));
        showNotes = in.readString();
        isFavorited = (in.readInt() == 1);
        hasPlayed = (in.readInt() == 1);
        mediaLocalPath = in.readString();
    }
}
