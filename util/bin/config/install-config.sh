#!/bin/bash
#==============================================================================
# Filename: install-config.sh
# Description: Script to install or update Samstraumr configuration files
#==============================================================================
# Usage: ./install-config.sh [-f|--force] [-v|--verbose]
#
# Options:
#   -f, --force     Force overwrite of existing configuration files
#   -v, --verbose   Display verbose output during configuration
#   -h, --help      Display this help message
#
# This script will:
# 1. Create necessary configuration directories if they don't exist
# 2. Set up project-specific configuration files
# 3. Set up user-specific configuration files in home directory
#==============================================================================

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../.." && pwd)"

# Source common functions if available
if [ -f "${PROJECT_ROOT}/util/lib/common.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/common.sh"
else
  # Define minimal set of color functions
  COLOR_RED='\033[0;31m'
  COLOR_GREEN='\033[0;32m'
  COLOR_YELLOW='\033[0;33m'
  COLOR_BLUE='\033[0;34m'
  COLOR_RESET='\033[0m'
  COLOR_BOLD='\033[1m'

  function print_header() {
    echo -e "${COLOR_BLUE}${COLOR_BOLD}$1${COLOR_RESET}"
  }

  function print_success() {
    echo -e "${COLOR_GREEN}$1${COLOR_RESET}"
  }

  function print_warning() {
    echo -e "${COLOR_YELLOW}$1${COLOR_RESET}"
  }

  function print_error() {
    echo -e "${COLOR_RED}Error: $1${COLOR_RESET}" >&2
  }

  function print_info() {
    echo -e "${COLOR_BLUE}$1${COLOR_RESET}"
  }

  function print_bold() {
    echo -e "${COLOR_BOLD}$1${COLOR_RESET}"
  }
fi

# Parse arguments
FORCE=false
VERBOSE=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help)
      show_help
      exit 0
      ;;
    -f|--force)
      FORCE=true
      shift
      ;;
    -v|--verbose)
      VERBOSE=true
      shift
      ;;
    *)
      # Unknown option
      print_error "Unknown option: $1"
      print_info "Use -h or --help for usage information."
      exit 1
      ;;
  esac
done

# Define configuration directories
PROJECT_CONFIG_DIR="${PROJECT_ROOT}/.s8r"
PROJECT_CONFIG_TEMPLATES="${PROJECT_CONFIG_DIR}/config"
USER_CONFIG_DIR="${HOME}/.s8r"

# Function to show help
function show_help() {
  print_header "Samstraumr Configuration Installer"
  echo ""
  print_bold "USAGE:"
  echo "  ./install-config.sh [options]"
  echo ""
  print_bold "OPTIONS:"
  echo "  -f, --force     Force overwrite of existing configuration files"
  echo "  -v, --verbose   Display verbose output during configuration"
  echo "  -h, --help      Display this help message"
  echo ""
  print_bold "DESCRIPTION:"
  echo "  This script installs or updates Samstraumr configuration files."
  echo "  It sets up directories and files for both project-specific and"
  echo "  user-specific configurations."
  echo ""
  print_bold "ACTIONS PERFORMED:"
  echo "  1. Creates necessary configuration directories if needed"
  echo "  2. Installs or updates project-specific configuration files"
  echo "  3. Installs or updates user-specific configuration files"
  echo "  4. Sets up CLAUDE.md file for LLM context"
  echo ""
  print_bold "EXAMPLES:"
  echo "  ./install-config.sh              # Standard installation"
  echo "  ./install-config.sh --force      # Force overwrite of existing files"
  echo "  ./install-config.sh --verbose    # Show detailed progress"
}

# Function to create directory if it doesn't exist
function ensure_directory() {
  local dir="$1"
  if [ ! -d "$dir" ]; then
    if $VERBOSE; then
      print_info "Creating directory: $dir"
    fi
    mkdir -p "$dir"
    if [ $? -ne 0 ]; then
      print_error "Failed to create directory: $dir"
      return 1
    fi
    return 0
  else
    if $VERBOSE; then
      print_info "Directory already exists: $dir"
    fi
    return 0
  fi
}

