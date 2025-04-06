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

package org.s8r.application.port;

/**
 * Service factory interface that provides access to application services.
 * 
 * <p>This interface follows the Factory pattern and abstracts the dependency injection
 * mechanism from client code. It allows clients to get instances of application
 * services without directly depending on the infrastructure layer.
 */
public interface ServiceFactory {
    
    /**
     * Gets an instance of the specified service type.
     *
     * @param <T> The service type
     * @param serviceType The class of the service type
     * @return The service instance
     */
    <T> T getService(Class<T> serviceType);
    
    /**
     * Gets the framework facade.
     *
     * @return The framework facade
     */
    S8rFacade getFramework();
    
    /**
     * Gets the logger.
     *
     * @return The logger
     */
    LoggerPort getLogger(Class<?> clazz);
}