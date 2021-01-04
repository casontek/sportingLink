package com.mycornership.betlink.models;


import com.google.gson.annotations.SerializedName;

public class BirthdaySticker {
    @SerializedName("id")
    private long id;
    @SerializedName("celebrant")
    private String celebrant;
    @SerializedName("username")
    private String username;
    @SerializedName("userPhoto")
    private String userPhoto;
    @SerializedName("sticker")
    private String sticker;

    public BirthdaySticker() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCelebrant() {
        return celebrant;
    }

    public void setCelebrant(String celebrant) {
        this.celebrant = celebrant;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }
}
