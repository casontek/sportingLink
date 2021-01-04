package com.mycornership.betlink.models;

import com.google.gson.annotations.SerializedName;

public class Video {
    @SerializedName("id")
    private long id;
    @SerializedName("title")
    private String title;
    @SerializedName("videoUrl")
    private String videoUrl;
    @SerializedName("category")
    private String category;
    @SerializedName("createdDate")
    private String createdDate;
    @SerializedName("eventDate")
    private String eventDate;
    @SerializedName("thumbnail")
    private String thumbnail;

    public Video() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
