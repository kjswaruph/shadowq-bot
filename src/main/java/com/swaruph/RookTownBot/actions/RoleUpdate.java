package com.swaruph.RookTownBot.actions;

import java.util.Objects;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleUpdate extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        JDA jda = event.getJDA();
        Guild guild = jda.getGuildById(1309781276034596945L);
        assert guild != null;
        TextChannel channel = guild.getTextChannelById(1316360055263727667L);
        assert channel != null;
        Message message = channel.retrieveMessageById(1324803655328731287L).complete();
        Emoji emoji = Emoji.fromUnicode("✅");
        message.addReaction(emoji).queue();
    }

//    @Override
//    public void onGuildJoin(GuildJoinEvent event) {
//        Role role = (Role) event.getGuild().getRolesByName("not-verified", true);
//        event.getGuild().addRoleToMember(Objects.requireNonNull(event.getGuild().getMember()), role).complete();
//    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Role role = event.getGuild().getRolesByName("not-verified", false).getFirst();
        TextChannel channel = event.getGuild().getTextChannelsByName("welcome", true).getFirst();
        channel.sendMessage("Welcome to the server! "+ event.getUser().getAsMention()).queue();
        event.getGuild().addRoleToMember(event.getUser(), role).queue();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        Guild guild = event.getGuild();
        User user = event.getUser();
        Role verifiedRole = guild.getRoleById(1316360120103342090L);
        Role unverifiedRole = guild.getRoleById(1316360176147759134L);
        assert verifiedRole != null;
        assert user != null;
        assert unverifiedRole != null;
        event.getGuild().addRoleToMember(user, verifiedRole).queue();
        event.getGuild().removeRoleFromMember(user, unverifiedRole).queue();
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        Guild guild = event.getGuild();
        User user = event.getUser();
        Role verifiedRole = guild.getRoleById(1316360120103342090L);
        Role unverifiedRole = guild.getRoleById(1316360176147759134L);
        assert unverifiedRole != null;
        assert user != null;
        assert verifiedRole != null;
        event.getGuild().removeRoleFromMember(user, verifiedRole).queue();
        event.getGuild().addRoleToMember(user, unverifiedRole).queue();
    }
}
