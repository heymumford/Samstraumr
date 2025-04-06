# OWASP Dependency Check Configuration

This document describes how the OWASP Dependency Check tool is configured and used in the Samstraumr project to identify and manage security vulnerabilities in project dependencies.

## Overview

OWASP Dependency Check is an open-source tool that detects publicly disclosed vulnerabilities in project dependencies. It's configured to run only in CI pipelines to avoid disrupting the development workflow, while still ensuring security checks are performed regularly.

## Configuration

The OWASP Dependency Check is configured in the parent `pom.xml` file and is activated via Maven profiles. The tool is not part of the standard build process and must be explicitly enabled by activating a specific profile.

### Profiles

Two profiles have been created to run the dependency check:

1. `ci-pipeline` - Runs the dependency check in blocking mode:
   - Fails the build for vulnerabilities with CVSS score 7.0 or higher
   - Used in the CI/CD pipeline to enforce security standards

2. `ci-pipeline-nonblocking` - Runs the dependency check in non-blocking mode:
   - Generates reports but does not fail the build
   - Useful for local testing of the CI pipeline

### Configuration Options

The following configuration options have been set:

- `parallelAnalysis`: Enabled to improve scan performance
- `failBuildOnCVSS`: Set to 7 for ci-pipeline (medium severity) and 11 for ci-pipeline-nonblocking (effectively disabled)
- `formats`: HTML and JSON reports are generated
- `suppressionFile`: Points to `owasp-suppressions.xml` to manage false positives
- `autoUpdate`: Set to true to automatically update the vulnerability database
- `cveValidForHours`: Set to 24 hours for ci-pipeline, 168 hours (7 days) for ci-pipeline-nonblocking
- `nvdApiWait`: Set to 2000ms to handle API rate limiting
- `nvdApiMaxRetries`: Set to appropriate values to handle temporary failures
- `skipOnError`: Set to true for nonblocking profile, false for blocking profile
- Various analyzer settings optimized for this project

### NVD API Key

OWASP Dependency Check can use the NVD (National Vulnerability Database) API to fetch vulnerability data. Using an API key significantly improves performance and reliability.

#### Setting up an NVD API Key

1. Register for an API key at the [NVD Website](https://nvd.nist.gov/developers/request-an-api-key)
2. Set the API key as an environment variable:

```bash
export NVD_API_KEY=your-api-key-here
```

3. In CI environments, configure this as a secret environment variable

For GitHub Actions workflows, add it as a repository secret and reference it in the workflow:

```yaml
- name: Run OWASP Dependency Check
  run: mvn -B verify -P ci-pipeline
  env:
    NVD_API_KEY: ${{ secrets.NVD_API_KEY }}
```

## Usage

### In CI Pipeline

The CI pipeline should activate the `ci-pipeline` profile to run the dependency check and fail the build if medium to high severity vulnerabilities are found.

### Local Testing

To test the dependency check locally without failing the build:

```bash
mvn clean verify -P ci-pipeline-nonblocking
```

To simulate the CI pipeline behavior locally (will fail on vulnerabilities):

```bash
mvn clean verify -P ci-pipeline
```

## Suppressing False Positives

If a vulnerability is identified as a false positive, it can be suppressed by adding an entry to the `owasp-suppressions.xml` file.

Example of a suppression entry:

```xml
<suppress>
   <notes>
     <![CDATA[
       False positive for CVE-XXXX-YYYY. We're using version z.y.z which has been patched.
     ]]>
   </notes>
   <packageUrl regex="true">^pkg:maven/group\.name/artifact\.name@.*$</packageUrl>
   <cve>CVE-XXXX-YYYY</cve>
</suppress>
```

Each suppression should include:
- Detailed notes explaining why it's a false positive
- The package URL pattern
- The specific CVE(s) being suppressed

## Reports

After running the dependency check, reports can be found at:

- HTML: `target/dependency-check-report.html`
- JSON: `target/dependency-check-report.json`

## Best Practices

1. Run the OWASP dependency check regularly during development
2. Review all vulnerabilities and address high severity issues promptly
3. Document any suppressed vulnerabilities thoroughly
4. Keep the NVD database up to date by running the check periodically
5. Include dependency-check reports in security reviews