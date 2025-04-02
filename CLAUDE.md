# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands
- Build project: `mvn clean install`
- Build without tests: `mvn clean install -DskipTests`
- Run all tests: `mvn test`
- Run with performance optimizations:
  - Linux/WSL: `./build-performance.sh`
  - Windows: `build-performance.bat`
  - Custom command with optimizations: `./build-performance.sh clean test -P atl-tests`
  - With specific thread count: `mvn test -T 12` (uses 12 threads)

## Test Commands
### Test Categorization
- Run ATL (critical) tests: `mvn test -P atl-tests`
- Run BTL (robustness) tests: `mvn test -P btl-tests`
- Run tests with specific tag: `mvn test -Dcucumber.filter.tags="@TagName"`
- Run tests with tag combinations: `mvn test -Dcucumber.filter.tags="@L0_Tube and @Identity"`
- Run tests with quality checks skipped: `mvn test -P atl-tests -P skip-quality-checks -Dspotless.check.skip=true -Dpmd.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true -Djacoco.skip=true -Dmaven.test.skip=false`

### Test Tag Hierarchy
- Layer-based hierarchy: `@L0_Tube`, `@L1_Bundle`, `@L2_Machine`, `@L3_System`
- Critical path: `@ATL` (critical), `@BTL` (robustness)
- Capabilities: `@Identity`, `@Flow`, `@State`, `@Awareness`
- Lifecycle: `@Init`, `@Runtime`, `@Termination`
- Patterns: `@Observer`, `@Transformer`, `@Validator`, `@CircuitBreaker`
- Non-functional: `@Performance`, `@Resilience`, `@Scale`

## Quality Check Commands
- Run all quality checks: `./build-checks.sh`
- Skip quality checks: `mvn install -P skip-quality-checks`
- Run specific checks:
  - Formatting: `mvn spotless:check` (Fix: `mvn spotless:apply`)
  - Code analysis: `mvn pmd:check`
  - Coding standards: `mvn checkstyle:check`
  - Bug detection: `mvn spotbugs:check`
  - Code coverage: `mvn jacoco:report`
- File encoding and line ending checks:
  - Check encoding and line endings: `./check-encoding.sh`
  - Check with detailed output: `./check-encoding.sh --verbose`
  - Automatically fix issues: `./check-encoding.sh --fix`

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

## Project Organization
- Multi-module Maven project (Java 21)
- BDD testing approach with Cucumber
- Feature files contain detailed test documentation with tags for selective test execution
- Quality checks integrated into the build process
  - Spotless: Code formatting
  - PMD: Static code analysis
  - Checkstyle: Coding standards
  - SpotBugs: Bug detection
  - JaCoCo: Code coverage

## Quality Reports
- Cucumber Reports: `target/cucumber-reports/cucumber.html`
- JaCoCo Coverage: `target/site/jacoco/index.html`
- CheckStyle: `target/checkstyle-result.xml`
- PMD: `target/pmd.xml`
- SpotBugs: `target/spotbugsXml.xml`
