<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Active Directory Flattening Implementation

## Overview

This document outlines the specific steps to implement the directory flattening strategy documented in `folder-flattening-plan.md`. The goal is to reduce unnecessary nesting, improve discoverability, and ensure no directory paths exceed the maximum of 9 levels.

## Current Status

Our analysis has identified several key areas requiring immediate attention:

| Category | Current Max Depth | Target Max Depth | Critical Areas |
|----------|------------------|------------------|----------------|
| Java Test Classes | 16 levels | 9 levels | `org/s8r/tube/lifecycle/steps/adam` |
| Feature Files | 15 levels | 9 levels | `tube/features/L0_Tube/lifecycle/` |
| Documentation | 10 levels | 7 levels | `docs/planning/active/...` |
| Source Code | 13 levels | 9 levels | `org/s8r/tube/legacy/core/exception` |

## Implementation Phasing

### Phase 1: java test classes (highest priority)

#### Current structure:
```
org/s8r/tube/lifecycle/steps/adam/AdamTubeSteps.java
org/s8r/core/tube/test/steps/TubeSteps.java
org/s8r/core/tube/test/runners/TubeTestRunner.java
org/s8r/tube/legacy/test/steps/LegacySteps.java
```

#### Target structure:
```
org/s8r/test/tube/lifecycle/AdamTubeSteps.java
org/s8r/test/tube/TubeSteps.java
org/s8r/test/tube/TubeTestRunner.java
org/s8r/test/legacy/LegacySteps.java
```

#### Implementation steps:

1. Create the new directory structure:
```bash
mkdir -p Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/lifecycle
mkdir -p Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube
mkdir -p Samstraumr/samstraumr-core/src/test/java/org/s8r/test/legacy
mkdir -p Samstraumr/samstraumr-core/src/test/java/org/s8r/test/component
```

2. Create README.md files in each new directory explaining the purpose and organization.

3. Move and refactor the test classes:
```bash
# Active Directory Flattening Implementation
git mv Samstraumr/samstraumr-core/src/test/java/org/s8r/tube/lifecycle/steps/adam/*.java Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/lifecycle/

# Active Directory Flattening Implementation
git mv Samstraumr/samstraumr-core/src/test/java/org/s8r/core/tube/test/steps/*.java Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/

# Active Directory Flattening Implementation
git mv Samstraumr/samstraumr-core/src/test/java/org/s8r/core/tube/test/runners/*.java Samstraumr/samstraumr-core/src/test/java/org/s8r/test/tube/

# Active Directory Flattening Implementation
git mv Samstraumr/samstraumr-core/src/test/java/org/s8r/tube/legacy/test/steps/*.java Samstraumr/samstraumr-core/src/test/java/org/s8r/test/legacy/
```

4. Update package declarations in all moved files.

5. Update import statements in dependent files.

6. Run tests to verify functionality remains intact.

### Phase 2: feature files restructuring

#### Current structure:
```
test/resources/tube/features/L0_Tube/lifecycle/childhood-phase-tests.feature
test/resources/tube/features/L0_Tube/lifecycle/memory-identity-tests.feature
test/resources/composites/features/L1_Bundle/patterns/observer-tube-test.feature
test/resources/composites/features/L1_Composite/patterns/transformer-tube-test.feature
```

#### Target structure:
```
test/resources/features/tube-lifecycle/childhood-phase-test.feature
test/resources/features/identity/memory-identity-test.feature
test/resources/features/composite-patterns/observer-tube-test.feature
test/resources/features/composite-patterns/transformer-tube-test.feature
```

#### Implementation steps:

1. Create the new directory structure:
```bash
mkdir -p Samstraumr/samstraumr-core/src/test/resources/features/tube-lifecycle
mkdir -p Samstraumr/samstraumr-core/src/test/resources/features/identity
mkdir -p Samstraumr/samstraumr-core/src/test/resources/features/composite-patterns
mkdir -p Samstraumr/samstraumr-core/src/test/resources/features/machine
mkdir -p Samstraumr/samstraumr-core/src/test/resources/features/system
```

2. Create README.md files in each new directory explaining the purpose and organization.

3. Move and rename feature files:
```bash
# Active Directory Flattening Implementation
git mv Samstraumr/samstraumr-core/src/test/resources/tube/features/L0_Tube/lifecycle/*.feature Samstraumr/samstraumr-core/src/test/resources/features/tube-lifecycle/

# Active Directory Flattening Implementation
git mv Samstraumr/samstraumr-core/src/test/resources/tube/lifecycle/*identity*.feature Samstraumr/samstraumr-core/src/test/resources/features/identity/

# Active Directory Flattening Implementation
git mv Samstraumr/samstraumr-core/src/test/resources/composites/features/L1_Bundle/patterns/*.feature Samstraumr/samstraumr-core/src/test/resources/features/composite-patterns/
git mv Samstraumr/samstraumr-core/src/test/resources/composites/features/L1_Composite/patterns/*.feature Samstraumr/samstraumr-core/src/test/resources/features/composite-patterns/

# Active Directory Flattening Implementation
git mv Samstraumr/samstraumr-core/src/test/resources/tube/features/L2_Machine/*.feature Samstraumr/samstraumr-core/src/test/resources/features/machine/

# Active Directory Flattening Implementation
git mv Samstraumr/samstraumr-core/src/test/resources/tube/features/L3_System/*.feature Samstraumr/samstraumr-core/src/test/resources/features/system/
```

