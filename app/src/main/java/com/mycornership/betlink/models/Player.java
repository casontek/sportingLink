package com.mycornership.betlink.models;

import com.google.gson.annotations.SerializedName;

public class Player {
    @SerializedName("playerId")
    private long playerId;
    @SerializedName("teamId")
    private long teamId;
    @SerializedName("name")
    private String name;
    @SerializedName("number")
    private int number;
    @SerializedName("position")
    private int position;

    public Player() {
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
