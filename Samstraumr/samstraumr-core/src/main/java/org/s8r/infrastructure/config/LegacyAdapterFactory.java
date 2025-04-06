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

import org.s8r.adapter.CoreLegacyEnvironmentConverter;
import org.s8r.adapter.CoreLegacyIdentityConverter;
import org.s8r.adapter.TubeLegacyEnvironmentConverter;
import org.s8r.adapter.TubeLegacyIdentityConverter;
import org.s8r.domain.identity.LegacyEnvironmentConverter;
import org.s8r.domain.identity.LegacyIdentityConverter;

/**
 * Factory for creating legacy adapters.
 * 
 * <p>This class provides access to adapters that convert between legacy and new
 * object representations, while maintaining clean architecture boundaries.
 */
public class LegacyAdapterFactory {
    
    private static final CoreLegacyEnvironmentConverter CORE_ENV_CONVERTER = new CoreLegacyEnvironmentConverter();
    private static final TubeLegacyEnvironmentConverter TUBE_ENV_CONVERTER = new TubeLegacyEnvironmentConverter();
    private static final CoreLegacyIdentityConverter CORE_IDENTITY_CONVERTER = new CoreLegacyIdentityConverter(CORE_ENV_CONVERTER);
    private static final TubeLegacyIdentityConverter TUBE_IDENTITY_CONVERTER = new TubeLegacyIdentityConverter(TUBE_ENV_CONVERTER);
    
    private LegacyAdapterFactory() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Gets the converter for core environment objects.
     * 
     * @return The core environment converter
     */
    public static LegacyEnvironmentConverter getCoreEnvironmentConverter() {
        return CORE_ENV_CONVERTER;
    }
    
    /**
     * Gets the converter for tube environment objects.
     * 
     * @return The tube environment converter
     */
    public static LegacyEnvironmentConverter getTubeEnvironmentConverter() {
        return TUBE_ENV_CONVERTER;
    }
    
    /**
     * Gets the converter for core identity objects.
     * 
     * @return The core identity converter
     */
    public static LegacyIdentityConverter getCoreIdentityConverter() {
        return CORE_IDENTITY_CONVERTER;
    }
    
    /**
     * Gets the converter for tube identity objects.
     * 
     * @return The tube identity converter
     */
    public static LegacyIdentityConverter getTubeIdentityConverter() {
        return TUBE_IDENTITY_CONVERTER;
    }
}