# Function to install a configuration file
function install_config_file() {
  local source_file="$1"
  local target_file="$2"
  local description="$3"
  
  # Check if source file exists
  if [ ! -f "$source_file" ]; then
    print_error "Source file not found: $source_file"
    return 1
  fi
  
  # Check if target file already exists
  if [ -f "$target_file" ] && ! $FORCE; then
    print_warning "$description already exists at: $target_file"
    print_warning "Use --force to overwrite."
    return 0
  fi
  
  # Copy the file
  cp "$source_file" "$target_file"
  if [ $? -ne 0 ]; then
    print_error "Failed to install $description to: $target_file"
    return 1
  fi
  
  if $VERBOSE; then
    print_success "Installed $description to: $target_file"
  fi
  return 0
}

# Function to safely update a JSON config file
function update_project_config() {
  local config_file="$1"
  
  # Check if the file exists
  if [ ! -f "$config_file" ]; then
    # If it doesn't exist, we'll create it with default values
    return 1
  fi
  
  # Only update configuration if --force is specified
  if ! $FORCE; then
    if $VERBOSE; then
      print_info "Skipping config update (use --force to update)"
    fi
    return 0
  fi
  
  # For now, with --force we simply replace the file
  # In a more complex implementation, we would merge settings
  return 1  # Return 1 to trigger recreation with --force
}

# Function to safely backup a file before modifying it
function backup_file() {
  local file="$1"
  local backup="${file}.bak"
  
  # Only create backup if file exists
  if [ -f "$file" ]; then
    if $VERBOSE; then
      print_info "Creating backup of: $file"
    fi
    
    # Use a safer approach to handle special characters in filenames
    if ! cp -f "$file" "$backup" 2>/dev/null; then
      print_error "Failed to create backup of: $file"
      return 1
    fi
    
    # Verify the backup was created
    if [ ! -f "$backup" ]; then
      print_error "Backup was not created: $backup"
      return 1
    fi
  else
    if $VERBOSE; then
      print_info "File does not exist, no backup needed: $file"
    fi
  fi
  
  return 0
}

