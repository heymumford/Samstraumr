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

import org.s8r.core.env.Environment;
import org.s8r.domain.identity.LegacyEnvironmentConverter;

/**
 * Implementation of LegacyEnvironmentConverter for core.env.Environment.
 * 
 * <p>This class encapsulates all dependencies on the legacy core package,
 * providing a clean implementation of the domain interface that can be injected
 * where needed without creating direct dependencies on legacy code.
 */
public class CoreLegacyEnvironmentConverter implements LegacyEnvironmentConverter {

    @Override
    public Object createLegacyEnvironment(Map<String, String> parameters) {
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
        Map<String, String> result = new HashMap<>();
        
        for (String key : environment.getParameterKeys()) {
            result.put(key, environment.getParameter(key));
        }
        
        return result;
    }

    @Override
    public String getLegacyEnvironmentClassName(Object legacyEnvironment) {
        if (legacyEnvironment == null) {
            return null;
        }
        return legacyEnvironment.getClass().getName();
    }
}