#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Script to migrate the remaining documentation files to the new directory structure
# with prefix-based naming conventions

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Ensure required directories exist
mkdir -p "${PROJECT_ROOT}/core"
mkdir -p "${PROJECT_ROOT}/dev"
mkdir -p "${PROJECT_ROOT}/guides"
mkdir -p "${PROJECT_ROOT}/ref"
mkdir -p "${PROJECT_ROOT}/plans"
mkdir -p "${PROJECT_ROOT}/contrib"
mkdir -p "${PROJECT_ROOT}/tools"
mkdir -p "${PROJECT_ROOT}/specs"
mkdir -p "${PROJECT_ROOT}/assets"

# Function to convert a filename to kebab-case with prefix
add_prefix() {
  local filename="$1"
  local prefix="$2"
  local basename
  
  # Get the base filename without directory and extension
  basename=$(basename "$filename")
  extension="${basename##*.}"
  basename="${basename%.*}"
  
  # Remove existing prefixes if present
  basename="${basename#concept-}"
  basename="${basename#guide-}"
  basename="${basename#ref-}"
  basename="${basename#standard-}"
  basename="${basename#tool-}"
  basename="${basename#spec-}"
  basename="${basename#active-}"
  basename="${basename#complete-}"
  basename="${basename#archived-}"
  basename="${basename#contrib-}"
  basename="${basename#test-}"
  
  # Add the prefix
  echo "${prefix}-${basename}.${extension}"
}

# Migration plan for remaining files
echo "Starting migration of files to new structure..."

# Migrate guide files with guide- prefix
echo "Migrating guides..."
for file in "${PROJECT_ROOT}/guides"/*.md; do
  if [ -f "$file" ] && [ "$(basename "$file")" != "README.md" ]; then
    new_name=$(add_prefix "$(basename "$file")" "guide")
    cp "$file" "${PROJECT_ROOT}/guides/${new_name}"
    echo "Migrated $(basename "$file") to guides/${new_name}"
  fi
done

# Migrate reference files with ref- prefix
echo "Migrating reference files..."
for file in "${PROJECT_ROOT}/reference"/*.md; do
  if [ -f "$file" ] && [ "$(basename "$file")" != "README.md" ]; then
    new_name=$(add_prefix "$(basename "$file")" "ref")
    cp "$file" "${PROJECT_ROOT}/ref/${new_name}"
    echo "Migrated $(basename "$file") to ref/${new_name}"
  fi
done

# Migrate general files to appropriate locations based on content
echo "Migrating general files..."
for file in "${PROJECT_ROOT}/general"/*.md; do
  if [ -f "$file" ] && [ "$(basename "$file")" != "README.md" ]; then
    # Determine destination based on content
    content=$(cat "$file" | head -10)
    if grep -q "configuration" <<< "$content"; then
      new_name=$(add_prefix "$(basename "$file")" "guide")
      cp "$file" "${PROJECT_ROOT}/guides/${new_name}"
      echo "Migrated $(basename "$file") to guides/${new_name}"
    else
      new_name=$(add_prefix "$(basename "$file")" "dev")
      cp "$file" "${PROJECT_ROOT}/dev/${new_name}"
      echo "Migrated $(basename "$file") to dev/${new_name}"
    fi
  fi
done

# Migrate proposal files to specs with spec- prefix
echo "Migrating proposals..."
for file in "${PROJECT_ROOT}/proposals"/*.md; do
  if [ -f "$file" ] && [ "$(basename "$file")" != "README.md" ]; then
    new_name=$(add_prefix "$(basename "$file")" "spec")
    cp "$file" "${PROJECT_ROOT}/specs/${new_name}"
    echo "Migrated $(basename "$file") to specs/${new_name}"
  fi
done

# Migrate remaining planning files to plans with appropriate prefixes
echo "Migrating planning files..."
for file in "${PROJECT_ROOT}/planning"/*.md; do
  if [ -f "$file" ] && [ "$(basename "$file")" != "README.md" ]; then
    # Determine state based on filename or content
    state="archived"
    if grep -q "IN PROGRESS" "$file" || grep -q "Status: In Progress" "$file" || grep -q "Active" "$file"; then
      state="active"
    elif grep -q "COMPLETED" "$file" || grep -q "Status: Complete" "$file" || grep -q "Completed" "$file" || grep -q "Summary" "$file"; then
      state="complete"
    fi
    
    new_name=$(add_prefix "$(basename "$file")" "${state}")
    cp "$file" "${PROJECT_ROOT}/plans/${new_name}"
    echo "Migrated $(basename "$file") to plans/${new_name}"
  fi
done

# Special case: migrate planning/active/* files to plans/
echo "Migrating active plans..."
if [ -d "${PROJECT_ROOT}/planning/active" ]; then
  for file in "${PROJECT_ROOT}/planning/active"/*.md; do
    if [ -f "$file" ]; then
      new_name=$(add_prefix "$(basename "$file")" "active")
      cp "$file" "${PROJECT_ROOT}/plans/${new_name}"
      echo "Migrated active/$(basename "$file") to plans/${new_name}"
    fi
  done
fi

# Special case: migrate planning/completed/* files to plans/
echo "Migrating completed plans..."
if [ -d "${PROJECT_ROOT}/planning/completed" ]; then
  for file in "${PROJECT_ROOT}/planning/completed"/*.md; do
    if [ -f "$file" ]; then
      new_name=$(add_prefix "$(basename "$file")" "complete")
      cp "$file" "${PROJECT_ROOT}/plans/${new_name}"
      echo "Migrated completed/$(basename "$file") to plans/${new_name}"
    fi
  done
fi

# Special case: migrate planning/archived/* files to plans/
echo "Migrating archived plans..."
if [ -d "${PROJECT_ROOT}/planning/archived" ]; then
  for file in "${PROJECT_ROOT}/planning/archived"/*.md; do
    if [ -f "$file" ]; then
      new_name=$(add_prefix "$(basename "$file")" "archived")
      cp "$file" "${PROJECT_ROOT}/plans/${new_name}"
      echo "Migrated archived/$(basename "$file") to plans/${new_name}"
    fi
  done
fi

# Update cross-references in all migrated files
echo "Updating cross-references..."
find "${PROJECT_ROOT}" -name "*.md" -type f | xargs -I{} sed -i 's|](../concepts/|](../core/concept-|g' {}
find "${PROJECT_ROOT}" -name "*.md" -type f | xargs -I{} sed -i 's|](../guides/|](../guides/guide-|g' {}
find "${PROJECT_ROOT}" -name "*.md" -type f | xargs -I{} sed -i 's|](../reference/|](../ref/ref-|g' {}
find "${PROJECT_ROOT}" -name "*.md" -type f | xargs -I{} sed -i 's|](../reference/standards/|](../ref/standard-|g' {}
find "${PROJECT_ROOT}" -name "*.md" -type f | xargs -I{} sed -i 's|](../testing/|](../dev/test-|g' {}
find "${PROJECT_ROOT}" -name "*.md" -type f | xargs -I{} sed -i 's|](../contribution/|](../contrib/contrib-|g' {}
find "${PROJECT_ROOT}" -name "*.md" -type f | xargs -I{} sed -i 's|](../proposals/|](../specs/spec-|g' {}
find "${PROJECT_ROOT}" -name "*.md" -type f | xargs -I{} sed -i 's|](../planning/|](../plans/|g' {}

echo "Migration complete!"
echo "Note: This script copies files. The original files still exist and can be removed when you've verified the migration worked correctly."