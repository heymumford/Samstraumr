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
 * <p>This implementation uses reflective adapters to avoid direct dependencies on
 * legacy code, following Clean Architecture principles. The adapters use reflection
 * to interact with legacy classes, removing compile-time dependencies.
 */
public class LegacyAdapterFactory implements LegacyAdapterResolver {
    
    private static final LegacyAdapterFactory INSTANCE = new LegacyAdapterFactory();
    
    private LegacyEnvironmentConverter coreEnvConverter;
    private LegacyEnvironmentConverter tubeEnvConverter;
    private LegacyIdentityConverter coreIdentityConverter;
    private LegacyIdentityConverter tubeIdentityConverter;
    private org.s8r.adapter.ReflectiveAdapterFactory reflectiveFactory;
    
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
    
    /**
     * Gets or creates the reflective adapter factory.
     * 
     * @return The reflective adapter factory
     */
    private org.s8r.adapter.ReflectiveAdapterFactory getReflectiveFactory() {
        if (reflectiveFactory == null) {
            try {
                // Get the logger from the DependencyContainer
                org.s8r.application.port.LoggerPort logger = 
                    DependencyContainer.getInstance().getLogger(LegacyAdapterFactory.class);
                
                // Create the reflective factory
                reflectiveFactory = new org.s8r.adapter.ReflectiveAdapterFactory(logger);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create ReflectiveAdapterFactory", e);
            }
        }
        return reflectiveFactory;
    }

    @Override
    public LegacyEnvironmentConverter getCoreEnvironmentConverter() {
        if (coreEnvConverter == null) {
            try {
                // Try to use the reflective adapter first
                coreEnvConverter = getReflectiveFactory().getCoreEnvironmentConverter();
            } catch (Exception e) {
                // Fall back to direct implementation if reflection fails
                try {
                    Class<?> converterClass = Class.forName("org.s8r.adapter.CoreLegacyEnvironmentConverter");
                    coreEnvConverter = (LegacyEnvironmentConverter) converterClass.getDeclaredConstructor().newInstance();
                } catch (Exception fallbackEx) {
                    throw new RuntimeException("Failed to create CoreLegacyEnvironmentConverter", fallbackEx);
                }
            }
        }
        return coreEnvConverter;
    }
    
    @Override
    public LegacyEnvironmentConverter getTubeEnvironmentConverter() {
        if (tubeEnvConverter == null) {
            try {
                // Try to use the reflective adapter first
                tubeEnvConverter = getReflectiveFactory().getTubeEnvironmentConverter();
            } catch (Exception e) {
                // Fall back to direct implementation if reflection fails
                try {
                    Class<?> converterClass = Class.forName("org.s8r.adapter.TubeLegacyEnvironmentConverter");
                    tubeEnvConverter = (LegacyEnvironmentConverter) converterClass.getDeclaredConstructor().newInstance();
                } catch (Exception fallbackEx) {
                    throw new RuntimeException("Failed to create TubeLegacyEnvironmentConverter", fallbackEx);
                }
            }
        }
        return tubeEnvConverter;
    }
    
    @Override
    public LegacyIdentityConverter getCoreIdentityConverter() {
        if (coreIdentityConverter == null) {
            try {
                // Try to use the reflective adapter first
                coreIdentityConverter = getReflectiveFactory().getCoreIdentityConverter();
            } catch (Exception e) {
                // Fall back to direct implementation if reflection fails
                try {
                    Class<?> converterClass = Class.forName("org.s8r.adapter.CoreLegacyIdentityConverter");
                    coreIdentityConverter = (LegacyIdentityConverter) converterClass.getDeclaredConstructor()
                        .newInstance(getCoreEnvironmentConverter());
                } catch (Exception fallbackEx) {
                    throw new RuntimeException("Failed to create CoreLegacyIdentityConverter", fallbackEx);
                }
            }
        }
        return coreIdentityConverter;
    }
    
    @Override
    public LegacyIdentityConverter getTubeIdentityConverter() {
        if (tubeIdentityConverter == null) {
            try {
                // Try to use the reflective adapter first
                tubeIdentityConverter = getReflectiveFactory().getTubeIdentityConverter();
            } catch (Exception e) {
                // Fall back to direct implementation if reflection fails
                try {
                    Class<?> converterClass = Class.forName("org.s8r.adapter.TubeLegacyIdentityConverter");
                    tubeIdentityConverter = (LegacyIdentityConverter) converterClass.getDeclaredConstructor()
                        .newInstance(getTubeEnvironmentConverter());
                } catch (Exception fallbackEx) {
                    throw new RuntimeException("Failed to create TubeLegacyIdentityConverter", fallbackEx);
                }
            }
        }
        return tubeIdentityConverter;
    }
}