package com.swaruph.commands;

import java.awt.Color;
import java.util.List;

import com.swaruph.model.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Hello implements ICommand {

    public static OptionMapping num1;
    public static OptionMapping num2;

    @Override
    public String getName() {
        return "hello";
    }

    @Override
    public String getDescription() {
        return "sends how are you";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.INTEGER, "num1", "first number", true),
                new OptionData(OptionType.INTEGER, "num2", "second number", true)
        );
    }


    @Override
    public void execute(SlashCommandInteractionEvent event) {

        Button addButton = Button.primary("add-button", "Addition");
        Button subButton = Button.danger("sub-button", "Subtraction");

        num1 = event.getOption("num1");
        num2 = event.getOption("num2");


        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Hello");
        embed.setDescription("How are you?");
        embed.setColor(Color.YELLOW);
        embed.setFooter("Hello", event.getUser().getAvatarUrl());
        embed.addField("Select you operation", "Addition, Subtraction, Multiplication, Division", false);
        embed.addField("Result", "0", false);


        event.reply("Hello")
             .addEmbeds(embed.build())
             .addActionRow(addButton, subButton)
             .queue();
    }
}
