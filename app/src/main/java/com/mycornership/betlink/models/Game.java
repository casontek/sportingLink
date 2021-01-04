package com.mycornership.betlink.models;

import com.google.gson.annotations.SerializedName;

public class Game {
    @SerializedName("id")
    private long id;
    @SerializedName("fixture")
    private Match match;
    @SerializedName("postDate")
    private String postDate;
    @SerializedName("expireDate")
    private String expireDate;
    @SerializedName("expireTime")
    private String expireTime;

    public Game() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", match=" + match +
                ", postDate='" + postDate + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", expireTime='" + expireTime + '\'' +
                '}';
    }

}
