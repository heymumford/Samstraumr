#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# 
# Samstraumr bin directory setup script
# This script helps users set up their PATH to include the Samstraumr bin directory
#

set -e

# Print message in color
print_message() {
  echo -e "\033[1;34m$1\033[0m"
}

# Print error message in color
print_error() {
  echo -e "\033[1;31m$1\033[0m"
}

# Print success message in color
print_success() {
  echo -e "\033[1;32m$1\033[0m"
}

# Print warning message in color
print_warning() {
  echo -e "\033[1;33m$1\033[0m"
}

# Print section header
print_section() {
  echo -e "\033[1;36m==== $1 ====\033[0m"
}

# Detect shell
detect_shell() {
  local shell_path=$(echo "$SHELL" | xargs basename)
  if [ -z "$shell_path" ]; then
    echo "bash"  # Default to bash if we can't detect
  else
    echo "$shell_path"
  fi
}

# Find the Samstraumr project root
find_project_root() {
  local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  echo "$(cd "$script_dir/.." && pwd)"
}

PROJECT_ROOT=$(find_project_root)
BIN_DIR="${PROJECT_ROOT}/bin"
USER_SHELL=$(detect_shell)
SHELL_RC=""

case "$USER_SHELL" in
  bash)
    SHELL_RC="$HOME/.bashrc"
    ;;
  zsh)
    SHELL_RC="$HOME/.zshrc"
    ;;
  fish)
    SHELL_RC="$HOME/.config/fish/config.fish"
    ;;
  *)
    SHELL_RC="$HOME/.bashrc"  # Default to bashrc
    print_warning "Unknown shell: $USER_SHELL, defaulting to ~/.bashrc"
    ;;
esac

print_section "Samstraumr Bin Setup"
print_message "Project root: $PROJECT_ROOT"
print_message "Bin directory: $BIN_DIR"
print_message "Detected shell: $USER_SHELL (config: $SHELL_RC)"

# Check if bin directory is already in PATH
if echo "$PATH" | tr ':' '\n' | grep -q "^$BIN_DIR$"; then
  print_success "$BIN_DIR is already in your PATH"
else
  print_message "Adding $BIN_DIR to your PATH in $SHELL_RC"
  
  # Different syntax for different shells
  case "$USER_SHELL" in
    fish)
      echo -e "\n# Samstraumr bin directory\nfish_add_path $BIN_DIR" >> "$SHELL_RC"
      ;;
    *)
      echo -e "\n# Samstraumr bin directory\nexport PATH=\"\$PATH:$BIN_DIR\"" >> "$SHELL_RC"
      ;;
  esac
  
  print_success "Added bin directory to $SHELL_RC"
  print_message "To activate in current session, run: "
  
  case "$USER_SHELL" in
    fish)
      echo "  source $SHELL_RC"
      ;;
    *)
      echo "  source $SHELL_RC"
      ;;
  esac
fi

# Display instructions to move scripts
print_section "Migration Instructions"
print_message "The scripts previously located in the root directory are now deprecated."
print_message "The canonical executables are now in the bin directory and its subdirectories."
print_message ""
print_message "Your options:"
print_message "1. Use the bin directory scripts directly (recommended):"
print_message "   Example: s8r-build instead of ./s8r-build"
print_message ""
print_message "2. Remove root scripts to avoid confusion (after updating any references):"
print_message "   ./bin/remove-root-scripts.sh"
print_message ""
print_message "3. Continue using root scripts temporarily (discouraged):"
print_message "   Example: ./s8r-build"
print_message "   These will be removed in a future version."

print_section "Next Steps"
print_message "1. Update any documentation referring to ./s8r-* scripts"
print_message "2. Update CI/CD pipelines to use bin directory scripts"
print_message "3. Start using the bin directory executables in your workflows"

print_success "Setup complete!"