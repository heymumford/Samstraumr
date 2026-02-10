# Target Directory Structure: Samstraumr Root Cleanup

**Purpose:** Complete architecture for reorganizing 112 .md files, 10 .json files, and 4 .sh scripts from root into proper documentation and script directories.

**Status:** Design specification (Phase 1, Agent 3)

**Date:** 2026-02-10

---

## Executive Summary

| Metric | Target |
|--------|--------|
| **Root files** | 6 (README.md, CONTRIBUTING.md, SUPPORTING.md, LICENSE, pom.xml, .gitignore) |
| **Organized files** | 112 .md + 10 .json → docs/ |
| **Migrated scripts** | 4 .sh → scripts/ |
| **Folder cleanup** | Remove empty dirs, deduplicate overlapping structures |
| **Total docs folders** | ~18 (consolidated from current 30+) |

---

## Complete Target Tree

```
Samstraumr/
│
├── README.md                          # Public: Project overview
├── CONTRIBUTING.md                    # Public: Contribution guidelines
├── SUPPORTING.md                      # Public: Support, security, conduct
├── LICENSE                            # (existing)
├── pom.xml                            # (existing)
├── .gitignore                         # (existing)
├── .github/                           # (existing)
│   └── workflows/
│       ├── samstraumr-pipeline.yml
│       └── ...
│
├── bin/                               # (existing) Shell entry points
│   ├── s8r-build
│   ├── s8r-test
│   └── ...
│
├── util/                              # (existing) Build utilities
│   ├── lib/
│   │   └── unified-common.sh
│   └── ...
│
├── scripts/                           # CONSOLIDATED → Analysis & Admin scripts
│   ├── README.md                      # Index of all scripts
│   ├── analysis/                      # NEW → Analysis automation
│   │   ├── run-component-type-test.sh # (migrated from root)
│   │   ├── run-composite-connection-test.sh
│   │   ├── run-isolated-validator-test.sh
│   │   └── run-manual-validator-test.sh
│   └── admin/                         # NEW → Admin/ops scripts
│       └── [scripts for releases, CI optimization, cleanup]
│
├── modules/                           # (existing) Maven module sources
│   ├── samstraumr-core/
│   │   └── src/
│   │       ├── main/java/org/s8r/
│   │       └── test/java/org/s8r/test/
│   └── ...
│
├── quality-tools/                     # (existing) Quality configs
│   ├── checkstyle.xml
│   ├── spotbugs-excludes.xml
│   └── ...
│
└── docs/                              # EXPANDED → Master documentation hub
    │
    ├── README.md                      # Master index → links all sections
    │
    ├── 0-START/                       # NEW → Quick start + onboarding
    │   ├── README.md
    │   ├── START_HERE.md              (migrated from root)
    │   ├── START_HERE_PHASE1.md
    │   ├── START_NOW.md
    │   ├── QUICK_START_GUIDE.md
    │   ├── QUICK_REFERENCE_CARD.md
    │   └── AGENTS.md                  (Developer persona guide)
    │
    ├── architecture/                  # (existing, consolidated)
    │   ├── README.md
    │   ├── CLEAN_ARCHITECTURE.md      (main reference)
    │   ├── decisions/                 # ADRs
    │   │   └── [decision logs]
    │   ├── clean/                     # Clean architecture specifics
    │   │   └── [boundary rules, layer design]
    │   ├── adapter-patterns/
    │   ├── reports/
    │   └── component-lifecycle.md     (state machine docs)
    │
    ├── concepts/                      # (existing) Domain language & theory
    │   ├── README.md
    │   ├── biological-models/
    │   ├── consciousness/             # Consciousness modeling
    │   ├── genealogy/                 # Identity system
    │   ├── port-driven-design/        # Hexagonal architecture
    │   └── emergent-systems/          # Complex systems theory
    │
    ├── research/                      # CONSOLIDATED → All research artifacts
    │   ├── README.md
    │   ├── analysis/                  # Analysis artifacts
    │   │   ├── ARCHITECTURE_NOVELTY_ANALYSIS.md    (migrated)
    │   │   ├── DDD_ANALYSIS_SUMMARY.md
    │   │   ├── COGNITIVE_LOAD_METRICS.md
    │   │   ├── MARKET_RESEARCH_SUMMARY.md
    │   │   ├── RESEARCH_ANALYSIS_INDEX.md
    │   │   └── ddd-analysis-report.json
    │   │
    │   ├── debates/                   # Adversarial/debate outputs
    │   │   ├── ADVERSARIAL_DEBATE_SUMMARY.md
    │   │   ├── ADVERSARIAL_TESTS_SUMMARY.md
    │   │   ├── DEBATE_INDEX.md
    │   │   ├── DEBATE_SUMMARY.md
    │   │   ├── DEBATE_EVOLUTION_VS_COGNITION.json
    │   │   ├── DEBATE_VISUAL_SUMMARY.md
    │   │   ├── METAPHOR_ANALYSIS_INDEX.md
    │   │   ├── METAPHOR_ANALYSIS_SUMMARY.md
    │   │   ├── METAPHOR_CODE_EVIDENCE.md
    │   │   ├── METAPHOR_EPISTEMIC_ANALYSIS.json
    │   │   ├── METAPHOR_QUICK_REFERENCE.md
    │   │   ├── METAPHOR_REFACTORING_GUIDE.md
    │   │   ├── ddd-vs-testing-debate.json
    │   │   └── README_ADVERSARIAL_DEBATE.md
    │   │
    │   ├── expert-synthesis/          # Expert panel + iterations
    │   │   ├── EXPERT_PANEL_SYNTHESIS_MASTER.md
    │   │   ├── iterations/
    │   │   │   ├── ITERATION_1_EXECUTIVE_SUMMARY.md
    │   │   │   ├── ITERATION_1_INDEX.md
    │   │   │   ├── ITERATION_1_AGENT_CRITIQUES.json
    │   │   │   ├── ITERATION_2_EXECUTIVE_SUMMARY.md
    │   │   │   ├── ITERATION_2_HANDOFF_BRIEF.md
    │   │   │   ├── ITERATION_3_EXECUTIVE_SUMMARY.md
    │   │   │   ├── ITERATION_3_INDEX.md
    │   │   │   └── RESOURCE_ALLOCATION_ITERATION_2.md
    │   │   └── VALUE_FRAMEWORK_ITERATION_1.md
    │   │
    │   ├── rankings/                  # Research prioritization
    │   │   ├── FINAL_RESEARCH_RANKINGS.md
    │   │   ├── RESEARCH_OPPORTUNITIES.md
    │   │   ├── RESEARCH_SYNTHESIS_REPORT.md
    │   │   └── RESEARCH_ROADMAP_SYNTHESIS.md
    │   │
    │   └── evidence/                  # Evidence collection
    │       ├── EVIDENCE_SUMMARY.md
    │       ├── DEVELOPER_COGNITION_ANALYSIS.json
    │       ├── COMPARATIVE_ARCHITECTURE_ANALYSIS.json
    │       └── findings_catalog.md
    │
    ├── planning/                      # CONSOLIDATED → Project execution
    │   ├── README.md
    │   ├── phases/                    # Phase-based planning
    │   │   ├── phase-1/               # Phase 1 outputs
    │   │   │   ├── PHASE_1_ELITE_SYSTEM_INDEX.md
    │   │   │   ├── PHASE_1_ELITE_EXECUTION_DETAILS.md
    │   │   │   ├── PHASE_1_EXECUTION_CHECKLIST.md
    │   │   │   ├── PHASE_1_EXECUTION_STARTED.txt
    │   │   │   ├── PHASE_1_FINAL_REFINED_PLAN.md
    │   │   │   ├── PHASE_1_LIVE_EXECUTION_LOG.md
    │   │   │   ├── PHASE_1_GO_NO_GO_ANALYSIS.md
    │   │   │   ├── PHASE_1_QUANTIFIED_RISK_ANALYSIS.md
    │   │   │   ├── PHASE_1_BACKUP_PLAYBOOKS.md
    │   │   │   ├── PHASE_1_DECISION_TREES.md
    │   │   │   ├── PHASE_1_REAL_TIME_DASHBOARD_TEMPLATE.md
    │   │   │   │
    │   │   │   ├── hiring/            # Recruitment
    │   │   │   │   ├── PHASE_1_HIRING_MARKET_INTELLIGENCE.md
    │   │   │   │   ├── PHASE_1_INTERVIEW_SCORECARD.md
    │   │   │   │   ├── PHASE_1_TALENT_POSITIONING_STRATEGY.md
    │   │   │   │   └── RECRUITER_EMAIL_TEMPLATES.md
    │   │   │   │
    │   │   │   ├── budget/            # Financial
    │   │   │   │   ├── PHASE_1_BUDGET_SUMMARY.md
    │   │   │   │   ├── PHASE_1_FINANCIAL_MODELING.md
    │   │   │   │   └── PHASE_1_CONTRACTOR_SCOPE_DOCUMENTS.md
    │   │   │   │
    │   │   │   ├── contracts/         # Legal
    │   │   │   │   └── PHASE_1_CONTRACT_TEMPLATE.md
    │   │   │   │
    │   │   │   ├── roles/             # Staffing
    │   │   │   │   ├── PHASE_1_COLLABORATOR_JOB_DESCRIPTION.md
    │   │   │   │   ├── PHASE_1_COLLABORATOR_HARNESS_CALIBRATED.md
    │   │   │   │   ├── PHASE_1_PRODUCTOWNER_ANALYSIS.md
    │   │   │   │   └── COLLABORATOR_JOB_DESCRIPTION.md
    │   │   │   │
    │   │   │   └── comms/             # Communications
    │   │   │       ├── PHASE_1_PRE_WRITTEN_COMMUNICATIONS.md
    │   │   │       └── KICKOFF_MEETING_SLIDES.md
    │   │   │
    │   │   ├── phase-2/               # Phase 2 outputs
    │   │   │   ├── ITERATION_2_LAUNCH_CHECKLIST.md
    │   │   │   └── [synthesis results]
    │   │   │
    │   │   └── phase-3/               # Phase 3 implementation
    │   │       ├── IMPLEMENTATION_PLAN_ITERATION_3.md
    │   │       └── AGENTIC_IMPLEMENTATION_GUIDE.md
    │   │
    │   ├── active/                    # Active execution tracking
    │   │   ├── task_plan.md           (main task tracking)
    │   │   ├── task_plan_phase1_tdd.md
    │   │   ├── phase_tracker.md       (cross-phase tracker)
    │   │   ├── WEEK_0_ACTIVE_PLAN.md
    │   │   ├── WEEK_0_EXECUTION_TRACKER.md
    │   │   ├── WEEK_0_READINESS_SUMMARY.md
    │   │   ├── WEEKEND_CHECKLIST.md
    │   │   ├── ACTION_NOW.md          (daily priorities)
    │   │   ├── EXECUTE_TODAY.md
    │   │   ├── START_NOW.md
    │   │   └── QUICK_START_GUIDE.md
    │   │
    │   ├── completed/                 # Archived execution
    │   │   ├── ITERATION_1_COMPLETE.txt
    │   │   └── [completed phases]
    │   │
    │   ├── feature-tracking/          # Feature management
    │   │   ├── feature_tracking_sheet.md
    │   │   ├── FEATURE_TRACKING_SHEET.csv
    │   │   ├── VALIDATION_TASKS.md
    │   │   ├── SUCCESS_CRITERIA.md
    │   │   ├── GO_CHECKLIST.md
    │   │   ├── GO_NO_GO_DECISION_TREE.md
    │   │   └── GATE_DECISION_FRAMEWORK.md
    │   │
    │   └── decision-logs/             # Major decisions
    │       ├── DECISION_ENGINE.md
    │       ├── POST_MERGE_CLEANUP.md
    │       └── [ADR-style entries]
    │
    ├── guides/                        # (existing) How-to documentation
    │   ├── README.md
    │   ├── development.md
    │   ├── testing/                   # Testing guides
    │   ├── migration/
    │   ├── tdd/
    │   └── ...
    │
    ├── testing/                       # (existing) Test framework docs
    │   ├── README.md
    │   ├── test-pyramid.md            (L0-L3 documentation)
    │   ├── lifecycle-test/
    │   ├── features/
    │   ├── examples/
    │   └── ...
    │
    ├── reference/                     # (existing) API & standards
    │   ├── README.md
    │   ├── standards/                 (coding standards, naming, etc)
    │   │   ├── MUMFORD_STYLE.md       (writing standards)
    │   │   └── [java-naming, documentation-standards]
    │   ├── security/
    │   │   ├── SECURITY.md
    │   │   └── ...
    │   ├── release/
    │   │   ├── CHANGELOG_COMMANDS.md
    │   │   └── ...
    │   ├── mcp-reference.md           (MCP server mapping)
    │   └── ...
    │
    ├── dev/                           # (existing) Development setup
    │   ├── README.md
    │   ├── setup.md
    │   ├── environment.md
    │   ├── ci/
    │   │   ├── CI_CD_STRATEGY.md
    │   │   ├── ci-workflow-analysis-and-refactoring.md
    │   │   ├── CI-OPTIMIZATION-README.md
    │   │   ├── CI-OPTIMIZATION-CHANGES.md
    │   │   ├── JUNIT6_MIGRATION_PLAN.md
    │   │   └── ...
    │   └── ...
    │
    ├── publication/                   # NEW → Research publication pathway
    │   ├── README.md
    │   ├── PUBLICATION_STRATEGY.md
    │   ├── PUBLICATION_STRATEGY_README.md
    │   ├── PUBLICATION_VENUES_CHECKLIST.md
    │   ├── PUBLICATION_STRATEGY.json
    │   └── papers/
    │       └── [draft papers, submissions]
    │
    ├── roadmap/                       # (existing) Long-term planning
    │   ├── RESEARCH_ROADMAP.json
    │   ├── RESEARCH_ROADMAP_QUICK_REFERENCE.md
    │   ├── REFACTORING_ROADMAP.md
    │   └── ...
    │
    ├── tools/                         # (existing) Tool documentation
    │   ├── README.md
    │   └── [gradle, maven, docker configs]
    │
    ├── site/                          # (existing) Website/documentation site
    │   ├── markdown/
    │   └── xml/
    │
    ├── reports/                       # (existing) Analysis reports
    │   ├── test-reports/
    │   └── ...
    │
    ├── contrib/                       # CONSOLIDATE → Contribution guidance
    │   ├── README.md
    │   ├── CONTRIBUTING.md → (migrate to root)
    │   ├── CODE_OF_CONDUCT.md → (migrate to root)
    │   └── git-hooks/
    │
    ├── cleanup/                       # NEW → This reorganization
    │   ├── README.md
    │   ├── target-structure.md        (this document)
    │   ├── migration-plan.md          (file-by-file mappings)
    │   ├── validation-checklist.md
    │   └── completion-report.md
    │
    ├── archive/                       # (existing) Old analysis cycles
    │   ├── proposals-2024-2025/
    │   ├── testing-legacy-tube/
    │   └── ...
    │
    ├── MANIFEST.md                    # NEW → Master file manifest
    ├── documentation-standards.md     # (existing quality)
    └── redirection-notice.md          # (existing) Broken links notice
```

