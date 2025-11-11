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
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for validating package structure compliance as described in ADR-0005.
 *
 * <p>This test suite verifies that the package structure aligns with Clean Architecture and follows
 * the defined organizational patterns.
 */
@UnitTest
@Tag("architecture")
@Tag("adr-validation")
@DisplayName("Package Structure Tests (ADR-0005)")
public class PackageStructureTest {

  private static final Path SRC_DIR =
      Paths.get(
          "/home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/samstraumr-core/src/main/java/org/s8r");

  @Nested
  @DisplayName("Layer Organization Tests")
  class LayerOrganizationTests {

    @Test
    @DisplayName("Source directory should contain clean architecture layers")
    void sourceDirectoryShouldContainCleanArchitectureLayers() throws IOException {
      // Get all directories at the top level
      try (Stream<Path> paths = Files.list(SRC_DIR)) {
        List<String> dirs =
            paths
                .filter(Files::isDirectory)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

        assertTrue(dirs.contains("domain"), "Source should contain domain layer");
        assertTrue(dirs.contains("application"), "Source should contain application layer");
        assertTrue(dirs.contains("infrastructure"), "Source should contain infrastructure layer");
        assertTrue(dirs.contains("adapter"), "Source should contain adapter layer");
      }
    }

    @Test
    @DisplayName("Adapter layer should have in/out separation")
    void adapterLayerShouldHaveInOutSeparation() throws IOException {
      Path adapterDir = SRC_DIR.resolve("adapter");

      try (Stream<Path> paths = Files.list(adapterDir)) {
        List<String> dirs =
            paths
                .filter(Files::isDirectory)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

        assertTrue(dirs.contains("in"), "Adapter layer should have 'in' subdirectory");
        assertTrue(dirs.contains("out"), "Adapter layer should have 'out' subdirectory");
      }
    }
  }

  @Nested
  @DisplayName("Package Component Tests")
  class PackageComponentTests {

    @Test
    @DisplayName("Domain layer should contain expected components")
    void domainLayerShouldContainExpectedComponents() throws IOException {
      Path domainDir = SRC_DIR.resolve("domain");

      try (Stream<Path> paths = Files.list(domainDir)) {
        List<String> dirs =
            paths
                .filter(Files::isDirectory)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

        assertTrue(dirs.contains("component"), "Domain should have component package");
        assertTrue(dirs.contains("identity"), "Domain should have identity package");
        assertTrue(dirs.contains("lifecycle"), "Domain should have lifecycle package");
        assertTrue(dirs.contains("event"), "Domain should have event package");
        assertTrue(dirs.contains("exception"), "Domain should have exception package");
      }
    }

    @Test
    @DisplayName("Application layer should contain expected components")
    void applicationLayerShouldContainExpectedComponents() throws IOException {
      Path applicationDir = SRC_DIR.resolve("application");

      try (Stream<Path> paths = Files.list(applicationDir)) {
        List<String> dirs =
            paths
                .filter(Files::isDirectory)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

        assertTrue(dirs.contains("service"), "Application should have service package");
        assertTrue(dirs.contains("port"), "Application should have port package");
        assertTrue(dirs.contains("dto"), "Application should have dto package");
      }
    }

    @Test
    @DisplayName("Infrastructure layer should contain expected components")
    void infrastructureLayerShouldContainExpectedComponents() throws IOException {
      Path infrastructureDir = SRC_DIR.resolve("infrastructure");

      try (Stream<Path> paths = Files.list(infrastructureDir)) {
        List<String> dirs =
            paths
                .filter(Files::isDirectory)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

        assertTrue(dirs.contains("persistence"), "Infrastructure should have persistence package");
        assertTrue(dirs.contains("event"), "Infrastructure should have event package");
        assertTrue(dirs.contains("logging"), "Infrastructure should have logging package");
        assertTrue(dirs.contains("config"), "Infrastructure should have config package");
      }
    }
  }

  @Nested
  @DisplayName("File Organization Tests")
  class FileOrganizationTests {

    @Test
    @DisplayName("Package-info files should exist in core packages")
    void packageInfoFilesShouldExistInCorePackages() throws IOException {
      // Check domain packages
      assertTrue(
          Files.exists(SRC_DIR.resolve("domain").resolve("package-info.java")),
          "Domain layer should have package-info.java");

      // Check application packages
      assertTrue(
          Files.exists(SRC_DIR.resolve("application").resolve("package-info.java")),
          "Application layer should have package-info.java");

      // Check infrastructure packages
      assertTrue(
          Files.exists(SRC_DIR.resolve("infrastructure").resolve("package-info.java")),
          "Infrastructure layer should have package-info.java");

      // Check adapter packages
      assertTrue(
          Files.exists(SRC_DIR.resolve("adapter").resolve("package-info.java")),
          "Adapter layer should have package-info.java");
    }

