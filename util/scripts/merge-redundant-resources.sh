#!/bin/bash
#==============================================================================
# merge-redundant-resources.sh
# Implements Phase 3 of the directory flattening plan: merging redundant resources
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
SRC_DIR="modules/samstraumr-core/src/test/resources"
FEATURE_DIR="$SRC_DIR/features"

# Back up files before merging
backup_files() {
  header "Backing up test resources before merging"
  
  BACKUP_DIR="redundant-resources-backup-$(date +%Y%m%d%H%M%S)"
  mkdir -p "$BACKUP_DIR"
  
  # Copy all test resources to the backup directory
  cp -r "$SRC_DIR" "$BACKUP_DIR"
  
  success "Backed up test resources to $BACKUP_DIR"
}

# Analyze redundant test resources
analyze_redundant_resources() {
  header "Analyzing redundant test resources"
  
  # Create a report file for the analysis
  REPORT_FILE="$PROJECT_ROOT/redundant-resources-analysis.md"
  
  {
    echo "# Redundant Test Resources Analysis"
    echo "Generated: $(date)"
    echo ""
    echo "## Lifecycle Tests"
    echo ""
    echo "| Location | File Count | Status |"
    echo "|----------|------------|--------|"
  } > "$REPORT_FILE"
  
  # Find all lifecycle test locations
  lifecycle_locations=(
    "$SRC_DIR/component/features/L0_Core"
    "$SRC_DIR/tube/features/L0_Tube/lifecycle"
    "$SRC_DIR/tube/lifecycle"
  )
  
  for location in "${lifecycle_locations[@]}"; do
    if [ -d "$location" ]; then
      file_count=$(find "$location" -name "*.feature" -type f | wc -l)
      echo "| $location | $file_count | Redundant |" >> "$REPORT_FILE"
      info "Found $file_count feature files in $location"
    fi
  done
  
  {
    echo ""
    echo "## Pattern Tests"
    echo ""
    echo "| Location | File Count | Status |"
    echo "|----------|------------|--------|"
  } >> "$REPORT_FILE"
  
  # Find all pattern test locations
  pattern_locations=(
    "$SRC_DIR/composites/features/L1_Bundle/patterns"
    "$SRC_DIR/composites/features/L1_Composite/patterns"
    "$SRC_DIR/composites/patterns"
    "$SRC_DIR/test/patterns"
  )
  
  for location in "${pattern_locations[@]}"; do
    if [ -d "$location" ]; then
      file_count=$(find "$location" -name "*.feature" -type f | wc -l)
      echo "| $location | $file_count | Redundant |" >> "$REPORT_FILE"
      info "Found $file_count feature files in $location"
    fi
  done
  
  success "Analysis completed and saved to $REPORT_FILE"
}

