# Samstraumr CI/CD Workflows

This directory contains GitHub Actions workflows for Samstraumr (s8r) continuous integration.

## Current Workflows

### `s8r-ci` (Primary CI Pipeline)

A simplified CI pipeline focused on basic verification during TDD-based development. This workflow:

- Runs on push to main branch, pull requests, and manual triggering
- Performs basic compilation and structure verification
- Skips tests, quality checks, and detailed reports to enable rapid iteration

### `pages.yml`

Handles GitHub Pages deployment for documentation.

## TDD Development Approach

The current CI pipeline is intentionally simplified to support test-driven development (TDD) by:

1. Only checking for compilation errors and basic project structure
2. Not running full test suites or quality checks
3. Providing fast feedback on basic build health

This allows developers to focus on implementing tests and functionality incrementally without failing CI runs during early development stages.

## Future Enhancements

As the project matures, the CI pipeline will be expanded to include:

- Full test suite execution (unit, integration, orchestration)
- Code quality checks (spotless, checkstyle, spotbugs)
- Coverage reports and SonarQube analysis
- Comprehensive build reports

---

**Note:** The previous comprehensive CI pipeline is preserved in the git history and can be restored when the project reaches appropriate maturity.