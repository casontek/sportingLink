package com.mycornership.betlink.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BirthdayResponse {
    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private List<Celebrant> data;

    public BirthdayResponse() {
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

    public List<Celebrant> getData() {
        return data;
    }

    public void setData(List<Celebrant> data) {
        this.data = data;
    }
}
