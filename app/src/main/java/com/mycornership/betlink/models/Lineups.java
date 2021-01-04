package com.mycornership.betlink.models;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Lineups {
    @SerializedName("matchId")
    private long matchId;
    @SerializedName("homeFormation")
    private String homeFormation;
    @SerializedName("homeFormation")
    private String awayFormation;
    @SerializedName("homeLineup")
    private List<Player> homeLineup = new ArrayList<>();
    @SerializedName("awayLineup")
    private List<Player> awayLineup = new ArrayList<>();
    @SerializedName("homeBackup")
    private List<Player> homeBackup = new ArrayList<>();
    @SerializedName("awayBackup")
    private List<Player> awayBackup = new ArrayList<>();
    @SerializedName("createdDate")
    private String createdDate;

    public Lineups() {
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public String getHomeFormation() {
        return homeFormation;
    }

    public void setHomeFormation(String homeFormation) {
        this.homeFormation = homeFormation;
    }

    public String getAwayFormation() {
        return awayFormation;
    }

    public void setAwayFormation(String awayFormation) {
        this.awayFormation = awayFormation;
    }

    public List<Player> getHomeLineup() {
        return homeLineup;
    }

    public void setHomeLineup(List<Player> homeLineup) {
        this.homeLineup = homeLineup;
    }

    public List<Player> getAwayLineup() {
        return awayLineup;
    }

    public void setAwayLineup(List<Player> awayLineup) {
        this.awayLineup = awayLineup;
    }

    public List<Player> getHomeBackup() {
        return homeBackup;
    }

    public void setHomeBackup(List<Player> homeBackup) {
        this.homeBackup = homeBackup;
    }

    public List<Player> getAwayBackup() {
        return awayBackup;
    }

    public void setAwayBackup(List<Player> awayBackup) {
        this.awayBackup = awayBackup;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
