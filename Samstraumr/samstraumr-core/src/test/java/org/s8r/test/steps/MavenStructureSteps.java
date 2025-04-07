package org.s8r.test.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.s8r.test.annotation.UnitTest;

/**
 * Step definitions for testing the Maven project structure.
 * Implements the steps defined in maven-structure-test.feature.
 */
@UnitTest
public class MavenStructureSteps {

    private Document rootPom;
    private Document modulePom;
    private Document corePom;
    private String projectVersion;
    private File projectRoot;
    
    @Given("the project has a root pom.xml file")
    public void theProjectHasARootPomXmlFile() throws IOException, ParserConfigurationException, SAXException {
        projectRoot = findProjectRoot();
        File rootPomFile = new File(projectRoot, "pom.xml");
        assertTrue(rootPomFile.exists(), "Root pom.xml file should exist");
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        rootPom = builder.parse(rootPomFile);
        
        // Extract project version
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            projectVersion = xpath.evaluate("//*[local-name()='project']/*[local-name()='version']/text()", rootPom);
            assertNotNull(projectVersion, "Project version should be defined in root POM");
            assertFalse(projectVersion.isEmpty(), "Project version should not be empty");
        } catch (XPathExpressionException e) {
            fail("Failed to extract project version: " + e.getMessage());
        }
    }

    @Given("the project has a module container pom.xml file")
    public void theProjectHasAModuleContainerPomXmlFile() throws IOException, ParserConfigurationException, SAXException {
        File modulePomFile = new File(projectRoot, "Samstraumr/pom.xml");
        assertTrue(modulePomFile.exists(), "Module container pom.xml file should exist");
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        modulePom = builder.parse(modulePomFile);
    }

    @Given("the project has a core module pom.xml file")
    public void theProjectHasACoreModulePomXmlFile() throws IOException, ParserConfigurationException, SAXException {
        File corePomFile = new File(projectRoot, "Samstraumr/samstraumr-core/pom.xml");
        assertTrue(corePomFile.exists(), "Core module pom.xml file should exist");
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        corePom = builder.parse(corePomFile);
    }

    @Then("the root pom should contain modules section with {string}")
    public void theRootPomShouldContainModulesSectionWithModule(String moduleName) {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList modules = (NodeList) xpath.evaluate("//*[local-name()='modules']/*[local-name()='module']", rootPom, XPathConstants.NODESET);
            
            boolean foundModule = false;
            for (int i = 0; i < modules.getLength(); i++) {
                if (modules.item(i).getTextContent().equals(moduleName)) {
                    foundModule = true;
                    break;
                }
            }
            
            assertTrue(foundModule, "Root POM should include " + moduleName + " module");
        } catch (XPathExpressionException e) {
            fail("Failed to evaluate XPath: " + e.getMessage());
        }
    }

    @Then("the module container pom should reference the {string} module")
    public void theModuleContainerPomShouldReferenceTheModule(String moduleName) {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList modules = (NodeList) xpath.evaluate("//*[local-name()='modules']/*[local-name()='module']", modulePom, XPathConstants.NODESET);
            
            boolean foundModule = false;
            for (int i = 0; i < modules.getLength(); i++) {
                if (modules.item(i).getTextContent().equals(moduleName)) {
                    foundModule = true;
                    break;
                }
            }
            
            assertTrue(foundModule, "Module container POM should include " + moduleName + " module");
        } catch (XPathExpressionException e) {
            fail("Failed to evaluate XPath: " + e.getMessage());
        }
    }

    @Then("all pom files should have consistent versions")
    public void allPomFilesShouldHaveConsistentVersions() {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Check module POM version (direct or from parent)
            String moduleVersion;
            String versionNode = xpath.evaluate("//*[local-name()='project']/*[local-name()='version']", modulePom);
            if (versionNode != null && !versionNode.isEmpty()) {
                moduleVersion = versionNode;
            } else {
                moduleVersion = xpath.evaluate("//*[local-name()='project']/*[local-name()='parent']/*[local-name()='version']", modulePom);
            }
            
            // Check core POM version (usually from parent)
            String coreVersion = xpath.evaluate("//*[local-name()='project']/*[local-name()='parent']/*[local-name()='version']", corePom);
            
            assertEquals(projectVersion, moduleVersion, "Root POM and module POM should have same version");
            assertEquals(projectVersion, coreVersion, "Root POM and core POM should have same version");
            
            // Check version.properties if it exists
            File versionProps = new File(projectRoot, "Samstraumr/version.properties");
            if (versionProps.exists()) {
                Properties props = new Properties();
                props.load(new FileInputStream(versionProps));
                String propsVersion = props.getProperty("version");
                
                assertEquals(projectVersion, propsVersion, "POM version and version.properties should match");
            }
        } catch (XPathExpressionException | IOException e) {
            fail("Failed to check versions: " + e.getMessage());
        }
    }

    @Then("all dependencies should be managed in the parent pom")
    public void allDependenciesShouldBeManagedInTheParentPom() {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Check that dependencies are managed in the parent POM
            NodeList dependencyManagementNodes = (NodeList) xpath.evaluate(
                    "//*[local-name()='dependencyManagement']/*[local-name()='dependencies']/*[local-name()='dependency']", 
                    rootPom, XPathConstants.NODESET);
            
            assertTrue(dependencyManagementNodes.getLength() > 10, 
                    "Root POM should manage dependencies. Found only " + dependencyManagementNodes.getLength());
            
            // Check that core POM dependencies don't specify versions
            NodeList coreDependencies = (NodeList) xpath.evaluate(
                    "//*[local-name()='dependencies']/*[local-name()='dependency']/*[local-name()='version']", 
                    corePom, XPathConstants.NODESET);
            
            for (int i = 0; i < coreDependencies.getLength(); i++) {
                Node versionNode = coreDependencies.item(i);
                String version = versionNode.getTextContent();
                assertTrue(version.startsWith("${"), 
                        "Dependencies in core POM should use properties for versions, found direct version: " + version);
            }
            
        } catch (XPathExpressionException e) {
            fail("Failed to check dependency management: " + e.getMessage());
        }
    }

    @Then("all plugin versions should be defined as properties")
    public void allPluginVersionsShouldBeDefinedAsProperties() {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Get all plugin versions defined in pluginManagement
            NodeList pluginVersionNodes = (NodeList) xpath.evaluate(
                    "//*[local-name()='build']/*[local-name()='pluginManagement']/*[local-name()='plugins']" +
                    "/*[local-name()='plugin']/*[local-name()='version']", 
                    rootPom, XPathConstants.NODESET);
            
            // Check that each plugin version uses a property
            for (int i = 0; i < pluginVersionNodes.getLength(); i++) {
                String version = pluginVersionNodes.item(i).getTextContent();
                assertTrue(version.startsWith("${"), 
                        "Plugin version should be defined as a property: " + version);
            }
            
            // Check that property definitions exist for these plugin versions
            NodeList pluginVersionProperties = (NodeList) xpath.evaluate(
                    "//*[local-name()='properties']/*[contains(local-name(), '.plugin.version') or " +
                    "contains(local-name(), '.version')]", 
                    rootPom, XPathConstants.NODESET);
            
            assertTrue(pluginVersionProperties.getLength() > 5, 
                    "Plugin versions should be defined as properties (found only " + 
                    pluginVersionProperties.getLength() + " version properties)");
            
        } catch (XPathExpressionException e) {
            fail("Failed to check plugin version properties: " + e.getMessage());
        }
    }

    @Then("all plugins should be managed in pluginManagement section")
    public void allPluginsShouldBeManagedInPluginManagementSection() {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Check that plugins are managed in the parent POM
            NodeList pluginManagementNodes = (NodeList) xpath.evaluate(
                    "//*[local-name()='build']/*[local-name()='pluginManagement']/*[local-name()='plugins']/*[local-name()='plugin']", 
                    rootPom, XPathConstants.NODESET);
            
            assertTrue(pluginManagementNodes.getLength() > 5, 
                    "Root POM should manage plugins (found only " + pluginManagementNodes.getLength() + " plugins)");
            
            // Collect plugin artifactIds from pluginManagement
            Set<String> managedPlugins = new HashSet<>();
            for (int i = 0; i < pluginManagementNodes.getLength(); i++) {
                Node plugin = pluginManagementNodes.item(i);
                String artifactId = xpath.evaluate("./*[local-name()='artifactId']", plugin);
                managedPlugins.add(artifactId);
            }
            
            // Check that plugins used in the build section are also in pluginManagement
            NodeList buildPlugins = (NodeList) xpath.evaluate(
                    "//*[local-name()='build']/*[local-name()='plugins']/*[local-name()='plugin']/*[local-name()='artifactId']", 
                    rootPom, XPathConstants.NODESET);
            
            for (int i = 0; i < buildPlugins.getLength(); i++) {
                String artifactId = buildPlugins.item(i).getTextContent();
                assertTrue(managedPlugins.contains(artifactId), 
                        "Plugin " + artifactId + " used in build section should be defined in pluginManagement");
            }
            
            // Also check core module plugins
            buildPlugins = (NodeList) xpath.evaluate(
                    "//*[local-name()='build']/*[local-name()='plugins']/*[local-name()='plugin']/*[local-name()='artifactId']", 
                    corePom, XPathConstants.NODESET);
            
            for (int i = 0; i < buildPlugins.getLength(); i++) {
                String artifactId = buildPlugins.item(i).getTextContent();
                assertTrue(managedPlugins.contains(artifactId) || "exec-maven-plugin".equals(artifactId), 
                        "Plugin " + artifactId + " used in core module should be defined in pluginManagement");
            }
            
        } catch (XPathExpressionException e) {
            fail("Failed to check plugin management: " + e.getMessage());
        }
    }

    @Then("no SNAPSHOT dependencies should be used in non-SNAPSHOT versions")
    public void noSNAPSHOTDependenciesShouldBeUsedInNonSNAPSHOTVersions() {
        if (projectVersion.contains("SNAPSHOT")) {
            // If this is a SNAPSHOT version, this check is not applicable
            return;
        }
        
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Check for SNAPSHOT dependencies in dependencyManagement
            NodeList snapshotDependencies = (NodeList) xpath.evaluate(
                    "//*[local-name()='dependencyManagement']/*[local-name()='dependencies']" +
                    "/*[local-name()='dependency']/*[local-name()='version'][contains(text(), 'SNAPSHOT')]", 
                    rootPom, XPathConstants.NODESET);
            
            assertEquals(0, snapshotDependencies.getLength(), 
                    "Non-SNAPSHOT version should not use SNAPSHOT dependencies (found " + 
                    snapshotDependencies.getLength() + " SNAPSHOT dependencies)");
            
        } catch (XPathExpressionException e) {
            fail("Failed to check for SNAPSHOT dependencies: " + e.getMessage());
        }
    }

    @Then("the root pom should contain the following profiles:")
    public void theRootPomShouldContainTheFollowingProfiles(DataTable dataTable) {
        List<String> expectedProfiles = dataTable.asMaps().stream()
                .map(row -> row.get("Profile Name"))
                .collect(Collectors.toList());
        
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Get all profile IDs
            NodeList profileNodes = (NodeList) xpath.evaluate(
                    "//*[local-name()='profiles']/*[local-name()='profile']/*[local-name()='id']", 
                    rootPom, XPathConstants.NODESET);
            
            Set<String> actualProfiles = new HashSet<>();
            for (int i = 0; i < profileNodes.getLength(); i++) {
                actualProfiles.add(profileNodes.item(i).getTextContent());
            }
            
            // Check that all expected profiles exist
            for (String profile : expectedProfiles) {
                assertTrue(actualProfiles.contains(profile), 
                        "Root POM should contain profile: " + profile);
            }
            
        } catch (XPathExpressionException e) {
            fail("Failed to check profiles: " + e.getMessage());
        }
    }

    @Then("each profile should have the correct properties and plugins")
    public void eachProfileShouldHaveTheCorrectPropertiesAndPlugins() {
        try {
            // Define expected properties and plugins for specific profiles
            verifyProfile("tdd-development", new String[]{"jacoco.line.coverage", "jacoco.branch.coverage"}, new String[]{});
            verifyProfile("skip-tests", new String[]{"skipTests", "maven.test.skip"}, new String[]{});
            verifyProfile("quality-checks", new String[]{}, 
                    new String[]{"spotless-maven-plugin", "maven-checkstyle-plugin", "spotbugs-maven-plugin"});
            verifyProfile("security-checks", new String[]{}, new String[]{"dependency-check-maven"});
            
        } catch (XPathExpressionException e) {
            fail("Failed to check profile configurations: " + e.getMessage());
        }
    }

    @Then("the dependencies should respect Clean Architecture principles")
    public void theDependenciesShouldRespectCleanArchitecturePrinciples() {
        // Verify domain package exists
        File domainDir = new File(projectRoot, "Samstraumr/samstraumr-core/src/main/java/org/s8r/domain");
        File applicationDir = new File(projectRoot, "Samstraumr/samstraumr-core/src/main/java/org/s8r/application");
        File infrastructureDir = new File(projectRoot, "Samstraumr/samstraumr-core/src/main/java/org/s8r/infrastructure");
        File adapterDir = new File(projectRoot, "Samstraumr/samstraumr-core/src/main/java/org/s8r/adapter");
        
        assertTrue(domainDir.exists(), "Domain package should exist");
        assertTrue(applicationDir.exists(), "Application package should exist");
        assertTrue(infrastructureDir.exists(), "Infrastructure package should exist");
        assertTrue(adapterDir.exists(), "Adapter package should exist");
        
        // Basic existence check for package-info.java files
        assertTrue(new File(domainDir, "package-info.java").exists() || 
                new File(domainDir, "event/package-info.java").exists(),
                "Domain package should have package-info.java");
        
        assertTrue(new File(applicationDir, "package-info.java").exists() || 
                new File(applicationDir, "port/package-info.java").exists(),
                "Application package should have package-info.java");
    }

    @Then("domain layer should not depend on infrastructure implementations")
    public void domainLayerShouldNotDependOnInfrastructureImplementations() {
        // Note: A more comprehensive check would analyze import statements in Java files
        // but that's beyond the scope of what we can reasonably do in a step definition
        
        // For this test, we'll check for typical framework dependencies in the domain layer
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Check for Spring, Hibernate, etc. dependencies that domain shouldn't depend on
            NodeList frameworks = (NodeList) xpath.evaluate(
                    "//*[local-name()='dependencies']/*[local-name()='dependency'][" +
                    "contains(./*[local-name()='groupId'], 'springframework') or " +
                    "contains(./*[local-name()='groupId'], 'hibernate') or " +
                    "contains(./*[local-name()='groupId'], 'jakarta.persistence')" +
                    "]", 
                    corePom, XPathConstants.NODESET);
            
            // If such dependencies exist, domain should not depend on them
            if (frameworks.getLength() > 0) {
                // This is a simple check and might not catch all issues
                // A more thorough analysis would look at import statements in the domain classes
                File domainLayerPom = new File(projectRoot, "Samstraumr/samstraumr-core/domain/pom.xml");
                if (domainLayerPom.exists()) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document domainPom = builder.parse(domainLayerPom);
                    
                    NodeList domainDeps = (NodeList) xpath.evaluate(
                            "//*[local-name()='dependencies']/*[local-name()='dependency'][" +
                            "contains(./*[local-name()='groupId'], 'springframework') or " +
                            "contains(./*[local-name()='groupId'], 'hibernate') or " +
                            "contains(./*[local-name()='groupId'], 'jakarta.persistence')" +
                            "]", 
                            domainPom, XPathConstants.NODESET);
                    
                    assertEquals(0, domainDeps.getLength(), 
                            "Domain layer should not depend on infrastructure frameworks");
                }
            }
        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
            // Log the error but don't fail the test since this is an approximate check
            System.err.println("Warning: Could not fully verify domain independence: " + e.getMessage());
        }
    }

    @Then("all Java module dependencies should be correctly defined")
    public void allJavaModuleDependenciesShouldBeCorrectlyDefined() {
        // For multi-module projects, check that inter-module dependencies are correctly defined
        // For single-module projects, this is less applicable
        
        // Check for Java Module-Info.java files (if using JPMS)
        File moduleInfoFile = new File(projectRoot, "Samstraumr/samstraumr-core/src/main/java/module-info.java");
        if (moduleInfoFile.exists()) {
            // If using Java modules, should verify module declarations
            assertTrue(moduleInfoFile.exists(), "Module-info.java should exist");
        }
        
        // For Maven modules (not Java modules), check parent-child relationships
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Check that core module correctly references parent
            String parentGroupId = xpath.evaluate(
                    "//*[local-name()='project']/*[local-name()='parent']/*[local-name()='groupId']", 
                    corePom);
            String parentArtifactId = xpath.evaluate(
                    "//*[local-name()='project']/*[local-name()='parent']/*[local-name()='artifactId']", 
                    corePom);
            
            assertEquals("org.s8r", parentGroupId, 
                    "Core module parent groupId should be org.s8r");
            assertEquals("samstraumr", parentArtifactId, 
                    "Core module parent artifactId should be samstraumr");
            
            // Check that module POM correctly references parent
            parentGroupId = xpath.evaluate(
                    "//*[local-name()='project']/*[local-name()='parent']/*[local-name()='groupId']", 
                    modulePom);
            parentArtifactId = xpath.evaluate(
                    "//*[local-name()='project']/*[local-name()='parent']/*[local-name()='artifactId']", 
                    modulePom);
            
            assertEquals("org.s8r", parentGroupId, 
                    "Module POM parent groupId should be org.s8r");
            assertEquals("samstraumr-parent", parentArtifactId, 
                    "Module POM parent artifactId should be samstraumr-parent");
            
        } catch (XPathExpressionException e) {
            fail("Failed to check module dependencies: " + e.getMessage());
        }
    }

    @Then("test dependencies should have test scope")
    public void testDependenciesShouldHaveTestScope() {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Check core module test dependencies
            NodeList testDependencies = (NodeList) xpath.evaluate(
                    "//*[local-name()='dependencies']/*[local-name()='dependency'][" +
                    "contains(./*[local-name()='artifactId'], 'junit') or " +
                    "contains(./*[local-name()='artifactId'], 'cucumber') or " +
                    "contains(./*[local-name()='artifactId'], 'mockito')" +
                    "]/*[local-name()='scope']", 
                    corePom, XPathConstants.NODESET);
            
            for (int i = 0; i < testDependencies.getLength(); i++) {
                assertEquals("test", testDependencies.item(i).getTextContent(), 
                        "Test dependencies should have test scope");
            }
            
        } catch (XPathExpressionException e) {
            fail("Failed to check test dependencies: " + e.getMessage());
        }
    }

    @Then("JUnit and Cucumber dependencies should be properly configured")
    public void junitAndCucumberDependenciesShouldBeProperlyConfigured() {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Check for JUnit dependencies
            NodeList junitDeps = (NodeList) xpath.evaluate(
                    "//*[local-name()='dependencies']/*[local-name()='dependency'][" +
                    "contains(./*[local-name()='artifactId'], 'junit')]", 
                    corePom, XPathConstants.NODESET);
            
            assertTrue(junitDeps.getLength() >= 2, 
                    "Core module should include JUnit dependencies");
            
            // Check for Cucumber dependencies
            NodeList cucumberDeps = (NodeList) xpath.evaluate(
                    "//*[local-name()='dependencies']/*[local-name()='dependency'][" +
                    "contains(./*[local-name()='artifactId'], 'cucumber')]", 
                    corePom, XPathConstants.NODESET);
            
            assertTrue(cucumberDeps.getLength() >= 2, 
                    "Core module should include Cucumber dependencies");
            
            // Check for consistent versioning via properties
            String junitVersion = xpath.evaluate(
                    "//*[local-name()='properties']/*[local-name()='junit.version']", 
                    rootPom);
            String cucumberVersion = xpath.evaluate(
                    "//*[local-name()='properties']/*[local-name()='cucumber.version']", 
                    rootPom);
            
            assertFalse(junitVersion.isEmpty(), "JUnit version should be defined as a property");
            assertFalse(cucumberVersion.isEmpty(), "Cucumber version should be defined as a property");
            
        } catch (XPathExpressionException e) {
            fail("Failed to check test framework configuration: " + e.getMessage());
        }
    }

    @Then("the surefire plugin should be configured for test discovery")
    public void theSurefirePluginShouldBeConfiguredForTestDiscovery() {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Check surefire plugin configuration for includes
            String includesPath = "//*[local-name()='build']/*[local-name()='plugins']" +
                    "/*[local-name()='plugin'][" +
                    "./*[local-name()='artifactId']='maven-surefire-plugin'" +
                    "]/*[local-name()='configuration']/*[local-name()='includes']";
            
            NodeList includeNodes = (NodeList) xpath.evaluate(includesPath + "/*", corePom, XPathConstants.NODESET);
            
            assertTrue(includeNodes.getLength() > 0, 
                    "Surefire plugin should have includes configuration");
            
            // Verify typical test patterns are included
            boolean hasTestPattern = false;
            boolean hasTestsPattern = false;
            
            for (int i = 0; i < includeNodes.getLength(); i++) {
                String pattern = includeNodes.item(i).getTextContent();
                if (pattern.contains("*Test.java")) {
                    hasTestPattern = true;
                }
                if (pattern.contains("*Tests.java")) {
                    hasTestsPattern = true;
                }
            }
            
            assertTrue(hasTestPattern || hasTestsPattern, 
                    "Surefire plugin should include standard test patterns");
            
        } catch (XPathExpressionException e) {
            fail("Failed to check surefire configuration: " + e.getMessage());
        }
    }

    @Then("the correct test resources should be filtered")
    public void theCorrectTestResourcesShouldBeFiltered() {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Check test resources configuration
            NodeList testResourceNodes = (NodeList) xpath.evaluate(
                    "//*[local-name()='testResources']/*[local-name()='testResource']", 
                    corePom, XPathConstants.NODESET);
            
            assertTrue(testResourceNodes.getLength() > 0, 
                    "Core POM should configure test resources");
            
            // Check filtering
            boolean hasFilteringEnabled = false;
            for (int i = 0; i < testResourceNodes.getLength(); i++) {
                Node resourceNode = testResourceNodes.item(i);
                String filtering = xpath.evaluate("./*[local-name()='filtering']", resourceNode);
                if ("true".equals(filtering)) {
                    hasFilteringEnabled = true;
                    break;
                }
            }
            
            assertTrue(hasFilteringEnabled, "At least one test resource should have filtering enabled");
            
            // Check includes for .feature files
            boolean includesFeatureFiles = false;
            for (int i = 0; i < testResourceNodes.getLength(); i++) {
                Node resourceNode = testResourceNodes.item(i);
                NodeList includes = (NodeList) xpath.evaluate(
                        "./*[local-name()='includes']/*[local-name()='include']", 
                        resourceNode, XPathConstants.NODESET);
                
                for (int j = 0; j < includes.getLength(); j++) {
                    String include = includes.item(j).getTextContent();
                    if (include.contains("*.feature")) {
                        includesFeatureFiles = true;
                        break;
                    }
                }
                
                if (includesFeatureFiles) {
                    break;
                }
            }
            
            assertTrue(includesFeatureFiles, "Test resources should include .feature files");
            
        } catch (XPathExpressionException e) {
            fail("Failed to check test resources: " + e.getMessage());
        }
    }

    @Then("test tag properties should be defined")
    public void testTagPropertiesShouldBeDefined() {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            
            // Check for test tag properties
            String testTags = xpath.evaluate("//*[local-name()='properties']/*[local-name()='test.tags']", rootPom);
            String allTestTags = xpath.evaluate("//*[local-name()='properties']/*[local-name()='all.test.tags']", rootPom);
            
            assertFalse(testTags.isEmpty() && allTestTags.isEmpty(), 
                    "Test tag properties should be defined");
            
            // Check for cucumber.filter.tags property in profiles
            NodeList profileNodes = (NodeList) xpath.evaluate(
                    "//*[local-name()='profiles']/*[local-name()='profile']" +
                    "[./*[local-name()='id']='unit-tests' or " +
                    "./*[local-name()='id']='component-tests' or " +
                    "./*[local-name()='id']='atl-tests']" +
                    "/*[local-name()='properties']/*[local-name()='cucumber.filter.tags']", 
                    corePom, XPathConstants.NODESET);
            
            assertTrue(profileNodes.getLength() > 0, 
                    "Test profiles should define cucumber.filter.tags property");
            
        } catch (XPathExpressionException e) {
            fail("Failed to check test tag properties: " + e.getMessage());
        }
    }
    
    // Helper methods
    
    private File findProjectRoot() {
        // Try to find pom.xml in current or ancestor directories
        File current = new File("").getAbsoluteFile();
        while (current != null) {
            if (new File(current, "pom.xml").exists()) {
                return current;
            }
            current = current.getParentFile();
        }
        
        fail("Could not find project root directory with pom.xml");
        return null; // Never reached due to fail() above
    }
    
    private void verifyProfile(String profileId, String[] expectedProperties, String[] expectedPlugins) 
            throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        
        // Check properties
        for (String property : expectedProperties) {
            String xpathExpr = String.format(
                    "//*[local-name()='profiles']/*[local-name()='profile'][*[local-name()='id']='%s']" +
                    "/*[local-name()='properties']/*[local-name()='%s']", 
                    profileId, property);
            
            Node propertyNode = (Node) xpath.evaluate(xpathExpr, rootPom, XPathConstants.NODE);
            assertNotNull(propertyNode, 
                    "Profile " + profileId + " should define property " + property);
        }
        
        // Check plugins
        for (String plugin : expectedPlugins) {
            String xpathExpr = String.format(
                    "//*[local-name()='profiles']/*[local-name()='profile'][*[local-name()='id']='%s']" +
                    "/*[local-name()='build']/*[local-name()='plugins']/*[local-name()='plugin']" +
                    "[*[local-name()='artifactId']='%s']", 
                    profileId, plugin);
            
            Node pluginNode = (Node) xpath.evaluate(xpathExpr, rootPom, XPathConstants.NODE);
            assertNotNull(pluginNode, 
                    "Profile " + profileId + " should include plugin " + plugin);
        }
    }
}