package com.marketlogic.meeting.booking.config;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;

/**
 * {@link ConfigReader} is responsible for instantiating Meeting Booking Program.
 * AppMain instantiates {@link ConfigReader}, {@link PropertiesConfiguration} for further processing.
 */
public class ConfigReader {

    private static final Logger logger = Logger.getLogger(ConfigReader.class);
    private static final ConfigReader configReader = new ConfigReader();
    private final PropertiesConfiguration config = new PropertiesConfiguration();

    private ConfigReader() {
    }

    public static ConfigReader getInstance() {
        return configReader;
    }

    public void readConfig(final String path) {
        try {
            config.read(new FileReader(new File(path)));
        } catch (Exception e) {
            logger.error("Exception in reading config file " + path, e);
        }
    }

    public String getStringProperty(final String key) {
        return config.getString(key);
    }

    public String getStringProperty(final String key, final String defaultValue) {
        return config.getString(key, defaultValue);
    }

    //TODO: getProperty Method to be implemented for other primitive data types
}
