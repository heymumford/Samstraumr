#!/usr/bin/env bash
#==============================================================================
# cleanup-scripts.sh: Remove outdated scripts and temporary files
# This script helps clean up unused scripts and temporary files after the
# migration to the unified s8r command system
#==============================================================================

set -e

# Define color codes for terminal output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
RESET='\033[0m'

# Directory where script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Check if we should run in non-interactive mode
AUTO_CONFIRM="no"
if [[ "$1" == "--yes" || "$1" == "-y" ]]; then
  AUTO_CONFIRM="yes"
fi

# Print a message with color
info() { echo -e "${BLUE}$1${RESET}"; }
warn() { echo -e "${YELLOW}Warning: $1${RESET}"; }
error() { echo -e "${RED}Error: $1${RESET}" >&2; exit 1; }
success() { echo -e "${GREEN}$1${RESET}"; }

# Log file for cleanup operations
LOG_FILE="${SCRIPT_DIR}/cleanup-scripts.log"
> "${LOG_FILE}"  # Clear log file

# Log a message to both console and log file
log() {
  echo -e "$1" | tee -a "${LOG_FILE}"
}

# Function to remove a file or directory with confirmation
remove_item() {
  local item="$1"
  local confirm="${2:-yes}"  # Default to requiring confirmation
  
  if [ ! -e "$item" ]; then
    warn "Item does not exist: $item"
    return 0
  fi
  
  # Show item details
  if [ -d "$item" ]; then
    log "Directory: $item ($(find "$item" -type f | wc -l) files)"
  else
    log "File: $item ($(wc -l < "$item" 2>/dev/null || echo "0") lines)"
  fi
  
  # If confirmation is required, ask user (unless AUTO_CONFIRM is enabled)
  if [ "$confirm" = "yes" ] && [ "$AUTO_CONFIRM" != "yes" ]; then
    read -p "Remove this item? (y/n): " response
    if [[ ! "$response" =~ ^[Yy]$ ]]; then
      log "Skipped: $item"
      return 0
    fi
  fi
  
  # Remove the item
  if [ -d "$item" ]; then
    rm -rf "$item"
    log "${GREEN}Removed directory: $item${RESET}"
  else
    rm -f "$item"
    log "${GREEN}Removed file: $item${RESET}"
  fi
}

# Main process
info "=== Samstraumr Script Cleanup Tool ==="
info "This tool will remove outdated scripts and temporary files"
info "Log will be saved to: ${LOG_FILE}"
echo

# Category 1: Temporary directory
log "${MAGENTA}=== Category 1: Temporary Directories ===${RESET}"
remove_item "${SCRIPT_DIR}/temp"

# Category 2: Backup files
log "${MAGENTA}=== Category 2: Backup Files ===${RESET}"
remove_item "${SCRIPT_DIR}/util/bin/version/version-manager.sh.backup"

# Category 3: Outdated build scripts
log "${MAGENTA}=== Category 3: Outdated Build Scripts ===${RESET}"
find "${SCRIPT_DIR}/util/bin/build" -type f -name "*.sh" -print0 | while IFS= read -r -d '' file; do
  remove_item "$file"
done

# Category 4: Outdated test scripts
log "${MAGENTA}=== Category 4: Outdated Test Scripts ===${RESET}"
find "${SCRIPT_DIR}/util/bin/test" -type f -name "*.sh" -print0 | while IFS= read -r -d '' file; do
  remove_item "$file"
done

# Category 5: Outdated version scripts
log "${MAGENTA}=== Category 5: Outdated Version Scripts ===${RESET}"
# Keep the modular version script as it's used by s8r-version
find "${SCRIPT_DIR}/util/bin/version" -type f -name "*.sh" ! -name "version-manager-modular.sh" -print0 | while IFS= read -r -d '' file; do
  remove_item "$file"
done

# Category 6: Outdated Docmosis scripts
log "${MAGENTA}=== Category 6: Outdated Docmosis Scripts ===${RESET}"
find "${SCRIPT_DIR}/util/bin/docs" -type f -name "*.sh" -print0 | while IFS= read -r -d '' file; do
  # Extract filename for comparison
  filename=$(basename "$file")
  # Keep any scripts specifically needed
  if [[ "$filename" == "needed-script.sh" ]]; then
    log "${YELLOW}Keeping required script: $file${RESET}"
  else
    remove_item "$file"
  fi
done

# Category 7: Script backups
log "${MAGENTA}=== Category 7: Script Backups ===${RESET}"
find "${SCRIPT_DIR}" -path "*/.script_backups/*" -type f -name "*.sh" -print0 | while IFS= read -r -d '' file; do
  remove_item "$file" "no"  # No confirmation needed for backups
done

# Category 8: Duplicate test annotations
log "${MAGENTA}=== Category 8: Duplicate Test Annotations ===${RESET}"
# We're keeping annotations in org.s8r.test.annotation package and removing others
find "${SCRIPT_DIR}" -path "*/src/test/java/org/*" -name "*.java" -not -path "*/src/test/java/org/s8r/test/annotation/*" | grep -E "UnitTest|ATL|BTL|CompositeTest|MachineTest|TubeTest|StreamTest|FlowTest|OrchestrationTest|AcceptanceTest|AdaptationTest|ApiTest|EndToEndTest|IntegrationTest|PropertyTest|SmokeTest|SystemTest" | while IFS= read -r file; do
  remove_item "$file"
done

# Category 9: Duplicate test runners
log "${MAGENTA}=== Category 9: Duplicate Test Runners ===${RESET}"
# We're keeping runners in org.s8r.test.runner package and removing others
find "${SCRIPT_DIR}" -path "*/src/test/java/org/*" -name "*.java" -not -path "*/src/test/java/org/s8r/test/runner/*" | grep -E "CucumberRunner|OrchestrationTestRunner|CriticalTestRunner" | while IFS= read -r file; do
  remove_item "$file"
done

# Category 10: Redundant template files
log "${MAGENTA}=== Category 10: Redundant Template Files ===${RESET}"
# Keep only the template in util/config, remove others
if [ -f "${SCRIPT_DIR}/java-header-template.txt" ]; then
  remove_item "${SCRIPT_DIR}/java-header-template.txt"
fi

# Summary
echo
info "=== Cleanup Summary ==="
success "Operation completed! See ${LOG_FILE} for details."
echo "Removed files: $(grep -c "Removed file" "${LOG_FILE}")"
echo "Removed directories: $(grep -c "Removed directory" "${LOG_FILE}")"
echo "Skipped items: $(grep -c "Skipped" "${LOG_FILE}")"
echo

# Instructions for next steps
info "=== Recommended Next Steps ==="
echo "1. Run './s8r test all' to verify everything still works"
echo "2. Run 'git status' to review changes"
echo "3. Commit the cleanup: git commit -m \"chore: remove outdated scripts and temporary files\""