# Root Directory Organization Standards Research

**Research Date:** February 10, 2026
**Agent:** Standards Researcher (Agent 2)
**Status:** Complete

---

## Executive Summary

This guide synthesizes industry best practices for root directory organization across three domains: Java frameworks (Spring Boot, Maven, Gradle), open source projects on GitHub, and research projects with analysis components.

**Key Consensus:** Keep root directories minimal. Use root for build configuration, essential metadata, and entry points only. Move all analysis, research, and non-essential files into organized subdirectories.

---

## Best Practices Summary

### 1. **Root Directory is Sacred Space**
The root directory serves one purpose: rapid project orientation. Every file in root must answer "Who is this? How do I build/run it?"

**What belongs in root:**
- Build configuration (pom.xml, settings.gradle.kts, build.gradle.kts)
- Essential metadata (README.md, LICENSE, CONTRIBUTING.md)
- Version control metadata (.git, .gitignore)
- Environment setup (.editorconfig, .pre-commit-config.yaml)

**What does NOT belong:**
- Analysis, research, or planning documents
- Generated reports or build artifacts
- Temporary files or backups
- Historical records or logs

### 2. **Metadata Files Have a Home: .github/**
GitHub and other hosting platforms recognize `.github/` as the designated location for metadata, configuration, and automation.