---

## File Migration Map

### Root Files (STAYS IN ROOT)

| File | Destination | Rationale |
|------|-------------|-----------|
| `README.md` | Root | Public entry point (REQUIRED) |
| `CONTRIBUTING.md` | Root | GitHub detects automatically |
| `CODE_OF_CONDUCT.md` | Root (as SUPPORTING.md) | GitHub community profile |
| `SECURITY.md` | Root | GitHub security policy |
| `LICENSE` | Root | (existing) |
| `pom.xml` | Root | (existing) |
| `.gitignore` | Root | (existing) |

### Core Documentation Files

#### Start/Onboarding (→ docs/0-START/)
- START_HERE.md
- START_HERE_PHASE1.md
- START_NOW.md
- QUICK_START_GUIDE.md
- QUICK_REFERENCE_CARD.md
- AGENTS.md

#### Architecture (→ docs/architecture/)
- Component lifecycle documentation (state machine)
- Clean Architecture rules + verification

#### Research/Analysis (→ docs/research/)

**analysis/:**
- ARCHITECTURE_NOVELTY_ANALYSIS.md
- DDD_ANALYSIS_SUMMARY.md
- COGNITIVE_LOAD_METRICS.md
- MARKET_RESEARCH_SUMMARY.md
- RESEARCH_ANALYSIS_INDEX.md
- ddd-analysis-report.json

