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

package org.s8r.adapter;

import java.util.HashMap;
import java.util.Map;

import org.s8r.application.port.LoggerPort;
import org.s8r.domain.identity.LegacyEnvironmentConverter;
import org.s8r.tube.Environment;

/**
 * A concrete implementation of LegacyEnvironmentConverter specifically for Tube environments.
 * This class provides direct conversion between Environment objects without using reflection.
 * <p>
 * This is more efficient than the reflection-based approach when we know we're working with
 * Tube environments specifically, and serves as the primary migration path for existing code.
 */
public class TubeLegacyEnvironmentConverter implements LegacyEnvironmentConverter {
    
    private final LoggerPort logger;
    
    /**
     * Creates a new TubeLegacyEnvironmentConverter.
     *
     * @param logger Logger for recording conversion operations
     */
    public TubeLegacyEnvironmentConverter(LoggerPort logger) {
        this.logger = logger;
        logger.debug("TubeLegacyEnvironmentConverter initialized");
    }
    
    @Override
    public Object createLegacyEnvironment(Map<String, String> parameters) {
        logger.debug("Creating legacy environment with {} parameters", 
                parameters != null ? parameters.size() : 0);
        
        Environment environment = new Environment();
        
        if (parameters != null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                environment.setParameter(entry.getKey(), entry.getValue());
            }
        }
        
        return environment;
    }
    
    @Override
    public Map<String, String> extractParametersFromLegacyEnvironment(Object legacyEnvironment) {
        if (!(legacyEnvironment instanceof Environment)) {
            throw new IllegalArgumentException("Expected Environment object, got: " + 
                    (legacyEnvironment != null ? legacyEnvironment.getClass().getName() : "null"));
        }
        
        Environment environment = (Environment) legacyEnvironment;
        Map<String, String> parameters = new HashMap<>();
        
        for (String key : environment.getParameterKeys()) {
            parameters.put(key, environment.getParameter(key));
        }
        
        logger.debug("Extracted {} parameters from legacy environment", parameters.size());
        return parameters;
    }
    
    @Override
    public String getLegacyEnvironmentClassName(Object legacyEnvironment) {
        if (legacyEnvironment == null) {
            return null;
        }
        return legacyEnvironment.getClass().getName();
    }
    
    /**
     * Creates a new environment object with the given name and parameters.
     * This is a helper method specific to Tube environments, providing a simpler API
     * than the generic createLegacyEnvironment method.
     *
     * @param name the name of the environment
     * @param parameters the parameters to set in the environment
     * @return a new Environment instance
     */
    public Environment createNamedEnvironment(String name, Map<String, String> parameters) {
        logger.debug("Creating named environment: {} with {} parameters", 
                name, parameters != null ? parameters.size() : 0);
        
        Environment environment = new Environment();
        // Set the name as a parameter
        environment.setParameter("name", name);
        
        if (parameters != null) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                environment.setParameter(entry.getKey(), entry.getValue());
            }
        }
        
        return environment;
    }
    
    /**
     * Converts a component Environment to a legacy Tube Environment.
     *
     * @param componentEnvironment the component environment to convert
     * @return a legacy Tube Environment
     */
    public Environment toDomainEnvironment(org.s8r.component.Environment componentEnvironment) {
        if (componentEnvironment == null) {
            return null;
        }
        
        Environment legacyEnvironment = new Environment();
        
        // Copy parameters
        for (String key : componentEnvironment.getParameterKeys()) {
            legacyEnvironment.setParameter(key, componentEnvironment.getParameter(key));
        }
        
        logger.debug("Converted component environment to legacy environment");
        return legacyEnvironment;
    }
    
    /**
     * Converts a component core Environment to a legacy Tube Environment.
     *
     * @param coreEnvironment the component core environment to convert
     * @return a legacy Tube Environment
     */
    public Environment toDomainEnvironment(org.s8r.component.core.Environment coreEnvironment) {
        if (coreEnvironment == null) {
            return null;
        }
        
        Environment legacyEnvironment = new Environment();
        
        // Copy parameters
        for (String key : coreEnvironment.getParameterKeys()) {
            legacyEnvironment.setParameter(key, coreEnvironment.getParameter(key));
        }
        
        logger.debug("Converted core environment to legacy environment");
        return legacyEnvironment;
    }
    
    /**
     * Converts a legacy Tube Environment to a component Environment.
     *
     * @param legacyEnvironment the legacy environment to convert
     * @return a component Environment
     */
    public org.s8r.component.Environment fromLegacyEnvironment(Environment legacyEnvironment) {
        if (legacyEnvironment == null) {
            return null;
        }
        
        org.s8r.component.Environment componentEnvironment = new org.s8r.component.Environment();
        
        // Copy parameters
        for (String key : legacyEnvironment.getParameterKeys()) {
            componentEnvironment.setParameter(key, legacyEnvironment.getParameter(key));
        }
        
        logger.debug("Converted legacy environment to component environment");
        return componentEnvironment;
    }
    
    /**
     * Converts a legacy Tube Environment to a component core Environment.
     *
     * @param legacyEnvironment the legacy environment to convert
     * @return a component core Environment
     */
    public org.s8r.component.core.Environment fromLegacyEnvironmentToCore(Environment legacyEnvironment) {
        if (legacyEnvironment == null) {
            return null;
        }
        
        org.s8r.component.core.Environment coreEnvironment = new org.s8r.component.core.Environment();
        
        // Copy parameters
        for (String key : legacyEnvironment.getParameterKeys()) {
            coreEnvironment.setParameter(key, legacyEnvironment.getParameter(key));
        }
        
        logger.debug("Converted legacy environment to core environment");
        return coreEnvironment;
    }
}