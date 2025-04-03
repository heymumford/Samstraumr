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
- Run all tests: `./util/test/run-tests.sh all`
- Run specific test types:
  - Tube Tests (unit/JUnit): `./util/test/run-tests.sh tube`
  - Flow Tests (integration/JUnit): `./util/test/run-tests.sh flow`
  - Bundle Tests (component/JUnit): `./util/test/run-tests.sh bundle`
  - Stream Tests (system/TestContainers): `./util/test/run-tests.sh stream`
  - Adaptation Tests (property/JUnit): `./util/test/run-tests.sh adaptation`
  - Machine Tests (e2e/Cucumber): `./util/test/run-tests.sh machine`
  - BDD Acceptance Tests (business/Cucumber): `./util/test/run-tests.sh acceptance`
  - Critical Tests (fast for CI): `./util/test/run-tests.sh critical`
- Options:
  - Parallel execution: `--parallel` or `-p`
  - Thread count: `--threads <count>` or `-t <count>`
  - Skip quality checks: `--skip-quality` or `-s`
  - Verbose output: `--verbose` or `-v`
  - Help: `--help` or `-h`

### Maven Direct Execution
- Run ATL (critical) tests: `mvn test -P atl-tests`
- Run BTL (robustness) tests: `mvn test -P btl-tests`
- Run by test type:
  - `mvn test -Dtest=*TubeTest` (unit)
  - `mvn test -Dtest=*FlowTest` (integration)
  - `mvn test -Dtest=*BundleTest` (component)
  - `mvn test -Dtest=*StreamTest` (system)
  - `mvn test -Dtest=*AdaptationTest` (property)
- Run by Cucumber tags:
  - `mvn test -Dcucumber.filter.tags="@TagName"`
  - `mvn test -Dcucumber.filter.tags="@L0_Tube and @Identity"`
- Run with quality checks skipped: `mvn test -P skip-quality-checks -Dspotless.check.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true -Djacoco.skip=true -Dmaven.test.skip=false`

### Test Tag Hierarchy
- Test type tags: `@TubeTest`, `@FlowTest`, `@BundleTest`, `@StreamTest`, `@AdaptationTest`, `@L2_Machine`, `@Acceptance`
- Layer-based hierarchy: `@L0_Tube`, `@L1_Bundle`, `@L2_Machine`, `@L3_System`
- Critical path: `@ATL` (critical), `@BTL` (robustness)
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
- **Logging**: Use SLF4J with Log4j2 implementation; consistent log levels; parameterized logging.
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
