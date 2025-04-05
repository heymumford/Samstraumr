<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Build Report Guide

This document outlines how to generate and interpret comprehensive build reports for Samstraumr, following the project's philosophy of flow, awareness, and adaptive components.

## Quick Start Guide

Generate a build report from the Linux command line with a single command:

```bash
# Build Report Guide
./util/build/generate-build-report.sh --skip-tests --skip-quality
```

The report will be generated at `target/samstraumr-report/index.html`. Open it in your browser:

```bash
# Build Report Guide
xdg-open target/samstraumr-report/index.html

# Build Report Guide
firefox target/samstraumr-report/index.html
# Build Report Guide
google-chrome target/samstraumr-report/index.html
```

## Additional Options

```bash
# Build Report Guide
./util/build/generate-build-report.sh

# Build Report Guide
./util/build/generate-build-report.sh --skip-tests

# Build Report Guide
./util/build/generate-build-report.sh --skip-quality

# Build Report Guide
./util/build/generate-build-report.sh --output /path/to/custom/directory

# Build Report Guide
./util/build/generate-build-report.sh --help
```

## Philosophy

In Samstraumr, we view build reports as a form of system awareness, similar to how Tubes monitor their state and environment. A proper build report should:

1. **Provide Flow Visibility**: Track the movement of code from conception to execution
2. **Enable Self-Awareness**: Offer metrics that allow the system to understand its own health
3. **Support Adaptation**: Identify areas needing improvement to guide evolution
4. **Maintain Lineage**: Record the history and reasoning behind changes

Just as Tubes log to their "Mimir log" to maintain state information, our build reports serve as a comprehensive record of system health and development flow.

## Integrated Build Report Structure

### 1. build status summary

The integrated build report aggregates data from multiple sources:

- **Build Flow**: Shows the progression through pipeline stages
- **Test Results**: Summarizes test outcomes across all test categories
- **Quality Metrics**: Presents code quality indicators
- **Component Health**: Reports on the health of individual components

### 2. implementation options

#### Option a: maven site plugin

The most comprehensive Maven-native solution involves configuring the Maven Site plugin:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-site-plugin</artifactId>
  <version>3.12.1</version>
  <configuration>
    <locales>en</locales>
    <reportPlugins>
      <!-- All reporting plugins go here -->
    </reportPlugins>
  </configuration>
</plugin>

<reporting>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-project-info-reports-plugin</artifactId>
    </plugin>
    <plugin>
      <groupId>org.jacoco</groupId>
      <artifactId>jacoco-maven-plugin</artifactId>
    </plugin>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-checkstyle-plugin</artifactId>
    </plugin>
    <plugin>
      <groupId>com.github.spotbugs</groupId>
      <artifactId>spotbugs-maven-plugin</artifactId>
    </plugin>
    <plugin>
      <groupId>net.masterthought</groupId>
      <artifactId>maven-cucumber-reporting</artifactId>
    </plugin>
  </plugins>
</reporting>
```

To generate the site: `mvn site`

#### Option b: custom report aggregator script

Create a script in the `util/build` directory that:

1. Gathers data from all individual reports
2. Transforms them into a unified format
3. Generates a single HTML report with all metrics

```bash
# Build Report Guide
# Build Report Guide

# Build Report Guide
REPORT_DIR="target/samstraumr-report"
mkdir -p "$REPORT_DIR"

# Build Report Guide
echo "Gathering report data..."

