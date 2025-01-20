package com.swaruph.RookTownBot.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.socketconnection.jva.ValorantAPI;
import net.socketconnection.jva.enums.Region;
import net.socketconnection.jva.player.ValorantPlayer;

import static com.swaruph.RookTownBot.RookTownBot.rookDB;

public class CustomMatch {

    ValorantAPI valorantAPI;
    ValorantPlayer admin;
    String matchID;
    int totalRounds;

    public CustomMatch(ValorantAPI valorantAPI, ValorantPlayer player) {
        this.valorantAPI = valorantAPI;
        this.admin = player;
    }

    private final Map<String, JsonObject> matchDataCache = new HashMap<>();

    public JsonObject getMatchData() throws IOException {
        if(!matchDataCache.containsKey(matchID)) {
            matchID = getMatchID();
        }
        if(matchDataCache.containsKey(matchID)){
            return matchDataCache.get(matchID);
        }
        JsonObject matchData = valorantAPI.sendRestRequest("/v4/match/" + admin.getRegion().getQuery() + "/" + matchID).getAsJsonObject().get("data").getAsJsonObject();
        matchDataCache.put(matchID, matchData);
        this.totalRounds = getTotalRounds(matchID);
        return matchData;
    }

    public String getMatchID() throws IOException {
        JsonArray matchHistory = valorantAPI.sendRestRequest("/v3/matches/" + admin.getRegion().getQuery() + "/" + admin.getUsername() + "/" + admin.getTag() + "?mode=custom&size=1").getAsJsonObject().get("data").getAsJsonArray();
        if (matchHistory != null && !matchHistory.isEmpty()) {
            JsonObject firstMatch = matchHistory.get(0).getAsJsonObject();
            JsonObject metadata = firstMatch.get("metadata").getAsJsonObject();
            return metadata.get("matchid").getAsString();
        } else {
            return null; // Or handle the case where no matches are found
        }
    }

    public String getMatchMap(String matchID) throws IOException {
        JsonObject matchData = getMatchData();
        return matchData.get("metadata").getAsJsonObject().get("map").getAsJsonObject().get("name").getAsString();
    }

    public String getMatchMode(String matchID) throws IOException {
        JsonObject matchData = getMatchData();
        return matchData.get("metadata").getAsJsonObject().get("queue").getAsJsonObject().get("mode_type").getAsString();
    }

    public String getMatchRegion(String matchID) throws IOException {
        JsonObject matchData = getMatchData();
        String regionData = matchData.get("metadata").getAsJsonObject().get("region").getAsString();
        String cluster = matchData.get("metadata").getAsJsonObject().get("cluster").getAsString();
        Region region = Region.getFromQuery(matchData.get("metadata").getAsJsonObject().get("region").getAsString());
        return cluster+", "+region.getName();
    }

    public Map<String, List<String>> getPlayersData(String matchID) throws IOException {
        JsonObject matchData = getMatchData();
        JsonArray matchPlayers = matchData.get("players").getAsJsonArray();
        Map<String, List<String>> playerData = new HashMap<>();
        for(JsonElement player : matchPlayers) {
            JsonObject playerObject = player.getAsJsonObject();
            String puuid = playerObject.get("puuid").getAsString();
            String agent = playerObject.get("agent").getAsJsonObject().get("name").getAsString();
            String teamId = playerObject.get("team_id").getAsString();
            JsonObject stats = playerObject.get("stats").getAsJsonObject();
            int acs = (int) Math.round((double) stats.get("score").getAsInt() / totalRounds);
            int kills = stats.get("kills").getAsInt();
            int deaths = stats.get("deaths").getAsInt();
            int assists = stats.get("assists").getAsInt();
            playerData.put(puuid, List.of(agent, teamId, String.valueOf(acs), String.valueOf(kills), String.valueOf(deaths), String.valueOf(assists)));
        }
        return playerData;
    }

    public String winningTeam(String matchID) throws IOException {
        JsonObject matchData = getMatchData();
        JsonArray teams = matchData.get("teams").getAsJsonArray();
        JsonObject team1 = teams.get(0).getAsJsonObject();
        JsonObject team2 = teams.get(1).getAsJsonObject();
        int team1Rounds = team1.get("rounds").getAsJsonObject().get("won").getAsInt();
        int team2Rounds = team2.get("rounds").getAsJsonObject().get("won").getAsInt();
        if (team1Rounds > team2Rounds) {
            return team1.get("team_id").getAsString();
        } else {
            return team2.get("team_id").getAsString();
        }
    }

