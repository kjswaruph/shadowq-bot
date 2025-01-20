package com.swaruph.RookTownBot.config;

import static com.swaruph.RookTownBot.config.ConfigLoader.properties;

public class DiscordConfig {

    public String getToken() {
        return properties.getProperty("BOT.TOKEN");
    }

}