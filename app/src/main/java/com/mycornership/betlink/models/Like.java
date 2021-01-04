package com.mycornership.betlink.models;


import com.google.gson.annotations.SerializedName;

public class Like {
    @SerializedName("predictionId")
    private long predictionId;
    @SerializedName("username")
    private String username;
    @SerializedName("createdDate")
    private String createdDate;

    public Like() {
    }

    public long getPredictionId() {
        return predictionId;
    }

    public void setPredictionId(long predictionId) {
        this.predictionId = predictionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
