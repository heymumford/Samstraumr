#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# 
# Samstraumr bin directory setup script
# This script sets up a complete bin directory structure with all executables
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

# Find the Samstraumr project root
find_project_root() {
  local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  echo "$(cd "$script_dir/.." && pwd)"
}

PROJECT_ROOT=$(find_project_root)
BIN_DIR="${PROJECT_ROOT}/bin"
S8R_ALL_DIR="${BIN_DIR}/s8r-all"

print_section "Samstraumr Bin Directory Setup"
print_message "Project root: $PROJECT_ROOT"
print_message "Bin directory: $BIN_DIR"

# Create bin subdirectories
print_message "Creating bin subdirectories..."
mkdir -p "$BIN_DIR/core"
mkdir -p "$BIN_DIR/build"
mkdir -p "$BIN_DIR/test"
mkdir -p "$BIN_DIR/component"
mkdir -p "$BIN_DIR/dev"
mkdir -p "$BIN_DIR/migration"
mkdir -p "$BIN_DIR/help"
mkdir -p "$BIN_DIR/utils"
mkdir -p "$BIN_DIR/ai"

# Ensure s8r-all directory exists
if [[ ! -d "$S8R_ALL_DIR" ]]; then
  print_message "Creating s8r-all directory for script backups..."
  mkdir -p "$S8R_ALL_DIR"
fi

# Function to copy script if it exists and is different
copy_script_if_needed() {
  local src="$1"
  local dest="$2"
  local dest_dir=$(dirname "$dest")
  
  if [[ ! -f "$src" ]]; then
    print_warning "Script not found: $src"
    
    # If we don't have the original but have a backup, use that
    local script_name=$(basename "$src")
    if [[ -f "$S8R_ALL_DIR/$script_name" ]]; then
      print_message "Using backup from s8r-all: $script_name"
      src="$S8R_ALL_DIR/$script_name"
    else
      return
    fi
  fi
  
  # Create destination directory if it doesn't exist
  mkdir -p "$dest_dir"
  
  # Always backup to s8r-all if it's not there yet
  if [[ ! -f "$S8R_ALL_DIR/$(basename $src)" ]]; then
    cp -v "$src" "$S8R_ALL_DIR/"
    chmod +x "$S8R_ALL_DIR/$(basename $src)"
    print_message "Backed up $(basename $src) to s8r-all"
  fi
  
  # If destination already exists, check if it's a symlink or different
  if [[ -f "$dest" ]]; then
    if [[ -L "$dest" ]]; then
      print_warning "Removing symlink: $dest"
      rm -f "$dest"
    elif diff -q "$src" "$dest" >/dev/null; then
      print_message "Script $src already exists in $dest_dir and is identical. Skipping."
      return
    else
      print_warning "Script $dest already exists but is different from $src"
      if [[ ! -f "$dest.orig" ]]; then
        cp -v "$dest" "$dest.orig"
        print_message "Backed up original to $dest.orig"
      fi
    fi
  fi
  
  # Copy the script (not symlink)
  cp -v "$src" "$dest"
  chmod +x "$dest"
  print_success "Copied $(basename $src) to $dest_dir"
}

# Copy scripts to appropriate directories
print_section "Copying Scripts to Bin Directory"

# Core scripts
print_message "Copying core scripts..."
for script in s8r s8r-init s8r-list; do
  copy_script_if_needed "$PROJECT_ROOT/$script" "$BIN_DIR/core/$script"
done

# Build scripts
print_message "Copying build scripts..."
for script in s8r-build s8r-build-new s8r-ci; do
  copy_script_if_needed "$PROJECT_ROOT/$script" "$BIN_DIR/build/$script"
done

# Test scripts
print_message "Copying test scripts..."
for script in s8r-test s8r-test-new s8r-test-cli s8r-run-tests.sh; do
  copy_script_if_needed "$PROJECT_ROOT/$script" "$BIN_DIR/test/$script"
done

# Component scripts
print_message "Copying component scripts..."
for script in s8r-component s8r-composite s8r-machine s8r-tube; do
  copy_script_if_needed "$PROJECT_ROOT/$script" "$BIN_DIR/component/$script"
done

# Dev scripts
print_message "Copying dev scripts..."
for script in s8r-dev s8r-dev-help-build; do
  copy_script_if_needed "$PROJECT_ROOT/$script" "$BIN_DIR/dev/$script"
done

