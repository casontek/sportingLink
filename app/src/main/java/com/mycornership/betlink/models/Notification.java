package com.mycornership.betlink.models;

import com.google.gson.annotations.SerializedName;

public class Notification {
    @SerializedName("date")
    private String date;
    @SerializedName("username")
    private String username;
    @SerializedName("message")
    private String message;

    public Notification() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
