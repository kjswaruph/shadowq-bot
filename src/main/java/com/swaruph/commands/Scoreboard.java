package com.swaruph.commands;

import java.util.List;

import com.swaruph.model.ICommand;
import com.swaruph.actions.ScorecardProcessor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class Scoreboard implements ICommand {

    @Override
    public String getName() {
        return "scoreboard";
    }

    @Override
    public String getDescription() {
        return "Take a screenshot of the scoreboard";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = List.of(
                new OptionData(OptionType.ATTACHMENT, "screenshot", "The screenshot of the scoreboard", true),
                new OptionData(OptionType.STRING, "match", "The match for which the scoreboard is being submitted", true)
        );
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        ScorecardProcessor processor = new ScorecardProcessor();
        List<OptionMapping> options = event.getOptions();
        String match = options.get(1).getAsString();
        Message.Attachment screenshot = options.get(0).getAsAttachment();
        processor.processImage(screenshot, event.getChannel().asTextChannel());
        Message message = event.getHook().sendMessage("Processing the scoreboard").complete();
        // Process the scoreboard
        message.editMessage("Scoreboard processed for " + match).queue();;
        event.reply(message + match).queue();
    }
}
