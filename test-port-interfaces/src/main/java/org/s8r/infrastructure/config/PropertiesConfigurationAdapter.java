package org.s8r.infrastructure.config;

import org.s8r.application.port.ConfigurationPort;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of the ConfigurationPort interface using Java Properties.
 * This adapter manages configuration properties in memory with the ability
 * to load from and save to properties files.
 */
public class PropertiesConfigurationAdapter implements ConfigurationPort {

    private final Map<String, String> properties;

    /**
     * Creates a new PropertiesConfigurationAdapter with an empty properties map.
     */
    public PropertiesConfigurationAdapter() {
        this.properties = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new PropertiesConfigurationAdapter with the given properties.
     *
     * @param properties Initial properties
     */
    public PropertiesConfigurationAdapter(Map<String, String> properties) {
        this.properties = new ConcurrentHashMap<>(properties);
    }

    /**
     * Creates a new PropertiesConfigurationAdapter and loads properties from the given source.
     *
     * @param source The source file path to load properties from
     * @throws IOException If an error occurs while loading properties
     */
    public PropertiesConfigurationAdapter(String source) throws IOException {
        this.properties = new ConcurrentHashMap<>();
        
        // Load the properties directly rather than calling loadConfiguration
        // since we want to throw IOException rather than returning false
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(source)) {
            props.load(fis);
            
            // Update properties map
            for (String key : props.stringPropertyNames()) {
                properties.put(key, props.getProperty(key));
            }
        }
    }

    @Override
    public Optional<String> getString(String key) {
        return Optional.ofNullable(properties.get(key));
    }

    @Override
    public String getString(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    @Override
    public Optional<Integer> getInteger(String key) {
        String value = properties.get(key);
        if (value == null) {
            return Optional.empty();
        }
        
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public int getInteger(String key, int defaultValue) {
        return getInteger(key).orElse(defaultValue);
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        String value = properties.get(key);
        if (value == null) {
            return Optional.empty();
        }
        
        // Handle common boolean string values
        value = value.toLowerCase();
        if (value.equals("true") || value.equals("yes") || value.equals("1")) {
            return Optional.of(true);
        } else if (value.equals("false") || value.equals("no") || value.equals("0")) {
            return Optional.of(false);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(key).orElse(defaultValue);
    }

    @Override
    public List<String> getStringList(String key) {
        String value = properties.get(key);
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    @Override
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    @Override
    public boolean removeProperty(String key) {
        return properties.remove(key) != null;
    }

    @Override
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    @Override
    public Map<String, String> getAllProperties() {
        return new HashMap<>(properties);
    }

    @Override
    public boolean loadConfiguration(String source) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(source)) {
            props.load(fis);
            
            // Update properties map
            for (String key : props.stringPropertyNames()) {
                properties.put(key, props.getProperty(key));
            }
            
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean saveConfiguration(String destination) {
        Properties props = toProperties();
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            props.store(fos, "Configuration saved by PropertiesConfigurationAdapter");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Map<String, String> getPropertiesWithPrefix(String prefix) {
        return properties.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Properties toProperties() {
        Properties props = new Properties();
        properties.forEach(props::setProperty);
        return props;
    }

    @Override
    public void clear() {
        properties.clear();
    }
}