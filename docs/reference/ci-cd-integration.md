<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# CI/CD Integration Guide

This guide explains how to use Samstraumr's Continuous Integration and Continuous Deployment (CI/CD) tools to automate building, testing, and releasing the framework.

## Overview

Samstraumr includes a comprehensive CI/CD pipeline that:

1. Runs quality checks (static analysis, code style)
2. Executes tests at different levels (unit, component, integration)
3. Analyzes test coverage
4. Packages and publishes artifacts
5. Generates documentation
6. Creates releases

The CI/CD system consists of:

- GitHub Actions workflows for cloud-based automation
- Local scripts for running the same checks on developers' machines
- Tools for triggering workflows remotely

## CI/CD Architecture

![CI/CD Architecture](../assets/ci-cd-architecture.png)

The Samstraumr CI/CD system is designed with these principles:

1. **Consistency**: The same checks run locally and in the cloud
2. **Speed**: Fast feedback through parallel execution and incremental builds
3. **Reliability**: Comprehensive testing at multiple levels
4. **Transparency**: Clear reporting of results and issues
5. **Automation**: Minimal manual intervention required

## GitHub Actions Workflows

### Main CI Workflow

The primary CI workflow is defined in `.github/workflows/s8r-ci.yml` and includes these jobs:

| Job | Description | Runs When |
|-----|-------------|-----------|
| basic-verification | Fast checks to verify basic build requirements | Always |
| quality-checks | Static analysis (checkstyle, PMD, SpotBugs) | After basic verification |
| unit-tests | Low-level unit tests | After basic verification |
| component-tests | Component-level tests | After unit tests |
| composite-tests | Composite component tests | After unit tests |
| integration-tests | System integration tests | After component and composite tests |
| coverage-analysis | Code coverage analysis | After all tests |
| package-deploy | Build and publish artifacts | After tests on main branch |
| documentation | Generate and publish documentation | After package-deploy |

### Using GitHub Actions

To trigger workflows manually:

1. Go to the repository on GitHub
2. Click "Actions"
3. Select the workflow
4. Click "Run workflow"
5. Choose options and click "Run"

## Local CI Runner

The `s8r-ci` script runs the same checks locally that would run in the cloud pipeline.

### Installation

The script is already included in the repository at `bin/ci/s8r-ci`.

### Basic Usage

```bash
# Run the entire CI pipeline locally
./bin/s8r-ci

# Run with verbose output
./bin/s8r-ci --verbose

# Run only specific jobs
./bin/s8r-ci --job unit-tests
./bin/s8r-ci --job quality-checks
```

### Advanced Options

```bash
# Skip running tests (run only verification and quality checks)
./bin/s8r-ci --skip-tests

# Dry run (show what would be run without executing)
./bin/s8r-ci --dry-run

# Run jobs in parallel where possible
./bin/s8r-ci --parallel

# Use 'act' to run GitHub Actions workflows locally
./bin/s8r-ci --act
```

### Output

The `s8r-ci` script generates a comprehensive report in the `ci-results` directory with:

- Summary of all job results
- Detailed logs for each job
- Coverage analysis report
- Build artifacts

## Remote Trigger Tool

The `trigger-workflow` script allows triggering GitHub Actions workflows programmatically from outside GitHub.

### Usage

```bash
# Trigger the default CI workflow on main branch
./bin/ci/trigger-workflow --token YOUR_GITHUB_TOKEN

# Trigger a specific workflow
./bin/ci/trigger-workflow --workflow release.yml --ref v2.5.0 --token YOUR_GITHUB_TOKEN

# Trigger with inputs
./bin/ci/trigger-workflow --input run_tests=true --input deploy=false --token YOUR_GITHUB_TOKEN
```

### Setting Up Permissions

The trigger tool requires a GitHub Personal Access Token (PAT) with appropriate permissions:

1. Go to GitHub → Settings → Developer settings → Personal access tokens
2. Create a new token with `repo` and `workflow` permissions
3. Use this token with the `--token` option or set as `GH_TOKEN` environment variable

## Release Process Integration

The CI/CD system integrates with the release process to ensure quality before publishing:

1. **Pre-Release Verification**: The `prepare-release` script verifies all requirements
2. **CI Checks**: Full CI pipeline runs to validate the release
3. **Automated Publishing**: If all checks pass, release artifacts are published

To prepare a release:

```bash
# Verify release readiness
./bin/s8r-release 2.5.0

# Publish the release after verification
./bin/release/publish-release 2.5.0
```

## Scientific Documentation Verification

As part of our commitment to making S8r accessible to scientists, the CI pipeline includes checks for documentation accessibility:

1. **Standards Verification**: Checks documentation against scientific documentation standards
2. **Jargon Detection**: Identifies excessive technical jargon in user-facing documentation
3. **Scientific Examples**: Verifies presence of domain-specific examples

## Customizing the CI Pipeline

### Adding New Checks

To add a new check to the CI pipeline:

1. Add the check to the appropriate job in `.github/workflows/s8r-ci.yml`
2. Add the same check to the corresponding function in `bin/ci/s8r-ci`
3. Update this documentation with the new check

### Skipping Checks

In special circumstances, checks can be skipped:

- In GitHub Actions, use the "Run workflow" dialog options
- In local runs, use the appropriate `--skip-*` option
- In commit messages, use `[skip ci]` to skip the entire pipeline

## Troubleshooting

### Common GitHub Actions Issues

- **Workflow doesn't run**: Check branch protection rules and trigger conditions
- **Job fails with permission error**: Verify GitHub token permissions
- **Tests pass locally but fail in CI**: Check for environment differences

### Local Runner Issues

- **Script fails to find tools**: Ensure PATH includes required tools
- **Tests fail with memory errors**: Adjust Java memory settings with MAVEN_OPTS
- **Timeout errors**: Use `--job` to run specific jobs individually

## Best Practices

1. **Run locally before pushing**: Use `s8r-ci` to verify changes before pushing
2. **Fix issues immediately**: Address CI failures as soon as they occur
3. **Keep tests fast**: Optimize tests to provide quick feedback
4. **Don't bypass checks**: Fix issues rather than disabling checks
5. **Monitor coverage**: Ensure test coverage remains above thresholds

## For Scientists and Non-Technical Users

If you're a scientist working with S8r and need to understand the CI/CD process:

1. **What is CI/CD?** Continuous Integration and Deployment automatically checks code quality and runs tests whenever changes are made. Think of it as an automated laboratory assistant that verifies everything works correctly.

2. **Why it matters to you:** CI/CD ensures that S8r remains stable and reliable for your scientific work. It automatically tests that your calculations will produce consistent results.

3. **What you need to do:** Generally, nothing! The CI/CD system works in the background. However, if you're contributing to S8r, simply run `./bin/s8r-ci` before submitting changes to ensure everything works.

## Reference

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Maven Build Lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)
- [JaCoCo Code Coverage](https://www.eclemma.org/jacoco/)