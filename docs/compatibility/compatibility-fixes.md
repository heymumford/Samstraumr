<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Compatibility Fixes

This document summarizes the changes made to support the transition from the legacy `Bundle` architecture to the new `Composite` architecture.

## Summary of Changes

1. **Bundle Compatibility Layer**
   - Updated `Bundle.java` to properly delegate to `Composite`
   - Implemented `Bundle.BundleEvent` and `Bundle.CircuitBreaker` adapter classes
   - Fixed `BundleFactory` to properly create instances with delegation to `CompositeFactory`
2. **Test Framework Compatibility**
   - Added PostgreSQL TestContainers dependency to support `StreamTest`
   - Fixed imports in test files for annotations
   - Enhanced `RunTests.java` to map between industry-standard and Samstraumr-specific terminology
   - Fixed `RunATLCucumberTest.java` to support both JUnit and Cucumber-based ATL tests
3. **Type Compatibility Issues**
   - Fixed `StreamTest.java` to use the fully qualified `org.samstraumr.tube.composite.Composite.CircuitBreaker` type
   - Fixed `PatternSteps.java` to address `Bundle.BundleEvent` inconsistencies
4. **Documentation**
   - Created a README.md in the bundle package explaining the compatibility approach
   - Created a run-atl-tests.sh script to simplify running ATL tests
   - Documented the compatibility layer and migration path

## Testing

To run ATL tests, use the provided script:

```bash
./run-atl-tests.sh
```

## Next Steps

1. Begin migrating from `Bundle` to `Composite` in application code
2. Update remaining test files to use `Composite` instead of `Bundle`
3. Consider adding deprecation warnings to encourage migration
4. Plan for complete removal of `Bundle` in a future release

## Compatibility Mapping

| Industry Standard | Samstraumr Specific |
|-------------------|---------------------|
| Unit              | Tube                |
| Component         | Composite           |
| Integration       | Flow                |
| API               | Machine             |
| System            | Stream              |
| Property          | Adaptation          |
| End-to-End        | Acceptance          |
| Smoke             | Orchestration       |
