package org.s8r.architecture;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Tests to validate the Maven project structure and configuration.
 * 
 * <p>These tests verify that the Maven project structure follows
 * the architectural decisions and best practices defined for the project.
 * This includes the three-tier Maven structure, proper dependency management,
 * profile configuration, and Clean Architecture compliance.
 */
@Tag("architecture")
@DisplayName("Maven Structure Test")
public class MavenStructureTest {

    private static final String NAMESPACE_URI = "http://maven.apache.org/POM/4.0.0";
    
    private static Document rootPom;
    private static Document modulePom;
    private static Document corePom;
    
    private static String projectVersion;
    
    @BeforeAll
    static void setup() throws ParserConfigurationException, SAXException, IOException {
        // Find project root directory
        Path currentDir = Paths.get("").toAbsolutePath();
        Path projectRoot = findProjectRoot(currentDir);
        assertNotNull(projectRoot, "Could not find project root directory");
        
        // Load POM files
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        rootPom = builder.parse(projectRoot.resolve("pom.xml").toFile());
        modulePom = builder.parse(projectRoot.resolve("Samstraumr/pom.xml").toFile());
        corePom = builder.parse(projectRoot.resolve("Samstraumr/samstraumr-core/pom.xml").toFile());
        
        // Extract project version
        XPath xpath = XPathFactory.newInstance().newXPath();
        projectVersion = xpath.evaluate("//*[local-name()='project']/*[local-name()='version']/text()", rootPom);
    }
    
    @Test
    @DisplayName("Verify Three-Tier Maven Structure")
    void verifyThreeTierMavenStructure() throws XPathExpressionException {
        // Root POM should have Samstraumr module
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList modules = (NodeList) xpath.evaluate("//*[local-name()='modules']/*[local-name()='module']", rootPom, XPathConstants.NODESET);
        
        boolean foundSamstraumrModule = false;
        for (int i = 0; i < modules.getLength(); i++) {
            if (modules.item(i).getTextContent().equals("Samstraumr")) {
                foundSamstraumrModule = true;
                break;
            }
        }
        
        assertTrue(foundSamstraumrModule, "Root POM should include Samstraumr module");
        
        // Module POM should have samstraumr-core module
        modules = (NodeList) xpath.evaluate("//*[local-name()='modules']/*[local-name()='module']", modulePom, XPathConstants.NODESET);
        
        boolean foundCoreModule = false;
        for (int i = 0; i < modules.getLength(); i++) {
            if (modules.item(i).getTextContent().equals("samstraumr-core")) {
                foundCoreModule = true;
                break;
            }
        }
        
        assertTrue(foundCoreModule, "Module POM should include samstraumr-core module");
        
        // All versions should be consistent
        String moduleVersion = xpath.evaluate("//*[local-name()='project']/*[local-name()='version']/text()", modulePom);
        String coreVersion = xpath.evaluate("//*[local-name()='project']/*[local-name()='parent']/*[local-name()='version']/text()", corePom);
        
        assertEquals(projectVersion, moduleVersion, "Root POM and module POM should have same version");
        assertEquals(projectVersion, coreVersion, "Root POM and core POM should have same version");
    }
    
    @Test
    @DisplayName("Verify Maven Profiles")
    void verifyMavenProfiles() throws XPathExpressionException {
        // Check that all required profiles exist
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList profileNodes = (NodeList) xpath.evaluate("//*[local-name()='profiles']/*[local-name()='profile']/*[local-name()='id']", 
                rootPom, XPathConstants.NODESET);
        
        Set<String> expectedProfiles = new HashSet<>(Arrays.asList(
                "atl-tests", "skip-tests", "tdd-development", "coverage", 
                "quality-checks", "security-checks", "build-report"));
        
        Set<String> actualProfiles = new HashSet<>();
        for (int i = 0; i < profileNodes.getLength(); i++) {
            actualProfiles.add(profileNodes.item(i).getTextContent());
        }
        
        assertTrue(actualProfiles.containsAll(expectedProfiles), 
                "Root POM should contain all required profiles. Missing: " + 
                expectedProfiles.stream().filter(p -> !actualProfiles.contains(p))
                        .collect(Collectors.joining(", ")));
        
        // Verify specific profile configurations
        assertTrue(hasProfileWithProperty(rootPom, "tdd-development", "jacoco.line.coverage", "0.0"),
                "tdd-development profile should set jacoco.line.coverage to 0.0");
        
        assertTrue(hasProfileWithProperty(rootPom, "skip-tests", "skipTests", "true"),
                "skip-tests profile should set skipTests to true");
        
        assertTrue(hasProfileWithPlugin(rootPom, "quality-checks", "com.diffplug.spotless", "spotless-maven-plugin"),
                "quality-checks profile should include spotless-maven-plugin");
        
        assertTrue(hasProfileWithPlugin(rootPom, "security-checks", "org.owasp", "dependency-check-maven"),
                "security-checks profile should include dependency-check-maven plugin");
    }
    
