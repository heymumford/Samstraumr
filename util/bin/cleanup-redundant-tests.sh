#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#
# cleanup-redundant-tests.sh - Remove redundant test files per ADR-0014
#
# This script removes duplicate and obsolete test files identified during
# the contract-first testing strategy migration. Run with --dry-run first
# to preview changes.
#
# Usage:
#   ./util/bin/cleanup-redundant-tests.sh --dry-run  # Preview changes
#   ./util/bin/cleanup-redundant-tests.sh            # Execute cleanup
#
# See: docs/architecture/decisions/0014-adopt-contract-first-testing-strategy.md
#

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

DRY_RUN=false
FORCE=false

# Parse arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --dry-run)
      DRY_RUN=true
      shift
      ;;
    --force)
      FORCE=true
      shift
      ;;
    *)
      echo "Unknown option: $1"
      echo "Usage: $0 [--dry-run] [--force]"
      exit 1
      ;;
  esac
done

echo -e "${BLUE}=== Samstraumr Test Cleanup Script ===${NC}"
echo -e "${BLUE}Per ADR-0014: Contract-First Testing Strategy${NC}"
echo ""

if [ "$DRY_RUN" = true ]; then
  echo -e "${YELLOW}DRY RUN MODE - No files will be deleted${NC}"
  echo ""
fi

# ============================================================================
# PHASE 1: Safe Deletions (Backups and Obsolete Directories)
# ============================================================================

echo -e "${GREEN}Phase 1: Removing backup and obsolete directories${NC}"

PHASE1_DIRS=(
  "Samstraumr"                    # Old project structure (644KB)
  "test-backup"                   # Backup files
  "temp-backup"                   # Temporary backups
  "api-migration-backup"          # Migration backup
)

PHASE1_COUNT=0
PHASE1_SIZE=0

for dir in "${PHASE1_DIRS[@]}"; do
  full_path="${PROJECT_ROOT}/${dir}"
  if [ -d "$full_path" ]; then
    size=$(du -sk "$full_path" 2>/dev/null | cut -f1 || echo 0)
    count=$(find "$full_path" -type f -name "*.feature" 2>/dev/null | wc -l | tr -d ' ')
    echo -e "  ${RED}DELETE${NC} ${dir}/ (${count} features, ${size}KB)"
    PHASE1_COUNT=$((PHASE1_COUNT + count))
    PHASE1_SIZE=$((PHASE1_SIZE + size))

    if [ "$DRY_RUN" = false ]; then
      rm -rf "$full_path"
    fi
  fi
done

echo -e "  Phase 1 total: ${PHASE1_COUNT} features, ${PHASE1_SIZE}KB"
echo ""

# ============================================================================
# PHASE 2: Legacy Test Directories
# ============================================================================

echo -e "${GREEN}Phase 2: Removing legacy test directories${NC}"

PHASE2_DIRS=(
  "modules/samstraumr-core/src/test/resources/tube"
  "modules/samstraumr-core/src/test/resources/component"
  "modules/samstraumr-core/src/test/resources/composites"
  "modules/samstraumr-core/src/test/resources/core"
  "modules/samstraumr-core/src/test/resources/test"
)

PHASE2_COUNT=0
PHASE2_SIZE=0

for dir in "${PHASE2_DIRS[@]}"; do
  full_path="${PROJECT_ROOT}/${dir}"
  if [ -d "$full_path" ]; then
    size=$(du -sk "$full_path" 2>/dev/null | cut -f1 || echo 0)
    count=$(find "$full_path" -type f -name "*.feature" 2>/dev/null | wc -l | tr -d ' ')
    echo -e "  ${RED}DELETE${NC} ${dir}/ (${count} features, ${size}KB)"
    PHASE2_COUNT=$((PHASE2_COUNT + count))
    PHASE2_SIZE=$((PHASE2_SIZE + size))

    if [ "$DRY_RUN" = false ]; then
      rm -rf "$full_path"
    fi
  fi