**debates/:**
- ADVERSARIAL_DEBATE_SUMMARY.md
- ADVERSARIAL_TESTS_SUMMARY.md
- DEBATE_INDEX.md
- DEBATE_SUMMARY.md
- DEBATE_VISUAL_SUMMARY.md
- DEBATE_EVOLUTION_VS_COGNITION.json
- METAPHOR_* (all 6 files)
- ddd-vs-testing-debate.json
- README_ADVERSARIAL_DEBATE.md

**expert-synthesis/:**
- EXPERT_PANEL_SYNTHESIS_MASTER.md
- ITERATION_1_EXECUTIVE_SUMMARY.md
- ITERATION_1_INDEX.md
- ITERATION_1_AGENT_CRITIQUES.json
- ITERATION_2_EXECUTIVE_SUMMARY.md
- ITERATION_2_HANDOFF_BRIEF.md
- ITERATION_3_EXECUTIVE_SUMMARY.md
- ITERATION_3_INDEX.md
- RESOURCE_ALLOCATION_ITERATION_2.md
- VALUE_FRAMEWORK_ITERATION_1.md

**rankings/:**
- FINAL_RESEARCH_RANKINGS.md
- RESEARCH_OPPORTUNITIES.md
- RESEARCH_SYNTHESIS_REPORT.md
- RESEARCH_ROADMAP_SYNTHESIS.md

