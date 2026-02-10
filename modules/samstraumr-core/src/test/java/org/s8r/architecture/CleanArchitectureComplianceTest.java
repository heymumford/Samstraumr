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
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.architecture.util.ArchitectureAnalyzer;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for validating compliance with Clean Architecture principles as described in ADR-0003.
 *
 * <p>This test suite verifies that the codebase follows the structural guidelines and dependency
 * rules of Clean Architecture.
 */
@UnitTest
@Tag("architecture")
@Tag("adr-validation")
@DisplayName("Clean Architecture Compliance Tests (ADR-0003)")
public class CleanArchitectureComplianceTest {

  // Dynamically resolve the source directory from the project structure
  private static final Path SRC_DIR = getSrcDir();

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

  @Nested
  @DisplayName("Layer Structure Tests")
  class LayerStructureTests {

    @Test
    @DisplayName("Required architectural layers should exist")
    void requiredArchitecturalLayersShouldExist() throws IOException {
      // Check if key architectural layers exist
      boolean domainExists = Files.exists(SRC_DIR.resolve("domain"));
      boolean applicationExists = Files.exists(SRC_DIR.resolve("application"));
      boolean infrastructureExists = Files.exists(SRC_DIR.resolve("infrastructure"));
      boolean adapterExists = Files.exists(SRC_DIR.resolve("adapter"));

      assertTrue(domainExists, "Domain layer should exist");
      assertTrue(applicationExists, "Application layer should exist");
      assertTrue(infrastructureExists, "Infrastructure layer should exist");
      assertTrue(adapterExists, "Adapter layer should exist");
    }

    @Test
    @DisplayName("Each layer should have the expected structure")
    void eachLayerShouldHaveExpectedStructure() throws IOException {
      // Check domain layer structure
      try (Stream<Path> paths = Files.list(SRC_DIR.resolve("domain"))) {
        List<String> folders =
            paths
                .filter(Files::isDirectory)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

        assertTrue(folders.contains("component"), "Domain layer should have component package");
        assertTrue(folders.contains("event"), "Domain layer should have event package");
        assertTrue(folders.contains("exception"), "Domain layer should have exception package");
      }

      // Check application layer structure
      try (Stream<Path> paths = Files.list(SRC_DIR.resolve("application"))) {
        List<String> folders =
            paths
                .filter(Files::isDirectory)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

        assertTrue(folders.contains("service"), "Application layer should have service package");
        assertTrue(folders.contains("port"), "Application layer should have port package");
        assertTrue(folders.contains("dto"), "Application layer should have dto package");
      }
    }
  }

  // Files exempt from strict architecture checking during migration
  private static final Set<String> EXEMPT_FILES =
      Set.of(
          "ComponentDuplicateDetector.java", // Migration in progress - depends on ports
          "PortAdapterFactory.java" // Factory pattern requires adapter access
          );

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
  @DisplayName("Layer Dependency Tests")
  class LayerDependencyTests {

    @Test
    @DisplayName("Domain layer should not depend on outer layers")
    void domainLayerShouldNotDependOnOuterLayers() throws IOException {
      // Get all Java files in domain layer
      List<Path> domainFiles = findJavaFiles(SRC_DIR.resolve("domain"));

      for (Path file : domainFiles) {
        // Skip exempt files during migration
        if (EXEMPT_FILES.contains(file.getFileName().toString())) {
          continue;
        }

        String content = Files.readString(file);

        // Check for imports from outer layers
        assertFalse(
            content.contains("import org.s8r.application"),
            "Domain should not import from application layer: " + file);
        assertFalse(
            content.contains("import org.s8r.infrastructure"),
            "Domain should not import from infrastructure layer: " + file);
        assertFalse(
            content.contains("import org.s8r.adapter"),
            "Domain should not import from adapter layer: " + file);
      }
    }

