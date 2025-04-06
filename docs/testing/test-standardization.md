<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Test Standardization

## Changes Made

1. Renamed test classes to avoid conflicts with annotation names:
   - AdaptationTest → AdaptationTestSuite
   - BundleTest → BundleTestSuite
   - CompositeTest → CompositeTestSuite
   - FlowTest → FlowTestSuite
   - StreamTest → StreamTestSuite
   - TubeTest → TubeTestSuite
2. Updated feature file headers to match kebab-case filenames:
   - TubeInitializationTest.feature → tube-initialization-test.feature
   - ObserverTubeTest.feature → observer-tube-test.feature
   - TransformerTubeTest.feature → transformer-tube-test.feature
   - ValidatorTubeTest.feature → validator-tube-test.feature

## Documentation Standards

All test files now follow the standards defined in DOCUMENTATION_STANDARDS.md:

- Java test classes: {ComponentName}TestSuite.java
- Cucumber feature files: {functionality-area}-test.feature

Annotation classes like @AdaptationTest and @CompositeTest have been preserved to maintain tag-based test filtering.
