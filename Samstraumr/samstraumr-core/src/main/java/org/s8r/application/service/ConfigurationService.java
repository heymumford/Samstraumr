/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.LoggerPort;

/**
 * Service for accessing and managing configuration settings.
 * 
 * <p>This service provides application-level operations for configuration access,
 * abstracting the details of the configuration storage mechanism. It follows
 * Clean Architecture principles by depending on the ConfigurationPort interface
 * rather than on specific infrastructure implementations.
 */
public class ConfigurationService {
    
    private final ConfigurationPort configurationPort;
    private final LoggerPort logger;
    
    /**
     * Constructs a new ConfigurationService.
     * 
     * @param configurationPort The configuration port to use
     * @param logger The logger to use
     */
    public ConfigurationService(ConfigurationPort configurationPort, LoggerPort logger) {
        this.configurationPort = configurationPort;
        this.logger = logger;
        
        logger.debug("ConfigurationService created");
    }
    
    /**
     * Gets a system configuration value.
     * 
     * @param key The configuration key
     * @return The configuration value, or empty if not found
     */
    public Optional<String> getConfigValue(String key) {
        logger.debug("Getting config value for key: {}", key);
        return configurationPort.getString(key);
    }
    
    /**
     * Gets a system configuration value with a default fallback.
     * 
     * @param key The configuration key
     * @param defaultValue The default value to return if the key is not found
     * @return The configuration value, or the default value if not found
     */
    public String getConfigValue(String key, String defaultValue) {
        String value = configurationPort.getString(key, defaultValue);
        
        if (value.equals(defaultValue)) {
            logger.debug("Using default value for config key {}: {}", key, defaultValue);
        } else {
            logger.debug("Found config value for key {}: {}", key, value);
        }
        
        return value;
    }
    
    /**
     * Gets all system configuration values with a certain prefix.
     * 
     * @param prefix The prefix for the configuration keys
     * @return A map of configuration values with keys that start with the prefix
     */
    public Map<String, String> getSystemConfiguration(String prefix) {
        logger.debug("Getting system configuration with prefix: {}", prefix);
        return configurationPort.getSubset(prefix);
    }
    
    /**
     * Checks if logging is enabled for a specific component.
     * 
     * @param componentId The component ID
     * @return True if logging is enabled for the component
     */
    public boolean isLoggingEnabled(String componentId) {
        String key = "component." + componentId + ".logging.enabled";
        return configurationPort.getBoolean(key, true);
    }
    
    /**
     * Gets the log level for a specific component.
     * 
     * @param componentId The component ID
     * @return The log level, or "INFO" if not specified
     */
    public String getLogLevel(String componentId) {
        String key = "component." + componentId + ".logging.level";
        return configurationPort.getString(key, "INFO");
    }
    
    /**
     * Gets feature flag values.
     * 
     * @return A map of feature flag names to their boolean values
     */
    public Map<String, Boolean> getFeatureFlags() {
        Map<String, String> featuresConfig = configurationPort.getSubset("feature.");
        Map<String, Boolean> featureFlags = new HashMap<>();
        
        for (Map.Entry<String, String> entry : featuresConfig.entrySet()) {
            String featureName = entry.getKey().substring("feature.".length());
            boolean enabled = "true".equalsIgnoreCase(entry.getValue()) || 
                             "yes".equalsIgnoreCase(entry.getValue()) || 
                             "1".equals(entry.getValue());
            
            featureFlags.put(featureName, enabled);
        }
        
        logger.debug("Retrieved {} feature flags", featureFlags.size());
        return featureFlags;
    }
    
    /**
     * Gets a list of enabled extensions.
     * 
     * @return A list of enabled extension names
     */
    public List<String> getEnabledExtensions() {
        return configurationPort.getStringList("extensions.enabled", List.of());
    }
    
    /**
     * Checks if a specific feature is enabled.
     * 
     * @param featureName The feature name
     * @return True if the feature is enabled
     */
    public boolean isFeatureEnabled(String featureName) {
        String key = "feature." + featureName;
        return configurationPort.getBoolean(key, false);
    }
}