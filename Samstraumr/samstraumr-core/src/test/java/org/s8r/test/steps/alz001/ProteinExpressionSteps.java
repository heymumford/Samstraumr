/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

package org.s8r.test.steps.alz001;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Step definitions for protein expression test scenarios.
 * 
 * <p>Implements steps for creating, configuring, and testing protein expression
 * components and their capabilities.
 */
public class ProteinExpressionSteps extends ALZ001BaseSteps {

    /**
     * Creates a protein expression component.
     */
    @When("I create a protein expression component")
    public void createProteinExpressionComponent() {
        // Create the component
        ProteinExpressionComponent component = new ProteinExpressionComponent();
        component.initialize();
        
        // Store in test context
        storeInContext("proteinComponent", component);
    }
    
    /**
     * Verifies the component is properly initialized.
     */
    @Then("the component should be properly initialized")
    public void componentShouldBeProperlyInitialized() {
        ProteinExpressionComponent component = getFromContext("proteinComponent");
        assertNotNull(component, "Protein expression component should exist");
        assertTrue(component.isInitialized(), "Component should be initialized");
    }
    
    /**
     * Verifies the component has protein modeling capabilities.
     */
    @Then("the component should have protein modeling capabilities")
    public void componentShouldHaveProteinModelingCapabilities() {
        ProteinExpressionComponent component = getFromContext("proteinComponent");
        assertTrue(component.hasCapability("protein_analysis"), 
                "Component should have protein analysis capability");
        assertTrue(component.hasCapability("expression_modeling"), 
                "Component should have expression modeling capability");
    }
    
    /**
     * Verifies the component state.
     */
    @Then("the component should be in {string} state")
    public void componentShouldBeInState(String state) {
        ProteinExpressionComponent component = getFromContext("proteinComponent");
        assertEquals(state, component.getState(), 
                "Component should be in " + state + " state");
    }
    
    /**
     * Sets up an initialized component.
     */
    @Given("an initialized protein expression component")
    public void initializedProteinExpressionComponent() {
        createProteinExpressionComponent();
    }
    
    /**
     * Loads protein dataset with specified types.
     */
    @When("I load a dataset with the following protein types:")
    public void loadDatasetWithProteinTypes(DataTable dataTable) {
        ProteinExpressionComponent component = getFromContext("proteinComponent");
        
        // Parse data table
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        // Load each protein type
        for (Map<String, String> row : rows) {
            String proteinType = row.get("protein_type");
            String unit = row.get("measurement_unit");
            double min = Double.parseDouble(row.get("normal_range_min"));
            double max = Double.parseDouble(row.get("normal_range_max"));
            
            component.addProteinType(proteinType, unit, min, max);
        }
        
        // Generate sample data
        component.generateSampleData();
        
        // Mark data as loaded
        storeInContext("dataLoaded", true);
    }
    
    /**
     * Verifies data is successfully loaded.
     */
    @Then("the data should be successfully loaded")
    public void dataShouldBeSuccessfullyLoaded() {
        ProteinExpressionComponent component = getFromContext("proteinComponent");
        assertTrue(component.hasData(), "Component should have data loaded");
        
        Boolean dataLoaded = getFromContext("dataLoaded");
        assertTrue(dataLoaded, "Data should be marked as loaded");
    }
    
    /**
     * Verifies component reports data statistics.
     */
    @Then("the component should report data statistics for each protein type")
    public void componentShouldReportDataStatistics() {
        ProteinExpressionComponent component = getFromContext("proteinComponent");
        Map<String, Map<String, Double>> statistics = component.getStatistics();
        
        assertNotNull(statistics, "Statistics should be available");
        assertFalse(statistics.isEmpty(), "Statistics should not be empty");
        
        // Verify each protein type has statistics
        for (String proteinType : component.getProteinTypes()) {
            assertTrue(statistics.containsKey(proteinType), 
                    "Statistics should exist for protein: " + proteinType);
        }
    }
    
    /**
     * Verifies statistical parameters are included.
     */
    @Then("the statistical parameters should include mean, median, and standard deviation")
    public void statisticalParametersShouldIncludeBasicStats() {
        ProteinExpressionComponent component = getFromContext("proteinComponent");
        Map<String, Map<String, Double>> statistics = component.getStatistics();
        
        // Check first protein type's statistics
        String firstProtein = component.getProteinTypes().iterator().next();
        Map<String, Double> firstProteinStats = statistics.get(firstProtein);
        
        assertTrue(firstProteinStats.containsKey("mean"), "Statistics should include mean");
        assertTrue(firstProteinStats.containsKey("median"), "Statistics should include median");
        assertTrue(firstProteinStats.containsKey("std_dev"), "Statistics should include standard deviation");
    }
    
    /**
     * Sets up component with loaded data.
     */
    @Given("an initialized protein expression component with loaded data")
    public void initializedComponentWithLoadedData() {
        initializedProteinExpressionComponent();
        
        // Create a data table with protein types
        List<Map<String, String>> rows = List.of(
                Map.of("protein_type", "Tau", 
                       "measurement_unit", "ng/ml", 
                       "normal_range_min", "80", 
                       "normal_range_max", "150"),
                Map.of("protein_type", "Amyloid-beta", 
                       "measurement_unit", "pg/ml", 
                       "normal_range_min", "500", 
                       "normal_range_max", "1200")
        );
        
        DataTable dataTable = DataTable.create(rows);
        loadDatasetWithProteinTypes(dataTable);
    }
    
