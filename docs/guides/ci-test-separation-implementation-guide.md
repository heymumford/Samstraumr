# CI Test Stage Separation - Implementation Guide

**Version:** 1.0.0
**Date:** 2025-11-16
**Status:** Ready for Implementation

---

## Overview

This guide provides step-by-step instructions for implementing the new CI test stage separation architecture. Follow these steps in order to ensure a smooth transition from the current monolithic pipeline to the optimized multi-stage approach.

**Expected Benefits:**
- 45% reduction in pipeline time (60min → 30-35min)
- Contract tests integrated into CI
- Faster failure feedback
- Lower CI costs
- Consistent containerized environment

---

## Prerequisites

Before starting, ensure you have:
- [ ] Docker installed locally (for testing containers)
- [ ] Access to GitHub Actions for the repository
- [ ] Admin/maintainer permissions to modify CI workflows
- [ ] Understanding of the current test structure (read design doc first)

---

## Phase 1: Container Setup

### Step 1.1: Build and Test Base Container

```bash
# Build the base test container
cd /path/to/Samstraumr
docker build -f Dockerfile.test-base -t test-base:latest .

# Verify the image was created
docker images | grep test-base

# Expected output: test-base:latest (~200MB)
```

### Step 1.2: Test Alpine Compatibility

```bash
# Run a quick compatibility test
docker run --rm -v $(pwd):/workspace test-base:latest mvn --version

# Expected output: Maven 3.9.x, Java 21 (Eclipse Temurin)

# Compile the project in Alpine container
docker run --rm -v $(pwd):/workspace test-base:latest \
  sh -c "cd /workspace && mvn clean compile"

# If compilation succeeds, Alpine is compatible! ✓
```

**Troubleshooting:**
- If you see musl libc errors, check the dependencies in `pom.xml`
- Common issues: native libraries, JNI dependencies
- Fallback: Replace `alpine` with `jammy` in Dockerfiles

### Step 1.3: Build Test-Specific Containers

```bash
# Build unit test container
docker build -f Dockerfile.test-unit -t test-unit:latest .

# Build contract test container
docker build -f Dockerfile.test-contract -t test-contract:latest .

# Build integration test container
docker build -f Dockerfile.test-integration -t test-integration:latest .

# Verify all images
docker images | grep "test-"
```

### Step 1.4: Test Local Container Execution

```bash
# Build docker-compose services
docker-compose -f docker-compose.test.yml build

# Run unit tests in container
docker-compose -f docker-compose.test.yml run --rm unit

# Verify tests run successfully
# Expected: All @L0_Unit tests pass

# Run contract tests in container
docker-compose -f docker-compose.test.yml run --rm contract

# Expected: All adapter contract tests pass
```

**Success Criteria:**
- [ ] All containers build successfully
- [ ] Unit tests pass in container
- [ ] Contract tests pass in container
- [ ] No compatibility issues with Alpine

---

## Phase 2: Contract Test Integration

### Step 2.1: Update s8r-test Script

Edit `bin/s8r-test` and add contract test support:

```bash
# Add to TEST_CONFIG map (around line 70)
[contract]="@Contract,org.s8r.adapter.contract.RunAdapterContractTests"
```

**File location:** `bin/s8r-test:70`

### Step 2.2: Add Contract Test Command

Add to the help text (around line 220):

```bash
echo "  contract               Run contract (adapter port) tests"
```

**File location:** `bin/s8r-test:220`

### Step 2.3: Test Contract Tests Locally

```bash
# Test the new contract test command
bin/s8r-test contract

# Expected output: Runs RunAdapterContractTests suite
# All 7+ contract tests should execute
```

### Step 2.4: Verify Contract Test Coverage

```bash
# Check which contract tests exist
find modules/samstraumr-core/src/test/java/org/s8r/adapter/contract \
  -name "*ContractTest.java"

# Expected output:
# ComponentPortContractTest.java
# ConfigurationPortContractTest.java
# DataFlowEventPortContractTest.java
# FileSystemPortContractTest.java
# MachinePortContractTest.java
# NotificationPortContractTest.java
# PortContractTest.java (base class)
```

**Success Criteria:**
- [ ] `bin/s8r-test contract` command works
- [ ] All contract tests execute
- [ ] All contract tests pass
- [ ] Test results show coverage

---

## Phase 3: Pipeline Migration

### Step 3.1: Create Feature Branch

```bash
git checkout -b feature/ci-test-stage-separation
```

### Step 3.2: Copy New Workflow

The new workflow is already at `.github/workflows/s8r-ci-v2.yml`.

