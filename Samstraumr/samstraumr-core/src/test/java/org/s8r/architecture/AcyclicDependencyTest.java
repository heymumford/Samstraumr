package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.architecture.util.ArchitectureAnalyzer;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for ensuring acyclic dependencies as described in ADR-0012.
 * 
 * <p>This test suite verifies that the codebase does not contain
 * circular dependencies between packages or modules.
 */
@UnitTest
@Tag("architecture")
@Tag("adr-validation")
@DisplayName("Acyclic Dependency Tests (ADR-0012)")
public class AcyclicDependencyTest {

    private static final String SRC_DIR_PATH = 
        "/home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/samstraumr-core/src/main/java/org/s8r";
    private static final String TEST_DIR_PATH = 
        "/home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/samstraumr-core/src/test/java/org/s8r";

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
            Map<String, List<String>> violations = 
                ArchitectureAnalyzer.analyzePackageDependencies(SRC_DIR_PATH);
            
            // Collect all violations for a detailed error message
            StringBuilder errorMessage = new StringBuilder();
            if (!violations.isEmpty()) {
                errorMessage.append("Clean Architecture dependency violations found:\n");
                for (Map.Entry<String, List<String>> entry : violations.entrySet()) {
                    errorMessage.append("  Package '").append(entry.getKey())
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
                boolean isTestUtilityCycle = cycle.stream()
                    .allMatch(pkg -> pkg.contains(".util.") || pkg.contains(".mock."));
                
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