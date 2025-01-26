package com.swaruph.RookTownBot.config;

import io.github.cdimascio.dotenv.Dotenv;

public class ConfigLoader {

    private static ConfigLoader instance;
    private final Dotenv dotenv;

    private ConfigLoader(){
        dotenv = Dotenv.load();
    }

    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    public String getProperty(String key) {
        return dotenv.get(key);
    }
}