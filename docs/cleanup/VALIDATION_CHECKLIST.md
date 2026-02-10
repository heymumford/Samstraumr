# Validation Checklist: Root Directory Cleanup

**Purpose:** Step-by-step verification for Phase 4 (Agent 6) to validate cleanup completion and detect broken links/missing files.

**Date:** 2026-02-10

---

## Phase 4A: Pre-Validation Setup (Agent 6)

### Environment Check
- [ ] Read `target-structure.md` (Agent 3 output)
- [ ] Read `MIGRATION_PLAN.md` (Agent 4 execution plan)
- [ ] Verify git branch: `git branch --show-current` (expect `feature/cleanup-root-directory`)
- [ ] Verify git status clean: `git status` (expect `working tree clean`)
- [ ] Record baseline: `git log --oneline -1` (document last commit before Phase 4)

---

## Phase 4B: File Count Validation

### Root Directory Cleanup

```bash
# Command: Count root files
find . -maxdepth 1 -type f \( -name "*.md" -o -name "*.json" -o -name "*.sh" \) | sort

# Expected files (6 ONLY):
# ./.gitignore
# ./CLAUDE.md
# ./CONTRIBUTING.md
# ./LICENSE
# ./README.md
# ./pom.xml
```

**Validation:**
- [ ] Total root files: ≤ 10 (accept 6-10 for flexibility)
- [ ] No untracked analysis files in root
- [ ] No leftover .sh scripts in root
- [ ] CLAUDE.md present (project config)

### docs/ Organization Validation

```bash
# Command: Count .md files in docs/
find docs/ -name "*.md" -type f | wc -l
# Expected: ≥ 120

# Command: Count .json files in docs/
find docs/ -name "*.json" -type f | wc -l
# Expected: 10

# Detailed breakdown by folder:
echo "=== docs/0-START ==="
ls -1 docs/0-START/*.md | wc -l           # expect: 6
echo "=== docs/research/ ==="
find docs/research -name "*.md" | wc -l  # expect: 32-35
find docs/research -name "*.json" | wc -l # expect: 7
echo "=== docs/planning/ ==="
find docs/planning -name "*.md" | wc -l  # expect: 40+
find docs/planning -name "*.csv" | wc -l # expect: 1
echo "=== docs/dev/ci ==="
ls -1 docs/dev/ci/*.md | wc -l           # expect: 5
echo "=== docs/reference/standards ==="
ls -1 docs/reference/standards/*.md | wc -l # expect: 2
echo "=== docs/roadmap ==="
ls -1 docs/roadmap/*.* | wc -l           # expect: 3
echo "=== docs/publication ==="
ls -1 docs/publication/*.* | wc -l       # expect: 4
```

**Validation Checklist:**
- [ ] docs/0-START/: 6 files
- [ ] docs/research/: 32+ files (7 .json)
- [ ] docs/planning/: 40+ files (1 .csv)
- [ ] docs/publication/: 4 files
- [ ] docs/dev/ci/: 5 files
- [ ] docs/reference/standards/: 2 files
- [ ] docs/roadmap/: 3 files

### scripts/ Organization Validation

```bash
# Command: Count .sh files in scripts/
find scripts/ -name "*.sh" -type f | wc -l
# Expected: 4

# List them:
ls -1 scripts/analysis/*.sh
# Expected:
#   run-component-type-test.sh
#   run-composite-connection-test.sh
#   run-isolated-validator-test.sh
#   run-manual-validator-test.sh
```

**Validation:**
- [ ] scripts/analysis/: 4 .sh files
- [ ] All files have execute permissions (check with `ls -l`)

---

## Phase 4C: File Integrity Validation

### Verify All Files Tracked by Git

```bash
# Find any untracked files
git status --porcelain | grep "^??"

# Expected: Empty (all files committed)
```

**Validation:**
- [ ] No untracked files in docs/ (except .gitignore exceptions)
- [ ] No leftover root files
- [ ] git status is clean

