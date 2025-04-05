# GitHub Actions Workflows

This directory contains the CI/CD workflows for the Samstraumr project.

## Available Workflows

1. **samstraumr-pipeline.yml** - Main CI pipeline that runs on every push and PR
   - Builds and tests on Java 21
   - Verifies compilation and basic structure
   - Runs unit tests

2. **smoke-test.yml** - Runs smoke tests weekly
   - Runs every Monday at 4 AM UTC
   - Can be triggered manually
   - Tests core functionality

3. **full-pipeline.yml** - Comprehensive testing
   - Runs every Sunday at 2 AM UTC
   - Can be triggered manually
   - Tests on both Java 21 and 17
   - Includes security dependency checks

4. **pages.yml** - GitHub Pages deployment
   - Builds and deploys documentation site

## Java 21 Migration Notes

- All workflows have been updated to use Java 21
- Added necessary JVM flags for Java module system compatibility:
  ```
  --add-opens java.base/java.lang=ALL-UNNAMED
  --add-opens java.base/java.util=ALL-UNNAMED
  --add-opens java.base/java.lang.reflect=ALL-UNNAMED
  ```
- Full-pipeline includes testing on Java 17 for backward compatibility
# Last workflow check: 2025-04-04 21:44:57
