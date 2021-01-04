package com.mycornership.betlink.models;

import com.google.gson.annotations.SerializedName;

public class Match {
    @SerializedName("fixture_id")
    private long matchId;
    @SerializedName("league_id")
    private String leagueId;
    @SerializedName("league")
    private League league;
    @SerializedName("homeTeam")
    private Team homeTeam;
    @SerializedName("awayTeam")
    private Team awayTeam;
    @SerializedName("event_date")
    private String matchDate;
    @SerializedName("event_timestamp")
    private long matchTime;
    @SerializedName("goalsHomeTeam")
    private int homeGoal;
    @SerializedName("goalsAwayTeam")
    private int awayGoal;
    @SerializedName("status")
    private String status;

    public Match() {
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public String getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(String leagueId) {
        this.leagueId = leagueId;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public long getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(long matchTime) {
        this.matchTime = matchTime;
    }

    public int getHomeGoal() {
        return homeGoal;
    }

    public void setHomeGoal(int homeGoal) {
        this.homeGoal = homeGoal;
    }

    public int getAwayGoal() {
        return awayGoal;
    }

    public void setAwayGoal(int awayGoal) {
        this.awayGoal = awayGoal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Match{" +
                "matchId=" + matchId +
                ", leagueId='" + leagueId + '\'' +
                ", league=" + league +
                ", homeTeam=" + homeTeam +
                ", awayTeam=" + awayTeam +
                ", matchDate='" + matchDate + '\'' +
                ", matchTime=" + matchTime +
                ", homeGoal=" + homeGoal +
                ", awayGoal=" + awayGoal +
                '}';
    }
}
