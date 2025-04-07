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

/**
 * Identity domain model in the Samstraumr framework.
 * 
 * <p>This package contains the domain model for identity management,
 * which provides unique identification and addressing for components
 * and other entities in the system.</p>
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.identity.ComponentId} - Component identity value object</li>
 *   <li>{@link org.s8r.domain.identity.ComponentHierarchy} - Component hierarchy management</li>
 *   <li>{@link org.s8r.domain.identity.IdentityConverter} - Converts between identity formats</li>
 *   <li>{@link org.s8r.domain.identity.LegacyIdentityConverter} - Conversion for legacy identity formats</li>
 * </ul>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Component domain model that uses identities</li>
 *   <li>{@link org.s8r.component.identity} - Identity abstraction layer</li>
 * </ul>
 */
package org.s8r.domain.identity;