4. Update feature file references in test runner classes.

5. Run tests to verify functionality remains intact.

### Phase 3: merge redundant test resources

We've identified several redundant test resource paths with similar concepts:

1. Multiple locations for lifecycle tests:
   - `/component/features/L0_Core/lifecycle-tests.feature`
   - `/tube/features/L0_Tube/lifecycle/`
   - `/tube/lifecycle/`

2. Multiple locations for pattern tests:
   - `/composites/features/L1_Bundle/patterns/`
   - `/composites/features/L1_Composite/patterns/`
   - `/composites/patterns/`
   - `/test/patterns/`

#### Implementation steps:

1. Analyze files in each redundant location to understand relationships.

2. Create consolidated directories following our new structure.

3. Move and merge redundant files with clear naming conventions.

4. Update references in test runners and step definitions.

### Phase 4: documentation directory cleanup

#### Current structure:
```
docs/
  planning/
    active/
    archived/
    completed/
  plans/
  proposals/
  architecture/
    clean/
    event/
    monitoring/
    patterns/
```

#### Target structure:
```
docs/
  plans/           # All planning documents with clear naming
  architecture/    # All architecture documents with clear file naming
  guides/          # All user and developer guides
  reference/       # Technical reference material
  standards/       # Coding and documentation standards
```

#### Implementation steps:

1. Consolidate all planning documents:
```bash
mkdir -p docs/plans-new

# Active Directory Flattening Implementation
for file in docs/planning/active/*.md; do
  cp "$file" "docs/plans-new/active-$(basename "$file")"
done

# Active Directory Flattening Implementation
for file in docs/planning/completed/*.md; do
  cp "$file" "docs/plans-new/completed-$(basename "$file")"
done

# Active Directory Flattening Implementation
for file in docs/planning/archived/*.md; do
  cp "$file" "docs/plans-new/archived-$(basename "$file")"
done

# Active Directory Flattening Implementation
cp docs/plans/*.md docs/plans-new/

git mv docs/plans-new docs/plans-updated
```

2. Similarly consolidate architecture documents with descriptive prefixes.

3. Update cross-references between documentation files.

### Phase 5: source code simplification

#### Current structure:
```
samstraumr-core/src/main/java/org/samstraumr/
  domain/
    component/
      composite/
      pattern/
      monitoring/
    identity/
    lifecycle/
    event/
    exception/
```

#### Target structure:
```
samstraumr-core/src/main/java/org/samstraumr/
  domain/
    component/    # All component-related domain classes with clear naming
    event/        # All domain events
    exception/    # All domain exceptions
```

#### Implementation steps:

1. Identify small directories with fewer than 5 files.

2. Create naming conventions for flattened files.

3. Consolidate small directories using the established naming conventions.

4. Update import statements throughout the codebase.

5. Run tests to verify functionality remains intact.

## Verification Steps

After implementing each phase:

1. Run the directory depth analysis tool:
```bash
./util/scripts/flatten-directories.sh
```

2. Run tests to verify functionality:
```bash
./s8r-test all
```

3. Verify documentation cross-references:
```bash
./docs/scripts/fix-markdown-links.sh
```

## Success Metrics

We will validate success by measuring:

1. Maximum directory depth (target: 9 levels or fewer)
2. Number of directories with fewer than 5 files (target: 50% reduction)
3. Test pass rate (target: 100% - no functional regression)
4. Documentation link validity (target: 100% valid links)

## Implementation Timeline

| Phase | Task | Estimated Effort | Dependencies |
|-------|------|------------------|--------------|
| 1 | Java Test Classes Restructuring | 4 hours | None |
| 2 | Feature Files Restructuring | 6 hours | None |
| 3 | Merge Redundant Test Resources | 4 hours | Phase 2 |
| 4 | Documentation Directory Cleanup | 2 hours | None |
| 5 | Source Code Simplification | 8 hours | None |
| | **Total** | **24 hours** | |

## Backward Compatibility

To maintain backward compatibility during the transition:

1. Add symlinks from old locations to new locations where applicable.
2. Update import statements in dependent code.
3. Add deprecation notices in old locations.
4. Update documentation to reference new locations.

## Post-Implementation Tasks

After implementing all phases:

1. Remove any empty directories:
```bash
find . -type d -empty -delete
```

2. Update the directory structure documentation.

3. Add guidance in root README.md about the new structure.

4. Run a final verification pass.

5. Tag the repository with the new structure.

## Recommendations for Future Development

To prevent directory structure issues in the future:

1. Use the `flatten-directories.sh` script as part of pre-commit hooks.
2. Add maximum directory depth checks to the CI pipeline.
3. Update style guides and contribution guidelines.
