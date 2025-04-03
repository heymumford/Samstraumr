# Markdown File Renaming Commands

This document contains the commands to standardize markdown file naming across the project.

## Renaming Strategy
- README.md files remain in UPPER_CASE
- CLAUDE.md remains in UPPER_CASE
- Other documentation files use PascalCase

```bash
# Planning directory
mv -v docs/planning/SCRIPT_REORGANIZATION.md docs/planning/ScriptReorganization.md
mv -v docs/planning/README-IMPLEMENTATION.md docs/planning/ReadmeImplementation.md 
mv -v docs/planning/FILE_REORGANIZATION_PROGRESS.md docs/planning/FileReorganizationProgress.md
mv -v docs/planning/FOLDER_REFACTORING_PLAN.md docs/planning/FolderRefactoringPlan.md
mv -v docs/planning/README-NEW-DRAFT.md docs/planning/ReadmeNewDraft.md
mv -v docs/planning/DOCUMENTATION_PLAN.md docs/planning/DocumentationPlan.md
mv -v docs/planning/tmp-reorg-plan.md docs/planning/TempReorgPlan.md

# Testing directory
mv -v docs/testing/TEST_STANDARDIZATION.md docs/testing/TestStandardization.md
mv -v docs/testing/testing-annotations.md docs/testing/TestingAnnotations.md
mv -v docs/testing/atl-btl-strategy.md docs/testing/AtlBtlStrategy.md
mv -v docs/testing/test-strategy.md docs/testing/TestStrategy.md
mv -v docs/testing/bdd-with-cucumber.md docs/testing/BddWithCucumber.md

# Proposals directory
mv -v docs/proposals/samstraumr-testing-strategy.md docs/proposals/SamstraumrTestingStrategy.md
mv -v docs/proposals/llm-context-composite-tube-proposal.md docs/proposals/LlmContextCompositeTubeProposal.md

# Contribution directory
mv -v docs/contribution/code-standards.md docs/contribution/CodeStandards.md
mv -v docs/contribution/build-report-guide.md docs/contribution/BuildReportGuide.md
mv -v docs/contribution/configuration-standards.md docs/contribution/ConfigurationStandards.md
mv -v docs/contribution/WORKFLOW_ANALYSIS.md docs/contribution/WorkflowAnalysis.md
mv -v docs/contribution/quality-checks.md docs/contribution/QualityChecks.md
mv -v docs/contribution/ci-cd-guide.md docs/contribution/CiCdGuide.md

# Concepts directory
mv -v docs/concepts/state-management.md docs/concepts/StateManagement.md
mv -v docs/concepts/composites-and-machines.md docs/concepts/CompositesAndMachines.md
mv -v docs/concepts/identity-addressing.md docs/concepts/IdentityAddressing.md
mv -v docs/concepts/systems-theory-foundation.md docs/concepts/SystemsTheoryFoundation.md
mv -v docs/concepts/core-concepts.md docs/concepts/CoreConcepts.md

# Research directory
mv -v docs/research/llm-context-proposal.md docs/research/LlmContextProposal.md

# Reference directory
mv -v docs/reference/configuration-reference.md docs/reference/ConfigurationReference.md
mv -v docs/reference/quality-changes.md docs/reference/QualityChanges.md
mv -v docs/reference/folder-structure.md docs/reference/FolderStructure.md

# Reference standards directory
mv -v docs/reference/standards/logging-standards.md docs/reference/standards/LoggingStandards.md
mv -v docs/reference/standards/documentation-standards.md docs/reference/standards/DocumentationStandards.md
mv -v docs/reference/standards/java-naming-standards.md docs/reference/standards/JavaNamingStandards.md
mv -v docs/reference/standards/file-organization.md docs/reference/standards/FileOrganization.md

# Guides directory
mv -v docs/guides/migration/bundle-to-composite-refactoring.md docs/guides/migration/BundleToCompositeRefactoring.md
mv -v docs/guides/tube-patterns.md docs/guides/TubePatterns.md
mv -v docs/guides/composition-strategies.md docs/guides/CompositionStrategies.md
mv -v docs/guides/migration-guide.md docs/guides/MigrationGuide.md 
mv -v docs/guides/getting-started.md docs/guides/GettingStarted.md

# Compatibility directory
mv -v docs/compatibility/COMPATIBILITY_REPORT.md docs/compatibility/CompatibilityReport.md
mv -v docs/compatibility/COMPATIBILITY_FIXES.md docs/compatibility/CompatibilityFixes.md

# Site markdown directory
mv -v docs/site/markdown/build-metrics.md docs/site/markdown/BuildMetrics.md
mv -v docs/site/markdown/build-status.md docs/site/markdown/BuildStatus.md

# Util directory
mv -v util/VERSION_SCRIPTS.md util/VersionScripts.md

# Additional files identified for renaming

# Root directory
mv -v "COMPATIBILITY_FIXES.md" "CompatibilityFixes.md"
mv -v "COMPATIBILITY_REPORT.md" "CompatibilityReport.md"
mv -v "TEST_STANDARDIZATION.md" "TestStandardization.md"

# Reference directory - still need to check if these exist with lowercase names
if [ -f "docs/reference/glossary.md" ]; then
  mv -v "docs/reference/glossary.md" "docs/reference/Glossary.md"
fi

if [ -f "docs/reference/faq.md" ]; then
  mv -v "docs/reference/faq.md" "docs/reference/Faq.md"
fi

# Guides directory
if [ -f "docs/guides/prerequisites.md" ]; then
  mv -v "docs/guides/prerequisites.md" "docs/guides/Prerequisites.md"
fi

# Contribution directory
if [ -f "docs/contribution/contributing.md" ]; then
  mv -v "docs/contribution/contributing.md" "docs/contribution/Contributing.md"
fi

# Site directory
if [ -f "docs/site/markdown/index.md" ]; then
  mv -v "docs/site/markdown/index.md" "docs/site/markdown/Index.md"
fi

# Samstraumr core resources
if [ -f "Samstraumr/samstraumr-core/src/main/resources/version-template.md" ]; then
  mv -v "Samstraumr/samstraumr-core/src/main/resources/version-template.md" "Samstraumr/samstraumr-core/src/main/resources/VersionTemplate.md"
fi

# Check if there are any remaining non-PascalCase files that aren't README.md or CLAUDE.md
echo "Checking for any remaining files that don't follow conventions..."
find . -name "*.md" | grep -v "README.md" | grep -v "CLAUDE.md" | grep -E "[^A-Z][A-Z]|[^a-z][a-z]|_|-"
```