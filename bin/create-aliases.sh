#!/bin/bash
# 
# Samstraumr aliases setup script
# This script creates shell aliases for all Samstraumr scripts in the bin directory
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

# Print section header
print_section() {
  echo -e "\033[1;36m==== $1 ====\033[0m"
}

# Find the Samstraumr project root
find_project_root() {
  local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  echo "$(cd "$script_dir/.." && pwd)"
}

PROJECT_ROOT=$(find_project_root)
BIN_DIR="${PROJECT_ROOT}/bin"

# Detect shell
USER_SHELL=$(basename "$SHELL")
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

print_section "Samstraumr Aliases Setup"
print_message "Project root: $PROJECT_ROOT"
print_message "Bin directory: $BIN_DIR"
print_message "Shell RC file: $SHELL_RC"

# Create aliases
print_message "Creating aliases for all Samstraumr scripts..."

# Start with a marker to identify our aliases block
ALIASES_START="# === BEGIN SAMSTRAUMR ALIASES ==="
ALIASES_END="# === END SAMSTRAUMR ALIASES ==="

# Check if aliases block already exists
if grep -q "$ALIASES_START" "$SHELL_RC"; then
  print_message "Samstraumr aliases already exist in $SHELL_RC, updating..."
  
  # Extract the block and remove it
  TEMP_FILE=$(mktemp)
  sed "/$ALIASES_START/,/$ALIASES_END/d" "$SHELL_RC" > "$TEMP_FILE"
  mv "$TEMP_FILE" "$SHELL_RC"
fi

# Add aliases to shell RC
echo "$ALIASES_START" >> "$SHELL_RC"
echo "# Generated on $(date)" >> "$SHELL_RC"
echo "" >> "$SHELL_RC"

# Add aliases for each script in each subdirectory
for category in core build test component dev migration help utils ai; do
  if [[ -d "$BIN_DIR/$category" ]]; then
    echo "# $category scripts" >> "$SHELL_RC"
    
    for script in "$BIN_DIR/$category"/*; do
      if [[ -f "$script" && -x "$script" ]]; then
        script_name=$(basename "$script")
        
        case "$USER_SHELL" in
          fish)
            echo "alias $script_name='$script'" >> "$SHELL_RC"
            ;;
          *)
            echo "alias $script_name='$script'" >> "$SHELL_RC"
            ;;
        esac
        
        print_message "Added alias for $script_name"
      fi
    done
    
    echo "" >> "$SHELL_RC"
  fi
done

echo "$ALIASES_END" >> "$SHELL_RC"

print_success "Aliases created in $SHELL_RC"
print_message "To activate the aliases, run: source $SHELL_RC"

print_section "Next Steps"
print_message "1. Run 'source $SHELL_RC' to load the aliases in the current session"
print_message "2. Use the aliases to run Samstraumr scripts from anywhere"
print_message "3. Run bin/remove-root-scripts.sh to safely remove scripts from the root directory (optional)"

print_success "Setup complete!"
