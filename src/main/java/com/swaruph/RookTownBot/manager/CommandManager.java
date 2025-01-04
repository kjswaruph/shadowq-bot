package com.swaruph.RookTownBot.manager;

import java.util.HashMap;
import java.util.Map;

import com.swaruph.RookTownBot.commands.ICommand;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public class CommandManager extends ListenerAdapter {

    private final Map<String, ICommand> commands = new HashMap<>();
    private boolean updated = false;

    public void addCommands(ICommand... slashCommands) {
        for (ICommand slashCommand : slashCommands) {
            final CommandData commandData = slashCommand.getCommandData();
            commands.put(commandData.getName(), slashCommand);
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        if (updated) return;
        updated = true;

        event.getJDA().updateCommands()
             .addCommands(commands.values().stream().map(ICommand::getCommandData).toList())
             .queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // Get our slash command by name
        final ICommand slashCommand = commands.get(event.getName());
        if (slashCommand == null) {
            event.reply("This command was not found")
                 .setEphemeral(true)
                 .queue();
            return;
        }
        slashCommand.execute(event);
    }
}
