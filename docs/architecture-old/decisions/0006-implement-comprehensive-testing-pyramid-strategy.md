# 0006 Implement Comprehensive Testing Pyramid Strategy

Date: 2025-04-06

## Status

Accepted

## Context

Samstraumr has a unique domain model focused on tube-based component systems, data flows, and hierarchical composition. Our testing approach needs to balance:

1. **Industry Alignment**: Using recognized testing terminology for broad understanding
2. **Domain Specificity**: Respecting our unique domain concepts
3. **Test Coverage**: Ensuring all aspects of the system are verified
4. **Developer Experience**: Making testing accessible and intuitive
5. **Build Performance**: Optimizing test execution for CI/CD pipelines

We previously lacked a structured approach to testing, resulting in:
- Inconsistent testing practices across teams and components
- Insufficient coverage of integration scenarios
- Slow test execution in the build pipeline
- Confusion about what should be tested at each level
- Lack of clarity on critical vs. non-critical tests

## Decision

We will implement a comprehensive testing pyramid strategy that aligns Samstraumr's domain-specific terminology with industry standards, organized in a layered approach:

### Test pyramid structure

```
           /\
          /  \
         / UI \        L4: Acceptance Tests (End-to-End)
        /------\
       /  API   \      L3: Stream Tests (System)
      /----------\
     / Integration \    L2: Machine Tests (API/Contract)
    /--------------\
   /   Component    \   L1: Composite Tests (Component)
  /------------------\
 /       Unit         \  L0: Tube Tests (Unit)
/----------------------\
      Foundation         Orchestration Tests (Smoke)
```

### Terminology mapping

We will establish clear mapping between industry-standard terms and Samstraumr terms:

| Industry Standard | Samstraumr Term    | Description                                      |
|-------------------|--------------------|-------------------------------------------------|
| Smoke Test        | Orchestration Test | Verifies the basic system assembly               |
| Unit Test         | Tube Test          | Tests individual tubes in isolation              |
| Component Test    | Composite Test     | Tests connected tubes working together           |
| Integration Test  | Flow Test          | Tests integration between system parts           |
| API/Contract Test | Machine Test       | Tests public interfaces and contracts            |
| System Test       | Stream Test        | Tests the entire system as a whole               |
| End-to-End Test   | Acceptance Test    | Tests from the user's perspective                |

### Critical vs. non-critical tests

We will categorize tests as:

- **Above The Line (ATL)**: Critical tests that must pass for the build to be considered valid
- **Below The Line (BTL)**: Non-critical tests that provide additional confidence but are not blocking

### Test organization

Tests will be organized by level (L0-L4) with clear directory structure and naming conventions:

```
test/
├── L0_Tube/            # Unit-level tests
├── L1_Composite/       # Component-level tests
├── L2_Machine/         # API/contract tests
├── L3_Stream/          # System tests
└── L4_Acceptance/      # End-to-end tests
```

## Consequences

### Positive

1. **Common Vocabulary**: Mapping industry and domain terms creates a common language
2. **Comprehensive Coverage**: Clear structure ensures all system aspects are tested
3. **Build Optimization**: ATL/BTL distinction allows for faster critical path builds
4. **Improved Maintainability**: Organized test structure makes maintenance easier
5. **Clear Expectations**: Developers understand what to test at each level
6. **Training Enhancement**: Easier to train new team members on testing practices

### Challenges and mitigations

1. **Challenge**: Migrating existing tests to the new structure
   - **Mitigation**: Incremental migration, starting with new tests and gradually refactoring existing ones

2. **Challenge**: Learning curve for the dual terminology
   - **Mitigation**: Clear documentation, IDE templates, and regular knowledge sharing sessions

3. **Challenge**: Risk of over-testing or duplicating test coverage
   - **Mitigation**: Test coverage analysis and review in pull requests

4. **Challenge**: Maintaining fast build times with comprehensive testing
   - **Mitigation**: Parallel test execution, ATL/BTL separation, and test optimization techniques

