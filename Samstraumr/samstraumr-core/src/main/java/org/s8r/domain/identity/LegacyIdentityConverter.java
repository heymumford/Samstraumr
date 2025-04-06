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

package org.s8r.domain.identity;

import java.util.List;
import java.util.Map;

/**
 * Interface for converting between different identity types for legacy systems.
 * 
 * <p>This interface allows for backward compatibility during the transition 
 * to the simplified package structure, while ensuring the domain layer
 * doesn't depend on legacy implementation details in the core or tube packages.
 */
public interface LegacyIdentityConverter extends IdentityConverter {
    
    /**
     * Creates a legacy identity (Adam type) with the given reason and environment.
     *
     * @param reason The reason for creation
     * @param legacyEnvironment The legacy environment object
     * @return An Object representing the legacy identity
     */
    Object createLegacyAdamIdentity(String reason, Object legacyEnvironment);
    
    /**
     * Creates a legacy identity (Child type) with the given parameters.
     *
     * @param reason The reason for creation
     * @param legacyEnvironment The legacy environment object
     * @param parentLegacyIdentity The parent legacy identity object
     * @return An Object representing the legacy identity
     */
    Object createLegacyChildIdentity(String reason, Object legacyEnvironment, Object parentLegacyIdentity);
    
    /**
     * Extracts parameters from a legacy identity.
     *
     * @param legacyIdentity The legacy identity object
     * @return A map containing the identity's parameters
     */
    Map<String, Object> extractFromLegacyIdentity(Object legacyIdentity);
    
    /**
     * Adds an entry to the lineage of a legacy identity.
     *
     * @param legacyIdentity The legacy identity object
     * @param lineageEntry The entry to add to the lineage
     */
    void addToLegacyIdentityLineage(Object legacyIdentity, String lineageEntry);
    
    /**
     * Gets the lineage from a legacy identity.
     *
     * @param legacyIdentity The legacy identity object
     * @return The list of lineage entries
     */
    List<String> getLegacyIdentityLineage(Object legacyIdentity);
    
    /**
     * Gets the unique ID from a legacy identity.
     *
     * @param legacyIdentity The legacy identity object
     * @return The unique ID
     */
    String getLegacyIdentityId(Object legacyIdentity);
    
    /**
     * Gets the reason from a legacy identity.
     *
     * @param legacyIdentity The legacy identity object
     * @return The reason
     */
    String getLegacyIdentityReason(Object legacyIdentity);
    
    /**
     * Gets the environment context from a legacy identity.
     *
     * @param legacyIdentity The legacy identity object
     * @return The environment context map
     */
    Map<String, String> getLegacyIdentityEnvironmentContext(Object legacyIdentity);
    
    /**
     * Adds an entry to the environment context of a legacy identity.
     *
     * @param legacyIdentity The legacy identity object
     * @param key The context key
     * @param value The context value
     */
    void addToLegacyIdentityEnvironmentContext(Object legacyIdentity, String key, String value);
    
    /**
     * Checks if a legacy identity is an Adam identity.
     *
     * @param legacyIdentity The legacy identity object
     * @return true if it's an Adam identity, false otherwise
     */
    boolean isLegacyAdamIdentity(Object legacyIdentity);
}