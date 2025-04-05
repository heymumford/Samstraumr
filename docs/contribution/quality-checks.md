<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Quality Checks

This document explains the quality gates and checks implemented in the Samstraumr project, both for local development and in the CI/CD pipeline.

## Quality Gates Overview

Samstraumr implements a comprehensive set of quality gates that must be passed for a build to be considered successful. These are enforced both locally through Maven and in the GitHub Actions CI/CD pipeline.

### Core quality gates

1. **Spotless** - Code formatting using Google Java Style
2. **Checkstyle** - Style checking based on Google's style with custom rules
3. **SpotBugs** - Static analysis for bug detection
4. **JaCoCo** - Code coverage analysis
5. **File Encoding** - UTF-8 encoding and proper line endings verification
6. **SonarQube** - Comprehensive static code analysis (when configured)

## Running Quality Checks Locally

### Using the s8r cli (recommended)

The preferred way to run quality checks is using the s8r CLI:

```bash
# Quality Checks
./s8r quality check

# Quality Checks
./s8r quality spotless -f        # Check and fix code formatting
./s8r quality checkstyle         # Run checkstyle
./s8r quality spotbugs           # Run SpotBugs analysis
./s8r quality jacoco             # Generate coverage report
./s8r quality encoding -f        # Check and fix file encodings

# Quality Checks
./s8r quality check -v
```

### Using the legacy quality check scripts

For compatibility with older workflows, you can use the legacy scripts:

```bash
# Quality Checks
./util/bin/quality/check-build-quality.sh

# Quality Checks
./util/bin/quality/check-build-quality.sh --skip-spotless --skip-spotbugs

# Quality Checks
./util/bin/quality/check-build-quality.sh --only=spotless,checkstyle

# Quality Checks
./util/bin/quality/check-build-quality.sh --help
```

### Direct maven commands

You can also run checks directly with Maven:

```bash
# Quality Checks
mvn validate -P quality-checks

# Quality Checks
mvn clean install -P skip-quality-checks

# Quality Checks
mvn spotless:apply

# Quality Checks
mvn spotless:check

# Quality Checks
mvn checkstyle:check

# Quality Checks
mvn spotbugs:check

# Quality Checks
mvn test jacoco:report
```

## Maven Configuration Details

Quality gates are configured in the root `pom.xml` file, with specific configurations for each tool:

### Spotless configuration

```xml
<plugin>
  <groupId>com.diffplug.spotless</groupId>
  <artifactId>spotless-maven-plugin</artifactId>
  <version>${spotless.plugin.version}</version>
  <configuration>
    <java>
      <googleJavaFormat>
        <version>${spotless.google.format.version}</version>
        <style>GOOGLE</style>
      </googleJavaFormat>
      <removeUnusedImports />
      <importOrder>
        <order>java,javax,org,com,</order>
      </importOrder>
    </java>
  </configuration>
</plugin>
```

Spotless is configured to:
- Use Google Java Format for consistent code style
- Remove unused imports automatically
- Apply a specific import order (standard Java imports first, then project imports)

### Checkstyle configuration

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-checkstyle-plugin</artifactId>
  <version>${checkstyle.plugin.version}</version>
  <configuration>
    <configLocation>checkstyle.xml</configLocation>
    <consoleOutput>true</consoleOutput>
    <failsOnError>true</failsOnError>
    <includeTestSourceDirectory>true</includeTestSourceDirectory>
  </configuration>
</plugin>
```

Checkstyle is configured to:
- Use a custom ruleset defined in `checkstyle.xml`
- Apply the same rules to both production and test code
- Fail the build if style issues are found
- Output results to the console for immediate feedback

### Spotbugs configuration

```xml
<plugin>
  <groupId>com.github.spotbugs</groupId>
  <artifactId>spotbugs-maven-plugin</artifactId>
  <version>${spotbugs.plugin.version}</version>
  <configuration>
    <effort>Max</effort>
    <threshold>Medium</threshold>
    <xmlOutput>true</xmlOutput>
  </configuration>