# Migration scripts
print_message "Copying migration scripts..."
for script in s8r-migration-monitor s8r-migration-report s8r-architecture-verify s8r-structure-verify; do
  copy_script_if_needed "$PROJECT_ROOT/$script" "$BIN_DIR/migration/$script"
done

# Help scripts
print_message "Copying help scripts..."
for script in s8r-help s8r-help-build s8r-help-component; do
  copy_script_if_needed "$PROJECT_ROOT/$script" "$BIN_DIR/help/$script"
done

# Utility scripts
print_message "Copying utility scripts..."
for script in s8r-docs s8r-quality s8r-version s8r-version-robust s8r-version-simple; do
  copy_script_if_needed "$PROJECT_ROOT/$script" "$BIN_DIR/utils/$script"
done

# AI scripts
print_message "Copying AI scripts..."
for script in s8r-ai-test; do
  copy_script_if_needed "$PROJECT_ROOT/$script" "$BIN_DIR/ai/$script"
done

# Copy or create symlinks script
if [[ ! -f "$BIN_DIR/s8r-scripts-version" ]]; then
  print_message "Creating script version management utility..."
  cat > "$BIN_DIR/s8r-scripts-version" << 'EOF'
#!/bin/bash
# Utility to switch between original and new versions of scripts

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

function find_project_root() {
  local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  echo "$(cd "$script_dir/.." && pwd)"
}

PROJECT_ROOT=$(find_project_root)
BIN_DIR="${PROJECT_ROOT}/bin"

function show_usage() {
  echo "Usage: s8r-scripts-version [command]"
  echo "Commands:"
  echo "  list       - List available script versions"
  echo "  use-new    - Use new versions of scripts"
  echo "  use-old    - Use original versions of scripts"
  echo "  help       - Show this help message"
  exit 1
}

function list_scripts() {
  print_message "Available script versions:"
  
  for script in build test; do
    if [[ -f "$BIN_DIR/build/s8r-$script" && -f "$BIN_DIR/build/s8r-$script-new" ]]; then
      echo "s8r-$script:"
      echo "  - Original: $(stat -c %y "$BIN_DIR/build/s8r-$script")"
      echo "  - New:      $(stat -c %y "$BIN_DIR/build/s8r-$script-new")"
      echo ""
    fi
  done
}

function use_new_scripts() {
  print_message "Switching to new script versions..."
  
  for script in build test; do
    if [[ -f "$BIN_DIR/build/s8r-$script" && -f "$BIN_DIR/build/s8r-$script-new" ]]; then
      # Backup original if not already backed up
      if [[ ! -f "$BIN_DIR/build/s8r-$script.orig" ]]; then
        cp -v "$BIN_DIR/build/s8r-$script" "$BIN_DIR/build/s8r-$script.orig"
      fi
      
      # Replace with new version
      cp -v "$BIN_DIR/build/s8r-$script-new" "$BIN_DIR/build/s8r-$script"
      print_success "Updated s8r-$script to use new version"
    fi
  done
}

function use_old_scripts() {
  print_message "Switching to original script versions..."
  
  for script in build test; do
    if [[ -f "$BIN_DIR/build/s8r-$script.orig" ]]; then
      # Restore original
      cp -v "$BIN_DIR/build/s8r-$script.orig" "$BIN_DIR/build/s8r-$script"
      print_success "Restored s8r-$script to original version"
    else
      print_error "No backup found for s8r-$script"
    fi
  done
}

# Process command line arguments
if [[ $# -eq 0 ]]; then
  show_usage
fi

case "$1" in
  list)
    list_scripts
    ;;
  use-new)
    use_new_scripts
    ;;
  use-old)
    use_old_scripts
    ;;
  help)
    show_usage
    ;;
  *)
    print_error "Unknown command: $1"
    show_usage
    ;;
esac
EOF
  chmod +x "$BIN_DIR/s8r-scripts-version"
  print_success "Created script version management utility"
fi

# Create PATH setup script
print_message "Creating PATH setup script..."
if [[ ! -f "$BIN_DIR/setup-path.sh" ]]; then
  cat > "$BIN_DIR/setup-path.sh" << 'EOF'
#!/bin/bash
# 
# Samstraumr bin directory PATH setup script
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

