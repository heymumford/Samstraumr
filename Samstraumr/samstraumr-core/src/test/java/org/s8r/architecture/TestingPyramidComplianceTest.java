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
 * Tests for validating compliance with the Testing Pyramid strategy as described in ADR-0006.
 * 
 * <p>This test suite verifies that the testing infrastructure aligns with the testing pyramid
 * strategy, covering all levels from unit to acceptance tests.
 */
@UnitTest
@Tag("architecture")
@Tag("adr-validation")
@DisplayName("Testing Pyramid Compliance Tests (ADR-0006)")
public class TestingPyramidComplianceTest {

    private static final Path TEST_DIR = Paths.get("/home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/samstraumr-core/src/test");
    private static final Path TEST_JAVA_DIR = TEST_DIR.resolve("java/org/s8r");
    private static final Path TEST_RESOURCES_DIR = TEST_DIR.resolve("resources");

    @Nested
    @DisplayName("Test Structure Tests")
    class TestStructureTests {
        
        @Test
        @DisplayName("Test directory should contain Java and resources directories")
        void testDirectoryShouldContainJavaAndResourcesDirectories() {
            assertTrue(Files.exists(TEST_JAVA_DIR), "Test Java directory should exist");
            assertTrue(Files.exists(TEST_RESOURCES_DIR), "Test resources directory should exist");
        }
        
        @Test
        @DisplayName("Test resources should contain feature files")
        void testResourcesShouldContainFeatureFiles() throws IOException {
            List<Path> featureFiles = findFiles(TEST_RESOURCES_DIR, ".feature");
            
            assertFalse(featureFiles.isEmpty(), "Test resources should contain feature files");
        }
        
        @Test
        @DisplayName("Test Java directory should contain test classes")
        void testJavaDirectoryShouldContainTestClasses() throws IOException {
            List<Path> testFiles = findFiles(TEST_JAVA_DIR, "Test.java");
            
            assertFalse(testFiles.isEmpty(), "Test Java directory should contain test classes");
        }
    }

    @Nested
    @DisplayName("Test Levels Tests")
    class TestLevelsTests {
        
        @Test
        @DisplayName("L0 (Unit) level tests should exist")
        void l0UnitLevelTestsShouldExist() throws IOException {
            // Check for unit/tube test files
            boolean hasTubeTests = hasTestFilesMatching("tube") || 
                                  hasFeatureFilesInDirectory("L0_Tube") ||
                                  hasFeatureFilesInDirectory("L0_Core");
            
            boolean hasPortTests = hasTestFilesMatching("Port") ||
                                  hasTestFilesMatching("Adapter");
            
            assertTrue(hasTubeTests || hasPortTests, "Should have L0 (Unit/Tube) level tests");
        }
        
        @Test
        @DisplayName("L1 (Component) level tests should exist")
        void l1ComponentLevelTestsShouldExist() throws IOException {
            // Check for component/composite test files
            boolean hasComponentTests = hasTestFilesMatching("component") || 
                                      hasTestFilesMatching("composite") ||
                                      hasFeatureFilesInDirectory("L1_Composite") ||
                                      hasFeatureFilesInDirectory("L1_Bundle");
            
            boolean hasServiceTests = hasTestFilesMatching("Service");
            
            assertTrue(hasComponentTests || hasServiceTests, "Should have L1 (Component/Composite) level tests");
        }
        
        @Test
        @DisplayName("L2 (Integration) level tests should exist")
        void l2IntegrationLevelTestsShouldExist() throws IOException {
            // Check for integration/machine test files
            boolean hasIntegrationTests = hasTestFilesMatching("machine") || 
                                        hasFeatureFilesInDirectory("L2_Machine");
            
            boolean hasPortIntegrationTests = hasFeatureFilesInDirectory("port-interfaces") ||
                                            hasTestFilesMatching("PortRunner");
            
            assertTrue(hasIntegrationTests || hasPortIntegrationTests, "Should have L2 (Integration/Machine) level tests");
        }
        
        @Test
        @DisplayName("L3 (System) level tests should exist")
        void l3SystemLevelTestsShouldExist() throws IOException {
            // Check for system level tests
            boolean hasSystemTests = hasTestFilesMatching("system") || 
                                   hasFeatureFilesInDirectory("L3_System");
            
            assertTrue(hasSystemTests, "Should have L3 (System) level tests");
        }
    }

    @Nested
    @DisplayName("Test Annotation Tests")
    class TestAnnotationTests {
        
