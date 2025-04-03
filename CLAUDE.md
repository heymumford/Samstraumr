# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands
- Build project: `mvn clean install`
- Build without tests: `mvn clean install -DskipTests`
- Run all tests: `mvn test`
- Run with performance optimizations:
  - Linux/WSL: `./util/build/build-performance.sh` or `./util/build/build-optimal.sh` (recommended)
  - Windows: `util\build\build-performance.bat`
  - Custom command with optimizations: `./util/build/build-optimal.sh clean test -P atl-tests`
  - With specific thread count: `mvn test -T 1C` (1 thread per CPU core)
  - Fast development mode: `./util/build/build-optimal.sh fast` or `mvn compile -P fast -T 1C`
  
  ⚠️ **IMPORTANT:** All scripts must be run from their locations in the util directory

## Test Commands

### Test Runner Script
- Run all tests: `./run-tests.sh all`
- Run using either industry-standard or Samstraumr terminology:
  - Industry Standard:
    - `./run-tests.sh smoke` (basic system assembly and connectivity)
    - `./run-tests.sh unit` (individual units in isolation)
    - `./run-tests.sh component` (connected components working together)
    - `./run-tests.sh integration` (interactions between different parts)
    - `./run-tests.sh api` (public interfaces and contracts)
    - `./run-tests.sh system` (entire system as a whole)
    - `./run-tests.sh endtoend` (user perspective and requirements)
    - `./run-tests.sh property` (system properties across inputs)
  - Samstraumr Terminology:
    - `./run-tests.sh orchestration` (basic system assembly and connectivity)
    - `./run-tests.sh tube` (individual tubes in isolation)
    - `./run-tests.sh composite` (or `bundle` for legacy) (connected tubes)
    - `./run-tests.sh flow` (interactions between different parts)
    - `./run-tests.sh machine` (public interfaces and contracts)
    - `./run-tests.sh stream` (entire system as a whole)
    - `./run-tests.sh acceptance` (user perspective and requirements)
    - `./run-tests.sh adaptation` (system properties across inputs)
- Options:
  - Include equivalent tags: `--both` or `-b` (e.g., run both unit and tube tests)
  - Output to file: `--output <file>` or `-o <file>`
  - Use specific Maven profile: `--profile <profile>` or `-p <profile>`
  - Help: `--help` or `-h`

### Maven Test Profiles
- Critical vs Robustness Tests:
  - `mvn test -P atl-tests` (Above The Line - critical tests)
  - `mvn test -P btl-tests` (Below The Line - robustness tests)
- Industry Standard Test Profiles:
  - `mvn test -P smoke-tests` (basic system verification)
  - `mvn test -P unit-tests` (individual units)
  - `mvn test -P component-tests` (connected components)
  - `mvn test -P integration-tests` (interactions between parts)
  - `mvn test -P api-tests` (public interfaces)
  - `mvn test -P system-tests` (entire system)
  - `mvn test -P endtoend-tests` (user perspective)
  - `mvn test -P property-tests` (system properties)
- Samstraumr Test Profiles:
  - `mvn test -P orchestration-tests` (basic system verification)
  - `mvn test -P tube-tests` (individual units)
  - `mvn test -P composite-tests` (connected components)
  - `mvn test -P flow-tests` (interactions between parts)
  - `mvn test -P machine-tests` (public interfaces)
  - `mvn test -P stream-tests` (entire system)
  - `mvn test -P acceptance-tests` (user perspective)
  - `mvn test -P adaptation-tests` (system properties)
- Run with quality checks skipped: `mvn test -P skip-quality-checks`

### Test Annotations and Tags
- Terminology Mapping:
  - Industry Standard → Samstraumr:
    - `@SmokeTest` → `@OrchestrationTest`
    - `@UnitTest` → `@TubeTest`
    - `@ComponentTest` → `@CompositeTest` (formerly `@BundleTest`)
    - `@IntegrationTest` → `@FlowTest`
    - `@ApiTest` → `@MachineTest`
    - `@SystemTest` → `@StreamTest`
    - `@EndToEndTest` → `@AcceptanceTest`
    - `@PropertyTest` → `@AdaptationTest`
