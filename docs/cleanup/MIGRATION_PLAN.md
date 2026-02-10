# Migration Plan: Root Directory Cleanup

**Purpose:** Step-by-step execution guide for migrating 112 .md files, 10 .json files, and 4 .sh scripts from root to organized docs/ and scripts/ folders.

**Status:** Phase 2 (Implementation) — Agent 4 (Migrator)

**Date:** 2026-02-10

---

## Overview

| Phase | Agent | Deliverable | Gate |
|-------|-------|-------------|------|
| Phase 1 | Agent 3 | Target structure (this doc) | ✅ Complete |
| **Phase 2** | **Agent 4** | **Execute migrations** | In progress |
| Phase 3 | Agent 5 | Update docs + README | Blocked on Phase 2 |
| Phase 4 | Agent 6 | Validate + verify links | Blocked on Phase 3 |

---

## Pre-Migration Checklist

Before starting any file moves:

- [ ] Read `docs/cleanup/target-structure.md` (Agent 3 output)
- [ ] Verify current root file count: `find . -maxdepth 1 -type f \( -name "*.md" -o -name "*.json" -o -name "*.sh" \) | wc -l` (expect ~126)
- [ ] Backup current git state: `git status`
- [ ] Create feature branch: `git checkout -b feature/cleanup-root-directory`
- [ ] No uncommitted changes: `git status` (should be clean)

---

## Phase 2A: Folder Structure Setup

### Step 1: Create Directory Tree

Execute in order (each mkdir ensures parent exists):

```bash
# docs/ subdirectories (create parent structure)
mkdir -p docs/0-START
mkdir -p docs/research/analysis
mkdir -p docs/research/debates
mkdir -p docs/research/expert-synthesis
mkdir -p docs/research/rankings
mkdir -p docs/research/evidence
mkdir -p docs/planning/phases/phase-1
mkdir -p docs/planning/phases/phase-1/hiring
mkdir -p docs/planning/phases/phase-1/budget
mkdir -p docs/planning/phases/phase-1/contracts
mkdir -p docs/planning/phases/phase-1/roles
mkdir -p docs/planning/phases/phase-1/comms
mkdir -p docs/planning/phases/phase-2
mkdir -p docs/planning/phases/phase-3
mkdir -p docs/planning/active
mkdir -p docs/planning/completed
mkdir -p docs/planning/feature-tracking
mkdir -p docs/planning/decision-logs
mkdir -p docs/publication
mkdir -p docs/dev/ci
mkdir -p docs/reference/standards
mkdir -p docs/cleanup
mkdir -p scripts/analysis
mkdir -p scripts/admin
```

### Step 2: Create README Index Files

Create placeholder README.md in each new folder (Agent 5 will fill these):

```bash
# Master index (update existing)
cat > docs/README.md << 'EOF'
# Samstraumr Documentation

Master index. See target-structure.md for organization guide.

- [Getting Started](0-START/)
- [Research](research/)
- [Planning](planning/)
- [Architecture](architecture/)
- [Concepts](concepts/)
- [Testing](testing/)
- [Guides](guides/)
- [Publication](publication/)
- [Reference](reference/)
EOF

# Quick README files for new folders
for dir in docs/0-START docs/research docs/planning docs/publication docs/cleanup scripts; do
  echo "# $(basename $dir)" > "$dir/README.md"
  echo "" >> "$dir/README.md"
  echo "See \`target-structure.md\` for details." >> "$dir/README.md"
done
```

### Step 3: Verify Structure

```bash
tree docs/ -L 3
tree scripts/ -L 2
```

---

## Phase 2B: File Migrations

### Instruction Format

For each file group:
1. **Source:** Root file names
2. **Destination:** Full path in docs/ or scripts/
3. **Git Command:** `git mv` (preserves history)
4. **Count:** How many files

### Group 1: Quick Start (→ docs/0-START/)

**Files:** 5
**Command:**
```bash
git mv START_HERE.md docs/0-START/
git mv START_HERE_PHASE1.md docs/0-START/
git mv START_NOW.md docs/0-START/
git mv QUICK_START_GUIDE.md docs/0-START/
git mv QUICK_REFERENCE_CARD.md docs/0-START/
git mv AGENTS.md docs/0-START/
```

**Verify:** `ls -1 docs/0-START/*.md | wc -l` → expect 6

---

### Group 2: Research → Analysis (→ docs/research/analysis/)