**evidence/:**
- EVIDENCE_SUMMARY.md
- DEVELOPER_COGNITION_ANALYSIS.json
- COMPARATIVE_ARCHITECTURE_ANALYSIS.json
- findings_catalog.md

#### Planning (→ docs/planning/)

**phases/phase-1/:**
- PHASE_1_ELITE_SYSTEM_INDEX.md
- PHASE_1_ELITE_EXECUTION_DETAILS.md
- PHASE_1_EXECUTION_CHECKLIST.md
- PHASE_1_FINAL_REFINED_PLAN.md
- PHASE_1_LIVE_EXECUTION_LOG.md
- PHASE_1_GO_NO_GO_ANALYSIS.md
- PHASE_1_QUANTIFIED_RISK_ANALYSIS.md
- PHASE_1_BACKUP_PLAYBOOKS.md
- PHASE_1_DECISION_TREES.md
- PHASE_1_REAL_TIME_DASHBOARD_TEMPLATE.md

**phases/phase-1/hiring/:**
- PHASE_1_HIRING_MARKET_INTELLIGENCE.md
- PHASE_1_INTERVIEW_SCORECARD.md
- PHASE_1_TALENT_POSITIONING_STRATEGY.md
- RECRUITER_EMAIL_TEMPLATES.md

**phases/phase-1/budget/:**
- PHASE_1_BUDGET_SUMMARY.md
- PHASE_1_FINANCIAL_MODELING.md
- PHASE_1_CONTRACTOR_SCOPE_DOCUMENTS.md

