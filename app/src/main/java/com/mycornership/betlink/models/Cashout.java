package com.mycornership.betlink.models;

import com.google.gson.annotations.SerializedName;

public class Cashout {
    @SerializedName("success")
    private boolean success;
    @SerializedName("amount")
    private String amount;
    @SerializedName("status")
    private String status;
    @SerializedName("username")
    private String username;
    @SerializedName("date")
    private String date;

    public Cashout() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
