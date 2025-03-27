package com.swaruph.RookTownBot.commands.queue;

import java.awt.Color;
import java.nio.file.Path;
import java.util.List;

import com.swaruph.RookTownBot.commands.ICommand;
import com.swaruph.RookTownBot.config.ConfigLoader;
import com.swaruph.RookTownBot.utils.TableGenerator;
import com.swaruph.RookTownBot.manager.LeaderboardManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

public class Leaderboard implements ICommand {

    @NotNull
    @Override
    public CommandData getCommandData() {
        OptionData sortBy = new OptionData(OptionType.STRING, "sort_by", "Rating/KDA/ADR/HS", false);
        OptionData size = new OptionData(OptionType.INTEGER, "size", "Enter the size: ", false);
        return Commands.slash("leaderboard", "Get leaderboard").addOptions(sortBy, size);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {


        event.deferReply().queue();

        LeaderboardManager leaderboardManager = new LeaderboardManager();

        OptionMapping sortBy = event.getOption("sort_by");
        OptionMapping size = event.getOption("size");


        List<com.swaruph.RookTownBot.model.Leaderboard> players;

        if(sortBy!=null && size!=null) {
           players = leaderboardManager.getLeaderboard(sortBy.getAsString(), size.getAsInt());
        }else{
           players = leaderboardManager.getLeaderboard("rating", 10);
        }


        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < players.size(); i++) {
            if(i>2){
                break;
            }
            if (i==0){
                sb.append("<a:posFirst:1324247637792391250> ").append("<@").append(players.get(0).getDiscordId()).append("> \n");
            }
            if (i==1){
                sb.append("<a:posSecond:1324247766024589352> ").append("<@").append(players.get(1).getDiscordId()).append("> \n");
            }
            if (i==2){
                sb.append("<a:posThird:1324247878188662795> ").append("<@").append(players.get(2).getDiscordId()).append(">");
            }
        }

        TableGenerator tableGenerator = new TableGenerator(players, ConfigLoader.getInstance().getProperty("LEADERBOARD_IMAGES_PATH"));
        Path path = tableGenerator.generateTable();
        FileUpload image = FileUpload.fromData(path);

        MessageEmbed embed = new EmbedBuilder()
                .setTitle("Leaderboard")
                .setDescription(sb.toString())
                .setColor(Color.RED)
                .setImage("attachment://" + path.getFileName().toString())
                .build();

        event.getHook().sendMessageEmbeds(embed).addFiles(image).queue();

    }


}
