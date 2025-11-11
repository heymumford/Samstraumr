# Port Interface CI/CD Integration

**Date:** April 8, 2025  
**Status:** Active  
**Author:** Eric C. Mumford (@heymumford)

## Overview

This document describes the integration of port interface testing, including performance testing, into the Continuous Integration and Continuous Deployment (CI/CD) pipeline for the Samstraumr project.

## CI/CD Integration Strategy

The Samstraumr project uses GitHub Actions for CI/CD automation. Port interface testing has been integrated into this pipeline to ensure that both functional correctness and performance characteristics of port interfaces are maintained throughout development.

## Workflows

### 1. Port Interface Functional Tests Workflow

The `port-interface-tests.yml` workflow runs all functional tests for port interfaces on each push to the main branch and on pull requests that affect port interface code. This includes:

- Unit tests for each port interface
- Contract tests for port implementations
- Integration tests between port interfaces

The workflow is triggered when:
- Code is pushed to the main branch
- A pull request is opened against the main branch
- Changes are made to port interface code or tests

### 2. Port Interface Performance Tests Workflow

The `port-performance.yml` workflow runs performance tests to ensure that port interfaces meet their performance requirements. This includes:

- Smoke performance tests on each push and pull request
- Full performance test suite weekly and on-demand
- Performance regression checks against established baselines

The workflow is triggered when:
- Code is pushed to the main branch
- A pull request is opened against the main branch
- Changes are made to port interface code or tests
- Scheduled to run weekly (full suite)
- Manually triggered via workflow dispatch

## Performance Monitoring

The port interface performance testing framework generates comprehensive reports that are stored as artifacts in the CI/CD pipeline. These reports include:

1. **Performance Metrics**:
   - Average operation time
   - 95th and 99th percentile response times
   - Throughput (operations per second)
   - Success rate
   - Resource utilization

2. **Performance Regression Detection**:
   - Current metrics are compared to established baselines
   - Alerts are generated when performance degrades beyond acceptable thresholds
   - Historical performance data is maintained for trend analysis

3. **Baseline Management**:
   - Baseline metrics are updated weekly during the scheduled full performance test suite run
   - Historical baselines are maintained for reference
   - Baseline changes are committed to the repository for tracking

## Report Generation

Performance test reports are generated in multiple formats:

1. **HTML Reports**: Interactive reports with detailed metrics and charts
2. **JSON Reports**: Machine-readable data for automated analysis
3. **Markdown Reports**: Human-readable summaries for quick review
4. **Baseline Reports**: Reference metrics for regression detection

All reports are stored as artifacts in the CI/CD pipeline and can be downloaded for analysis.

## Integration with Development Workflow

The port interface CI/CD integration enhances the development workflow in several ways:

1. **Early Detection of Issues**:
   - Functional issues are detected immediately on push or pull request
   - Performance regressions are identified before they affect production

2. **Automated Performance Testing**:
   - Eliminates the need for manual performance testing
   - Ensures consistent performance testing methodology
   - Makes performance testing a routine part of development

3. **Quality Gates**:
   - Pull requests that cause performance regressions can be automatically rejected
   - Performance metrics become a factor in code review
   - Encourages performance-aware development

4. **Documentation and Reporting**:
   - Developers can easily access performance reports
   - Performance trends are tracked and documented
   - Performance improvements can be quantified and recognized

## Setup and Configuration

To set up the port interface CI/CD integration, the following files are needed:

1. `.github/workflows/port-interface-tests.yml` - Functional test workflow
2. `.github/workflows/port-performance.yml` - Performance test workflow
3. `s8r-test-port-interfaces` - Script for running port interface tests
4. `s8r-test-port-performance` - Script for running port interface performance tests

These files are already configured in the Samstraumr repository.

## Running Locally

Developers can run the same tests locally for pre-commit verification:

```bash
# Run functional tests
./s8r-test-port-interfaces all

# Run performance tests
./s8r-test-port-performance smoke
```

## Conclusion

The integration of port interface testing, including performance testing, into the CI/CD pipeline ensures that both functional correctness and performance characteristics of port interfaces are maintained throughout development. This approach makes performance testing a routine part of the development process and provides early detection of issues.

## Related Documentation

- [Port Interface Test Report](/docs/test-reports/port-interface-test-report.md)
- [Port Interface Performance Testing](/docs/test-reports/port-interface-performance.md)
- [Port Interface Performance Benchmark Report](/docs/test-reports/port-interface-performance-report.md)