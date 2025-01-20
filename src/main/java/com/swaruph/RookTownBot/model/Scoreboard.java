package com.swaruph.RookTownBot.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.swaruph.RookTownBot.RookTownBot;
import com.swaruph.RookTownBot.config.ConfigLoader;
import com.swaruph.RookTownBot.utils.TableGenerator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import static com.swaruph.RookTownBot.RookTownBot.rookDB;

public class Scoreboard{
    private String matchId;
    private final int queueId;
    private final CustomMatch customMatch;
    private final List<LeaderboardPlayer> leaderboardPlayers;
    private final List<Rook> winningRooks;
    private final List<Rook> losingRooks;
    private String mvp;
    private final JDA jda = RookTownBot.getInstance().getJDA();

    public Scoreboard(CustomMatch customMatch, int queueId) {
        this.queueId = queueId;
        this.customMatch = customMatch;
        this.winningRooks = new ArrayList<>();
        this.losingRooks = new ArrayList<>();
        this.leaderboardPlayers = new ArrayList<>();
    }

    public String getWinningRooksAsString(){
        StringBuilder rooks = new StringBuilder();
        List<CompletableFuture<User>> futures = new ArrayList<>();
        for (Rook rook : winningRooks) {
            CompletableFuture<User> futureUser = jda.retrieveUserById(rook.getDiscordId()).submit();
            futures.add(futureUser);
        }
        CompletableFuture<User> cfMvpuser = futures.getFirst();

        try {
            User mvpUser = cfMvpuser.get();
            this.mvp = mvpUser.getAsMention();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int i = 0;
        for (CompletableFuture<User> future : futures) {
            try {
                User user = future.get();
                if (user != null ) {
                    if (i==0){
                        rooks.append(user.getAsMention()).append("(MVP)").append("\n");
                        i++;
                    }else {
                        rooks.append(user.getAsMention()).append("\n");
                    }
                } else {
                    rooks.append("Unknown User").append("\n");
                }
            } catch (InterruptedException |
                     ExecutionException e) {
                rooks.append("Error retrieving user").append("\n");
            }
        }
        return rooks.toString();
    }

    public String getLosingRooksAsString(){
        StringBuilder rooks = new StringBuilder();

        List<CompletableFuture<User>> futures = new ArrayList<>();
        for (Rook rook : losingRooks) {

            CompletableFuture<User> futureUser = jda.retrieveUserById(rook.getDiscordId()).submit();
            futures.add(futureUser);
        }
        for (CompletableFuture<User> future : futures) {
            try {
                User user = future.get();
                if (user != null) {
                    rooks.append(user.getAsMention()).append("\n");
                } else {
                    rooks.append("Unknown User").append("\n");
                }
            } catch (InterruptedException |
                     ExecutionException e) {
                rooks.append("Error retrieving user").append("\n");
            }
        }
        return rooks.toString();
    }

    public void getTableData() throws IOException {
        Map<String, Integer> KAST = customMatch.calculateKAST();
        Map<String, Integer> ADR = customMatch.calculateADR();
        Map<String, Integer> HS = customMatch.calculateHSRate();
        Map<String, List<String>> players = customMatch.getPlayersData();
        List<Map<String, Integer>> fkfd = customMatch.calculateFKFD();
        Map<String, Integer> FK = fkfd.get(0);
        Map<String, Integer> FD = fkfd.get(1);
        List<ScoreboardPlayer> winTeamPlayers = new ArrayList<>();
        List<ScoreboardPlayer> loseTeamPlayers = new ArrayList<>();
        Map<String, Integer> roundsPlayers = customMatch.getRoundsPlayed();
        boolean isRedWin = false;
        if(customMatch.winningTeam().equals("Red")){
            isRedWin = true;
        }
        for (Map.Entry<String, List<String>> entry : players.entrySet()){
            String puuid = entry.getKey();
            String playerName = customMatch.getPlayerNameByPuuid(puuid);

            List<String> playerData = entry.getValue();
            String agent = playerData.get(0);
            String team = playerData.get(1);
            int acs = Integer.parseInt(playerData.get(2));
            int kills = Integer.parseInt(playerData.get(3));
            int deaths = Integer.parseInt(playerData.get(4));
            int assists = Integer.parseInt(playerData.get(5));
            double kda = deaths == 0 ? kills + assists : (double) (kills + assists) / deaths;
            int KDDiff = kills - deaths;
            int kast = KAST.get(puuid);
            int adr = ADR.get(puuid);
            int hs = HS.get(puuid);
            int fk = FK.get(puuid);
            int fd = FD.get(puuid);
            int FKFDDiff = fk - fd;
            int rounds = roundsPlayers.get(puuid);
            double kpr = (double) kills /rounds;
            double apr = (double) assists /rounds;
            double fkpr = (double) fk /rounds;
            double fdpr = (double) fd /rounds;
            double rating = customMatch.calculateValorantRating(kills, deaths, assists, acs, adr, kast, fk, fd);
            ScoreboardPlayer scoreboardPlayer = new ScoreboardPlayer(playerName, agent, team,acs, kills, deaths, assists, KDDiff, kast, adr, hs, fk, fd, FKFDDiff);
            if(isRedWin){
                if(Objects.equals(team, "Red")){
                    winTeamPlayers.add(scoreboardPlayer);
                    winningRooks.add(new Rook(puuid, playerName));
                    leaderboardPlayers.add(new LeaderboardPlayer(puuid, rookDB.getDiscordIdByPuuid(puuid), playerName, agent, rounds, 1, rating, acs, kda, kast, adr, kpr, apr, fkpr, fdpr, hs, 0, 0, kills, kills, deaths, assists, fk, fd, 1, 0));
                }else{
                    loseTeamPlayers.add(scoreboardPlayer);
                    losingRooks.add(new Rook(puuid, playerName));
                    leaderboardPlayers.add(new LeaderboardPlayer(puuid, rookDB.getDiscordIdByPuuid(puuid), playerName, agent, rounds, 1, rating,  acs, kda, kast, adr, kpr, apr, fkpr, fdpr, hs, 0, 0, kills, kills, deaths, assists, fk, fd, 0, 1));
                }
            }
            else{
                if(Objects.equals(team, "Blue")){
                    winTeamPlayers.add(scoreboardPlayer);
                    winningRooks.add(new Rook(puuid, playerName));
                    leaderboardPlayers.add(new LeaderboardPlayer(puuid, rookDB.getDiscordIdByPuuid(puuid), playerName, agent, rounds, 1,rating, acs, kda, kast, adr, kpr, apr, fkpr, fdpr, hs, 0, 0, kills, kills, deaths, assists, fk, fd, 1, 0));

                }else{
                    loseTeamPlayers.add(scoreboardPlayer);
                    losingRooks.add(new Rook(puuid, playerName));
                    leaderboardPlayers.add(new LeaderboardPlayer(puuid, rookDB.getDiscordIdByPuuid(puuid), playerName, agent, rounds, 1, rating, acs, kda, kast, adr, kpr, apr, fkpr, fdpr, hs, 0, 0, kills, kills, deaths, assists, fk, fd, 0, 1));

                }
            }
        }
        winTeamPlayers.sort(Comparator.comparingInt(ScoreboardPlayer::getACS).reversed());
        loseTeamPlayers.sort(Comparator.comparingInt(ScoreboardPlayer::getACS).reversed());

        TableGenerator tableGenerator = new TableGenerator(winTeamPlayers, loseTeamPlayers, ConfigLoader.getInstance().getProperty("SCOREBOARD.IMAGES.PATH")+queueId+".png");
        tableGenerator.generateTable();
    }


    public List<LeaderboardPlayer> getLeaderboardPlayers(){
        return this.leaderboardPlayers;
    }

}
