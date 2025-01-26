package com.swaruph.RookTownBot.config;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    private static ConfigLoader instance;
    private final Properties properties;

    private ConfigLoader() {
        properties = new Properties();
        try (InputStream input = ConfigLoader.class.getResourceAsStream("/config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties file not found in the classpath.");
            }
            properties.load(input);
        } catch (Exception e) {
            logger.error("Failed to load configuration", e);
            throw new RuntimeException("Unable to load configuration.", e);
        }
    }

    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}