Review it carefully:
```bash
cat .github/workflows/s8r-ci-v2.yml
```

### Step 3.3: Test Workflow on Feature Branch

```bash
# Commit the changes
git add .
git commit -m "Add containerized CI with test stage separation"

# Push to feature branch
git push -u origin feature/ci-test-stage-separation

# Create a pull request
# This will trigger the OLD workflow (s8r-ci.yml)
```

### Step 3.4: Enable New Workflow Temporarily

To test the new workflow on your PR:

1. Temporarily rename workflows:
   ```bash
   git mv .github/workflows/s8r-ci.yml .github/workflows/s8r-ci.yml.backup
   git mv .github/workflows/s8r-ci-v2.yml .github/workflows/s8r-ci.yml
   git commit -m "Test new CI workflow"
   git push
   ```

2. Monitor the pipeline run in GitHub Actions

3. Verify all stages execute correctly

### Step 3.5: Run Parallel Testing (1 Week)

After successful test run, revert the names to run BOTH workflows:

```bash
git mv .github/workflows/s8r-ci.yml .github/workflows/s8r-ci-v2.yml
git mv .github/workflows/s8r-ci.yml.backup .github/workflows/s8r-ci.yml
git commit -m "Run both pipelines in parallel for validation"
git push
```

**Monitor for 1 week:**
- Both pipelines should produce same results
- New pipeline should be ~45% faster
- All quality gates should function correctly

**Success Criteria:**
- [ ] New workflow triggers correctly
- [ ] All 6 stages execute in correct order
- [ ] Contract tests run and gate integration tests
- [ ] Pipeline completes in 30-35 minutes
- [ ] Results match old pipeline

---

## Phase 4: Optimization

### Step 4.1: Enable Composite Test Parallelization

Edit `modules/samstraumr-core/src/test/resources/cucumber.properties`:

```properties
# Change from sequential to parallel for composite tests
cucumber.execution.parallel.enabled=true
cucumber.execution.parallel.config.strategy=dynamic
cucumber.execution.parallel.config.dynamic.factor=0.75
```

**Note:** This is already enabled, but verify it applies to composite tests.

### Step 4.2: Optimize Maven Caching

The new workflow uses GitHub Actions cache. Monitor cache hit rates:

```yaml
# Check cache statistics in GitHub Actions logs
# Look for "Cache restored from key: maven-..."
```

### Step 4.3: Fine-Tune JVM Settings

Monitor memory usage in each stage and adjust if needed:

```yaml
# For memory-intensive stages, increase heap:
env:
  MAVEN_OPTS: '${{ env.MAVEN_OPTS }} -Xmx3g'
```

### Step 4.4: Add Pipeline Metrics

Add timing information to job summaries:

```yaml
- name: Record timing
  run: |
    echo "Stage completed in $SECONDS seconds" >> $GITHUB_STEP_SUMMARY
```

**Success Criteria:**
- [ ] Composite tests run in parallel
- [ ] Maven cache hit rate > 80%
- [ ] No OOM errors in any stage
- [ ] Pipeline consistently under 35 minutes

---

## Phase 5: Migration & Rollout

### Step 5.1: Final Validation

Before making the new pipeline default:

1. Review comparison metrics:
   - Pipeline success rates (should be identical)
   - Pipeline duration (should be ~45% faster)
   - Test coverage (should be equal or higher)
   - False positive rate (should be low)

2. Get team approval:
   - Share comparison report
   - Get sign-off from 2+ maintainers

### Step 5.2: Make New Pipeline Default

```bash
# Archive old pipeline
git mv .github/workflows/s8r-ci.yml .github/workflows/s8r-ci-v1-archived.yml

# Make new pipeline the default
git mv .github/workflows/s8r-ci-v2.yml .github/workflows/s8r-ci.yml

git commit -m "Migrate to optimized CI pipeline with test stage separation"
git push
```

### Step 5.3: Update Documentation

Update the following docs:
- [ ] `README.md` - Update CI badge if needed
- [ ] `docs/guides/getting-started.md` - Reference new pipeline
- [ ] `util/BUILD.md` - Update build instructions
- [ ] `docs/testing/testing-strategy.md` - Add contract test section

### Step 5.4: Team Training

Conduct a brief training session:
- How the new pipeline works
- How to run tests locally with Docker
- How to interpret new quality gates
- What to do if contract tests fail

### Step 5.5: Monitor Post-Migration

For 2 weeks after migration, monitor:
- Pipeline failure rates
- Developer feedback
- CI cost changes
- Time-to-merge metrics

**Success Criteria:**
- [ ] New pipeline is default
- [ ] Old pipeline archived
- [ ] Documentation updated
- [ ] Team trained
- [ ] No significant issues for 2 weeks

