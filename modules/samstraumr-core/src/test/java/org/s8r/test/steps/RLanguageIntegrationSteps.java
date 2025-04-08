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

package org.s8r.test.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.s8r.application.port.FileSystemPort;
import org.s8r.application.service.EventService;
import org.s8r.test.annotation.ATL;
import org.s8r.tube.Environment;
import org.s8r.tube.Tube;
import org.s8r.tube.TubeStatus;
import org.s8r.tube.composite.Composite;
import org.s8r.tube.composite.CompositeFactory;
import org.s8r.tube.exception.TubeInitializationException;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for R Language Integration tests.
 * Implements behavior for creating and working with R-based calculation tubes.
 */
@ATL
public class RLanguageIntegrationSteps {

    private Environment env;
    private Tube rCalculationTube;
    private Tube rTimeSeriesTube;
    private Tube rPreprocessingTube;
    private Tube rAnalysisTube;
    private Tube rVisualizationTube;
    private Tube rErrorTube;
    private Tube rAdvancedTube;
    private Composite scientificComposite;
    
    private String rScript;
    private Map<String, Object> sampleData;
    private Map<String, Object> timeSeriesData;
    private Map<String, Object> compositeData;
    private Map<String, Object> regressionData;
    private Map<String, Object> outputData;
    private String errorMessage;
    
    private MockRLanguageService rService;
    private MockDataTransformationService transformService;
    
    @Before
    public void setup() {
        env = new Environment();
        
        // Initialize mock services
        rService = new MockRLanguageService();
        transformService = new MockDataTransformationService();
        
        // Add services to environment
        env.addParameter("rLanguageService", rService);
        env.addParameter("dataTransformationService", transformService);
        
        // Initialize data containers
        sampleData = new HashMap<>();
        timeSeriesData = new HashMap<>();
        compositeData = new HashMap<>();
        regressionData = new HashMap<>();
        outputData = new HashMap<>();
    }
    
    @Given("the R language service adapter is initialized")
    public void theRLanguageServiceAdapterIsInitialized() {
        assertNotNull(rService);
        assertTrue(rService.isInitialized());
    }
    
    @Given("the data transformation service is available")
    public void theDataTransformationServiceIsAvailable() {
        assertNotNull(transformService);
        assertTrue(transformService.isAvailable());
    }
    
    @Given("I have created a basic R calculation tube")
    public void iHaveCreatedABasicRCalculationTube() {
        rCalculationTube = Tube.create("BasicRCalculationTube", env);
        rCalculationTube.addAttribute("type", "r-calculation");
        assertNotNull(rCalculationTube);
        assertEquals(TubeStatus.ACTIVE, rCalculationTube.getStatus());
    }
    
    @Given("I have created a time series R calculation tube")
    public void iHaveCreatedATimeSeriesRCalculationTube() {
        rTimeSeriesTube = Tube.create("TimeSeriesRTube", env);
        rTimeSeriesTube.addAttribute("type", "r-time-series");
        rTimeSeriesTube.addAttribute("requires", "forecast");
        assertNotNull(rTimeSeriesTube);
        assertEquals(TubeStatus.ACTIVE, rTimeSeriesTube.getStatus());
    }
    
    @Given("I have created a data preprocessing R tube with script:")
    public void iHaveCreatedADataPreprocessingRTubeWithScript(String script) {
        rPreprocessingTube = Tube.create("PreprocessingRTube", env);
        rPreprocessingTube.addAttribute("type", "r-preprocessing");
        rPreprocessingTube.addAttribute("script", script);
        assertNotNull(rPreprocessingTube);
        assertEquals(TubeStatus.ACTIVE, rPreprocessingTube.getStatus());
    }
    