**Files:** 7
**Command:**
```bash
git mv ARCHITECTURE_NOVELTY_ANALYSIS.md docs/research/analysis/
git mv DDD_ANALYSIS_SUMMARY.md docs/research/analysis/
git mv COGNITIVE_LOAD_METRICS.md docs/research/analysis/
git mv MARKET_RESEARCH_SUMMARY.md docs/research/analysis/
git mv RESEARCH_ANALYSIS_INDEX.md docs/research/analysis/
git mv INDEX_ALL_ANALYSIS.md docs/research/analysis/
git mv ddd-analysis-report.json docs/research/analysis/
```

**Verify:** `ls -1 docs/research/analysis/ | wc -l` → expect 7

---

### Group 3: Research → Debates (→ docs/research/debates/)

**Files:** 12
**Command:**
```bash
git mv ADVERSARIAL_DEBATE_SUMMARY.md docs/research/debates/
git mv ADVERSARIAL_TESTS_SUMMARY.md docs/research/debates/
git mv DEBATE_INDEX.md docs/research/debates/
git mv DEBATE_SUMMARY.md docs/research/debates/
git mv DEBATE_VISUAL_SUMMARY.md docs/research/debates/
git mv DEBATE_EVOLUTION_VS_COGNITION.json docs/research/debates/
git mv METAPHOR_ANALYSIS_INDEX.md docs/research/debates/
git mv METAPHOR_ANALYSIS_SUMMARY.md docs/research/debates/
git mv METAPHOR_CODE_EVIDENCE.md docs/research/debates/
git mv METAPHOR_EPISTEMIC_ANALYSIS.json docs/research/debates/
git mv METAPHOR_QUICK_REFERENCE.md docs/research/debates/
git mv METAPHOR_REFACTORING_GUIDE.md docs/research/debates/
git mv ddd-vs-testing-debate.json docs/research/debates/
git mv README_ADVERSARIAL_DEBATE.md docs/research/debates/
```

**Verify:** `ls -1 docs/research/debates/ | wc -l` → expect 14

---

### Group 4: Research → Expert Synthesis (→ docs/research/expert-synthesis/)

**Files:** 9
**Command:**
```bash
git mv EXPERT_PANEL_SYNTHESIS_MASTER.md docs/research/expert-synthesis/
git mv ITERATION_1_EXECUTIVE_SUMMARY.md docs/research/expert-synthesis/
git mv ITERATION_1_INDEX.md docs/research/expert-synthesis/
git mv ITERATION_1_AGENT_CRITIQUES.json docs/research/expert-synthesis/
git mv ITERATION_2_EXECUTIVE_SUMMARY.md docs/research/expert-synthesis/
git mv ITERATION_2_HANDOFF_BRIEF.md docs/research/expert-synthesis/
git mv ITERATION_3_EXECUTIVE_SUMMARY.md docs/research/expert-synthesis/
git mv ITERATION_3_INDEX.md docs/research/expert-synthesis/
git mv RESOURCE_ALLOCATION_ITERATION_2.md docs/research/expert-synthesis/
git mv VALUE_FRAMEWORK_ITERATION_1.md docs/research/expert-synthesis/
```

**Verify:** `ls -1 docs/research/expert-synthesis/ | wc -l` → expect 10

---

### Group 5: Research → Rankings (→ docs/research/rankings/)

**Files:** 4
**Command:**
```bash
git mv FINAL_RESEARCH_RANKINGS.md docs/research/rankings/
git mv RESEARCH_OPPORTUNITIES.md docs/research/rankings/
git mv RESEARCH_SYNTHESIS_REPORT.md docs/research/rankings/
git mv RESEARCH_ROADMAP_SYNTHESIS.md docs/research/rankings/
```

**Verify:** `ls -1 docs/research/rankings/ | wc -l` → expect 4

---

### Group 6: Research → Evidence (→ docs/research/evidence/)

**Files:** 4
**Command:**
```bash
git mv EVIDENCE_SUMMARY.md docs/research/evidence/
git mv DEVELOPER_COGNITION_ANALYSIS.json docs/research/evidence/
git mv COMPARATIVE_ARCHITECTURE_ANALYSIS.json docs/research/evidence/
git mv findings_catalog.md docs/research/evidence/
```

**Verify:** `ls -1 docs/research/evidence/ | wc -l` → expect 4

---

### Group 7: Planning → Phases/Phase-1 (→ docs/planning/phases/phase-1/)

