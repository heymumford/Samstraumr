# Configuration Reference

*Last Updated: April 3, 2025*

This document provides a comprehensive reference for all configuration variables, environment settings, and external dependencies used throughout the Samstraumr framework.

## Table of Contents

- [Maven Properties](#maven-properties)
- [Environment Variables](#environment-variables)
- [Logging Configuration](#logging-configuration)
- [External Dependencies](#external-dependencies)
- [Prerequisite Software](#prerequisite-software)

## Maven Properties

These properties control build behavior and are defined in the POM files.

### Core build properties

| Property | Default Value | Description |
|----------|---------------|-------------|
| `project.build.sourceEncoding` | UTF-8 | Character encoding for source files |
| `maven.compiler.source` | 17 | Java source compatibility version |
| `maven.compiler.target` | 17 | Java bytecode compatibility version |
| `samstraumr.version` | *current version* | Project version from version.properties |
| `version.file.path` | ${project.basedir}/version.properties | Path to version properties file |

### Test configuration properties

| Property | Default Value | Description |
|----------|---------------|-------------|
| `atl.test.tags` | @ATL | Cucumber tags for Above-The-Line tests |
| `btl.test.tags` | @BTL | Cucumber tags for Below-The-Line tests |
| `all.test.tags` | @ATL or @BTL | Combined test tags |
| `cucumber.filter.tags` | *varies by profile* | Active Cucumber tags for test filtering |
| `test.includes` | *varies by profile* | Test class patterns for inclusion |

### Quality check properties

| Property | Default Value | Description |
|----------|---------------|-------------|
| `skip.quality.checks` | false | Master switch for all quality checks |
| `spotless.check.skip` | *inherited* | Enable/disable Spotless formatting check |
| `checkstyle.skip` | *inherited* | Enable/disable Checkstyle verification |
| `spotbugs.skip` | *inherited* | Enable/disable SpotBugs analysis |
| `jacoco.skip` | *inherited* | Enable/disable JaCoCo coverage |

### Plugin version properties

| Property | Default Value | Description |
|----------|---------------|-------------|
| `maven.compiler.plugin.version` | 3.11.0 | Maven Compiler Plugin version |
| `maven.surefire.plugin.version` | 3.2.5 | Maven Surefire Plugin version |
| `maven.resources.plugin.version` | 3.3.1 | Maven Resources Plugin version |
| `spotless.plugin.version` | 2.40.0 | Spotless Maven Plugin version |
| `checkstyle.plugin.version` | 3.3.1 | Maven Checkstyle Plugin version |
| `spotbugs.plugin.version` | 4.8.2.0 | SpotBugs Maven Plugin version |
| `jacoco.plugin.version` | 0.8.11 | JaCoCo Maven Plugin version |

### Library version properties

| Property | Default Value | Description |
|----------|---------------|-------------|
| `junit.jupiter.version` | 5.10.2 | JUnit Jupiter version |
| `cucumber.version` | 7.15.0 | Cucumber BDD framework version |
| `slf4j.version` | 2.0.12 | SLF4J logging facade version |
| `log4j.version` | 2.22.1 | Log4j2 implementation version |
| `jackson.version` | 2.16.1 | Jackson JSON library version |
| `oshi.version` | 6.4.7 | OSHI hardware/OS information library version |
| `testcontainers.version` | 1.19.3 | Testcontainers version for integration tests |
| `mockito.version` | 5.8.0 | Mockito mocking framework version |

## Environment Variables

Samstraumr supports these environment variables to customize behavior or provide system information.

| Environment Variable | Default | Description | Used In |
|---------------------|---------|-------------|---------|
| `SAMSTRAUMR_LOG_LEVEL` | INFO | Override log level for application | log4j2.xml |
| `SAMSTRAUMR_LOG_DIR` | logs | Override log directory location | log4j2.xml via APP_LOG_ROOT property |
| `MAVEN_OPTS` | - | JVM options for Maven builds | Maven build process |
| `JAVA_TOOL_OPTIONS` | -Dfile.encoding=UTF-8 | JVM options for all Java processes | Used for encoding consistency |

## Logging Configuration

Logging is configured via `log4j2.xml` in `src/main/resources`.

### Log file locations

| Log File | Path | Content | Rotation |
|----------|------|---------|----------|
| Application Log | ${APP_LOG_ROOT}/application.log | All log messages (INFO and above) | 10 MB / daily, max 10 files |
| Error Log | ${APP_LOG_ROOT}/error.log | ERROR level messages only | 10 MB / daily, max 10 files |

### Log levels

| Package | Default Level | Purpose |
|---------|--------------|---------|
| Root | INFO | Base level for all logging |
| org.samstraumr.tube | DEBUG | Detailed tube operations |
| org.samstraumr.tube.error | ERROR | Error-specific logging channel |

### Log patterns

The standard log pattern is:
```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n
```

This includes:
- Timestamp with millisecond precision
- Thread name in square brackets
- Log level (padded to 5 characters)
- Logger name (shortened to max 36 characters)
- Message content
- Newline

## External Dependencies

These are external libraries and frameworks used by Samstraumr.

### Runtime dependencies

| Dependency | Purpose | Configuration |
|------------|---------|---------------|
| Log4j2 | Logging implementation | Via log4j2.xml |
| SLF4J | Logging facade | Programmatic setup in each class |
| Jackson | JSON serialization | Used by Environment class |
| OSHI | Hardware/OS information | Used by Environment class |

### Test dependencies

| Dependency | Purpose | Configuration |
|------------|---------|---------------|
| JUnit Jupiter | Test framework | Via Maven Surefire |
| Cucumber | BDD testing | Feature files in test resources |
| Testcontainers | Integration testing | Used in Stream tests |
| Mockito | Mocking framework | Used in unit tests |

## Prerequisite Software

These software packages must be installed separately (not included in POM dependencies).

### Required software

| Software | Version | Installation | Purpose |
|----------|---------|--------------|---------|
| Java | JDK 17+ | System package manager | Core runtime |
| Maven | 3.8.0+ | System package manager | Build system |
| Git | 2.25.0+ | System package manager | Version control |

### Optional tools

| Software | Purpose | Installation | Notes |
|----------|---------|--------------|-------|
| Act | Local GitHub Actions testing | `sudo curl -s https://raw.githubusercontent.com/nektos/act/master/install.sh \| sudo bash` | Best installed to /usr/local/bin |
| Docker/Podman | Container runtime for tests | System package manager | Required for Testcontainers and Act |
| SonarQube Scanner | Code quality analysis | Download from sonarqube.org | For external quality analysis |

## Command-Line Arguments

Samstraumr command-line tools support various arguments:

### Testing scripts

| Argument | Options | Description |
|----------|---------|-------------|
| `--both` or `-b` | - | Include both industry-standard and Samstraumr-specific tests |
| `--output <file>` or `-o <file>` | Path to output file | Redirect test output to file |
| `--profile <profile>` or `-p <profile>` | Maven profile name | Use specific Maven profile |
| `--help` or `-h` | - | Display help information |

### Quality check scripts

| Argument | Options | Description |
|----------|---------|-------------|
| `--verbose` | - | Show detailed output |
| `--fix` | - | Automatically fix detected issues |
