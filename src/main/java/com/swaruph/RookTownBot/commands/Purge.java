package com.swaruph.RookTownBot.commands;

import java.util.Objects;

import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

public class Purge implements ICommand{

    @NotNull
    @Override
    public CommandData getCommandData() {
        OptionData optionData = new OptionData(OptionType.INTEGER, "amount", "The amount of messages to purge").setRequired(false);
        return Commands.slash("purge", "Purge messages from a channel").addOptions(optionData);
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        Role role = Objects.requireNonNull(event.getGuild()).getRoleById(1324703647501783162L);
        if (event.getGuild().getSelfMember().getRoles().contains(role)){
            event.reply("You don't have permission to purge messages").queue();
            return;
        }
        OptionMapping amount = event.getOption("amount");
        if (amount.getAsInt()>100){
            event.reply("You can only purge upto 100 messages at a time").queue();
            return;
        }
        TextChannel channel = event.getChannel().asTextChannel();
        MessageHistory history = channel.getHistory();
        assert amount != null;
        history.retrievePast(amount.getAsInt()).queue(channel::purgeMessages);
        event.reply("Purged "+amount.getAsInt()).queue();
    }
}