**Centralizes:**
- Workflows (.github/workflows/*.yml)
- Issue templates (.github/ISSUE_TEMPLATE/)
- Pull request templates (.github/PULL_REQUEST_TEMPLATE.md)
- Configuration (dependabot.yml, codeql.yml)

**Exception:** LICENSE and README.md stay in root because GitHub auto-surfaces them.

### 3. **Organize by Purpose, Not by Artifact Type**
Use top-level directories that reflect project structure:
- `src/` — Application source (Java convention)
- `modules/` — Multi-module projects (Maven/Gradle)
- `docs/` — All documentation
- `tests/` — Test code (when separate from src)
- `scripts/` — Build/deployment scripts
- `config/` — Configuration files

### 4. **Documentation Stays in docs/, Not Root**
Move all documentation into a `docs/` directory with clear subdirectories:

```
docs/
├── README.md (entry point for docs)
├── architecture/
│   ├── ADRs (Architecture Decision Records)
│   ├── C4 diagrams
│   └── Clean Architecture boundaries
├── guides/
│   ├── Contributing guide
│   ├── Development setup
│   └── Testing guide
├── concepts/
│   ├── Domain language
│   ├── Core principles
│   └── System theory
├── dev/
│   ├── TDD process
│   ├── BDD scenarios
│   └── Test pyramid
├── planning/
│   ├── Roadmap
│   ├── Phase plans
│   └── Execution logs
└── reference/
    ├── API reference
    ├── CLI documentation
    └── Configuration reference
```

### 5. **Analysis/Research Files Go to docs/planning/ or docs/analysis/**
For research-heavy projects (like Samstraumr):

```
docs/planning/
├── research-roadmap.md      # Master research plan
├── rankings.md              # Prioritized recommendations
├── implementation-plan.md   # Feature breakdown
├── timeline.md              # Gantt/phase schedule
└── synthesis/
    ├── phase-1-findings.md
    ├── phase-2-findings.md
    └── final-synthesis.md
```

Never create these in root.

---

## Reference Project Examples

### Example 1: Spring Boot (Java Framework)

**Source:** [Spring Boot Official Documentation](https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html)

**Root Structure:**
```
spring-boot-app/
├── .github/
│   ├── workflows/
│   ├── ISSUE_TEMPLATE/
│   └── pull_request_template.md
├── .mvn/                    # Maven wrapper configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.example.myapp/
│   │   │       ├── MyApplication.java      # Main class
│   │   │       ├── customer/
│   │   │       ├── order/
│   │   │       └── config/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/
│       └── resources/
├── docs/                    # Documentation
│   ├── architecture/
│   ├── guides/
│   └── reference/
├── .editorconfig
├── .gitignore
├── .pre-commit-config.yaml
├── pom.xml                  # Single build file in root
├── README.md
├── LICENSE
└── CONTRIBUTING.md
```

**Key Rules:**
- Main application class at package root (e.g., `com.example.myapp.MyApplication`)
- Never use "default package"
- Organize by feature/domain within src/main/java
- Keep root minimal: one pom.xml, entry point docs

**Documentation Principle:** All guides, architecture, and reference material in `docs/`.

---

### Example 2: Maven Standard Directory Layout

**Source:** [Apache Maven - Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)

**Root Structure:**
```
my-app/
├── pom.xml                 # Maven Project Object Model
├── src/
│   ├── main/
│   │   ├── java/          # Source code
│   │   ├── resources/      # Properties, config files
│   │   ├── filters/        # Resource filter files
│   │   ├── webapp/         # Web app sources (if applicable)
│   │   └── assembly/       # Assembly descriptors
│   ├── test/
│   │   ├── java/          # Test code
│   │   ├── resources/      # Test resources
│   │   └── filters/        # Test resource filters
│   ├── it/                # Integration tests
│   └── site/              # Site documentation source
├── target/                # Build output (generated)
├── .mvn/                  # Maven wrapper
├── README.md
├── LICENSE
└── CONTRIBUTING.md
```

**Critical Convention:**
- Maven defines a single, universal structure
- Tools recognize this structure automatically
- `target/` is generated—never commit
- Anything outside `src/` must be explicitly documented

**Research Projects:** Parallel structure with `docs/` alongside `src/`.

---

### Example 3: GitHub Open Source Best Practices

**Source:** [GitHub Repository Structure Best Practices](https://medium.com/code-factory-berlin/github-repository-structure-best-practices-248e6effc405) and [Starting an Open Source Project](https://opensource.guide/starting-a-project/)

**Canonical Root Structure:**
```
awesome-project/
├── README.md              # Auto-surfaced on GitHub homepage
├── LICENSE                # Auto-recognized by GitHub
├── CONTRIBUTING.md        # How to participate
├── CODE_OF_CONDUCT.md     # Community standards
├── SECURITY.md            # Security policy
├── .github/
│   ├── workflows/         # GitHub Actions
│   ├── ISSUE_TEMPLATE/
│   ├── PULL_REQUEST_TEMPLATE.md
│   ├── dependabot.yml
│   └── codeql.yml
├── src/                   # Source code
├── test/                  # Tests
├── docs/                  # Documentation
│   ├── README.md          # Documentation entry point
│   ├── guides/
│   ├── architecture/
│   └── api/
├── .editorconfig          # Editor settings
├── .gitignore
├── .pre-commit-config.yaml
├── build.gradle.kts       # Build configuration
└── scripts/               # Build/deployment scripts
```

**GitHub-Specific Rules:**
- README.md in root: GitHub auto-displays on homepage
- LICENSE in root: GitHub auto-recognizes license
- All automation → .github/workflows/
- Keep root to ≤20 files
- Use lowercase for directory names (except .github)

**Metadata Files:** Move everything possible to `.github/` to keep root clean.

---

## Root Directory Checklist

### What SHOULD Be in Root

- [x] **Build configuration** — pom.xml, build.gradle.kts, settings.gradle.kts
- [x] **README.md** — Project overview and quick start
- [x] **LICENSE** — Legal licensing information
- [x] **CONTRIBUTING.md** — How to contribute
- [x] **.gitignore** — VCS ignore rules
- [x] **.editorconfig** — Editor settings (cross-IDE)
- [x] **.pre-commit-config.yaml** — Pre-commit hooks
- [x] **.github/** — Workflows, templates, automation
- [x] **.mvn/** — Maven wrapper configuration (if using Maven)
- [x] **src/** — Application source code
- [x] **modules/** — Multi-module project structure (if applicable)
- [x] **docs/** — All documentation
- [x] **scripts/** — Build/deployment/utility scripts
- [x] **config/** — Configuration files (if needed)

### What Should NOT Be in Root

- [ ] **Analysis documents** → Move to `docs/planning/` or `docs/analysis/`
- [ ] **Research files** → Move to `docs/planning/research-roadmap.md`
- [ ] **Planning documents** → Move to `docs/planning/`
- [ ] **Execution logs** → Move to `docs/planning/logs/`
- [ ] **Reports** → Move to `docs/planning/reports/` or `docs/analysis/`
- [ ] **Phase summaries** → Move to `docs/planning/phases/`
- [ ] **Debate/comparison files** → Move to `docs/concepts/` or `docs/analysis/`
- [ ] **Meeting notes** → Move to `docs/planning/notes/`
- [ ] **Feature tracking sheets** → Move to `docs/planning/` (link only)
- [ ] **Generated/build artifacts** → Always in `target/` or `.build/`
- [ ] **Backups/archives** → Move to `.archive/` or exclude entirely
- [ ] **Temporary files** → .gitignore and cleanup

---

## Domain-Specific Guidance

### For Java Projects (Maven/Gradle)

**Root Convention:**
```
Root files: pom.xml/settings.gradle.kts + metadata only
Source: src/main/java
Tests: src/test/java
Docs: docs/ (not in root)
```

**Rule:** Everything after `src/` is metadata or support. Documentation, planning, research → `docs/`.

### For Research Projects

**Root Convention (Anti-Pattern Identified):**
```
❌ WRONG: Root filled with analysis files
❌ WRONG: Phase summaries in root
❌ WRONG: Execution logs in root

✓ RIGHT: All research in docs/planning/
✓ RIGHT: Synthesis documents in docs/planning/synthesis/
✓ RIGHT: Root has only: README.md, CONTRIBUTING.md, LICENSE
```

**Recommendation:**
```
docs/planning/
├── research-rankings.md          # Prioritized research
├── implementation-plan.md        # Feature breakdown
├── timeline.md                   # Phase schedule
├── synthesis/
│   ├── phase-1-analysis.md
│   ├── phase-2-expert-panel.md
│   └── phase-3-findings.md
└── logs/
    ├── execution-week-0.md
    ├── execution-week-1.md
    └── decision-log.md
```

**Entry Point in Root:** `README.md` links to `docs/planning/README.md` and key documents via relative links.

### For Open Source/GitHub Projects

**Root Convention:**
```
Essentials: README.md, LICENSE, CONTRIBUTING.md, CODE_OF_CONDUCT.md
Workflows: .github/workflows/
Metadata: .github/ (NOT root)
Docs: docs/
Source: src/
Tests: test/ or src/test/ (framework-specific)
```

**GitHub Auto-Recognition:**
- README.md → Homepage
- LICENSE → License page
- CONTRIBUTING.md → Contribution guidelines
- CODE_OF_CONDUCT.md → Community standards
- .github/workflows/ → Actions

---

## Actionable Cleanup Roadmap for Samstraumr

Based on audit findings, Samstraumr root currently has 130+ files. Industry standard: ≤25 files in root.

### Phase 1: Create Documentation Home (docs/planning/)

```bash
# Create destination directories
mkdir -p docs/planning/research
mkdir -p docs/planning/synthesis
mkdir -p docs/planning/execution
mkdir -p docs/planning/reference
```

### Phase 2: Migrate Analysis Files

**Current (Root):** ANALYSIS_*, DEBATE_*, METAPHOR_*, RESEARCH_*, VALUE_*
**Move to:** `docs/planning/research/` or `docs/planning/synthesis/`

Affected files (sample):
- ANALYSIS_INDEX.md → docs/planning/research/analysis-index.md
- ADVERSARIAL_DEBATE_SUMMARY.md → docs/planning/synthesis/debate-summary.md
- FINAL_RESEARCH_RANKINGS.md → docs/planning/research/rankings.md
- IMPLEMENTATION_PLAN_*.md → docs/planning/implementation/

### Phase 3: Migrate Phase/Iteration Documents

**Current (Root):** PHASE_*, ITERATION_*, WEEK_*, EXECUTION_*
**Move to:** `docs/planning/execution/` with phase subdirectories

```
docs/planning/execution/
├── phase-0/
├── phase-1/
│   ├── budget-summary.md
│   ├── execution-checklist.md
│   ├── execution-log.md
│   └── ... (all PHASE_1_* files)
├── phase-2/
├── phase-3/
└── week-0/
    ├── active-plan.md
    ├── execution-tracker.md
    └── readiness-summary.md
```

### Phase 4: Consolidate Entry Points

**Root Entry:** README.md (update with index)
**Documentation Entry:** docs/README.md (master documentation index)
**Planning Entry:** docs/planning/README.md (master planning index)

Each links to subdirectories: research, synthesis, execution, reference.

### Phase 5: Clean Artifacts

**Remove from root:**
- api-migration-backup/ → .archive/api-migration-backup/
- build-reports/ → target/ or docs/reports/
- test-backup/ → .archive/ or remove if stale
- temp-backup/ → .archive/ or remove

---

## Implementation Verification

After cleanup, verify:

```bash
# Root directory should be clean
ls -la / | wc -l           # Should be ≤30 files/dirs

# All documentation accessible
ls -la docs/
ls -la docs/planning/
ls -la docs/planning/research/
ls -la docs/planning/synthesis/
ls -la docs/planning/execution/

# Build still works
mvn clean verify           # Should succeed

# Git recognizes structure
git ls-files | head -20    # Should show src/, modules/, docs/, etc.
```

---

## Reference Links & Sources

### Java Frameworks & Build Tools
- [Spring Boot Code Structure](https://docs.spring.io/spring-boot/reference/using/structuring-your-code.html)
- [Maven Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)
- [Gradle Organizing Projects](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html)

### Open Source Standards
- [GitHub Repository Structure Best Practices](https://medium.com/code-factory-berlin/github-repository-structure-best-practices-248e6effc405)
- [Starting an Open Source Project](https://opensource.guide/starting-a-project/)
- [Folder Structure Conventions (GitHub)](https://github.com/kriasoft/Folder-Structure-Conventions)

### Research Project Organization
- [Reproducible Data Science: Project Organization](https://ecorepsci.github.io/reproducible-science/project-organization.html)
- [Setting up an Organised Folder Structure for Research Projects](http://www.nikola.me/folder_structure.html)
- [UBC Research Data Management: Directory Structures](https://ubc-library-rc.github.io/rdm/content/04_directory_structures.html)
- [Graduate Institute: Folder Structure Guide](https://libguides.graduateinstitute.ch/rdm/folders/)
- [Project TIER: Root Directory Standards](https://www.projecttier.org/tier-protocol/protocol-4-0/root/)

---

## Consensus Rules (Non-Negotiable)

| Rule | Rationale | Verification |
|------|-----------|--------------|
| **Root ≤ 25 files** | Reduces cognitive load, speeds orientation | `ls -1 \| wc -l` |
| **Build config in root only** | Single source of truth for build | `ls pom.xml settings.gradle*` |
| **All docs in docs/** | Centralized documentation discovery | `ls docs/README.md` |
| **Research in docs/planning/** | Separates analysis from runtime code | `ls docs/planning/` |
| **Metadata in .github/** | GitHub auto-recognition | `ls .github/workflows/` |
| **src/ for code, test/ for tests** | Maven/Gradle convention | `ls src/main/java` |
| **LICENSE & README in root** | GitHub auto-surfaces these | `ls LICENSE README.md` |
| **No generated files in root** | Reduces noise, improves .gitignore | `ls target/ build/` |

---

## Summary

**Industry consensus is unambiguous:** Root directories should be minimal, purposeful, and self-documenting.

For Samstraumr (research + Java framework):
- Keep: pom.xml, .github/, src/, modules/, docs/, scripts/, config/
- Move: All analysis/research/planning → docs/planning/ with clear subdirectories
- Archive: Backups and old branches → .archive/ or exclude entirely

**Result:** A root directory that immediately communicates "Samstraumr is a Java framework project with research components in docs/planning/."

---

**Next Steps:** Agent 1 (Cleanup Lead) will execute this roadmap in parallel phases.

