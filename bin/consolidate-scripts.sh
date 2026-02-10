#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# Filename: consolidate-scripts.sh
# Description: Deploys consolidated scripts and updates symlinks
#
# This script installs the consolidated versions of the Samstraumr scripts,
# creates backups of the originals, and sets up symlinks for backward
# compatibility.
#==============================================================================

set -e

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="${PROJECT_ROOT:-$(git rev-parse --show-toplevel 2>/dev/null || pwd)}"

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
BOLD='\033[1m'
RESET='\033[0m'

# Functions for prettier output
info() { echo -e "${BLUE}→ $1${RESET}"; }
success() { echo -e "${GREEN}✓ $1${RESET}"; }
error() { echo -e "${RED}✗ $1${RESET}" >&2; exit 1; }
warn() { echo -e "${YELLOW}! $1${RESET}" >&2; }

# Consolidation config
BACKUP_DIR="${PROJECT_ROOT}/backup/scripts/$(date +%Y%m%d_%H%M%S)"
CONSOLIDATED_SCRIPTS=(
  "s8r-version-consolidated"
  "s8r-build-consolidated"
  "s8r-test-consolidated"
)
TARGET_SCRIPTS=(
  "s8r-version"
  "s8r-build"
  "s8r-test"
)
ORIGINAL_LOCATIONS=(
  "${PROJECT_ROOT}/bin/utils/s8r-version"
  "${PROJECT_ROOT}/bin/s8r-all/s8r-build"
  "${PROJECT_ROOT}/bin/s8r-all/s8r-test"
)
SYMLINK_LOCATIONS=(
  "${PROJECT_ROOT}/bin/utils/s8r-version-robust:${PROJECT_ROOT}/bin/s8r-version"
  "${PROJECT_ROOT}/bin/utils/s8r-version-simple:${PROJECT_ROOT}/bin/s8r-version"
  "${PROJECT_ROOT}/bin/s8r-all/s8r-build:${PROJECT_ROOT}/bin/s8r-build"
  "${PROJECT_ROOT}/bin/s8r-all/s8r-build-new:${PROJECT_ROOT}/bin/s8r-build"
  "${PROJECT_ROOT}/bin/s8r-all/s8r-test:${PROJECT_ROOT}/bin/s8r-test"
  "${PROJECT_ROOT}/bin/s8r-all/s8r-test-new:${PROJECT_ROOT}/bin/s8r-test"
)

function show_help() {
  echo -e "${BOLD}Script Consolidation Tool${RESET}"
  echo
  echo "Usage: ./$(basename "$0") [options]"
  echo
  echo "Options:"
  echo "  -b, --backup-only      Create backups without deploying new scripts"
  echo "  -i, --install-only     Install new scripts without creating backups"
  echo "  -n, --no-symlinks      Don't create symlinks for backward compatibility"
  echo "  -h, --help             Show this help message"
  echo
  echo "This script consolidates redundant Samstraumr scripts by:"
  echo "1. Backing up original script files"
  echo "2. Installing unified versions in /bin directory"
  echo "3. Creating symlinks for backward compatibility"
}

# Parse arguments
BACKUP_ONLY=false
INSTALL_ONLY=false
NO_SYMLINKS=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    -b|--backup-only)
      BACKUP_ONLY=true
      shift
      ;;
    -i|--install-only)
      INSTALL_ONLY=true
      shift
      ;;
    -n|--no-symlinks)
      NO_SYMLINKS=true
      shift
      ;;
    -h|--help)
      show_help
      exit 0
      ;;
    *)
      error "Unknown argument: $1"
      ;;
  esac
done