    @Given("I have created a statistical analysis R tube with script:")
    public void iHaveCreatedAStatisticalAnalysisRTubeWithScript(String script) {
        rAnalysisTube = Tube.create("AnalysisRTube", env);
        rAnalysisTube.addAttribute("type", "r-analysis");
        rAnalysisTube.addAttribute("script", script);
        assertNotNull(rAnalysisTube);
        assertEquals(TubeStatus.ACTIVE, rAnalysisTube.getStatus());
    }
    
    @Given("I have created a visualization R tube with script:")
    public void iHaveCreatedAVisualizationRTubeWithScript(String script) {
        rVisualizationTube = Tube.create("VisualizationRTube", env);
        rVisualizationTube.addAttribute("type", "r-visualization");
        rVisualizationTube.addAttribute("script", script);
        assertNotNull(rVisualizationTube);
        assertEquals(TubeStatus.ACTIVE, rVisualizationTube.getStatus());
    }
    
    @Given("I have created an R calculation tube with potential errors")
    public void iHaveCreatedAnRCalculationTubeWithPotentialErrors() {
        rErrorTube = Tube.create("ErrorRTube", env);
        rErrorTube.addAttribute("type", "r-calculation");
        rErrorTube.addAttribute("error-handling", "enabled");
        assertNotNull(rErrorTube);
        assertEquals(TubeStatus.ACTIVE, rErrorTube.getStatus());
    }
    
    @Given("I have created an R calculation tube with external package dependencies")
    public void iHaveCreatedAnRCalculationTubeWithExternalPackageDependencies() {
        rAdvancedTube = Tube.create("AdvancedRTube", env);
        rAdvancedTube.addAttribute("type", "r-advanced");
        rAdvancedTube.addAttribute("packages", "dplyr,ggplot2,tidyr,broom");
        assertNotNull(rAdvancedTube);
        assertEquals(TubeStatus.ACTIVE, rAdvancedTube.getStatus());
    }
    
    @When("I configure the tube with the R script:")
    public void iConfigureTheTubeWithTheRScript(String script) {
        this.rScript = script;
        rCalculationTube.addAttribute("script", script);
    }
    
    @When("I configure the tube with the R script that contains syntax errors:")
    public void iConfigureTheTubeWithTheRScriptThatContainsSyntaxErrors(String script) {
        this.rScript = script;
        rErrorTube.addAttribute("script", script);
        // Set the error flag in our mock service to simulate syntax error
        rService.setSyntaxError(true);
    }
    
    @When("I configure the tube with the R script using specialized libraries:")
    public void iConfigureTheTubeWithTheRScriptUsingSpecializedLibraries(String script) {
        this.rScript = script;
        rAdvancedTube.addAttribute("script", script);
    }
    
    @When("I pass the following sample data:")
    public void iPassTheFollowingSampleData(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        List<Double> values = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            values.add(Double.parseDouble(row.get("values")));
        }
        
