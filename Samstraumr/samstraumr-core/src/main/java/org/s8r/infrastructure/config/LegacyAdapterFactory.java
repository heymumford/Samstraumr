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

package org.s8r.infrastructure.config;

import org.s8r.application.port.LegacyAdapterResolver;
import org.s8r.domain.identity.LegacyEnvironmentConverter;
import org.s8r.domain.identity.LegacyIdentityConverter;

/**
 * Implementation of LegacyAdapterResolver that provides access to adapters for legacy code.
 * 
 * <p>This class provides access to adapters that convert between legacy and new
 * object representations, while maintaining clean architecture boundaries.
 * 
 * <p>Internal adapter implementations are lazy-loaded to prevent direct dependencies
 * on concrete adapter implementations from leaking into other parts of the code.
 */
public class LegacyAdapterFactory implements LegacyAdapterResolver {
    
    private static final LegacyAdapterFactory INSTANCE = new LegacyAdapterFactory();
    
    private LegacyEnvironmentConverter coreEnvConverter;
    private LegacyEnvironmentConverter tubeEnvConverter;
    private LegacyIdentityConverter coreIdentityConverter;
    private LegacyIdentityConverter tubeIdentityConverter;
    
    private LegacyAdapterFactory() {
        // Private constructor to enforce singleton pattern
        // No initialization here - adapters are created lazily
    }
    
    /**
     * Gets the singleton instance.
     * 
     * @return The factory instance
     */
    public static LegacyAdapterFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public LegacyEnvironmentConverter getCoreEnvironmentConverter() {
        if (coreEnvConverter == null) {
            try {
                // Use reflection to create the adapter without direct dependency
                Class<?> converterClass = Class.forName("org.s8r.adapter.CoreLegacyEnvironmentConverter");
                coreEnvConverter = (LegacyEnvironmentConverter) converterClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create CoreLegacyEnvironmentConverter", e);
            }
        }
        return coreEnvConverter;
    }
    
    @Override
    public LegacyEnvironmentConverter getTubeEnvironmentConverter() {
        if (tubeEnvConverter == null) {
            try {
                // Use reflection to create the adapter without direct dependency
                Class<?> converterClass = Class.forName("org.s8r.adapter.TubeLegacyEnvironmentConverter");
                tubeEnvConverter = (LegacyEnvironmentConverter) converterClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create TubeLegacyEnvironmentConverter", e);
            }
        }
        return tubeEnvConverter;
    }
    
    @Override
    public LegacyIdentityConverter getCoreIdentityConverter() {
        if (coreIdentityConverter == null) {
            try {
                // Use reflection to create the adapter without direct dependency
                Class<?> converterClass = Class.forName("org.s8r.adapter.CoreLegacyIdentityConverter");
                coreIdentityConverter = (LegacyIdentityConverter) converterClass.getDeclaredConstructor()
                    .newInstance(getCoreEnvironmentConverter());
            } catch (Exception e) {
                throw new RuntimeException("Failed to create CoreLegacyIdentityConverter", e);
            }
        }
        return coreIdentityConverter;
    }
    
    @Override
    public LegacyIdentityConverter getTubeIdentityConverter() {
        if (tubeIdentityConverter == null) {
            try {
                // Use reflection to create the adapter without direct dependency
                Class<?> converterClass = Class.forName("org.s8r.adapter.TubeLegacyIdentityConverter");
                tubeIdentityConverter = (LegacyIdentityConverter) converterClass.getDeclaredConstructor()
                    .newInstance(getTubeEnvironmentConverter());
            } catch (Exception e) {
                throw new RuntimeException("Failed to create TubeLegacyIdentityConverter", e);
            }
        }
        return tubeIdentityConverter;
    }
}