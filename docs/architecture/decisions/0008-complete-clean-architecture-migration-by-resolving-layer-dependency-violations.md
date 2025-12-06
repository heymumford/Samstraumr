# 8. Complete Clean Architecture Migration by Resolving Layer Dependency Violations

Date: 2025-12-06

## Status

Accepted

## Context

Following ADR-0003 (Adopt Clean Architecture) and ADR-0005 (Refactor Package Structure), the Samstraumr codebase has made significant progress toward Clean Architecture compliance. However, automated architecture compliance tests (CleanArchitectureComplianceTest) reveal **6 remaining violations** that prevent full compliance:

### Current Violations

1. **Layer Dependency Violations (3 issues)**
   - Domain validation layer imports from application layer (`ComponentRepository`, `LoggerPort`)
   - Infrastructure config imports from adapter layer (`PortAdapterFactory`)
   - Violates Clean Architecture's Dependency Rule (dependencies should point inward)

2. **Package Documentation (1 issue)**
   - 17 packages missing `package-info.java` files
   - Packages: `component.termination`, `component.lifecycle`, `component.logging`, `application.service.security`, and 13 others

3. **Package Structure (1 issue)**
   - Unexpected top-level `org.s8r.migration` package
   - Should be `org.s8r.infrastructure.migration` per Clean Architecture layers

4. **Infrastructure Implementation (1 issue)**
   - Infrastructure layer not properly implementing application layer interfaces
   - `PortAdapterFactory` has incorrect dependency direction

### Impact

These violations:
- Block CI/CD pipeline (architecture tests fail)
- Create maintenance risk (easy to violate dependency rules)
- Reduce code discoverability (missing package documentation)
- Confuse new developers (unclear package hierarchy)

### Technical Debt

The violations represent **technical debt** from the initial migration (ADR-0005). The infrastructure was partially migrated, but some legacy dependencies were not fully inverted. Specifically:

- **Domain layer** still depends on application layer for validation (should be pure domain logic)
- **Infrastructure layer** still depends on adapter layer (should only depend on application ports)
- **Package documentation** was not systematically added during migration

## Decision

We will **complete the Clean Architecture migration** by resolving all 6 violations through a systematic 4-phase approach:

### Phase 1: Quick Wins (Estimated: 4-6 hours)

**Fix package structure:**
- Move `org.s8r.migration` → `org.s8r.infrastructure.migration`
- Update all imports
- **Resolution**: 1/6 violations fixed

**Add package documentation:**
- Generate `package-info.java` for 17 missing packages
- Use standardized template with package purpose description
- **Resolution**: 2/6 violations fixed

### Phase 2: Dependency Inversion - Domain Layer (Estimated: 12-16 hours)

**Remove domain → application dependencies:**

Current problem:
```java
// domain/validation/ComponentDuplicateDetector.java
import org.s8r.application.port.ComponentRepository;  // VIOLATION
import org.s8r.application.port.LoggerPort;           // VIOLATION
```

Solution:
1. Create pure domain interfaces:
   - `org.s8r.domain.component.ComponentValidator` (replaces ComponentRepository dependency)
   - `org.s8r.domain.logging.DomainLogger` (replaces LoggerPort dependency)

2. Implement adapters in infrastructure:
   - `org.s8r.infrastructure.validation.ComponentValidatorAdapter`
   - `org.s8r.infrastructure.logging.DomainLoggerAdapter`

3. Use dependency injection to wire implementations

**Resolution**: 3/6 violations fixed

### Phase 3: Dependency Inversion - Infrastructure Layer (Estimated: 8-12 hours)

**Remove infrastructure → adapter dependencies:**

Current problem:
```java
// infrastructure/config/PortAdapterFactory.java
import org.s8r.adapter.in.cli.CliAdapter;  // VIOLATION
```

Solution:
1. Move adapter-specific factory logic to adapter layer
2. Infrastructure should only know about application layer ports
3. Use application layer interfaces for all infrastructure dependencies

**Resolution**: 4/6 violations fixed

