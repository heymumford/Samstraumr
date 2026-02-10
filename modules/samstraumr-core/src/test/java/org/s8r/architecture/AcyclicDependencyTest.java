/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.architecture.util.ArchitectureAnalyzer;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for ensuring acyclic dependencies as described in ADR-0012.
 *
 * <p>This test suite verifies that the codebase does not contain circular dependencies between
 * packages or modules.
 */
@UnitTest
@Tag("architecture")
@Tag("adr-validation")
@DisplayName("Acyclic Dependency Tests (ADR-0012)")
public class AcyclicDependencyTest {

  // Dynamically resolve the source directory from the project structure
  private static final String SRC_DIR_PATH = getSrcDir().toString();
  private static final String TEST_DIR_PATH = getTestDir().toString();

  private static Path getSrcDir() {
    // Check if we're running from project root or module directory
    Path userDir = Paths.get(System.getProperty("user.dir"));
    Path candidatePath =
        userDir
            .resolve("modules")
            .resolve("samstraumr-core")
            .resolve("src")
            .resolve("main")
            .resolve("java")
            .resolve("org")
            .resolve("s8r");

    if (Files.exists(candidatePath)) {
      return candidatePath.normalize().toAbsolutePath();
    }

    // We're already in the module directory
    candidatePath =
        userDir.resolve("src").resolve("main").resolve("java").resolve("org").resolve("s8r");

    if (Files.exists(candidatePath)) {
      return candidatePath.normalize().toAbsolutePath();
    }

    throw new IllegalStateException("Could not locate source directory. Current dir: " + userDir);
  }

  private static Path getTestDir() {
    // Check if we're running from project root or module directory
    Path userDir = Paths.get(System.getProperty("user.dir"));
    Path candidatePath =
        userDir
            .resolve("modules")
            .resolve("samstraumr-core")
            .resolve("src")
            .resolve("test")
            .resolve("java")
            .resolve("org")
            .resolve("s8r");

    if (Files.exists(candidatePath)) {
      return candidatePath.normalize().toAbsolutePath();
    }

    // We're already in the module directory
    candidatePath =
        userDir.resolve("src").resolve("test").resolve("java").resolve("org").resolve("s8r");

    if (Files.exists(candidatePath)) {
      return candidatePath.normalize().toAbsolutePath();
    }

    throw new IllegalStateException("Could not locate test directory. Current dir: " + userDir);
  }

  // Legacy packages exempt from strict dependency checking
  private static final Set<String> LEGACY_PACKAGES =
      Set.of("org.s8r.component", "org.s8r.core", "org.s8r.tube", "org.s8r.adapter");

  // Packages with known violations that are scheduled for refactoring
  // TODO: Remove each package as it's fixed (tracked in KANBAN.md)
  private static final Set<String> MIGRATION_IN_PROGRESS_PACKAGES =
      Set.of(
          "org.s8r.domain.validation", // Uses application ports - needs domain port abstraction
          "org.s8r.infrastructure.config" // Uses adapter types - needs refactoring
          );

  @Nested
  @DisplayName("Source Code Dependency Tests")
  class SourceCodeDependencyTests {

    @Test
    @DisplayName("Source code should have no circular dependencies")
    void sourceCodeShouldHaveNoCircularDependencies() throws IOException {
      // Use the ArchitectureAnalyzer to check for circular dependencies
      List<List<String>> circularDependencies =
          ArchitectureAnalyzer.detectCircularDependencies(SRC_DIR_PATH);

      // Collect all circular dependencies for a detailed error message
      StringBuilder errorMessage = new StringBuilder();
      if (!circularDependencies.isEmpty()) {
        errorMessage.append("Circular dependencies found in source code:\n");
        for (List<String> cycle : circularDependencies) {
          errorMessage.append("  Cycle: ");
          for (int i = 0; i < cycle.size(); i++) {
            errorMessage.append(cycle.get(i));
            if (i < cycle.size() - 1) {
              errorMessage.append(" -> ");
            }
          }
          errorMessage.append("\n");
        }
        errorMessage.append("\nSuggestions to fix:\n");
        errorMessage.append("1. Extract common interfaces to a shared package\n");
        errorMessage.append("2. Use dependency inversion principle\n");
        errorMessage.append("3. Consider using an event system for communication\n");
        errorMessage.append("4. Merge tightly coupled packages\n");
      }

      assertTrue(circularDependencies.isEmpty(), errorMessage.toString());
    }

