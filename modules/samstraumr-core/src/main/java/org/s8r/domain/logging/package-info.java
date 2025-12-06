/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */

/**
 * Domain logging abstractions providing clean interfaces for logging within the domain model.
 *
 * <p>This package contains pure domain interfaces for logging that enable domain logic to emit log
 * messages without depending on external logging frameworks. Infrastructure layer implementations
 * bridge to concrete logging systems (SLF4J, Log4j, etc.).
 *
 * <p>This package is part of the domain layer.
 */
package org.s8r.domain.logging;