    public String rounds(String matchID) throws IOException {
        JsonObject matchData = getMatchData();
//        if (matchID==null){
//            matchData = getMatchData();
//        }else {
//            matchData = getMatchData(matchID);
//        }
        JsonArray teams = matchData.get("teams").getAsJsonArray();
        JsonObject team1 = teams.get(0).getAsJsonObject();
        JsonObject team2 = teams.get(1).getAsJsonObject();
        int team1Rounds = team1.get("rounds").getAsJsonObject().get("won").getAsInt();
        int team2Rounds = team2.get("rounds").getAsJsonObject().get("won").getAsInt();
        if (team1Rounds > team2Rounds) {
            return "Team A " + team1Rounds + " : " + team2Rounds + " Team B";
        } else {
            return "Team A " + team2Rounds + " : " + team1Rounds + " Team B";
        }
    }

    public String getPlayerNameByPuuid(String puuid) throws IOException {
        if(rookDB.getPlayerNameByPuuid(puuid) != null) {
            return rookDB.getPlayerNameByPuuid(puuid);
        }
        JsonObject player = valorantAPI.sendRestRequest("/v2/by-puuid/account"+ "/" + puuid).getAsJsonObject().get("data").getAsJsonObject();
        String username = player.get("name").getAsString();
        String tag = player.get("tag").getAsString();
        return username + "#" + tag;
    }

    public int getTotalRounds(String matchID) throws IOException {
        JsonObject matchData = getMatchData();
//        if (matchID==null){
//            matchData = getMatchData();
//        }else {
//            matchData = getMatchData(matchID);
//        }
        JsonArray teams = matchData.get("teams").getAsJsonArray();
        JsonObject team1 = teams.get(0).getAsJsonObject();
        int won = team1.get("rounds").getAsJsonObject().get("won").getAsInt();
        int lost = team1.get("rounds").getAsJsonObject().get("lost").getAsInt();
        return won+lost;
    }

    public Map<String, Integer> calculateKAST(String matchID) throws IOException {
        JsonObject matchData = getMatchData();
//        if(matchID==null) {
//            matchData = getMatchData();
//        }else {
//            matchData = getMatchData(matchID);
//        }
        JsonArray kills = matchData.get("kills").getAsJsonArray();
        JsonArray rounds = matchData.get("rounds").getAsJsonArray();
        Map<String, Set<Integer>> playerKastRounds = new HashMap<>();

        for (int roundNum = 0; roundNum < rounds.size(); roundNum++) {
            Set<String> deadPlayersInRound = new HashSet<>();

            for (JsonElement killElement : kills) {
                JsonObject kill = killElement.getAsJsonObject();
                if (kill.get("round").getAsInt() == roundNum) {
                    deadPlayersInRound.add(kill.getAsJsonObject("victim").get("puuid").getAsString());
                }
            }

            JsonObject round = rounds.get(roundNum).getAsJsonObject();
            JsonArray roundStats = round.getAsJsonArray("stats");
            for (JsonElement statElement : roundStats) {
                JsonObject playerStat = statElement.getAsJsonObject();
                String playerPuuid = playerStat.getAsJsonObject("player").get("puuid").getAsString();

                if (!deadPlayersInRound.contains(playerPuuid) &&
                        !playerStat.get("was_afk").getAsBoolean()) {
                    playerKastRounds.putIfAbsent(playerPuuid, new HashSet<>());
                    playerKastRounds.get(playerPuuid).add(roundNum);
                }
            }
        }

        for (JsonElement killElement : kills) {
            JsonObject kill = killElement.getAsJsonObject();
            int round = kill.get("round").getAsInt();

            String killerPuuid = kill.getAsJsonObject("killer").get("puuid").getAsString();
            playerKastRounds.putIfAbsent(killerPuuid, new HashSet<>());
            playerKastRounds.get(killerPuuid).add(round);

            JsonArray assistants = kill.get("assistants").getAsJsonArray();
            for (JsonElement assistant : assistants) {
                String assistantPuuid = assistant.getAsJsonObject().get("puuid").getAsString();
                playerKastRounds.putIfAbsent(assistantPuuid, new HashSet<>());
                playerKastRounds.get(assistantPuuid).add(round);
            }

            String victimPuuid = kill.getAsJsonObject("victim").get("puuid").getAsString();
            String victimTeam = kill.getAsJsonObject("victim").get("team").getAsString();
            int killTime = kill.get("time_in_round_in_ms").getAsInt();

            for (JsonElement potentialTradeElement : kills) {
                JsonObject potentialTrade = potentialTradeElement.getAsJsonObject();
                if (potentialTrade.get("round").getAsInt() != round)
                    continue;

                int tradeTime = potentialTrade.get("time_in_round_in_ms").getAsInt();
                if (tradeTime <= killTime)
                    continue;
                if (tradeTime - killTime > 5000)
                    break;

                JsonObject tradeKiller = potentialTrade.getAsJsonObject("killer");
                JsonObject tradeVictim = potentialTrade.getAsJsonObject("victim");

                if (tradeVictim.get("puuid").getAsString().equals(killerPuuid) &&
                        tradeKiller.get("team").getAsString().equals(victimTeam)) {
                    playerKastRounds.putIfAbsent(victimPuuid, new HashSet<>());
                    playerKastRounds.get(victimPuuid).add(round);
                    break;
                }
            }
        }

        Map<String, Integer> playerKast = new HashMap<>();
        int totalRounds = rounds.size();
        for (Map.Entry<String, Set<Integer>> entry : playerKastRounds.entrySet()) {
            String playerPuuid = entry.getKey();
            Set<Integer> playerRounds = entry.getValue();
            double kastPercentage = (playerRounds.size() / (double) totalRounds) * 100;
            playerKast.put(playerPuuid, (int) Math.round(kastPercentage));
        }

        return playerKast;
    }

