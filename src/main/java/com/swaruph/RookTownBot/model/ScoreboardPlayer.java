package com.swaruph.RookTownBot.model;

public class ScoreboardPlayer {

    private String scoreboardPlayerName;
    private String agent;
    private String team;
    private int ACS;
    private int kills;
    private int deaths;
    private int assists;
    private int KDDiff;
    private int KAST;
    private int ADR;
    private int HS;
    private int FK;
    private int FD;

    public ScoreboardPlayer(String scoreboardPlayerName, String agent, String team, int ACS, int kills, int deaths, int assists, int KDDiff, int KAST, int ADR, int HS, int FK, int FD, int FKFDDiff){
        this.scoreboardPlayerName = scoreboardPlayerName;
        this.agent = agent;
        this.team = team;
        this.ACS = ACS;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.KDDiff = KDDiff;
        this.KAST = KAST;
        this.ADR = ADR;
        this.HS = HS;
        this.FK = FK;
        this.FD = FD;
    }

    public String getScoreboardPlayerName() {
        return scoreboardPlayerName;
    }

    public String getAgent() {
        return agent;
    }

    public String getTeam() {
        return team;
    }

    public int getACS() {
        return ACS;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getAssists() {
        return assists;
    }

    public int getKAST() {
        return KAST;
    }

    public int getADR() {
        return ADR;
    }

    public int getHS() {
        return HS;
    }

    public int getFK() {
        return FK;
    }

    public int getFD() {
        return FD;
    }

    public int getKDDiff() {
        return KDDiff;
    }

    public void setKDDiff(int KDDiff) {
        this.KDDiff = KDDiff;
    }
}