### Verify File Rename History

```bash
# Check git history for specific files (they should show as "renamed")
git log --follow docs/planning/active/task_plan.md | head -5
# Expected: Original commit from root, then rename in latest commit

git log --oneline --all | head -5
# Should show migration commit with message pattern: "feat(cleanup): migrate..."
```

**Validation:**
- [ ] Git history preserved for all moved files
- [ ] Latest commit is migration commit
- [ ] No `git rm` followed by `git add` (proper `git mv` used)

---

## Phase 4D: Content Validation (Spot Checks)

### Verify No Files Corrupted

```bash
# Sample 10 random .md files and verify they open
find docs/ -name "*.md" | shuf | head -10 | while read f; do
  lines=$(wc -l < "$f")
  echo "$f: $lines lines"
  head -3 "$f"
  echo "---"
done
```

**Validation:**
- [ ] All sampled files have content (not empty)
- [ ] All sampled files start with valid markdown (# heading or similar)
- [ ] No corrupted binary data

### Verify Key Files Exist

```bash
# Critical files that must exist:
test -f docs/0-START/START_HERE.md && echo "✓ START_HERE.md" || echo "✗ START_HERE.md MISSING"
test -f docs/planning/active/task_plan.md && echo "✓ task_plan.md" || echo "✗ task_plan.md MISSING"
test -f docs/research/expert-synthesis/EXPERT_PANEL_SYNTHESIS_MASTER.md && echo "✓ EXPERT_PANEL" || echo "✗ EXPERT_PANEL MISSING"
test -f docs/publication/PUBLICATION_STRATEGY.md && echo "✓ PUBLICATION_STRATEGY.md" || echo "✗ PUBLICATION_STRATEGY.md MISSING"
test -f scripts/analysis/run-component-type-test.sh && echo "✓ test scripts" || echo "✗ test scripts MISSING"
```

**Validation:**
- [ ] All critical files present
- [ ] No "MISSING" messages in output

---

## Phase 4E: Link Validation

### Root README.md Links

```bash
# Check if docs/ is mentioned in root README
grep -q "docs/" README.md && echo "✓ Root README mentions docs/" || echo "✗ Root README broken"

# Check for broken markdown links (generic check)
grep -E "\[.*\]\(.*\.md\)" README.md | head -5
```

**Validation:**
- [ ] README.md mentions docs/ structure
- [ ] No broken links in README (manual review)

### Internal Cross-References (Sample)

```bash
# Check if old root references in docs/ files need updating
# Example: docs/planning/active/task_plan.md might reference other files

grep -r "target-structure.md" docs/cleanup/ | head -3
# Should find references to target-structure (that's expected)

grep -r "PHASE_1_ELITE" docs/planning/phases/phase-1/ | wc -l
# Should find internal references
```

**Validation:**
- [ ] No "root/" or "./" references in migrated files (for docs)
- [ ] Internal cross-links still work (sample check)
- [ ] File manifests reference correct paths

### Index Files Present

```bash
# Check for README.md in each major folder
for dir in docs/0-START docs/research docs/planning docs/publication docs/dev/ci; do
  test -f "$dir/README.md" && echo "✓ $dir/README.md" || echo "✗ $dir/README.md MISSING"
done
```

**Validation:**
- [ ] All major folders have README.md index
- [ ] No missing index files

---

## Phase 4F: Structure Validation

### Verify Folder Tree Matches Target

```bash
# Generate tree output
tree docs/ -L 3 -d > /tmp/current-tree.txt

# Manually compare against target-structure.md
# Key folders to check:
# - docs/0-START/
# - docs/research/{analysis,debates,expert-synthesis,rankings,evidence}
# - docs/planning/{phases/{phase-1/{hiring,budget,contracts,roles,comms},phase-2,phase-3},active,completed,feature-tracking,decision-logs}
# - docs/publication/
# - docs/dev/ci/
# - docs/reference/standards/
# - docs/roadmap/
# - scripts/analysis/
```

**Validation:**
- [ ] All expected folders exist
- [ ] No extra/unexpected folders
- [ ] Folder nesting matches target
- [ ] Deduplication complete (no `contrib/` and `contribution/` both present)

### Verify Archive Status

```bash
# Check that old/legacy files are archived
find docs/archive -type f | head -10
# Should have some legacy files

test -d docs/archive && echo "✓ archive/ exists" || echo "✗ archive/ missing"
```

**Validation:**
- [ ] docs/archive/ exists (for old analysis cycles)
- [ ] Legacy files properly archived

---

## Phase 4G: Permissions & Metadata

### Script Permissions

```bash
# Check that .sh files are executable
ls -l scripts/analysis/*.sh | grep "^-rwx"
# All should start with -rwx (executable)
```

**Validation:**
- [ ] All .sh files executable (mode 755 or similar)
- [ ] No permission errors when listing

### File Ownership

```bash
# Verify files owned by correct user
ls -l docs/0-START/START_HERE.md | awk '{print $3}' | grep "$USER"
```

**Validation:**
- [ ] Files owned by correct user (vorthruna)
- [ ] No permission issues

---

## Phase 4H: Git Status Final Check

```bash
# Full status
git status

# Expected output:
# On branch feature/cleanup-root-directory
# Your branch is ahead of 'main' by 1 commit.
#   (use "git push" to publish your local commits)
# nothing to commit, working tree clean
```

**Validation:**
- [ ] On correct branch: `feature/cleanup-root-directory`
- [ ] Working tree clean (no uncommitted changes)
- [ ] All 120+ file moves committed
- [ ] Ready to push

---

## Phase 4I: Specific File Existence Validation

### All 112 .md Files Accounted For

```bash
# Create manifest of expected files
cat > /tmp/expected-files.txt << 'EOF'
# Copy this from MIGRATION_PLAN.md appendix and verify each file exists
EOF

# For each file in list, verify it exists in docs/ or scripts/
while read file; do
  if [ -f "$file" ]; then
    echo "✓ $file"
  else
    echo "✗ MISSING: $file"
  fi
done < /tmp/expected-files.txt | grep "✗" | wc -l

# Expected: 0 missing files
```

**Validation:**
- [ ] 0 missing files
- [ ] All 112 .md files migrated
- [ ] All 10 .json files migrated
- [ ] All 4 .sh scripts migrated

### No Duplicates

```bash
# Find duplicate files (same name in different locations)
find docs/ -name "*.md" | sort | uniq -d
find docs/ -name "*.json" | sort | uniq -d

# Expected: Empty (no duplicates)
```

**Validation:**
- [ ] No duplicate files
- [ ] No file appears in multiple folders

---

## Phase 4J: Documentation Completeness

### Index Files Complete

```bash
# Check that major index files exist and link sections
for file in docs/README.md docs/research/README.md docs/planning/README.md; do
  test -f "$file" && wc -l "$file" || echo "MISSING: $file"
done
```

**Validation:**
- [ ] docs/README.md exists and is substantial (>50 lines)
- [ ] docs/research/README.md exists
- [ ] docs/planning/README.md exists

### MANIFEST.md Created

```bash
test -f docs/MANIFEST.md && echo "✓ MANIFEST.md" || echo "✗ MANIFEST.md MISSING"
# Should list all 112+ files with locations
```

**Validation:**
- [ ] docs/MANIFEST.md exists (created by Agent 5)
- [ ] Contains inventory of all moved files

---

## Phase 4K: Summary Report

### Generate Validation Report

```bash
# Create report
cat > docs/cleanup/VALIDATION_REPORT.md << 'EOF'
# Validation Report: Root Directory Cleanup

**Date:** $(date)
**Validator:** Agent 6
**Status:** [PASS/FAIL]

## Summary
- Root files: [COUNT]/6
- Docs files: [COUNT]/112+
- Scripts migrated: [COUNT]/4
- Broken links: [COUNT] (0 expected)
- Missing files: [COUNT] (0 expected)

## Detailed Checks
...
EOF
```

**Validation:**
- [ ] Report generated
- [ ] All checks pass
- [ ] Ready for final commit

---

## Success Criteria (Phase 4)

| Check | Status | Evidence |
|-------|--------|----------|
| Root cleaned | ✓/✗ | `find . -maxdepth 1 -name "*.md" -o "*.sh"` ≤ 6 |
| All files migrated | ✓/✗ | `find docs/ -name "*.md"` = 112+ |
| Git history preserved | ✓/✗ | `git log --follow` shows renames |
| No broken links | ✓/✗ | Manual verification (sample 20 links) |
| Structure matches target | ✓/✗ | `tree docs/ -L 3` matches target-structure.md |
| All scripts executable | ✓/✗ | `ls -l scripts/analysis/*.sh` all `-rwx` |
| Index files created | ✓/✗ | Each folder has README.md |
| MANIFEST.md complete | ✓/✗ | `wc -l docs/MANIFEST.md` ≥ 120 |

---

## Failure Recovery

### If Validation Fails

**Scenario: Files missing from docs/**
```bash
# Check if they're still in root
find . -maxdepth 1 -name "*.md" | wc -l

# If > 6, something wasn't migrated
# Trigger Agent 4 to re-run migrations
```

**Scenario: Broken links in root README**
```bash
# Identify broken links
grep -E "\[.*\]\(.*\)" README.md | while read line; do
  echo "Check: $line"
done

# Manual fix required
```

**Scenario: Git history lost**
```bash
# Verify with:
git log --oneline --all | grep "migrate\|cleanup"

# If no migration commit, Phase 2 failed
# Trigger Agent 4 to redo from branch reset
```

---

## Handoff to Final Merge

Once all Phase 4 checks pass:

1. **Generate final report:** docs/cleanup/VALIDATION_REPORT.md
2. **Push branch:** `git push origin feature/cleanup-root-directory`
3. **Create PR:** (Manual step - not agent responsibility)
4. **Status:** "PHASE 4 COMPLETE - Ready for merge"

**Next Steps (Manual):**
- Code review (verify all moves intentional)
- Merge to main branch
- Cleanup feature branch

---

## Appendix: Quick Validation Script

```bash
#!/bin/bash
# quick-validate.sh - Fast validation check

echo "=== ROOT CLEANUP ==="
ROOT_FILES=$(find . -maxdepth 1 -type f \( -name "*.md" -o -name "*.json" -o -name "*.sh" \) | wc -l)
echo "Root files: $ROOT_FILES (expect ≤10)"

echo ""
echo "=== DOCS ORGANIZATION ==="
MD_COUNT=$(find docs/ -name "*.md" | wc -l)
JSON_COUNT=$(find docs/ -name "*.json" | wc -l)
echo "Markdown files in docs/: $MD_COUNT (expect ≥112)"
echo "JSON files in docs/: $JSON_COUNT (expect 10)"

echo ""
echo "=== SCRIPTS ==="
SH_COUNT=$(find scripts/ -name "*.sh" | wc -l)
echo "Scripts migrated: $SH_COUNT (expect 4)"

echo ""
echo "=== GIT STATUS ==="
git status --short | wc -l
echo "Uncommitted changes: ^ (expect 0)"

echo ""
echo "=== KEY FILES ==="
test -f docs/0-START/START_HERE.md && echo "✓ START_HERE.md" || echo "✗ START_HERE MISSING"
test -f docs/planning/active/task_plan.md && echo "✓ task_plan.md" || echo "✗ task_plan MISSING"
test -f scripts/analysis/run-component-type-test.sh && echo "✓ test scripts" || echo "✗ scripts MISSING"
test -f README.md && echo "✓ README.md in root" || echo "✗ README MISSING"
```

Save as: `docs/cleanup/quick-validate.sh`

Run with: `bash docs/cleanup/quick-validate.sh`