        sampleData.put("values", values);
        rCalculationTube.addAttribute("data", sampleData);
    }
    
    @When("I pass the following time series data:")
    public void iPassTheFollowingTimeSeriesData(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        List<String> dates = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        int frequency = 0;
        int forecastPeriods = 0;
        
        for (Map<String, String> row : rows) {
            dates.add(row.get("date"));
            values.add(Double.parseDouble(row.get("values")));
            // Take the frequency and forecast periods from the first row
            if (frequency == 0) {
                frequency = Integer.parseInt(row.get("frequency"));
                forecastPeriods = Integer.parseInt(row.get("forecast_periods"));
            }
        }
        
        timeSeriesData.put("dates", dates);
        timeSeriesData.put("values", values);
        timeSeriesData.put("frequency", frequency);
        timeSeriesData.put("forecast_periods", forecastPeriods);
        
        rTimeSeriesTube.addAttribute("data", timeSeriesData);
    }
    
    @When("I pass the following scientific data:")
    public void iPassTheFollowingScientificData(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            xValues.add(Double.parseDouble(row.get("x")));
            yValues.add(Double.parseDouble(row.get("y")));
        }
        
        regressionData.put("x", xValues);
        regressionData.put("y", yValues);
        
        rAdvancedTube.addAttribute("data", regressionData);
    }
    
    @When("I create a composite connecting these tubes in sequence")
    public void iCreateACompositeConnectingTheseTubesInSequence() {
        scientificComposite = CompositeFactory.createComposite(env);
        scientificComposite.addTube(rPreprocessingTube);
        scientificComposite.addTube(rAnalysisTube);
        scientificComposite.addTube(rVisualizationTube);
        
        // Connect tubes in sequence
        scientificComposite.connect(rPreprocessingTube.getUniqueId(), rAnalysisTube.getUniqueId());
        scientificComposite.connect(rAnalysisTube.getUniqueId(), rVisualizationTube.getUniqueId());
        
        assertNotNull(scientificComposite);
        assertEquals(3, scientificComposite.getTubes().size());
    }
    
    @When("I provide the following input data:")
    public void iProvideTheFollowingInputData(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        List<Double> primaryValues = new ArrayList<>();
        List<Double> secondaryValues = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            primaryValues.add(Double.parseDouble(row.get("values")));
            secondaryValues.add(Double.parseDouble(row.get("secondary_values")));
        }
        
        compositeData.put("values", primaryValues);
        compositeData.put("secondary_values", secondaryValues);
        
        // Set input data on the composite
        scientificComposite.addAttribute("input_data", compositeData);
    }
    
    @Then("the tube should execute successfully")
    public void theTubeShouldExecuteSuccessfully() {
        try {
            if (rCalculationTube != null && rCalculationTube.getStatus() == TubeStatus.ACTIVE) {
                outputData = rService.executeScript(rCalculationTube.getAttribute("script").toString(), 
                                              (Map<String, Object>)rCalculationTube.getAttribute("data"));
                rCalculationTube.addAttribute("result", outputData);
            } else if (rTimeSeriesTube != null && rTimeSeriesTube.getStatus() == TubeStatus.ACTIVE) {
                outputData = rService.executeScript(rTimeSeriesTube.getAttribute("script").toString(), 
                                              (Map<String, Object>)rTimeSeriesTube.getAttribute("data"));
                rTimeSeriesTube.addAttribute("result", outputData);
            } else if (rAdvancedTube != null && rAdvancedTube.getStatus() == TubeStatus.ACTIVE) {
                outputData = rService.executeScript(rAdvancedTube.getAttribute("script").toString(), 
                                              (Map<String, Object>)rAdvancedTube.getAttribute("data"));
                rAdvancedTube.addAttribute("result", outputData);
            }
            
            assertNotNull(outputData);
            assertFalse(outputData.isEmpty());
        } catch (Exception e) {
            throw new AssertionError("R script execution failed", e);
        }
    }
    
    @Then("the tube should report a syntax error")
    public void theTubeShouldReportASyntaxError() {
        try {
            outputData = rService.executeScript(rErrorTube.getAttribute("script").toString(), 
                                          new HashMap<String, Object>());
            fail("Expected syntax error was not thrown");
        } catch (Exception e) {
            errorMessage = e.getMessage();
            rErrorTube.addAttribute("error", errorMessage);
            rErrorTube.addAttribute("status", "FAILED");
            assertNotNull(errorMessage);
            assertTrue(errorMessage.contains("syntax error"));
        }
    }
    
    @Then("the error message should contain meaningful debugging information")
    public void theErrorMessageShouldContainMeaningfulDebuggingInformation() {
        assertNotNull(errorMessage);
        assertTrue(errorMessage.contains("line"));
        assertTrue(errorMessage.contains("missing"));
    }
    
    @Then("the tube state should be marked as failed")
    public void theTubeStateShouldBeMarkedAsFailed() {
        assertEquals("FAILED", rErrorTube.getAttribute("status"));
    }
    
    @Then("the system should maintain stability despite the R script error")
    public void theSystemShouldMaintainStabilityDespiteTheRScriptError() {
        // Verify that error handling didn't crash the environment
        assertTrue(env.isActive());
        
        // Create a new tube to verify system is still functional
        Tube testTube = Tube.create("TestTubeAfterError", env);
        assertNotNull(testTube);
        assertEquals(TubeStatus.ACTIVE, testTube.getStatus());
    }
    
    @Then("the output should contain valid statistical results")
    public void theOutputShouldContainValidStatisticalResults() {
        Map<String, Object> result = (Map<String, Object>)rCalculationTube.getAttribute("result");
        assertNotNull(result);
        assertTrue(result.containsKey("mean"));
        assertTrue(result.containsKey("sd"));
        assertTrue(result.containsKey("median"));
        assertTrue(result.containsKey("quartiles"));
    }
    
    @Then("the mean value should be approximately {double}")
    public void theMeanValueShouldBeApproximately(double expectedMean) {
        Map<String, Object> result = (Map<String, Object>)rCalculationTube.getAttribute("result");
        double actualMean = (double)result.get("mean");
        // Allow for a small difference due to floating point precision
        assertEquals(expectedMean, actualMean, 0.01);
    }
    
    @Then("the standard deviation should be approximately {double}")
    public void theStandardDeviationShouldBeApproximately(double expectedSD) {
        Map<String, Object> result = (Map<String, Object>)rCalculationTube.getAttribute("result");
        double actualSD = (double)result.get("sd");
        // Allow for a small difference due to floating point precision
        assertEquals(expectedSD, actualSD, 0.01);
    }
    
    @Then("the output should contain decomposition components")
    public void theOutputShouldContainDecompositionComponents() {
        Map<String, Object> result = (Map<String, Object>)rTimeSeriesTube.getAttribute("result");
        assertNotNull(result);
        assertTrue(result.containsKey("decomposition"));
        
        Map<String, Object> decomposition = (Map<String, Object>)result.get("decomposition");
        assertTrue(decomposition.containsKey("trend"));
        assertTrue(decomposition.containsKey("seasonal"));
        assertTrue(decomposition.containsKey("random"));
    }
    
    @Then("the output should contain forecast predictions for {int} periods")
    public void theOutputShouldContainForecastPredictionsForPeriods(int expectedPeriods) {
        Map<String, Object> result = (Map<String, Object>)rTimeSeriesTube.getAttribute("result");
        Map<String, Object> forecast = (Map<String, Object>)result.get("forecast");
        assertNotNull(forecast);
        
        List<Double> predictions = (List<Double>)forecast.get("mean");
        assertEquals(expectedPeriods, predictions.size());
    }
    
    @Then("the composite should process data through all tubes successfully")
    public void theCompositeShouldProcessDataThroughAllTubesSuccessfully() {
        // Process data through the composite
        try {
            Map<String, Object> input = (Map<String, Object>)scientificComposite.getAttribute("input_data");
            
            // Preprocess data
            Map<String, Object> preprocessOutput = rService.executeScript(
                rPreprocessingTube.getAttribute("script").toString(), input);
            rPreprocessingTube.addAttribute("result", preprocessOutput);
            
            // Analysis with preprocessed data
            preprocessOutput.put("secondary_values", input.get("secondary_values"));
            Map<String, Object> analysisOutput = rService.executeScript(
                rAnalysisTube.getAttribute("script").toString(), preprocessOutput);
            rAnalysisTube.addAttribute("result", analysisOutput);
            
            // Visualization with analysis data
            Map<String, Object> visualizationInput = new HashMap<>(preprocessOutput);
            visualizationInput.putAll(analysisOutput);
            Map<String, Object> visualizationOutput = rService.executeScript(
                rVisualizationTube.getAttribute("script").toString(), visualizationInput);
            rVisualizationTube.addAttribute("result", visualizationOutput);
            
            // Final composite output
            scientificComposite.addAttribute("result", visualizationOutput);
            
            // Verify all tubes have executed
            assertNotNull(rPreprocessingTube.getAttribute("result"));
            assertNotNull(rAnalysisTube.getAttribute("result"));
            assertNotNull(rVisualizationTube.getAttribute("result"));
            assertNotNull(scientificComposite.getAttribute("result"));
        } catch (Exception e) {
            throw new AssertionError("Composite processing failed", e);
        }
    }
    
    @Then("the final output should contain both statistical results and visualization data")
    public void theFinalOutputShouldContainBothStatisticalResultsAndVisualizationData() {
        Map<String, Object> result = (Map<String, Object>)scientificComposite.getAttribute("result");
        assertNotNull(result);
        
        // Check for visualization outputs
        assertTrue(result.containsKey("histogram"));
        assertTrue(result.containsKey("boxplot_data"));
        assertTrue(result.containsKey("outliers_marked"));
        
        // Check for statistical results from previous tubes
        Map<String, Object> analysisResult = (Map<String, Object>)rAnalysisTube.getAttribute("result");
        assertNotNull(analysisResult);
        assertTrue(analysisResult.containsKey("mean"));
        assertTrue(analysisResult.containsKey("sd"));
        assertTrue(analysisResult.containsKey("correlation"));
    }
    
    @Then("the normalized values should be between {int} and {int}")
    public void theNormalizedValuesShouldBeBetweenAnd(int min, int max) {
        Map<String, Object> prepResult = (Map<String, Object>)rPreprocessingTube.getAttribute("result");
        List<Double> normalized = (List<Double>)prepResult.get("normalized");
        
        for (Double value : normalized) {
            assertTrue(value >= min && value <= max);
        }
    }
    
    @Then("the correlation between normalized values and secondary values should be high")
    public void theCorrelationBetweenNormalizedValuesAndSecondaryValuesShouldBeHigh() {
        Map<String, Object> analysisResult = (Map<String, Object>)rAnalysisTube.getAttribute("result");
        double correlation = (double)analysisResult.get("correlation");
        
        // For our test, "high" means above 0.7
        assertTrue(correlation > 0.7);
    }
    
    @Then("the output should contain regression coefficients")
    public void theOutputShouldContainRegressionCoefficients() {
        Map<String, Object> result = (Map<String, Object>)rAdvancedTube.getAttribute("result");
        assertNotNull(result);
        assertTrue(result.containsKey("coefficients"));
        
        List<Double> coefficients = (List<Double>)result.get("coefficients");
        assertNotNull(coefficients);
        assertTrue(coefficients.size() >= 2); // Intercept and slope
    }
    
    @Then("the output should include predicted values")
    public void theOutputShouldIncludePredictedValues() {
        Map<String, Object> result = (Map<String, Object>)rAdvancedTube.getAttribute("result");
        assertNotNull(result);
        assertTrue(result.containsKey("predictions"));
    }
    
    @Then("the R-squared value should indicate a good fit")
    public void theRSquaredValueShouldIndicateAGoodFit() {
        Map<String, Object> result = (Map<String, Object>)rAdvancedTube.getAttribute("result");
        double rSquared = (double)result.get("r_squared");
        
        // For our test, "good fit" means R-squared > 0.8
        assertTrue(rSquared > 0.8);
    }
    
    /**
     * Mock implementation of R Language Service for testing.
     */
    private class MockRLanguageService {
        private boolean initialized = true;
        private boolean syntaxError = false;
        
        public boolean isInitialized() {
            return initialized;
        }
        
        public void setSyntaxError(boolean syntaxError) {
            this.syntaxError = syntaxError;
        }
        
        public Map<String, Object> executeScript(String script, Map<String, Object> data) throws Exception {
            if (syntaxError) {
                throw new Exception("R syntax error: missing closing bracket on line 2");
            }
            
            // Mock implementation that generates synthetic results
            Map<String, Object> result = new HashMap<>();
            
            // Basic statistical calculations
            if (script.contains("calculate <-") && data.containsKey("values")) {
                List<Double> values = (List<Double>)data.get("values");
                double sum = 0.0;
                for (Double val : values) {
                    sum += val;
                }
                double mean = sum / values.size();
                
                // Calculate standard deviation
                double variance = 0.0;
                for (Double val : values) {
                    variance += Math.pow(val - mean, 2);
                }
                variance /= values.size();
                double sd = Math.sqrt(variance);
                
                // Sort values for median and quartiles
                List<Double> sortedValues = new ArrayList<>(values);
                sortedValues.sort(null);
                double median = sortedValues.get(sortedValues.size() / 2);
                
                Map<String, Double> quartiles = new HashMap<>();
                quartiles.put("0%", sortedValues.get(0));
                quartiles.put("25%", sortedValues.get(sortedValues.size() / 4));
                quartiles.put("50%", median);
                quartiles.put("75%", sortedValues.get(3 * sortedValues.size() / 4));
                quartiles.put("100%", sortedValues.get(sortedValues.size() - 1));
                
                result.put("mean", mean);
                result.put("sd", sd);
                result.put("median", median);
                result.put("quartiles", quartiles);
            }
            
            // Time series analysis
            else if (script.contains("analyze_time_series") && data.containsKey("values")) {
                List<Double> values = (List<Double>)data.get("values");
                int periods = (Integer)data.get("forecast_periods");
                
                // Mock decomposition
                Map<String, Object> decomposition = new HashMap<>();
                decomposition.put("trend", generateTrend(values));
                decomposition.put("seasonal", generateSeasonal(values));
                decomposition.put("random", generateRandom(values));
                
                // Mock forecast
                Map<String, Object> forecast = new HashMap<>();
                forecast.put("mean", generateForecast(values, periods));
                forecast.put("lower", generateForecastBound(values, periods, -1));
                forecast.put("upper", generateForecastBound(values, periods, 1));
                
                result.put("decomposition", decomposition);
                result.put("forecast", forecast);
            }
            
            // Preprocessing
            else if (script.contains("preprocess")) {
                List<Double> values = (List<Double>)data.get("values");
                
                // Normalize
                List<Double> normalized = new ArrayList<>();
                double min = values.stream().min(Double::compare).orElse(0.0);
                double max = values.stream().max(Double::compare).orElse(1.0);
                double range = max - min;
                
                for (Double val : values) {
                    normalized.add((val - min) / range);
                }
                
                // Mock outliers
                List<Integer> outliers = new ArrayList<>();
                for (int i = 0; i < values.size(); i++) {
                    if (Math.abs(values.get(i) - calculateMean(values)) > 1.5 * calculateSD(values)) {
                        outliers.add(i);
                    }
                }
                
                result.put("original", values);
                result.put("normalized", normalized);
                result.put("outliers", outliers);
            }
            
            // Statistical analysis
            else if (script.contains("analyze")) {
                List<Double> normalized = (List<Double>)data.get("normalized");
                List<Double> secondary = (List<Double>)data.get("secondary_values");
                
                result.put("mean", calculateMean(normalized));
                result.put("sd", calculateSD(normalized));
                result.put("min", normalized.stream().min(Double::compare).orElse(0.0));
                result.put("max", normalized.stream().max(Double::compare).orElse(1.0));
                
                if (secondary != null) {
                    result.put("correlation", 0.85); // Mock correlation
                }
            }
            
            // Visualization
            else if (script.contains("visualize")) {
                // Mock histogram
                Map<String, Object> histogram = new HashMap<>();
                histogram.put("breaks", new double[]{0.0, 0.2, 0.4, 0.6, 0.8, 1.0});
                histogram.put("counts", new int[]{2, 3, 4, 3, 2});
                
                // Mock boxplot stats
                Map<String, Object> boxplot = new HashMap<>();
                boxplot.put("stats", new double[]{0.1, 0.25, 0.5, 0.75, 0.9});
                boxplot.put("n", 14);
                boxplot.put("conf", new double[]{0.45, 0.55});
                
                result.put("histogram", histogram);
                result.put("boxplot_data", boxplot);
                result.put("outliers_marked", data.get("outliers"));
            }
            
            // Advanced regression
            else if (script.contains("advanced_analysis")) {
                List<Double> xValues = (List<Double>)data.get("x");
                List<Double> yValues = (List<Double>)data.get("y");
                
                // Mock regression results
                List<Double> coefficients = new ArrayList<>();
                coefficients.add(1.2); // Intercept
                coefficients.add(1.5); // Slope
                
                List<Double> pValues = new ArrayList<>();
                pValues.add(0.001);
                pValues.add(0.0001);
                
                // Mock predictions
                double[][] predictions = new double[20][3];
                for (int i = 0; i < 20; i++) {
                    double x = 1.0 + (i * 0.35);
                    predictions[i][0] = 1.2 + (1.5 * x);
                    predictions[i][1] = predictions[i][0] - 0.5;
                    predictions[i][2] = predictions[i][0] + 0.5;
                }
                
                result.put("coefficients", coefficients);
                result.put("p_values", pValues);
                result.put("r_squared", 0.92);
                result.put("predictions", predictions);
            }
            
            return result;
        }
        
        // Helper methods for mock implementation
        private List<Double> generateTrend(List<Double> values) {
            List<Double> trend = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                trend.add(values.get(0) + (i * (values.get(values.size()-1) - values.get(0)) / values.size()));
            }
            return trend;
        }
        
        private List<Double> generateSeasonal(List<Double> values) {
            List<Double> seasonal = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                seasonal.add(Math.sin(2 * Math.PI * i / 12) * 10);
            }
            return seasonal;
        }
        
        private List<Double> generateRandom(List<Double> values) {
            List<Double> random = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                random.add(values.get(i) - (generateTrend(values).get(i) + generateSeasonal(values).get(i)));
            }
            return random;
        }
        
        private List<Double> generateForecast(List<Double> values, int periods) {
            List<Double> forecast = new ArrayList<>();
            double lastTrend = generateTrend(values).get(values.size() - 1);
            double trendIncrement = (lastTrend - generateTrend(values).get(0)) / values.size();
            
            for (int i = 0; i < periods; i++) {
                double nextValue = lastTrend + (i * trendIncrement);
                nextValue += Math.sin(2 * Math.PI * (values.size() + i) / 12) * 10;
                forecast.add(nextValue);
            }
            return forecast;
        }
        
        private List<Double> generateForecastBound(List<Double> values, int periods, int direction) {
            List<Double> bound = new ArrayList<>();
            List<Double> forecast = generateForecast(values, periods);
            
            for (int i = 0; i < periods; i++) {
                bound.add(forecast.get(i) + (direction * (5 + i * 0.5)));
            }
            return bound;
        }
        
        private double calculateMean(List<Double> values) {
            double sum = 0.0;
            for (Double val : values) {
                sum += val;
            }
            return sum / values.size();
        }
        
        private double calculateSD(List<Double> values) {
            double mean = calculateMean(values);
            double variance = 0.0;
            for (Double val : values) {
                variance += Math.pow(val - mean, 2);
            }
            variance /= values.size();
            return Math.sqrt(variance);
        }
    }
    
    /**
     * Mock implementation of Data Transformation Service for testing.
     */
    private class MockDataTransformationService {
        private boolean available = true;
        
        public boolean isAvailable() {
            return available;
        }
        
        public Map<String, Object> transformData(Map<String, Object> data, String format) {
            // Mock implementation for testing
            return data;
        }
    }
    
    // JUnit 5 Jupiter helper method for failing assertions (needed because of using static imports)
    private void fail(String message) {
        throw new AssertionError(message);
    }
}