    @Test
    @DisplayName("Application layer should only depend on domain layer")
    void applicationLayerShouldOnlyDependOnDomainLayer() throws IOException {
      // Get all Java files in application layer
      List<Path> applicationFiles = findJavaFiles(SRC_DIR.resolve("application"));

      for (Path file : applicationFiles) {
        String content = Files.readString(file);

        // Application can import from domain
        // But should not import from infrastructure or adapter
        assertFalse(
            content.contains("import org.s8r.infrastructure"),
            "Application should not import from infrastructure layer: " + file);
        assertFalse(
            content.contains("import org.s8r.adapter"),
            "Application should not import from adapter layer: " + file);
      }
    }

    @Test
    @DisplayName("Infrastructure layer should depend on domain and application layers")
    void infrastructureLayerShouldDependOnDomainAndApplicationLayers() throws IOException {
      // Get all Java files in infrastructure layer
      List<Path> infrastructureFiles = findJavaFiles(SRC_DIR.resolve("infrastructure"));
      boolean implementsApplicationInterfaces = false;

      for (Path file : infrastructureFiles) {
        // Skip exempt files during migration
        if (EXEMPT_FILES.contains(file.getFileName().toString())) {
          continue;
        }

        String content = Files.readString(file);

        // Check if any file implements interfaces from application
        if (content.contains("import org.s8r.application.port") && content.contains("implements")) {
          implementsApplicationInterfaces = true;
        }

        // Infrastructure should not import from adapter
        assertFalse(
            content.contains("import org.s8r.adapter"),
            "Infrastructure should not import from adapter layer: " + file);
      }

      // At least one infrastructure class should implement application interfaces
      assertTrue(
          implementsApplicationInterfaces,
          "Infrastructure layer should implement interfaces from application layer");
    }

    @Test
    @DisplayName("Clean Architecture dependency rules should be followed")
    void cleanArchitectureDependencyRulesShouldBeFollowed() throws IOException {
      // Use the ArchitectureAnalyzer to check for dependency violations
      Map<String, List<String>> allViolations =
          ArchitectureAnalyzer.analyzePackageDependencies(SRC_DIR.toString());

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
      }

      assertTrue(violations.isEmpty(), errorMessage.toString());
    }