    @Test
    @DisplayName("Verify Dependency Management")
    void verifyDependencyManagement() throws XPathExpressionException {
        // Check that dependencies are managed in the parent POM
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList dependencyManagementNodes = (NodeList) xpath.evaluate(
                "//*[local-name()='dependencyManagement']/*[local-name()='dependencies']/*[local-name()='dependency']", 
                rootPom, XPathConstants.NODESET);
        
        assertTrue(dependencyManagementNodes.getLength() > 10, 
                "Root POM should manage dependencies. Found only " + dependencyManagementNodes.getLength());
        
        // Check that core POM dependencies don't specify versions
        NodeList coreDependencies = (NodeList) xpath.evaluate(
                "//*[local-name()='dependencies']/*[local-name()='dependency']/*[local-name()='version']", 
                corePom, XPathConstants.NODESET);
        
        assertEquals(0, coreDependencies.getLength(), 
                "Dependencies in core POM should not specify versions directly (found " + 
                coreDependencies.getLength() + " version elements)");
        
        // Check that plugin versions are defined as properties
        NodeList pluginVersionProperties = (NodeList) xpath.evaluate(
                "//*[local-name()='properties']/*[contains(local-name(), '.plugin.version') or contains(local-name(), '.version')]", 
                rootPom, XPathConstants.NODESET);
        
        assertTrue(pluginVersionProperties.getLength() > 5, 
                "Plugin versions should be defined as properties (found only " + 
                pluginVersionProperties.getLength() + " version properties)");
    }
    
    @Test
    @DisplayName("Verify Plugin Management")
    void verifyPluginManagement() throws XPathExpressionException {
        // Check that plugins are managed in the parent POM
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList pluginManagementNodes = (NodeList) xpath.evaluate(
                "//*[local-name()='build']/*[local-name()='pluginManagement']/*[local-name()='plugins']/*[local-name()='plugin']", 
                rootPom, XPathConstants.NODESET);
        
        assertTrue(pluginManagementNodes.getLength() > 5, 
                "Root POM should manage plugins (found only " + pluginManagementNodes.getLength() + " plugins)");
        
        // Verify critical plugins are properly configured
        boolean foundSurefirePlugin = false;
        boolean foundCompilerPlugin = false;
        
        for (int i = 0; i < pluginManagementNodes.getLength(); i++) {
            Node plugin = pluginManagementNodes.item(i);
            String artifactId = xpath.evaluate("./*[local-name()='artifactId']", plugin);
            
            if ("maven-surefire-plugin".equals(artifactId)) {
                foundSurefirePlugin = true;
            } else if ("maven-compiler-plugin".equals(artifactId)) {
                foundCompilerPlugin = true;
                
                // Verify compiler plugin has proper Java version
                String source = xpath.evaluate(
                        "./*[local-name()='configuration']/*[local-name()='source']", plugin);
                assertTrue(source.equals("${maven.compiler.source}") || source.matches("\\d+"), 
                        "Compiler plugin should specify source version");
            }
        }
        
        assertTrue(foundSurefirePlugin, "Maven Surefire Plugin should be defined in plugin management");
        assertTrue(foundCompilerPlugin, "Maven Compiler Plugin should be defined in plugin management");
    }
    
