package com.swaruph.RookTownBot.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.swaruph.RookTownBot.model.LeaderboardPlayer;

public class RookDB {

    public void createRookTable() {
        String query = "CREATE TABLE IF NOT EXISTS rook (\n"
                + "rook_id integer PRIMARY KEY,\n"
                + "riot_id text NOT NULL,\n"
                + "gender text ,\n"
                + "age integer,\n"
                + "rook_rank text, \n"
                + "in_game_role text, \n"
                + "in_game_rank text, \n"
                + "agent_pool text \n"
                + ");";
        Database db = new Database();
        db.execute(query);
    }

    public void insertIntoRook(String puuid, String discordId, String name) {
        String query = "INSERT INTO rook (puuid, discord_id, name) VALUES (?, ?, ?)";
        try {
            Connection con = Database.connect();
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, puuid);
            pstmt.setString(2, discordId);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
            pstmt.close();
            con.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    public String getDiscordIdByPuuid(String puuid) {
        String discordId = null;
        String query = "SELECT discord_id FROM rook WHERE puuid = ?";
        try {
            Connection con = Database.connect();
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, puuid);
            ResultSet resultSet = pstmt.executeQuery();
            discordId = resultSet.getString("discord_id");
            pstmt.close();
            con.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        if(discordId==null){
            discordId = "1310486108483878983";
            return discordId;
        }
        return discordId;
    }

    public String getNameByDiscordId(String discordId) {
        String name;
        String query = "SELECT name FROM rook WHERE discord_id = ?";
        try {
            Connection con = Database.connect();
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, discordId);
            ResultSet resultSet = pstmt.executeQuery();
            name = resultSet.getString("name");
            pstmt.close();
            con.close();
            return name;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertIntoLeaderboard(String puuid) {
        String query = "INSERT INTO rook (puuid) VALUES (?)";
        try {
            Connection con = Database.connect();
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, puuid);
            pstmt.executeUpdate();
            pstmt.close();
            con.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateLeaderboardStats(LeaderboardPlayer leaderboard) {
        String sql =  "INSERT OR REPLACE INTO rook (puuid, discord_id, name, agents, totalRounds, totalMatches, rating, ACS, KDA, KAST, ADR, KPR, APR, FKPR, FDPR, HS, CL, CLWP, KMAX, kills, deaths, assists,FK, FD, wins, loses) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection con = Database.connect();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, leaderboard.getPuuid());
            pstmt.setString(2, leaderboard.getDiscordId());
            pstmt.setString(3, leaderboard.getLeaderboardPlayerName());
            pstmt.setString(4, leaderboard.getAgents());
            pstmt.setInt(5, leaderboard.getTotalRounds());
            pstmt.setInt(6, leaderboard.getTotalMatches());
            pstmt.setDouble(7, leaderboard.getRating());
            pstmt.setDouble(8, leaderboard.getACS());
            pstmt.setDouble(9, leaderboard.getKDA());
            pstmt.setInt(10, leaderboard.getKAST());
            pstmt.setDouble(11, leaderboard.getADR());
            pstmt.setDouble(12, leaderboard.getKPR());
            pstmt.setDouble(13, leaderboard.getAPR());
            pstmt.setDouble(14, leaderboard.getFKPR());
            pstmt.setDouble(15, leaderboard.getFDPR());
            pstmt.setInt(16, leaderboard.getHS());
            pstmt.setInt(17, leaderboard.getCL());
            pstmt.setDouble(18, leaderboard.getCLWP());
            pstmt.setInt(19, leaderboard.getKMAX());
            pstmt.setInt(20, leaderboard.getKills());
            pstmt.setInt(21, leaderboard.getDeaths());
            pstmt.setInt(22, leaderboard.getAssists());
            pstmt.setInt(23, leaderboard.getFK());
            pstmt.setInt(24, leaderboard.getFD());
            pstmt.setInt(25, leaderboard.getWins());
            pstmt.setInt(26, leaderboard.getLoses());
            pstmt.executeUpdate();
            pstmt.close();
            con.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }

    public LeaderboardPlayer getLeaderboardPlayer(String puuid) {
        String sql = "SELECT * FROM rook WHERE puuid = ?";
        LeaderboardPlayer leaderboardPlayer = new LeaderboardPlayer(puuid);
        try {
            Connection con = Database.connect();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, puuid);
            ResultSet resultSet = pstmt.executeQuery();
            leaderboardPlayer.setLeaderboardPlayerName(resultSet.getString("name"));
            leaderboardPlayer.setDiscordId(resultSet.getString("discord_id"));
            leaderboardPlayer.setAgents(resultSet.getString("agents"));
            leaderboardPlayer.setTotalRounds(resultSet.getInt("totalRounds"));
            leaderboardPlayer.setTotalMatches(resultSet.getInt("totalMatches"));
            leaderboardPlayer.setRating(resultSet.getDouble("rating"));
            leaderboardPlayer.setACS(resultSet.getDouble("ACS"));
            leaderboardPlayer.setKDA(resultSet.getDouble("KDA"));
            leaderboardPlayer.setKAST(resultSet.getInt("KAST"));
            leaderboardPlayer.setADR(resultSet.getDouble("ADR"));
            leaderboardPlayer.setKPR(resultSet.getDouble("KPR"));
            leaderboardPlayer.setAPR(resultSet.getDouble("APR"));
            leaderboardPlayer.setFKPR(resultSet.getDouble("FKPR"));
            leaderboardPlayer.setFDPR(resultSet.getDouble("FDPR"));
            leaderboardPlayer.setHS(resultSet.getInt("HS"));
            leaderboardPlayer.setCL(resultSet.getInt("CL"));
            leaderboardPlayer.setCLWP(resultSet.getDouble("CLWP"));
            leaderboardPlayer.setKMAX(resultSet.getInt("KMAX"));
            leaderboardPlayer.setKills(resultSet.getInt("kills"));
            leaderboardPlayer.setDeaths(resultSet.getInt("deaths"));
            leaderboardPlayer.setAssists(resultSet.getInt("assists"));
            leaderboardPlayer.setFK(resultSet.getInt("FK"));
            leaderboardPlayer.setFD(resultSet.getInt("FD"));
            leaderboardPlayer.setWins(resultSet.getInt("wins"));
            leaderboardPlayer.setLoses(resultSet.getInt("loses"));
            resultSet.close();
            pstmt.close();
            con.close();
        } catch (
                SQLException e) {
            throw new RuntimeException(e);
        }
        return leaderboardPlayer;
    }

    public List<LeaderboardPlayer> getAllLeaderboardStats() {
        String query = "SELECT * FROM rook";
        List<LeaderboardPlayer> leaderboardPlayers = new ArrayList<>();
        try {
            Connection con = Database.connect();
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                LeaderboardPlayer leaderboardPlayer = new LeaderboardPlayer(resultSet.getString("puuid"));
                leaderboardPlayer.setLeaderboardPlayerName(resultSet.getString("name"));
                leaderboardPlayer.setDiscordId(resultSet.getString("discord_id"));
                leaderboardPlayer.setAgents(resultSet.getString("agents"));
                leaderboardPlayer.setTotalRounds(resultSet.getInt("totalRounds"));
                leaderboardPlayer.setTotalMatches(resultSet.getInt("totalMatches"));
                leaderboardPlayer.setRating(resultSet.getDouble("rating"));
                leaderboardPlayer.setACS(resultSet.getDouble("ACS"));
                leaderboardPlayer.setKDA(resultSet.getDouble("KDA"));
                leaderboardPlayer.setKAST(resultSet.getInt("KAST"));
                leaderboardPlayer.setADR(resultSet.getDouble("ADR"));
                leaderboardPlayer.setKPR(resultSet.getDouble("KPR"));
                leaderboardPlayer.setAPR(resultSet.getDouble("APR"));
                leaderboardPlayer.setFKPR(resultSet.getDouble("FKPR"));
                leaderboardPlayer.setFDPR(resultSet.getDouble("FDPR"));
                leaderboardPlayer.setHS(resultSet.getInt("HS"));
                leaderboardPlayer.setCL(resultSet.getInt("CL"));
                leaderboardPlayer.setCLWP(resultSet.getDouble("CLWP"));
                leaderboardPlayer.setKMAX(resultSet.getInt("KMAX"));
                leaderboardPlayer.setKills(resultSet.getInt("kills"));
                leaderboardPlayer.setDeaths(resultSet.getInt("deaths"));
                leaderboardPlayer.setAssists(resultSet.getInt("assists"));
                leaderboardPlayer.setFK(resultSet.getInt("FK"));
                leaderboardPlayer.setFD(resultSet.getInt("FD"));
                leaderboardPlayer.setWins(resultSet.getInt("wins"));
                leaderboardPlayer.setLoses(resultSet.getInt("loses"));
                leaderboardPlayers.add(leaderboardPlayer);
            }
            resultSet.close();
            pstmt.close();
            con.close();
        } catch (
                SQLException e) {
            throw new RuntimeException(e);
        }
        return leaderboardPlayers;
    }

    public String getPlayerNameByPuuid(String puuid) {
        Connection con = Database.connect();
        String name = null;
        String query = "SELECT name FROM rook WHERE puuid = ?";
        try {
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, puuid);
            ResultSet resultSet = pstmt.executeQuery();
            name = resultSet.getString("name");
            resultSet.close();
            pstmt.close();
            con.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return name;
    }
}