---

## Rollback Plan

If critical issues arise, follow this rollback procedure:

### Immediate Rollback (< 5 minutes)

```bash
# Restore old pipeline
git mv .github/workflows/s8r-ci.yml .github/workflows/s8r-ci-v2-broken.yml
git mv .github/workflows/s8r-ci-v1-archived.yml .github/workflows/s8r-ci.yml

git commit -m "ROLLBACK: Restore old CI pipeline due to [ISSUE]"
git push
```

### Document Rollback

1. Create incident report:
   - What failed
   - Why it failed
   - Impact assessment
   - Root cause
   - Fix timeline

2. Communicate to team:
   - Notify of rollback
   - Explain the issue
   - Provide timeline for fix

### Fix and Re-Deploy

1. Fix the issue on a feature branch
2. Test thoroughly
3. Re-run parallel validation
4. Re-attempt migration

---

## Common Issues and Solutions

### Issue: Alpine musl libc Incompatibility

**Symptoms:** Native library errors, JNI crashes

**Solution:**
```dockerfile
# Replace Alpine with Ubuntu-based image
FROM maven:3.9-eclipse-temurin-21-jammy
```

**Trade-off:** Image size increases from ~200MB to ~450MB

### Issue: Maven Dependency Download Slow

**Symptoms:** Long build times in CI

**Solution:**
```yaml
# Ensure cache is working
- uses: actions/cache@v4
  with:
    path: /root/.m2/repository
    key: maven-${{ hashFiles('**/pom.xml') }}
```

### Issue: Contract Tests Fail Unexpectedly

**Symptoms:** Tests pass locally, fail in CI

**Solution:**
1. Check for environment-specific issues
2. Verify test isolation (no shared state)
3. Run locally in Docker: `docker-compose run contract`

### Issue: Integration Tests Timeout

**Symptoms:** Integration stage exceeds time limit

**Solution:**
```yaml
# Increase timeout for integration stage
timeout-minutes: 30
```

### Issue: Quality Gate Too Strict

**Symptoms:** Legitimate PRs blocked

**Solution:**
```yaml
# Temporarily allow failures (with approval)
continue-on-error: true  # For specific job
```

**Note:** This should be temporary and require explicit approval.

---

## Verification Checklist

Before marking implementation complete, verify:

### Container Setup
- [ ] All Dockerfiles build successfully
- [ ] All containers run locally
- [ ] Docker Compose works correctly
- [ ] Alpine compatibility confirmed

### Test Execution
- [ ] Unit tests pass in container
- [ ] Contract tests execute and pass
- [ ] Integration tests pass in container
- [ ] All test types produce coverage reports

### CI Pipeline
- [ ] All 6 stages execute correctly
- [ ] Quality gates function properly
- [ ] Contract tests block integration on failure
- [ ] Parallel execution works (Stage 3a/3b)
- [ ] Pipeline completes in 30-35 minutes

### Documentation
- [ ] Design document reviewed
- [ ] Implementation guide followed
- [ ] README updated
- [ ] Testing docs updated
- [ ] Team trained

### Metrics
- [ ] Pipeline time reduced by ~45%
- [ ] Contract test coverage at 100%
- [ ] No increase in false positives
- [ ] CI costs reduced

---

## Support and Questions

If you encounter issues during implementation:

1. **Check the design document** for context: `docs/architecture/ci-test-stage-separation-design.md`
2. **Review common issues** section above
3. **Test locally first** using Docker Compose
4. **Check GitHub Actions logs** for specific errors
5. **Ask the team** in your development channel

---

## Next Steps After Implementation

Once implementation is complete and stable:

1. **Gather metrics** on improvements:
   - Pipeline duration reduction
   - Failure detection speed
   - CI cost savings
   - Developer satisfaction

2. **Identify further optimizations**:
   - Can more tests be parallelized?
   - Are there redundant quality checks?
   - Can we reduce test execution time?

3. **Share learnings**:
   - Write a blog post or case study
   - Present at team all-hands
   - Contribute back to community

---

## Conclusion

This implementation guide provides a structured approach to migrating from the current monolithic CI pipeline to an optimized, multi-stage architecture with proper test separation and containerization.

**Key Takeaways:**
- Follow phases in order
- Test thoroughly at each step
- Monitor metrics continuously
- Be prepared to rollback if needed
- Iterate and improve over time

**Estimated Implementation Time:** 3-4 weeks
**Expected ROI:** 45% faster pipelines, lower costs, better quality gates

Good luck with the implementation! 🚀
