package com.swaruph.RookTownBot.commands;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.swaruph.RookTownBot.config.ConfigLoader;
import com.swaruph.RookTownBot.config.ValorantConfig;
import com.swaruph.RookTownBot.manager.LeaderboardManager;
import com.swaruph.RookTownBot.manager.QueueManager;
import com.swaruph.RookTownBot.model.CustomMatch;
import com.swaruph.RookTownBot.model.Queue;
import com.swaruph.RookTownBot.model.Rook;
import com.swaruph.RookTownBot.model.Scoreboard;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.socketconnection.jva.ValorantAPI;
import net.socketconnection.jva.player.ValorantPlayer;
import org.jetbrains.annotations.NotNull;

import static com.swaruph.RookTownBot.RookTownBot.queueDB;
import static com.swaruph.RookTownBot.RookTownBot.rookDB;

public class StartQueue extends ListenerAdapter implements ICommand {

    @NotNull
    @Override
    public CommandData getCommandData() {
        return Commands.slash("start-queue", "Start a new queue");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event)  {

        int queueId;
        queueId = queueDB.getLastRowInQueue();

        int activeQueueCount = QueueManager.getInstance().getActiveQueueCount();
        if (activeQueueCount >= 5) {
            event.reply("Cannot start a new queue. There are already " + activeQueueCount + " active queues.").queue();
            return;
        }

        Queue queue = new Queue(queueId+1);
        queue.setQueueName("RookTown Queue "+ queue.getQueueId());
        queue.setQueueAdmin(event.getUser());
        queue.setQueueStatus(true);
        queue.setQueueType("Standard");

        queueDB.insertIntoQueue(queue.getQueueId(), queue.getQueueName(), queue.getQueueType(), queue.getQueueStatus(), queue.getQueueAdmin().getId());
        QueueManager.getInstance().addQueue(queue);

        Button joinButton = Button.primary("join-button-"+queue.getQueueId(), "Join queue");
        Button leaveButton = Button.danger("leave-button-"+queue.getQueueId(), "Leave queue");
        Button endButton = Button.danger("end-button-"+queue.getQueueId(), "End queue");

        EmbedBuilder embed =  new EmbedBuilder()
                .setTitle("Queue "+ queue.getQueueId())
                .setDescription("")
                .setColor(Color.GREEN)
                .addField("Players in queue", getRooksList(queue) , false)
                .addField("Queue", queue.size()+ "/10", false)
                .setFooter(getFormattedTime());

        event.replyEmbeds(embed.build())
             .addActionRow(joinButton, leaveButton, endButton)
             .queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        User user = event.getUser();
        Rook rook = new Rook(user);
        int queueId = getNumberFromString(event.getComponentId());
        Queue queue = QueueManager.getInstance().getQueue(queueId);

        if (queue == null) {
            event.reply("Queue not found.").setEphemeral(true).queue();
            return;
        }

        boolean isQueueAdmin = queue.getQueueAdmin().getId().equals(user.getId());

        if(event.getComponentId().equals("join-button-"+queueId)) {
            if (!isRookInQueue(queue, rook)) {
                addRookInQueue(queue, rook);
                event.deferEdit().queue(hook -> {
                    hook.editOriginalEmbeds(new EmbedBuilder()
                                .setTitle("Queue " + queue.getQueueId())
                                .setColor(Color.GREEN)
                                .addField("Players in queue", getRooksList(queue), false)
                                .addField("Queue", queue.size() + "/10", false)
                                .setFooter(getFormattedTime())
                                .build())
                        .queue();
                    hook.sendMessage("You joined the queue, Make sure you/party leader set \n 1. Match History on \n 2. Mode: Standard \n 3. Cheats off").setEphemeral(true).queue();
                });
            } else{
                event.reply("You are already in the queue!").setEphemeral(true).queue();
            }
        }else if (event.getComponentId().equals("leave-button-"+queueId)) {
            if (isRookInQueue(queue, rook)) {
                removeRookInPlayer(queue, rook);
                event.deferEdit().queue(hook -> {
                    hook.editOriginalEmbeds(new EmbedBuilder()
                                .setTitle("Queue " + queue.getQueueId())
                                .setColor(Color.GREEN)
                                .addField("Players in queue", getRooksList(queue), false)
                                .addField("Queue", queue.size() + "/10", false)
                                .setFooter(getFormattedTime())
                                .build())
                        .queue();
                    hook.sendMessage("Left queue").setEphemeral(true).queue();
                });
            } else {
                event.reply("You are not in the queue!").setEphemeral(true).queue();
            }
        } if (event.getComponentId().equals("end-button-" + queueId)) {
            if (isQueueAdmin && isRookInQueue(queue, rook)) {
                event.deferEdit().queue(hook -> {
                    try {
                        ValorantConfig valorantConfig = new ValorantConfig();
                        ValorantAPI valorantAPI = new ValorantAPI(valorantConfig.getToken());
                        String name = rookDB.getNameByDiscordId(queue.getQueueAdmin().getId());
                        String username = name.split("#")[0];
                        String tag = name.split("#")[1];
                        ValorantPlayer valorantPlayer = new ValorantPlayer(valorantAPI).fetchData(username, tag);
                        CustomMatch customMatch = new CustomMatch(valorantAPI, valorantPlayer);
                        String matchId = customMatch.getMatchID();
                        String mode = customMatch.getMatchMode();
                        if(!mode.equals("Standard")){
                            hook.sendMessage("Your last queue is "+mode+", play Standard mode").setEphemeral(true).queue();
                            return;
                        }
                        Scoreboard scoreboard = new Scoreboard(customMatch, queueId);
                        scoreboard.getTableData();

                        queueDB.setQueueStatus(queueId, false);
                        QueueManager.getInstance().removeQueue(queue.getQueueId());

                        hook.editOriginalEmbeds(new EmbedBuilder()
                                    .setTitle("Queue " + queue.getQueueId() + "\n" + customMatch.rounds())
                                    .setDescription("Map: " + customMatch.getMatchMap() + "\n" + "Server: " + customMatch.getMatchRegion())
                                    .addField("Team A", scoreboard.getWinningRooksAsString(), true)
                                    .addField("Team B", scoreboard.getLosingRooksAsString(), true)
                                    .setColor(Color.RED)
                                    .setFooter(getFormattedTime())
                                    .build())
                            .queue();
                        hook.sendMessage("Queue ended").setEphemeral(true).queue();
                        TextChannel resultChannel = Objects.requireNonNull(event.getGuild()).getTextChannelById(1318190080049025064L);

                        Path path = Path.of(ConfigLoader.getInstance().getProperty("SCOREBOARD.IMAGES.PATH") + queueId + ".png");
                        FileUpload file = FileUpload.fromData(path);
                        MessageEmbed embed = new EmbedBuilder()
                                .setTitle("Queue " + queue.getQueueId())
                                .setDescription(customMatch.rounds() + "\n" + "Map: " + customMatch.getMatchMap() + "\n" + "Server: " + customMatch.getMatchRegion())
                                .setColor(Color.WHITE)
                                .setImage("attachment://scoreboard_" + queueId + ".png")
                                .build();

                        resultChannel.sendFiles(file)
                                     .addEmbeds(embed)
                                     .queue();

                        LeaderboardManager leaderboardManager = new LeaderboardManager();
                        leaderboardManager.updateLeaderboardStats(scoreboard.getLeaderboardPlayers());

                    }catch (IOException ex) {
                        hook.sendMessage("An error occurred while processing the scoreboard.").setEphemeral(true).queue();
                        throw new RuntimeException(ex);
                    }
                });

            } else {
                event.reply("Cannot end queue as you are not inqueue/queue admin").setEphemeral(true).queue();
            }
        }
    }

    public void addRookInQueue(Queue currentQueue, Rook user) {
        if (!currentQueue.isFull()) {
            currentQueue.addRook(user);
        } else {
            throw new IllegalStateException("Queue is already full");
        }
    }

    public void removeRookInPlayer(Queue currentQueue, Rook user) {
        currentQueue.removeRook(user);
    }

    public boolean isRookInQueue(Queue currentQueue, Rook user) {

        return currentQueue.contains(user);
    }



    public String getRooksList(Queue currentQueue) {
        if (currentQueue.size() == 0) {
            return "No one is in the queue yet";
        }
        List<Rook> rooks = currentQueue.getRooks();
        StringBuilder playerList = new StringBuilder();
        for (Rook player : rooks) {
            playerList.append(player.getAsMention()).append("\n");
        }
        return playerList.toString();
    }

    public static String getFormattedTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' h:mm a");
        return now.format(dateTimeFormatter);
    }

    public static int getNumberFromString(String s) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(s);
        if (m.find()) {
            return Integer.parseInt(m.group());
        } else {
            throw new IllegalArgumentException("No number found in the string");
        }
    }

}