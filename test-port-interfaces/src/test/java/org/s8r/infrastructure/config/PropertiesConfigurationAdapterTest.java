package org.s8r.infrastructure.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.s8r.application.port.ConfigurationPort;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PropertiesConfigurationAdapterTest {

    private ConfigurationPort configPort;
    private Map<String, String> testProperties;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        testProperties = new HashMap<>();
        testProperties.put("app.name", "Samstraumr");
        testProperties.put("app.version", "2.7.1");
        testProperties.put("app.debug", "true");
        testProperties.put("app.maxConnections", "100");
        testProperties.put("app.tags", "java,clean-architecture,testing");
        testProperties.put("module.cache.enabled", "true");
        testProperties.put("module.cache.size", "1024");
        testProperties.put("module.auth.enabled", "false");
        
        configPort = new PropertiesConfigurationAdapter(testProperties);
    }

    @Test
    @DisplayName("Should retrieve string values correctly")
    void testGetString() {
        assertEquals(Optional.of("Samstraumr"), configPort.getString("app.name"));
        assertEquals(Optional.of("2.7.1"), configPort.getString("app.version"));
        assertEquals(Optional.empty(), configPort.getString("app.nonexistent"));
        
        assertEquals("Samstraumr", configPort.getString("app.name", "DefaultApp"));
        assertEquals("DefaultValue", configPort.getString("app.nonexistent", "DefaultValue"));
    }

    @Test
    @DisplayName("Should retrieve integer values correctly")
    void testGetInteger() {
        assertEquals(Optional.of(100), configPort.getInteger("app.maxConnections"));
        assertEquals(Optional.empty(), configPort.getInteger("app.name")); // Not an integer
        assertEquals(Optional.empty(), configPort.getInteger("app.nonexistent"));
        
        assertEquals(100, configPort.getInteger("app.maxConnections", 50));
        assertEquals(50, configPort.getInteger("app.nonexistent", 50));
        assertEquals(50, configPort.getInteger("app.name", 50)); // Not an integer
    }

    @Test
    @DisplayName("Should retrieve boolean values correctly")
    void testGetBoolean() {
        assertEquals(Optional.of(true), configPort.getBoolean("app.debug"));
        assertEquals(Optional.of(true), configPort.getBoolean("module.cache.enabled"));
        assertEquals(Optional.of(false), configPort.getBoolean("module.auth.enabled"));
        assertEquals(Optional.empty(), configPort.getBoolean("app.name")); // Not a boolean
        assertEquals(Optional.empty(), configPort.getBoolean("app.nonexistent"));
        
        assertTrue(configPort.getBoolean("app.debug", false));
        assertFalse(configPort.getBoolean("module.auth.enabled", true));
        assertTrue(configPort.getBoolean("app.nonexistent", true));
        assertTrue(configPort.getBoolean("app.name", true)); // Not a boolean
    }

    @Test
    @DisplayName("Should retrieve string list values correctly")
    void testGetStringList() {
        List<String> expected = Arrays.asList("java", "clean-architecture", "testing");
        List<String> actual = configPort.getStringList("app.tags");
        
        assertEquals(expected.size(), actual.size());
        assertTrue(actual.containsAll(expected));
        
        assertTrue(configPort.getStringList("app.nonexistent").isEmpty());
    }

    @Test
    @DisplayName("Should set, remove, and check properties correctly")
    void testPropertyOperations() {
        // Set new property
        configPort.setProperty("new.property", "new-value");
        assertEquals("new-value", configPort.getString("new.property", ""));
        
        // Check property exists
        assertTrue(configPort.hasProperty("app.name"));
        assertTrue(configPort.hasProperty("new.property"));
        assertFalse(configPort.hasProperty("nonexistent.property"));
        
        // Remove property
        assertTrue(configPort.removeProperty("new.property"));
        assertFalse(configPort.hasProperty("new.property"));
        
        // Remove nonexistent property
        assertFalse(configPort.removeProperty("nonexistent.property"));
    }

    @Test
    @DisplayName("Should retrieve all properties correctly")
    void testGetAllProperties() {
        Map<String, String> allProperties = configPort.getAllProperties();
        
        assertEquals(testProperties.size(), allProperties.size());
        for (Map.Entry<String, String> entry : testProperties.entrySet()) {
            assertEquals(entry.getValue(), allProperties.get(entry.getKey()));
        }
    }

    @Test
    @DisplayName("Should retrieve properties with prefix correctly")
    void testGetPropertiesWithPrefix() {
        Map<String, String> moduleProperties = configPort.getPropertiesWithPrefix("module.");
        assertEquals(3, moduleProperties.size());
        assertTrue(moduleProperties.containsKey("module.cache.enabled"));
        assertTrue(moduleProperties.containsKey("module.cache.size"));
        assertTrue(moduleProperties.containsKey("module.auth.enabled"));
        
        Map<String, String> cacheProperties = configPort.getPropertiesWithPrefix("module.cache.");
        assertEquals(2, cacheProperties.size());
        assertTrue(cacheProperties.containsKey("module.cache.enabled"));
        assertTrue(cacheProperties.containsKey("module.cache.size"));
        
        Map<String, String> nonexistentProperties = configPort.getPropertiesWithPrefix("nonexistent.");
        assertTrue(nonexistentProperties.isEmpty());
    }

    @Test
    @DisplayName("Should convert to Java Properties correctly")
    void testToProperties() {
        Properties properties = configPort.toProperties();
        
        assertEquals(testProperties.size(), properties.size());
        for (Map.Entry<String, String> entry : testProperties.entrySet()) {
            assertEquals(entry.getValue(), properties.getProperty(entry.getKey()));
        }
    }

    @Test
    @DisplayName("Should clear all properties correctly")
    void testClear() {
        assertFalse(configPort.getAllProperties().isEmpty());
        
        configPort.clear();
        
        assertTrue(configPort.getAllProperties().isEmpty());
        assertEquals(Optional.empty(), configPort.getString("app.name"));
    }

    @Test
    @DisplayName("Should load configuration from file correctly")
    void testLoadConfiguration() throws Exception {
        // Create temp properties file
        File propsFile = tempDir.resolve("test.properties").toFile();
        Properties props = new Properties();
        props.setProperty("test.prop1", "value1");
        props.setProperty("test.prop2", "value2");
        
        try (FileOutputStream fos = new FileOutputStream(propsFile)) {
            props.store(fos, "Test properties");
        }
        
        assertTrue(configPort.loadConfiguration(propsFile.getAbsolutePath()));
        assertEquals("value1", configPort.getString("test.prop1", ""));
        assertEquals("value2", configPort.getString("test.prop2", ""));
        
        // Original properties should still be there
        assertEquals("Samstraumr", configPort.getString("app.name", ""));
    }
    
    @Test
    @DisplayName("Should load configuration via constructor correctly")
    void testLoadConfigurationViaConstructor() throws Exception {
        // Create temp properties file
        File propsFile = tempDir.resolve("test-constructor.properties").toFile();
        Properties props = new Properties();
        props.setProperty("test.prop1", "value1");
        props.setProperty("test.prop2", "value2");
        
        try (FileOutputStream fos = new FileOutputStream(propsFile)) {
            props.store(fos, "Test properties for constructor");
        }
        
        // Create a new adapter that loads from file
        ConfigurationPort configFromFile = new PropertiesConfigurationAdapter(propsFile.getAbsolutePath());
        
        // Verify properties were loaded
        assertEquals("value1", configFromFile.getString("test.prop1", ""));
        assertEquals("value2", configFromFile.getString("test.prop2", ""));
    }
    
    @Test
    @DisplayName("Should handle nonexistent file in constructor")
    void testLoadConfigurationViaConstructorWithNonexistentFile() {
        // This should throw an IOException which is part of the method signature
        assertThrows(IOException.class, () -> {
            new PropertiesConfigurationAdapter("/nonexistent/file.properties");
        });
    }

    @Test
    @DisplayName("Should fail gracefully when loading nonexistent file")
    void testLoadConfigurationNonexistentFile() {
        assertFalse(configPort.loadConfiguration("/nonexistent/file.properties"));
    }

    @Test
    @DisplayName("Should save configuration to file correctly")
    void testSaveConfiguration() throws Exception {
        File saveFile = tempDir.resolve("save.properties").toFile();
        
        assertTrue(configPort.saveConfiguration(saveFile.getAbsolutePath()));
        
        // Create a new adapter and load from the saved file
        ConfigurationPort loadedConfig = new PropertiesConfigurationAdapter();
        loadedConfig.loadConfiguration(saveFile.getAbsolutePath());
        
        // Verify all properties were saved and loaded correctly
        Map<String, String> originalProps = configPort.getAllProperties();
        Map<String, String> loadedProps = loadedConfig.getAllProperties();
        
        assertEquals(originalProps.size(), loadedProps.size());
        for (Map.Entry<String, String> entry : originalProps.entrySet()) {
            assertEquals(entry.getValue(), loadedProps.get(entry.getKey()));
        }
    }
}