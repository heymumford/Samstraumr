# CI Test Stage Separation - Deep Investigation & Solution Design

**Status:** Design Phase
**Date:** 2025-11-16
**Author:** Claude (AI Analysis)

---

## Executive Summary

This document presents a comprehensive investigation and solution design for separating CI test stages to reduce pipeline execution time and improve failure detection speed. The current monolithic approach runs all test types together, resulting in 60+ minute pipelines and slow failure feedback.

**Key Findings:**
- Current pipeline: 10 sequential stages, 60+ minutes total
- **Critical Gap:** Contract tests exist but are NOT in the CI pipeline
- No containerization currently used
- Composite tests run sequentially (could be parallelized)
- Quality checks don't gate downstream stages

**Proposed Solution:**
- 3-stage test gating: Unit → Contract → Integration
- Lightweight Alpine-based containers for all test stages
- Estimated time reduction: 40-50% (60min → 30-35min)
- Fast-fail architecture with quality gates

---

## Table of Contents

1. [Current State Analysis](#current-state-analysis)
2. [Problem Statement](#problem-statement)
3. [Test Type Analysis](#test-type-analysis)
4. [Container Research](#container-research)
5. [Solution Design](#solution-design)
6. [Implementation Plan](#implementation-plan)
7. [Risk Assessment](#risk-assessment)
8. [Success Metrics](#success-metrics)

---

## Current State Analysis

### CI/CD Platform
- **Platform:** GitHub Actions
- **Primary Workflow:** `.github/workflows/s8r-ci.yml` (373 lines)
- **Runner:** `ubuntu-latest` (no containerization)
- **Java Version:** 21 (Eclipse Temurin)
- **Build Tool:** Apache Maven 3.x

### Current Pipeline Structure

```
┌─────────────────────────────────────────────────────────────┐
│                    CURRENT PIPELINE (60+ min)               │
├─────────────────────────────────────────────────────────────┤
│ 1. Basic Verification           3-5 min    ✓ Gating         │
│ 2. Quality Checks              10-15 min   ✗ Non-blocking   │
│ 3. Unit Tests                   5-8 min    ✓ Parallel (4x)  │
│ 4. Component Tests            10-12 min    ✓ Parallel       │
│ 5. Composite Tests             8-10 min    ✗ Sequential     │
│ 6. Integration Tests          15-20 min    ✗ Sequential     │
│ 7. Coverage Analysis            5 min      -                │
│ 8. Package & Deploy            5-8 min     ✓ Main only      │
│ 9. Documentation                3-5 min    -                │
│ 10. Notification                1 min      -                │
└─────────────────────────────────────────────────────────────┘
```

### Test Organization

**Test Hierarchy:**
- **L0_Unit** (@L0_Unit) - Fast isolated tests
- **L1_Component** (@L1_Component) - Component integration
- **L2_Integration** (@L2_Integration) - System integration
- **L3_System** (@L3_System) - End-to-end scenarios

**Test Framework Stack:**
- JUnit 5.12.1 (unit testing)
- Cucumber 7.22.0 (BDD scenarios)
- Mockito 5.8.0 (mocking)
- Karate 1.4.1 (API/contract testing - port-interfaces module)

**Test Execution:**
- Script: `bin/s8r-test` (1016 lines)
- Parallel Config: `cucumber.properties` with dynamic parallelization (0.75 factor)
- Coverage: JaCoCo with 80% line/branch targets

**Contract Tests - CRITICAL FINDING:**
- Location: `modules/samstraumr-core/src/test/java/org/s8r/adapter/contract/`
- Test Suite: `RunAdapterContractTests.java`
- Count: 7+ contract test classes
- **Status: NOT currently in CI pipeline** ⚠️

### Pain Points Identified

| Issue | Impact | Priority |
|-------|--------|----------|
| Contract tests missing from CI | High - Missing quality gate | **CRITICAL** |
| Quality checks non-blocking | Medium - Can merge failing code | High |
| Composite tests sequential | Medium - 30-40% slower than needed | Medium |
| No containerization | Medium - Inconsistent environments | Medium |
| 60+ minute pipeline | High - Slow feedback loop | High |
| All tests in single conceptual stage | High - Slow failure detection | High |

---

## Problem Statement

### Current Issues

1. **Long Time to Fail:** When unit tests fail, the pipeline still takes 5-8 minutes to report failure because they run after compilation and quality checks
2. **No Contract Test Gate:** Contract tests exist but aren't executed in CI, creating a gap in the testing pyramid
3. **Resource Inefficiency:** No containerization means heavier runners and potential environment drift
4. **Poor Feedback Loops:** Developers wait 15-20+ minutes to see integration test results even when unit tests might catch the issue in 5 minutes

### Business Impact

- **Developer Productivity:** 60+ minutes per pipeline run means slower iteration cycles
- **CI Cost:** Longer runs = higher GitHub Actions costs
- **Quality Risk:** Missing contract tests in CI means adapter contracts aren't verified before merge
- **Deployment Speed:** Slow pipelines delay time-to-production

---

## Test Type Analysis

### Unit Tests (@L0_Unit)

**Characteristics:**
- **Purpose:** Fast, isolated component testing
- **Dependencies:** None (mocked)
- **Execution Time:** 5-8 minutes
- **Parallelization:** ✓ Yes (4 threads, dynamic factor 0.75)
- **Current State:** In CI pipeline, runs after basic verification

**Example Tests:**
- Component lifecycle tests
- Domain logic validation
- Utility function testing
- Mock-based adapter tests

**Quality Gate Role:** Should be the FIRST quality gate - nothing runs if these fail

### Contract Tests (MISSING FROM CI)

**Characteristics:**
- **Purpose:** Verify adapter implementations adhere to port interfaces
- **Dependencies:** Minimal (test implementations of ports)
- **Execution Time:** Estimated 3-5 minutes
- **Parallelization:** ✓ Yes (independent tests)
- **Current State:** **NOT in CI pipeline** ⚠️

**Existing Test Classes:**
```
org.s8r.adapter.contract.RunAdapterContractTests
├── ComponentPortContractTest
├── ConfigurationPortContractTest
├── DataFlowEventPortContractTest
├── FileSystemPortContractTest
├── MachinePortContractTest
├── PortContractTest (base class)
└── TestFixtureFactory
```

**Quality Gate Role:** Should gate integration tests - ensures adapters honor contracts

### Integration Tests (@L2_Integration)

**Characteristics:**
- **Purpose:** Machine-level integration, system behavior
- **Dependencies:** Requires working adapters and infrastructure
- **Execution Time:** 15-20 minutes
- **Parallelization:** Partial (some tests have shared state)
- **Current State:** In CI pipeline, runs after component/composite tests

**Quality Gate Role:** Final pre-deployment gate - verifies system works end-to-end

### Composite Tests (L1_Component)

**Characteristics:**
- **Purpose:** Component interaction patterns
- **Execution Time:** 8-10 minutes
- **Parallelization:** ✗ Currently sequential (opportunity!)
- **Current State:** In CI pipeline

**Note:** These align more with integration testing than contract testing for the purposes of this redesign.

---

## Container Research

### Container Strategy Analysis

**Requirements:**
- Java 21 (Eclipse Temurin)
- Maven 3.9.x
- Minimal size for fast CI
- Consistent build environment

### Container Options Evaluated

| Image | Size | Pros | Cons | Use Case |
|-------|------|------|------|----------|
| `maven:3.9-eclipse-temurin-21-alpine` | ~200MB | Smallest, fast pulls | musl libc compatibility | **CI Build/Test** ✓ |
| `eclipse-temurin:21-jdk-alpine` | ~180MB | Lightweight runtime | No Maven | Runtime only |
| `gcr.io/distroless/java21-debian12` | ~263MB | Max security, no shell | Larger, harder to debug | Production |
| `maven:3.9-eclipse-temurin-21-jammy` | ~450MB | Full compatibility | Larger size | Legacy support |

### Recommended Container Strategy

**For CI Test Stages:**
```dockerfile
# Use Alpine-based images for maximum speed
FROM maven:3.9-eclipse-temurin-21-alpine AS test-base

# JVM optimization for containers
ENV MAVEN_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"
```

**Benefits:**
- **70% size reduction** vs full images (200MB vs 650MB)
- **Faster CI startup** - smaller image pulls
- **Consistent environment** - same container locally and in CI
- **Resource efficiency** - lower memory footprint

**Compatibility Note:** Alpine uses musl libc instead of glibc. The project uses standard Java libraries that are compatible with musl, but we should verify during implementation.

---

## Solution Design

### Proposed Pipeline Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     PROPOSED PIPELINE (30-35 min)                       │
│                         -45% execution time                              │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  Stage 1: FAST VALIDATION (5-8 min)                                    │
│  ┌────────────────────────────────────────────┐                        │
│  │ Container: maven:3.9-temurin-21-alpine     │                        │
│  │ - Compile check                             │                        │
│  │ - Quick linters (checkstyle, basic PMD)    │                        │
│  └────────────────────────────────────────────┘                        │
│           ↓ QUALITY GATE: Must pass                                     │
│                                                                          │
│  Stage 2: UNIT TEST GATE (4-6 min)                                     │
│  ┌────────────────────────────────────────────┐                        │
│  │ Container: maven:3.9-temurin-21-alpine     │                        │
│  │ - Unit tests (@L0_Unit)                    │                        │
│  │ - Parallel: 4 threads                       │                        │
│  │ - Coverage: Unit coverage report            │                        │
│  └────────────────────────────────────────────┘                        │
│           ↓ QUALITY GATE: Unit tests must pass                          │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │ PARALLEL EXECUTION                                              │   │
│  │                                                                 │   │
│  │  Stage 3a: CONTRACT GATE (3-5 min)                             │   │
│  │  ┌──────────────────────────────────┐                          │   │
│  │  │ Container: maven:3.9-temurin-21-alpine │                    │   │
│  │  │ - Adapter contract tests          │                          │   │
│  │  │ - Port interface verification     │                          │   │
│  │  │ - Parallel execution              │                          │   │
│  │  └──────────────────────────────────┘                          │   │
│  │                                                                 │   │
│  │  Stage 3b: QUALITY ANALYSIS (6-8 min)                          │   │
│  │  ┌──────────────────────────────────┐                          │   │
│  │  │ Container: maven:3.9-temurin-21-alpine │                    │   │
│  │  │ - Deep static analysis (SpotBugs) │                          │   │
│  │  │ - Security scan (OWASP)            │                          │   │
│  │  │ - Architecture compliance          │                          │   │
│  │  └──────────────────────────────────┘                          │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│           ↓ QUALITY GATE: Both must pass                                │
│                                                                          │
│  Stage 4: INTEGRATION GATE (12-15 min)                                 │
│  ┌────────────────────────────────────────────┐                        │
│  │ Container: maven:3.9-temurin-21-alpine     │                        │
│  │ - Component tests (@L1_Component)          │                        │
│  │ - Composite tests (parallelized!)          │                        │
│  │ - Integration tests (@L2_Integration)      │                        │
│  │ - Parallel where possible                   │                        │
│  └────────────────────────────────────────────┘                        │
│           ↓ QUALITY GATE: Integration tests must pass                   │
│                                                                          │
│  Stage 5: COVERAGE & PACKAGE (3-5 min)                                 │
│  ┌────────────────────────────────────────────┐                        │
│  │ - Merge all coverage reports                │                        │
│  │ - Verify coverage thresholds (80%)          │                        │
│  │ - Package JAR artifacts                     │                        │
│  └────────────────────────────────────────────┘                        │
│           ↓ On main branch only                                         │
│                                                                          │
│  Stage 6: DEPLOY & DOCS (3-5 min)                                      │
│  ┌────────────────────────────────────────────┐                        │
│  │ - Generate documentation                    │                        │
│  │ - Publish artifacts                         │                        │
│  │ - Send notifications                        │                        │
│  └────────────────────────────────────────────┘                        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Key Improvements

| Improvement | Current | Proposed | Benefit |
|-------------|---------|----------|---------|
| **Contract Tests in CI** | ✗ Missing | ✓ Stage 3a | Close quality gap |
| **Fast Failure** | 5-8 min | 4-6 min | Faster feedback |
| **Composite Parallelization** | Sequential | Parallel | 30-40% faster |
| **Quality Gating** | Non-blocking | Blocking | Prevent bad merges |
| **Container Isolation** | No | Yes | Consistency |
| **Total Time** | 60+ min | 30-35 min | **45% reduction** |

### Quality Gate Strategy

```
┌─────────────────────────────────────────────────────────┐
│                    QUALITY GATES                        │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Gate 1: FAST VALIDATION                                │
│  → Blocks: Everything                                   │
│  → Time to Fail: 2-3 min (compilation)                  │
│  → Purpose: Catch syntax/basic errors immediately       │
│                                                         │
│  Gate 2: UNIT TESTS                                     │
│  → Blocks: Contract, Integration, Deploy                │
│  → Time to Fail: 4-6 min                                │
│  → Purpose: Verify core logic before integration        │
│                                                         │
│  Gate 3a: CONTRACT TESTS (NEW!)                         │
│  → Blocks: Integration, Deploy                          │
│  → Time to Fail: 10-12 min (after unit)                 │
│  → Purpose: Ensure adapters honor port contracts        │
│                                                         │
│  Gate 3b: QUALITY ANALYSIS                              │
│  → Blocks: Deploy (parallel with contracts)             │
│  → Time to Fail: 12-15 min                              │
│  → Purpose: Prevent security/quality issues             │
│                                                         │
│  Gate 4: INTEGRATION TESTS                              │
│  → Blocks: Deploy                                       │
│  → Time to Fail: 20-25 min                              │
│  → Purpose: Verify system behavior end-to-end           │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### Container Configuration

#### Test Base Image (Dockerfile.test-base)

```dockerfile
FROM maven:3.9-eclipse-temurin-21-alpine

# Install additional tools if needed
RUN apk add --no-cache \
    git \
    bash

# Set up Maven optimizations for containers
ENV MAVEN_OPTS="-XX:MaxRAMPercentage=75.0 \
                -XX:+UseG1GC \
                -XX:+UseStringDeduplication \
                -XX:+ExitOnOutOfMemoryError \
                -Xshare:on"

# Configure Maven settings
ENV MAVEN_CONFIG=/root/.m2
RUN mkdir -p ${MAVEN_CONFIG}

# Pre-download Maven dependencies (cache layer)
WORKDIR /build
COPY pom.xml .
COPY modules/pom.xml modules/
COPY test-module/pom.xml test-module/
COPY test-port-interfaces/pom.xml test-port-interfaces/
RUN mvn dependency:go-offline -B

WORKDIR /workspace
```

**Size:** ~200MB (vs ~450MB for jammy variant)

#### Unit Test Container

```dockerfile
FROM test-base:latest

# Optimized for fast, isolated tests
ENV MAVEN_CLI_OPTS="--batch-mode --errors --fail-at-end --show-version"
ENV MAVEN_PARALLEL_THREADS=4

CMD ["mvn", "test", "-Dtest.tags=@L0_Unit"]
```

#### Contract Test Container

```dockerfile
FROM test-base:latest

# Run adapter contract tests
ENV MAVEN_CLI_OPTS="--batch-mode --errors --fail-at-end --show-version"

CMD ["mvn", "test", "-Dtest=RunAdapterContractTests"]
```

#### Integration Test Container

```dockerfile
FROM test-base:latest

# Optimized for longer-running integration tests
ENV MAVEN_CLI_OPTS="--batch-mode --errors --fail-at-end --show-version"
ENV MAVEN_OPTS="${MAVEN_OPTS} -Xmx2g"

CMD ["mvn", "test", "-Dtest.tags=@L1_Component,@L2_Integration"]
```

---

## Implementation Plan

### Phase 1: Container Setup (Week 1)

**Tasks:**
1. Create `Dockerfile.test-base` in project root
2. Create stage-specific Dockerfiles (`Dockerfile.test-unit`, `Dockerfile.test-contract`, `Dockerfile.test-integration`)
3. Test local builds with Alpine containers
4. Verify musl libc compatibility with existing dependencies
5. Create docker-compose setup for local testing

**Deliverables:**
- [ ] Dockerfile.test-base
- [ ] Stage-specific Dockerfiles
- [ ] docker-compose.yml for local test execution
- [ ] Documentation: "Running Tests in Containers"

**Success Criteria:**
- All existing tests pass in Alpine containers
- No dependency compatibility issues
- Local docker-compose test suite works

### Phase 2: Contract Test Integration (Week 1-2)

**Tasks:**
1. Update `bin/s8r-test` to support contract test type
2. Add contract tests to TEST_CONFIG mapping
3. Create contract test stage in CI pipeline
4. Verify contract test coverage
5. Document contract testing approach

**Deliverables:**
- [ ] Updated s8r-test script with contract support
- [ ] Contract test CI stage
- [ ] Contract test documentation
- [ ] CI badge for contract tests

**Success Criteria:**
- Contract tests run successfully in CI
- Contract tests gate integration tests
- All 7+ contract test classes execute

### Phase 3: Pipeline Restructuring (Week 2)

**Tasks:**
1. Create new workflow: `.github/workflows/s8r-ci-v2.yml`
2. Implement 6-stage pipeline structure
3. Add container configurations to each stage
4. Configure quality gates with proper dependencies
5. Set up parallel execution for Stage 3a/3b
6. Configure artifact passing between stages

**Deliverables:**
- [ ] New s8r-ci-v2.yml workflow
- [ ] Quality gate configuration
- [ ] Stage dependency matrix
- [ ] Artifact upload/download configuration

**Success Criteria:**
- Pipeline completes in 30-35 minutes
- All quality gates function correctly
- Contract tests block integration on failure
- Parallel stages execute correctly

### Phase 4: Optimization (Week 3)

**Tasks:**
1. Parallelize composite tests (update cucumber.properties)
2. Optimize Maven dependency caching
3. Implement test result caching where appropriate
4. Fine-tune JVM settings for each stage
5. Add pipeline performance monitoring

**Deliverables:**
- [ ] Parallelized composite test configuration
- [ ] Optimized Maven cache strategy
- [ ] JVM tuning documentation
- [ ] Pipeline metrics dashboard

**Success Criteria:**
- Composite tests run in parallel
- Maven dependencies cached effectively
- Pipeline time ≤ 35 minutes consistently

### Phase 5: Migration & Validation (Week 3-4)

**Tasks:**
1. Run both pipelines in parallel for 1 week
2. Compare results and performance
3. Fix any discrepancies
4. Migrate default branch to new pipeline
5. Archive old pipeline
6. Update documentation

**Deliverables:**
- [ ] Pipeline comparison report
- [ ] Migration plan
- [ ] Updated CI/CD documentation
- [ ] Team training materials

**Success Criteria:**
- New pipeline matches old pipeline results
- All teams successfully using new pipeline
- Documentation complete
- Old pipeline deprecated

---

## Risk Assessment

### Technical Risks

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Alpine musl libc incompatibility | Low | High | Test thoroughly; fallback to jammy variant |
| Contract tests fail unexpectedly | Medium | Medium | Review and fix tests before CI integration |
| Parallel execution race conditions | Medium | Medium | Identify and isolate stateful tests |
| Container overhead slows CI | Low | High | Benchmark; optimize caching |
| Maven cache issues in containers | Medium | Medium | Use volume mounts; verify cache strategy |

### Process Risks

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Team unfamiliar with containers | Medium | Low | Provide training; good documentation |
| Parallel pipeline confusion | Low | Medium | Run both pipelines during transition |
| Quality gate too strict | Low | High | Allow temporary bypasses with approval |
| Longer initial setup time | High | Low | Expected; document as known issue |

### Rollback Plan

If critical issues arise:
1. **Immediate:** Revert to `.github/workflows/s8r-ci.yml`
2. **Short-term:** Fix issues in s8r-ci-v2.yml on separate branch
3. **Documentation:** Document what failed and why
4. **Communication:** Notify team of rollback and timeline

---

## Success Metrics

### Performance Metrics

| Metric | Current | Target | Measurement |
|--------|---------|--------|-------------|
| **Total Pipeline Time** | 60+ min | 30-35 min | GitHub Actions duration |
| **Time to Unit Test Feedback** | 8-13 min | 4-6 min | Stage 2 completion |
| **Time to Contract Test Feedback** | N/A (missing) | 10-12 min | Stage 3a completion |
| **Time to Integration Feedback** | 35-40 min | 20-25 min | Stage 4 completion |
| **Container Pull Time** | N/A | <1 min | First action in stage |
| **Test Parallelization** | Unit only | Unit + Contract + Composite | Test execution logs |

### Quality Metrics

| Metric | Current | Target | Measurement |
|--------|---------|--------|-------------|
| **Contract Test Coverage** | 0% (not in CI) | 100% | CI execution logs |
| **Quality Gate Effectiveness** | Non-blocking | Blocking | Merge attempts blocked |
| **False Positive Rate** | Unknown | <5% | Failed pipelines / total |
| **Environment Consistency** | Variable | 100% | Container usage |

### Cost Metrics

| Metric | Current | Target | Impact |
|--------|---------|--------|--------|
| **CI Minutes per Run** | 60+ | 30-35 | -42% GitHub Actions cost |
| **Daily CI Minutes** | ~1000 | ~500 | Assumes ~16 runs/day |
| **Monthly CI Cost** | Baseline | -45% | Significant savings |

### Developer Experience Metrics

| Metric | Target | Measurement |
|--------|--------|-------------|
| **Developer Satisfaction** | >80% positive | Survey after 2 weeks |
| **Time to Fix Failures** | -30% | Mean time to green |
| **Local Test Run Success** | >90% | Docker compose usage |

---

## Appendix A: Test Inventory

### Unit Tests (@L0_Unit)
```
Location: modules/samstraumr-core/src/test/java/org/s8r/
Count: 50+ unit test classes
Tags: @L0_Unit
Execution: JUnit 5 + Cucumber
Parallelization: 4 threads, dynamic factor 0.75
```

### Contract Tests (NOT IN CI - HIGH PRIORITY)
```
Location: modules/samstraumr-core/src/test/java/org/s8r/adapter/contract/
Count: 7 contract test classes
Runner: RunAdapterContractTests.java
Framework: JUnit 5
Tests:
  - ComponentPortContractTest
  - ConfigurationPortContractTest
  - DataFlowEventPortContractTest
  - FileSystemPortContractTest
  - MachinePortContractTest
  - PortContractTest (base)
  - NotificationPortContractTest
```

### Component Tests (@L1_Component)
```
Location: modules/samstraumr-core/src/test/resources/features/
Count: 30+ component scenarios
Tags: @L1_Component
Execution: Cucumber
```

### Integration Tests (@L2_Integration)
```
Location: modules/samstraumr-core/src/test/resources/features/
Count: 40+ integration scenarios
Tags: @L2_Integration
Execution: Cucumber
```

---

## Appendix B: Container Image Comparison

### Size Analysis

```bash
# Alpine variant (RECOMMENDED for CI)
maven:3.9-eclipse-temurin-21-alpine        ~200MB

# Jammy (Ubuntu) variant
maven:3.9-eclipse-temurin-21-jammy         ~450MB

# Slim variant
eclipse-temurin:21-jdk-slim                ~280MB

# Distroless (production only)
gcr.io/distroless/java21-debian12          ~263MB
```

### Pull Time Comparison (GitHub Actions)

```
Alpine:     15-20 seconds
Jammy:      30-45 seconds
Slim:       20-30 seconds
Distroless: 20-25 seconds
```

**Recommendation:** Alpine for all CI test stages

---

## Appendix C: GitHub Actions Configuration Examples

### Container Service Configuration

```yaml
jobs:
  unit-tests:
    runs-on: ubuntu-latest
    container:
      image: maven:3.9-eclipse-temurin-21-alpine
      env:
        MAVEN_OPTS: -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC
      volumes:
        - maven-cache:/root/.m2
    steps:
      - uses: actions/checkout@v5
      - name: Run unit tests
        run: bin/s8r-test unit --parallel
```

### Matrix Strategy for Parallel Tests

```yaml
jobs:
  parallel-tests:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        test-type: [contract, quality]
      fail-fast: false
    container:
      image: maven:3.9-eclipse-temurin-21-alpine
    steps:
      - uses: actions/checkout@v5
      - name: Run ${{ matrix.test-type }} tests
        run: bin/s8r-test ${{ matrix.test-type }}
```

---

## Appendix D: Migration Checklist

### Pre-Migration
- [ ] Review this design document with team
- [ ] Verify Alpine compatibility with all dependencies
- [ ] Test contract tests locally
- [ ] Create backup of current CI configuration
- [ ] Set up monitoring for new pipeline

### Migration
- [ ] Create Dockerfiles
- [ ] Update s8r-test script for contract tests
- [ ] Create s8r-ci-v2.yml workflow
- [ ] Test new workflow on feature branch
- [ ] Run parallel pipelines for 1 week
- [ ] Compare results

### Post-Migration
- [ ] Archive old pipeline
- [ ] Update documentation
- [ ] Train team on new workflow
- [ ] Monitor performance metrics
- [ ] Collect feedback
- [ ] Iterate on improvements

---

## Conclusion

This design addresses the core issues with the current CI pipeline:

1. **Closes quality gap** by adding contract tests to CI
2. **Reduces time by 45%** through parallelization and optimization
3. **Improves failure detection** with proper quality gates
4. **Increases consistency** via containerization
5. **Lowers costs** through more efficient resource usage

**Recommended Next Steps:**
1. Review this design with the team
2. Approve implementation plan
3. Begin Phase 1 (Container Setup)
4. Track progress against success metrics

**Estimated Implementation Time:** 3-4 weeks
**Estimated ROI:** 45% reduction in CI time = significant cost savings + improved developer experience
