package com.swaruph.RookTownBot.actions;


import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import com.swaruph.RookTownBot.model.Leaderboard;
import com.swaruph.RookTownBot.model.ScoreboardPlayer;

public class TableGenerator extends Application {

    private static String tableType;
    private static List<ScoreboardPlayer> teamA;
    private static List<ScoreboardPlayer> teamB;
    private static List<Leaderboard> leaderboardPlayers;
    private static String outputFileName;
    private static final CountDownLatch latch = new CountDownLatch(1);
    private static boolean isJavaFXLaunched = false;
    public static void generateTableImage(List<ScoreboardPlayer> teamAStats, List<ScoreboardPlayer> teamBStats, String fileName) {
        tableType = "scoreboard";
        teamA = teamAStats;
        teamB = teamBStats;
        outputFileName = fileName;
        if (!Platform.isFxApplicationThread()) {
            Platform.setImplicitExit(false);
            if (!isJavaFXLaunched) {
                new Thread(() -> Application.launch(TableGenerator.class)).start();
                isJavaFXLaunched = true;
            }else {
                Platform.runLater(() -> new TableGenerator().startNewTask());
            }
            try {
                // Wait for generation to complete
                if (!latch.await(30, TimeUnit.SECONDS)) {
                    System.err.println("Timeout while generating table image");
                }
            } catch (
                    InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while generating table image");
            }
        }
    }

