/**
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * <p>This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy
 * of the MPL was not distributed with this file, You can obtain one at
 * https://github.com/heymumford/Samstraumr/blob/main/LICENSE
 *
 * <p>Package containing identity-related functionality for components
 */

/**
 * Provides component identity management in the S8r framework.
 *
 * <p>This package contains classes that manage the identity and lineage of components in the S8r
 * framework. It implements a robust identity model that supports:
 *
 * <ul>
 *   <li>Unique identification of components
 *   <li>Parent-child relationship tracking
 *   <li>Lineage recording across the component hierarchy
 *   <li>Hierarchical addressing for navigation
 *   <li>Environmental context capture
 * </ul>
 *
 * <p>The identity model is based on the biological concept of lineage, where each component has a
 * unique identity that can be traced back through its ancestry to an origin component (Adam
 * component).
 *
 * <p>This package is part of the simplified structure, replacing the org.s8r.core.tube.identity
 * package with a more intuitive organization at org.s8r.component.identity.
 */
package org.s8r.component.identity;
