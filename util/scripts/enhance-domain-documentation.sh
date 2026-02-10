#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# Enhance Domain Documentation - Part of Phase 5 (Source Code Simplification)
#
# This script implements the revised approach to Phase 5, focusing on
# enhancing documentation and navigation aids for the domain layer.

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="${SCRIPT_DIR}"
SRC_DIR="${ROOT_DIR}/modules/samstraumr-core/src/main/java/org/s8r"
DOMAIN_DIR="${SRC_DIR}/domain"

echo "==================================================================="
echo "  Enhancing Domain Documentation"
echo "==================================================================="
echo

# Create README.md for the domain directory
echo "Creating README.md for domain directory..."
cat > "${DOMAIN_DIR}/README.md" << 'EOL'
# Domain Layer

This directory contains the domain model that forms the core business logic of the S8r framework, following Clean Architecture principles.

## Package Organization

The domain layer uses a standard package structure that organizes classes by domain concept:

- `org.s8r.domain.component` - Component domain model
  - `Component.java` - Domain component entity
  - `.composite` - Composite component domain entities
  - `.pattern` - Pattern-based domain entities

- `org.s8r.domain.event` - Domain events
  - `DomainEvent.java` - Base event class
  - `ComponentCreatedEvent.java` - Component creation events
  - `ComponentStateChangedEvent.java` - Component state changes

- `org.s8r.domain.identity` - Identity domain model
  - `ComponentId.java` - Component identity value object
  - `ComponentHierarchy.java` - Component hierarchy management

- `org.s8r.domain.machine` - Machine domain model
  - `Machine.java` - Domain machine entity
  - `MachineFactory.java` - Factory for machine creation
  - `MachineState.java` - Machine states

- `org.s8r.domain.lifecycle` - Lifecycle management
  - `LifecycleState.java` - Component lifecycle states

- `org.s8r.domain.exception` - Domain exceptions
  - `DomainException.java` - Base exception class
  - `ComponentException.java` - Component-specific exceptions

## Domain Concepts

The domain layer implements core business logic following Domain-Driven Design concepts:

- **Entities**: Business objects with identity (Component, Machine)
- **Value Objects**: Immutable objects defined by their attributes (Identity)
- **Aggregates**: Clusters of entities and value objects with consistency boundaries
- **Domain Events**: Record of something significant that occurred in the domain
- **Services**: Operations that don't belong to any entity
- **Repositories**: Interfaces for persistence operations (defined as ports)

## Clean Architecture

This layer follows Clean Architecture principles:

1. **Framework Independence**: No dependencies on external frameworks
2. **Testability**: Business rules can be tested without UI, database, etc.
3. **UI Independence**: UI can change without changing business rules
4. **Database Independence**: Business rules not bound to a specific database
5. **Independence from External Agencies**: Business rules don't know about interfaces to the outside world

## Related Packages

- `org.s8r.application.service` - Application services that use domain model
- `org.s8r.application.port` - Ports to interact with the domain model
- `org.s8r.infrastructure.persistence` - Repository implementations
- `org.s8r.component` - Component layer (more abstract, fundamental interfaces)
EOL
echo "✓ Created README.md for domain directory"

# Create necessary directories first
echo "Creating necessary directories..."
mkdir -p "${DOMAIN_DIR}/component"
mkdir -p "${DOMAIN_DIR}/component/composite"
mkdir -p "${DOMAIN_DIR}/component/pattern"
mkdir -p "${DOMAIN_DIR}/component/monitoring"
mkdir -p "${DOMAIN_DIR}/event"
mkdir -p "${DOMAIN_DIR}/identity"
mkdir -p "${DOMAIN_DIR}/machine"
mkdir -p "${DOMAIN_DIR}/lifecycle"
mkdir -p "${DOMAIN_DIR}/exception"
echo "✓ Created directories"

# Update package-info.java files with navigation aids
echo "Updating package-info.java files with navigation aids..."