    public void startNewTask() {
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void generateTableImage(List<Leaderboard> leaderboardPlayerStats, String fileName) {
        tableType = "leaderboard";
        leaderboardPlayers = leaderboardPlayerStats;
        outputFileName = fileName;
        if (!Platform.isFxApplicationThread()) {
            Platform.setImplicitExit(false);
            if (!isJavaFXLaunched) {
                new Thread(() -> Application.launch(TableGenerator.class)).start();
                isJavaFXLaunched = true;
            }else {
                Platform.runLater(() -> new TableGenerator().startNewTask());

            }

            try {
                // Wait for generation to complete
                if (!latch.await(30, TimeUnit.SECONDS)) {
                    System.err.println("Timeout while generating table image");
                }
            } catch (
                    InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while generating table image");
            }
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            // Create HTML with dynamic data
            String html = "";
            if (tableType.equals("scoreboard")) {
                html = generateScoreboardHTML();
            } else if (tableType.equals("leaderboard")) {
                html = generateLeaderboardHTML();
            }

            // Create WebView
            WebView webView = new WebView();
            webView.getEngine().loadContent(html);
            // Create scene
            VBox layout = new VBox(0); // Set spacing to 0 to avoid extra space
            layout.getChildren().add(webView);
            Scene scene = new Scene(layout);
            stage.setScene(scene);

            // Wait for page to load and take snapshot
            webView.getEngine().getLoadWorker().stateProperty().addListener((obs, oldDoc, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    Platform.runLater(() -> {
                        try {
                            // Add a delay to ensure content is fully rendered
                            PauseTransition pause = new PauseTransition(Duration.seconds(1));
                            pause.setOnFinished(event -> {
                                try {
                                    // Wait for the content to render
                                    double width = Double.parseDouble(webView.getEngine().executeScript("document.body.scrollWidth").toString());
                                    double height = Double.parseDouble(webView.getEngine().executeScript("document.body.scrollHeight").toString());
                                    webView.setPrefWidth(width);
                                    webView.setPrefHeight(height);
                                    stage.setWidth(width);
                                    stage.setHeight(height);

                                    // Take snapshot
                                    SnapshotParameters parameters = new SnapshotParameters();
                                    if (tableType.equals("scoreboard")) {
                                        double widthSize = Double.parseDouble(webView.getEngine().executeScript("document.getElementById(\"table\").offsetWidth").toString());
                                        parameters.setViewport(new Rectangle2D(0, 0, widthSize+20, 500));
                                    } else if (tableType.equals("leaderboard")) {
                                        double heightSize = Double.parseDouble(webView.getEngine().executeScript("document.getElementById(\"table\").offsetHeight").toString());
                                        parameters.setViewport(new Rectangle2D(0, 0, 1920, heightSize+20));
                                    }
                                    WritableImage snapshot = webView.snapshot(parameters, null);

                                    // Save image
                                    File outputFile = new File(outputFileName);
                                    ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", outputFile);

                                } catch (Exception e) {
                                    System.err.println("Error saving image: " + e.getMessage());
                                    e.printStackTrace();
                                } finally {
                                    // Clean up
                                    stage.close();
                                    latch.countDown();
                                    Platform.exit();
                                }
                            });
                            pause.play();

                        } catch (Exception e) {
                            System.err.println("Error adjusting stage height: " + e.getMessage());
                            e.printStackTrace();
                            latch.countDown();
                            Platform.exit();
                        }
                    });
                }
            });

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private String generateScoreboardHTML() {
        StringBuilder html = new StringBuilder();

        // Add DOCTYPE and HTML start
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");

        // Add head and CSS styles
        html.append("<head>\n");
        html.append("    <style type=\"text/css\">\n");
        html.append("        .tg {\n");
        html.append("            border-collapse: collapse;\n");
        html.append("            border-spacing: 0;\n");
        html.append("        }\n");
        html.append("        .tg td {\n");
        html.append("            border-color: black;\n");
        html.append("            border-style: solid;\n");
        html.append("            border-width: 1px;\n");
        html.append("            font-family: Arial, sans-serif;\n");
        html.append("            font-size: 14px;\n");
        html.append("            overflow: hidden;\n");
        html.append("            padding: 10px 5px;\n");
        html.append("            word-break: normal;\n");
        html.append("        }\n");
        html.append("        .tg th {\n");
        html.append("            border-color: black;\n");
        html.append("            border-style: solid;\n");
        html.append("            border-width: 1px;\n");
        html.append("            font-family: Arial, sans-serif;\n");
        html.append("            font-size: 14px;\n");
        html.append("            font-weight: normal;\n");
        html.append("            overflow: hidden;\n");
        html.append("            padding: 10px 5px;\n");
        html.append("            word-break: normal;\n");
        html.append("        }\n");
        html.append("        .tg .player {\n");
        html.append("            font-family: Arial, sans-serif;\n");
        html.append("            text-align: left;\n");
        html.append("            vertical-align: middle;\n");
        html.append("        }\n");
        html.append("        .tg .header {\n");
        html.append("            background-color: #656565;\n");
        html.append("            border-color: #000000;\n");
        html.append("            color: #ffffff;\n");
        html.append("            font-family: Arial, sans-serif;\n");
        html.append("            text-align: center;\n");
        html.append("            vertical-align: middle;\n");
        html.append("        }\n");
        html.append("    </style>\n");
        html.append("</head>\n");

        // Start body and table
        html.append("<body>\n");
        html.append("<table id=\"table\" class=\"tg\">\n");
        html.append("    <tbody>\n");

            addTeamHeaders(html, "Team A");
            addTeamRows(html, teamA);

            addTeamHeaders(html, "Team B");
            addTeamRows(html, teamB);


        // Close table, body, and HTML
        html.append("    </tbody>\n");
        html.append("</table>\n");
        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }

    private void addTeamHeaders(StringBuilder html, String teamName) {
        String[] headers = {"", teamName, "Agent", "ACS", "KDA", "K", "D", "A", "+/-",
                "KAST", "ADR", "HS%", "FK", "FD", "+/-"};

        html.append("    <tr>\n");
        for (String header : headers) {
            html.append("        <td class=\"header\">\n");
            html.append("            ").append(header).append("\n");
            html.append("        </td>\n");
        }
        html.append("    </tr>\n");
    }

    private void addTeamRows(StringBuilder html, List<ScoreboardPlayer> team) {
        int i = 1;
        for (ScoreboardPlayer player : team) {
            html.append("    <tr>\n");
            html.append("        <td class=\"player\">").append(i++).append("</td>\n");
            html.append("        <td class=\"player\">").append(player.getScoreboardPlayerName()).append("</td>\n");
            html.append("        <td class=\"player\">").append(player.getAgent()).append("</td>\n");
            html.append("        <td class=\"player\">").append(player.getACS()).append("</td>\n");
            html.append("        <td class=\"player\">").append(calculateKDA(player)).append("</td>\n");
            html.append("        <td class=\"player\">").append(player.getKills()).append("</td>\n");
            html.append("        <td class=\"player\">").append(player.getDeaths()).append("</td>\n");
            html.append("        <td class=\"player\">").append(player.getAssists()).append("</td>\n");
            html.append("        <td class=\"player\">").append(player.getKDDiff()).append("</td>\n");
            html.append("        <td class=\"player\">").append(player.getKAST()).append("</td>\n");
            html.append("        <td class=\"player\">").append(player.getADR()).append("</td>\n");
            html.append("        <td class=\"player\">").append(player.getHS()).append("</td>\n");
            html.append("        <td class=\"player\">").append(player.getFK()).append("</td>\n");
            html.append("        <td class=\"player\">").append(player.getFD()).append("</td>\n");
            html.append("        <td class=\"player\">").append(player.getKDDiff()).append("</td>\n");
            html.append("    </tr>\n");
        }
    }

    private String generateLeaderboardHTML() {
        StringBuilder html = new StringBuilder();

        // Add DOCTYPE and HTML start
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");

        // Add head and CSS styles
        html.append("<head>\n");
        html.append("    <meta charset=\"UTF-8\"/>\n");
        html.append("    <style>\n");
        html.append("        table {\n");
        html.append("            border: medium solid #6495ed;\n");
        html.append("            border-collapse: collapse;\n");
        html.append("            width: 100% ;\n");
        html.append("        }\n");
        html.append("        th {\n");
        html.append("            font-family: monospace;\n");
        html.append("            border: thin solid #6495ed;\n");
        html.append("            padding: 5px;\n");
        html.append("            background-color: #D0E3FA;\n");
        html.append("            text-align: center;\n");
        html.append("        }\n");
        html.append("        td {\n");
        html.append("            font-family: sans-serif;\n");
        html.append("            border: thin solid #6495ed;\n");
        html.append("            padding: 5px;\n");
        html.append("            text-align: center;\n");
        html.append("        }\n");
        html.append("        .odd {\n");
        html.append("            background: #e8edff;\n");
        html.append("        }\n");
        html.append("    </style>\n");
        html.append("</head>\n");

        // Start body and table
        html.append("<body>\n");
        html.append("<table id=\"table\">\n");

        // Add headers
        addTableHeaders(html);

        // Add player rows
        addLeaderboardRow(html, leaderboardPlayers);

        // Close table, body, and HTML
        html.append("</table>\n");
        html.append("</body>\n");
        html.append("</html>");

        return html.toString();
    }

    private void addTableHeaders(StringBuilder html) {
        String[] headers = {"Rank", "Name", "Agents", "Rounds", "Matches", "Rating",
                "ACS", "KDA", "KAST", "ADR", "KPR", "APR", "FKPR", "FDPR",
                "HS", "CL", "CLWP", "KMAX", "Kills", "Deaths", "Assists",
                "FK", "FD", "Wins", "Loses"};

        html.append("    <tr>\n");
        for (String header : headers) {
            html.append("        <th>").append(header).append("</th>\n");
        }
        html.append("    </tr>\n");
    }

    private void addLeaderboardRow(StringBuilder html, List<Leaderboard> players) {
        for (int i = 0; i < players.size(); i++) {
            Leaderboard player = players.get(i);
            String rowClass = (i % 2 == 0) ? "" : " class=\"odd\"";
            html.append("    <tr").append(rowClass).append(">\n");
            html.append("        <td>").append(i + 1).append("</td>\n");
            html.append("        <td>").append(player.getLeaderboardPlayerName()).append("</td>\n");
            html.append("        <td>").append(player.getAgents()).append("</td>\n");
            html.append("        <td>").append(player.getTotalRounds()).append("</td>\n");
            html.append("        <td>").append(player.getTotalMatches()).append("</td>\n");
            html.append("        <td>").append(player.getRating()).append("</td>\n");
            html.append("        <td>").append(player.getACS()).append("</td>\n");
            html.append("        <td>").append(player.getKDA()).append("</td>\n");
            html.append("        <td>").append(player.getKAST()).append("</td>\n");
            html.append("        <td>").append(player.getADR()).append("</td>\n");
            html.append("        <td>").append(player.getKPR()).append("</td>\n");
            html.append("        <td>").append(player.getAPR()).append("</td>\n");
            html.append("        <td>").append(player.getFKPR()).append("</td>\n");
            html.append("        <td>").append(player.getFDPR()).append("</td>\n");
            html.append("        <td>").append(player.getHS()).append("</td>\n");
            html.append("        <td>").append(player.getCL()).append("</td>\n");
            html.append("        <td>").append(player.getCLWP()).append("</td>\n");
            html.append("        <td>").append(player.getKMAX()).append("</td>\n");
            html.append("        <td>").append(player.getKills()).append("</td>\n");
            html.append("        <td>").append(player.getDeaths()).append("</td>\n");
            html.append("        <td>").append(player.getAssists()).append("</td>\n");
            html.append("        <td>").append(player.getFK()).append("</td>\n");
            html.append("        <td>").append(player.getFD()).append("</td>\n");
            html.append("        <td>").append(player.getWins()).append("</td>\n");
            html.append("        <td>").append(player.getLoses()).append("</td>\n");
            html.append("    </tr>\n");
        }
    }

    private String calculateKDA(ScoreboardPlayer player) {
        double kda = (player.getKills() + player.getAssists()) /
                (double) (player.getDeaths() == 0 ? 1 : player.getDeaths());
        return String.format("%.1f", kda);
    }
}


