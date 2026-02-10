#!/usr/bin/env bash
#==============================================================================
# Repository Cleanup Script for Samstraumr
# This script performs comprehensive cleanup operations on the repository
#==============================================================================
set -e

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

# Functions for prettier output
info() { echo -e "${BLUE}$1${RESET}"; }
success() { echo -e "${GREEN}$1${RESET}"; }
error() { echo -e "${RED}Error: $1${RESET}" >&2; exit 1; }
warn() { echo -e "${YELLOW}Warning: $1${RESET}" >&2; }
heading() { echo -e "${CYAN}${BOLD}$1${RESET}"; }

# Project root directory
PROJECT_ROOT="$(git rev-parse --show-toplevel)"
if [ -z "$PROJECT_ROOT" ]; then
  error "Not in a git repository"
fi

# Cleanup operation: Remove backup directories
remove_backup_dirs() {
  heading "Removing backup directories"
  
  # List of backup directories to remove
  local backup_dirs=(
    "backup-before-migration-*"
    "backup-legacy"
    ".script_backups"
  )
  
  for pattern in "${backup_dirs[@]}"; do
    while IFS= read -r dir; do
      if [ -d "$dir" ]; then
        info "Removing directory: $dir"
        rm -rf "$dir"
        success "✓ Removed $dir"
      fi
    done < <(find "$PROJECT_ROOT" -type d -name "$pattern" 2>/dev/null || true)
  done
  
  echo
}

# Cleanup operation: Remove temporary and unneeded files
remove_temp_files() {
  heading "Removing temporary and unneeded files"
  
  # List of patterns for temporary files
  local temp_patterns=(
    "*.tmp"
    "*.bak"
    "*~"
    "*.swp"
    ".DS_Store"
    "Thumbs.db"
    "*.old"
    "*.backup"
  )
  
  # Count removed files
  local removed_count=0
  
  for pattern in "${temp_patterns[@]}"; do
    while IFS= read -r file; do
      info "Removing file: $file"
      rm -f "$file"
      ((removed_count++))
    done < <(find "$PROJECT_ROOT" -type f -name "$pattern" 2>/dev/null || true)
  done
  
  if [ "$removed_count" -eq 0 ]; then
    info "No temporary files found to remove"
  else
    success "✓ Removed $removed_count temporary files"
  fi
  
  echo
}

# Cleanup operation: Remove empty directories
remove_empty_dirs() {
  heading "Removing empty directories"
  
  # Count removed directories
  local removed_count=0
  
  # Find empty directories (excluding .git)
  while IFS= read -r dir; do
    if [ -d "$dir" ] && [ "$dir" != "$PROJECT_ROOT/.git" ] && [ "$(ls -A "$dir")" = "" ]; then
      info "Removing empty directory: $dir"
      rmdir "$dir"
      ((removed_count++))
    fi
  done < <(find "$PROJECT_ROOT" -type d -not -path "*/\.git*" 2>/dev/null || true)
  
  if [ "$removed_count" -eq 0 ]; then
    info "No empty directories found to remove"
  else
    success "✓ Removed $removed_count empty directories"
  fi
  
  echo
}

# Cleanup operation: Clean build artifacts
clean_build_artifacts() {
  heading "Cleaning build artifacts"
  
  # Use Maven clean for Maven projects
  if [ -f "$PROJECT_ROOT/pom.xml" ]; then
    info "Running Maven clean on root project"
    cd "$PROJECT_ROOT"
    mvn clean -q 2>/dev/null || warn "Maven clean failed at root level, continuing with manual cleanup"
  else
    warn "No pom.xml found at root level, checking subprojects"
  fi
  
  # Check for Maven subprojects
  if [ -d "$PROJECT_ROOT/Samstraumr" ] && [ -f "$PROJECT_ROOT/modules/pom.xml" ]; then
    info "Running Maven clean on Samstraumr subproject"
    cd "$PROJECT_ROOT/Samstraumr"
    mvn clean -q 2>/dev/null || warn "Maven clean failed for Samstraumr, continuing with manual cleanup"
  fi
  
  # List of build directories to clean
  local build_dirs=(
    "target"
    "build"
    "dist"
    "out"
  )
  
  # Count removed directories
  local removed_count=0
  
  for dir in "${build_dirs[@]}"; do
    while IFS= read -r build_dir; do
      info "Removing build directory: $build_dir"
      rm -rf "$build_dir"
      ((removed_count++))
    done < <(find "$PROJECT_ROOT" -type d -name "$dir" 2>/dev/null || true)
  done
  
  if [ "$removed_count" -eq 0 ]; then
    info "No build directories found to remove"
  else
    success "✓ Removed $removed_count build directories"
  fi
  
  echo
}

# Cleanup operation: Update .gitignore
update_gitignore() {
  heading "Updating .gitignore file"
  
  local gitignore_file="$PROJECT_ROOT/.gitignore"
  
  if [ ! -f "$gitignore_file" ]; then
    warn "No .gitignore file found, creating one"
    touch "$gitignore_file"
  fi
  
  # Check if entries already exist
  local entries_to_add=(
    ".script_backups/"
    "backup-before-migration-*/"
    "backup-legacy/"
    "**/maven-failsafe-plugin/"
    "**/surefire-reports/dump*.dump*"
  )
  
  local added_count=0
  
  for entry in "${entries_to_add[@]}"; do
    if ! grep -q "^$entry" "$gitignore_file"; then
      echo "$entry" >> "$gitignore_file"
      ((added_count++))
    fi
  done
  
  if [ "$added_count" -eq 0 ]; then
    info "No new entries added to .gitignore"
  else
    success "✓ Added $added_count new entries to .gitignore"
  fi
  
  echo
}

# Main function
main() {
  heading "Samstraumr Repository Cleanup Tool"
  info "Running repository cleanup operations"
  echo "======================================================="
  echo
  
  # Create git tag to mark the current state
  local tag_name="cleanup-$(date +%Y%m%d)"
  # Check if tag already exists
  if git rev-parse --verify "$tag_name" >/dev/null 2>&1; then
    # Tag exists, create a new one with timestamp
    tag_name="${tag_name}-$(date +%H%M%S)"
  fi
  git tag -a "$tag_name" -m "State before repository cleanup on $(date)"
  success "✓ Created git tag: $tag_name"
  echo
  
  # Run cleanup operations
  remove_backup_dirs
  remove_temp_files
  clean_build_artifacts
  remove_empty_dirs
  update_gitignore
  
  heading "Cleanup Summary"
  success "✓ Repository cleanup completed successfully!"
  info "To consolidate duplicate scripts, run: ./util/scripts/consolidate-scripts.sh"
  info "To view the cleanup plan, see: ./cleanup-plan.md"
  info "Tagged pre-cleanup state as: cleanup-$(date +%Y%m%d)"
  echo
  info "Next steps:"
  echo "1. Review changes with 'git status'"
  echo "2. Commit the changes with 'git commit -m \"chore: repository cleanup\"'"
  echo "3. Consider running the script consolidation tool"
  echo "4. Follow the code consolidation plan in cleanup-plan.md"
  echo
}

# Execute main function
main "$@"