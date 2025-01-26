package org.samstraumr.tube.config;

import java.util.Properties;

public class TubeConfig {
    private final Properties properties;

    public TubeConfig() {
        this.properties = new Properties();
        loadDefaultConfigurations();
    }

    // Method to load default configurations
    private void loadDefaultConfigurations() {
        properties.setProperty("monitoring.frequency", "1 second");
        properties.setProperty("resource.usage.threshold.memory", "85%");
        properties.setProperty("resource.usage.threshold.cpu", "75%");
        properties.setProperty("health.check.interval", "5 minutes");
        properties.setProperty("adaptation.rate.default", "1.0");
        // Add more default configurations as needed
    }

    // Method to get a configuration value
    public String getConfigValue(String key) {
        return properties.getProperty(key);
    }

    // Method to set a configuration value
    public void setConfigValue(String key, String value) {
        properties.setProperty(key, value);
    }

    // Method to display all configurations
    public void displayConfigurations() {
        System.out.println("Current Tube Configurations:");
        properties.forEach((key, value) -> System.out.printf("%s: %s%n", key, value));
    }

    // Additional configuration-related functionality can be added here
}
