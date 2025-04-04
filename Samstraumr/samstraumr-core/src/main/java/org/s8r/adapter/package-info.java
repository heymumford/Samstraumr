/**
 * Adapter classes to provide backward compatibility for the S8r framework.
 *
 * <p>This package contains adapter classes that bridge the legacy API with the new, simplified
 * API structure. It facilitates a smooth migration path by allowing existing code to continue
 * using the old API while gradually transitioning to the new structure.
 *
 * <p>The adapters in this package serve as bidirectional translators between the old
 * (org.samstraumr.tube.*) and new (org.s8r.core.*) package structures, enabling incremental
 * adoption of the new structure without breaking existing functionality.
 *
 * <p>Key adapter patterns implemented:
 *
 * <ul>
 *   <li>Object Adapters - Converting between corresponding legacy and new classes
 *   <li>Bidirectional Conversion - Supporting both directions of conversion
 *   <li>Compatibility Layers - Implementing interfaces compatible with both structures
 * </ul>
 *
 * <p>Use these adapters when:
 * <ul>
 *   <li>You need to integrate legacy code with new code
 *   <li>You want to gradually migrate existing implementations
 *   <li>You need to maintain backward compatibility
 * </ul>
 */
package org.s8r.adapter;