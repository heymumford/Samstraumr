#!/bin/bash
#==============================================================================
# Filename: update-scripts.sh
# Description: Updates scripts to use the new unified configuration
#==============================================================================
# Usage: ./update-scripts.sh [-f|--force] [-v|--verbose]
#
# Options:
#   -f, --force     Force update of all scripts
#   -v, --verbose   Display verbose output during update
#   -h, --help      Display this help message
#
# This script will:
# 1. Find all shell scripts that source "$(cd "$(dirname "${BASH_SOURCE[0]}")" source the old .samstraumr.configsource the old .samstraumr.config pwd)/../../.samstraumr/config.sh"
# 2. Update them to source the new .samstraumr/config.sh instead
# 3. Find all scripts that reference .samstraumr/config.json
# 4. Update them to use .samstraumr/config.json
#==============================================================================

set -e

# Parse command line arguments
FORCE=false
VERBOSE=false

while [[ $# -gt 0 ]]; do
  case $1 in
    -f|--force)
      FORCE=true
      shift
      ;;
    -v|--verbose)
      VERBOSE=true
      shift
      ;;
    -h|--help)
      echo "Usage: ./update-scripts.sh [-f|--force] [-v|--verbose]"
      echo ""
      echo "Options:"
      echo "  -f, --force     Force update of all scripts"
      echo "  -v, --verbose   Display verbose output during update"
      echo "  -h, --help      Display this help message"
      exit 0
      ;;
    *)
      echo "Unknown option: $1"
      echo "Use -h or --help for usage information"
      exit 1
      ;;
  esac
done

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Function to log verbose messages
log_verbose() {
  if [[ "${VERBOSE}" == "true" ]]; then
    echo "$1"
  fi
}

# Function to log important messages
log_info() {
  echo "$1"
}

# Function to log errors
log_error() {
  echo "ERROR: $1" >&2
}

log_info "==== Updating Scripts to Use Unified Configuration ===="

# Find all shell scripts that source "$(cd "$(dirname "${BASH_SOURCE[0]}")" source the old .samstraumr.configsource the old .samstraumr.config pwd)/../../.samstraumr/config.sh"
log_verbose "Finding scripts that use old .samstraumr.config..."
OLD_CONFIG_SCRIPTS=$(grep -l "source.*\.samstraumr\.config" "${PROJECT_ROOT}" --include="*.sh" -r 2>/dev/null || echo "")

if [[ -n "${OLD_CONFIG_SCRIPTS}" ]]; then
    log_info "Found $(echo "${OLD_CONFIG_SCRIPTS}" | wc -l) scripts using old .samstraumr.config"
    
    # Create backup directory
    BACKUP_DIR="${PROJECT_ROOT}/.script_backups/$(date +%Y%m%d_%H%M%S)"
    mkdir -p "${BACKUP_DIR}"
    log_verbose "Created backup directory: ${BACKUP_DIR}"
    
    # Update each script
    for script in ${OLD_CONFIG_SCRIPTS}; do
        log_verbose "Processing ${script}..."
        
        # Backup the script
        cp "${script}" "${BACKUP_DIR}/$(basename "${script}")"
        
        # Update the script
        sed -i 's|source.*\.samstraumr\.config|source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/../../.samstraumr/config.sh"|g' "${script}"
        
        log_info "Updated: ${script}"
    done
    
    log_info "Backup of original scripts created in ${BACKUP_DIR}"
else
    log_info "No scripts found using old .samstraumr.config"
fi

# Find all scripts that reference .samstraumr/config.json
log_verbose "Finding scripts that reference .samstraumr/config.json..."
OLD_S8R_SCRIPTS=$(grep -l "\.s8r.*config\.json" "${PROJECT_ROOT}" --include="*.sh" -r 2>/dev/null || echo "")

if [[ -n "${OLD_S8R_SCRIPTS}" ]]; then
    log_info "Found $(echo "${OLD_S8R_SCRIPTS}" | wc -l) scripts referencing .samstraumr/config.json"
    
    # Create backup directory if it doesn't exist
    if [[ ! -d "${BACKUP_DIR}" ]]; then
        BACKUP_DIR="${PROJECT_ROOT}/.script_backups/$(date +%Y%m%d_%H%M%S)"
        mkdir -p "${BACKUP_DIR}"
        log_verbose "Created backup directory: ${BACKUP_DIR}"
    fi
    
    # Update each script
    for script in ${OLD_S8R_SCRIPTS}; do
        log_verbose "Processing ${script}..."
        
        # Backup the script
        cp "${script}" "${BACKUP_DIR}/$(basename "${script}")"
        
        # Update the script
        sed -i 's|\.s8r/config\.json|.samstraumr/config.json|g' "${script}"
        
        log_info "Updated: ${script}"
    done
    
    log_info "Backup of original scripts created in ${BACKUP_DIR}"
else
    log_info "No scripts found referencing .samstraumr/config.json"
fi

log_info "Script update completed successfully!"