        @Test
        @DisplayName("Unit test annotations should exist and be used")
        void unitTestAnnotationsShouldExistAndBeUsed() throws IOException {
            Path unitTestAnnotation = TEST_JAVA_DIR.resolve("test/annotation/UnitTest.java");
            assertTrue(Files.exists(unitTestAnnotation), "UnitTest annotation should exist");
            
            // Check if the annotation is used
            List<Path> testFiles = findFiles(TEST_JAVA_DIR, "Test.java");
            long unitTestCount = countFilesWithContent(testFiles, "@UnitTest");
            
            assertTrue(unitTestCount > 0, "UnitTest annotation should be used in test files");
        }
        
        @Test
        @DisplayName("Component test annotations should exist and be used")
        void componentTestAnnotationsShouldExistAndBeUsed() throws IOException {
            Path componentTestAnnotation = TEST_JAVA_DIR.resolve("test/annotation/CompositeTest.java");
            assertTrue(Files.exists(componentTestAnnotation), "CompositeTest annotation should exist");
            
            // Check if the annotation is used
            List<Path> testFiles = findFiles(TEST_JAVA_DIR, "Test.java");
            long componentTestCount = countFilesWithContent(testFiles, "@CompositeTest");
            
            assertTrue(componentTestCount > 0, "CompositeTest annotation should be used in test files");
        }
        
        @Test
        @DisplayName("ATL/BTL annotations should exist")
        void atlBtlAnnotationsShouldExist() throws IOException {
            Path atlAnnotation = TEST_JAVA_DIR.resolve("test/annotation/ATL.java");
            Path btlAnnotation = TEST_JAVA_DIR.resolve("test/annotation/BTL.java");
            
            assertTrue(Files.exists(atlAnnotation), "ATL annotation should exist");
            assertTrue(Files.exists(btlAnnotation), "BTL annotation should exist");
        }
    }

    @Nested
    @DisplayName("Test Runner Tests")
    class TestRunnerTests {
        
        @Test
        @DisplayName("Test runners should exist for different test types")
        void testRunnersShouldExistForDifferentTestTypes() throws IOException {
            // Check for various test runners
            List<Path> runnerFiles = findFiles(TEST_JAVA_DIR, "Run");
            
            // Check specific runners
            boolean hasComponentRunner = runnerFiles.stream()
                .anyMatch(p -> p.getFileName().toString().contains("Component"));
            
            boolean hasTubeRunner = runnerFiles.stream()
                .anyMatch(p -> p.getFileName().toString().contains("Tube"));
            
            boolean hasMachineRunner = runnerFiles.stream()
                .anyMatch(p -> p.getFileName().toString().contains("Machine"));
            
            assertTrue(hasComponentRunner, "Should have a Component test runner");
            assertTrue(hasTubeRunner, "Should have a Tube test runner");
            assertTrue(hasMachineRunner, "Should have a Machine test runner");
        }
        
        @Test
        @DisplayName("Cucumber runner should exist for BDD tests")
        void cucumberRunnerShouldExistForBddTests() throws IOException {
            List<Path> cucumberRunners = findFiles(TEST_JAVA_DIR, "Cucumber");
            
            assertFalse(cucumberRunners.isEmpty(), "Should have Cucumber test runners");
        }
    }

    @Nested
    @DisplayName("BDD Tests")
    class BddTests {
        
        @Test
        @DisplayName("Feature files should follow standard structure")
        void featureFilesShouldFollowStandardStructure() throws IOException {
            List<Path> featureFiles = findFiles(TEST_RESOURCES_DIR, ".feature");
            
            for (Path featureFile : featureFiles) {
                if (Files.exists(featureFile)) {
                    String content = Files.readString(featureFile);
                    
                    // Check if it has standard Gherkin structure
                    assertTrue(
                        content.contains("Feature:"), 
                        "Feature file should have Feature section: " + featureFile
                    );
                    
                    assertTrue(
                        content.contains("Scenario:") || content.contains("Scenario Outline:"), 
                        "Feature file should have Scenario section: " + featureFile
                    );
                    
                    assertTrue(
                        content.contains("Given") || content.contains("When") || content.contains("Then"), 
                        "Feature file should have Given/When/Then steps: " + featureFile
                    );
                }
            }
        }
        
        @Test
        @DisplayName("Step definition files should exist for feature files")
        void stepDefinitionFilesShouldExistForFeatureFiles() throws IOException {
            // Check for step definition files
            List<Path> stepFiles = findFiles(TEST_JAVA_DIR, "Steps.java");
            
            assertFalse(stepFiles.isEmpty(), "Should have step definition files");
            
            // Verify these are actual step definitions
            long actualStepFiles = countFilesWithContent(stepFiles, "@Given");
            
            assertTrue(actualStepFiles > 0, "Step files should contain @Given/@When/@Then annotations");
        }
    }
    
