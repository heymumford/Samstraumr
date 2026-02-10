#!/bin/bash
#
# Migrate package structure from mixed names to standardized org.s8r
#
# This script:
# 1. Processes Java files for package declaration updates
# 2. Updates import statements in Java files
# 3. Creates directory structure matching the new packages
# 4. Moves files to the new structure
# 5. Updates references in non-Java files
#
# Usage: ./migrate-packages-v2.sh [--dry-run] [--apply-directly]
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
APPLY_DIRECTLY=false
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
    --apply-directly)
      APPLY_DIRECTLY=true
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

# Helper function to create directories based on a package name
create_package_dir() {
  local base_dir=$1
  local package=$2
  
  # Convert package to directory path
  local dir_path="${base_dir}/$(echo $package | tr '.' '/')"
  
  if [ "$DRY_RUN" = true ]; then
    log "INFO" "(Dry run) Would create directory: ${dir_path}"
    return "${dir_path}"
  fi
  
  mkdir -p "${dir_path}"
  log "INFO" "Created directory: ${dir_path}"
  
  echo "${dir_path}"
}

# Process a single Java file
process_java_file() {
  local file=$1
  local source_type=$2  # main or test
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
  
  # Create directory for the new package
  local target_dir="${TARGET_DIR}/${source_type}/java"
  local package_dir="${target_dir}/$(echo $new_package | tr '.' '/')"
  mkdir -p "${package_dir}"
  
  # Set the target file path
  local target_file_name=$(basename "$file")
  local target_file="${package_dir}/${target_file_name}"
  
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
    # For ConfigurationParameter with GLUE_PROPERTY_NAME, update package references
    /^\s*@ConfigurationParameter.*GLUE_PROPERTY_NAME.*value\s*=\s*".*"/ {
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

# Copy resource files
copy_resources() {
  log "INFO" "Copying resource files..."
  
  if [ "$DRY_RUN" = true ]; then
    log "INFO" "(Dry run) Would copy resource files"
    return
  fi
  
  # Create resources directory structure
  mkdir -p "${TARGET_DIR}/main/resources" "${TARGET_DIR}/test/resources"
  
  # Copy main resources
  if [ -d "${SRC_DIR}/main/resources" ]; then
    cp -r "${SRC_DIR}/main/resources/"* "${TARGET_DIR}/main/resources/"
    log "SUCCESS" "Copied main resources"
  fi
  
  # Copy test resources
  if [ -d "${SRC_DIR}/test/resources" ]; then
    cp -r "${SRC_DIR}/test/resources/"* "${TARGET_DIR}/test/resources/"
    log "SUCCESS" "Copied test resources"
  fi
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
  
  # Update feature files
  find "${ROOT_DIR}" -name "*.feature" -type f | while read -r feature_file; do
    # Skip files in target directory
    if [[ "$feature_file" == *"/target/"* ]]; then
      continue
    fi
    
    # Create a temporary file
    local temp_file="${feature_file}.tmp"
    
    # Apply transformations
    sed -e 's/org\.s8r/org.s8r/g' \
        -e 's/org\.tube/org.s8r.tube.legacy/g' \
        "$feature_file" > "$temp_file"
    
    # Check if the file changed
    if cmp -s "$feature_file" "$temp_file"; then
      rm "$temp_file"
    else
      mv "$temp_file" "$feature_file"
      log "SUCCESS" "Updated references in: ${feature_file}"
    fi
  done
}

# Apply the migration directly to the source directory
apply_migration() {
  log "INFO" "Applying migration directly to source files..."
  
  if [ "$DRY_RUN" = true ]; then
    log "INFO" "(Dry run) Would apply migration to source files"
    return
  fi
  
  # First create a backup
  BACKUP_DIR="${ROOT_DIR}/backup-before-migration-$(date +%Y%m%d%H%M%S)"
  mkdir -p "${BACKUP_DIR}"
  cp -r "${SRC_DIR}" "${BACKUP_DIR}/"
  log "SUCCESS" "Created backup at ${BACKUP_DIR}"
  
  # For each file in the target directory
  find "${TARGET_DIR}" -type f -name "*.java" | while read -r target_file; do
    # Get relative path from target directory
    local rel_path=${target_file#"${TARGET_DIR}/"}
    
    # Construct source path (main/java or test/java)
    if [[ "$rel_path" == main/* ]]; then
      local source_path="${SRC_DIR}/main/java"
      local package_path=${rel_path#"main/java/"}
    elif [[ "$rel_path" == test/* ]]; then
      local source_path="${SRC_DIR}/test/java"
      local package_path=${rel_path#"test/java/"}
    else
      log "WARNING" "Unknown path format: ${rel_path}"
      continue
    fi
    
    # Create the directory in the source
    local dir_path=$(dirname "${source_path}/${package_path}")
    mkdir -p "${dir_path}"
    
    # Copy the file to the source
    cp "${target_file}" "${source_path}/${package_path}"
    log "SUCCESS" "Applied: ${source_path}/${package_path}"
  done
  
  # Remove old package directories that are now empty or contain only empty directories
  find "${SRC_DIR}" -type d -path "*/org/samstraumr*" -o -path "*/org/tube*" | sort -r | while read -r old_dir; do
    # Check if directory contains any files (recursively)
    if [ -z "$(find "${old_dir}" -type f 2>/dev/null)" ]; then
      rm -rf "${old_dir}"
      log "SUCCESS" "Removed empty directory: ${old_dir}"
    else
      log "WARNING" "Directory still contains files, not removing: ${old_dir}"
    fi
  done
}

# Print script header
echo "=========================================================="
echo "  Enhanced Package Migration: Standardize on org.s8r"
echo "  $(date)"
if [ "$DRY_RUN" = true ]; then
  echo -e "  ${YELLOW}DRY RUN MODE${RESET} - No files will be changed"
fi
if [ "$APPLY_DIRECTLY" = true ] && [ "$DRY_RUN" = false ]; then
  echo -e "  ${MAGENTA}DIRECT APPLICATION MODE${RESET} - Changes will be applied to source"
fi
echo "=========================================================="
echo

# Main execution flow
log "INFO" "Starting enhanced package migration process"

# Clear target directory if it exists
if [ -d "${TARGET_DIR}" ] && [ "$DRY_RUN" = false ]; then
  rm -rf "${TARGET_DIR}"
  log "INFO" "Cleared existing target directory"
fi

# Create base target directory structure
if [ "$DRY_RUN" = false ]; then
  mkdir -p "${TARGET_DIR}/main/java" "${TARGET_DIR}/test/java"
  log "INFO" "Created base target directory structure"
fi

# Process all Java files
log "INFO" "Processing Java files..."

# Main source files
find "${SRC_DIR}/main/java" -name "*.java" -type f | while read -r file; do
  process_java_file "$file" "main"
done

# Test source files
find "${SRC_DIR}/test/java" -name "*.java" -type f | while read -r file; do
  process_java_file "$file" "test"
done

# Copy resource files
copy_resources

# Update references in non-Java files
update_references

# Apply the migration directly if requested
if [ "$APPLY_DIRECTLY" = true ] && [ "$DRY_RUN" = false ]; then
  apply_migration
fi

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
  if [ "$APPLY_DIRECTLY" = false ]; then
    echo "3. Apply the migration with: $0 --apply-directly"
  else
    echo "3. Build and test the migrated code with: ./s8r build && ./s8r test all"
  fi
fi

echo
echo "=========================================================="