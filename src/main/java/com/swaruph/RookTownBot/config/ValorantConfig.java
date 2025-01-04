package com.swaruph.RookTownBot.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ValorantConfig {
    private Properties properties = new Properties();
    public ValorantConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public String getToken() {
        return properties.getProperty("HENRIK.DEV.KEY");
    }
}