# Domain root package-info.java
cat > "${DOMAIN_DIR}/package-info.java" << 'EOL'
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
 * Domain layer of the Samstraumr framework following Clean Architecture principles.
 * 
 * <p>This package and its subpackages contain the domain model that forms
 * the core business logic of the S8r framework. The domain layer is independent
 * of external frameworks, databases, and user interfaces.</p>
 * 
 * <h2>Subpackages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Component domain model</li>
 *   <li>{@link org.s8r.domain.event} - Domain events</li>
 *   <li>{@link org.s8r.domain.identity} - Identity domain model</li>
 *   <li>{@link org.s8r.domain.machine} - Machine domain model</li>
 *   <li>{@link org.s8r.domain.lifecycle} - Lifecycle management</li>
 *   <li>{@link org.s8r.domain.exception} - Domain exceptions</li>
 * </ul>
 * 
 * <h2>Clean Architecture</h2>
 * <p>This layer follows Clean Architecture principles:</p>
 * <ul>
 *   <li>Framework Independence: No dependencies on external frameworks</li>
 *   <li>Testability: Business rules can be tested without UI, database, etc.</li>
 *   <li>UI Independence: UI can change without changing business rules</li>
 *   <li>Database Independence: Business rules not bound to a specific database</li>
 *   <li>Independence from External Agencies: Business rules don't know about interfaces to the outside world</li>
 * </ul>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.application.service} - Application services that use domain model</li>
 *   <li>{@link org.s8r.application.port} - Ports to interact with the domain model</li>
 *   <li>{@link org.s8r.infrastructure.persistence} - Repository implementations</li>
 * </ul>
 * 
 * <p>For detailed information about the domain layer organization and design principles,
 * see the README.md file in this package directory.</p>
 */
package org.s8r.domain;
EOL
echo "✓ Updated domain/package-info.java"

# Component package-info.java
cat > "${DOMAIN_DIR}/component/package-info.java" << 'EOL'
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
 * Component domain model in the Samstraumr framework.
 * 
 * <p>This package contains the domain model for components, which are
 * the fundamental building blocks of the Samstraumr framework.</p>
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component.Component} - Domain component entity</li>
 * </ul>
 * 
 * <h2>Subpackages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component.composite} - Composite component domain entities</li>
 *   <li>{@link org.s8r.domain.component.pattern} - Pattern-based domain entities</li>
 *   <li>{@link org.s8r.domain.component.monitoring} - Component monitoring entities</li>
 * </ul>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.event} - Domain events related to components</li>
 *   <li>{@link org.s8r.domain.identity} - Identity management for components</li>
 *   <li>{@link org.s8r.component} - Component abstraction layer</li>
 * </ul>
 */
package org.s8r.domain.component;
EOL
echo "✓ Updated component/package-info.java"

# Component Composite package-info.java
cat > "${DOMAIN_DIR}/component/composite/package-info.java" << 'EOL'
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
 * Composite component domain entities in the Samstraumr framework.
 * 
 * <p>This package contains domain entities related to composite components,
 * which are components that contain other components.</p>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Component domain model</li>
 *   <li>{@link org.s8r.domain.event} - Domain events for composites</li>
 *   <li>{@link org.s8r.component.composite} - Composite abstraction layer</li>
 * </ul>
 */
package org.s8r.domain.component.composite;
EOL
echo "✓ Updated component/composite/package-info.java"

