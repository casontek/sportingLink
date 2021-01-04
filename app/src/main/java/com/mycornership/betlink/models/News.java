package com.mycornership.betlink.models;


import com.google.gson.annotations.SerializedName;

public class News {
    @SerializedName("id")
    private long id;
    @SerializedName("title")
    private String title;
    @SerializedName("detail")
    private String detail;
    @SerializedName("mediaUrl")
    private String mediaUrl;
    @SerializedName("createdDate")
    private String createdDate;
    @SerializedName("eventDate")
    private String eventDate;
    @SerializedName("eventTime")
    private String eventTime;
    @SerializedName("source")
    private String source;
    @SerializedName("editor")
    private String editor;
    @SerializedName("category")
    private String category;

    public News() {
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
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

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", eventDate='" + eventDate + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", source='" + source + '\'' +
                ", editor='" + editor + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
