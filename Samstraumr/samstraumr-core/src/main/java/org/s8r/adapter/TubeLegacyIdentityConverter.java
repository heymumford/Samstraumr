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

import org.s8r.application.port.LoggerPort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.identity.LegacyIdentityConverter;
import org.s8r.tube.TubeIdentity;
import org.s8r.tube.Environment;

/**
 * A concrete implementation of LegacyIdentityConverter specifically for Tube identities.
 * This class provides direct conversion between TubeIdentity and ComponentId without using reflection.
 * <p>
 * This is more efficient than the reflection-based approach when we know we're working with
 * Tube identities specifically, and serves as the primary migration path for existing code.
 */
public class TubeLegacyIdentityConverter implements LegacyIdentityConverter {
    
    private final LoggerPort logger;
    
    /**
     * Creates a new TubeLegacyIdentityConverter.
     *
     * @param logger Logger for recording conversion operations
     */
    public TubeLegacyIdentityConverter(LoggerPort logger) {
        this.logger = logger;
        logger.debug("TubeLegacyIdentityConverter initialized");
    }
    
    @Override
    public ComponentId toComponentId(String legacyId, String legacyReason, List<String> legacyLineage) {
        logger.debug("Converting legacy ID to ComponentId: {}", legacyId);
        return ComponentId.fromValues(legacyId, legacyReason, new ArrayList<>(legacyLineage));
    }
    
    @Override
    public String toLegacyIdString(ComponentId componentId) {
        if (componentId == null) {
            return null;
        }
        logger.debug("Converting ComponentId to legacy ID string: {}", componentId.getIdString());
        return componentId.getIdString();
    }
    
    @Override
    public Object createLegacyAdamIdentity(String reason, Object legacyEnvironment) {
        if (!(legacyEnvironment instanceof Environment)) {
            throw new IllegalArgumentException("Expected Environment object, got: " + 
                    (legacyEnvironment != null ? legacyEnvironment.getClass().getName() : "null"));
        }
        
        Environment env = (Environment) legacyEnvironment;
        logger.debug("Creating legacy Adam identity with reason: {}", reason);
        return TubeIdentity.createAdamIdentity(reason, env);
    }
    
    @Override
    public Object createLegacyChildIdentity(String reason, Object legacyEnvironment, Object parentLegacyIdentity) {
        if (!(legacyEnvironment instanceof Environment)) {
            throw new IllegalArgumentException("Expected Environment object, got: " + 
                    (legacyEnvironment != null ? legacyEnvironment.getClass().getName() : "null"));
        }
        
        if (!(parentLegacyIdentity instanceof TubeIdentity)) {
            throw new IllegalArgumentException("Expected TubeIdentity object, got: " + 
                    (parentLegacyIdentity != null ? parentLegacyIdentity.getClass().getName() : "null"));
        }
        
        Environment env = (Environment) legacyEnvironment;
        TubeIdentity parentId = (TubeIdentity) parentLegacyIdentity;
        logger.debug("Creating legacy child identity with reason: {} and parent: {}", reason, parentId.getUniqueId());
        return TubeIdentity.createChildIdentity(reason, env, parentId);
    }
    
    @Override
    public Map<String, Object> extractFromLegacyIdentity(Object legacyIdentity) {
        if (!(legacyIdentity instanceof TubeIdentity)) {
            throw new IllegalArgumentException("Expected TubeIdentity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        TubeIdentity identity = (TubeIdentity) legacyIdentity;
        Map<String, Object> result = new HashMap<>();
        
        result.put("id", identity.getUniqueId());
        result.put("reason", identity.getReason());
        result.put("lineage", identity.getLineage());
        result.put("environmentContext", identity.getEnvironmentContext());
        result.put("isAdam", identity.isAdamTube());
        
        logger.debug("Extracted data from legacy identity: {}", identity.getUniqueId());
        return result;
    }
    
    @Override
    public void addToLegacyIdentityLineage(Object legacyIdentity, String lineageEntry) {
        if (!(legacyIdentity instanceof TubeIdentity)) {
            throw new IllegalArgumentException("Expected TubeIdentity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        TubeIdentity identity = (TubeIdentity) legacyIdentity;
        identity.addToLineage(lineageEntry);
        logger.debug("Added entry to legacy identity lineage: {}", lineageEntry);
    }
    
    @Override
    public List<String> getLegacyIdentityLineage(Object legacyIdentity) {
        if (!(legacyIdentity instanceof TubeIdentity)) {
            throw new IllegalArgumentException("Expected TubeIdentity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        TubeIdentity identity = (TubeIdentity) legacyIdentity;
        logger.debug("Retrieved lineage from legacy identity: {}", identity.getUniqueId());
        return new ArrayList<>(identity.getLineage());
    }
    
    @Override
    public String getLegacyIdentityId(Object legacyIdentity) {
        if (!(legacyIdentity instanceof TubeIdentity)) {
            throw new IllegalArgumentException("Expected TubeIdentity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        TubeIdentity identity = (TubeIdentity) legacyIdentity;
        return identity.getUniqueId();
    }
    
    @Override
    public String getLegacyIdentityReason(Object legacyIdentity) {
        if (!(legacyIdentity instanceof TubeIdentity)) {
            throw new IllegalArgumentException("Expected TubeIdentity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        TubeIdentity identity = (TubeIdentity) legacyIdentity;
        return identity.getReason();
    }
    
    @Override
    public Map<String, String> getLegacyIdentityEnvironmentContext(Object legacyIdentity) {
        if (!(legacyIdentity instanceof TubeIdentity)) {
            throw new IllegalArgumentException("Expected TubeIdentity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        TubeIdentity identity = (TubeIdentity) legacyIdentity;
        return new HashMap<>(identity.getEnvironmentContext());
    }
    
    @Override
    public void addToLegacyIdentityEnvironmentContext(Object legacyIdentity, String key, String value) {
        if (!(legacyIdentity instanceof TubeIdentity)) {
            throw new IllegalArgumentException("Expected TubeIdentity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        TubeIdentity identity = (TubeIdentity) legacyIdentity;
        identity.addEnvironmentContext(key, value);
        logger.debug("Added to legacy identity environment context: {}={}", key, value);
    }
    
    @Override
    public boolean isLegacyAdamIdentity(Object legacyIdentity) {
        if (!(legacyIdentity instanceof TubeIdentity)) {
            throw new IllegalArgumentException("Expected TubeIdentity object, got: " + 
                    (legacyIdentity != null ? legacyIdentity.getClass().getName() : "null"));
        }
        
        TubeIdentity identity = (TubeIdentity) legacyIdentity;
        return identity.isAdamTube();
    }
}