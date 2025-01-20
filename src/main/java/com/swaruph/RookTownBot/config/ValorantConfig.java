package com.swaruph.RookTownBot.config;

import static com.swaruph.RookTownBot.config.ConfigLoader.properties;

public class ValorantConfig {

    public String getToken() {
        return properties.getProperty("HENRIK.DEV.KEY");
    }

}
