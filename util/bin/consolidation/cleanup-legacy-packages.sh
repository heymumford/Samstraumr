#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#
# Clean up legacy packages after migration
#
# This script removes the old package files that have been migrated to the new package structure
#

set -e

# Set colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
RESET='\033[0m'

# Script configuration
DRY_RUN=false
ROOT_DIR=$(pwd)
SRC_DIR="${ROOT_DIR}/modules/samstraumr-core/src"
LOG_FILE="${ROOT_DIR}/cleanup-legacy-packages.log"

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

# Remove legacy samstraumr package files
remove_samstraumr_files() {
  log "INFO" "Removing legacy org.s8r package files..."
  local count=$(find "${SRC_DIR}" -type f -path "*/org/samstraumr/*" | wc -l)
  log "INFO" "Found ${count} files to remove"
  
  if [ "$DRY_RUN" = true ]; then
    log "INFO" "(Dry run) Would remove ${count} org.s8r files"
    return
  fi
  
  # Remove legacy files
  find "${SRC_DIR}" -type f -path "*/org/samstraumr/*" -exec rm -f {} \;
  log "SUCCESS" "Removed ${count} org.s8r files"
  
  # Remove empty directories
  find "${SRC_DIR}/main/java/org/samstraumr" "${SRC_DIR}/test/java/org/samstraumr" -type d -empty -delete 2>/dev/null || true
  log "SUCCESS" "Removed empty org.s8r directories"
}

# Remove legacy tube package files
remove_tube_files() {
  log "INFO" "Removing legacy org.tube package files..."
  local count=$(find "${SRC_DIR}" -type f -path "*/org/tube/*" | wc -l)
  log "INFO" "Found ${count} files to remove"
  
  if [ "$DRY_RUN" = true ]; then
    log "INFO" "(Dry run) Would remove ${count} org.tube files"
    return
  fi
  
  # Remove legacy files
  find "${SRC_DIR}" -type f -path "*/org/tube/*" -exec rm -f {} \;
  log "SUCCESS" "Removed ${count} org.tube files"
  
  # Remove empty directories
  find "${SRC_DIR}/main/java/org/tube" "${SRC_DIR}/test/java/org/tube" -type d -empty -delete 2>/dev/null || true
  log "SUCCESS" "Removed empty org.tube directories"
}

# Print script header
echo "=========================================================="
echo "  Legacy Package Cleanup"
echo "  $(date)"
if [ "$DRY_RUN" = true ]; then
  echo -e "  ${YELLOW}DRY RUN MODE${RESET} - No files will be changed"
fi
echo "=========================================================="
echo

# Main execution flow
log "INFO" "Starting legacy package cleanup"

# Remove legacy files
remove_samstraumr_files
remove_tube_files

# Print completion message
if [ "$DRY_RUN" = true ]; then
  log "SUCCESS" "Dry run completed successfully. No files were actually removed."
  log "INFO" "Review the log file at ${LOG_FILE} to see what would happen."
else
  log "SUCCESS" "Legacy package cleanup completed successfully."
  log "INFO" "See complete log at: ${LOG_FILE}"
fi

echo
echo "=========================================================="