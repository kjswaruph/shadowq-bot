package com.swaruph.RookTownBot;


import com.swaruph.RookTownBot.commands.util.Help;
import com.swaruph.RookTownBot.database.QueueDB;
import com.swaruph.RookTownBot.database.RookDB;
import com.swaruph.RookTownBot.events.RoleUpdateonMemberJoin;
import com.swaruph.RookTownBot.commands.util.GetStarted;
import com.swaruph.RookTownBot.commands.queue.Leaderboard;
import com.swaruph.RookTownBot.commands.moderation.Purge;
import com.swaruph.RookTownBot.commands.queue.StartQueue;
import com.swaruph.RookTownBot.config.DiscordConfig;
import com.swaruph.RookTownBot.manager.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;


public class RookTownBot extends ListenerAdapter {

    private static RookTownBot instance;
    
    private final JDA jda;

    public static RookDB rookDB;
    public static QueueDB queueDB;
    
    public RookTownBot(){

        rookDB = new RookDB();
        queueDB = new QueueDB();
        final GetStarted getStarted = new GetStarted();
        final StartQueue startQueue = new StartQueue();
        final Leaderboard leaderboard = new Leaderboard();
        final Purge purge = new Purge();
        final RoleUpdateonMemberJoin roleUpdateonMemberJoin = new RoleUpdateonMemberJoin();
        final Help help = new Help();
        final CommandManager commandManager = new CommandManager();
        commandManager.addCommands(getStarted, startQueue, leaderboard, purge, help);

        DiscordConfig discordConfig = new DiscordConfig();
        jda = JDABuilder.createDefault(discordConfig.getToken())
                        .enableIntents(GatewayIntent.GUILD_MESSAGES,GatewayIntent.GUILD_MESSAGE_REACTIONS,GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                        .addEventListeners(commandManager, startQueue, getStarted)
                        .addEventListeners(roleUpdateonMemberJoin)
                        .setActivity(Activity.watching("RookTown"))
                        .build();
        instance = this;

    }

    @NotNull
    public JDA getJDA() {
        return jda;
    }

    public static RookTownBot getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        new RookTownBot();
    }

}