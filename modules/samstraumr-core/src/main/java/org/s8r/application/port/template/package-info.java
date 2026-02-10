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
 * This package contains port interfaces for template rendering and document generation.
 *
 * <p>The template port interfaces define the contract for template processing operations in the
 * application following the ports and adapters pattern (hexagonal architecture). The interfaces in
 * this package are output ports in the application layer, which will be implemented by adapters in
 * the infrastructure layer.
 *
 * <p>The main interface is {@link org.s8r.application.port.template.TemplatePort}, which defines
 * operations for rendering templates with data models and generating various document formats.
 */
package org.s8r.application.port.template;