**phases/phase-1/contracts/:**
- PHASE_1_CONTRACT_TEMPLATE.md

**phases/phase-1/roles/:**
- PHASE_1_COLLABORATOR_JOB_DESCRIPTION.md
- PHASE_1_COLLABORATOR_HARNESS_CALIBRATED.md
- PHASE_1_PRODUCTOWNER_ANALYSIS.md
- COLLABORATOR_JOB_DESCRIPTION.md

**phases/phase-1/comms/:**
- PHASE_1_PRE_WRITTEN_COMMUNICATIONS.md
- KICKOFF_MEETING_SLIDES.md

**phases/phase-2/:**
- ITERATION_2_LAUNCH_CHECKLIST.md

**phases/phase-3/:**
- IMPLEMENTATION_PLAN_ITERATION_3.md
- AGENTIC_IMPLEMENTATION_GUIDE.md

**active/:**
- task_plan.md
- task_plan_phase1_tdd.md
- phase_tracker.md
- WEEK_0_ACTIVE_PLAN.md
- WEEK_0_EXECUTION_TRACKER.md
- WEEK_0_READINESS_SUMMARY.md
- WEEKEND_CHECKLIST.md
- ACTION_NOW.md
- EXECUTE_TODAY.md

**feature-tracking/:**
- feature_tracking_sheet.md
- FEATURE_TRACKING_SHEET.csv
- VALIDATION_TASKS.md
- SUCCESS_CRITERIA.md
- GO_CHECKLIST.md
- GO_NO_GO_DECISION_TREE.md
- GATE_DECISION_FRAMEWORK.md