    @Test
    @DisplayName("Verify Test Framework Configuration")
    void verifyTestFrameworkConfiguration() throws XPathExpressionException {
        // Check JUnit and Cucumber dependencies
        XPath xpath = XPathFactory.newInstance().newXPath();
        
        // Check test scope for test dependencies
        NodeList testDependencies = (NodeList) xpath.evaluate(
                "//*[local-name()='dependencyManagement']/*[local-name()='dependencies']" +
                "/*[local-name()='dependency'][" +
                "contains(./*[local-name()='artifactId'], 'junit') or " +
                "contains(./*[local-name()='artifactId'], 'cucumber') or " +
                "contains(./*[local-name()='artifactId'], 'mockito')" +
                "]/*[local-name()='scope']", 
                rootPom, XPathConstants.NODESET);
        
        for (int i = 0; i < testDependencies.getLength(); i++) {
            assertEquals("test", testDependencies.item(i).getTextContent(), 
                    "Test dependencies should have test scope");
        }
        
        // Check test resources configuration
        NodeList testResources = (NodeList) xpath.evaluate(
                "//*[local-name()='testResources']/*[local-name()='testResource']/*[local-name()='includes']", 
                corePom, XPathConstants.NODESET);
        
        assertTrue(testResources.getLength() > 0, 
                "Core POM should configure test resources filtering");
        
        // Check surefire configuration
        String surefireConfig = xpath.evaluate(
                "//*[local-name()='build']/*[local-name()='plugins']" +
                "/*[local-name()='plugin'][" +
                "./*[local-name()='artifactId']='maven-surefire-plugin'" +
                "]/*[local-name()='configuration']/*[local-name()='includes']", 
                corePom);
        
        assertTrue(surefireConfig.length() > 0, 
                "Surefire plugin should have includes configuration");
    }
    
    @Test
    @DisplayName("Verify Clean Architecture Compliance")
    void verifyCleanArchitectureCompliance() throws XPathExpressionException {
        // Check Java module dependencies for Clean Architecture compliance
        // This is more of a placeholder since the actual verification would be more complex
        
        // A more complete implementation would analyze import statements in the code
        // to verify that dependencies flow in the correct direction according to Clean Architecture
        
        // For now, we'll just verify that the project structure exists
        File domainDir = new File("src/main/java/org/s8r/domain");
        File applicationDir = new File("src/main/java/org/s8r/application");
        File infrastructureDir = new File("src/main/java/org/s8r/infrastructure");
        File adapterDir = new File("src/main/java/org/s8r/adapter");
        
        assertTrue(domainDir.exists() || new File("../src/main/java/org/s8r/domain").exists(), 
                "Domain package should exist");
        assertTrue(applicationDir.exists() || new File("../src/main/java/org/s8r/application").exists(), 
                "Application package should exist");
        assertTrue(infrastructureDir.exists() || new File("../src/main/java/org/s8r/infrastructure").exists(), 
                "Infrastructure package should exist");
        assertTrue(adapterDir.exists() || new File("../src/main/java/org/s8r/adapter").exists(), 
                "Adapter package should exist");
        
        // Verify that there are package-info.java files in each package to document purpose
        verifyPackageInfo("src/main/java/org/s8r/domain");
        verifyPackageInfo("src/main/java/org/s8r/application");
        verifyPackageInfo("src/main/java/org/s8r/infrastructure");
        verifyPackageInfo("src/main/java/org/s8r/adapter");
    }
    
    // Helper methods
    
    private static Path findProjectRoot(Path startDir) {
        // Try to find pom.xml in current or ancestor directories
        Path current = startDir;
        while (current != null) {
            if (Files.exists(current.resolve("pom.xml"))) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }
    
    private boolean hasProfileWithProperty(Document pom, String profileId, String propertyName, String propertyValue) 
            throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        String xpathExpr = String.format(
                "//*[local-name()='profiles']/*[local-name()='profile'][*[local-name()='id']='%s']" +
                "/*[local-name()='properties']/*[local-name()='%s']", 
                profileId, propertyName);
        
        String value = xpath.evaluate(xpathExpr, pom);
        return propertyValue.equals(value);
    }
    
    private boolean hasProfileWithPlugin(Document pom, String profileId, String pluginGroupId, String pluginArtifactId) 
            throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        String xpathExpr = String.format(
                "//*[local-name()='profiles']/*[local-name()='profile'][*[local-name()='id']='%s']" +
                "/*[local-name()='build']/*[local-name()='plugins']/*[local-name()='plugin']" +
                "[*[local-name()='groupId']='%s' and *[local-name()='artifactId']='%s']", 
                profileId, pluginGroupId, pluginArtifactId);
        
        NodeList nodes = (NodeList) xpath.evaluate(xpathExpr, pom, XPathConstants.NODESET);
        return nodes.getLength() > 0;
    }
    
    private void verifyPackageInfo(String packagePath) {
        File packageDir = new File(packagePath);
        if (!packageDir.exists() && new File("../" + packagePath).exists()) {
            packageDir = new File("../" + packagePath);
        }
        
        if (packageDir.exists()) {
            File packageInfo = new File(packageDir, "package-info.java");
            assertTrue(packageInfo.exists(), 
                    "Package " + packagePath + " should have a package-info.java file");
        }
    }
}