- Additional Tags:
  - Criticality: `@ATL` (critical), `@BTL` (robustness)
  - Layer-based: `@L0_Tube`, `@L1_Bundle`, `@L2_Machine`, `@L3_System`
  - Capabilities: `@Identity`, `@Flow`, `@State`, `@Awareness`
  - Lifecycle: `@Init`, `@Runtime`, `@Termination`
  - Patterns: `@Observer`, `@Transformer`, `@Validator`, `@CircuitBreaker`
  - Non-functional: `@Performance`, `@Resilience`, `@Scale`

## Quality Check Commands
- Run all quality checks: `./util/quality/build-checks.sh`
- Skip quality checks: `mvn install -P skip-quality-checks` or `./util/quality/skip-quality-build.sh`
- Run specific checks:
  - Formatting: `mvn spotless:check` (Fix: `mvn spotless:apply`)
  - Code analysis: SonarQube (external)
  - Coding standards: `mvn checkstyle:check`
  - Bug detection: `mvn spotbugs:check`
  - Code coverage: `mvn jacoco:report`
- File encoding and line ending checks:
  - Check encoding and line endings: `./util/quality/check-encoding.sh`
  - Check with detailed output: `./util/quality/check-encoding.sh --verbose`
  - Automatically fix issues: `./util/quality/check-encoding.sh --fix`

## Code Style Guidelines
- **Imports**: Specific imports (no wildcards). Standard Java first, then third-party, then project imports.
- **Naming**: PascalCase for classes, camelCase for methods/variables, ALL_CAPS for constants.
- **Error Handling**: Custom exceptions with contextual messages; consistent logging before throwing exceptions.
- **Logging**: 
  - Use SLF4J with Log4j2 implementation
  - Follow log level guidelines in docs/LOGGING_STANDARDS.md
  - Always use parameterized logging (LOGGER.info("Value: {}", value))
  - Include exceptions in error logs (LOGGER.error("Message", exception))
  - Never use System.out.println or System.err.println
- **Testing**: Cucumber for BDD testing; feature files with descriptive scenarios.
- **Documentation**: Javadoc for public APIs; comments for complex operations.
- **Structure**: Final fields for immutability; private methods for implementation details.
- **Encoding**: UTF-8 for all text files; LF (Unix-style) line endings for all text files except .bat and .cmd files.
- **File Format**: EditorConfig ensures consistent formatting across different IDEs.

## Maven Optimization
- For faster builds, use `./util/build/build-optimal.sh` with appropriate flags:
  - Development iterations: `./util/build/build-optimal.sh fast` (skips tests and checks)
  - Clean local cache: `./util/maintenance/cleanup-maven.sh`
- Maven environment configuration:
  - Settings optimized in `~/.m2/settings.xml`
  - Memory settings: `MAVEN_OPTS="-Xmx1g -XX:+TieredCompilation -XX:TieredStopAtLevel=1"`
  - Parallel builds: `-T 1C` flag (1 thread per core)
  - Incremental compilation enabled for faster builds

## Project Organization
- Multi-module Maven project (Java 17)
- BDD testing approach with Cucumber
- Feature files contain detailed test documentation with tags for selective test execution
- Quality checks integrated into the build process
  - Spotless: Code formatting
  - SonarQube: Comprehensive static code analysis (external)
  - Checkstyle: Coding standards
  - SpotBugs: Bug detection
  - JaCoCo: Code coverage
- Version management
  - Central version.properties file in Samstraumr/ directory
  - Update version with: `./util/maintenance/update-version.sh <new-version>`
  - Resources filtered to include version information

## Quality Reports
- Cucumber Reports: `target/cucumber-reports/cucumber.html`
- JaCoCo Coverage: `target/site/jacoco/index.html`
- CheckStyle: `target/checkstyle-result.xml`
- SonarQube: Online dashboard
- SpotBugs: `target/spotbugsXml.xml`