**decision-logs/:**
- DECISION_ENGINE.md
- POST_MERGE_CLEANUP.md

#### Publication (→ docs/publication/) [NEW]
- PUBLICATION_STRATEGY.md
- PUBLICATION_STRATEGY_README.md
- PUBLICATION_VENUES_CHECKLIST.md
- PUBLICATION_STRATEGY.json

#### Reference (→ docs/reference/standards/)
- MUMFORD_STYLE.md
- Other standards files

#### Development (→ docs/dev/ci/)
- CI_CD_STRATEGY.md
- ci-workflow-analysis-and-refactoring.md
- CI-OPTIMIZATION-README.md
- CI-OPTIMIZATION-CHANGES.md
- JUNIT6_MIGRATION_PLAN.md

#### Roadmap (→ docs/roadmap/)
- RESEARCH_ROADMAP.json
- RESEARCH_ROADMAP_QUICK_REFERENCE.md
- REFACTORING_ROADMAP.md

### Scripts (→ scripts/analysis/)
- run-component-type-test.sh
- run-composite-connection-test.sh
- run-isolated-validator-test.sh
- run-manual-validator-test.sh

### Files Kept in Root (Special Cases)

| File | Decision | Reason |
|------|----------|--------|
| `CLAUDE.md` | Root | Project-specific config (per conventions) |
| `MANIFESTO.md` | Root→docs/concepts/ | Project philosophy (better organized in concepts) |
| `WRITING_MANIFESTO.md` | Root→docs/reference/standards/ | Part of standards |
| `notes.md` | Root→docs/planning/active/ | Active work tracking |

---

## Folder Consolidation Strategy

### REMOVE (Duplicates/Empty)
- `docs/contrib/` — MERGE into `docs/guides/contribution`
- `docs/contribution/` — MERGE into `docs/contrib/` (keep smaller name)
- **Final:** `docs/contrib/` (rename to `docs/contribution/` for clarity)

### REORGANIZE (Too shallow)
- `docs/proposals/` — MERGE into `docs/archive/proposals-2024-2025/`
- `docs/compatibility/` — CONSOLIDATE into `docs/reference/compatibility/`

### EXPAND (Need more structure)
- `docs/planning/` — Add `phases/`, `active/`, `feature-tracking/`, `decision-logs/`
- `docs/research/` — Add `analysis/`, `debates/`, `expert-synthesis/`, `rankings/`, `evidence/`

### CREATE (Missing)
- `docs/publication/` — New (publication pathway)
- `docs/0-START/` — New (quick onboarding)
- `docs/cleanup/` — New (this reorganization)

---

## File Count Analysis

