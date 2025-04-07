#!/bin/bash
#==============================================================================
# flatten-feature-files.sh
# Implements the directory flattening plan for feature files
#==============================================================================

set -e

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
RESET='\033[0m' # No Color
BOLD='\033[1m'

# Functions for prettier output
info() { echo -e "${BLUE}$1${RESET}"; }
success() { echo -e "${GREEN}$1${RESET}"; }
error() { echo -e "${RED}Error: $1${RESET}" >&2; }
warn() { echo -e "${YELLOW}Warning: $1${RESET}" >&2; }
header() { echo -e "\n${BOLD}${YELLOW}$1${RESET}\n"; }

# Find repository root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"
cd "$PROJECT_ROOT"

# Define source and target directories
SRC_DIR="Samstraumr/samstraumr-core/src/test/resources"
FEATURE_DIR="$SRC_DIR/features"

# Back up existing feature files
backup_feature_files() {
  header "Backing up existing feature files"
  
  BACKUP_DIR="feature-file-backup-$(date +%Y%m%d%H%M%S)"
  mkdir -p "$BACKUP_DIR"
  
  # Find all feature files and copy them to the backup directory
  find "$SRC_DIR" -name "*.feature" -type f | while read -r file; do
    rel_path="${file#$SRC_DIR/}"
    backup_path="$BACKUP_DIR/$rel_path"
    mkdir -p "$(dirname "$backup_path")"
    cp "$file" "$backup_path"
  done
  
  success "Backed up $(find "$BACKUP_DIR" -name "*.feature" -type f | wc -l) feature files to $BACKUP_DIR"
}

# Copy feature files to the new structure
copy_feature_files() {
  header "Copying feature files to the new structure"
  
  # 1. Tube lifecycle feature files
  info "Copying tube lifecycle feature files"
  mkdir -p "$FEATURE_DIR/tube-lifecycle"
  find "$SRC_DIR/tube/features/L0_Tube/lifecycle" -name "*.feature" -type f | while read -r file; do
    base_name=$(basename "$file")
    # Remove redundant prefixes from the filename if present
    new_name="${base_name#tube-}"
    cp "$file" "$FEATURE_DIR/tube-lifecycle/$new_name"
    success "Copied $base_name to tube-lifecycle/$new_name"
  done
  
  # 2. Identity-related feature files
  info "Copying identity-related feature files"
  mkdir -p "$FEATURE_DIR/identity"
  find "$SRC_DIR" -name "*identity*.feature" -type f | while read -r file; do
    base_name=$(basename "$file")
    # Skip if already copied to another directory
    if [[ "$file" == *"/features/identity/"* ]]; then
      continue
    fi
    # Add prefix if not present
    if [[ "$base_name" != *"identity"* ]]; then
      new_name="identity-$base_name"
    else
      new_name="$base_name"
    fi
    cp "$file" "$FEATURE_DIR/identity/$new_name"
    success "Copied $base_name to identity/$new_name"
  done
  
  # 3. Composite pattern feature files
  info "Copying composite pattern feature files"
  mkdir -p "$FEATURE_DIR/composite-patterns"
  find "$SRC_DIR" -path "*patterns*" -name "*.feature" -type f | while read -r file; do
    base_name=$(basename "$file")
    # Skip if already copied to another directory
    if [[ "$file" == *"/features/composite-patterns/"* ]]; then
      continue
    fi
    # Add prefix if not present
    if [[ "$base_name" == observer* ]]; then
      new_name="composite-observer-pattern-test.feature"
    elif [[ "$base_name" == transformer* ]]; then
      new_name="composite-transformer-pattern-test.feature"
    elif [[ "$base_name" == validator* ]]; then
      new_name="composite-validator-pattern-test.feature"
    else
      new_name="composite-$base_name"
    fi
    cp "$file" "$FEATURE_DIR/composite-patterns/$new_name"
    success "Copied $base_name to composite-patterns/$new_name"
  done
  
  # 4. Machine feature files
  info "Copying machine feature files"
  mkdir -p "$FEATURE_DIR/machine"
  find "$SRC_DIR/tube/features/L2_Machine" -name "*.feature" -type f | while read -r file; do
    base_name=$(basename "$file")
    # Remove redundant prefixes and fix naming
    if [[ "$base_name" == machine-* ]]; then
      new_name="$base_name"
    else
      new_name="machine-$base_name"
    fi
    cp "$file" "$FEATURE_DIR/machine/$new_name"
    success "Copied $base_name to machine/$new_name"
  done
  
  # 5. System feature files
  info "Copying system feature files"
  mkdir -p "$FEATURE_DIR/system"
  find "$SRC_DIR/tube/features/L3_System" -name "*.feature" -type f | while read -r file; do
    base_name=$(basename "$file")
    # Remove redundant prefixes and fix naming
    if [[ "$base_name" == system-* ]]; then
      new_name="$base_name"
    else
      new_name="system-$base_name"
    fi
    cp "$file" "$FEATURE_DIR/system/$new_name"
    success "Copied $base_name to system/$new_name"
  done
  
  # 6. Remaining component features
  info "Copying component features"
  mkdir -p "$FEATURE_DIR/component"
  find "$SRC_DIR/component/features" -name "*.feature" -type f | while read -r file; do
    base_name=$(basename "$file")
    # Skip if already copied to another directory or belongs to a specific category
    if [[ "$file" == *"/features/component/"* || "$base_name" == *"identity"* || "$base_name" == *"lifecycle"* ]]; then
      continue
    fi
    
    # Use appropriate naming based on content
    if [[ "$base_name" == component-* ]]; then
      new_name="$base_name"
    else
      new_name="component-$base_name"
    fi
    cp "$file" "$FEATURE_DIR/component/$new_name"
    success "Copied $base_name to component/$new_name"
  done
  
  success "Feature files copied to new structure"
}

# Update feature file content to use the standardized tag structure
update_feature_files() {
  header "Updating feature file content"
  
  # Run the standardize-test-tags.sh script on the new feature files directory
  if [ -f "util/scripts/standardize-test-tags.sh" ]; then
    info "Running standardize-test-tags.sh on new feature files"
    ./util/scripts/standardize-test-tags.sh "$FEATURE_DIR"
  else
    warn "standardize-test-tags.sh not found, skipping tag standardization"
  fi
  
  success "Feature files updated"
}

# Create symlinks for backward compatibility
create_symlinks() {
  header "Creating symlinks for backward compatibility"
  
  # Create key symlinks to maintain backward compatibility
  ln -sf "features" "$SRC_DIR/flattened-features" || true
  
  success "Symlinks created for backward compatibility"
}

# Main function
main() {
  header "Starting Feature Files Structure Flattening"
  
  # Step 1: Backup existing feature files
  backup_feature_files
  
  # Step 2: Copy feature files to the new structure
  copy_feature_files
  
  # Step 3: Update feature file content
  update_feature_files
  
  # Step 4: Create symlinks for backward compatibility
  create_symlinks
  
  success "Phase 2 complete: Feature files structure flattened"
  info "The original feature files remain in place for backward compatibility."
  info "Once verified, you can run tests to ensure everything still works."
}

# Run the main function
main