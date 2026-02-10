#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#
# Migrate package structure from mixed names to standardized org.s8r
#
# This script:
# 1. Processes Java files for package declaration updates
# 2. Updates import statements in Java files
# 3. Creates new package directory structure
# 4. Moves files to the new structure
# 5. Attempts to update references in non-Java files
#
# Usage: ./migrate-packages.sh [--dry-run]
#

set -e

# Set colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
RESET='\033[0m'

# Script configuration
DRY_RUN=false
ROOT_DIR=$(pwd)
SRC_DIR="${ROOT_DIR}/modules/samstraumr-core/src"
TARGET_DIR="${ROOT_DIR}/target/package-migration"
LOG_FILE="${ROOT_DIR}/package-migration.log"

# Parse command line arguments
for arg in "$@"; do
  case $arg in
    --dry-run)
      DRY_RUN=true
      shift
      ;;
    *)
      # Unknown option
      ;;
  esac
done

# Initialize or clear the log file
> "${LOG_FILE}"

# Helper function to log a message to the console and log file
log() {
  local level=$1
  local message=$2
  
  case "$level" in
    "INFO")
      echo -e "${BLUE}[INFO]${RESET} $message" | tee -a "${LOG_FILE}"
      ;;
    "WARNING")
      echo -e "${YELLOW}[WARNING]${RESET} $message" | tee -a "${LOG_FILE}"
      ;;
    "ERROR")
      echo -e "${RED}[ERROR]${RESET} $message" | tee -a "${LOG_FILE}"
      ;;
    "SUCCESS")
      echo -e "${GREEN}[SUCCESS]${RESET} $message" | tee -a "${LOG_FILE}"
      ;;
    *)
      echo -e "$message" | tee -a "${LOG_FILE}"
      ;;
  esac
}

# Helper function to create the target directory structure
create_target_directories() {
  log "INFO" "Creating target directory structure in ${TARGET_DIR}"
  
  if [ "$DRY_RUN" = true ]; then
    log "INFO" "(Dry run) Would create directory ${TARGET_DIR}"
    return
  fi
  
  mkdir -p "${TARGET_DIR}"
  find "${SRC_DIR}" -type d -path "*/java/*" | while read -r dir; do
    relative_dir=$(echo "$dir" | sed "s|${SRC_DIR}/||")
    target_subdir="${TARGET_DIR}/${relative_dir}"
    mkdir -p "${target_subdir}"
    log "INFO" "Created directory: ${target_subdir}"
  done
}

# Helper function to transform package paths according to the mapping
transform_package_path() {
  local package=$1
  
  # Apply package transformation rules
  if [[ "$package" == org.s8r* ]]; then
    # Replace org.s8r with org.s8r
    echo "$package" | sed 's/org\.s8r/org.s8r/'
  elif [[ "$package" == org.tube* ]]; then
    # Move org.tube to org.s8r.tube.legacy
    echo "$package" | sed 's/org\.tube/org.s8r.tube.legacy/'
  else
    # Return unchanged if no rule matches
    echo "$package"
  fi
}

