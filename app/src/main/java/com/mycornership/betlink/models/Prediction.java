package com.mycornership.betlink.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Prediction {
    @SerializedName("id")
    private long id;
    @SerializedName("username")
    private String username;
    @SerializedName("userPhoto")
    private String userPhoto;
    @SerializedName("predictionDate")
    private String predictionDate;
    @SerializedName("predictionTip")
    private String predictionTip;
    @SerializedName("predictionHint")
    private String predictionHint;
    @SerializedName("category")
    private String category;
    @SerializedName("fixture")
    private Match match;
    @SerializedName("status")
    private String status;
    @SerializedName("tipslike")
    private List<Like> tipslike = new ArrayList<>();

    public Prediction() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(String predictionDate) {
        this.predictionDate = predictionDate;
    }

    public String getPredictionTip() {
        return predictionTip;
    }

    public void setPredictionTip(String predictionTip) {
        this.predictionTip = predictionTip;
    }

    public String getPredictionHint() {
        return predictionHint;
    }

    public void setPredictionHint(String predictionHint) {
        this.predictionHint = predictionHint;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Like> getTipslike() {
        return tipslike;
    }

    public void setTipslike(List<Like> tipslike) {
        this.tipslike = tipslike;
    }

    @Override
    public String toString() {
        return "Prediction{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", userPhoto='" + userPhoto + '\'' +
                ", predictionDate='" + predictionDate + '\'' +
                ", predictionTip='" + predictionTip + '\'' +
                ", predictionHint='" + predictionHint + '\'' +
                ", category='" + category + '\'' +
                ", match=" + match +
                ", status='" + status + '\'' +
                ", tipslike=" + tipslike +
                '}';
    }
}