    public Map<String, Integer> calculateADR(String matchID) throws IOException {
        JsonObject matchData = getMatchData();
        JsonArray players = matchData.get("players").getAsJsonArray();
        Map<String, Integer> playerDamage = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            String puuid = players.get(i).getAsJsonObject().get("puuid").getAsString();
            int damage = players.get(i).getAsJsonObject().get("stats").getAsJsonObject().get("damage").getAsJsonObject().get("dealt").getAsInt();
            damage = damage/totalRounds;
            playerDamage.put(puuid, damage);
        }
        return playerDamage;
    }

    public List<Map<String, Integer>> calculateFKFD(String matchID) throws IOException {
        JsonObject matchData = getMatchData();

        JsonArray kills = matchData.get("kills").getAsJsonArray();
        Map<String, Integer> playerFK = new HashMap<>();
        Map<String, Integer> playerFD = new HashMap<>();
        int j = 0;
        for (int i = 0; i<kills.size(); i++){
            JsonObject kill = kills.get(i).getAsJsonObject();
            int round = kill.get("round").getAsInt();
            if(round != j){
                continue;
            }
            j++;
            JsonObject killer = kill.get("killer").getAsJsonObject();
            String killerPuuid = killer.get("puuid").getAsString();
            JsonObject victim = kill.get("victim").getAsJsonObject();
            String victimPuuid = victim.get("puuid").getAsString();
            playerFK.put(killerPuuid, playerFK.getOrDefault(killerPuuid, 0)+1);
            playerFD.put(victimPuuid, playerFD.getOrDefault(victimPuuid, 0)+1);
        }
        JsonArray players = matchData.get("players").getAsJsonArray();
        for (int i = 0; i < players.size(); i++) {
            String puuid = players.get(i).getAsJsonObject().get("puuid").getAsString();
            if(!playerFK.containsKey(puuid)){
                playerFK.put(puuid, 0);
            }
            if(!playerFD.containsKey(puuid)){
                playerFD.put(puuid, 0);
            }
        }

        LinkedList<Map<String, Integer>> list = new LinkedList<>();
        list.add(playerFK);
        list.add(playerFD);
        return list;
    }

    public Map<String, Integer> calculateHSRate(String matchID) throws IOException {
        JsonObject matchData = getMatchData();
//        if (matchID==null){
//            matchData = getMatchData();
//        }else {
//            matchData = getMatchData(matchID);
//        }
        JsonArray players = matchData.get("players").getAsJsonArray();
        Map<String, Integer> playerHSRate = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            String puuid = players.get(i).getAsJsonObject().get("puuid").getAsString();
            JsonObject player = players.get(i).getAsJsonObject();
            JsonObject stats = player.getAsJsonObject("stats");
            int headshots = stats.get("headshots").getAsInt();
            int totalShots = headshots + stats.get("bodyshots").getAsInt() + stats.get("legshots").getAsInt();
            int hsRate = (headshots*100)/totalShots;
            playerHSRate.put(puuid, hsRate);
        }
        return playerHSRate;
    }

    public double getKPR(String puuid, String matchID) throws IOException {
        JsonObject matchData = getMatchData();
        JsonArray players = matchData.get("players").getAsJsonArray();
        for (int i = 0; i < players.size(); i++) {
            JsonObject player = players.get(i).getAsJsonObject();
            if (player.get("puuid").getAsString().equals(puuid)) {
                JsonObject stats = player.getAsJsonObject("stats");
                int kills = stats.get("kills").getAsInt();
                return kills/totalRounds;
            }
        }
        return 0;
    }

    public double getAPR(String puuid, String matchID){
        JsonObject matchData = matchDataCache.get(matchID);
        JsonArray players = matchData.get("players").getAsJsonArray();
        for (int i = 0; i < players.size(); i++) {
            JsonObject player = players.get(i).getAsJsonObject();
            if (player.get("puuid").getAsString().equals(puuid)) {
                JsonObject stats = player.getAsJsonObject("stats");
                int assists = stats.get("assists").getAsInt();
                return assists/totalRounds;
            }
        }
        return 0;
    }

    public double getFKPR(String puuid, int FK) throws IOException {
        return (double) FK/totalRounds;
    }

    public double getFDPR(String puuid, int FD) throws IOException {
        return (double) FD /totalRounds;
    }

    public Map<String, Integer> getRoundsPlayed(String matchId) throws IOException {
        JsonObject matchData = getMatchData();
//        if (matchID == null){
//            matchData = getMatchData();
//        }else {
//            matchData = getMatchData(matchId);
//        }
        JsonArray players = matchData.get("players").getAsJsonArray();
        Map<String, Integer> playersRounds = new HashMap<>();
        for (int i = 0; i < players.size(); i++) {
            JsonObject player = players.get(i).getAsJsonObject();
            String playerPuuid = player.get("puuid").getAsString();
            int afkRounds = player.get("behavior").getAsJsonObject().get("afk_rounds").getAsInt() + player.get("behavior").getAsJsonObject().get("rounds_in_spawn").getAsInt();
            int roundsPlayed = totalRounds - afkRounds;
            playersRounds.put(playerPuuid, roundsPlayed);
        }
        return playersRounds;
    }

    public double calculateValorantRating(int kills, int deaths, int assists, int acs, double adra, int kast, int fk, int fd) {
        double KILL_CONTRIBUTION_WEIGHT = 0.4;
        double DEATH_CONTRIBUTION_WEIGHT = 0.4;
        double ASSIST_CONTRIBUTION_WEIGHT = 0.1;
        double ADRa_CONTRIBUTION_WEIGHT = 0.1;
        double KAST_CONTRIBUTION_WEIGHT = 0.1;
        double ACS_CONTRIBUTION_WEIGHT = 0.1;
        double FIRST_KILL_BONUS = 0.05;
        double FIRST_DEATH_PENALTY = -0.05;

        double killContribution = kills * 0.5;
        double deathContribution = deaths * -0.5;

        double firstKillBonus = fk * FIRST_KILL_BONUS;
        double firstDeathPenalty = fd * FIRST_DEATH_PENALTY;

        return (killContribution + deathContribution +
                (assists * ASSIST_CONTRIBUTION_WEIGHT) +
                (adra * ADRa_CONTRIBUTION_WEIGHT) +
                (kast * KAST_CONTRIBUTION_WEIGHT) +
                (acs * ACS_CONTRIBUTION_WEIGHT) +
                firstKillBonus + firstDeathPenalty) /
                (KILL_CONTRIBUTION_WEIGHT + DEATH_CONTRIBUTION_WEIGHT +
                        ASSIST_CONTRIBUTION_WEIGHT + ADRa_CONTRIBUTION_WEIGHT +
                        KAST_CONTRIBUTION_WEIGHT);
    }
}