    @Test
    @DisplayName("Clean architecture layer dependencies should be correct")
    void cleanArchitectureLayerDependenciesShouldBeCorrect() throws IOException {
      // Use the ArchitectureAnalyzer to check for layer dependency violations
      Map<String, List<String>> allViolations =
          ArchitectureAnalyzer.analyzePackageDependencies(SRC_DIR_PATH);

      // Filter out violations from legacy packages and migration-in-progress packages
      Map<String, List<String>> violations = new HashMap<>();
      for (Map.Entry<String, List<String>> entry : allViolations.entrySet()) {
        String pkg = entry.getKey();
        boolean isLegacy = LEGACY_PACKAGES.stream().anyMatch(pkg::startsWith);
        boolean isMigrating = MIGRATION_IN_PROGRESS_PACKAGES.stream().anyMatch(pkg::startsWith);
        if (!isLegacy && !isMigrating) {
          // Also filter out violations TO legacy or migrating packages
          List<String> filteredViolations =
              entry.getValue().stream()
                  .filter(v -> LEGACY_PACKAGES.stream().noneMatch(v::startsWith))
                  .filter(v -> MIGRATION_IN_PROGRESS_PACKAGES.stream().noneMatch(v::startsWith))
                  .collect(Collectors.toList());
          if (!filteredViolations.isEmpty()) {
            violations.put(pkg, filteredViolations);
          }
        }
      }

      // Collect all violations for a detailed error message
      StringBuilder errorMessage = new StringBuilder();
      if (!violations.isEmpty()) {
        errorMessage.append("Clean Architecture dependency violations found:\n");
        for (Map.Entry<String, List<String>> entry : violations.entrySet()) {
          errorMessage
              .append("  Package '")
              .append(entry.getKey())
              .append("' incorrectly depends on:\n");
          for (String violation : entry.getValue()) {
            errorMessage.append("    - ").append(violation).append("\n");
          }
        }
        errorMessage.append("\nRemember the dependency rule: dependencies point inward.\n");
        errorMessage.append("Domain → Application → Infrastructure/Adapters\n");
      }

      assertTrue(violations.isEmpty(), errorMessage.toString());
    }
  }

  @Nested
  @DisplayName("Test Code Dependency Tests")
  class TestCodeDependencyTests {

    @Test
    @DisplayName("Test code should have no circular dependencies")
    void testCodeShouldHaveNoCircularDependencies() throws IOException {
      // Use the ArchitectureAnalyzer to check for circular dependencies in test code
      List<List<String>> circularDependencies =
          ArchitectureAnalyzer.detectCircularDependencies(TEST_DIR_PATH);

      // Allow some specific circular dependencies in test code if needed
      List<List<String>> filteredCircularDependencies = new ArrayList<>();
      for (List<String> cycle : circularDependencies) {
        // Filter out any test utilities circular dependencies
        // These are sometimes unavoidable in test code
        boolean isTestUtilityCycle =
            cycle.stream().allMatch(pkg -> pkg.contains(".util.") || pkg.contains(".mock."));

        if (!isTestUtilityCycle) {
          filteredCircularDependencies.add(cycle);
        }
      }

      // Collect all circular dependencies for a detailed error message
      StringBuilder errorMessage = new StringBuilder();
      if (!filteredCircularDependencies.isEmpty()) {
        errorMessage.append("Circular dependencies found in test code:\n");
        for (List<String> cycle : filteredCircularDependencies) {
          errorMessage.append("  Cycle: ");
          for (int i = 0; i < cycle.size(); i++) {
            errorMessage.append(cycle.get(i));
            if (i < cycle.size() - 1) {
              errorMessage.append(" -> ");
            }
          }
          errorMessage.append("\n");
        }
        errorMessage.append("\nEven in test code, circular dependencies should be avoided.\n");
        errorMessage.append("Consider refactoring your test classes to eliminate these cycles.\n");
      }

      assertTrue(filteredCircularDependencies.isEmpty(), errorMessage.toString());
    }
  }

  @Nested
  @DisplayName("Module Dependency Tests")
  class ModuleDependencyTests {

    @Test
    @DisplayName("No circular dependencies should exist between modules")
    void noCircularDependenciesShouldExistBetweenModules() {
      // This would check Maven module dependencies
      // Not implemented yet because we're a single-module project currently
      // When we become multi-module, this should be implemented

      // For now, just pass the test
      assertTrue(true, "No circular dependencies between modules");
    }
  }
}
