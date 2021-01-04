package com.mycornership.betlink.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TipsResponse {
    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<Prediction> data;

    public TipsResponse() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Prediction> getData() {
        return data;
    }

    public void setData(List<Prediction> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

}
