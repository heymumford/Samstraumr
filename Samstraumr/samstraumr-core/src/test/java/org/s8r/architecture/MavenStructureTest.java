/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.s8r.test.annotation.ATL;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that verify the Maven project structure follows conventions.
 * These tests ensure that the project organization is maintainable
 * and follows clean architecture principles.
 */
@ATL
@DisplayName("Maven Structure Tests")
public class MavenStructureTest {

    private static final Path PROJECT_ROOT = Paths.get(System.getProperty("user.dir")).getParent();

    @Nested
    @DisplayName("Basic Maven Structure")
    class BasicMavenStructure {
        
        @ParameterizedTest(name = "Module {0} should have pom.xml")
        @ValueSource(strings = {
            ".",
            "Samstraumr",
            "Samstraumr/samstraumr-core"
        })
        void eachModuleShouldHavePomXml(String modulePath) {
            Path pomPath = PROJECT_ROOT.resolve(modulePath).resolve("pom.xml");
            assertTrue(
                Files.exists(pomPath) && Files.isRegularFile(pomPath),
                "Module should have pom.xml: " + modulePath
            );
        }
        
        @Test
        @DisplayName("Core module should have clean architecture layers")
        void coreModuleShouldHaveCleanArchitectureLayers() {
            String[] layers = {"domain", "application", "infrastructure"};
            Path basePackage = PROJECT_ROOT.resolve("Samstraumr/samstraumr-core/src/main/java/org/s8r");
            
            for (String layer : layers) {
                Path layerPath = basePackage.resolve(layer);
                assertTrue(
                    Files.exists(layerPath) && Files.isDirectory(layerPath),
                    "Clean Architecture layer should exist: " + layer
                );
            }
        }
    }
    
    @Nested
    @DisplayName("Required Maven Files")
    class RequiredMavenFiles {
        
        @ParameterizedTest(name = "File {0} should exist")
        @ValueSource(strings = {
            "pom.xml",
            "Samstraumr/pom.xml",
            "Samstraumr/samstraumr-core/pom.xml"
        })
        void requiredFilesExist(String filePath) {
            Path path = PROJECT_ROOT.resolve(filePath);
            assertTrue(
                Files.exists(path) && Files.isRegularFile(path),
                "Required file should exist: " + filePath
            );
        }
    }
    
    @Nested
    @DisplayName("Source Directories")
    class SourceDirectories {
        
        @ParameterizedTest(name = "Directory {0} should exist")
        @ValueSource(strings = {
            "Samstraumr/samstraumr-core/src/main/java",
            "Samstraumr/samstraumr-core/src/test/java",
            "Samstraumr/samstraumr-core/src/main/resources",
            "Samstraumr/samstraumr-core/src/test/resources"
        })
        void sourceDirectoriesExist(String dirPath) {
            Path path = PROJECT_ROOT.resolve(dirPath);
            assertTrue(
                Files.exists(path) && Files.isDirectory(path),
                "Source directory should exist: " + dirPath
            );
        }
    }
    
    @Nested
    @DisplayName("Required Packages")
    class RequiredPackages {
        
        static Stream<String> requiredPackages() {
            return Stream.of(
                "domain",
                "application",
                "infrastructure",
                "component",
                "component/identity",
                "component/composite",
                "component/machine",
                "application/port",
                "application/service"
            );
        }
        
        @ParameterizedTest(name = "Package {0} should exist")
        @MethodSource("requiredPackages")
        void requiredPackagesExist(String packagePath) {
            Path path = PROJECT_ROOT.resolve("Samstraumr/samstraumr-core/src/main/java/org/s8r")
                                   .resolve(packagePath);
            assertTrue(
                Files.exists(path) && Files.isDirectory(path),
                "Required package should exist: " + packagePath
            );
        }
    }
    
    @Nested
    @DisplayName("Port Interface Packages")
    class PortInterfacePackages {
        
        static Stream<String> portPackages() {
            return Stream.of(
                "application/port",
                "infrastructure/cache",
                "infrastructure/filesystem",
                "infrastructure/notification",
                "infrastructure/event",
                "infrastructure/security"
            );
        }
        
        @ParameterizedTest(name = "Port package {0} should exist")
        @MethodSource("portPackages")
        void portPackagesExist(String packagePath) {
            Path path = PROJECT_ROOT.resolve("Samstraumr/samstraumr-core/src/main/java/org/s8r")
                                   .resolve(packagePath);
            assertTrue(
                Files.exists(path) && Files.isDirectory(path),
                "Port interface package should exist: " + packagePath
            );
        }
    }
}