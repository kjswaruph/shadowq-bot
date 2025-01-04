package com.swaruph.RookTownBot.model;

public class Leaderboard {

    private String puuid;
    private LeaderboardPlayer leaderboardPlayer;

    public Leaderboard(String puuid, LeaderboardPlayer leaderboardPlayer){
        this.puuid = puuid;
        this.leaderboardPlayer = leaderboardPlayer;
    }

    public String getPuuid() {
        return puuid;
    }

    public LeaderboardPlayer getLeaderboardPlayer() {
        return leaderboardPlayer;
    }

    public String getLeaderboardPlayerName() {
        return leaderboardPlayer.getLeaderboardPlayerName();
    }

    public String getDiscordId() {
        return leaderboardPlayer.getDiscordId();
    }

    public String getAgents() {
        return leaderboardPlayer.getAgents();
    }

    public int getTotalRounds() {
        return leaderboardPlayer.getTotalRounds();
    }

    public int getTotalMatches() {
        return leaderboardPlayer.getTotalMatches();
    }

    public double getRating() {
        return leaderboardPlayer.getRating();
    }

    public double getACS() {
        return leaderboardPlayer.getACS();
    }

    public double getKDA() {
        return leaderboardPlayer.getKDA();
    }

    public int getKAST() {
        return leaderboardPlayer.getKAST();
    }

    public double getADR() {
        return leaderboardPlayer.getADR();
    }

    public double getKPR(){
        return leaderboardPlayer.getKPR();
    }

    public double getAPR(){
        return leaderboardPlayer.getAPR();
    }

    public double getFKPR(){
        return leaderboardPlayer.getFKPR();
    }

    public double getFDPR(){
        return leaderboardPlayer.getFDPR();
    }

    public int getHS(){
        return leaderboardPlayer.getHS();
    }

    public int getCL(){
        return leaderboardPlayer.getCL();
    }

    public double getCLWP(){
        return leaderboardPlayer.getCLWP();
    }

    public int getKMAX(){
        return leaderboardPlayer.getKMAX();
    }

    public int getKills() {
        return leaderboardPlayer.getKills();
    }

    public int getDeaths() {
        return leaderboardPlayer.getDeaths();
    }

    public int getAssists() {
        return leaderboardPlayer.getAssists();
    }

    public int getFK() {
        return leaderboardPlayer.getFK();
    }

    public int getFD() {
        return leaderboardPlayer.getFD();
    }

    public int getWins() {
        return leaderboardPlayer.getWins();
    }

    public int getLoses() {
        return leaderboardPlayer.getLoses();
    }

}
