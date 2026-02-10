/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */

package org.s8r.test.context;

import org.s8r.application.port.ValidationPort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context for sharing data between ValidationPort test steps.
 */
public class ValidationPortTestContext {
    private ValidationPort validationPort;
    private ValidationPort.ValidationResult lastResult;
    private String currentRuleName;
    private String currentFieldName;
    private Object currentValue;
    private Map<String, Object> currentMap = new HashMap<>();
    private List<Object> currentAllowedValues = new ArrayList<>();
    private Map<String, Object> registeredRules = new HashMap<>();
    private Map<String, Map<String, Object>> registeredRuleSets = new HashMap<>();
    private Map<String, Object> lastEntityData = new HashMap<>();
    private String lastEntityType;

    public ValidationPort getValidationPort() {
        return validationPort;
    }

    public void setValidationPort(ValidationPort validationPort) {
        this.validationPort = validationPort;
    }

    public ValidationPort.ValidationResult getLastResult() {
        return lastResult;
    }

    public void setLastResult(ValidationPort.ValidationResult lastResult) {
        this.lastResult = lastResult;
    }

    public String getCurrentRuleName() {
        return currentRuleName;
    }

    public void setCurrentRuleName(String currentRuleName) {
        this.currentRuleName = currentRuleName;
    }

    public String getCurrentFieldName() {
        return currentFieldName;
    }

    public void setCurrentFieldName(String currentFieldName) {
        this.currentFieldName = currentFieldName;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Object currentValue) {
        this.currentValue = currentValue;
    }

    public Map<String, Object> getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(Map<String, Object> currentMap) {
        this.currentMap = currentMap;
    }

    public void addToCurrentMap(String key, Object value) {
        this.currentMap.put(key, value);
    }

    public void clearCurrentMap() {
        this.currentMap.clear();
    }

    public List<Object> getCurrentAllowedValues() {
        return currentAllowedValues;
    }

    public void setCurrentAllowedValues(List<Object> currentAllowedValues) {
        this.currentAllowedValues = currentAllowedValues;
    }

    public void addAllowedValue(Object value) {
        this.currentAllowedValues.add(value);
    }

    public void clearAllowedValues() {
        this.currentAllowedValues.clear();
    }

    public Map<String, Object> getRegisteredRules() {
        return registeredRules;
    }

    public void addRegisteredRule(String ruleName, Object ruleDefinition) {
        this.registeredRules.put(ruleName, ruleDefinition);
    }

    public Map<String, Map<String, Object>> getRegisteredRuleSets() {
        return registeredRuleSets;
    }

    public void addRegisteredRuleSet(String ruleSetName, Map<String, Object> ruleSetDefinition) {
        this.registeredRuleSets.put(ruleSetName, ruleSetDefinition);
    }

    public Map<String, Object> getLastEntityData() {
        return lastEntityData;
    }

    public void setLastEntityData(Map<String, Object> lastEntityData) {
        this.lastEntityData = lastEntityData;
    }

    public String getLastEntityType() {
        return lastEntityType;
    }

    public void setLastEntityType(String lastEntityType) {
        this.lastEntityType = lastEntityType;
    }
}