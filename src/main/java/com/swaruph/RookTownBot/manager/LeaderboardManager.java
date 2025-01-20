package com.swaruph.RookTownBot.manager;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.swaruph.RookTownBot.model.Leaderboard;
import com.swaruph.RookTownBot.model.LeaderboardPlayer;

import static com.swaruph.RookTownBot.RookTownBot.rookDB;

public class LeaderboardManager {

    public void updateLeaderboardStats(List<LeaderboardPlayer> allPlayers) throws IOException {
        for (LeaderboardPlayer player : allPlayers) {
            LeaderboardPlayer leaderboardPlayer = rookDB.getLeaderboardPlayer(player.getPuuid());
            if (leaderboardPlayer == null) {
                leaderboardPlayer = new LeaderboardPlayer(player.getPuuid());
                rookDB.insertIntoLeaderboard(player.getPuuid());
            }
            updateStatsFromMatch(player, leaderboardPlayer);
            rookDB.updateLeaderboardStats(leaderboardPlayer);
        }
    }

    private void updateStatsFromMatch(LeaderboardPlayer player, LeaderboardPlayer leaderboardPlayer) {
        leaderboardPlayer.setLeaderboardPlayerName(player.getLeaderboardPlayerName());
        leaderboardPlayer.setDiscordId(player.getDiscordId());
        leaderboardPlayer.setAgents(player.getAgents());
        leaderboardPlayer.setTotalRounds(player.getTotalRounds());
        leaderboardPlayer.setTotalMatches(player.getTotalMatches());
        leaderboardPlayer.setRating(player.getRating());
        leaderboardPlayer.setACS(player.getACS());
        leaderboardPlayer.setKDA(player.getKDA());
        leaderboardPlayer.setKAST(player.getKAST());
        leaderboardPlayer.setADR(player.getADR());
        leaderboardPlayer.setKPR(player.getKPR());
        leaderboardPlayer.setAPR(player.getAPR());
        leaderboardPlayer.setFKPR(player.getFKPR());
        leaderboardPlayer.setFDPR(player.getFDPR());
        leaderboardPlayer.setHS(player.getHS());
        leaderboardPlayer.setCL(player.getCL());
        leaderboardPlayer.setCLWP(player.getCLWP());
        leaderboardPlayer.setKMAX(player.getKMAX());
        leaderboardPlayer.setKills(player.getKills());
        leaderboardPlayer.setDeaths(player.getDeaths());
        leaderboardPlayer.setAssists(player.getAssists());
        leaderboardPlayer.setFK(player.getFK());
        leaderboardPlayer.setFD(player.getFD());
        leaderboardPlayer.setWins(player.getWins());
        leaderboardPlayer.setLoses(player.getLoses());
    }

    public List<Leaderboard> getLeaderboard(String sortBy, int limit) {
        List<LeaderboardPlayer> allStats = rookDB.getAllLeaderboardStats();
        return allStats.stream()
                       .map(stats -> new Leaderboard(
                               rookDB.getPlayerNameByPuuid(stats.getPuuid()),
                               stats))
                       .sorted((e1, e2) -> compareBySort(e1, e2, sortBy))
                       .limit(limit)
                       .collect(Collectors.toList());
    }

    private int compareBySort(Leaderboard e1, Leaderboard e2, String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "rating" -> Double.compare(e2.getRating(), e1.getRating());
            case "kda" -> Double.compare(e2.getKDA(), e1.getKDA());
            case "acs" -> Double.compare(e2.getACS(), e1.getACS());
            case "adr" -> Double.compare(e2.getADR(), e1.getADR());
            case "hs" -> Integer.compare(e2.getHS(), e1.getHS());
            case "wins" -> Integer.compare(e2.getWins(), e1.getWins());
            default -> Double.compare(e2.getRating(), e1.getRating());
        };
    }
}


