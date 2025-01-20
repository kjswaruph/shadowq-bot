package com.swaruph.RookTownBot.events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleUpdateonMemberJoin extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();
        Guild guild = jda.getGuildById(1309781276034596945L);
        TextChannel channel = guild.getTextChannelById(1316360055263727667L);
        Message message = channel.retrieveMessageById(1324803655328731287L).complete();
        Emoji emoji = Emoji.fromUnicode("✅");
        message.addReaction(emoji).queue();
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Role role = event.getGuild().getRolesByName("not-verified", false).getFirst();
        TextChannel channel = event.getGuild().getTextChannelsByName("welcome", true).getFirst();
        channel.sendMessage("Welcome to the server! "+ event.getUser().getAsMention()).queue();
        event.getGuild().addRoleToMember(event.getUser(), role).queue();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getMessageIdLong() != 1324803655328731287L){return;}
        Guild guild = event.getGuild();
        User user = event.getUser();
        Role verifiedRole = guild.getRoleById(1316360120103342090L);
        Role unverifiedRole = guild.getRoleById(1316360176147759134L);
        event.getGuild().addRoleToMember(user, verifiedRole).queue();
        event.getGuild().removeRoleFromMember(user, unverifiedRole).queue();
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if(event.getMessageIdLong() != 1324803655328731287L){return;}
        Guild guild = event.getGuild();
        User user = event.getUser();

        Role verifiedRole = guild.getRoleById(1316360120103342090L);
        Role unverifiedRole = guild.getRoleById(1316360176147759134L);

        event.getGuild().removeRoleFromMember(user, verifiedRole).queue();
        event.getGuild().addRoleToMember(user, unverifiedRole).queue();
    }
}
