package com.swaruph.RookTownBot.utils;


import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.swaruph.RookTownBot.model.Leaderboard;
import com.swaruph.RookTownBot.model.ScoreboardPlayer;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableGenerator{

    private static final Logger logger = LoggerFactory.getLogger(TableGenerator.class);

    private final String tableType;
    private List<ScoreboardPlayer> teamA;
    private List<ScoreboardPlayer> teamB;
    private List<Leaderboard> leaderboardPlayers;
    private final String outputFileName;

    public TableGenerator(List<ScoreboardPlayer> teamA, List<ScoreboardPlayer> teamB, String outputFileName) {
        this.tableType = "scoreboard";
        this.teamA = teamA;
        this.teamB = teamB;
        this.outputFileName = outputFileName;
    }

    public TableGenerator(List<Leaderboard> leaderboardPlayers, String outputFileName) {
        this.tableType = "leaderboard";
        this.leaderboardPlayers = leaderboardPlayers;
        this.outputFileName = outputFileName+getOutputFileName();
    }

    public Path generateTable() {
        if (tableType.equals("scoreboard")) {
            generateScoreboard();
        } else if (tableType.equals("leaderboard")) {
            generateLeaderboard();
        }
        return Path.of(outputFileName);
    }

    private void generateLeaderboard() {
        String dot = generateLeaderboardDOT();
        try {
            MutableGraph g = new Parser().read(dot);
            Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(outputFileName));

        }catch (Exception e){
            logger.error("Failed to generate leaderboard", e);
        }
    }

    private void generateScoreboard() {
        String dot = generateScoreboardDOT();
        try {
            MutableGraph g = new Parser().read(dot);
            Graphviz.fromGraph(g).render(Format.PNG).toFile(new File(outputFileName));
        }catch (Exception e){
            logger.error("Failed to generate scoreboard", e);
        }
    }

    private String generateScoreboardDOT() {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph G {\n");
        dot.append("    fontname=\"Helvetica,Arial,sans-serif\"\n");
        dot.append("    node [fontname=\"Helvetica,Arial,sans-serif\"]\n");
        dot.append("    edge [fontname=\"Helvetica,Arial,sans-serif\"]\n");
        dot.append("    a0 [shape=none label=<<TABLE BORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"5\">\n");
        dot.append("        <TR> <TD BGCOLOR=\"azure3\" HEIGHT=\"35\"></TD> <TD BGCOLOR=\"azure3\"><B>Team A</B></TD> <TD BGCOLOR=\"azure3\"><B>Agent</B></TD> <TD BGCOLOR=\"azure3\"><B>ACS</B></TD> <TD BGCOLOR=\"azure3\"><B>KDA</B></TD> <TD BGCOLOR=\"azure3\"><B>K</B></TD> <TD BGCOLOR=\"azure3\"><B>D</B></TD> <TD BGCOLOR=\"azure3\"><B>A</B></TD> <TD BGCOLOR=\"azure3\"><B>+/-</B></TD> <TD BGCOLOR=\"azure3\"><B>KAST</B></TD> <TD BGCOLOR=\"azure3\"><B>ADR</B></TD> <TD BGCOLOR=\"azure3\"><B>HS</B></TD> <TD BGCOLOR=\"azure3\"><B>FK</B></TD> <TD BGCOLOR=\"azure3\"><B>FD</B></TD> <TD BGCOLOR=\"azure3\"><B>+/-</B></TD></TR>\n");

        int i = 1;
        for (ScoreboardPlayer player : teamA) {
            String playerName = player.getScoreboardPlayerName();
            if (playerName.length() > 18) {
                playerName = playerName.substring(0, 18) + "..."; // Shorten long names
            }
            dot.append("        <TR>")
               .append("<TD>").append(i++).append("</TD>")
               .append("<TD>").append(playerName).append("</TD>")
               .append("<TD>").append(player.getAgent()).append("</TD>")
               .append("<TD>").append(player.getACS()).append("</TD>")
               .append("<TD>").append(calculateKDA(player)).append("</TD>")
               .append("<TD>").append(player.getKills()).append("</TD>")
               .append("<TD>").append(player.getDeaths()).append("</TD>")
               .append("<TD>").append(player.getAssists()).append("</TD>")
               .append("<TD>").append(player.getKDDiff()).append("</TD>")
               .append("<TD>").append(player.getKAST()).append("</TD>")
               .append("<TD>").append(player.getADR()).append("</TD>")
               .append("<TD>").append(player.getHS()).append("</TD>")
               .append("<TD>").append(player.getFK()).append("</TD>")
               .append("<TD>").append(player.getFD()).append("</TD>")
               .append("<TD>").append(player.getFK() - player.getFD()).append("</TD>")
               .append("</TR>\n");
        }
        dot.append("        <TR> <TD BGCOLOR=\"azure3\" HEIGHT=\"35\"></TD> <TD BGCOLOR=\"azure3\"><B>Team B</B></TD> <TD BGCOLOR=\"azure3\"><B>Agent</B></TD> <TD BGCOLOR=\"azure3\"><B>ACS</B></TD> <TD BGCOLOR=\"azure3\"><B>KDA</B></TD> <TD BGCOLOR=\"azure3\"><B>K</B></TD> <TD BGCOLOR=\"azure3\"><B>D</B></TD> <TD BGCOLOR=\"azure3\"><B>A</B></TD> <TD BGCOLOR=\"azure3\"><B>+/-</B></TD> <TD BGCOLOR=\"azure3\"><B>KAST</B></TD> <TD BGCOLOR=\"azure3\"><B>ADR</B></TD> <TD BGCOLOR=\"azure3\"><B>HS</B></TD> <TD BGCOLOR=\"azure3\"><B>FK</B></TD> <TD BGCOLOR=\"azure3\"><B>FD</B></TD> <TD BGCOLOR=\"azure3\"><B>+/-</B></TD></TR>\n");
        i = 0;
        for (ScoreboardPlayer player : teamB) {
            String playerName = player.getScoreboardPlayerName();
            if (playerName.length() > 18) {
                playerName = playerName.substring(0, 18) + "..."; // Shorten long names
            }
            dot.append("        <TR>")
               .append("<TD>").append(i++).append("</TD>")
               .append("<TD>").append(playerName).append("</TD>")
               .append("<TD>").append(player.getAgent()).append("</TD>")
               .append("<TD>").append(player.getACS()).append("</TD>")
               .append("<TD>").append(calculateKDA(player)).append("</TD>")
               .append("<TD>").append(player.getKills()).append("</TD>")
               .append("<TD>").append(player.getDeaths()).append("</TD>")
               .append("<TD>").append(player.getAssists()).append("</TD>")
               .append("<TD>").append(player.getKDDiff()).append("</TD>")
               .append("<TD>").append(player.getKAST()).append("</TD>")
               .append("<TD>").append(player.getADR()).append("</TD>")
               .append("<TD>").append(player.getHS()).append("</TD>")
               .append("<TD>").append(player.getFK()).append("</TD>")
               .append("<TD>").append(player.getFD()).append("</TD>")
               .append("<TD>").append(player.getFK() - player.getFD()).append("</TD>")
               .append("</TR>\n");
        }
        dot.append("    </TABLE>>];\n");
        dot.append("}");
        return dot.toString();
    }


    private String generateLeaderboardDOT() {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph G {\n");
        dot.append("    fontname=\"Helvetica,Arial,sans-serif\"\n");
        dot.append("    node [fontname=\"Helvetica,Arial,sans-serif\"]\n");
        dot.append("    edge [fontname=\"Helvetica,Arial,sans-serif\"]\n");
        dot.append("    a0 [shape=none label=<<TABLE border=\"1\" cellspacing=\"0\" cellpadding=\"5\">\n");
        dot.append("        <TR> <TD bgcolor=\"azure3\" height=\"35\"><B>Rank</B></TD> <TD bgcolor=\"azure3\"><B>Name</B></TD> <TD bgcolor=\"azure3\"><B>Agents</B></TD> <TD bgcolor=\"azure3\"><B>Rounds</B></TD> <TD bgcolor=\"azure3\"><B>Matches</B></TD> <TD bgcolor=\"azure3\"><B>Rating</B></TD> <TD bgcolor=\"azure3\"><B>ACS</B></TD> <TD bgcolor=\"azure3\"><B>KDA</B></TD> <TD bgcolor=\"azure3\"><B>ADR</B></TD> <TD bgcolor=\"azure3\"><B>KPR</B></TD> <TD bgcolor=\"azure3\"><B>APR</B></TD> <TD bgcolor=\"azure3\"><B>FKPR</B></TD> <TD bgcolor=\"azure3\"><B>FDPR</B></TD> <TD bgcolor=\"azure3\"><B>HS</B></TD> <TD bgcolor=\"azure3\"><B>CL</B></TD> <TD bgcolor=\"azure3\"><B>CLWP</B></TD> <TD bgcolor=\"azure3\"><B>KMAX</B></TD> <TD bgcolor=\"azure3\"><B>Kills</B></TD> <TD bgcolor=\"azure3\"><B>Deaths</B></TD> <TD bgcolor=\"azure3\"><B>Assists</B></TD> <TD bgcolor=\"azure3\"><B>FK</B></TD> <TD bgcolor=\"azure3\"><B>FD</B></TD> <TD bgcolor=\"azure3\"><B>Wins</B></TD> <TD bgcolor=\"azure3\"><B>Loses</B></TD></TR>\n");

        int rank = 1;
        for (Leaderboard leaderboardPlayer : leaderboardPlayers) {
            String playerName = leaderboardPlayer.getLeaderboardPlayerName();
            if (playerName.length() > 18) {
                playerName = playerName.substring(0, 18) + "..."; // Shorten long names
            }
            dot.append("        <TR>")
               .append("<TD>").append(rank++).append("</TD>")
               .append("<TD>").append(playerName).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getAgents()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getTotalRounds()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getTotalMatches()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getRating()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getACS()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getKDA()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getADR()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getKPR()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getAPR()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getFKPR()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getFDPR()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getHS()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getCL()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getCLWP()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getKMAX()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getKills()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getDeaths()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getAssists()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getFK()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getFD()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getWins()).append("</TD>")
               .append("<TD>").append(leaderboardPlayer.getLoses()).append("</TD>")
               .append("</TR>\n");
        }

        dot.append("    </TABLE>>];\n");
        dot.append("}");
        return dot.toString();
    }


    private String calculateKDA(ScoreboardPlayer player) {
        double kda = (player.getKills() + player.getAssists()) /
                (double) (player.getDeaths() == 0 ? 1 : player.getDeaths());
        return String.format("%.1f", kda);
    }

    private String getOutputFileName(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return "_"+now.format(formatter)+".png";
    }
}


