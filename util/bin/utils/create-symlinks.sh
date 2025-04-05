#!/bin/bash
#==============================================================================
# Filename: create-symlinks.sh
# Description: Create symbolic links for backward compatibility
#==============================================================================

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Source the configuration file
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  source "$(cd "$(dirname "${BASH_SOURCE[0]}")" source "${PROJECT_ROOT}/.samstraumr.configsource "${PROJECT_ROOT}/.samstraumr.config pwd)/../../.samstraumr/config.sh""
else
  echo "Error: Configuration file not found: ${PROJECT_ROOT}/.samstraumr.config"
  exit 1
fi

# Define ANSI color codes
COLOR_RED='\033[0;31m'
COLOR_GREEN='\033[0;32m'
COLOR_YELLOW='\033[0;33m'
COLOR_BLUE='\033[0;34m'
COLOR_RESET='\033[0m'

#------------------------------------------------------------------------------
# Functions
#------------------------------------------------------------------------------

function print_header() {
  echo -e "${COLOR_BLUE}$1${COLOR_RESET}"
  echo -e "${COLOR_BLUE}$(printf '=%.0s' $(seq 1 ${#1}))${COLOR_RESET}"
}

function print_success() {
  echo -e "${COLOR_GREEN}✓ $1${COLOR_RESET}"
}

function print_error() {
  echo -e "${COLOR_RED}✗ $1${COLOR_RESET}" >&2
}

function print_warning() {
  echo -e "${COLOR_YELLOW}! $1${COLOR_RESET}"
}

function create_symlink() {
  local source="$1"
  local target="$2"
  
  # Check if source exists
  if [ ! -f "$source" ]; then
    print_error "Source file does not exist: $source"
    return 1
  fi
  
  # Check if target exists
  if [ -f "$target" ]; then
    # Check if it's already a symlink to the source
    if [ -L "$target" ] && [ "$(readlink "$target")" = "$source" ]; then
      print_warning "Symlink already exists: $target -> $source"
      return 0
    fi
    
    # Backup existing file
    local backup="${target}.bak"
    mv "$target" "$backup"
    print_warning "Existing file backed up to: $backup"
  fi
  
  # Create symlink
  ln -sf "$source" "$target"
  print_success "Created symlink: $target -> $source"
  
  return 0
}

function create_redirect_script() {
  local source="$1"
  local target="$2"
  
  # Check if source exists
  if [ ! -f "$source" ]; then
    print_error "Source file does not exist: $source"
    return 1
  fi
  
  # Check if target exists
  if [ -f "$target" ]; then
    # Backup existing file
    local backup="${target}.bak"
    mv "$target" "$backup"
    print_warning "Existing file backed up to: $backup"
  fi
  
  # Create redirect script
  cat > "$target" << EOF
#!/bin/bash
# This is a redirect script that points to the new location

echo -e "\033[1;33mWARNING: $(basename "$target") has been moved to ${source/$PROJECT_ROOT\//}\033[0m"
echo -e "Please use \033[1;32m./${source/$PROJECT_ROOT\//}\033[0m instead."
echo ""

# Forward to new script
$source "\$@"
EOF

  # Make it executable
  chmod +x "$target"
  
  print_success "Created redirect script: $target -> $source"
  
  return 0
}

#------------------------------------------------------------------------------
# Main
#------------------------------------------------------------------------------

print_header "Creating Symbolic Links for Backward Compatibility"

# Build scripts
echo "Setting up build scripts..."
create_redirect_script "${PROJECT_ROOT}/util/bin/build/build-optimal.sh" "${PROJECT_ROOT}/util/build-optimal.sh"
create_redirect_script "${PROJECT_ROOT}/util/bin/build/build-optimal.sh" "${PROJECT_ROOT}/setup-fast.sh"

# Test scripts
echo "Setting up test scripts..."
create_redirect_script "${PROJECT_ROOT}/util/bin/test/run-tests.sh" "${PROJECT_ROOT}/util/test-run.sh"
create_redirect_script "${PROJECT_ROOT}/util/bin/test/run-tests.sh" "${PROJECT_ROOT}/run-tests.sh"
create_redirect_script "${PROJECT_ROOT}/util/bin/test/run-atl-tests.sh" "${PROJECT_ROOT}/util/test-run-atl.sh"
create_redirect_script "${PROJECT_ROOT}/util/bin/test/run-atl-tests.sh" "${PROJECT_ROOT}/run-atl-tests.sh"

# Version scripts
echo "Setting up version scripts..."
create_redirect_script "${PROJECT_ROOT}/util/bin/version/version-manager.sh" "${PROJECT_ROOT}/util/version"
create_redirect_script "${PROJECT_ROOT}/util/bin/version/version-manager.sh" "${PROJECT_ROOT}/update-version.sh"

# Quality scripts
echo "Setting up quality scripts..."
create_redirect_script "${PROJECT_ROOT}/util/bin/quality/check-encoding.sh" "${PROJECT_ROOT}/util/scripts/check-encoding.sh"
create_redirect_script "${PROJECT_ROOT}/util/bin/utils/update-java-headers.sh" "${PROJECT_ROOT}/util/scripts/update-java-headers.sh"
create_redirect_script "${PROJECT_ROOT}/util/bin/utils/update-java-headers.sh" "${PROJECT_ROOT}/update-java-headers.sh"

print_header "Summary"
echo "Created all necessary symlinks and redirect scripts."
echo "You can now use either the old paths or the new organized structure."
echo "For best results, prefer using the new unified CLI:"
echo ""
echo "  ./util/samstraumr <command> [options]"
echo ""
echo "See ./util/samstraumr help for more information."

exit 0