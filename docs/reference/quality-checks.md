<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Quality Checks Reference

This document provides reference information for the unified quality check system in Samstraumr.

## Overview

Samstraumr includes a comprehensive quality check system that ensures code quality, style consistency, and security. The system includes:

1. **Centralized Configuration**: All quality tool configurations are stored in the `quality-tools` directory
2. **Unified Command**: A single command (`./s8r quality`) runs all quality checks
3. **Multiple Profiles**: Different quality profiles for different usage scenarios
4. **Flexible Tool Selection**: Run specific tools or skip specific tools as needed
5. **HTML Reporting**: Consolidated HTML reports for all quality issues

## Quality Profiles

The unified quality check system includes three profiles:

1. **standard**: Basic quality checks for development
   - Spotless (code formatting)
   - Checkstyle (code style)
   - SpotBugs (bug detection)

2. **strict**: Thorough checks for releases
   - All standard checks
   - JaCoCo (code coverage)
   - JavaDoc (documentation quality)
   - File encoding checks

3. **security**: Focused on security issues
   - SpotBugs (with security analyzers)
   - OWASP Dependency Check

## Command-Line Usage

The unified quality check system can be used in two ways:

### S8r CLI Integration

```bash
# Run standard quality profile
./s8r quality

# Run strict quality profile
./s8r quality -p strict

# Run only specific tools
./s8r quality -t spotless -t checkstyle

# Run with automatic fixes
./s8r quality -f

# Skip specific tools
./s8r quality -s jacoco

# Generate reports without failing
./s8r quality -r

# Show verbose output
./s8r quality -v
```

### Direct Script Usage

```bash
# Run standard quality profile
./s8r-quality

# Run strict quality profile
./s8r-quality --profile strict

# Run only specific tools
./s8r-quality --tool spotless --tool checkstyle

# Run with automatic fixes
./s8r-quality --fix

# Skip specific tools
./s8r-quality --skip jacoco

# Generate reports without failing
./s8r-quality --report-only

# Show verbose output
./s8r-quality --verbose
```

## Quality Thresholds

The unified quality check system enforces the following thresholds:

### Code Coverage

- Line coverage: 70%
- Branch coverage: 60%
- Method coverage: 70%
- Class coverage: 80%

### Complexity

- Maximum class complexity: 20
- Maximum method complexity: 10

### Duplication

- Maximum duplicated code: 3%
- Minimum token count: 100
- Minimum line count: 10

### Security

- CVSS threshold: 7.0 (Critical vulnerabilities)

### Documentation

- JavaDoc coverage: 80%

## Report Locations

Quality reports are generated in the following locations:

- Main report: `target/quality-reports/quality-report-TIMESTAMP.html`
- JaCoCo: `target/quality-reports/jacoco/`
- SpotBugs: `target/quality-reports/spotbugs.html`
- Checkstyle: `target/checkstyle-result.xml`
- OWASP: `target/quality-reports/dependency-check-report.html`

## Tool Configuration

All quality tool configurations are stored in the `quality-tools` directory:

- Checkstyle: `quality-tools/checkstyle/checkstyle.xml`
- SpotBugs: `quality-tools/spotbugs/spotbugs-exclude.xml`
- PMD: `quality-tools/pmd/pmd-ruleset.xml`
- JaCoCo: `quality-tools/jacoco/jacoco.xml`

## Implementation Details

The unified quality check system is implemented as a set of bash scripts:

- Main script: `util/bin/quality/check-quality.sh`
- Tool modules: `util/bin/quality/modules/*.sh`
- Configuration: `quality-tools/quality-thresholds.xml`

## Customizing Quality Checks

To customize quality checks:

1. **Edit Tool Configurations**: Modify the configuration files in `quality-tools` directory
2. **Adjust Thresholds**: Edit the `quality-tools/quality-thresholds.xml` file
3. **Create Custom Profiles**: Add new profiles to the `quality-tools/quality-thresholds.xml` file
4. **Add Custom Modules**: Create new modules in the `util/bin/quality/modules` directory

## CI/CD Integration

For CI/CD integration, use the following command:

```bash
./s8r-quality --profile strict --report-only
```

This will run all quality checks in strict mode and generate reports without failing the build. You can then parse the reports to determine if the quality checks passed or failed.

## Pre-Commit Usage

To run quality checks before committing, add the following to your pre-commit hook:

```bash
#!/bin/bash
# Run quality checks before committing
./s8r-quality --profile standard

# If quality checks fail, ask user if they want to commit anyway
if [ $? -ne 0 ]; then
  echo "Quality checks failed. Commit anyway? (y/n)"
  read -r response
  if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
    exit 0
  else
    exit 1
  fi
fi
```

## Troubleshooting

Common issues and solutions:

1. **Missing Tools**: Ensure all required tools are installed (Java, Maven, etc.)
2. **Configuration Issues**: Check the configuration files in `quality-tools` directory
3. **Report Generation Failures**: Ensure the target directory is writable
4. **Tool Execution Failures**: Check the tool-specific output in the report directory

## Further Reading

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [SpotBugs Documentation](https://spotbugs.readthedocs.io/en/latest/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)