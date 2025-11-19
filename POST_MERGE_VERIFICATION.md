# Post-Merge Verification Checklist

**Execute these verification steps AFTER the PR merges to main**

## Phase 1: Update Local Repository

```bash
# Switch to main and pull latest
git checkout main
git pull origin main

# Verify merge commit exists
git log --oneline -5
# Should show: "Merge branch consolidation work: CI/CD modernization..."
```

## Phase 2: Build Verification

### Quick Compile Test
```bash
mvn clean compile -DskipTests
```
**Expected:** ✅ BUILD SUCCESS

### Full Build with Tests
```bash
mvn clean verify
```
**Expected:** ✅ BUILD SUCCESS with all tests passing

### Architecture Compliance Tests
```bash
mvn test -Dtest="*Architecture*"
```
**Expected:** ✅ All architecture tests pass

## Phase 3: Verify New Files

Check that all consolidated files are present:

```bash
# Refactoring documentation
ls -lh REFACTORING_ASSESSMENT.md
# Expected: ~47KB file

# Consolidation summary
ls -lh BRANCH_CONSOLIDATION_SUMMARY.md
# Expected: ~7KB file

# CI/CD documentation
ls -lh docs/CI_CD_STRATEGY.md
# Expected: ~15KB file

# Local quality script
ls -lh scripts/local-quality-check.sh
# Expected: executable script ~7KB

# Pre-commit config
ls -lh .pre-commit-config.yaml
# Expected: ~3KB file
```

## Phase 4: Verify Refactored Code

Check that refactored components exist:

```bash
# New lifecycle manager
ls -lh modules/samstraumr-core/src/main/java/org/s8r/component/lifecycle/ComponentLifecycleManager.java

# New logger
ls -lh modules/samstraumr-core/src/main/java/org/s8r/component/logging/ComponentLogger.java

# New termination manager
ls -lh modules/samstraumr-core/src/main/java/org/s8r/component/termination/ComponentTerminationManager.java

# New machine operation
ls -lh modules/samstraumr-core/src/main/java/org/s8r/domain/machine/MachineOperation.java
```

## Phase 5: Verify Maven Dependencies

Check that dependency updates are applied:

```bash
# Check exec-maven-plugin version
grep "maven.exec.plugin.version" pom.xml
# Expected: <maven.exec.plugin.version>3.5.0</maven.exec.plugin.version>

# Check maven-site-plugin version
grep "maven.site.plugin.version" pom.xml
# Expected: <maven.site.plugin.version>4.0.0-M16</maven.site.plugin.version>
```

## Phase 6: Verify CI Workflows

Check that new workflows are in place:

```bash
# New main CI workflow
ls -lh .github/workflows/ci-main.yml
# Expected: ~15KB file

# Verify old workflows removed
ls .github/workflows/build.yml 2>/dev/null
# Expected: "No such file or directory"

ls .github/workflows/ci-documentation.yml 2>/dev/null
# Expected: "No such file or directory"

ls .github/workflows/codeql.yml 2>/dev/null
# Expected: "No such file or directory"
```

## Phase 7: Test Local Quality Checks

```bash
# Run the new local quality check script
./scripts/local-quality-check.sh --help

# Expected: Shows usage information
```

## Phase 8: Test Pre-Commit Hooks (Optional)

```bash
# Install pre-commit (if not already installed)
pip install pre-commit

# Install hooks
pre-commit install

# Test hooks
pre-commit run --all-files
```

## Verification Summary Template

Copy this and fill in the results:

```
## Verification Results - [DATE]

### Build Status
- [ ] Clean compile: SUCCESS / FAILED
- [ ] Full build with tests: SUCCESS / FAILED
- [ ] Architecture tests: SUCCESS / FAILED

### File Verification
- [ ] REFACTORING_ASSESSMENT.md: PRESENT / MISSING
- [ ] BRANCH_CONSOLIDATION_SUMMARY.md: PRESENT / MISSING
- [ ] docs/CI_CD_STRATEGY.md: PRESENT / MISSING
- [ ] scripts/local-quality-check.sh: PRESENT / MISSING
- [ ] .pre-commit-config.yaml: PRESENT / MISSING

### Refactored Components
- [ ] ComponentLifecycleManager.java: PRESENT / MISSING
- [ ] ComponentLogger.java: PRESENT / MISSING
- [ ] ComponentTerminationManager.java: PRESENT / MISSING
- [ ] MachineOperation.java: PRESENT / MISSING

### Maven Dependencies
- [ ] maven-exec-plugin: 3.5.0 ✓ / OTHER
- [ ] maven-site-plugin: 4.0.0-M16 ✓ / OTHER

### CI Workflows
- [ ] ci-main.yml: PRESENT / MISSING
- [ ] Old workflows removed: YES / NO

### Overall Status
- [ ] ALL CHECKS PASSED - Ready for cleanup
- [ ] SOME ISSUES - Need investigation

### Notes:
[Add any observations or issues here]
```

## If Issues Are Found

### Build Failures
1. Check Maven error messages
2. Verify Java version: `java -version` (should be 21)
3. Clear Maven cache: `rm -rf ~/.m2/repository/org/s8r`
4. Retry build

### Missing Files
1. Verify you're on main: `git branch --show-current`
2. Check merge commit: `git log --oneline -10`
3. If files missing, the PR may not have merged correctly

### Test Failures
1. Review test output
2. Check if pre-existing or introduced by changes
3. Run individual failing tests for more detail

## Success Criteria

✅ **All verification steps pass** → Proceed to branch cleanup (see `POST_MERGE_CLEANUP.md`)

⚠️ **Some steps fail** → Investigate before cleanup

❌ **Critical failures** → Contact repository maintainer

---

**Estimated Time:** 15-30 minutes
**Required Tools:** Git, Maven, Java 21
**Optional Tools:** pre-commit
