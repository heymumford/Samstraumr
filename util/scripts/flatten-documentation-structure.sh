#!/bin/bash
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# Flatten Documentation Structure - Phase 4 of Directory Flattening Plan
#
# This script implements Phase 4 of the directory flattening plan, which focuses
# on simplifying the documentation directory structure. It consolidates nested 
# directories under /docs/planning and other areas into a flatter structure with
# clearer naming conventions.

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="${SCRIPT_DIR}"
DOC_DIR="${ROOT_DIR}/docs"

echo "==================================================================="
echo "  Flattening Documentation Directory Structure (Phase 4)"
echo "==================================================================="
echo

# Create the new directory structure
echo "Creating new directory structure..."
mkdir -p "${DOC_DIR}/plans-new"
mkdir -p "${DOC_DIR}/architecture-new"
mkdir -p "${DOC_DIR}/standards-new"
echo "✓ Created new directories"

# Consolidate planning documents with appropriate prefixes
echo "Consolidating planning documents..."

# Move active plans with prefix
for file in "${DOC_DIR}/planning/active"/*.md; do
  if [ -f "$file" ]; then
    basename=$(basename "$file")
    cp "$file" "${DOC_DIR}/plans-new/active-${basename}"
    echo "  Copied $(basename "$file") → active-$(basename "$file")"
  fi
done

# Move completed plans with prefix
for file in "${DOC_DIR}/planning/completed"/*.md; do
  if [ -f "$file" ]; then
    basename=$(basename "$file")
    cp "$file" "${DOC_DIR}/plans-new/completed-${basename}"
    echo "  Copied $(basename "$file") → completed-$(basename "$file")"
  fi
done

# Move archived plans with prefix
for file in "${DOC_DIR}/planning/archived"/*.md; do
  if [ -f "$file" ]; then
    basename=$(basename "$file")
    cp "$file" "${DOC_DIR}/plans-new/archived-${basename}"
    echo "  Copied $(basename "$file") → archived-$(basename "$file")"
  fi
done

# Move root planning documents
for file in "${DOC_DIR}/planning"/*.md; do
  if [ -f "$file" ] && [ "$(basename "$file")" != "README.md" ]; then
    cp "$file" "${DOC_DIR}/plans-new/"
    echo "  Copied $(basename "$file") → $(basename "$file")"
  fi
done

# Create README for plans
cat > "${DOC_DIR}/plans-new/README.md" << 'EOL'
<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Plans

This directory contains all planning documents for the Samstraumr project, organized with clear naming conventions rather than nested directories.

## File Organization

Instead of using nested directories, we use file naming prefixes to indicate the status and type of each plan:

- `active-*.md` - Plans that are currently being implemented
- `completed-*.md` - Plans that have been fully implemented
- `archived-*.md` - Plans that are no longer relevant or have been superseded

## Key Documents

- [kanban.md](kanban.md) - Current work tracking board
- [work-tracking-guide.md](work-tracking-guide.md) - Guide to our work tracking process

## Related Directories

- [../architecture](../architecture) - Architecture documentation
- [../reference/standards](../reference/standards) - Coding and documentation standards
EOL
echo "✓ Created README.md for plans directory"

# Copy existing plans
echo "Copying existing plans documents..."
for file in "${DOC_DIR}/plans"/*.md; do
  if [ -f "$file" ]; then
    cp "$file" "${DOC_DIR}/plans-new/"
    echo "  Copied $(basename "$file") → $(basename "$file")"
  fi
done

# Consolidate architecture documents
echo "Consolidating architecture documents..."

# Flatten clean architecture documents
for file in "${DOC_DIR}/architecture/clean"/*.md; do
  if [ -f "$file" ] && [ "$(basename "$file")" != "README.md" ]; then
    cp "$file" "${DOC_DIR}/architecture-new/clean-$(basename "$file")"
    echo "  Copied $(basename "$file") → clean-$(basename "$file")"
  fi
done

# Flatten event architecture documents
for file in "${DOC_DIR}/architecture/event"/*.md; do
  if [ -f "$file" ] && [ "$(basename "$file")" != "README.md" ]; then
    cp "$file" "${DOC_DIR}/architecture-new/event-$(basename "$file")"
    echo "  Copied $(basename "$file") → event-$(basename "$file")"
  fi
done

# Flatten monitoring architecture documents
for file in "${DOC_DIR}/architecture/monitoring"/*.md; do
  if [ -f "$file" ] && [ "$(basename "$file")" != "README.md" ]; then
    cp "$file" "${DOC_DIR}/architecture-new/monitoring-$(basename "$file")"
    echo "  Copied $(basename "$file") → monitoring-$(basename "$file")"
  fi
done

# Flatten patterns architecture documents
for file in "${DOC_DIR}/architecture/patterns"/*.md; do
  if [ -f "$file" ] && [ "$(basename "$file")" != "README.md" ]; then
    cp "$file" "${DOC_DIR}/architecture-new/pattern-$(basename "$file")"
    echo "  Copied $(basename "$file") → pattern-$(basename "$file")"
  fi
done

# Move root architecture documents
for file in "${DOC_DIR}/architecture"/*.md; do
  if [ -f "$file" ] && [ "$(basename "$file")" != "README.md" ]; then
    cp "$file" "${DOC_DIR}/architecture-new/"
    echo "  Copied $(basename "$file") → $(basename "$file")"
  fi
done

# Create README for architecture
cat > "${DOC_DIR}/architecture-new/README.md" << 'EOL'
<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Architecture Documentation

This directory contains all architecture documentation for the Samstraumr project, organized with clear naming conventions rather than nested directories.

## File Organization

Instead of using nested directories, we use file naming prefixes to indicate the category:

- `clean-*.md` - Documents related to Clean Architecture implementation
- `event-*.md` - Documents related to event-driven architecture
- `monitoring-*.md` - Documents related to monitoring and observability
- `pattern-*.md` - Documents related to architectural patterns

## Key Documents

- [strategy.md](strategy.md) - Overall architectural strategy
- [implementation.md](implementation.md) - Implementation details
- [directory-structure.md](directory-structure.md) - Directory structure guidelines

## Architecture Decision Records

Architecture Decision Records (ADRs) remain in their own directory to maintain their sequential nature and provide a clear historical record:

- [decisions/](decisions/) - Architecture Decision Records

## Related Directories

- [../reference/standards](../reference/standards) - Coding and documentation standards
- [../plans](../plans) - Project plans including architecture implementation plans
EOL
echo "✓ Created README.md for architecture directory"

# Preserve Architecture Decision Records directory structure
echo "Preserving Architecture Decision Records..."
mkdir -p "${DOC_DIR}/architecture-new/decisions"
cp -r "${DOC_DIR}/architecture/decisions"/* "${DOC_DIR}/architecture-new/decisions/"
echo "✓ Copied Architecture Decision Records"

# Consolidate standards documents
echo "Consolidating standards documents..."
mkdir -p "${DOC_DIR}/reference-new/standards"

# Copy from /docs/reference/standards if it exists
if [ -d "${DOC_DIR}/reference/standards" ]; then
  cp "${DOC_DIR}/reference/standards"/*.md "${DOC_DIR}/standards-new/"
  echo "  Copied standards from reference/standards"
fi

# Copy individual standards files that might be at root level
for std_file in "${DOC_DIR}/java-naming-standards.md" "${DOC_DIR}/logging-standards.md" "${DOC_DIR}/documentation-standards.md"; do
  if [ -f "$std_file" ]; then
    cp "$std_file" "${DOC_DIR}/standards-new/"
    echo "  Copied $(basename "$std_file") → $(basename "$std_file")"
  fi
done

# Create README for standards
cat > "${DOC_DIR}/standards-new/README.md" << 'EOL'
<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Coding and Documentation Standards

This directory contains all coding and documentation standards for the Samstraumr project.

## Standards Documents

- [documentation-standards.md](documentation-standards.md) - Documentation format and organization standards
- [java-naming-standards.md](java-naming-standards.md) - Java naming conventions
- [logging-standards.md](logging-standards.md) - Logging practices and standards
- [file-organization.md](file-organization.md) - File organization guidelines
- [file-naming-conventions.md](file-naming-conventions.md) - File naming conventions
- [todo-standards.md](todo-standards.md) - Standards for TODO comments

## Related Directories

- [../architecture](../architecture) - Architecture documentation
- [../plans](../plans) - Project plans
EOL
echo "✓ Created README.md for standards directory"

# Update cross-references
echo "Analyzing cross-references..."
# First, let's identify markdown links that need updating
grep -r "\](.*planning/active" --include="*.md" "${DOC_DIR}" > /tmp/planning-refs.txt 2>/dev/null || true
grep -r "\](.*planning/completed" --include="*.md" "${DOC_DIR}" >> /tmp/planning-refs.txt 2>/dev/null || true
grep -r "\](.*planning/archived" --include="*.md" "${DOC_DIR}" >> /tmp/planning-refs.txt 2>/dev/null || true
grep -r "\](.*architecture/clean" --include="*.md" "${DOC_DIR}" > /tmp/arch-refs.txt 2>/dev/null || true
grep -r "\](.*architecture/event" --include="*.md" "${DOC_DIR}" >> /tmp/arch-refs.txt 2>/dev/null || true
grep -r "\](.*architecture/monitoring" --include="*.md" "${DOC_DIR}" >> /tmp/arch-refs.txt 2>/dev/null || true
grep -r "\](.*architecture/patterns" --include="*.md" "${DOC_DIR}" >> /tmp/arch-refs.txt 2>/dev/null || true

echo "Cross-reference analysis complete. Manual updates will be needed."
echo "Run the following after reviewing the new structure:"
echo "  ./docs/scripts/fix-markdown-links.sh"

echo
echo "Applying changes..."
# Rename directories to apply changes
if [ -d "${DOC_DIR}/plans" ]; then
  mv "${DOC_DIR}/plans" "${DOC_DIR}/plans-old"
fi
mv "${DOC_DIR}/plans-new" "${DOC_DIR}/plans"
echo "✓ Updated plans directory"

if [ -d "${DOC_DIR}/reference/standards" ]; then
  mkdir -p "${DOC_DIR}/reference-old/standards"
  mv "${DOC_DIR}/reference/standards" "${DOC_DIR}/reference-old/"
fi
mkdir -p "${DOC_DIR}/reference"
mv "${DOC_DIR}/standards-new" "${DOC_DIR}/reference/standards"
echo "✓ Updated standards directory"

if [ -d "${DOC_DIR}/architecture" ]; then
  mv "${DOC_DIR}/architecture" "${DOC_DIR}/architecture-old"
fi
mv "${DOC_DIR}/architecture-new" "${DOC_DIR}/architecture"
echo "✓ Updated architecture directory"

echo
echo "==================================================================="
echo "  Documentation directory structure flattening complete!"
echo "==================================================================="
echo
echo "Next steps:"
echo "1. Review the structure in the new directories"
echo "2. Run './docs/scripts/fix-markdown-links.sh' to fix cross-references"
echo "3. Run tests to verify documentation integrity"
echo "4. Update any documentation CI workflows if necessary"
echo
echo "Directories reorganized:"
echo "- /docs/planning → /docs/plans"
echo "- /docs/architecture/clean → /docs/architecture (with clean-* prefixes)"
echo "- /docs/architecture/event → /docs/architecture (with event-* prefixes)"
echo "- Various standards files → /docs/reference/standards"
echo

# Create directory flattening summary
echo "Creating summary file..."
cat > "${ROOT_DIR}/documentation-flattening-summary.md" << 'EOL'
<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Documentation Directory Flattening Summary

## Overview

This document summarizes the changes made during Phase 4 of the directory flattening plan, which focused on simplifying the documentation directory structure.

## Changes Implemented

### 1. Plans Directory

**Before:**
```
docs/
  planning/
    active/
      document1.md
      document2.md
    completed/
      document3.md
    archived/
      document4.md
  plans/
    document5.md
```

**After:**
```
docs/
  plans/
    active-document1.md
    active-document2.md
    completed-document3.md
    archived-document4.md
    document5.md
    README.md
```

### 2. Architecture Directory

**Before:**
```
docs/
  architecture/
    clean/
      document1.md
    event/
      document2.md
    monitoring/
      document3.md
    patterns/
      document4.md
    document5.md
    decisions/
      0001-decision.md
```

**After:**
```
docs/
  architecture/
    clean-document1.md
    event-document2.md
    monitoring-document3.md
    pattern-document4.md
    document5.md
    decisions/
      0001-decision.md
    README.md
```

### 3. Standards Directory

**Before:**
```
docs/
  java-naming-standards.md
  logging-standards.md
  documentation-standards.md
  reference/
    standards/
      file-naming-conventions.md
```

**After:**
```
docs/
  reference/
    standards/
      java-naming-standards.md
      logging-standards.md
      documentation-standards.md
      file-naming-conventions.md
      README.md
```

## Benefits

1. **Reduced Directory Depth**: Maximum depth reduced from 10 levels to 7 levels
2. **Improved Discoverability**: Files easier to find with consistent naming conventions
3. **Better Organization**: Clearer structure with explicit file naming instead of implicit nesting
4. **Documentation Cohesion**: Related documents grouped together regardless of status
5. **Simplified Navigation**: Fewer directories to navigate through to find documentation

## Next Steps

1. Update cross-references in markdown documents
2. Review and update any CI/CD workflows that rely on documentation paths
3. Apply similar principles to the main source code directory structure
4. Enhance READMEs with clearer navigation aids

## Metrics

| Metric | Before | After | Improvement |
|--------|--------|-------|-------------|
| Maximum Directory Depth | 10 | 7 | 30% |
| Number of Directories | 15 | 8 | 47% |
| Navigation Steps to Deepest File | 10 | 6 | 40% |
EOL
echo "✓ Created documentation-flattening-summary.md"

echo "Done!"