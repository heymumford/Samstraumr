# Code Standards

This document outlines the coding standards for contributing to Samstraumr.

## General Principles

- **Clarity over Cleverness**: Prefer clear, readable code over clever optimizations
- **Consistency**: Follow established patterns in the codebase
- **Testability**: Design code with testing in mind
- **Documentation**: Document public APIs and complex logic
- **Continuous Integration**: Always verify code passes all tests and checks before submitting
- **Configuration**: Externalize configuration and avoid hardcoded values (see [Configuration Standards](configuration-standards.md))

## Java Code Style

Samstraumr follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with the following modifications:

### Naming conventions

- **Classes**: PascalCase, descriptive nouns
- **Interfaces**: PascalCase, descriptive nouns
- **Methods**: camelCase, verb phrases describing actions
- **Variables**: camelCase, descriptive nouns
- **Constants**: UPPER_SNAKE_CASE
- **Packages**: lowercase, domain-reversed structure

### Structural guidelines

- **Class Structure**:
  1. Static fields
  2. Instance fields
  3. Constructors
  4. Public methods
  5. Protected methods
  6. Private methods
  7. Inner classes/interfaces
- **Method Structure**:
  1. Input validation
  2. Core logic
  3. Result preparation and return

### Imports

- Use specific imports (not wildcard imports)
- Group imports in the following order:
  1. Java standard library
  2. Third-party libraries
  3. Samstraumr imports

### Documentation

- All public APIs must have Javadoc
- Include `@param`, `@return`, and `@throws` tags as appropriate
- Document any assumptions or non-obvious behaviors
- Use `{@link}` to reference other classes and methods

### Error handling

- Use custom exceptions with descriptive messages
- Log exceptions at appropriate levels before throwing
- Never swallow exceptions without logging
- Use appropriate exception types for different error conditions

## Quality Checks

All code must pass the following quality checks:

- **Spotless**: Code formatting
- **SonarQube**: Comprehensive static code analysis
- **Checkstyle**: Coding standards
- **SpotBugs**: Bug detection
- **JaCoCo**: Code coverage (minimum threshold required)

## Testing Standards

- Tests should be isolated and not depend on external resources
- Use appropriate test categories (unit, component, integration, etc.)
- Test both happy paths and error conditions
- Use descriptive test method names explaining what is being tested
- Follow the BDD approach with Cucumber for behavioral tests

## Version Control Standards

- Make atomic commits that address a single concern
- Use descriptive commit messages in the imperative mood
- Reference issue numbers in commit messages when applicable
- Keep pull requests focused on a single feature or bug fix

## Build Procedures

### Local development environment

1. **Standard Build**
   - `mvn clean install` - Full build with tests
   - `mvn clean install -DskipTests` - Build without running tests
   - `./util/build/build-performance.sh` - Build with optimizations (Linux/WSL)
   - `./util/build/build-optimal.sh` - Recommended optimized build (Linux/WSL)
   - `./util/build/build-optimal.sh fast` - Fast development build, skipping tests and checks
2. **Test Execution**
   - `./run-tests.sh all` - Run all tests
   - `./run-tests.sh <test-type>` - Run specific test type (e.g., unit, component, integration)
   - `mvn test -P <profile>` - Run tests with specific Maven profile (e.g., atl-tests, unit-tests)
   - See the project configuration files for complete test command reference
3. **Quality Checks**
   - `./util/quality/build-checks.sh` - Run all quality checks
   - `./util/quality/skip-quality-build.sh` - Build without quality checks
   - `mvn spotless:apply` - Apply code formatting

### Github actions verification

To verify GitHub workflows locally before pushing:

1. **Install Act**
   - Install [Act](https://github.com/nektos/act) on your system
   - For Linux: `sudo curl -s https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash`
   - Or place in /usr/local/bin: `sudo mv act /usr/local/bin/`
2. **Configure Act**
   - Create configuration: `echo "-P ubuntu-latest=ghcr.io/catthehacker/ubuntu:act-latest" > ~/.config/act/actrc`
3. **Run Workflows Locally**
   - List available workflows: `act -l`
   - Run a specific job with dry-run: `act -j initialization --dryrun`
   - Run a specific job: `act -j initialization`
   - Run with specific event: `act workflow_dispatch -j get-version -W .github/workflows/samstraumr-pipeline.yml`
4. **Badge Updates**
   - GitHub Actions automatically updates the build status badge in the README