# Compare and merge duplicate lifecycle files
merge_lifecycle_files() {
  header "Merging redundant lifecycle feature files"
  
  # Ensure the target directory exists
  mkdir -p "$FEATURE_DIR/lifecycle-unified"
  
  # Define source directories with their priority (lower number = higher priority)
  declare -A source_dirs
  source_dirs["$SRC_DIR/tube/features/L0_Tube/lifecycle"]=1
  source_dirs["$SRC_DIR/tube/lifecycle"]=2
  source_dirs["$SRC_DIR/component/features/L0_Core"]=3
  
  # Track files that have been merged
  declare -A merged_files
  
  # Process each source directory in priority order
  for source_dir in "${!source_dirs[@]}"; do
    priority=${source_dirs["$source_dir"]}
    
    if [ ! -d "$source_dir" ]; then
      warn "Directory does not exist: $source_dir"
      continue
    fi
    
    info "Processing source directory: $source_dir (priority $priority)"
    
    # Find all feature files in the source directory
    find "$source_dir" -name "*.feature" -type f | while read -r file; do
      base_name=$(basename "$file")
      file_key=$(echo "$base_name" | sed 's/-tests\.feature/-test.feature/g')
      
      # Skip if we've already processed a higher priority version of this file
      if [[ -n "${merged_files[$file_key]}" && ${merged_files[$file_key]} -lt $priority ]]; then
        info "  Skipping $base_name (already merged from higher priority source)"
        continue
      fi
      
      # If it's a lifecycle file, create a unified version with a consistent name
      if [[ "$base_name" == *"lifecycle"* || "$base_name" == *"phase"* ]]; then
        # Standardize the filename
        new_name="lifecycle-$(echo "$base_name" | sed 's/lifecycle-//g' | sed 's/-tests\.feature/-test.feature/g')"
        
        # Copy to the unified directory
        cp "$file" "$FEATURE_DIR/lifecycle-unified/$new_name"
        success "  Merged $base_name to lifecycle-unified/$new_name"
        
        # Mark as merged
        merged_files["$file_key"]=$priority
      fi
    done
  done
  
  # Create README for the lifecycle-unified directory
  cat > "$FEATURE_DIR/lifecycle-unified/README.md" << 'EOL'
<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Unified Lifecycle Tests

## Purpose

This directory contains the consolidated lifecycle test features for both the Tube and Component models. These tests verify the lifecycle state transitions, initialization, and identity establishment processes.

## Test Organization

The tests are organized by lifecycle phase, with each phase having its own feature file:

- **Conception Tests**: Initial identity establishment and creation
- **Embryonic Tests**: Early structure and basic validation
- **Infancy Tests**: Initial capability and connection establishment
- **Childhood Tests**: Functional development and expanded capabilities
- **Full Lifecycle Tests**: End-to-end lifecycle transitions

## File Naming Conventions

Files follow the pattern: `lifecycle-[phase]-test.feature`

## Related Files

- Java step definitions: `org.s8r.test.tube.lifecycle` package
- Implementation classes: `org.s8r.component.core` package
EOL
  
  success "Lifecycle files merged to unified directory"
}

# Compare and merge duplicate pattern files
merge_pattern_files() {
  header "Merging redundant pattern feature files"
  
  # Ensure the target directory exists
  mkdir -p "$FEATURE_DIR/patterns-unified"
  
  # Define source directories with their priority (lower number = higher priority)
  declare -A source_dirs
  source_dirs["$SRC_DIR/composites/features/L1_Composite/patterns"]=1
  source_dirs["$SRC_DIR/composites/features/L1_Bundle/patterns"]=2
  source_dirs["$SRC_DIR/composites/patterns"]=3
  source_dirs["$SRC_DIR/test/patterns"]=4
  
  # Track files that have been merged
  declare -A merged_files
  
  # Process each source directory in priority order
  for source_dir in "${!source_dirs[@]}"; do
    priority=${source_dirs["$source_dir"]}
    
    if [ ! -d "$source_dir" ]; then
      warn "Directory does not exist: $source_dir"
      continue
    fi
    
    info "Processing source directory: $source_dir (priority $priority)"
    
    # Find all feature files in the source directory
    find "$source_dir" -name "*.feature" -type f | while read -r file; do
      base_name=$(basename "$file")
      file_key=$(echo "$base_name" | sed 's/-tests\.feature/-test.feature/g')
      
      # Skip if we've already processed a higher priority version of this file
      if [[ -n "${merged_files[$file_key]}" && ${merged_files[$file_key]} -lt $priority ]]; then
        info "  Skipping $base_name (already merged from higher priority source)"
        continue
      fi
      
      # Standardize the filename
      if [[ "$base_name" == *"observer"* ]]; then
        new_name="pattern-observer-test.feature"
      elif [[ "$base_name" == *"transformer"* ]]; then
        new_name="pattern-transformer-test.feature"
      elif [[ "$base_name" == *"validator"* || "$base_name" == *"filter"* ]]; then
        new_name="pattern-filter-test.feature"
      else
        new_name="pattern-$(echo "$base_name" | sed 's/-tests\.feature/-test.feature/g')"
      fi
      
      # Copy to the unified directory
      cp "$file" "$FEATURE_DIR/patterns-unified/$new_name"
      success "  Merged $base_name to patterns-unified/$new_name"
      
      # Mark as merged
      merged_files["$file_key"]=$priority
    done
  done
  
  # Create README for the patterns-unified directory
  cat > "$FEATURE_DIR/patterns-unified/README.md" << 'EOL'
<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Unified Design Pattern Tests

## Purpose

This directory contains the consolidated design pattern test features for components and composites. These tests verify the implementation of common design patterns like Observer, Transformer, and Filter in a composite component context.

## Test Organization

The tests are organized by design pattern:

- **Observer Pattern**: Components that monitor state changes in other components
- **Transformer Pattern**: Components that transform data from one format to another
- **Filter Pattern**: Components that validate and filter incoming data
- **Circuit Breaker Pattern**: Components that implement fault tolerance patterns

## File Naming Conventions

Files follow the pattern: `pattern-[pattern-name]-test.feature`

## Related Files

- Java step definitions: `org.s8r.test.tube` and `org.s8r.test.component` packages
- Implementation classes: `org.s8r.component.composite` package
EOL
  
  success "Pattern files merged to unified directory"
}