    @Test
    @DisplayName("Files should follow consistent naming patterns")
    void filesShouldFollowConsistentNamingPatterns() throws IOException {
      // Check naming patterns in domain
      List<Path> componentFiles = findJavaFiles(SRC_DIR.resolve("domain").resolve("component"));

      boolean hasComponentEntity =
          componentFiles.stream()
              .anyMatch(p -> p.getFileName().toString().equals("Component.java"));

      assertTrue(hasComponentEntity, "Domain layer should have Component.java entity");

      // Check naming patterns in application services
      List<Path> serviceFiles = findJavaFiles(SRC_DIR.resolve("application").resolve("service"));

      boolean hasComponentService =
          serviceFiles.stream().anyMatch(p -> p.getFileName().toString().endsWith("Service.java"));

      assertTrue(hasComponentService, "Application layer should have *Service.java files");

      // Check naming patterns in application ports
      List<Path> portFiles = findJavaFiles(SRC_DIR.resolve("application").resolve("port"));

      boolean hasRepository =
          portFiles.stream().anyMatch(p -> p.getFileName().toString().endsWith("Repository.java"));

      assertTrue(hasRepository, "Port package should have *Repository.java interfaces");

      // Check naming patterns in infrastructure
      List<Path> repoImplFiles =
          findJavaFiles(SRC_DIR.resolve("infrastructure").resolve("persistence"));

      boolean hasRepoImpl =
          repoImplFiles.stream()
              .anyMatch(
                  p ->
                      p.getFileName().toString().startsWith("InMemory")
                          && p.getFileName().toString().endsWith("Repository.java"));

      assertTrue(
          hasRepoImpl,
          "Infrastructure layer should have repository implementations following [Technology][Entity]Repository.java pattern");
    }
  }

  @Nested
  @DisplayName("Package Depth Tests")
  class PackageDepthTests {

    @Test
    @DisplayName("Package depth should not exceed reasonable limits")
    void packageDepthShouldNotExceedReasonableLimits() throws IOException {
      int maxDepth = findMaxPackageDepth(SRC_DIR, 0);

      assertTrue(
          maxDepth <= 6, "Maximum package depth should not exceed 6 levels, but found " + maxDepth);
    }
  }

  @Nested
  @DisplayName("Package Cohesion Tests")
  class PackageCohesionTests {

    @Test
    @DisplayName("Packages should contain related files")
    void packagesShouldContainRelatedFiles() throws IOException {
      // Check domain entity packages
      checkPackageCoherence(SRC_DIR.resolve("domain").resolve("component"), "Component");

      // Check application service packages
      checkPackageCoherence(SRC_DIR.resolve("application").resolve("service"), "Service");

      // Check infrastructure packages
      checkPackageCoherence(SRC_DIR.resolve("infrastructure").resolve("persistence"), "Repository");
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

  /** Helper method to find the maximum package depth. */
  private int findMaxPackageDepth(Path directory, int currentDepth) throws IOException {
    if (!Files.isDirectory(directory)) {
      return currentDepth;
    }

    int maxDepth = currentDepth;

    try (Stream<Path> subDirs = Files.list(directory)) {
      List<Path> dirs = subDirs.filter(Files::isDirectory).collect(Collectors.toList());

      for (Path dir : dirs) {
        int depth = findMaxPackageDepth(dir, currentDepth + 1);
        maxDepth = Math.max(maxDepth, depth);
      }
    }

    return maxDepth;
  }

  /** Helper method to check if files in a package are cohesive (related). */
  private void checkPackageCoherence(Path directory, String commonSuffix) throws IOException {
    if (!Files.exists(directory)) {
      return;
    }

    List<Path> files = findJavaFiles(directory);

    if (files.size() <= 1) {
      return; // Not enough files to check coherence
    }

    long countRelated =
        files.stream()
            .map(p -> p.getFileName().toString())
            .filter(name -> name.contains(commonSuffix) || name.equals("package-info.java"))
            .count();

    double coherenceRatio = (double) countRelated / files.size();

    assertTrue(
        coherenceRatio >= 0.75,
        "Package "
            + directory
            + " should have at least 75% related files, but found "
            + (coherenceRatio * 100)
            + "%");
  }
}
