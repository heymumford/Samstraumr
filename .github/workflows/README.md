# GitHub Actions Workflows

**Last Updated**: 2025-11-19
**Status**: Optimized and Consolidated

---

## Quick Reference

### Active Workflows (9 Total)

| Workflow | Purpose | Triggers | Duration | Blocking |
|----------|---------|----------|----------|----------|
| **ci-main.yml** | Primary CI pipeline | Every push/PR to main | 15-25 min | ✅ Yes |
| **ci-fast.yml** | Fast feedback loop | Every push/PR | 5-10 min | ✅ Yes |
| **ci-security.yml** | Security scanning | Push/PR + weekly | 15-25 min | ✅ Yes |
| **documentation-ci.yml** | Docs validation | Doc changes, releases | 15-25 min | ⚠️ Docs only |
| **ci-scheduled.yml** | Weekly comprehensive | Sunday 2 AM UTC | 45-60 min | ❌ No |
| **port-performance.yml** | Performance tests | Port interface changes | 20-30 min | ⚠️ Perf only |
| **s8r-structure-pipeline.yml** | CLI validation | s8r* file changes | 10-15 min | ⚠️ CLI only |
| **pages.yml** | Deploy to Pages | Main branch (docs) | 5-10 min | ❌ No |
| **local-pipeline.yml** | Manual testing | Manual dispatch | 10-15 min | ❌ No |

---

## For Developers

### Before You Commit

```bash
# Install pre-commit hooks (one-time setup)
pip install pre-commit
pre-commit install

# Run fast local checks
./scripts/local-quality-check.sh --fast
```

### Before You Push

```bash
# Run comprehensive local checks
./scripts/local-quality-check.sh --full

# Auto-fix formatting issues
./scripts/local-quality-check.sh --fix
```

### Understanding CI Checks

When you create a PR, these checks run:
1. **ci-fast** (5-10 min) - Quick validation
2. **ci-main** (15-25 min) - Comprehensive testing
3. **ci-security** (15-25 min) - Security scanning

All three must pass before merge.

---

## Recent Changes (2025-11-19)

### ✅ Removed (Redundant)
- `codeql.yml` → Merged into `ci-security.yml`
- `ci-documentation.yml` → Replaced by `documentation-ci.yml`
- `build.yml` → Superseded by `ci-fast.yml`

### 📈 Improvements
- **47% fewer workflows** (17 → 9 planned)
- **40% faster CI** (~250 min → ~150 min daily)
- **50% faster PR feedback** (30-45 min → 15-25 min)
- **0 duplicate jobs** (was 6+)

---

## Documentation

See comprehensive documentation at [docs/CI_CD_STRATEGY.md](../../docs/CI_CD_STRATEGY.md)
