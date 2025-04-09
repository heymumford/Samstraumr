<\!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

Licensed under the Mozilla Public License 2.0
-->

# Tube Lifecycle Tests

This directory contains Cucumber feature files for testing the tube lifecycle functionality.

## Overview

The tube lifecycle tests verify the behavior of tubes as they progress through their lifecycle phases:

- Pre-conception
- Conception
- Embryonic
- Childhood
- Infancy
- Maturity

## Feature Files

- `adam-tube-tests.feature` - Tests for the Adam tube implementation
- `atomic-tube-tests.feature` - Tests for atomic tube behavior
- `childhood-phase-tests.feature` - Tests for the childhood lifecycle phase
- `conception-phase-tests.feature` - Tests for the conception lifecycle phase
- `creation-lifecycle-tests.feature` - Tests for the entire creation lifecycle
- `early-conception-phase-tests.feature` - Tests for early conception state
- `embryonic-phase-tests.feature` - Tests for the embryonic lifecycle phase
- `infancy-phase-tests.feature` - Tests for the infancy lifecycle phase
- `pre-conception-phase-tests.feature` - Tests for pre-conception state
- `tube-initialization-tests.feature` - Tests for tube initialization process
- `tube-survival-strategies.feature` - Tests for tube survival strategies (immortality vs. reproduction)

## Survival Strategies

The tube survival strategies tests implement the conceptual model where tubes have two fundamental survival approaches: immortality and reproduction. The choice between these strategies is determined by environmental conditions:

- In harsh or unfavorable environments, tubes choose immortality with self-sufficiency and self-management
- In favorable environments, tubes choose reproduction, transferring essential knowledge to offspring before terminating

These tests employ comprehensive pairwise analysis to verify tube behavior across different environmental conditions and strategy combinations.

## Related Directories

- `/features/identity` - Tests for tube identity functionality
- `/features/composite-patterns` - Tests for tube pattern implementations