# Main installation function
function install_config() {
  # Generate a single, clean header
  local header_text="Installing Samstraumr Configuration"
  print_header "$header_text"
  
  # Create a line of equal signs matching the header length
  local header_length=${#header_text}
  local separator=""
  for ((i=0; i<header_length; i++)); do
    separator="${separator}="
  done
  print_header "$separator"
  
  # Ensure all necessary directories exist
  ensure_directory "$PROJECT_CONFIG_DIR" || return 1
  ensure_directory "$PROJECT_CONFIG_TEMPLATES" || return 1
  ensure_directory "$USER_CONFIG_DIR" || return 1
  ensure_directory "${PROJECT_CONFIG_DIR}/user" || return 1

  # Install CLAUDE.md file
  if [ -f "${PROJECT_CONFIG_TEMPLATES}/claude.md.template" ]; then
    # Check if file already exists for appropriate messaging
    local file_existed=false
    if [ -f "${PROJECT_ROOT}/CLAUDE.md" ]; then
      file_existed=true
      # Backup if using force mode
      if $FORCE; then
        backup_file "${PROJECT_ROOT}/CLAUDE.md" || return 1
      fi
    fi
    
    install_config_file "${PROJECT_CONFIG_TEMPLATES}/claude.md.template" "${PROJECT_ROOT}/CLAUDE.md" "CLAUDE.md" || return 1
    
    # Choose appropriate message based on whether we created or updated
    if $file_existed && $FORCE; then
      print_success "Updated CLAUDE.md for LLM context"
    else
      print_success "Installed CLAUDE.md for LLM context"
    fi
  else
    print_error "CLAUDE.md template not found at: ${PROJECT_CONFIG_TEMPLATES}/claude.md.template"
  fi
  
  # Install user config if it doesn't exist
  if [ ! -f "${USER_CONFIG_DIR}/config.json" ]; then
    cat > "${USER_CONFIG_DIR}/config.json" << EOF
{
  "docmosis": {
    "key": "",
    "site": "Free Trial Java"
  }
}
EOF
    print_success "Created user configuration file at: ${USER_CONFIG_DIR}/config.json"
  else
    # Don't modify existing user config - could contain personal settings
    print_info "User configuration file already exists at: ${USER_CONFIG_DIR}/config.json"
  fi
  
  # Check if we should update the project config
  local should_create_config=0  # 0=false, 1=true in bash
  if [ ! -f "${PROJECT_CONFIG_DIR}/config.json" ]; then
    should_create_config=1
  else
    update_project_config "${PROJECT_CONFIG_DIR}/config.json"
    should_create_config=$?
  fi
  
  # Install project config if needed
  if [ $should_create_config -eq 1 ] || $FORCE; then
    # Backup existing config if we're going to modify it
    if [ -f "${PROJECT_CONFIG_DIR}/config.json" ] && $FORCE; then
      backup_file "${PROJECT_CONFIG_DIR}/config.json" || return 1
    fi
    
    # Get the current version
    local version="1.0.0"
    if [ -f "${PROJECT_ROOT}/modules/version.properties" ]; then
      local found_version
      found_version=$(grep -oP 'samstraumr\.version=\K[0-9]+\.[0-9]+\.[0-9]+' "${PROJECT_ROOT}/modules/version.properties" 2>/dev/null || echo "")
      if [ -n "$found_version" ]; then
        version="$found_version"
      fi
    fi
    
    # Create project config file
    cat > "${PROJECT_CONFIG_DIR}/config.json" << EOF
{
  "project": {
    "name": "Samstraumr",
    "version": "${version}",
    "default_command": "build",
    "scripts_dir": "util/bin"
  },
  "paths": {
    "src": "modules/samstraumr-core/src",
    "docs": "docs",
    "tests": "modules/samstraumr-core/src/test",
    "templates": "modules/samstraumr-core/src/main/resources/templates",
    "output": "target"
  },
  "commands": {
    "build": "util/bin/build/build-optimal.sh",
    "test": "util/bin/test/run-tests.sh",
    "version": "util/bin/version/version-manager.sh",
    "docs": "util/bin/docs/generate-docmosis-docs.sh",
    "quality": "util/bin/quality/check-encoding.sh",
    "install": "util/bin/config/install-config.sh"
  }
}
EOF
    # Use appropriate message based on whether file existed
    if [ -f "${PROJECT_CONFIG_DIR}/config.json.bak" ]; then
      print_success "Updated project configuration file at: ${PROJECT_CONFIG_DIR}/config.json"
    else
      print_success "Created project configuration file at: ${PROJECT_CONFIG_DIR}/config.json"
    fi
  else
    print_info "Project configuration file already exists at: ${PROJECT_CONFIG_DIR}/config.json"
  fi
  
  # Create a README for user directory
  if [ ! -f "${PROJECT_CONFIG_DIR}/user/README.md" ] || $FORCE; then
    cat > "${PROJECT_CONFIG_DIR}/user/README.md" << EOF
# This directory is for user-specific settings and should not be committed to Git
EOF
    if $VERBOSE; then
      print_success "Created README in user directory"
    fi
  fi
  
  # Return success silently - the calling script will print a success message
  return 0
}

# Set error handling
set -o pipefail  # Detect errors in pipelines
trap 'echo "Error on line $LINENO"' ERR  # Report line number on error

# Remove duplicate header line from the output in verbose mode
uniq_output() {
  local current_line=""
  local prev_line=""
  while IFS= read -r line; do
    current_line="$line"
    # Only print if line is different from the previous and isn't a duplicate header
    if [[ "$current_line" != "$prev_line" ]]; then
      echo "$current_line"
    fi
    prev_line="$current_line"
  done
}

# Execute the main function
if ! install_config; then
  print_error "Installation failed!"
  exit 1
fi

exit 0