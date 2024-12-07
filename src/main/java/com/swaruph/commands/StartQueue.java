package com.swaruph.commands;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.swaruph.managers.QueueManager;
import com.swaruph.model.ICommand;
import com.swaruph.actions.QueueAction;
import com.swaruph.model.Queue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class StartQueue implements ICommand {
    private QueueAction queueAction = new QueueAction();
    QueueManager queueManager = QueueManager.getInstance(queueAction);

    @Override
    public String getName() {
        return "startqueue";
    }

    @Override
    public String getDescription() {
        return "Start the queue for the game";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = List.of(
                new OptionData(OptionType.STRING, "startqueue", "10 man queue", true)
        );
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Queue queue = new Queue(1);
        queueManager.addQueuestoMap(queue);
        queue.setQueueName("RookTown Queue "+ queue.getQueueId());
        queue.setQueueAdmin(event.getUser());
        queue.setQueueStatus("Active");
        queue.setQueueType("Standard");
        System.out.println("In Start Queue");
        System.out.println(queueManager.getCurrentQueue().getQueueId());

        Button joinButton = Button.primary("join-button", "Join queue");
        Button leaveButton = Button.danger("leave-button", "Leave queue");
        EmbedBuilder embed =  new EmbedBuilder()
                .setTitle("RookTown Queue")
                .setColor(Color.GREEN)
                .addField("Players in queue", queueAction.getRooksList(queue) , false)
                .addField("Queue", queue.size()+ "/10", false)
                .setFooter("Queue started at " + getFormattedTime());

        event.replyEmbeds(embed.build())
             .addActionRow(joinButton, leaveButton)
             .queue();
    }

    public static String getFormattedTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        String time = now.format(formatter);

        if (now.toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
            return "Today at " + time;
        } else if (now.toLocalDate().equals(LocalDateTime.now().minusDays(1).toLocalDate())) {
            return "Yesterday at " + time;
        } else {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d 'at' h:mm a");
            return now.format(dateFormatter);
        }
    }
}