| Category | Count | Destination |
|----------|-------|-------------|
| **Markdown files** | 112 | docs/ + root |
| **JSON files** | 10 | docs/research/, docs/planning/, docs/publication/ |
| **Shell scripts** | 4 | scripts/analysis/ |
| **Root files (stays)** | 6 | Root |
| **Organized in docs/** | 116 | docs/ (across 18+ folders) |

### Folder File Counts (Post-Migration)

| Folder | Est. Count | Type |
|--------|-----------|------|
| docs/0-START/ | 5 | .md |
| docs/research/analysis/ | 7 | .md + .json |
| docs/research/debates/ | 12 | .md + .json |
| docs/research/expert-synthesis/ | 9 | .md + .json |
| docs/research/rankings/ | 4 | .md |
| docs/research/evidence/ | 4 | .md + .json |
| docs/planning/phases/phase-1/ | 28 | .md (deep) |
| docs/planning/active/ | 10 | .md |
| docs/planning/feature-tracking/ | 7 | .md + .csv |
| docs/publication/ | 4 | .md + .json |
| docs/dev/ci/ | 5 | .md |
| docs/roadmap/ | 3 | .md + .json |
| docs/reference/standards/ | 2 | .md |
| scripts/analysis/ | 4 | .sh |
| **Root** | 6 | .md |

---

## Migration Checklist

### Pre-Migration
- [ ] Review target structure with stakeholders
- [ ] Audit all 112 files for categorization accuracy
- [ ] Check for broken internal links (current root → docs/)
- [ ] Verify .gitignore rules (hidden files, caches)
- [ ] Create `docs/cleanup/` folder

### Migration Execution
- [ ] Create new folder structure (dirs, index files)
- [ ] Move files per migration map
- [ ] Create README.md index files in each folder
- [ ] Update internal cross-references
- [ ] Consolidate duplicate folders
- [ ] Create MANIFEST.md (master file inventory)

### Post-Migration
- [ ] Validate all moved files accessible via docs/README.md
- [ ] Check git status (no untracked files)
- [ ] Update CI/CD if any broken paths
- [ ] Verify README.md external links
- [ ] Create migration completion report
- [ ] Clean git history if needed

---

## Index File Templates

### Master README (docs/README.md)

```markdown
# Samstraumr Documentation

## Quick Navigation
- [Getting Started](0-START/) — Project overview + quick start
- [Architecture](architecture/) — Design, decisions, patterns
- [Concepts](concepts/) — Domain language + theory
- [Research](research/) — Analysis, debates, rankings
- [Planning](planning/) — Execution plans + tracking
- [Testing](testing/) — Test pyramid + BDD
- [Guides](guides/) — How-to documentation
- [Reference](reference/) — Standards, API, tools
- [Publication](publication/) — Research publication pathway

## By Role
- **Developer:** [Getting Started](0-START/) → [Architecture](architecture/) → [Guides](guides/)
- **Researcher:** [Research](research/) → [Concepts](concepts/) → [Publication](publication/)
- **Project Manager:** [Planning](planning/) → [Success Criteria](planning/feature-tracking/SUCCESS_CRITERIA.md)
```

### Folder Index Templates
- `docs/research/README.md` — Index: analysis, debates, synthesis, rankings, evidence
- `docs/planning/README.md` — Index: phases, active, feature-tracking, decisions
- Similar for other top-level folders

---

## Success Metrics

| Metric | Target | Verification |
|--------|--------|--------------|
| Root files | ≤ 10 | `ls -1 Samstraumr/ \| grep -E "\.(md\|json\|sh)$"` |
| Docs organized | 116/116 | All 112 .md + 10 .json in docs/ |
| Scripts migrated | 4/4 | All .sh in scripts/ |
| Broken links | 0 | Links validation scan |
| git status | Clean | `git status` shows no untracked files |
| README updated | ✓ | Root README mentions docs/ structure |

---

## Risk Mitigation

| Risk | Mitigation |
|------|-----------|
| **Broken internal links** | Audit + create redirect notice in docs/redirection-notice.md |
| **Git history disruption** | Use `git mv` for tracked files only; untracked → manual move |
| **CI/CD path breaks** | Update CI configs in Phase 2 (Agent 4) |
| **Stakeholder confusion** | Create migration.md explaining what moved where |

---

## Next Steps (Agent 4 - Migrator)

1. Create `docs/cleanup/migration-plan.md` (file-by-file, git commands)
2. Execute file moves with `git mv` (preserve history)
3. Create README/index files in each folder
4. Update root README links
5. Verify no broken references

---

## Document Changes Required at ROOT

### README.md Changes
- Add: "Documentation is organized in [docs/](docs/). [See documentation index](docs/README.md)."
- Update: Links to docs/ structure

### New SUPPORTING.md
Consolidate:
- CODE_OF_CONDUCT.md → SUPPORTING.md (merge)
- SECURITY.md → SUPPORTING.md (merge)
- MANIFESTO.md → reference section

### CONTRIBUTING.md (verify current state)
- Ensure points to docs/contrib/ for detailed guides
- Link to docs/dev/setup.md for environment setup

---

## Conclusion

This structure eliminates root clutter while preserving logical organization across 18+ documented folders. All 112 .md files, 10 .json files, and 4 .sh scripts are mapped to appropriate destinations with clear rationale.

**Result:** Public domain standard (minimal root) + comprehensive internal documentation structure.

