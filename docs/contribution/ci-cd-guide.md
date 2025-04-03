# Continuous Integration and Deployment Guide

This guide explains how to work with Samstraumr's CI/CD pipeline and verify your changes before submitting them.

## GitHub Actions Pipeline

Samstraumr uses GitHub Actions for continuous integration, with the main workflow defined in `.github/workflows/samstraumr-pipeline.yml`.

### Pipeline Structure

The pipeline consists of the following stages:

1. **Get Version**: Extracts the current version from `version.properties`
2. **Initialization**: Verifies basic compilation and setup
3. **Orchestration Tests**: Runs high-level system assembly tests (ATL)
4. **Parallel Test Execution**:
   - Unit and Component Tests
   - Integration Tests
   - E2E and Acceptance Tests
5. **Quality Analysis**: Runs code quality checks (on PRs and main branch)
6. **BTL Tests**: Runs Below-The-Line tests (on workflow dispatch or main branch)
7. **Workflow Summary**: Generates status report

### Automatic Badge Updates

The build status badge in the README is automatically updated when the pipeline runs on GitHub. The badge URL is:

```
[![Build Status](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml/badge.svg)](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml)
```

## Local Pipeline Verification with Act

[Act](https://github.com/nektos/act) allows you to run GitHub Actions workflows locally to verify your changes before pushing them.

For generating build reports locally without GitHub Actions:

```bash
# Generate a local build report (fastest option)
./util/build/generate-build-report.sh --skip-tests --skip-quality

# Generate full report including tests and quality checks
./util/build/generate-build-report.sh
```

The build report will be generated in `target/samstraumr-report/index.html`.

### Installation

1. **Linux/WSL**:
   ```bash
   sudo curl -s https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash
   ```
   
   Alternatively, download the binary and place in your path:
   ```bash
   sudo mv act /usr/local/bin/
   ```

2. **macOS**:
   ```bash
   brew install act
   ```

3. **Windows**:
   ```bash
   choco install act-cli
   ```

### Configuration

1. Create a configuration file:
   ```bash
   mkdir -p ~/.config/act
   echo "-P ubuntu-latest=ghcr.io/catthehacker/ubuntu:act-latest" > ~/.config/act/actrc
   ```

2. Configure for root user if needed (for docker socket access):
   ```bash
   sudo mkdir -p /root/.config/act
   sudo bash -c 'echo "-P ubuntu-latest=ghcr.io/catthehacker/ubuntu:act-latest" > /root/.config/act/actrc'
   ```

### Running Workflows Locally

1. **List Available Jobs**:
   ```bash
   act -l
   ```

2. **Dry Run** (no actual execution):
   ```bash
   act -j initialization --dryrun
   ```

3. **Run Individual Jobs**:
   ```bash
   sudo act -j get-version
   sudo act -j initialization
   ```

4. **Run with Specific Event**:
   ```bash
   sudo act workflow_dispatch -j get-version -W .github/workflows/samstraumr-pipeline.yml
   ```

5. **Run Entire Workflow**:
   ```bash
   sudo act
   ```

### Troubleshooting

1. **Permission Issues**:
   - If you encounter permission errors with Docker/Podman: `sudo act` to run with elevated privileges
   - Ensure Docker daemon is running: `systemctl status docker` (or equivalent)

2. **Image Issues**:
   - Act uses container images to simulate GitHub runners
   - Medium size image (~500MB) is recommended: `ghcr.io/catthehacker/ubuntu:act-latest`
   - If images won't pull, check internet connection and registry access

3. **Act Not Finding Workflows**:
   - Use `-W .github/workflows/samstraumr-pipeline.yml` to explicitly specify workflow file
   - Ensure you're running act from the repository root directory

## Version Management

For version bumps and badge updates:

1. **Bump Version**:
   ```bash
   ./util/version bump patch
   ```

2. **Generate Badges**:
   ```bash
   ./util/badges/generate-badges.sh all
   ```

## Best Practices

1. **Pre-Push Verification**:
   - Run at minimum: `act -j initialization --dryrun`
   - Run all tests locally: `./run-tests.sh all`
   - Check code quality: `./util/quality/build-checks.sh`

2. **Pull Request Workflow**:
   - Create feature branch
   - Make changes and run local verification
   - Create PR
   - Verify GitHub Actions pipeline passes
   - Address review comments
   - Merge when approved

3. **Badge Status**:
   - Green: All tests passing
   - Yellow: In progress
   - Red: Tests failing
   
   Always make sure your changes maintain a green badge status!