</plugin>
```

SpotBugs is configured to:
- Use maximum analysis effort for thorough bug detection
- Set a medium threshold for reporting issues (balance between strictness and practicality)
- Generate XML reports for integration with other tools

### Jacoco configuration

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>${jacoco.plugin.version}</version>
  <executions>
    <execution>
      <id>prepare-agent</id>
      <goals>
        <goal>prepare-agent</goal>
      </goals>
    </execution>
    <execution>
      <id>report</id>
      <phase>test</phase>
      <goals>
        <goal>report</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

JaCoCo is configured to:
- Instrument code during test execution
- Generate coverage reports after tests complete
- Run automatically as part of the test phase

### Maven profiles

The `pom.xml` defines specific profiles for quality checks:

1. **quality-checks** (default): Activates all quality tools during build
2. **skip-quality-checks**: Disables all quality checks for faster builds

## CI/CD Configuration Details

The GitHub Actions workflow (`.github/workflows/samstraumr-pipeline.yml`) implements quality gates in multiple steps:

### Job: `quality-analysis`

This job runs after all tests have completed and performs the following checks:

1. **Spotless**: Checks code formatting

   ```yaml
   - name: Run Spotless check
     run: mvn -B spotless:check
   ```
2. **Checkstyle**: Verifies coding standards

   ```yaml
   - name: Run Checkstyle
     run: mvn -B checkstyle:check
   ```
3. **SpotBugs**: Performs static analysis

   ```yaml
   - name: Run SpotBugs
     run: mvn -B spotbugs:check
   ```
4. **JaCoCo**: Generates coverage reports

   ```yaml
   - name: Generate JaCoCo report
     run: mvn -B jacoco:report -Djacoco.skip=false
   ```
5. **SonarQube**: Runs when configured with appropriate secrets

   ```yaml
   - name: SonarQube analysis
     env:
       GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
       SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
       SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
     if: env.SONAR_TOKEN != '' && env.SONAR_HOST_URL != ''
     run: |
       mvn -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
         -Dsonar.projectKey=heymumford_Samstraumr \
         -Dsonar.projectName="Samstraumr" \
         -Dsonar.qualitygate.wait=true
   ```

### Quality reports as artifacts

All quality reports are uploaded as artifacts for review:

```yaml
- name: Upload Quality Reports
  if: always()
  uses: actions/upload-artifact@v4
  with:
    name: quality-reports
    path: |
      **/target/site/jacoco/
      **/target/checkstyle-result.xml
      **/target/spotbugsXml.xml
    retention-days: 7
```

## Common Issues and Solutions

### Code formatting issues

When Spotless fails, use the fix option to automatically format your code:

```bash
./s8r quality spotless -f
# Quality Checks
mvn spotless:apply
```

Common formatting issues include:
- Incorrect indentation (use spaces, not tabs)
- Line length exceeding limits (keep under 100 characters)
- Missing or incorrect braces placement
- Import order violations

### File encoding issues

For encoding issues, ensure all files are saved with UTF-8 encoding:

```bash
./s8r quality encoding -f
# Quality Checks
./util/bin/quality/fix-line-endings.sh
```

Common encoding issues include:
- Files with BOM markers
- CRLF vs LF line endings (use LF for all files except .bat/.cmd)
- Non-UTF-8 character encoding (especially in files created on Windows)

### Spotbugs issues

To see detailed information about SpotBugs issues:

```bash
mvn spotbugs:gui
```

Common SpotBugs issues include:
- Null pointer dereferences
- Resource leaks (unclosed streams, readers, etc.)
- Potential concurrency issues
- Inefficient code patterns

### Checkstyle issues

Checkstyle warnings can be viewed in the console output or in the generated XML file:

```bash
cat target/checkstyle-result.xml
```

Common Checkstyle issues include:
- Missing Javadoc comments
- Method parameter naming violations
- Line length exceeding limits
- Whitespace and brace placement issues

## Quality Metrics and Reports

After running tests and quality checks, reports are available in the following locations:

- **JaCoCo**: `target/site/jacoco/index.html`
  - Shows code coverage metrics with highlighted source code
  - Includes line, branch, and method coverage statistics
- **Checkstyle**: `target/checkstyle-result.xml`
  - Lists all style violations with file and line references
  - Contains detailed messages about each issue
- **SpotBugs**: `target/spotbugsXml.xml`
  - Contains all detected bugs categorized by type
  - Includes bug patterns, priorities, and locations
- **Cucumber**: `target/cucumber-reports/cucumber.html`
  - Comprehensive BDD test report with scenarios and steps
  - Shows pass/fail status and execution time

In the CI/CD pipeline, these reports are uploaded as artifacts and can be downloaded from the GitHub Actions workflow run page.

## Quality Gate Thresholds

The project enforces the following quality thresholds:

1. **Spotless**: Zero formatting issues (strict enforcement)
2. **Checkstyle**: Zero style violations (configurable severity levels)
3. **SpotBugs**: No bugs at or above medium priority
4. **JaCoCo**: No minimum coverage threshold enforced, but reports generated for visibility
5. **SonarQube**: When enabled, follows the configured quality gate in SonarQube

## Adding Custom Quality Checks

To add new quality checks:

1. Add the appropriate plugin to the `pom.xml`
2. Configure the plugin with project-specific settings
3. Update the `quality-checks` profile to include the new check
4. Add the check to the CI/CD pipeline in `.github/workflows/samstraumr-pipeline.yml`
5. Update this documentation with details of the new check

## Quality Check Resources

- [Spotless Documentation](https://github.com/diffplug/spotless)
- [Checkstyle Documentation](https://checkstyle.org/)
- [SpotBugs Documentation](https://spotbugs.github.io/)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [SonarQube Documentation](https://docs.sonarqube.org/)