### Phase 4: Verification & Documentation (Estimated: 4-6 hours)

**Ensure compliance:**
1. Run full architecture test suite (all tests must pass)
2. Add ArchUnit rules to prevent future regressions
3. Update architecture documentation
4. Document migration patterns for future reference

**Resolution**: 6/6 violations fixed + regression prevention

### Total Estimated Effort

- **Time**: 28-40 hours (3.5 to 5 days)
- **Complexity**: MODERATE (clear path, well-defined violations)
- **Risk**: LOW (adapter pattern preserves existing behavior)

## Implementation Strategy

**Incremental approach:**
1. Make changes in small, testable increments
2. Run architecture tests after each phase
3. Commit after each phase completes
4. Use adapter pattern to minimize disruption

**Testing strategy:**
1. Existing unit tests should continue passing (behavior preserved)
2. Architecture tests validate layer boundaries
3. Integration tests verify dependency injection working correctly

**Rollback plan:**
- Each phase is independently committable
- If issues arise, revert to previous phase
- Adapter pattern allows old and new code to coexist temporarily

## Consequences

### Positive

1. **Full Clean Architecture Compliance**
   - All architecture tests pass
   - CI/CD pipeline unblocked
   - Automated governance prevents regressions

2. **Improved Maintainability**
   - Clear dependency directions (always inward)
   - Domain layer is pure business logic (no infrastructure dependencies)
   - Easy to test domain logic in isolation

3. **Better Developer Experience**
   - Package documentation improves code discoverability
   - Consistent package structure reduces cognitive load
   - New developers can navigate codebase with confidence

4. **Technical Debt Elimination**
   - No legacy dependency violations
   - Clean slate for future development
   - Architecture tests catch violations early

### Negative

1. **Short-term Effort**
   - 28-40 hours of focused refactoring work
   - May delay other feature development by 1 sprint

2. **Temporary Code Duplication**
   - Adapter pattern may introduce temporary duplication
   - Will be cleaned up after migration completes

3. **Learning Curve**
   - Team must understand dependency injection patterns
   - Requires discipline to maintain architecture boundaries

### Mitigation

- **Effort**: Schedule as dedicated sprint work (not background task)
- **Duplication**: Document cleanup plan in ADR
- **Learning**: Pair programming during migration, knowledge sharing sessions

## Alternatives Considered

### Alternative 1: Disable Architecture Tests

**Approach**: Comment out failing tests, continue development

**Rejected because:**
- Accumulates technical debt
- No governance against future violations
- Defeats purpose of adopting Clean Architecture (ADR-0003)

### Alternative 2: Gradual Migration Over Multiple Sprints

**Approach**: Fix 1-2 violations per sprint over 3-4 sprints

**Rejected because:**
- CI/CD remains broken for extended period
- Context switching overhead (revisit codebase multiple times)
- Risk of regression between sprints

### Alternative 3: Full Rewrite

**Approach**: Rewrite all violating packages from scratch

**Rejected because:**
- High risk (may introduce bugs)
- Unnecessary (adapter pattern preserves behavior)
- Time-consuming (weeks instead of days)

## References

- [ADR-0003: Adopt Clean Architecture](0003-adopt-clean-architecture-for-system-design.md)
- [ADR-0005: Refactor Package Structure](0005-refactor-package-structure-to-align-with-clean-architecture.md)
- [ADR-0012: Enforce Acyclic Dependencies](0012-enforce-acyclic-dependencies.md)
- [CleanArchitectureComplianceTest.java](../../modules/samstraumr-core/src/test/java/org/s8r/architecture/CleanArchitectureComplianceTest.java)

## Validation Criteria

This ADR is successfully implemented when:

1. ✅ All 15 architecture compliance tests pass
2. ✅ Zero violations in `CleanArchitectureComplianceTest`
3. ✅ CI/CD pipeline passes on main branch
4. ✅ All existing functional tests continue passing
5. ✅ Documentation updated to reflect new patterns
6. ✅ ArchUnit rules prevent future regressions
