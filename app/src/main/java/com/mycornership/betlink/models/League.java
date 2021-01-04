package com.mycornership.betlink.models;

import com.google.gson.annotations.SerializedName;

public  class League {
    @SerializedName("league_id")
    private String leagueId;
    @SerializedName("name")
    private String name;
    @SerializedName("country")
    private String country;
    @SerializedName("logo")
    private String logo;

    public League() {
    }

    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        return "League{" +
                "leagueId='" + leagueId + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }
}
