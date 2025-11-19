<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# GitHub Actions Workflows

This directory contains the refactored, streamlined CI/CD workflows for the Samstraumr project.

## 🎯 Current Active Workflows (Refactored Architecture)

### Primary CI Workflows

#### 1. **ci-fast.yml** - Fast CI (Primary Gate) ⚡
**Purpose**: Fast feedback on every PR/push (5-10 minutes)  
**Triggers**: Push to any branch, Pull Request to main  
**Jobs**:
- Quick compile check
- Static analysis (Checkstyle, PMD, SpotBugs)
- Unit tests (parallel)
- Component tests (parallel)
- Architecture verification

**Use this for**: Every code change, fast feedback during development

#### 2. **ci-comprehensive.yml** - Comprehensive CI (Quality Gate) 🎯
**Purpose**: Comprehensive testing for main branch (15-30 minutes)  
**Triggers**: Push to main only, Manual dispatch  
**Jobs**:
- Integration tests
- Composite tests
- Coverage analysis (JaCoCo)
- OWASP dependency security scan
- Package and deploy artifacts

**Use this for**: Main branch validation, deployment preparation

#### 3. **ci-documentation.yml** - Documentation CI 📚
**Purpose**: Documentation validation and deployment (3-5 minutes)  
**Triggers**: Path-based (docs/**, **.md), Manual dispatch  
**Jobs**:
- Markdown link checking
- TODO format validation
- JavaDoc generation
- GitHub Pages deployment (main only)

**Use this for**: Documentation changes, API doc updates

#### 4. **ci-scheduled.yml** - Scheduled CI (Weekly) 📅
**Purpose**: Comprehensive periodic checks (30-60 minutes)  
**Triggers**: Weekly schedule (Sunday 2 AM UTC), Manual dispatch  
**Jobs**:
- Multi-Java testing (Java 21 & 17)
- Performance benchmarks
- Smoke tests (orchestration)
- Dependency update checks

**Use this for**: Weekly health checks, compatibility testing

#### 5. **ci-security.yml** - Security CI 🔒
**Purpose**: Security scanning and vulnerability detection (10-20 minutes)  
**Triggers**: Push/PR to main, Weekly schedule (Thursday 10 AM UTC), Manual dispatch  
**Jobs**:
- CodeQL analysis (Java, JavaScript, Python)
- OWASP dependency vulnerability scan
- Secret scanning (Gitleaks)
- Security advisory checks

**Use this for**: Security validation, vulnerability detection

## 🔧 Composite Actions (Reusable Components)

Located in `.github/actions/`, these provide standardized setup across workflows:

1. **setup-java-21** - Java 21 setup with Maven caching and module options
2. **setup-test-environment** - Complete test environment with parallel execution config
3. **run-quality-checks** - Static analysis suite (Checkstyle, PMD, SpotBugs)
4. **upload-test-results** - Standardized test result and coverage upload

## 📊 Workflow Architecture

```
┌─────────────────────────────────────────┐
│         CI/CD WORKFLOW HIERARCHY         │
├─────────────────────────────────────────┤
│                                          │
│  ON PUSH/PR (Fast - ~5-10 min)         │
│  ┌────────────────────────────────┐    │
│  │ ci-fast.yml (PRIMARY GATE)     │    │
│  │  • Compile + Static Analysis   │    │
│  │  • Unit + Component Tests      │    │
│  │  • Architecture Verification   │    │
│  └────────────────────────────────┘    │
│              ↓ (on main only)           │
│  ┌────────────────────────────────┐    │
│  │ ci-comprehensive.yml           │    │
│  │  • Integration Tests           │    │
│  │  • Coverage + Security         │    │
│  │  • Package + Deploy            │    │
│  └────────────────────────────────┘    │
│                                          │
│  ON PATH CHANGES                         │
│  ┌────────────────────────────────┐    │
│  │ ci-documentation.yml           │    │
│  │  • Validate + Generate         │    │
│  │  • Deploy to Pages             │    │
│  └────────────────────────────────┘    │
│                                          │
│  ON SCHEDULE (Weekly)                    │
│  ┌────────────────────────────────┐    │
│  │ ci-scheduled.yml               │    │
│  │  • Multi-Java Tests            │    │
│  │  • Performance + Smoke Tests   │    │
│  └────────────────────────────────┘    │
│                                          │
│  SECURITY (Push/PR/Schedule)             │
│  ┌────────────────────────────────┐    │
│  │ ci-security.yml                │    │
│  │  • CodeQL + OWASP + Secrets    │    │
│  └────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

## 🗄️ Legacy Workflows (Archived)

The following workflows have been **deprecated** and replaced by the refactored architecture:

| Legacy Workflow | Replaced By | Status |
|----------------|-------------|---------|
| `build.yml` | `ci-fast.yml` | ⚠️ To be archived |
| `s8r-ci.yml` | `ci-fast.yml` + `ci-comprehensive.yml` | ⚠️ To be archived |
| `samstraumr-pipeline.yml` | `ci-fast.yml` | ⚠️ To be archived |
| `full-pipeline.yml` | `ci-scheduled.yml` | ⚠️ To be archived |
| `local-pipeline.yml` | `ci-fast.yml` (workflow_dispatch) | ⚠️ To be archived |
| `smoke-test.yml` | `ci-scheduled.yml` | ⚠️ To be archived |
| `codeql.yml` | `ci-security.yml` | ⚠️ To be archived |
| `documentation-ci.yml` | `ci-documentation.yml` | ⚠️ To be archived |
| `port-performance.yml` | `ci-scheduled.yml` | ⚠️ To be archived |
| `s8r-structure-pipeline.yml` | `ci-fast.yml` (path-based) | ⚠️ To be archived |
| `pages.yml` | `ci-documentation.yml` | ⚠️ To be archived |

## 💡 Benefits of Refactored Architecture

### Before Refactoring
- ❌ 11 workflow files with overlapping responsibilities
- ❌ ~15-20 minutes average PR feedback time
- ❌ ~3 duplicate test runs per push to main
- ❌ High confusion about which workflow is authoritative
- ❌ Duplicate Java/Maven setup in every workflow

### After Refactoring
- ✅ 5 workflow files with clear responsibilities
- ✅ ~5-10 minutes average PR feedback time
- ✅ 1 fast CI run per PR, comprehensive only on main
- ✅ Clear hierarchy and purpose for each workflow
- ✅ Reusable composite actions eliminate duplication

## 🚀 Quick Start Guide

### For Developers
- **Every PR**: Automatically runs `ci-fast.yml` for quick feedback
- **Merge to main**: Automatically runs `ci-fast.yml` + `ci-comprehensive.yml`
- **Documentation changes**: Automatically runs `ci-documentation.yml`

### For Manual Triggers
```bash
# Trigger fast CI manually
gh workflow run ci-fast.yml

# Trigger comprehensive CI manually
gh workflow run ci-comprehensive.yml

# Trigger scheduled tests manually (without waiting for Sunday)
gh workflow run ci-scheduled.yml

# Trigger security scan manually
gh workflow run ci-security.yml
```

## 📋 Maintenance Notes

### Java 21 Configuration
All workflows use Java 21 as the primary version with required module options:
```bash
JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED \
           --add-opens java.base/java.util=ALL-UNNAMED \
           --add-opens java.base/java.lang.reflect=ALL-UNNAMED"
```

### Multi-Java Testing
`ci-scheduled.yml` tests on both Java 21 (primary) and Java 17 (backward compatibility)

### Caching Strategy
Maven packages are cached in all workflows for faster execution:
```yaml
~/.m2/repository
!~/.m2/repository/org/samstraumr
```

## 📖 Related Documentation

- [CI Workflow Analysis and Refactoring Plan](../../docs/ci-workflow-analysis-and-refactoring.md)
- [Design Gap Analysis Report](../../docs/architecture/reports/design-analysis-gaps-report.md)

## 🔄 Migration Status

**Phase**: Implementation Complete ✅  
**Next Steps**: 
1. Test new workflows in parallel with legacy workflows
2. Monitor performance and adjust as needed
3. Archive legacy workflows once validation is complete
4. Update team documentation and communication

---

**Last Updated**: 2025-11-16  
**Refactored By**: Copilot AI Agent  
**Status**: ✅ Active - New Architecture Implemented