# Create backup directory
if [[ "$INSTALL_ONLY" != "true" ]]; then
  info "Creating backup directory: $BACKUP_DIR"
  mkdir -p "$BACKUP_DIR"
  
  # Backup all original scripts
  for i in "${!ORIGINAL_LOCATIONS[@]}"; do
    original="${ORIGINAL_LOCATIONS[$i]}"
    backup_file="$BACKUP_DIR/$(basename "$original")"
    
    if [[ -f "$original" ]]; then
      info "Backing up $original"
      cp -v "$original" "$backup_file"
    else
      warn "Original file not found, skipping backup: $original"
    fi
  done
  
  # Also backup the redundant scripts
  for i in "${!CONSOLIDATED_SCRIPTS[@]}"; do
    script_name="${CONSOLIDATED_SCRIPTS[$i]}"
    backup_name="${TARGET_SCRIPTS[$i]}"
    redundant_files=(
      "${PROJECT_ROOT}/bin/utils/${backup_name}"
      "${PROJECT_ROOT}/bin/utils/${backup_name}-robust"
      "${PROJECT_ROOT}/bin/utils/${backup_name}-simple"
      "${PROJECT_ROOT}/bin/s8r-all/${backup_name}"
      "${PROJECT_ROOT}/bin/s8r-all/${backup_name}-new"
    )
    
    for file in "${redundant_files[@]}"; do
      if [[ -f "$file" ]]; then
        info "Backing up redundant file: $file"
        cp -v "$file" "$BACKUP_DIR/$(basename "$file")"
      fi
    done
  done
  
  success "Backups created in $BACKUP_DIR"
fi

# Install consolidated scripts
if [[ "$BACKUP_ONLY" != "true" ]]; then
  info "Installing consolidated scripts"
  
  # Create bin directory if it doesn't exist
  mkdir -p "${PROJECT_ROOT}/bin"
  
  # Install each consolidated script
  for i in "${!CONSOLIDATED_SCRIPTS[@]}"; do
    source_file="${PROJECT_ROOT}/bin/${CONSOLIDATED_SCRIPTS[$i]}"
    target_file="${PROJECT_ROOT}/bin/${TARGET_SCRIPTS[$i]}"
    
    if [[ -f "$source_file" ]]; then
      info "Installing ${TARGET_SCRIPTS[$i]}"
      cp -v "$source_file" "$target_file"
      chmod +x "$target_file"
    else
      error "Consolidated script not found: $source_file"
    fi
  done
  
  success "Consolidated scripts installed in ${PROJECT_ROOT}/bin"
fi

# Create symlinks for backward compatibility
if [[ "$NO_SYMLINKS" != "true" && "$BACKUP_ONLY" != "true" ]]; then
  info "Creating symlinks for backward compatibility"
  
  for symlink_info in "${SYMLINK_LOCATIONS[@]}"; do
    IFS=':' read -r symlink_path target_path <<< "$symlink_info"
    
    # Create directory if it doesn't exist
    symlink_dir=$(dirname "$symlink_path")
    if [[ ! -d "$symlink_dir" ]]; then
      mkdir -p "$symlink_dir"
    fi
    
    # Remove existing file or symlink
    if [[ -e "$symlink_path" || -L "$symlink_path" ]]; then
      rm -f "$symlink_path"
    fi
    
    # Create relative symlink
    # Calculate the relative path from symlink to target
    rel_path=$(python3 -c "import os.path; print(os.path.relpath('$target_path', '${symlink_dir}'))" 2>/dev/null || echo "../../../bin/$(basename "$target_path")")
    
    info "Creating symlink: $symlink_path -> $rel_path"
    ln -sf "$rel_path" "$symlink_path"
  done
  
  success "Symlinks created for backward compatibility"
fi

# Final information
info "Script consolidation complete"
echo -e "${GREEN}${BOLD}Summary:${RESET}"
echo -e "- Consolidated version scripts: ${BOLD}s8r-version${RESET}, ${BOLD}s8r-version-robust${RESET}, ${BOLD}s8r-version-simple${RESET}"
echo -e "- Consolidated build scripts: ${BOLD}s8r-build${RESET}, ${BOLD}s8r-build-new${RESET}"
echo -e "- Consolidated test scripts: ${BOLD}s8r-test${RESET}, ${BOLD}s8r-test-new${RESET}"
echo -e "- All scripts are now centralized in: ${BOLD}${PROJECT_ROOT}/bin${RESET}"
echo -e "- Original scripts backed up to: ${BOLD}${BACKUP_DIR}${RESET}"
echo
echo -e "${YELLOW}To roll back changes:${RESET}"
echo -e "  cp -r ${BACKUP_DIR}/* ${PROJECT_ROOT}"