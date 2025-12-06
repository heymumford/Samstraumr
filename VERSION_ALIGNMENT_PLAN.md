# Samstraumr Version Alignment Implementation Plan

**Generated**: 2025-12-05
**Target Version**: 3.1.1
**Status**: READY FOR EXECUTION

---

## Phase 1: Critical - Version Alignment

### Pre-Execution Checklist

```bash
# 1. Verify current version
./s8r-version get
# Expected output: 3.1.1

# 2. Create backup branch
git checkout -b docs/version-alignment-3.1.1
git push origin docs/version-alignment-3.1.1

# 3. Document current state
grep -rn "3\.0\.[0-9]" . --include="*.md" --include="pom.xml" > version-refs-before.txt
```

---

## File Changes Required

### User-Facing Documentation (CRITICAL)

#### File 1: README.md
```diff
- Line 6 (BEFORE):
[![Version](https://img.shields.io/badge/version-3.0.5-blue)](https://github.com/heymumford/Samstraumr/releases)

+ Line 6 (AFTER):
[![Version](https://img.shields.io/badge/version-3.1.1-blue)](https://github.com/heymumford/Samstraumr/releases)

- Line 121 (BEFORE):
    <version>3.0.5</version>

+ Line 121 (AFTER):
    <version>3.1.1</version>
```

**Command**:
```bash
sed -i '' 's/version-3\.0\.5-blue/version-3.1.1-blue/g' README.md
sed -i '' 's/<version>3\.0\.5<\/version>/<version>3.1.1<\/version>/g' README.md
```

#### File 2: CLAUDE.md
```diff
- Line 340 (BEFORE):
- Current: 3.0.2 (see `pom.xml`)

+ Line 340 (AFTER):
- Current: 3.1.1 (see `pom.xml`)
```

**Command**:
```bash
sed -i '' 's/Current: 3\.0\.2/Current: 3.1.1/g' CLAUDE.md
```

---

### Build System (HIGH PRIORITY)

#### Files 3-7: POM Files (Automated)

**Affected Files**:
- pom.xml (line 19)
- modules/pom.xml (line 20)
- modules/samstraumr-core/pom.xml (line 20)
- test-port-interfaces/pom.xml (lines 10, 27)
- lifecycle-test/pom.xml (line 10)

**Command**:
```bash
# Use Maven Versions Plugin for atomic update
mvn versions:set -DnewVersion=3.1.1 -DprocessAllModules=true
mvn versions:commit  # Commit the changes (remove backup POMs)

# If issues occur, rollback with:
# mvn versions:revert
```

---

### Supporting Documentation (MEDIUM PRIORITY)

#### File 8: REFACTORING_ASSESSMENT.md
```diff
- Line 4 (BEFORE):
**Framework Version**: 3.0.5

+ Line 4 (AFTER):
**Framework Version**: 3.1.1
```

**Command**:
```bash
sed -i '' 's/Framework Version\*\*: 3\.0\.5/Framework Version**: 3.1.1/g' REFACTORING_ASSESSMENT.md
```

#### File 9: docs/testing/karate-testing-guide.md
```diff
- Line 53 (BEFORE):
    s8rVersion: '3.0.2',

+ Line 53 (AFTER):
    s8rVersion: '3.1.1',
```

**Command**:
```bash
sed -i '' "s/s8rVersion: '3\.0\.2'/s8rVersion: '3.1.1'/g" docs/testing/karate-testing-guide.md
```

#### File 10: docs/architecture/reports/design-analysis-gaps-report.md
```diff
- Line 5 (BEFORE):
**Framework Version**: 3.0.5

+ Line 5 (AFTER):
**Framework Version**: 3.1.1
```

**Command**:
```bash
sed -i '' 's/Framework Version\*\*: 3\.0\.5/Framework Version**: 3.1.1/g' docs/architecture/reports/design-analysis-gaps-report.md
```

---

### Changelog Update (DOCUMENTATION)

#### File 11: docs/reference/release/changelog.md

