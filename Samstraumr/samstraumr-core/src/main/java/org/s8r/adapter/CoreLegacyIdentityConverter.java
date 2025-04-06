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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.s8r.core.env.Environment;
import org.s8r.core.tube.identity.Identity;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.identity.LegacyIdentityConverter;

/**
 * Implementation of LegacyIdentityConverter for core.tube.identity.Identity.
 * 
 * <p>This class encapsulates all dependencies on the legacy core package,
 * providing a clean implementation of the domain interface that can be injected
 * where needed without creating direct dependencies on legacy code.
 */
public class CoreLegacyIdentityConverter implements LegacyIdentityConverter {
    
    private final CoreLegacyEnvironmentConverter environmentConverter;
    
    /**
     * Creates a new converter with the specified environment converter.
     * 
     * @param environmentConverter The environment converter to use
     */
    public CoreLegacyIdentityConverter(CoreLegacyEnvironmentConverter environmentConverter) {
        this.environmentConverter = environmentConverter;
    }

    @Override
    public ComponentId toComponentId(String legacyId, String legacyReason, List<String> legacyLineage) {
        return ComponentId.fromValues(legacyId, legacyReason, new ArrayList<>(legacyLineage));
    }

    @Override
    public String toLegacyIdString(ComponentId componentId) {
        if (componentId == null) {
            return null;
        }
        return componentId.getIdString();
    }

    @Override
    public Object createLegacyAdamIdentity(String reason, Object legacyEnvironment) {
        if (!(legacyEnvironment instanceof Environment)) {
            throw new IllegalArgumentException("Expected Environment object, got: " + 
                    (legacyEnvironment != null ? legacyEnvironment.getClass().getName() : "null"));
        }
        
        String uniqueId = UUID.randomUUID().toString();
        Identity identity = new Identity(uniqueId, reason);
        
        // Copy environment parameters
        Environment env = (Environment) legacyEnvironment;
        for (String key : env.getParameterKeys()) {
            identity.addEnvironmentContext(key, env.getParameter(key));
        }
        
        return identity;
    }

    @Override
    public Object createLegacyChildIdentity(String reason, Object legacyEnvironment, Object parentLegacyIdentity) {
        if (!(legacyEnvironment instanceof Environment)) {
            throw new IllegalArgumentException("Expected Environment object, got: " + 
                    (legacyEnvironment != null ? legacyEnvironment.getClass().getName() : "null"));
        }
        
        if (!(parentLegacyIdentity instanceof Identity)) {
            throw new IllegalArgumentException("Expected Identity object, got: " + 
                    (parentLegacyIdentity != null ? parentLegacyIdentity.getClass().getName() : "null"));
        }
        
        Identity parent = (Identity) parentLegacyIdentity;
        String uniqueId = UUID.randomUUID().toString();
        Identity identity = new Identity(uniqueId, reason);
        
        // Copy parent lineage
        for (String entry : parent.getLineage()) {
            identity.addToLineage(entry);
        }
        identity.addToLineage(parent.getUniqueId());
        
        // Copy environment parameters
        Environment env = (Environment) legacyEnvironment;
        for (String key : env.getParameterKeys()) {
            identity.addEnvironmentContext(key, env.getParameter(key));
        }
        
        return identity;
    }

    @Override
    public Map<String, Object> extractFromLegacyIdentity(Object legacyIdentity) {
        if (!(legacyIdentity instanceof Identity)) {
            throw new IllegalArgumentException("Expected Identity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        Identity identity = (Identity) legacyIdentity;
        Map<String, Object> result = new HashMap<>();
        
        result.put("id", identity.getUniqueId());
        result.put("reason", identity.getReason());
        result.put("lineage", identity.getLineage());
        result.put("environmentContext", identity.getEnvironmentContext());
        result.put("isAdam", identity.isAdamComponent());
        
        return result;
    }

    @Override
    public void addToLegacyIdentityLineage(Object legacyIdentity, String lineageEntry) {
        if (!(legacyIdentity instanceof Identity)) {
            throw new IllegalArgumentException("Expected Identity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        Identity identity = (Identity) legacyIdentity;
        identity.addToLineage(lineageEntry);
    }

    @Override
    public List<String> getLegacyIdentityLineage(Object legacyIdentity) {
        if (!(legacyIdentity instanceof Identity)) {
            throw new IllegalArgumentException("Expected Identity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        Identity identity = (Identity) legacyIdentity;
        return identity.getLineage();
    }

    @Override
    public String getLegacyIdentityId(Object legacyIdentity) {
        if (!(legacyIdentity instanceof Identity)) {
            throw new IllegalArgumentException("Expected Identity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        Identity identity = (Identity) legacyIdentity;
        return identity.getUniqueId();
    }

    @Override
    public String getLegacyIdentityReason(Object legacyIdentity) {
        if (!(legacyIdentity instanceof Identity)) {
            throw new IllegalArgumentException("Expected Identity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        Identity identity = (Identity) legacyIdentity;
        return identity.getReason();
    }

    @Override
    public Map<String, String> getLegacyIdentityEnvironmentContext(Object legacyIdentity) {
        if (!(legacyIdentity instanceof Identity)) {
            throw new IllegalArgumentException("Expected Identity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        Identity identity = (Identity) legacyIdentity;
        return identity.getEnvironmentContext();
    }

    @Override
    public void addToLegacyIdentityEnvironmentContext(Object legacyIdentity, String key, String value) {
        if (!(legacyIdentity instanceof Identity)) {
            throw new IllegalArgumentException("Expected Identity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        Identity identity = (Identity) legacyIdentity;
        identity.addEnvironmentContext(key, value);
    }

    @Override
    public boolean isLegacyAdamIdentity(Object legacyIdentity) {
        if (!(legacyIdentity instanceof Identity)) {
            throw new IllegalArgumentException("Expected Identity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        Identity identity = (Identity) legacyIdentity;
        return identity.isAdamComponent();
    }
}