done

echo -e "  Phase 2 total: ${PHASE2_COUNT} features, ${PHASE2_SIZE}KB"
echo ""

# ============================================================================
# PHASE 3: Duplicate Lifecycle/Pattern Directories
# ============================================================================

echo -e "${GREEN}Phase 3: Consolidating duplicate test directories${NC}"

# Keep: lifecycle-unified, composite-patterns
# Remove: lifecycle, tube-lifecycle, patterns, patterns-unified

PHASE3_DIRS=(
  "modules/samstraumr-core/src/test/resources/features/lifecycle"
  "modules/samstraumr-core/src/test/resources/features/tube-lifecycle"
  "modules/samstraumr-core/src/test/resources/features/patterns"
  "modules/samstraumr-core/src/test/resources/features/patterns-unified"
)

PHASE3_COUNT=0
PHASE3_SIZE=0

for dir in "${PHASE3_DIRS[@]}"; do
  full_path="${PROJECT_ROOT}/${dir}"
  if [ -d "$full_path" ]; then
    size=$(du -sk "$full_path" 2>/dev/null | cut -f1 || echo 0)
    count=$(find "$full_path" -type f -name "*.feature" 2>/dev/null | wc -l | tr -d ' ')
    echo -e "  ${RED}DELETE${NC} ${dir}/ (${count} features, ${size}KB)"
    PHASE3_COUNT=$((PHASE3_COUNT + count))
    PHASE3_SIZE=$((PHASE3_SIZE + size))

    if [ "$DRY_RUN" = false ]; then
      rm -rf "$full_path"
    fi
  fi
done

echo -e "  Phase 3 total: ${PHASE3_COUNT} features, ${PHASE3_SIZE}KB"
echo ""

# ============================================================================
# PHASE 4: Karate L3_System Duplicates
# ============================================================================

echo -e "${GREEN}Phase 4: Removing duplicate Karate L3_System tests${NC}"

KARATE_DIR="${PROJECT_ROOT}/test-port-interfaces/src/test/java/karate/L3_System"
PHASE4_COUNT=0
PHASE4_SIZE=0

if [ -d "$KARATE_DIR" ]; then
  size=$(du -sk "$KARATE_DIR" 2>/dev/null | cut -f1 || echo 0)
  count=$(find "$KARATE_DIR" -type f -name "*.feature" 2>/dev/null | wc -l | tr -d ' ')
  echo -e "  ${RED}DELETE${NC} test-port-interfaces/.../karate/L3_System/ (${count} features, ${size}KB)"
  PHASE4_COUNT=$count
  PHASE4_SIZE=$size

  if [ "$DRY_RUN" = false ]; then
    rm -rf "$KARATE_DIR"
  fi
fi

echo -e "  Phase 4 total: ${PHASE4_COUNT} features, ${PHASE4_SIZE}KB"
echo ""

# ============================================================================
# SUMMARY
# ============================================================================

TOTAL_COUNT=$((PHASE1_COUNT + PHASE2_COUNT + PHASE3_COUNT + PHASE4_COUNT))
TOTAL_SIZE=$((PHASE1_SIZE + PHASE2_SIZE + PHASE3_SIZE + PHASE4_SIZE))

echo -e "${BLUE}=== CLEANUP SUMMARY ===${NC}"
echo ""
echo -e "  Features removed: ${RED}${TOTAL_COUNT}${NC}"
echo -e "  Space freed: ${RED}${TOTAL_SIZE}KB${NC}"
echo ""

if [ "$DRY_RUN" = true ]; then
  echo -e "${YELLOW}This was a dry run. No files were deleted.${NC}"
  echo -e "Run without --dry-run to execute cleanup."
else
  echo -e "${GREEN}Cleanup complete.${NC}"
  echo ""
  echo "Next steps:"
  echo "  1. Run: mvn clean compile -q"
  echo "  2. Run: ./s8r-test unit"
  echo "  3. Verify tests pass"
fi