# Update test runners with unified locations
update_test_runners() {
  header "Updating test runners with unified locations"
  
  # Define test runner files to update
  test_runners=(
    "modules/samstraumr-core/src/test/java/org/s8r/test/runner/CucumberRunner.java"
  )
  
  for runner in "${test_runners[@]}"; do
    if [ -f "$runner" ]; then
      info "Updating test runner: $runner"
      
      # Add the unified directories to the features path
      sed -i 's#"src/test/resources/features, src/test/resources/tube/features, src/test/resources/composites/features, src/test/resources/test"#"src/test/resources/features, src/test/resources/features/lifecycle-unified, src/test/resources/features/patterns-unified, src/test/resources/tube/features, src/test/resources/composites/features, src/test/resources/test"#g' "$runner"
      
      success "Updated test runner: $runner"
    else
      warn "Test runner file not found: $runner"
    fi
  done
  
  success "Test runners updated"
}

# Copy unified resources to the main features directory
copy_to_main_features() {
  header "Copying unified resources to the main features directory"
  
  # Copy lifecycle files
  if [ -d "$FEATURE_DIR/lifecycle-unified" ]; then
    cp -r "$FEATURE_DIR/lifecycle-unified/"* "$FEATURE_DIR/tube-lifecycle/"
    success "Copied unified lifecycle files to tube-lifecycle directory"
  fi
  
  # Copy pattern files
  if [ -d "$FEATURE_DIR/patterns-unified" ]; then
    cp -r "$FEATURE_DIR/patterns-unified/"* "$FEATURE_DIR/composite-patterns/"
    success "Copied unified pattern files to composite-patterns directory"
  fi
  
  success "Unified resources copied to main features directory"
}

# Main function
main() {
  header "Starting Redundant Test Resources Merging"
  
  # Step 1: Back up files before merging
  backup_files
  
  # Step 2: Analyze redundant resources
  analyze_redundant_resources
  
  # Step 3: Merge lifecycle files
  merge_lifecycle_files
  
  # Step 4: Merge pattern files
  merge_pattern_files
  
  # Step 5: Update test runners
  update_test_runners
  
  # Step 6: Copy unified resources to the main features directory
  copy_to_main_features
  
  success "Phase 3 complete: Redundant resources merged"
  info "Check the analysis report at $PROJECT_ROOT/redundant-resources-analysis.md"
  info "Unified lifecycle files: $FEATURE_DIR/lifecycle-unified"
  info "Unified pattern files: $FEATURE_DIR/patterns-unified"
  info "Original files remain in place for backward compatibility"
}

# Run the main function
main