**Files:** 11 (main phase-1 docs, not subfolders)
**Command:**
```bash
git mv PHASE_1_ELITE_SYSTEM_INDEX.md docs/planning/phases/phase-1/
git mv PHASE_1_ELITE_EXECUTION_DETAILS.md docs/planning/phases/phase-1/
git mv PHASE_1_EXECUTION_CHECKLIST.md docs/planning/phases/phase-1/
git mv PHASE_1_EXECUTION_STARTED.txt docs/planning/phases/phase-1/
git mv PHASE_1_FINAL_REFINED_PLAN.md docs/planning/phases/phase-1/
git mv PHASE_1_LIVE_EXECUTION_LOG.md docs/planning/phases/phase-1/
git mv PHASE_1_GO_NO_GO_ANALYSIS.md docs/planning/phases/phase-1/
git mv PHASE_1_QUANTIFIED_RISK_ANALYSIS.md docs/planning/phases/phase-1/
git mv PHASE_1_BACKUP_PLAYBOOKS.md docs/planning/phases/phase-1/
git mv PHASE_1_DECISION_TREES.md docs/planning/phases/phase-1/
git mv PHASE_1_REAL_TIME_DASHBOARD_TEMPLATE.md docs/planning/phases/phase-1/
```

**Verify:** `ls -1 docs/planning/phases/phase-1/*.md | wc -l` → expect 11

---

### Group 8: Planning → Phases/Phase-1/Hiring (→ docs/planning/phases/phase-1/hiring/)

**Files:** 4
**Command:**
```bash
git mv PHASE_1_HIRING_MARKET_INTELLIGENCE.md docs/planning/phases/phase-1/hiring/
git mv PHASE_1_INTERVIEW_SCORECARD.md docs/planning/phases/phase-1/hiring/
git mv PHASE_1_TALENT_POSITIONING_STRATEGY.md docs/planning/phases/phase-1/hiring/
git mv RECRUITER_EMAIL_TEMPLATES.md docs/planning/phases/phase-1/hiring/
```

**Verify:** `ls -1 docs/planning/phases/phase-1/hiring/*.md | wc -l` → expect 4

---

### Group 9: Planning → Phases/Phase-1/Budget (→ docs/planning/phases/phase-1/budget/)

**Files:** 3
**Command:**
```bash
git mv PHASE_1_BUDGET_SUMMARY.md docs/planning/phases/phase-1/budget/
git mv PHASE_1_FINANCIAL_MODELING.md docs/planning/phases/phase-1/budget/
git mv PHASE_1_CONTRACTOR_SCOPE_DOCUMENTS.md docs/planning/phases/phase-1/budget/
```

**Verify:** `ls -1 docs/planning/phases/phase-1/budget/*.md | wc -l` → expect 3

---

### Group 10: Planning → Phases/Phase-1/Contracts (→ docs/planning/phases/phase-1/contracts/)

**Files:** 1
**Command:**
```bash
git mv PHASE_1_CONTRACT_TEMPLATE.md docs/planning/phases/phase-1/contracts/
```

**Verify:** `ls -1 docs/planning/phases/phase-1/contracts/*.md | wc -l` → expect 1

---

### Group 11: Planning → Phases/Phase-1/Roles (→ docs/planning/phases/phase-1/roles/)

**Files:** 4
**Command:**
```bash
git mv PHASE_1_COLLABORATOR_JOB_DESCRIPTION.md docs/planning/phases/phase-1/roles/
git mv PHASE_1_COLLABORATOR_HARNESS_CALIBRATED.md docs/planning/phases/phase-1/roles/
git mv PHASE_1_PRODUCTOWNER_ANALYSIS.md docs/planning/phases/phase-1/roles/
git mv COLLABORATOR_JOB_DESCRIPTION.md docs/planning/phases/phase-1/roles/
```

**Verify:** `ls -1 docs/planning/phases/phase-1/roles/*.md | wc -l` → expect 4

---

### Group 12: Planning → Phases/Phase-1/Comms (→ docs/planning/phases/phase-1/comms/)

**Files:** 2
**Command:**
```bash
git mv PHASE_1_PRE_WRITTEN_COMMUNICATIONS.md docs/planning/phases/phase-1/comms/
git mv KICKOFF_MEETING_SLIDES.md docs/planning/phases/phase-1/comms/
```

