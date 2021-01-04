package com.mycornership.betlink.models;

import com.google.gson.annotations.SerializedName;

public class Celebrant {
    @SerializedName("id")
    private long id;
    @SerializedName("name")
    private String name;
    @SerializedName("birthDate")
    private String birthDate;
    @SerializedName("noLoves")
    private int noLoves;
    @SerializedName("noLikes")
    private int noLikes;
    @SerializedName("shortBio")
    private String shortBio;
    @SerializedName("photo")
    private String photo;
    @SerializedName("photo2")
    private String photo2;
    @SerializedName("note")
    private String note;
    @SerializedName("message")
    private String message;
    @SerializedName("achievements")
    private String achievements;
    @SerializedName("video")
    private String video;

    public Celebrant() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public int getNoLoves() {
        return noLoves;
    }

    public void setNoLoves(int noLoves) {
        this.noLoves = noLoves;
    }

    public int getNoLikes() {
        return noLikes;
    }

    public void setNoLikes(int noLikes) {
        this.noLikes = noLikes;
    }

    public String getShortBio() {
        return shortBio;
    }

    public void setShortBio(String shortBio) {
        this.shortBio = shortBio;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto2() {
        return photo2;
    }

    public void setPhoto2(String photo2) {
        this.photo2 = photo2;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAchievements() {
        return achievements;
    }

    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