# Include subdirectories in PATH explanation
print_section "Using Bin Directory"
print_message "With the bin directory in your PATH, you can run any Samstraumr script directly."
print_message "However, the scripts are organized into subdirectories, so you need to know which subdirectory to use."
print_message "Here's a quick reference:"
print_message ""
print_message "For core commands (s8r, s8r-init, s8r-list):"
print_message "  Either add bin/core to your PATH, or create aliases:"
print_message "  alias s8r='$BIN_DIR/core/s8r'"
print_message "  alias s8r-init='$BIN_DIR/core/s8r-init'"
print_message "  alias s8r-list='$BIN_DIR/core/s8r-list'"
print_message ""
print_message "For build commands (s8r-build, s8r-ci):"
print_message "  Either add bin/build to your PATH, or create aliases:"
print_message "  alias s8r-build='$BIN_DIR/build/s8r-build'"
print_message "  alias s8r-ci='$BIN_DIR/build/s8r-ci'"
print_message ""
print_message "You can do the same for other command categories."

print_section "Next Steps"
print_message "1. Run 'source $SHELL_RC' to update your PATH for the current session"
print_message "2. Consider running the bin/create-aliases.sh script to create aliases for commonly used scripts"
print_message "3. Run bin/remove-root-scripts.sh to safely remove scripts from the root directory (optional)"

print_success "Setup complete!"
EOF
  chmod +x "$BIN_DIR/setup-path.sh"
  print_success "Created PATH setup script"
fi

# Create aliases script
print_message "Creating aliases script..."
cat > "$BIN_DIR/create-aliases.sh" << 'EOF'
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
EOF
chmod +x "$BIN_DIR/create-aliases.sh"
print_success "Created aliases script"

# Update the bin README
print_message "Updating bin directory README..."
cat > "$BIN_DIR/README.md" << 'EOF'
# Samstraumr Executables Directory

This directory contains all executable scripts for the Samstraumr project, organized into categories for better maintainability and discovery.

## Setup Instructions

To properly use these executables, you have three options:

### Option 1: Add this directory to your PATH

```bash
# Run the setup script
./bin/setup-path.sh

# This will add the bin directory to your shell configuration
# and provide instructions for activating it in your current session
```

### Option 2: Create shell aliases (recommended)

```bash
# Run the aliases script
./bin/create-aliases.sh

# This will create aliases for all scripts in your shell configuration
# and provide instructions for activating them in your current session
```

### Option 3: Use the scripts directly with full paths

```bash
# You can always use the full path to run a script
/path/to/modules/bin/core/s8r
/path/to/modules/bin/build/s8r-build
```

## Directory Structure

Scripts are organized by functionality:

- `core/` - Core S8r framework executables (s8r, s8r-init, s8r-list)
- `build/` - Build-related executables (s8r-build, s8r-ci)
- `test/` - Testing executables (s8r-test, s8r-test-cli)
- `component/` - Component-related executables (s8r-component, s8r-composite, s8r-machine, s8r-tube)
- `dev/` - Development executables (s8r-dev)
- `migration/` - Migration-related executables (s8r-migration-monitor, s8r-architecture-verify)
- `help/` - Help documentation executables (s8r-help)
- `utils/` - Utility executables (s8r-docs, s8r-quality, s8r-version)
- `ai/` - AI-related executables (s8r-ai-test)
- `s8r-all/` - Backup copies of all scripts

## Management Scripts

This directory also contains management scripts:

- `setup-path.sh` - Adds the bin directory to your PATH
- `create-aliases.sh` - Creates shell aliases for all scripts
- `remove-root-scripts.sh` - Safely removes scripts from the root directory
- `setup-bin-directory.sh` - Sets up the entire bin directory structure
- `s8r-scripts-version` - Manages script versions

## Script Versions

Some scripts have both original and new versions. To manage these versions, use:

```bash
# List available script versions
s8r-scripts-version list

# Switch to new versions of scripts
s8r-scripts-version use-new

# Switch back to original versions
s8r-scripts-version use-old
```

## Implementation Notes

These are the canonical executables for the project. They are actual files (not symlinks) that should be used for all operations.

The scripts previously in the root directory are deprecated and have been removed. If you need to restore them, you can find backup copies in the `s8r-all` directory, but it's strongly recommended to use the bin-directory executables instead.

When updating scripts, please update them directly in their appropriate subdirectory. The `setup-bin-directory.sh` script will automatically create backups in the `s8r-all` directory.
EOF
print_success "Updated bin directory README"

print_section "Bin Directory Setup Complete"
print_message "All Samstraumr scripts have been copied to the bin directory structure."
print_message ""
print_message "Next steps:"
print_message "1. Run bin/setup-path.sh to add the bin directory to your PATH"
print_message "2. Run bin/create-aliases.sh to create shell aliases for all scripts"
print_message "3. Commit changes to the repository"
print_message ""
print_success "Setup complete!"