**Verify:** `ls -1 docs/planning/phases/phase-1/comms/*.md | wc -l` → expect 2

---

### Group 13: Planning → Phases/Phase-2 (→ docs/planning/phases/phase-2/)

**Files:** 1
**Command:**
```bash
git mv ITERATION_2_LAUNCH_CHECKLIST.md docs/planning/phases/phase-2/
```

**Verify:** `ls -1 docs/planning/phases/phase-2/*.md | wc -l` → expect 1

---

### Group 14: Planning → Phases/Phase-3 (→ docs/planning/phases/phase-3/)

**Files:** 2
**Command:**
```bash
git mv IMPLEMENTATION_PLAN_ITERATION_3.md docs/planning/phases/phase-3/
git mv AGENTIC_IMPLEMENTATION_GUIDE.md docs/planning/phases/phase-3/
```

**Verify:** `ls -1 docs/planning/phases/phase-3/*.md | wc -l` → expect 2

---

### Group 15: Planning → Active (→ docs/planning/active/)

**Files:** 10
**Command:**
```bash
git mv task_plan.md docs/planning/active/
git mv task_plan_phase1_tdd.md docs/planning/active/
git mv phase_tracker.md docs/planning/active/
git mv WEEK_0_ACTIVE_PLAN.md docs/planning/active/
git mv WEEK_0_EXECUTION_TRACKER.md docs/planning/active/
git mv WEEK_0_READINESS_SUMMARY.md docs/planning/active/
git mv WEEKEND_CHECKLIST.md docs/planning/active/
git mv ACTION_NOW.md docs/planning/active/
git mv EXECUTE_TODAY.md docs/planning/active/
git mv notes.md docs/planning/active/
```

**Verify:** `ls -1 docs/planning/active/*.md | wc -l` → expect 10

---

### Group 16: Planning → Feature-Tracking (→ docs/planning/feature-tracking/)

**Files:** 7
**Command:**
```bash
git mv feature_tracking_sheet.md docs/planning/feature-tracking/
git mv FEATURE_TRACKING_SHEET.csv docs/planning/feature-tracking/
git mv VALIDATION_TASKS.md docs/planning/feature-tracking/
git mv SUCCESS_CRITERIA.md docs/planning/feature-tracking/
git mv GO_CHECKLIST.md docs/planning/feature-tracking/
git mv GO_NO_GO_DECISION_TREE.md docs/planning/feature-tracking/
git mv GATE_DECISION_FRAMEWORK.md docs/planning/feature-tracking/
```

**Verify:** `ls -1 docs/planning/feature-tracking/* | wc -l` → expect 7

---

### Group 17: Planning → Decision-Logs (→ docs/planning/decision-logs/)

**Files:** 2
**Command:**
```bash
git mv DECISION_ENGINE.md docs/planning/decision-logs/
git mv POST_MERGE_CLEANUP.md docs/planning/decision-logs/
```

**Verify:** `ls -1 docs/planning/decision-logs/*.md | wc -l` → expect 2

---

### Group 18: Publication (→ docs/publication/)

**Files:** 4
**Command:**
```bash
git mv PUBLICATION_STRATEGY.md docs/publication/
git mv PUBLICATION_STRATEGY_README.md docs/publication/
git mv PUBLICATION_VENUES_CHECKLIST.md docs/publication/
git mv PUBLICATION_STRATEGY.json docs/publication/
```

**Verify:** `ls -1 docs/publication/* | wc -l` → expect 4

---

### Group 19: Dev/CI (→ docs/dev/ci/)

**Files:** 5
**Command:**
```bash
git mv CI_CD_STRATEGY.md docs/dev/ci/
git mv ci-workflow-analysis-and-refactoring.md docs/dev/ci/
git mv CI-OPTIMIZATION-README.md docs/dev/ci/
git mv CI-OPTIMIZATION-CHANGES.md docs/dev/ci/
git mv JUNIT6_MIGRATION_PLAN.md docs/dev/ci/
```

**Verify:** `ls -1 docs/dev/ci/*.md | wc -l` → expect 5

---

### Group 20: Reference/Standards (→ docs/reference/standards/)

**Files:** 2
**Command:**
```bash
git mv MUMFORD_STYLE.md docs/reference/standards/
git mv WRITING_MANIFESTO.md docs/reference/standards/
```

**Verify:** `ls -1 docs/reference/standards/*.md | wc -l` → expect 2

---

### Group 21: Roadmap (→ docs/roadmap/)

