package com.mycornership.betlink.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class FBModel {
    @SerializedName("message")
    private String message;
    @SerializedName("type")
    private String type;
    @SerializedName("error_user_title")
    private String userTitle;
    @SerializedName("error_user_msg")
    private String userMessage;
    @SerializedName("code")
    private String code;
    @SerializedName("fbtrace_id")
    private String traceId;

    public FBModel() {

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(String userTitle) {
        this.userTitle = userTitle;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "FBEModel{" +
                "message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", userTitle='" + userTitle + '\'' +
                ", userMessage='" + userMessage + '\'' +
                ", code='" + code + '\'' +
                ", traceId='" + traceId + '\'' +
                '}';
    }

}