    /**
     * Calculates baseline statistics.
     */
    @When("I calculate baseline statistics")
    public void calculateBaselineStatistics() {
        ProteinExpressionComponent component = getFromContext("proteinComponent");
        component.calculateStatistics();
        
        // Mark statistics as calculated
        storeInContext("statisticsCalculated", true);
    }
    
    /**
     * Verifies statistical profiles are generated.
     */
    @Then("the component should generate statistical profiles for each protein type")
    public void componentShouldGenerateStatisticalProfiles() {
        ProteinExpressionComponent component = getFromContext("proteinComponent");
        Map<String, Map<String, Object>> profiles = component.getStatisticalProfiles();
        
        assertNotNull(profiles, "Statistical profiles should exist");
        assertEquals(component.getProteinTypes().size(), profiles.size(), 
                "Should have profiles for each protein type");
    }
    
    /**
     * Verifies profiles include specified statistics.
     */
    @Then("the profiles should include:")
    public void profilesShouldInclude(DataTable dataTable) {
        ProteinExpressionComponent component = getFromContext("proteinComponent");
        Map<String, Map<String, Object>> profiles = component.getStatisticalProfiles();
        
        // Get first protein's profile
        String firstProtein = component.getProteinTypes().iterator().next();
        Map<String, Object> profile = profiles.get(firstProtein);
        
        // Check statistics from data table
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String statistic = row.get("statistic");
            assertTrue(profile.containsKey(statistic), 
                    "Profile should contain statistic: " + statistic);
        }
    }
    
    /**
     * Verifies statistical profiles are stored.
     */
    @Then("the statistical profiles should be stored for comparison")
    public void statisticalProfilesShouldBeStoredForComparison() {
        ProteinExpressionComponent component = getFromContext("proteinComponent");
        assertTrue(component.hasStoredProfiles(), 
                "Component should have stored profiles for comparison");
    }
    
    /**
     * Mock protein expression component class for testing.
     */
    public static class ProteinExpressionComponent {
        private boolean initialized = false;
        private String state = "CREATED";
        private final Map<String, String> capabilities = new HashMap<>();
        private final Map<String, Map<String, Double>> proteinRanges = new HashMap<>();
        private final Map<String, String> proteinUnits = new HashMap<>();
        private boolean hasData = false;
        private boolean hasStoredProfiles = false;
        private final Map<String, Map<String, Double>> statistics = new HashMap<>();
        private final Map<String, Map<String, Object>> statisticalProfiles = new HashMap<>();
        
        public void initialize() {
            this.initialized = true;
            this.state = "READY";
            
            // Set default capabilities
            capabilities.put("protein_analysis", "true");
            capabilities.put("expression_modeling", "true");
            capabilities.put("time_series_analysis", "true");
        }
        
        public boolean isInitialized() {
            return initialized;
        }
        
        public boolean hasCapability(String capability) {
            return capabilities.containsKey(capability) && 
                   "true".equals(capabilities.get(capability));
        }
        
        public String getState() {
            return state;
        }
        
        public void addProteinType(String proteinType, String unit, double min, double max) {
            Map<String, Double> range = new HashMap<>();
            range.put("min", min);
            range.put("max", max);
            
            proteinRanges.put(proteinType, range);
            proteinUnits.put(proteinType, unit);
        }
        
        public void generateSampleData() {
            hasData = true;
            
            // Pre-populate some basic statistics
            for (String protein : proteinRanges.keySet()) {
                Map<String, Double> proteinStats = new HashMap<>();
                Map<String, Double> range = proteinRanges.get(protein);
                
                double min = range.get("min");
                double max = range.get("max");
                double mean = (min + max) / 2;
                
                proteinStats.put("mean", mean);
                proteinStats.put("median", mean);
                proteinStats.put("std_dev", (max - min) / 6);
                
                statistics.put(protein, proteinStats);
            }
        }
        
        public boolean hasData() {
            return hasData;
        }
        
        public Iterable<String> getProteinTypes() {
            return proteinRanges.keySet();
        }
        
        public Map<String, Map<String, Double>> getStatistics() {
            return statistics;
        }
        
        public void calculateStatistics() {
            // Generate more detailed statistical profiles
            for (String protein : proteinRanges.keySet()) {
                Map<String, Object> profile = new HashMap<>();
                
                // Basic statistics already in statistics map
                Map<String, Double> basicStats = statistics.get(protein);
                profile.putAll(basicStats);
                
                // Add additional statistics
                profile.put("percentiles", new double[] {5.0, 25.0, 75.0, 95.0});
                profile.put("range", proteinRanges.get(protein));
                profile.put("unit", proteinUnits.get(protein));
                
                statisticalProfiles.put(protein, profile);
            }
            
            hasStoredProfiles = true;
        }
        
        public Map<String, Map<String, Object>> getStatisticalProfiles() {
            return statisticalProfiles;
        }
        
        public boolean hasStoredProfiles() {
            return hasStoredProfiles;
        }
    }
}