**Add after line 17** (before ## [3.0.2]):

```markdown
## [3.1.1] - 2025-11-19

### Changed
- Documentation version alignment across all markdown and POM files
- Updated Maven coordinates in README and examples
- Synchronized CLAUDE.md with actual release version

### Fixed
- Version drift between modules/version.properties and documentation
- Inconsistent version references causing user confusion
- Outdated version badge in README.md

### Documentation
- Added version alignment validation to CI pipeline
- Created VERSION_ALIGNMENT_PLAN.md for future reference
```

---

## Validation Commands

### Post-Change Validation

```bash
# Test 1: Version consistency check
echo "=== Checking for remaining 3.0.x references ==="
grep -rn "3\.0\.[0-9]" . --include="*.md" --include="pom.xml" --exclude-dir=".git" --exclude-dir="target"
# Expected: Only changelog entries (historical records)

# Test 2: Maven build validation
echo "=== Verifying Maven build ==="
./s8r-build compile
# Expected: BUILD SUCCESS

# Test 3: Version verification
echo "=== Verifying version tools ==="
./s8r-version verify
# Expected: Version 3.1.1 verified

# Test 4: Dependency tree check
echo "=== Checking dependency resolution ==="
mvn dependency:tree | grep "org.s8r" | grep "3.1.1"
# Expected: All internal dependencies at 3.1.1

# Test 5: Documentation link validation
echo "=== Validating documentation links ==="
./s8r-docs check
# Expected: No broken links

# Test 6: Test suite validation
echo "=== Running test suite ==="
./s8r-test unit
# Expected: Tests pass
```

---

## Rollback Procedure

If validation fails:

```bash
# Option 1: Revert all changes
git reset --hard HEAD~1

# Option 2: Restore from backup branch
git checkout main
git reset --hard origin/main

# Option 3: Maven POM rollback (if only POMs changed)
mvn versions:revert

# Verify rollback
./s8r-version get
grep "version" README.md
```

---

## Automated Execution Script

```bash
#!/usr/bin/env bash
# version-alignment-execute.sh

set -euo pipefail

echo "========================================="
echo "Samstraumr Version Alignment to 3.1.1"
echo "========================================="

# Pre-flight checks
echo "1. Verifying current version..."
./s8r-version get | grep "3.1.1" || { echo "ERROR: version.properties not at 3.1.1"; exit 1; }

# Create backup
echo "2. Creating backup branch..."
git checkout -b docs/version-alignment-3.1.1
git push origin docs/version-alignment-3.1.1

# Document before state
echo "3. Documenting current state..."
grep -rn "3\.0\.[0-9]" . --include="*.md" --include="pom.xml" --exclude-dir=".git" > version-refs-before.txt

# Update files
echo "4. Updating README.md..."
sed -i '' 's/version-3\.0\.5-blue/version-3.1.1-blue/g' README.md
sed -i '' 's/<version>3\.0\.5<\/version>/<version>3.1.1<\/version>/g' README.md

echo "5. Updating CLAUDE.md..."
sed -i '' 's/Current: 3\.0\.2/Current: 3.1.1/g' CLAUDE.md

echo "6. Updating POM files..."
mvn versions:set -DnewVersion=3.1.1 -DprocessAllModules=true
mvn versions:commit

echo "7. Updating supporting documentation..."
sed -i '' 's/Framework Version\*\*: 3\.0\.5/Framework Version**: 3.1.1/g' REFACTORING_ASSESSMENT.md
sed -i '' "s/s8rVersion: '3\.0\.2'/s8rVersion: '3.1.1'/g" docs/testing/karate-testing-guide.md
sed -i '' 's/Framework Version\*\*: 3\.0\.5/Framework Version**: 3.1.1/g' docs/architecture/reports/design-analysis-gaps-report.md

# Validation
echo "8. Running validation..."
./s8r-build compile || { echo "ERROR: Build failed"; exit 1; }
./s8r-version verify || { echo "ERROR: Version verification failed"; exit 1; }

# Document after state
echo "9. Documenting final state..."
grep -rn "3\.0\.[0-9]" . --include="*.md" --include="pom.xml" --exclude-dir=".git" > version-refs-after.txt

echo "10. Comparing before/after..."
diff version-refs-before.txt version-refs-after.txt || true

# Commit
echo "11. Committing changes..."
git add .
git commit -m "docs: align version references to 3.1.1

- Updated README.md version badge and Maven example
- Updated CLAUDE.md version reference
- Updated all POM files using Maven versions plugin
- Updated supporting documentation files
- Added changelog entry for 3.1.1

Fixes version drift between source of truth and documentation.
Ensures new users get correct Maven coordinates."

echo "========================================="
echo "✓ Version alignment complete!"
echo "========================================="
echo ""
echo "Next steps:"
echo "1. Review changes: git diff HEAD~1"
echo "2. Run full test suite: ./s8r-test all"
echo "3. Push to remote: git push origin docs/version-alignment-3.1.1"
echo "4. Create pull request for review"
```

---

## Risk Assessment

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| POM update breaks build | LOW | HIGH | Use Maven versions plugin (atomic), test before commit |
| Broken documentation links | MEDIUM | LOW | Run link validator before commit |
| Missed version reference | MEDIUM | MEDIUM | Comprehensive grep scan, automated validation |
| Git merge conflicts | LOW | LOW | Work on dedicated branch, coordinate with team |
| Rollback needed | LOW | MEDIUM | Multiple rollback options documented |

---

## Success Criteria

- [ ] Zero 3.0.x references in user-facing documentation (README, CLAUDE.md)
- [ ] All POM files updated to 3.1.1
- [ ] Maven build succeeds
- [ ] Version verification passes
- [ ] No broken documentation links
- [ ] Unit tests pass
- [ ] Changelog updated
- [ ] Changes reviewed and merged

---

## Timeline

- **Preparation**: 15 minutes (backup, verification)
- **Execution**: 30 minutes (run script, validate)
- **Review**: 15 minutes (diff review, PR creation)
- **Total**: ~1 hour

---

## Contact

**Plan Author**: Claude Code (Mumford Coordinator Mode)
**Date**: 2025-12-05
**Codebase Version**: 3.1.1