# Event package-info.java
cat > "${DOMAIN_DIR}/event/package-info.java" << 'EOL'
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
 * Domain events in the Samstraumr framework.
 * 
 * <p>This package contains domain events that represent significant
 * occurrences within the system. Domain events are used to communicate
 * between different parts of the system without creating direct dependencies.</p>
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.event.DomainEvent} - Base event class</li>
 *   <li>{@link org.s8r.domain.event.ComponentCreatedEvent} - Component creation events</li>
 *   <li>{@link org.s8r.domain.event.ComponentStateChangedEvent} - Component state changes</li>
 *   <li>{@link org.s8r.domain.event.MachineStateChangedEvent} - Machine state changes</li>
 * </ul>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Component domain model that generates events</li>
 *   <li>{@link org.s8r.domain.machine} - Machine domain model that generates events</li>
 *   <li>{@link org.s8r.application.port.EventDispatcher} - Port for dispatching events</li>
 *   <li>{@link org.s8r.infrastructure.event} - Event handling infrastructure</li>
 * </ul>
 */
package org.s8r.domain.event;
EOL
echo "✓ Updated event/package-info.java"

# Identity package-info.java
cat > "${DOMAIN_DIR}/identity/package-info.java" << 'EOL'
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
EOL
echo "✓ Updated identity/package-info.java"

# Machine package-info.java
cat > "${DOMAIN_DIR}/machine/package-info.java" << 'EOL'
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
 * Machine domain model in the Samstraumr framework.
 * 
 * <p>This package contains the domain model for machines, which provide
 * orchestration and state management for groups of components.</p>
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.machine.Machine} - Domain machine entity</li>
 *   <li>{@link org.s8r.domain.machine.MachineFactory} - Factory for machine creation</li>
 *   <li>{@link org.s8r.domain.machine.MachineState} - Machine states</li>
 *   <li>{@link org.s8r.domain.machine.MachineType} - Types of machines</li>
 * </ul>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Component domain model used by machines</li>
 *   <li>{@link org.s8r.domain.event} - Domain events related to machines</li>
 *   <li>{@link org.s8r.component.machine} - Machine abstraction layer</li>
 * </ul>
 */
package org.s8r.domain.machine;
EOL
echo "✓ Updated machine/package-info.java"

# Lifecycle package-info.java
cat > "${DOMAIN_DIR}/lifecycle/package-info.java" << 'EOL'
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
 * Lifecycle management in the Samstraumr framework.
 * 
 * <p>This package contains classes related to component lifecycle management,
 * which is a key concept in the Samstraumr framework. Components follow a
 * biological-inspired lifecycle model.</p>
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.lifecycle.LifecycleState} - Component lifecycle states</li>
 * </ul>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Component domain model that uses lifecycle states</li>
 *   <li>{@link org.s8r.domain.event} - Domain events related to lifecycle changes</li>
 * </ul>
 */
package org.s8r.domain.lifecycle;
EOL
echo "✓ Updated lifecycle/package-info.java"

# Exception package-info.java
cat > "${DOMAIN_DIR}/exception/package-info.java" << 'EOL'
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
 * Domain exceptions in the Samstraumr framework.
 * 
 * <p>This package contains exception classes that represent errors and
 * exceptional conditions within the domain layer.</p>
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.exception.DomainException} - Base exception class</li>
 *   <li>{@link org.s8r.domain.exception.ComponentException} - Component-specific exceptions</li>
 *   <li>{@link org.s8r.domain.exception.ComponentNotFoundException} - Exception for missing components</li>
 *   <li>{@link org.s8r.domain.exception.InvalidStateTransitionException} - Invalid state transitions</li>
 * </ul>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Component domain model that throws these exceptions</li>
 *   <li>{@link org.s8r.domain.machine} - Machine domain model that throws these exceptions</li>
 * </ul>
 */
package org.s8r.domain.exception;
EOL
echo "✓ Updated exception/package-info.java"

echo
echo "==================================================================="
echo "  Domain Documentation Enhancement Complete!"
echo "==================================================================="
echo
echo "Next steps:"
echo "1. Run a compilation check to verify changes:"
echo "   cd ${ROOT_DIR}/Samstraumr && mvn compile -Dmaven.test.skip=true"
echo "2. Update KANBAN.md to reflect Phase 5 progress"
echo "3. Update summary documents with the new approach"
echo

echo "Done!"