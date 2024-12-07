package com.swaruph;

import java.io.IOException;

import com.swaruph.actions.ScorecardProcessor;
import com.swaruph.commands.Hello;
import com.swaruph.commands.Scoreboard;
import com.swaruph.commands.StartQueue;
import com.swaruph.config.DiscordConfig;
import com.swaruph.managers.CommandManager;
import com.swaruph.managers.QueueManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static void main(String[] args) throws IOException {

        DiscordConfig config = new DiscordConfig();

        JDA api = JDABuilder.createDefault(config.getToken())
                            .enableIntents(GatewayIntent.GUILD_MESSAGES,GatewayIntent.GUILD_MESSAGE_REACTIONS,GatewayIntent.MESSAGE_CONTENT)
                            .build();

        api.addEventListener(new RookTownBotListener());
        api.addEventListener(new ScorecardProcessor());
        CommandManager commandManager = new CommandManager();
        commandManager.registerCommand(new Scoreboard());
        commandManager.registerCommand(new StartQueue());
        commandManager.registerCommand(new Hello());
        api.addEventListener(commandManager);
    }
}
