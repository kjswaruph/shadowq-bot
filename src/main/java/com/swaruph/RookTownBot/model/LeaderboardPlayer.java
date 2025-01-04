package com.swaruph.RookTownBot.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LeaderboardPlayer {
    /*
    Player  |  Agents  |  Rounds  |  Rating  |  ACS  |  KDA  |  KAST  |  ADR  |  KPR  |  APR  |  FKPR  |  FDPR  |  HS%  |  CL%  |  CL  |  KMAX  |  Kills  |  Deaths  |  Assists  |  Wins  |  FK  |  FD
    */
    private final String puuid;
    private String discordId;
    private String leaderboardPlayerName;
    private String agents;
    private int totalRounds;
    private int totalMatches;
    private double rating;
    private double ACS;
    private double KDA;
    private int KAST;
    private double ADR;
    private double KPR;
    private double APR;
    private double FKPR;
    private double FDPR;
    private int HS;
    private int CL; //Clutch success
    private double CLWP; //Clutches won/played
    private int KMAX; //Max kills in a game
    private int kills;
    private int deaths;
    private int assists;
    private int FK;
    private int FD;
    private int wins;
    private int loses;

    public LeaderboardPlayer(String playerNameByPuuid) {
        this.puuid = playerNameByPuuid;
    }

    public LeaderboardPlayer(String puuid, String discordId, String leaderboardPlayerName, String agents, int totalRounds, int totalMatches, double rating,  double ACS, double KDA, int KAST, double ADR, double KPR, double APR, double FKPR, double FDPR, int HS, int CL, double CLWP, int KMAX, int kills, int deaths, int assists, int FK, int FD, int wins, int loses) {
        this.puuid = puuid;
        this.discordId = discordId;
        this.leaderboardPlayerName = leaderboardPlayerName;
        this.agents = agents;
        this.totalRounds = totalRounds;
        this.totalMatches = totalMatches;
        this.rating = rating;
        this.ACS = ACS;
        this.KDA = KDA;
        this.KAST = KAST;
        this.ADR = ADR;
        this.KPR = KPR;
        this.APR = APR;
        this.FKPR = FKPR;
        this.FDPR = FDPR;
        this.HS = HS;
        this.CL = CL;
        this.CLWP = CLWP;
        this.KMAX = KMAX;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.FK = FK;
        this.FD = FD;
        this.wins = wins;
        this.loses = loses;
    }

    public String getPuuid() {
        return puuid;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getLeaderboardPlayerName() {
        return leaderboardPlayerName;
    }

    public void setLeaderboardPlayerName(String leaderboardPlayerName) {
        this.leaderboardPlayerName = leaderboardPlayerName;
    }

    public String getAgents() {
        return agents;
    }

    public void setAgents(String agent) {
        if(this.agents == null){
            this.agents = agent;
            return;
        }
        if(this.agents.contains(agent)){
            return;
        }
        this.agents = this.agents + ", " + agent;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = this.totalRounds + totalRounds ;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = this.totalMatches +  totalMatches;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if(this.rating == 0){
            this.rating = roundToTwoDecimalPlaces(rating);
            return;
        }
        this.rating = roundToTwoDecimalPlaces(((this.rating*(totalMatches-1))+rating)/(totalMatches));
    }

    public double getACS() {
        return ACS;
    }

    public void setACS(double ACS) {
        if (this.ACS == 0){
            this.ACS = roundToTwoDecimalPlaces(ACS);
            return;
        }
        this.ACS = roundToTwoDecimalPlaces(((this.ACS*(totalMatches-1))+ACS)/(totalMatches));
    }

    public double getKDA() {
        return KDA;
    }

    public void setKDA(double KDA) {
        if (this.KDA == 0){
            this.KDA = roundToTwoDecimalPlaces(KDA);
            return;
        }
        this.KDA = roundToTwoDecimalPlaces(((this.KDA*(totalMatches-1))+KDA)/(totalMatches));
    }

    public int getKAST() {
        return KAST;
    }

    public void setKAST(int KAST) {
        if (this.KAST == 0){
            this.KAST = KAST;
            return;
        }
        this.KAST = ((this.KAST*(totalMatches-1))+KAST)/(totalMatches);
    }

    public double getADR() {
        return ADR;
    }

    public void setADR(double ADR) {
        if (this.ADR == 0){
            this.ADR = roundToTwoDecimalPlaces(ADR);
            return;
        }
        this.ADR = roundToTwoDecimalPlaces(((this.ADR*(totalMatches-1))+ADR)/(totalMatches));
    }

    public double getKPR() {
        return KPR;
    }

    public void setKPR(double KPR) {
        if (this.KPR == 0){
            this.KPR = roundToTwoDecimalPlaces(KPR);
            return;
        }
        this.KPR = roundToTwoDecimalPlaces(((this.KPR*(totalMatches-1))+KPR)/(totalMatches));
    }

    public double getAPR() {
        return APR;
    }

    public void setAPR(double APR) {
        if (this.APR == 0){
            this.APR = roundToTwoDecimalPlaces(APR);
            return;
        }
        this.APR = roundToTwoDecimalPlaces(((this.APR*(totalMatches-1))+APR)/(totalMatches));
    }

    public double getFKPR() {
        return FKPR;
    }

    public void setFKPR(double FKPR) {
        if (this.FKPR == 0){
            this.FKPR = roundToTwoDecimalPlaces(FKPR);
            return;
        }
        this.FKPR = roundToTwoDecimalPlaces(((this.FKPR*totalMatches)+FKPR)/(totalMatches+1));
    }

    public double getFDPR() {
        return FDPR;
    }

    public void setFDPR(double FDPR) {
        if (this.FDPR == 0){
            this.FDPR = roundToTwoDecimalPlaces(FDPR);
            return;
        }
        this.FDPR = roundToTwoDecimalPlaces(((this.FDPR*(totalMatches-1))+FDPR)/(totalMatches));
    }

    public int getHS() {
        return HS;
    }

    public void setHS(int HS) {
        if (this.HS == 0){
            this.HS = HS;
            return;
        }
        this.HS = ((this.HS*(totalMatches-1))+HS)/(totalMatches);
    }

    public int getCL() {
        return CL;
    }

    public void setCL(int CL) {
        if (this.CL == 0){
            this.CL = CL;
            return;
        }
        this.CL = ((this.CL*(totalMatches-1))+CL)/(totalMatches);
    }

    public double getCLWP() {
        return CLWP;
    }

    public void setCLWP(double CLWP) {
        if (this.CLWP == 0){
            this.CLWP = roundToTwoDecimalPlaces(CLWP);
            return;
        }
        this.CLWP = roundToTwoDecimalPlaces(((this.CLWP*(totalMatches-1))+CLWP)/(totalMatches));
    }

    public int getKMAX() {
        return KMAX;
    }

    public void setKMAX(int KMAX) {
        if(KMAX>this.KMAX){
            this.KMAX = KMAX;
        }
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = this.kills + kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = this.deaths + deaths;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = this.assists + assists;
    }

    public int getFK() {
        return FK;
    }

    public void setFK(int FK) {
        this.FK = this.FK + FK;
    }

    public int getFD() {
        return FD;
    }

    public void setFD(int FD) {
        this.FD = this.FD + FD;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = this.wins + wins;
    }

    public int getLoses() {
        return loses;
    }

    public void setLoses(int loses) {
        this.loses = this.loses + loses;
    }

    private double roundToTwoDecimalPlaces(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}