**Files:** 3
**Command:**
```bash
git mv RESEARCH_ROADMAP.json docs/roadmap/
git mv RESEARCH_ROADMAP_QUICK_REFERENCE.md docs/roadmap/
git mv REFACTORING_ROADMAP.md docs/roadmap/
```

**Verify:** `ls -1 docs/roadmap/* | wc -l` → expect 3

---

### Group 22: Scripts/Analysis (→ scripts/analysis/)

**Files:** 4
**Command:**
```bash
git mv run-component-type-test.sh scripts/analysis/
git mv run-composite-connection-test.sh scripts/analysis/
git mv run-isolated-validator-test.sh scripts/analysis/
git mv run-manual-validator-test.sh scripts/analysis/
```

**Verify:** `ls -1 scripts/analysis/*.sh | wc -l` → expect 4

---

### Group 23: Special Cases (ROOT)

**Files to STAY IN ROOT:** 6

These files MUST remain in root (not moved):
```bash
# No moves needed - already in root:
# - README.md
# - CONTRIBUTING.md
# - LICENSE
# - pom.xml
# - .gitignore
# - CLAUDE.md (project config, stays root)
```

---

### Group 24: Files Needing Action

**Relocate to Root (new):**
```bash
# Create SUPPORTING.md (consolidates CODE_OF_CONDUCT + SECURITY + MANIFESTO)
git mv CODE_OF_CONDUCT.md root.bak-CODE_OF_CONDUCT.md
git mv SECURITY.md root.bak-SECURITY.md
git mv MANIFESTO.md root.bak-MANIFESTO.md
# Agent 5 will create SUPPORTING.md by merging these
```

**Archive (no longer needed):**
```bash
git mv BRANCH_CLEANUP_GUIDE.md docs/archive/
git mv BRANCH_CONSOLIDATION_SUMMARY.md docs/archive/
git mv CHANGELOG_COMMANDS.md docs/archive/
git mv PR_DESCRIPTION.md docs/archive/
git mv POST_MERGE_CLEANUP.md docs/archive/
git mv PRODUCTION_CONTROLS.md docs/archive/
# ... more legacy files ...
```

---

## Phase 2C: Verification & Cleanup

### Step 1: Verify All Files Migrated

```bash
# Count files in root (should be ~6)
find . -maxdepth 1 -type f \( -name "*.md" -o -name "*.json" -o -name "*.sh" \) | wc -l

# Expected: ~6 (README, CONTRIBUTING, CLAUDE, LICENSE, pom.xml, .gitignore)
```

### Step 2: Verify Git Status

```bash
# Check git status
git status

# Expected: All moved files show as "renamed"
# Example: renamed: ACTION_NOW.md -> docs/planning/active/ACTION_NOW.md
```

### Step 3: Commit All Moves

```bash
git add .
git commit -m "feat(cleanup): migrate 112 docs + 4 scripts to organized structure

- Moved 112 .md files from root to docs/ (research, planning, publication)
- Moved 10 .json files to appropriate research/planning folders
- Moved 4 .sh test scripts to scripts/analysis/
- Root now contains only: README, CONTRIBUTING, CLAUDE, LICENSE, pom.xml, .gitignore
- Created folder structure for: 0-START, research/, planning/, publication/
- All internal git history preserved via 'git mv'

Migration tracked in docs/cleanup/MIGRATION_PLAN.md

Co-Authored-By: Claude Haiku 4.5 <noreply@anthropic.com>"
```

### Step 4: Post-Migration File Count

```bash
# Count total files moved
find docs/ -type f \( -name "*.md" -o -name "*.json" \) | wc -l
# Expected: ~122 (116 migrated + ~6 new READMEs)

find scripts/ -type f -name "*.sh" | wc -l
# Expected: 4

# Root files
ls -1 *.md *.json *.sh 2>/dev/null | wc -l
# Expected: ~6
```

---

## Blockers & Fallback Procedures

### Blocker: `git mv` fails (file conflicts)

**Symptom:** `error: source/destination differ in case`

**Solution:**
```bash
# Use manual move for conflicting files
mv SOURCE_FILE.md docs/destination/
git add -A
```

### Blocker: Too many uncommitted changes

**Symptom:** `git status` shows hundreds of changes

**Solution:**
```bash
# Revert and start over
git reset --hard HEAD
git clean -fd
```

### Blocker: Broken internal links

