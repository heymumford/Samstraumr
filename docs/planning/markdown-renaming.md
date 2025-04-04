# Markdown Renaming

This document outlines the plan for standardizing the naming of Markdown (.md) files in the Samstraumr project.

## Naming Conventions

According to our file organization standards:

1. **README.md** files should remain in UPPER_CASE
2. **CLAUDE.md** should remain in UPPER_CASE as a special project file
3. Other documentation files should follow PascalCase naming convention

## Files to Rename

### Root level

- CLAUDE.md - (keep as is)
- README.md - (keep as is)

### Docs/ directory

#### All-caps files to rename to pascalcase

- DOCUMENTATION_STANDARDS.md → DocumentationStandards.md
- JAVA_NAMING_STANDARDS.md → JavaNamingStandards.md
- LOGGING_STANDARDS.md → LoggingStandards.md
- FOLDER_STRUCTURE.md → FolderStructure.md

#### Documentation files needing consistency

- ATL-BTL-Strategy.md → ATLBTLStrategy.md (ATL and BTL are acronyms that should be uppercase)
- BundleToCompositeRefactoring.md → (Already PascalCase, keep)
- CompositesAndMachines.md → (Already PascalCase, keep)
- CoreConcepts.md → (Already PascalCase, keep)
- FAQ.md → (Already PascalCase, keep)
- Glossary.md → (Already PascalCase, keep)
- GettingStarted.md → (Already PascalCase, keep)
- Migration.md → (Already PascalCase, keep)
- StateManagement.md → (Already PascalCase, keep)
- SystemsTheoryFoundation.md → (Already PascalCase, keep)
- Testing-Strategy-TBD.md → TestingStrategyTbd.md
- Testing.md → (Already PascalCase, keep)
- TestingStrategy.md → (Already PascalCase, keep)
- TubePatterns.md → (Already PascalCase, keep)

#### Docs/compatibility/

- COMPATIBILITY_FIXES.md → CompatibilityFixes.md
- COMPATIBILITY_REPORT.md → CompatibilityReport.md

#### Docs/planning/

- DOCUMENTATION_PLAN.md → DocumentationPlan.md
- FILE_REORGANIZATION_PROGRESS.md → FileReorganizationProgress.md
- FOLDER_REFACTORING_PLAN.md → FolderRefactoringPlan.md
- README-IMPLEMENTATION.md → ReadmeImplementation.md
- README-NEW-DRAFT.md → ReadmeNewDraft.md
- SCRIPT_REORGANIZATION.md → ScriptReorganization.md

#### Docs/testing/

- TEST_STANDARDIZATION.md → TestStandardization.md

#### Docs/contribution/

- WORKFLOW_ANALYSIS.md → WorkflowAnalysis.md

### Util/ directory

- VERSION_SCRIPTS.md → VersionScripts.md (in keeping with PascalCase convention)
- README.md - (keep as is)

## Implementation Steps

1. Create backup of important files
2. Systematically rename files according to the plan
3. Update references to these files in other documents
4. Test links to ensure proper navigation
