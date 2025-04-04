/**
 * Test annotations for the Samstraumr framework, providing a consolidated location for all test
 * categorization and tagging.
 *
 * <p>This package is part of the package structure simplification initiative, centralizing all test
 * annotations into a single package under the simplified org.s8r root package.
 *
 * <p>The annotations in this package serve multiple purposes:
 *
 * <ul>
 *   <li>Test categorization (unit, integration, etc.)
 *   <li>Test priority (ATL, BTL)
 *   <li>Test domain semantics (tube tests, composite tests, etc.)
 * </ul>
 *
 * <p>Most annotations in this package are wrappers around JUnit's @Tag annotation, providing
 * semantic meaning to tags using Java's type system.
 *
 * <p>When adding new annotations to this package, follow these guidelines:
 * <ul>
 *   <li>Use clear, descriptive names
 *   <li>Include detailed JavaDoc explaining the purpose and usage
 *   <li>Include the corresponding JUnit tag
 *   <li>Keep annotations focused on a single concern
 * </ul>
 */
package org.s8r.test.annotation;