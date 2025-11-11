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
     * Enumeration of configuration change types.
     */
    enum ChangeType {
        /** A property was set or updated */
        SET,
        /** A property was removed */
        REMOVE,
        /** All properties were cleared */
        CLEAR,
        /** Multiple properties were loaded at once */
        LOAD
    }

    /**
     * Interface for listening to configuration changes.
     */
    interface ConfigurationChangeListener {
        /**
         * Called when a configuration property is changed.
         *
         * @param key The key of the property that changed
         * @param value The new value of the property (null if removed)
         * @param changeType The type of change that occurred
         */
        void onConfigurationChange(String key, String value, ChangeType changeType);
    }

    /**
     * Represents the result of a configuration operation.
     */
    interface ConfigurationResult {
        /**
         * @return true if the operation was successful, false otherwise
         */
        boolean isSuccess();

        /**
         * @return An optional error message if the operation failed
         */
        Optional<String> getErrorMessage();

        /**
         * @return An optional exception if the operation failed with an exception
         */
        Optional<Exception> getException();

        /**
         * Creates a successful result.
         *
         * @return A successful result
         */
        static ConfigurationResult success() {
            return new SimpleConfigurationResult(true, null, null);
        }

        /**
         * Creates a failure result with an error message.
         *
         * @param errorMessage The error message
         * @return A failure result
         */
        static ConfigurationResult failure(String errorMessage) {
            return new SimpleConfigurationResult(false, errorMessage, null);
        }

        /**
         * Creates a failure result with an exception.
         *
         * @param exception The exception that caused the failure
         * @return A failure result
         */
        static ConfigurationResult failure(Exception exception) {
            return new SimpleConfigurationResult(false, exception.getMessage(), exception);
        }

        /**
         * Creates a failure result with an error message and exception.
         *
         * @param errorMessage The error message
         * @param exception The exception that caused the failure
         * @return A failure result
         */
        static ConfigurationResult failure(String errorMessage, Exception exception) {
            return new SimpleConfigurationResult(false, errorMessage, exception);
        }
    }

    /**
     * Simple implementation of ConfigurationResult.
     */
    class SimpleConfigurationResult implements ConfigurationResult {
        private final boolean success;
        private final String errorMessage;
        private final Exception exception;

        /**
         * Creates a new SimpleConfigurationResult.
         *
         * @param success Whether the operation was successful
         * @param errorMessage The error message if the operation failed
         * @param exception The exception if the operation failed with an exception
         */
        SimpleConfigurationResult(boolean success, String errorMessage, Exception exception) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.exception = exception;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        @Override
        public Optional<String> getErrorMessage() {
            return Optional.ofNullable(errorMessage);
        }

        @Override
        public Optional<Exception> getException() {
            return Optional.ofNullable(exception);
        }
    }

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
     * Loads configuration from a specific source with detailed error information.
     *
     * @param source The source identifier (file path, URL, etc.)
     * @return A ConfigurationResult with detailed success/error information
     */
    ConfigurationResult loadConfigurationWithDetails(String source);

    /**
     * Saves the current configuration to a specific destination.
     *
     * @param destination The destination identifier (file path, etc.)
     * @return true if the configuration was saved successfully, false otherwise
     */
    boolean saveConfiguration(String destination);

    /**
     * Saves the current configuration to a specific destination with detailed error information.
     *
     * @param destination The destination identifier (file path, etc.)
     * @return A ConfigurationResult with detailed success/error information
     */
    ConfigurationResult saveConfigurationWithDetails(String destination);

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

    /**
     * Registers a listener for configuration changes.
     *
     * @param listener The listener to register
     * @return A unique identifier for the registered listener
     */
    String registerChangeListener(ConfigurationChangeListener listener);

    /**
     * Unregisters a previously registered configuration change listener.
     *
     * @param listenerId The identifier of the listener to unregister
     * @return true if the listener was unregistered, false if it was not found
     */
    boolean unregisterChangeListener(String listenerId);
}