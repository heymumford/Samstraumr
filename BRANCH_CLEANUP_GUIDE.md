# Branch Cleanup Quick Reference

**Session**: 2025-11-19
**Status**: Ready for PR creation

---

## Quick Start: Create All PRs

### 1. Phase 1: Security & Dependencies (CREATE FIRST)
**Click**: https://github.com/heymumford/Samstraumr/pull/new/claude/phase1-dependencies-014QMV2b1Pnx8qzRNqJn22CC

**Copy this title**:
```
Phase 1: Security & Dependency Updates (9 consolidated updates)
```

**Copy this description** (see docs/branch-consolidation-execution-log.md for full version):
- 9 dependency updates including CRITICAL log4j security fix
- All backward compatible
- No breaking changes

---

### 2. Phase 2: CI Improvements (CREATE AFTER PHASE 1 MERGES)
**Click**: https://github.com/heymumford/Samstraumr/pull/new/claude/phase2-ci-improvements-014QMV2b1Pnx8qzRNqJn22CC

**Copy this title**:
```
Phase 2: CI/CD Pipeline & Build Tool Improvements
```

**Copy this description**:
- CI workflow script path fixes
- Docker security improvements
- Maven plugin updates

---

### 3. Phase 3: Refactoring (CREATE AFTER PHASE 2 MERGES)
**Click**: https://github.com/heymumford/Samstraumr/pull/new/claude/phase3-refactoring-014QMV2b1Pnx8qzRNqJn22CC

**Copy this title**:
```
Phase 3: Component Architecture Refactoring (Phases 1-2)
```

**Copy this description**:
- Martin Fowler refactoring methodology
- Component god class decomposition
- +2,456 insertions, -231 deletions

---

## Cleanup Commands (Run After Each Merge)

### After Phase 1 Merges
```bash
git push origin --delete dependabot/maven/log4j.version-2.25.2
git push origin --delete dependabot/maven/cucumber.version-7.31.0
git push origin --delete dependabot/maven/com.itextpdf-itextpdf-5.5.13.4
git push origin --delete dependabot/maven/org.jetbrains-annotations-26.0.2-1
git push origin --delete dependabot/maven/org.springframework-spring-test-7.0.0
git push origin --delete dependabot/maven/com.github.spotbugs-spotbugs-maven-plugin-4.9.8.1
git push origin --delete dependabot/maven/net.sourceforge.pmd-pmd-java-7.18.0
git push origin --delete dependabot/maven/net.sourceforge.pmd-pmd-javascript-7.18.0
git push origin --delete dependabot/maven/net.sourceforge.pmd-pmd-jsp-7.18.0
git push origin --delete claude/phase1-dependencies-014QMV2b1Pnx8qzRNqJn22CC
```

### After Phase 2 Merges
```bash
git push origin --delete copilot/sub-pr-105
git push origin --delete copilot/sub-pr-105-again
git push origin --delete copilot/sub-pr-55
git push origin --delete copilot/sub-pr-58
git push origin --delete claude/phase2-ci-improvements-014QMV2b1Pnx8qzRNqJn22CC
```

### After Phase 3 Merges
```bash
git push origin --delete claude/code-repository-assessment-01KMgijpgrNW63zo6QJW4CMb
git push origin --delete claude/phase3-refactoring-014QMV2b1Pnx8qzRNqJn22CC
```

### Immediate Cleanup (Incomplete Branches)
```bash
git push origin --delete copilot/sub-pr-88
```

### Final Cleanup
```bash
git push origin --delete claude/branch-consolidation-014QMV2b1Pnx8qzRNqJn22CC
```

---

## What About JUnit 6?

**Deferred to separate PR** - Major version jump (5.x → 6.x) requires dedicated testing.

Create a GitHub issue to track this work separately.

---

## Expected Timeline

- **Phase 1**: ~50 minutes (CI + review)
- **Phase 2**: ~45 minutes (CI + review)
- **Phase 3**: ~50 minutes (CI + review)
- **Total**: ~2.5 hours

---

For detailed information, see `docs/branch-consolidation-execution-log.md`
