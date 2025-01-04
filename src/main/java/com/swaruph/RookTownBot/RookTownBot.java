package com.swaruph.RookTownBot;


import com.swaruph.RookTownBot.actions.RoleUpdate;
import com.swaruph.RookTownBot.commands.GetStarted;
import com.swaruph.RookTownBot.commands.Leaderboard;
import com.swaruph.RookTownBot.commands.Purge;
import com.swaruph.RookTownBot.commands.StartQueue;
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
    
    public RookTownBot(){

        final GetStarted getStarted = new GetStarted();
        final StartQueue startQueue = new StartQueue();
        final Leaderboard leaderboard = new Leaderboard();
        final Purge purge = new Purge();
        final RoleUpdate roleUpdate = new RoleUpdate();

        final CommandManager commandManager = new CommandManager();
        commandManager.addCommands(getStarted, startQueue, leaderboard, purge);

        DiscordConfig discordConfig = new DiscordConfig();
        jda = JDABuilder.createDefault(discordConfig.getToken())
                        .enableIntents(GatewayIntent.GUILD_MESSAGES,GatewayIntent.GUILD_MESSAGE_REACTIONS,GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS)
                        .addEventListeners(commandManager, startQueue, getStarted)
                        .addEventListeners(roleUpdate)
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
