<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# README

This directory contains documentation related to development, testing, and implementation details for Samstraumr.

## Available Documents

### Testing

- **[test-strategy.md](./test-strategy.md)**: Overall testing strategy
- **[test-bdd-cucumber.md](./test-bdd-cucumber.md)**: BDD with Cucumber implementation
- **[test-annotations.md](./test-annotations.md)**: Testing annotations reference
- **[test-atl-strategy.md](./test-atl-strategy.md)**: Above-The-Line testing strategy

## Testing Approach

Samstraumr uses a comprehensive testing approach combining:

1. Behavior-Driven Development (BDD) using Cucumber for scenario testing
2. Above-The-Line (ATL) tests for critical functionality validation
3. Dual terminology for tests (both industry-standard and Samstraumr-specific)
4. Multiple test layers matching our architectural layers

## Key Testing Concepts

- **Above-The-Line (ATL)**: Critical tests that must always pass for a build to succeed
- **Behavior-Driven Development**: Scenario-based testing with Gherkin syntax
- **Test Pyramid**: Balance of unit, integration, and end-to-end tests
- **Test Equivalence**: Mapping between industry-standard and Samstraumr terminology