**Symptom:** Links like `[docs](docs/planning/X.md)` break in README

**Solution:** (Handled by Agent 5 in Phase 3)
- Update all broken links in README.md
- Create `docs/redirection-notice.md` explaining old → new paths

---

## Success Criteria (Phase 2)

| Criterion | Verification |
|-----------|--------------|
| All 112 .md files moved | `find docs/ -name "*.md" \| wc -l` ≥ 112 |
| All 10 .json files moved | `find docs/ -name "*.json" \| wc -l` = 10 |
| All 4 .sh files moved | `find scripts/ -name "*.sh" \| wc -l` = 4 |
| Root cleaned | `find . -maxdepth 1 -name "*.md" -o -name "*.sh"` ≤ 6 |
| Git history preserved | `git log --follow docs/planning/active/task_plan.md` shows original commit |
| No files left behind | `git status` shows all files committed |
| Folder structure complete | `tree docs/ -L 3` matches target-structure.md |

---

## Handoff to Agent 5 (Phase 3)

Once Phase 2 is complete:

1. **Push branch:** `git push -u origin feature/cleanup-root-directory`
2. **Create PR:** (Manual - not agent responsibility)
3. **Pass to Agent 5:**
   - All files migrated
   - git status clean
   - No broken links detected
   - Ready for documentation updates + README refresh

**Agent 5 Will:**
- Update root README.md with new structure links
- Create index files in each folder
- Add SUPPORTING.md (merged from CODE_OF_CONDUCT + SECURITY + MANIFESTO)
- Verify all cross-links work
- Create MANIFEST.md (master inventory)

---

## Appendix: File Inventory (All 126 Files)

### Migrated Files (120 total)

**docs/0-START/ (6):**
- AGENTS.md
- QUICK_REFERENCE_CARD.md
- QUICK_START_GUIDE.md
- START_HERE.md
- START_HERE_PHASE1.md
- START_NOW.md

**docs/research/analysis/ (7):**
- ARCHITECTURE_NOVELTY_ANALYSIS.md
- COGNITIVE_LOAD_METRICS.md
- DDD_ANALYSIS_SUMMARY.md
- INDEX_ALL_ANALYSIS.md
- MARKET_RESEARCH_SUMMARY.md
- RESEARCH_ANALYSIS_INDEX.md
- ddd-analysis-report.json

**docs/research/debates/ (14):**
- ADVERSARIAL_DEBATE_SUMMARY.md
- ADVERSARIAL_TESTS_SUMMARY.md
- DEBATE_EVOLUTION_VS_COGNITION.json
- DEBATE_INDEX.md
- DEBATE_SUMMARY.md
- DEBATE_VISUAL_SUMMARY.md
- METAPHOR_ANALYSIS_INDEX.md
- METAPHOR_ANALYSIS_SUMMARY.md
- METAPHOR_CODE_EVIDENCE.md
- METAPHOR_EPISTEMIC_ANALYSIS.json
- METAPHOR_QUICK_REFERENCE.md
- METAPHOR_REFACTORING_GUIDE.md
- README_ADVERSARIAL_DEBATE.md
- ddd-vs-testing-debate.json

**docs/research/expert-synthesis/ (10):**
- EXPERT_PANEL_SYNTHESIS_MASTER.md
- ITERATION_1_AGENT_CRITIQUES.json
- ITERATION_1_EXECUTIVE_SUMMARY.md
- ITERATION_1_INDEX.md
- ITERATION_2_EXECUTIVE_SUMMARY.md
- ITERATION_2_HANDOFF_BRIEF.md
- ITERATION_3_EXECUTIVE_SUMMARY.md
- ITERATION_3_INDEX.md
- RESOURCE_ALLOCATION_ITERATION_2.md
- VALUE_FRAMEWORK_ITERATION_1.md

**docs/research/rankings/ (4):**
- FINAL_RESEARCH_RANKINGS.md
- RESEARCH_OPPORTUNITIES.md
- RESEARCH_ROADMAP_SYNTHESIS.md
- RESEARCH_SYNTHESIS_REPORT.md

**docs/research/evidence/ (4):**
- COMPARATIVE_ARCHITECTURE_ANALYSIS.json
- DEVELOPER_COGNITION_ANALYSIS.json
- EVIDENCE_SUMMARY.md
- findings_catalog.md

