# Samstraumr Validation TODOs

This document tracks validation issues identified in the acceptance tests. 
Each issue has a corresponding acceptance test that will pass when it's fixed.

## Component/Tube Validation

- [ ] **Issue**: Component duplicate detection
  - **Test**: `run_acceptance_component_duplicate_tests` in s8r-acceptance-tests.sh
  - **Description**: The system should prevent creating components with duplicate names and return a non-zero exit code with an error message.

- [ ] **Issue**: Non-existent component reference validation
  - **Test**: `run_acceptance_nonexistent_component_tests` in s8r-acceptance-tests.sh
  - **Description**: The system should detect and error when referencing non-existent components.

- [ ] **Issue**: Component name validation
  - **Test**: `run_acceptance_component_name_validation` in s8r-acceptance-tests.sh
  - **Description**: The system should validate component names for illegal characters and length.

- [ ] **Issue**: Component type validation
  - **Test**: `run_acceptance_component_type_validation` in s8r-acceptance-tests.sh
  - **Description**: The system should validate that component types are known/allowed.

## Composite Validation

- [ ] **Issue**: Composite connection validation
  - **Test**: `run_acceptance_composite_connection_validation` in s8r-acceptance-tests.sh
  - **Description**: The system should prevent connecting components that don't exist in the composite.

- [ ] **Issue**: Connection cycle detection
  - **Test**: `run_acceptance_connection_cycle_detection` in s8r-acceptance-tests.sh
  - **Description**: The system should prevent creating cycles in component connections.

## Machine Validation

- [ ] **Issue**: Machine state validation
  - **Test**: `run_acceptance_machine_state_validation` in s8r-acceptance-tests.sh
  - **Description**: The system should prevent invalid state transitions (e.g., stopping a machine that isn't running).

- [ ] **Issue**: Machine component validation
  - **Test**: `run_acceptance_machine_component_validation` in s8r-acceptance-tests.sh
  - **Description**: The system should prevent adding non-existent composites to machines.

## Cross-Entity Validation

- [ ] **Issue**: Cross-entity name uniqueness
  - **Test**: `run_acceptance_cross_entity_uniqueness` in s8r-acceptance-tests.sh
  - **Description**: Names should be unique across different entity types (tubes, components, composites, machines).

## Error Handling

- [ ] **Issue**: Component/Machine resilience
  - **Test**: `run_acceptance_component_resilience` in s8r-acceptance-tests.sh
  - **Description**: The system should have documented resilience capabilities for recovering from component failures.

## Completion Checklist

When all of these issues are fixed, the following command should show all acceptance tests passing:

```bash
./s8r-run-tests.sh --acceptance
```

This would indicate that the validation system is complete and working as expected.