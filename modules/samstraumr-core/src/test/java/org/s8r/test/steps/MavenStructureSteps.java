/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.steps;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/** Step definitions for Maven structure tests. */
public class MavenStructureSteps {

  private static final Path PROJECT_ROOT =
      Paths.get(System.getProperty("user.dir")).getParent().getParent();
  private Path currentPath;
  private boolean fileExists;

  @Given("I am in the project root directory")
  public void iAmInTheProjectRootDirectory() {
    currentPath = PROJECT_ROOT;
    Assertions.assertTrue(Files.exists(currentPath), "Project root directory should exist");
  }

  @When("I look for the file {string}")
  public void iLookForTheFile(String filePath) {
    currentPath = PROJECT_ROOT.resolve(filePath);
    fileExists = Files.exists(currentPath);
  }

  @Then("the file should exist")
  public void theFileShouldExist() {
    Assertions.assertTrue(fileExists, "File should exist: " + currentPath);
  }

  @Then("the file should not exist")
  public void theFileShouldNotExist() {
    Assertions.assertFalse(fileExists, "File should not exist: " + currentPath);
  }

  @When("I check for the directory {string}")
  public void iCheckForTheDirectory(String directoryPath) {
    currentPath = PROJECT_ROOT.resolve(directoryPath);
    fileExists = Files.exists(currentPath) && Files.isDirectory(currentPath);
  }

  @Then("the directory should exist")
  public void theDirectoryShouldExist() {
    Assertions.assertTrue(fileExists, "Directory should exist: " + currentPath);
  }

  @Then("the directory should not exist")
  public void theDirectoryShouldNotExist() {
    Assertions.assertFalse(fileExists, "Directory should not exist: " + currentPath);
  }

  @When("I check for required packages")
  public void iCheckForRequiredPackages() {
    // Implementation will be called by specific steps
  }

  @Then("the following packages should exist:")
  public void theFollowingPackagesShouldExist(List<String> packages) {
    Path basePackage = PROJECT_ROOT.resolve("Samstraumr/samstraumr-core/src/main/java/org/s8r");

    for (String pkg : packages) {
      Path packagePath = basePackage.resolve(pkg.replace(".", "/"));
      Assertions.assertTrue(
          Files.exists(packagePath) && Files.isDirectory(packagePath),
          "Package should exist: " + pkg);
    }
  }

  @Then("the core module should have the clean architecture layers")
  public void theCoreModuleShouldHaveCleanArchitectureLayers() {
    Path basePackage = PROJECT_ROOT.resolve("Samstraumr/samstraumr-core/src/main/java/org/s8r");

    String[] layers = {"domain", "application", "infrastructure"};

    for (String layer : layers) {
      Path layerPath = basePackage.resolve(layer);
      Assertions.assertTrue(
          Files.exists(layerPath) && Files.isDirectory(layerPath),
          "Clean Architecture layer should exist: " + layer);
    }
  }

  @And("each module should have a pom.xml file")
  public void eachModuleShouldHaveAPomXmlFile() {
    List<String> modules = Arrays.asList(".", "Samstraumr", "Samstraumr/samstraumr-core");

    for (String module : modules) {
      Path pomPath = PROJECT_ROOT.resolve(module).resolve("pom.xml");
      Assertions.assertTrue(
          Files.exists(pomPath) && Files.isRegularFile(pomPath),
          "Module should have pom.xml: " + module);
    }
  }
}