**docs/planning/phases/phase-1/ (11):**
- PHASE_1_BACKUP_PLAYBOOKS.md
- PHASE_1_DECISION_TREES.md
- PHASE_1_ELITE_EXECUTION_DETAILS.md
- PHASE_1_ELITE_SYSTEM_INDEX.md
- PHASE_1_EXECUTION_CHECKLIST.md
- PHASE_1_EXECUTION_STARTED.txt
- PHASE_1_FINAL_REFINED_PLAN.md
- PHASE_1_GO_NO_GO_ANALYSIS.md
- PHASE_1_LIVE_EXECUTION_LOG.md
- PHASE_1_QUANTIFIED_RISK_ANALYSIS.md
- PHASE_1_REAL_TIME_DASHBOARD_TEMPLATE.md

**docs/planning/phases/phase-1/hiring/ (4):**
- PHASE_1_HIRING_MARKET_INTELLIGENCE.md
- PHASE_1_INTERVIEW_SCORECARD.md
- PHASE_1_TALENT_POSITIONING_STRATEGY.md
- RECRUITER_EMAIL_TEMPLATES.md

**docs/planning/phases/phase-1/budget/ (3):**
- PHASE_1_BUDGET_SUMMARY.md
- PHASE_1_CONTRACTOR_SCOPE_DOCUMENTS.md
- PHASE_1_FINANCIAL_MODELING.md

**docs/planning/phases/phase-1/contracts/ (1):**
- PHASE_1_CONTRACT_TEMPLATE.md

**docs/planning/phases/phase-1/roles/ (4):**
- COLLABORATOR_JOB_DESCRIPTION.md
- PHASE_1_COLLABORATOR_HARNESS_CALIBRATED.md
- PHASE_1_COLLABORATOR_JOB_DESCRIPTION.md
- PHASE_1_PRODUCTOWNER_ANALYSIS.md

**docs/planning/phases/phase-1/comms/ (2):**
- KICKOFF_MEETING_SLIDES.md
- PHASE_1_PRE_WRITTEN_COMMUNICATIONS.md

**docs/planning/phases/phase-2/ (1):**
- ITERATION_2_LAUNCH_CHECKLIST.md

**docs/planning/phases/phase-3/ (2):**
- AGENTIC_IMPLEMENTATION_GUIDE.md
- IMPLEMENTATION_PLAN_ITERATION_3.md

**docs/planning/active/ (10):**
- ACTION_NOW.md
- EXECUTE_TODAY.md
- WEEK_0_ACTIVE_PLAN.md
- WEEK_0_EXECUTION_TRACKER.md
- WEEK_0_READINESS_SUMMARY.md
- WEEKEND_CHECKLIST.md
- notes.md
- phase_tracker.md
- task_plan.md
- task_plan_phase1_tdd.md

**docs/planning/feature-tracking/ (7):**
- FEATURE_TRACKING_SHEET.csv
- GATE_DECISION_FRAMEWORK.md
- GO_CHECKLIST.md
- GO_NO_GO_DECISION_TREE.md
- SUCCESS_CRITERIA.md
- VALIDATION_TASKS.md
- feature_tracking_sheet.md

**docs/planning/decision-logs/ (2):**
- DECISION_ENGINE.md
- POST_MERGE_CLEANUP.md

**docs/publication/ (4):**
- PUBLICATION_STRATEGY.json
- PUBLICATION_STRATEGY.md
- PUBLICATION_STRATEGY_README.md
- PUBLICATION_VENUES_CHECKLIST.md

**docs/dev/ci/ (5):**
- CI-OPTIMIZATION-CHANGES.md
- CI-OPTIMIZATION-README.md
- CI_CD_STRATEGY.md
- JUNIT6_MIGRATION_PLAN.md
- ci-workflow-analysis-and-refactoring.md

**docs/reference/standards/ (2):**
- MUMFORD_STYLE.md
- WRITING_MANIFESTO.md

**docs/roadmap/ (3):**
- REFACTORING_ROADMAP.md
- RESEARCH_ROADMAP.json
- RESEARCH_ROADMAP_QUICK_REFERENCE.md

**scripts/analysis/ (4):**
- run-component-type-test.sh
- run-composite-connection-test.sh
- run-isolated-validator-test.sh
- run-manual-validator-test.sh

### Files Staying in Root (6)
- README.md
- CONTRIBUTING.md
- CLAUDE.md (project config)
- LICENSE
- pom.xml
- .gitignore

---

**Total: 120 migrated + 6 root = 126 files**

