# Samstraumr Test Tools

This directory contains tools for running, analyzing, and verifying tests in the Samstraumr framework.

## Tools

### run-full-test-suite

Comprehensive test suite runner that:
- Runs all tests in the test suite
- Generates coverage reports
- Analyzes test coverage
- Verifies test suite quality
- Produces detailed reports

```bash
# Basic usage
./run-full-test-suite

# Run with parallel execution and verbose output
./run-full-test-suite -v -p

# Set custom coverage threshold and fix issues
./run-full-test-suite -t 90 -f
```

### verify-test-suite

Tool to verify test suite quality according to standards:
- Validates feature files
- Checks step definitions
- Verifies test runners
- Validates test tags
- Analyzes test coverage
- Verifies Maven structure

```bash
# Basic usage
./verify-test-suite

# Generate detailed verification report
./verify-test-suite -r

# Try to fix common issues automatically
./verify-test-suite -f
```

### analyze-test-coverage

Detailed test coverage analysis tool that:
- Analyzes JaCoCo coverage reports
- Identifies coverage gaps
- Provides actionable recommendations
- Prioritizes test development areas

```bash
# Basic usage
./analyze-test-coverage

# Generate detailed coverage analysis report
./analyze-test-coverage -r

# Analyze specific packages
./analyze-test-coverage -i 'org.s8r.core.*'
```

## Workflow

For Phase 4 deployment preparation, use these tools in the following order:

1. **Verify Test Suite**: Check if tests meet quality standards
   ```bash
   ./verify-test-suite -r
   ```

2. **Fix Issues**: Address any issues found during verification
   ```bash
   ./verify-test-suite -f
   ```

3. **Run Full Test Suite**: Execute all tests to ensure they pass
   ```bash
   ./run-full-test-suite -p
   ```

4. **Analyze Coverage**: Identify any remaining coverage gaps
   ```bash
   ./analyze-test-coverage -r
   ```

5. **Implement Additional Tests**: Based on coverage analysis results

6. **Repeat**: Verify, run tests, and analyze until coverage meets standards

## Reports

All tools can generate detailed reports in various formats:
- Markdown reports for documentation
- Plain text for command-line viewing
- HTML for web viewing (where supported)

Reports are stored in the `/test-results` directory by default.

## Configuration

These tools respect the following environment variables:
- `PROJECT_ROOT`: Root directory of the project
- `MAVEN_OPTS`: Options to pass to Maven when running tests

## Integration with CI/CD

These tools can be integrated into CI/CD pipelines:

```yaml
test-verification:
  script:
    - ./bin/test/verify-test-suite -r -e
    
full-test-suite:
  script:
    - ./bin/test/run-full-test-suite -p
  artifacts:
    paths:
      - test-results/
```

## See Also

- `/docs/reference/build-and-test.md`: Documentation on the build and test process
- `/util/lib/test-lib.sh`: Common library functions for testing
- `/modules/samstraumr-core/src/test/`: Test source directory