# Build Report Guide
TEST_RESULTS=$(find . -type d -name "surefire-reports" -exec grep -l "testcase" {}/*.xml \; | xargs cat)

# Build Report Guide
COVERAGE_DATA=$(find . -name "jacoco.xml" -exec cat {} \;)

# Build Report Guide
CHECKSTYLE=$(find . -name "checkstyle-result.xml" -exec cat {} \;)
SPOTBUGS=$(find . -name "spotbugsXml.xml" -exec cat {} \;)

# Build Report Guide
CUCUMBER=$(find . -name "cucumber.json" -exec cat {} \;)

# Build Report Guide
echo "Generating unified report..."
cat > "$REPORT_DIR/index.html" << EOF
<!DOCTYPE html>
<html>
<head>
  <title>Samstraumr Build Report</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 40px; }
    .section { margin-bottom: 30px; }
    .metrics { display: flex; flex-wrap: wrap; }
    .metric { border: 1px solid #ccc; padding: 15px; margin: 10px; flex: 1; min-width: 200px; }
    .success { color: green; }
    .warning { color: orange; }
    .error { color: red; }
  </style>
</head>
<body>
  <h1>Samstraumr Build Report</h1>
  <p>Generated on $(date)</p>
  <p>Version: $(grep "samstraumr.version=" "Samstraumr/version.properties" | cut -d= -f2)</p>
  
  <!-- Report sections go here -->
</body>
</html>
EOF

echo "Build report generated at $REPORT_DIR/index.html"
```

#### Option c: github pages integration

Leverage GitHub Actions to publish the build report to GitHub Pages:

1. Add a reporting job to your GitHub workflow
2. Generate the report using either Maven Site or custom script
3. Publish to GitHub Pages

Example workflow addition:

```yaml
  report-generation:
    name: Generate Build Report
    needs: [quality-analysis, btl-tests]
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: 17
          distribution: 'temurin'

      - name: Download all artifacts
        uses: actions/download-artifact@v4
        with:
          path: artifacts
      
      - name: Generate comprehensive report
        run: |
          ./util/build/samstraumr-build-report.sh
      
      - name: Deploy to GitHub Pages
        uses: JamesIves/github-pages-deploy-action@v4
        with:
          folder: target/samstraumr-report
          branch: gh-pages
```

### 3. report contents

The comprehensive build report should include the following sections, inspired by Samstraumr's Tube status monitoring:

#### Build flow status

- **INITIALIZING**: Project setup and environment preparation
- **PROCESSING**: Compilation and resource processing
- **VALIDATING**: Tests and quality checks
- **OUTPUTTING**: Packaging and artifact generation
- **ERROR**: Any failures encountered during the build

#### Test results by category

- Test success/failure counts by category (ATL/BTL)
- Test duration and performance metrics
- Failure analysis with root cause indicators

#### Quality metrics

- Code coverage percentage (overall and by component)
- Code quality indicators (complexity, technical debt)
- Static analysis findings categorized by severity
- Trend analysis showing quality evolution over time

#### Component health

- Status of each major component (like Tubes)
- Dependency health and update recommendations
- Performance indicators by component

## Using the Build Report

The build report serves several purposes within the Samstraumr development workflow:

1. **Decision Support**: Informs decisions about where to focus improvement efforts
2. **Release Readiness**: Determines if a build is ready for release
3. **System Awareness**: Provides visibility into the system's current state
4. **Adaptation Guidance**: Identifies areas needing adaptation and improvement

Like a Tube that monitors its environment and adapts, teams should use the build report to adapt their development approach based on the feedback it provides.

## Best Practices

1. **Generate Reports Regularly**: Not just during releases, but during development
2. **Track Trends**: Monitor how metrics change over time
3. **Set Quality Gates**: Define minimum thresholds for metrics before allowing release
4. **Address Issues Promptly**: Treat report findings as opportunities for improvement
5. **Share Widely**: Make reports accessible to all team members

## Implementation Recommendations

For Samstraumr, we recommend implementing:

1. **Stage 1**: Add Maven Site plugin configuration to aggregate existing reports
2. **Stage 2**: Develop custom report styling that aligns with Samstraumr visual identity
3. **Stage 3**: Create a GitHub Action workflow that publishes reports to GitHub Pages
4. **Stage 4**: Integrate trend analysis to track metrics over time