# Process a single Java file
process_java_file() {
  local file=$1
  local file_rel_path=${file#"${SRC_DIR}/"}
  
  log "INFO" "Processing file: ${file_rel_path}"
  
  # Extract current package declaration
  local current_package=$(grep -m 1 "^package " "$file" | sed 's/package \(.*\);/\1/')
  
  if [ -z "$current_package" ]; then
    log "WARNING" "No package declaration found in $file"
    return
  fi
  
  # Transform the package
  local new_package=$(transform_package_path "$current_package")
  
  if [ "$current_package" == "$new_package" ]; then
    log "INFO" "Package unchanged: $current_package"
  else
    log "INFO" "Package transformation: $current_package -> $new_package"
  fi
  
  if [ "$DRY_RUN" = true ]; then
    return
  fi
  
  # Create the target file path
  local target_file_dir="${TARGET_DIR}/$(echo "$file_rel_path" | sed 's|\(.*\)/[^/]*$|\1|')"
  local target_file_name=$(basename "$file")
  local target_file="${target_file_dir}/${target_file_name}"
  
  # Ensure target directory exists
  mkdir -p "$target_file_dir"
  
  # Create the new file with updated package
  awk -v old_pkg="$current_package" -v new_pkg="$new_package" '
    # If we find package declaration, replace it
    /^package / && $2 ~ old_pkg";" {
      print "package " new_pkg ";"
      next
    }
    # For import statements, apply the same transformations
    /^import / {
      line = $0
      if (line ~ /org\.s8r/) {
        gsub(/org\.s8r/, "org.s8r", line)
      } else if (line ~ /org\.tube/) {
        gsub(/org\.tube/, "org.s8r.tube.legacy", line)
      }
      print line
      next
    }
    # Print all other lines unchanged
    { print }
  ' "$file" > "$target_file"
  
  log "SUCCESS" "Created file with updated package: ${target_file}"
}

# Update references in non-Java files
update_references() {
  log "INFO" "Updating references in non-Java files..."
  
  if [ "$DRY_RUN" = true ]; then
    log "INFO" "(Dry run) Would update references in non-Java files"
    return
  fi
  
  # Find XML files (like Maven POMs) and update references
  find "${ROOT_DIR}" -name "*.xml" -type f | while read -r xml_file; do
    # Skip files in target directory
    if [[ "$xml_file" == *"/target/"* ]]; then
      continue
    fi
    
    # Create a temporary file
    local temp_file="${xml_file}.tmp"
    
    # Apply transformations
    sed -e 's/org\.s8r/org.s8r/g' \
        -e 's/org\.tube/org.s8r.tube.legacy/g' \
        "$xml_file" > "$temp_file"
    
    # Check if the file changed
    if cmp -s "$xml_file" "$temp_file"; then
      rm "$temp_file"
    else
      mv "$temp_file" "$xml_file"
      log "SUCCESS" "Updated references in: ${xml_file}"
    fi
  done
  
  # Update properties files
  find "${ROOT_DIR}" -name "*.properties" -type f | while read -r prop_file; do
    # Skip files in target directory
    if [[ "$prop_file" == *"/target/"* ]]; then
      continue
    fi
    
    # Create a temporary file
    local temp_file="${prop_file}.tmp"
    
    # Apply transformations
    sed -e 's/org\.s8r/org.s8r/g' \
        -e 's/org\.tube/org.s8r.tube.legacy/g' \
        "$prop_file" > "$temp_file"
    
    # Check if the file changed
    if cmp -s "$prop_file" "$temp_file"; then
      rm "$temp_file"
    else
      mv "$temp_file" "$prop_file"
      log "SUCCESS" "Updated references in: ${prop_file}"
    fi
  done
}

# Print script header
echo "=========================================================="
echo "  Package Migration: Standardize on org.s8r"
echo "  $(date)"
if [ "$DRY_RUN" = true ]; then
  echo -e "  ${YELLOW}DRY RUN MODE${RESET} - No files will be changed"
fi
echo "=========================================================="
echo

# Main execution flow
log "INFO" "Starting package migration process"

# Create target directory structure
create_target_directories

# Find and process all Java files
log "INFO" "Processing Java files..."
find "${SRC_DIR}" -name "*.java" -type f | while read -r file; do
  process_java_file "$file"
done

# Update references in non-Java files
update_references

# Print completion message
if [ "$DRY_RUN" = true ]; then
  log "SUCCESS" "Dry run completed successfully. No files were actually changed."
  log "INFO" "Review the log file at ${LOG_FILE} to see what would happen."
else
  log "SUCCESS" "Package migration completed successfully."
  log "INFO" "Migrated files are available at: ${TARGET_DIR}"
  log "INFO" "See complete log at: ${LOG_FILE}"
  echo
  echo "Next steps:"
  echo "1. Review the migrated files to ensure correctness"
  echo "2. Run tests on the migrated codebase"
  echo "3. Replace the original files with the migrated ones"
fi

echo
echo "=========================================================="