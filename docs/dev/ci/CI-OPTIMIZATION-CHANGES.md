# CI/CD Optimization Changes

This document describes the changes made to address feedback on PR #105 regarding CI test stages and Docker Compose configuration.

## Changes Made

### 1. Consolidated Dockerfiles

**Problem**: Maintaining four separate Dockerfiles for each test stage was becoming burdensome and led to duplication.

**Solution**: Created a single multi-stage `Dockerfile.test` that consolidates all test stages:
- `test-base`: Base stage with common dependencies and configuration
- `test-unit`: Unit test stage (extends test-base)
- `test-contract`: Contract test stage (extends test-base)
- `test-integration`: Integration test stage (extends test-base)

**Benefits**:
- Single source of truth for Docker configuration
- Easier to maintain and update
- Better layer caching through multi-stage builds
- Reduced duplication

**Files**:
- Created: `Dockerfile.test` (replaces 4 separate Dockerfiles)
- Updated: `docker-compose.test.yml` to use the new multi-stage Dockerfile

### 2. Reduced GitHub Actions Workflow Duplication

**Problem**: The GitHub Actions workflow had a lot of duplicated checkout/cache/container steps across all jobs.

**Solution**: Created a composite action `.github/actions/setup-test-environment/action.yml` that encapsulates common setup steps:
- Checkout code
- Cache Maven dependencies
- Optional: Compile project
- Optional: Compile test classes

**Benefits**:
- DRY (Don't Repeat Yourself) principle
- Easier to maintain and update common setup logic
- Consistent setup across all jobs
- Reduced workflow file size

**Files**:
- Created: `.github/actions/setup-test-environment/action.yml`
- Updated: `.github/workflows/s8r-ci-v2.yml` to use the composite action

### 3. Fixed MAVEN_CLI_OPTS Expansion in Docker Compose

**Problem**: Docker Compose does not expand environment variables in the 'command' field using `${VAR}` syntax.

**Solution**: 
- Added `MAVEN_CLI_OPTS` to the environment section
- Changed command to use shell variable expansion with `$$MAVEN_CLI_OPTS`

**Benefits**:
- Proper variable expansion at runtime
- More maintainable configuration

**Files**:
- Updated: `docker-compose.test.yml` (contract service)

## Usage

### Docker Compose (Local Development)

Build all test images:
```bash
docker-compose -f docker-compose.test.yml build
```

Run specific test stage:
```bash
docker-compose -f docker-compose.test.yml run unit
docker-compose -f docker-compose.test.yml run contract
docker-compose -f docker-compose.test.yml run integration
```

Run all tests in parallel:
```bash
docker-compose -f docker-compose.test.yml up
```

### GitHub Actions Workflow

The workflow now uses the composite action for all test jobs. No changes to workflow triggers or job dependencies were made.

## Migration Notes

### Old Dockerfiles (Deprecated)

The following files are now deprecated and can be removed:
- `Dockerfile.test-base`
- `Dockerfile.test-unit`
- `Dockerfile.test-contract`
- `Dockerfile.test-integration`

They have been replaced by the single `Dockerfile.test` with multi-stage builds.

### Backward Compatibility

The docker-compose configuration maintains the same service names and commands, so existing scripts and documentation should continue to work without changes.

## Future Improvements

Consider these additional optimizations:
- Use GitHub Actions matrix strategy for parallel test execution
- Add more parameterization to the composite action
- Consider using Docker BuildKit for better caching
- Add health checks to Docker Compose services
