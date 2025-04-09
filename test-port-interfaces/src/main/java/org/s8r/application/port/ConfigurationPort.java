package org.s8r.application.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * Port interface for configuration management.
 * This interface defines operations for accessing and manipulating
 * configuration properties in the application.
 */
public interface ConfigurationPort {

    /**
     * Retrieves a string configuration value.
     *
     * @param key The configuration key
     * @return The value associated with the key, or empty if not found
     */
    Optional<String> getString(String key);

    /**
     * Retrieves a string configuration value with a default value.
     *
     * @param key The configuration key
     * @param defaultValue The default value to return if the key is not found
     * @return The value associated with the key, or the default value if not found
     */
    String getString(String key, String defaultValue);

    /**
     * Retrieves an integer configuration value.
     *
     * @param key The configuration key
     * @return The value associated with the key, or empty if not found or not a valid integer
     */
    Optional<Integer> getInteger(String key);

    /**
     * Retrieves an integer configuration value with a default value.
     *
     * @param key The configuration key
     * @param defaultValue The default value to return if the key is not found or not a valid integer
     * @return The value associated with the key, or the default value if not found or not a valid integer
     */
    int getInteger(String key, int defaultValue);

    /**
     * Retrieves a boolean configuration value.
     *
     * @param key The configuration key
     * @return The value associated with the key, or empty if not found or not a valid boolean
     */
    Optional<Boolean> getBoolean(String key);

    /**
     * Retrieves a boolean configuration value with a default value.
     *
     * @param key The configuration key
     * @param defaultValue The default value to return if the key is not found or not a valid boolean
     * @return The value associated with the key, or the default value if not found or not a valid boolean
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * Retrieves a list of strings configuration value.
     *
     * @param key The configuration key
     * @return The list of strings associated with the key, or empty list if not found
     */
    List<String> getStringList(String key);

    /**
     * Sets a configuration value.
     *
     * @param key The configuration key
     * @param value The value to set
     */
    void setProperty(String key, String value);

    /**
     * Removes a configuration property.
     *
     * @param key The configuration key to remove
     * @return true if the property was removed, false if it did not exist
     */
    boolean removeProperty(String key);

    /**
     * Checks if a configuration property exists.
     *
     * @param key The configuration key to check
     * @return true if the property exists, false otherwise
     */
    boolean hasProperty(String key);

    /**
     * Retrieves all configuration properties.
     *
     * @return A map of all configuration properties
     */
    Map<String, String> getAllProperties();

    /**
     * Loads configuration from a specific source.
     *
     * @param source The source identifier (file path, URL, etc.)
     * @return true if the configuration was loaded successfully, false otherwise
     */
    boolean loadConfiguration(String source);

    /**
     * Saves the current configuration to a specific destination.
     *
     * @param destination The destination identifier (file path, etc.)
     * @return true if the configuration was saved successfully, false otherwise
     */
    boolean saveConfiguration(String destination);

    /**
     * Gets a subset of properties with a common prefix.
     *
     * @param prefix The prefix to filter properties by
     * @return A map of properties that match the given prefix
     */
    Map<String, String> getPropertiesWithPrefix(String prefix);

    /**
     * Converts the current configuration to a Java Properties object.
     *
     * @return A Properties object representing the current configuration
     */
    Properties toProperties();

    /**
     * Clears all configuration properties.
     */
    void clear();
}