package com.swaruph.RookTownBot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public interface ICommand {

    @NotNull
    CommandData  getCommandData();

    void execute(@NotNull SlashCommandInteractionEvent event);
}
