#!/bin/bash
#==============================================================================
# Filename: install.sh
# Description: Installs or updates the Samstraumr unified configuration system
#==============================================================================
# Usage: ./install.sh [-f|--force] [-v|--verbose]
#
# Options:
#   -f, --force     Force overwrite of existing configuration files
#   -v, --verbose   Display verbose output during installation
#   -h, --help      Display this help message
#
# This script will:
# 1. Set up the unified configuration system
# 2. Migrate from existing configurations
# 3. Update scripts to use the new configuration
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
      echo "Usage: ./install.sh [-f|--force] [-v|--verbose]"
      echo ""
      echo "Options:"
      echo "  -f, --force     Force overwrite of existing configuration files"
      echo "  -v, --verbose   Display verbose output during installation"
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

# Define paths
CONFIG_JSON="${SCRIPT_DIR}/config.json"
CONFIG_SH="${SCRIPT_DIR}/config.sh"
OLD_CONFIG="${PROJECT_ROOT}/.samstraumr.config"
OLD_S8R_CONFIG="${PROJECT_ROOT}/.samstraumr/config.json"
USER_CONFIG_DIR="${SCRIPT_DIR}/user"
TEMPLATE_DIR="${SCRIPT_DIR}/templates"

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

# Check prerequisites
if ! command -v jq &> /dev/null; then
    log_error "jq is required but not installed. Please install jq to continue."
    echo "  Ubuntu/Debian: apt-get install jq"
    echo "  CentOS/RHEL: yum install jq"
    echo "  macOS: brew install jq"
    exit 1
fi

log_info "==== Installing Samstraumr Unified Configuration ===="

# Generate the shell configuration
log_verbose "Generating shell configuration from JSON..."
if [[ -f "${SCRIPT_DIR}/config-generator.sh" ]]; then
    bash "${SCRIPT_DIR}/config-generator.sh"
else
    log_error "config-generator.sh not found in ${SCRIPT_DIR}"
    exit 1
fi

# Create or update the legacy config migration script
log_verbose "Creating migration script for legacy configs..."
cat > "${SCRIPT_DIR}/migrate-configs.sh" << 'EOT'
#!/bin/bash
# Script to migrate from old configuration files to the new unified config system

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
OLD_CONFIG="${PROJECT_ROOT}/.samstraumr.config"
OLD_S8R_CONFIG="${PROJECT_ROOT}/.samstraumr/config.json"
NEW_CONFIG="${SCRIPT_DIR}/config.json"

# Function to extract value from shell configuration
extract_shell_var() {
    local var_name="$1"
    local file="$2"
    local value

    value=$(grep "^${var_name}=" "$file" | head -1 | cut -d= -f2- | sed 's/^"//; s/"$//')
    echo "$value"
}

# Backup old configuration files
if [[ -f "${OLD_CONFIG}" ]]; then
    cp "${OLD_CONFIG}" "${OLD_CONFIG}.bak"
    echo "Backed up ${OLD_CONFIG} to ${OLD_CONFIG}.bak"
fi

if [[ -f "${OLD_S8R_CONFIG}" ]]; then
    cp "${OLD_S8R_CONFIG}" "${OLD_S8R_CONFIG}.bak"
    echo "Backed up ${OLD_S8R_CONFIG} to ${OLD_S8R_CONFIG}.bak"
fi

# Update new configuration with values from old files
if [[ -f "${OLD_CONFIG}" ]]; then
    echo "Migrating from ${OLD_CONFIG}..."
    
    # Extract values from old bash config
    SAMSTRAUMR_VERSION=$(extract_shell_var "SAMSTRAUMR_VERSION" "${OLD_CONFIG}")
    
    # Update the version in the new config if found
    if [[ -n "${SAMSTRAUMR_VERSION}" ]]; then
        echo "Updating version to ${SAMSTRAUMR_VERSION}..."
        jq ".project.version = \"${SAMSTRAUMR_VERSION}\"" "${NEW_CONFIG}" > "${NEW_CONFIG}.tmp"
        mv "${NEW_CONFIG}.tmp" "${NEW_CONFIG}"
    fi
fi

# Extract values from old s8r config if it exists
if [[ -f "${OLD_S8R_CONFIG}" ]]; then
    echo "Migrating from ${OLD_S8R_CONFIG}..."
    
    # Extract and update commands
    jq -r '.commands | to_entries[] | "\(.key)|\(.value)"' "${OLD_S8R_CONFIG}" | while IFS='|' read -r cmd path; do
        echo "Migrating command: ${cmd} -> ${path}"
        jq ".commands.\"${cmd}\" = \"${path}\"" "${NEW_CONFIG}" > "${NEW_CONFIG}.tmp"
        mv "${NEW_CONFIG}.tmp" "${NEW_CONFIG}"
    done
fi

echo "Migration completed successfully."
EOT

chmod +x "${SCRIPT_DIR}/migrate-configs.sh"

# Run the migration script if old configs exist
if [[ -f "${OLD_CONFIG}" ]] || [[ -f "${OLD_S8R_CONFIG}" ]]; then
    log_info "Migrating from existing configuration files..."
    bash "${SCRIPT_DIR}/migrate-configs.sh"
else
    log_verbose "No existing configuration files found to migrate."
fi

# Create symlink from s8r directory to new location if needed
if [[ -d "${PROJECT_ROOT}/.s8r" ]] && [[ "${FORCE}" == "true" ]]; then
    log_info "Creating compatibility symlinks..."
    # Backup old s8r config directory
    mv "${PROJECT_ROOT}/.s8r" "${PROJECT_ROOT}/.s8r.bak"
    # Create symlink
    ln -sf "${SCRIPT_DIR}" "${PROJECT_ROOT}/.s8r"
    log_info "Created symlink from .s8r to .samstraumr for backward compatibility"
fi

# Update s8r CLI to use new config
if [[ -f "${PROJECT_ROOT}/s8r" ]]; then
    log_verbose "Updating s8r CLI to use the new configuration..."
    sed -i 's|\.s8r/config\.json|\.samstraumr/config\.json|g' "${PROJECT_ROOT}/s8r"
    log_info "Updated s8r CLI to use the new configuration path"
fi

log_info "Configuration system installed successfully!"
log_info "To use the new configuration in your shell scripts:"
log_info "  source \"${PROJECT_ROOT}/.samstraumr/config.sh\""
log_info ""
log_info "To update the s8r CLI to use the new configuration:"
log_info "  ./s8r install"