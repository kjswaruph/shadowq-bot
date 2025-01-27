package com.swaruph.RookTownBot.config;

public class ConfigLoader {

    private static ConfigLoader instance;

    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    public String getProperty(String key) {
        return System.getenv(key);
    }
}