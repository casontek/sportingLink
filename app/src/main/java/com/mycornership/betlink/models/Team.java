package com.mycornership.betlink.models;


import com.google.gson.annotations.SerializedName;

public class Team {
    @SerializedName("team_id")
    private long teamId;
    @SerializedName("team_name")
    private String name;
    @SerializedName("logo")
    private String logo;

    public Team() {
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        return "Team{" +
                "teamId=" + teamId +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }

}
