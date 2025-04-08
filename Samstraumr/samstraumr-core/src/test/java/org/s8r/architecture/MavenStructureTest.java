/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.architecture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Architecture tests for Maven project structure.
 * 
 * <p>These tests verify that the Maven project structure follows the established conventions.
 */
@Tag("architecture")
@Tag("maven")
@DisplayName("Maven Structure Tests")
public class MavenStructureTest {
    
    private static final Path PROJECT_ROOT = Paths.get(System.getProperty("user.dir")).getParent().getParent();
    
    @Test
    @DisplayName("Should have correct Maven structure")
    public void shouldHaveCorrectMavenStructure() {
        // Verify root POM exists
        Path rootPom = PROJECT_ROOT.resolve("pom.xml");
        assertTrue(Files.exists(rootPom), "Root pom.xml should exist");
        
        // Verify main module POM exists
        Path modulePom = PROJECT_ROOT.resolve("Samstraumr").resolve("pom.xml");
        assertTrue(Files.exists(modulePom), "Module pom.xml should exist");
        
        // Verify core module POM exists
        Path corePom = PROJECT_ROOT.resolve("Samstraumr").resolve("samstraumr-core").resolve("pom.xml");
        assertTrue(Files.exists(corePom), "Core module pom.xml should exist");
    }
    
    @Test
    @DisplayName("Should have correct source structure")
    public void shouldHaveCorrectSourceStructure() {
        // Verify main source directory structure
        Path mainJava = PROJECT_ROOT.resolve("Samstraumr/samstraumr-core/src/main/java");
        assertTrue(Files.exists(mainJava), "Main Java source directory should exist");
        
        // Verify test source directory structure
        Path testJava = PROJECT_ROOT.resolve("Samstraumr/samstraumr-core/src/test/java");
        assertTrue(Files.exists(testJava), "Test Java source directory should exist");
        
        // Verify resources directory structure
        Path mainResources = PROJECT_ROOT.resolve("Samstraumr/samstraumr-core/src/main/resources");
        assertTrue(Files.exists(mainResources), "Main resources directory should exist");
        
        Path testResources = PROJECT_ROOT.resolve("Samstraumr/samstraumr-core/src/test/resources");
        assertTrue(Files.exists(testResources), "Test resources directory should exist");
    }
    
    @Test
    @DisplayName("Should have correct package structure")
    public void shouldHaveCorrectPackageStructure() {
        // Verify core packages
        Path basePackage = PROJECT_ROOT.resolve("Samstraumr/samstraumr-core/src/main/java/org/s8r");
        assertTrue(Files.exists(basePackage), "Base package should exist");
        
        // Check for key packages in clean architecture
        List<String> requiredPackages = List.of(
            "application",
            "domain",
            "infrastructure",
            "component"
        );
        
        for (String pkg : requiredPackages) {
            Path packagePath = basePackage.resolve(pkg);
            assertTrue(Files.exists(packagePath), "Package " + pkg + " should exist");
        }
    }
}