    @Nested
    @DisplayName("Clean Architecture Tests")
    class CleanArchitectureTests {
        
        @Test
        @DisplayName("Port interfaces should follow Clean Architecture principles")
        void portInterfacesShouldFollowCleanArchitecturePrinciples() throws IOException {
            // Check for port interfaces
            List<Path> portInterfaces = findFiles(Paths.get("/home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/samstraumr-core/src/main/java/org/s8r/application/port"), "Port.java");
            
            assertFalse(portInterfaces.isEmpty(), "Should have port interfaces");
            
            // Check for adapter implementations
            List<Path> adapters = findFiles(Paths.get("/home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/samstraumr-core/src/main/java/org/s8r/infrastructure"), "Adapter.java");
            
            assertFalse(adapters.isEmpty(), "Should have adapter implementations");
            
            // Check for service implementations
            List<Path> services = findFiles(Paths.get("/home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/samstraumr-core/src/main/java/org/s8r/application/service"), "Service.java");
            
            assertFalse(services.isEmpty(), "Should have service implementations");
        }
        
        @Test
        @DisplayName("Port interfaces should have tests following TDD principles")
        void portInterfacesShouldHaveTestsFollowingTDDPrinciples() throws IOException {
            // Get all port interfaces
            List<Path> portInterfaces = findFiles(Paths.get("/home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/samstraumr-core/src/main/java/org/s8r/application/port"), "Port.java");
            
            assertFalse(portInterfaces.isEmpty(), "Should have port interfaces");
            
            // Check each port interface has tests
            for (Path portInterface : portInterfaces) {
                String fileName = portInterface.getFileName().toString();
                String baseName = fileName.substring(0, fileName.indexOf(".java"));
                
                // Look for corresponding tests
                List<Path> contractTests = findFiles(TEST_JAVA_DIR, baseName + "ContractTest.java");
                List<Path> adapterTests = findFiles(TEST_JAVA_DIR, baseName.replace("Port", "Adapter") + "Test.java");
                List<Path> serviceTests = findFiles(TEST_JAVA_DIR, baseName.replace("Port", "Service") + "Test.java");
                
                boolean hasTests = !contractTests.isEmpty() || !adapterTests.isEmpty() || !serviceTests.isEmpty();
                
                // Only perform assertion if this is not a new port interface
                // by checking if it has an implementation
                List<Path> adapters = findFiles(Paths.get("/home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/samstraumr-core/src/main/java/org/s8r/infrastructure"), 
                        baseName.replace("Port", "Adapter") + ".java");
                
                if (!adapters.isEmpty()) {
                    assertTrue(hasTests, "Port interface " + baseName + " should have tests");
                }
            }
        }
    }
    
    /**
     * Helper method to find files with a given suffix.
     */
    private List<Path> findFiles(Path directory, String suffix) throws IOException {
        if (!Files.exists(directory)) {
            return Collections.emptyList();
        }
        
        try (Stream<Path> walk = Files.walk(directory)) {
            return walk
                .filter(p -> !Files.isDirectory(p))
                .filter(p -> p.getFileName().toString().contains(suffix))
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Helper method to check if there are test files matching a pattern.
     */
    private boolean hasTestFilesMatching(String pattern) throws IOException {
        List<Path> testFiles = findFiles(TEST_JAVA_DIR, "Test.java");
        return testFiles.stream()
            .anyMatch(p -> p.getFileName().toString().toLowerCase().contains(pattern.toLowerCase()));
    }
    
    /**
     * Helper method to check if there are feature files in a directory.
     */
    private boolean hasFeatureFilesInDirectory(String directoryName) throws IOException {
        try (Stream<Path> walk = Files.walk(TEST_RESOURCES_DIR)) {
            return walk
                .filter(Files::isDirectory)
                .filter(p -> p.getFileName().toString().equals(directoryName))
                .findAny()
                .isPresent();
        }
    }
    
    /**
     * Helper method to count files that contain specific content.
     */
    private long countFilesWithContent(List<Path> files, String content) throws IOException {
        long count = 0;
        for (Path file : files) {
            if (Files.exists(file)) {
                String fileContent = Files.readString(file);
                if (fileContent.contains(content)) {
                    count++;
                }
            }
        }
        return count;
    }
}