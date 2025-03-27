package com.swaruph.RookTownBot.commands.util;

import java.awt.Color;

import com.swaruph.RookTownBot.commands.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

public class Help implements ICommand {

    @Override
    public @NotNull CommandData getCommandData() {
        return Commands.slash("help", "Returns info of all commands");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        String description = """
                Here is the list of commands!
                For more info on a specific command, use rt help {command}
                Need more help? Ask mods
                """;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(" | Commands List", null, event.getUser().getAvatarUrl());
        embed.setDescription(description);
        embed.setColor(Color.RED);
        embed.addField("General", "`help` `ping`", false);
        embed.addField("Account", "`get-started`", false);
        embed.addField("Queue", "`start-queue` `leaderboard`", false);
        embed.addField("Moderation", "`purge`", false);
        event.replyEmbeds(embed.build()).queue();

    }
}
