# Maven and Cucumber Configuration Standards for ALZ001 Tests

This document outlines the configuration standards for Maven and Cucumber used in the ALZ001 test suite. Following these standards ensures maintainability, consistency, and reliability across the test codebase.

## Maven Configuration

### Key Maven Files

| File | Absolute Path | Purpose |
|------|---------------|---------|
| Root POM | `/home/emumford/NativeLinuxProjects/Samstraumr/pom.xml` | Parent POM that defines shared properties, plugins, dependencies |
| Modules POM | `/home/emumford/NativeLinuxProjects/Samstraumr/modules/pom.xml` | POM for all modules, inherits from root POM |
| Core POM | `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/pom.xml` | POM for the core module where ALZ001 tests reside |

### Maven Best Practices

1. **Use the Maven Standard Directory Layout**:
   - Main Java code in `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/main/java`
   - Test Java code in `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java`
   - Test resources in `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/resources`

2. **Dependency Management**:
   - All version numbers are defined in the root POM (`/home/emumford/NativeLinuxProjects/Samstraumr/pom.xml`)
   - Dependencies are managed in the `<dependencyManagement>` section of the root POM
   - Module POMs reference dependencies without version numbers
   - Current Cucumber version: ${cucumber.version} (7.15.0)
   - Current JUnit version: ${junit.version} (5.10.1)

3. **Maven Profiles**:
   - `alz001-tests`: Runs all tests tagged with @ALZ001
   - `tdd-development`: Default profile with test coverage thresholds
   - `quality-checks`: Performs code quality checks
   - `coverage`: Enforces test coverage thresholds

4. **Maven Commands for ALZ001 Tests**:
   ```bash
   # Run all ALZ001 tests
   mvn test -P alz001-tests
   
   # Run ALZ001 tests with coverage reporting
   mvn verify -P alz001-tests,coverage
   
   # Run ALZ001 tests with quality checks
   mvn verify -P alz001-tests,quality-checks
   ```

5. **Maven Plugin Configuration**:
   - Surefire Plugin: Configured to handle JUnit 5 and Cucumber
   - JaCoCo Plugin: For test coverage reporting
   - Enforcer Plugin: Ensures consistent Maven and Java versions

## Cucumber Configuration

### Key Cucumber Files

| File | Absolute Path | Purpose |
|------|---------------|---------|
| Cucumber Properties | `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/resources/cucumber.properties` | Global Cucumber configuration |
| ALZ001 Test Runner | `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/runner/ALZ001Tests.java` | JUnit test runner for ALZ001 tests |
| Feature Files | `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/resources/features/alz001/*.feature` | ALZ001 feature files |
| Step Definitions | `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/steps/alz001/*.java` | Step definition implementations |

### Cucumber Best Practices

1. **Feature File Organization**:
   - All ALZ001 feature files are in `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/resources/features/alz001/`
   - Features must follow the naming convention: `alz001-<capability>-<description>.feature`
   - Each feature file should focus on a single capability

2. **Step Definition Organization**:
   - Base step classes in `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/steps/alz001/base/`
   - Mock components in `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/steps/alz001/mock/`
   - Capability-specific step definitions in `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/steps/alz001/`

3. **Tagging Strategy**:
   - `@ALZ001`: Root tag for all ALZ001 tests
   - `@L0_Component`, `@L1_Composite`, `@L2_Machine`, `@L3_System`: Test levels
   - `@Conception`, `@Embryonic`, `@Infancy`, `@Maturity`: Lifecycle phases
   - `@ProteinExpression`, `@NeuronalNetwork`, etc.: Capability-specific tags
   - `@Positive`, `@Negative`: Test expectations

4. **Cucumber Runner Configuration**:
   - Configured to run in parallel (`cucumber.execution.parallel.enabled=true`)
   - Uses dynamic strategy to optimize thread allocation
   - Reports stored in `target/cucumber-reports/`

5. **Running Cucumber Tests**:
   ```bash
   # Run all ALZ001 tests
   ./s8r-test ALZ001
   
   # Run specific capability tests
   ./s8r-test ALZ001 --tags "@ProteinExpression"
   
   # Run tests at a specific level
   ./s8r-test ALZ001 --tags "@L0_Component"
   ```

## Integration with Test Architecture

The ALZ001 test architecture integrates seamlessly with Maven and Cucumber through:

1. **Test Context Management**:
   - `ALZ001TestContext` in `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/steps/alz001/base/ALZ001TestContext.java`
   - Thread-safe context for Cucumber parallel execution

2. **Mock Components**:
   - Base class: `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/steps/alz001/mock/ALZ001MockComponent.java`
   - Factory: `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/steps/alz001/mock/ALZ001MockFactory.java`

3. **Step Definition Base**:
   - `/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/steps/alz001/ALZ001BaseSteps.java`
   - Provides common functionality for all step definitions

## Maintaining Clean Standards

To ensure the ALZ001 tests maintain clean standards:

1. **Follow the Composition Pattern**:
   - Use composition instead of inheritance for step definitions
   - Each capability's step definition composes the base steps class
   - Share data through the test context, not through inheritance

2. **Keep Configuration in the Right Place**:
   - Maven profiles in the appropriate POM files
   - Cucumber global settings in `cucumber.properties`
   - Test runner settings in the JUnit runner class

3. **Document All Changes**:
   - Update this document when changing configuration
   - Add comments to explain non-obvious configurations
   - Keep paths absolute for clarity

4. **Run Quality Checks**:
   - Execute `mvn verify -P quality-checks` before committing
   - Ensure code style consistency
   - Maintain test coverage above thresholds

By following these standards, the ALZ001 test suite will remain maintainable, reliable, and aligned with Samstraumr's clean architecture principles.