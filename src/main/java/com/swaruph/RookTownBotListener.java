package com.swaruph;

import java.awt.Color;

import com.swaruph.actions.Add;
import com.swaruph.actions.QueueAction;
import com.swaruph.actions.Subtract;
import com.swaruph.commands.Hello;
import com.swaruph.managers.QueueManager;
import com.swaruph.model.Queue;
import com.swaruph.model.Rook;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static com.swaruph.commands.StartQueue.getFormattedTime;

public class RookTownBotListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        JDA jda = event.getJDA();
        jda.getPresence().setActivity(Activity.watching("Rook Town"));
        Guild guild = event.getJDA().getGuildById(1309781276034596945L);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        Emoji e = Emoji.fromUnicode("U+2705");
        MessageChannel channel = event.getChannel();
        channel.addReactionById(channel.getLatestMessageId(), e).queue();
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        QueueAction queueAction = new QueueAction();
        QueueManager queueManager = QueueManager.getInstance(queueAction);
        Queue queue = queueManager.getCurrentQueue();
        User user = event.getUser();
        Rook rook = new Rook(user);
        if(event.getComponentId().equals("join-button")) {
            if (!queueAction.isRookInQueue(queue, rook)) {
                queueAction.addRookInQueue(queue, rook);
                System.out.println("Current queue: " + queueAction.getRooksList(queue));
                System.out.println("Checking if user is in queue: " + rook.getUser().getId());
                event.editMessageEmbeds(new EmbedBuilder()
                        .setTitle("RookTown Queue" + queue.getQueueId())
                        .setColor(Color.GREEN)
                        .addField("Players in queue", queueAction.getRooksList(queue), false)
                        .addField("Queue", queue.size()+ "/10", false)
                        .setFooter("Queue started at "+ getFormattedTime())
                        .build())
                     .queue();
                event.reply("Joined queue").setEphemeral(true).queue();
            } else{
                event.reply("You are already in the queue!").setEphemeral(true).queue();
            }
        }else if (event.getComponentId().equals("leave-button")) {
            if (queueAction.isRookInQueue(queue, rook)) {
                queueAction.removeRookInPlayer(queue, rook);
                event.editMessageEmbeds(new EmbedBuilder()
                        .setTitle("RookTown Queue "+ queue.getQueueId())
                        .setColor(Color.GREEN)
                        .addField("Players in queue", queueAction.getRooksList(queue), false)
                        .addField("Queue", queue.size()+ "/10", false)
                        .setFooter("Queue started at "+getFormattedTime())
                        .build())
                     .queue();
                event.reply("Left queue").setEphemeral(true).queue();
            } else {
                event.reply("You are not in the queue!").setEphemeral(true).queue();
            }
        }
        else if(event.getComponentId().equals("add-button")) {
            event.editMessageEmbeds(new EmbedBuilder()
                    .setTitle("Addition")
                    .setDescription("Addition of two number")
                    .setColor(Color.GREEN)
                    .setFooter("Addition", event.getUser().getAvatarUrl())
                    .addField("Addition of"+ Hello.num1.getAsInt() +"and"+ Hello.num2.getAsInt(), "Result: "+Add.add(Hello.num1, Hello.num2), false)
                    .build())
                 .queue();
            event.reply("Addition operation complete").setEphemeral(true).queue();
        }
        else if(event.getComponentId().equals("sub-button")) {
            event.editMessageEmbeds(new EmbedBuilder()
                    .setTitle("Subtraction")
                    .setDescription("Subtraction of two number")
                    .setColor(Color.GREEN)
                    .setFooter("Subtraction", event.getUser().getAvatarUrl())
                    .addField("Subtraction of"+ Hello.num1+ "and"+Hello.num2, Subtract.subtract(Hello.num1, Hello.num2), false)
                    .build())
                 .queue();
            event.reply("Subtraction operation complete").setEphemeral(true).queue();
        }
    }

}