    @Test
    @DisplayName("No circular dependencies should exist")
    void noCircularDependenciesShouldExist() throws IOException {
      // Use the ArchitectureAnalyzer to check for circular dependencies
      List<List<String>> circularDependencies =
          ArchitectureAnalyzer.detectCircularDependencies(SRC_DIR.toString());

      // Collect all circular dependencies for a detailed error message
      StringBuilder errorMessage = new StringBuilder();
      if (!circularDependencies.isEmpty()) {
        errorMessage.append("Circular dependencies found:\n");
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
      }

      assertTrue(circularDependencies.isEmpty(), errorMessage.toString());
    }
  }

  @Nested
  @DisplayName("Component Responsibility Tests")
  class ComponentResponsibilityTests {

    @Test
    @DisplayName("Entities should be in domain layer")
    void entitiesShouldBeInDomainLayer() throws IOException {
      // Check for entity classes in domain layer
      List<Path> domainFiles = findJavaFiles(SRC_DIR.resolve("domain"));

      // Look for key entity files
      boolean hasComponentEntity =
          domainFiles.stream().anyMatch(p -> p.toString().endsWith("Component.java"));
      boolean hasMachineEntity =
          domainFiles.stream().anyMatch(p -> p.toString().endsWith("Machine.java"));

      assertTrue(hasComponentEntity, "Component entity should be in domain layer");
      assertTrue(hasMachineEntity, "Machine entity should be in domain layer");
    }

    @Test
    @DisplayName("Use cases should be in application layer")
    void useCasesShouldBeInApplicationLayer() throws IOException {
      // Check for service classes in application layer
      List<Path> applicationFiles = findJavaFiles(SRC_DIR.resolve("application"));

      boolean hasComponentService =
          applicationFiles.stream().anyMatch(p -> p.toString().endsWith("ComponentService.java"));
      boolean hasMachineService =
          applicationFiles.stream().anyMatch(p -> p.toString().endsWith("MachineService.java"));

      assertTrue(hasComponentService, "ComponentService should be in application layer");
      assertTrue(hasMachineService, "MachineService should be in application layer");
    }

    @Test
    @DisplayName("Repository interfaces should be in application layer")
    void repositoryInterfacesShouldBeInApplicationLayer() throws IOException {
      Path portsDir = SRC_DIR.resolve("application").resolve("port");
      List<Path> portFiles = findJavaFiles(portsDir);

      boolean hasComponentRepository =
          portFiles.stream().anyMatch(p -> p.toString().endsWith("ComponentRepository.java"));

      assertTrue(
          hasComponentRepository,
          "ComponentRepository interface should be in application.port package");
    }

    @Test
    @DisplayName("Repository implementations should be in infrastructure layer")
    void repositoryImplementationsShouldBeInInfrastructureLayer() throws IOException {
      // Implementations should be in infrastructure layer
      List<Path> infrastructureFiles = findJavaFiles(SRC_DIR.resolve("infrastructure"));

      boolean hasRepositoryImpl =
          infrastructureFiles.stream()
              .anyMatch(
                  p ->
                      p.toString().contains("Repository")
                          && !p.toString().contains("Interface.java"));

      assertTrue(hasRepositoryImpl, "Repository implementations should be in infrastructure layer");
    }
  }

  @Nested
  @DisplayName("Dependency Inversion Tests")
  class DependencyInversionTests {

    @Test
    @DisplayName("Application layer should define interfaces implemented by infrastructure")
    void applicationLayerShouldDefineInterfacesImplementedByInfrastructure() throws IOException {
      // Get interfaces from application.port package
      Path portsDir = SRC_DIR.resolve("application").resolve("port");
      List<Path> interfaceFiles = findJavaFiles(portsDir);

      // Get implementations from infrastructure layer
      List<Path> infrastructureFiles = findJavaFiles(SRC_DIR.resolve("infrastructure"));

      // For each interface, check if there's an implementation
      for (Path interfaceFile : interfaceFiles) {
        String interfaceName = interfaceFile.getFileName().toString();
        interfaceName = interfaceName.substring(0, interfaceName.length() - 5); // Remove .java

        // Read content to check if it's an enum or interface
        String interfaceContent = Files.readString(interfaceFile);

        // List of interfaces exempt from implementation requirement during migration
        // Also includes enums and package-info files which don't need implementations
        Set<String> exemptInterfaces =
            Set.of("package-info", "S8rFacade", "ServiceFactory", "MachineRepository");
        if (exemptInterfaces.contains(interfaceName)) {
          continue;
        }

        // Skip enums - they don't need implementations
        if (interfaceContent.contains("public enum ")) {
          continue;
        }

        // Skip classes that are not interfaces
        if (!interfaceContent.contains("public interface ")) {
          continue;
        }

        final String finalInterfaceName = interfaceName;
        boolean hasImplementation =
            infrastructureFiles.stream()
                .anyMatch(
                    p -> {
                      try {
                        String content = Files.readString(p);
                        return content.contains("implements " + finalInterfaceName);
                      } catch (IOException e) {
                        return false;
                      }
                    });

        assertTrue(
            hasImplementation,
            "Interface "
                + interfaceName
                + " should have an implementation in infrastructure layer");
      }
    }

    @Test
    @DisplayName("Application services should use interfaces not implementations")
    void applicationServicesShouldUseInterfacesNotImplementations() throws IOException {
      // Get service files from application layer
      Path servicesDir = SRC_DIR.resolve("application").resolve("service");
      List<Path> serviceFiles = findJavaFiles(servicesDir);

      // Set of service files that are allowed to break the rule during migration
      Set<String> exemptServices =
          Set.of(
              "ProjectInitializationService.java",
              "ComponentService.java",
              "DataFlowService.java",
              "MachineService.java",
              "package-info.java");

      for (Path serviceFile : serviceFiles) {
        String filename = serviceFile.getFileName().toString();

        // Skip exempt services
        if (exemptServices.contains(filename)) {
          continue;
        }

        String content = Files.readString(serviceFile);

        // Check that it imports interfaces from application.port
        assertTrue(
            content.contains("import org.s8r.application.port"),
            "Service should import interfaces from application.port");

        // Check that it doesn't import implementations from infrastructure
        assertFalse(
            content.contains("import org.s8r.infrastructure"),
            "Service should not import directly from infrastructure");
      }
    }
  }

  @Nested
  @DisplayName("Package Organization Tests")
  class PackageOrganizationTests {

    @Test
    @DisplayName("Each package should have a package-info.java file")
    void eachPackageShouldHavePackageInfoFile() throws IOException {
      List<String> packagesWithoutInfo = new ArrayList<>();

      // Find all directories under src
      try (Stream<Path> paths = Files.walk(SRC_DIR)) {
        List<Path> directories = paths.filter(Files::isDirectory).collect(Collectors.toList());

        // Check each directory for a package-info.java file
        for (Path dir : directories) {
          // Skip directories that don't contain any Java files (other than package-info.java)
          // These are organizational directories without actual source files
          boolean hasJavaFiles;
          try (Stream<Path> filesInDir = Files.list(dir)) {
            hasJavaFiles =
                filesInDir.anyMatch(
                    f ->
                        f.toString().endsWith(".java")
                            && !f.getFileName().toString().equals("package-info.java"));
          }
          if (!hasJavaFiles) {
            continue;
          }

          Path packageInfo = dir.resolve("package-info.java");
          if (!Files.exists(packageInfo)) {
            // Convert Path to package name
            String relativePath = SRC_DIR.relativize(dir).toString();
            if (!relativePath.isEmpty()) {
              String packageName = "org.s8r." + relativePath.replace("/", ".");
              // Skip legacy packages that are being migrated
              boolean isLegacy = LEGACY_PACKAGES.stream().anyMatch(packageName::startsWith);
              if (!isLegacy) {
                packagesWithoutInfo.add(packageName);
              }
            }
          }
        }
      }

      if (!packagesWithoutInfo.isEmpty()) {
        fail(
            "The following packages are missing package-info.java: "
                + String.join(", ", packagesWithoutInfo));
      }
    }

    @Test
    @DisplayName("Package structure should be hierarchical with clear boundaries")
    void packageStructureShouldBeHierarchicalWithClearBoundaries() throws IOException {
      // Check that package structure follows clean architecture
      Set<String> foundLayers = new HashSet<>();

      // First level under src should be clean architecture layers
      try (Stream<Path> paths = Files.list(SRC_DIR)) {
        paths
            .filter(Files::isDirectory)
            .map(p -> p.getFileName().toString())
            .forEach(foundLayers::add);
      }

      // Expected layers
      Set<String> expectedLayers = Set.of("domain", "application", "infrastructure", "adapter");

      // All expected layers should be present
      for (String layer : expectedLayers) {
        assertTrue(
            foundLayers.contains(layer), "Package structure should include '" + layer + "' layer");
      }

      // Make sure no unexpected layers are present at the top level
      foundLayers.removeAll(expectedLayers);
      foundLayers.remove("core"); // core is allowed as a legacy package
      foundLayers.remove("tube"); // tube is allowed as a legacy package
      foundLayers.remove("test"); // test is allowed for test utilities
      foundLayers.remove("component"); // component is allowed as a legacy package

      if (!foundLayers.isEmpty()) {
        fail(
            "Unexpected top-level packages found: "
                + String.join(", ", foundLayers)
                + ". Top-level packages should be limited to clean architecture layers.");
      }
    }
  }

  /** Helper method to recursively find all Java files under a directory. */
  private List<Path> findJavaFiles(Path directory) throws IOException {
    if (!Files.exists(directory)) {
      return Collections.emptyList();
    }

    try (Stream<Path> walk = Files.walk(directory)) {
      return walk.filter(p -> !Files.isDirectory(p))
          .filter(p -> p.toString().endsWith(".java"))
          .collect(Collectors.toList());
    }
  }
}
