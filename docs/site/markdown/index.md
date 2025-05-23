<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Index

![Build Status](https://github.com/heymumford/Samstraumr/actions/workflows/samstraumr-pipeline.yml/badge.svg)

Samstraumr is a design framework for building adaptive software systems through Tube-Based Development (TBD). It implements principles from systems theory to create resilient, self-monitoring components.

## Report Overview

This build report provides a comprehensive overview of the health and status of the Samstraumr codebase, following the Samstraumr philosophy of system awareness and adaptability.

Just as Tubes in Samstraumr monitor their state and environment, this report monitors the overall system health, providing metrics on:

1. **Build Status**: Current state of the system build
2. **Test Coverage**: How thoroughly the code is tested
3. **Code Quality**: How well the code adheres to quality standards
4. **Dependencies**: External components the system relies on

Use this report as a form of system awareness to guide improvements and adaptations to the codebase.

## Quick Navigation

- [Build Status](build-status.html.md) - Flow status of the build pipeline
- [Build Metrics](build-metrics.html.md) - Quantitative measures of the build
- [Test Results](surefire-report.html.md) - Detailed test outcomes
- [Code Coverage](jacoco/index.html.md) - Test coverage analysis
- [Code Quality](checkstyle.html.md) - Coding standards compliance

## Report Generation

This report was automatically generated as part of the build process. To generate this report locally, run:

```
mvn clean site -P build-report
```
