#!/bin/bash
# 
# S8r scripts organization script
# This script organizes s8r script files into appropriate categories
# in the bin directory for better project organization.
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

# Base directory
BASE_DIR=$(pwd)
if [[ ! -d "$BASE_DIR/util" ]]; then
  print_error "Error: Must be run from the root of the Samstraumr project."
  exit 1
fi

print_message "Starting S8r scripts organization..."

# Create bin directories for organized scripts
mkdir -p "$BASE_DIR/bin/core"
mkdir -p "$BASE_DIR/bin/build"
mkdir -p "$BASE_DIR/bin/test"
mkdir -p "$BASE_DIR/bin/component"
mkdir -p "$BASE_DIR/bin/dev"
mkdir -p "$BASE_DIR/bin/migration"
mkdir -p "$BASE_DIR/bin/help"
mkdir -p "$BASE_DIR/bin/utils"

# Organize s8r scripts by function
# Core scripts
print_message "Organizing core scripts..."
for script in s8r s8r-init s8r-list; do
  if [[ -f "$BASE_DIR/$script" ]]; then
    # Copy the script to the bin/core directory
    cp -v "$BASE_DIR/$script" "$BASE_DIR/bin/core/$script"
    print_message "Copied $script to bin/core/$script"
    print_message "⚠️  Warning: Scripts are now in TWO locations - update references to use bin/core/$script"
  fi
done

# Build scripts
print_message "Organizing build scripts..."
for script in s8r-build s8r-build-new s8r-ci; do
  if [[ -f "$BASE_DIR/$script" ]]; then
    # Copy the script to the bin/build directory
    cp -v "$BASE_DIR/$script" "$BASE_DIR/bin/build/$script"
    print_message "Copied $script to bin/build/$script"
    print_message "⚠️  Warning: Scripts are now in TWO locations - update references to use bin/build/$script"
  fi
done

# Test scripts
print_message "Organizing test scripts..."
for script in s8r-test s8r-test-new s8r-test-cli s8r-run-tests.sh; do
  if [[ -f "$BASE_DIR/$script" ]]; then
    # Copy the script to the bin/test directory
    cp -v "$BASE_DIR/$script" "$BASE_DIR/bin/test/$script"
    print_message "Copied $script to bin/test/$script"
    print_message "⚠️  Warning: Scripts are now in TWO locations - update references to use bin/test/$script"
  fi
done

# Component scripts
print_message "Organizing component scripts..."
for script in s8r-component s8r-composite s8r-machine s8r-tube; do
  if [[ -f "$BASE_DIR/$script" ]]; then
    # Copy the script to the bin/component directory
    cp -v "$BASE_DIR/$script" "$BASE_DIR/bin/component/$script"
    print_message "Copied $script to bin/component/$script"
    print_message "⚠️  Warning: Scripts are now in TWO locations - update references to use bin/component/$script"
  fi
done

# Dev scripts
print_message "Organizing dev scripts..."
for script in s8r-dev s8r-dev-help-build; do
  if [[ -f "$BASE_DIR/$script" ]]; then
    # Copy the script to the bin/dev directory
    cp -v "$BASE_DIR/$script" "$BASE_DIR/bin/dev/$script"
    print_message "Copied $script to bin/dev/$script"
    print_message "⚠️  Warning: Scripts are now in TWO locations - update references to use bin/dev/$script"
  fi
done

# Migration scripts
print_message "Organizing migration scripts..."
for script in s8r-migration-monitor s8r-migration-report s8r-architecture-verify s8r-structure-verify; do
  if [[ -f "$BASE_DIR/$script" ]]; then
    # Copy the script to the bin/migration directory
    cp -v "$BASE_DIR/$script" "$BASE_DIR/bin/migration/$script"
    print_message "Copied $script to bin/migration/$script"
    print_message "⚠️  Warning: Scripts are now in TWO locations - update references to use bin/migration/$script"
  fi
done

# Help scripts
print_message "Organizing help scripts..."
for script in s8r-help s8r-help-build s8r-help-component; do
  if [[ -f "$BASE_DIR/$script" ]]; then
    # Copy the script to the bin/help directory
    cp -v "$BASE_DIR/$script" "$BASE_DIR/bin/help/$script"
    print_message "Copied $script to bin/help/$script"
    print_message "⚠️  Warning: Scripts are now in TWO locations - update references to use bin/help/$script"
  fi
done

# Utility scripts
print_message "Organizing utility scripts..."
for script in s8r-docs s8r-quality s8r-version s8r-version-robust s8r-version-simple; do
  if [[ -f "$BASE_DIR/$script" ]]; then
    # Copy the script to the bin/utils directory
    cp -v "$BASE_DIR/$script" "$BASE_DIR/bin/utils/$script"
    print_message "Copied $script to bin/utils/$script"
    print_message "⚠️  Warning: Scripts are now in TWO locations - update references to use bin/utils/$script"
  fi
done

# Additional script to handle choice between -new scripts
print_message "Creating script selection utility..."
cat > "$BASE_DIR/bin/utils/use-new-scripts.sh" << 'EOF'
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

BASE_DIR=$(pwd)
if [[ ! -d "$BASE_DIR/util" ]]; then
  BASE_DIR="/home/emumford/NativeLinuxProjects/Samstraumr"
  if [[ ! -d "$BASE_DIR/util" ]]; then
    print_error "Error: Cannot find Samstraumr project root."
    exit 1
  fi
fi

function show_usage() {
  echo "Usage: $0 [command]"
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
    if [[ -f "$BASE_DIR/s8r-$script" && -f "$BASE_DIR/s8r-$script-new" ]]; then
      echo "s8r-$script:"
      echo "  - Original: $(stat -c %y "$BASE_DIR/s8r-$script")"
      echo "  - New:      $(stat -c %y "$BASE_DIR/s8r-$script-new")"
      echo ""
    fi
  done
}

function use_new_scripts() {
  print_message "Switching to new script versions..."
  
  for script in build test; do
    if [[ -f "$BASE_DIR/s8r-$script" && -f "$BASE_DIR/s8r-$script-new" ]]; then
      # Backup original if not already backed up
      if [[ ! -f "$BASE_DIR/s8r-$script.orig" ]]; then
        cp -v "$BASE_DIR/s8r-$script" "$BASE_DIR/s8r-$script.orig"
      fi
      
      # Replace with new version
      cp -v "$BASE_DIR/s8r-$script-new" "$BASE_DIR/s8r-$script"
      print_success "Updated s8r-$script to use new version"
    fi
  done
}

function use_old_scripts() {
  print_message "Switching to original script versions..."
  
  for script in build test; do
    if [[ -f "$BASE_DIR/s8r-$script.orig" ]]; then
      # Restore original
      cp -v "$BASE_DIR/s8r-$script.orig" "$BASE_DIR/s8r-$script"
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

chmod +x "$BASE_DIR/bin/utils/use-new-scripts.sh"
print_success "Created script selection utility at bin/utils/use-new-scripts.sh"

# Create the bin/s8r-all directory with copies for easy access to all scripts
print_message "Creating bin/s8r-all directory for easy access..."
mkdir -p "$BASE_DIR/bin/s8r-all"
for script in $(find "$BASE_DIR" -maxdepth 1 -name "s8r*" -type f -o -name "s8r*" -type l); do
  script_name=$(basename "$script")
  if [[ -f "$script" ]]; then
    cp -v "$script" "$BASE_DIR/bin/s8r-all/$script_name"
    print_message "Copied $script_name to bin/s8r-all/$script_name"
  fi
done
print_message "⚠️  Warning: Scripts are now in THREE locations (root, category directory, and s8r-all). You should decide on ONE location to use."

# Add README.md to bin directory
print_message "Creating README for bin directory..."
cat > "$BASE_DIR/bin/README.md" << 'EOF'
# Samstraumr Scripts Directory

This directory contains organized scripts for the Samstraumr project. The scripts are organized into the following categories:

## Directory Structure

- `core/` - Core S8r framework scripts (s8r, s8r-init, s8r-list)
- `build/` - Build-related scripts (s8r-build, s8r-ci)
- `test/` - Testing scripts (s8r-test, s8r-test-cli)
- `component/` - Component-related scripts (s8r-component, s8r-composite, s8r-machine, s8r-tube)
- `dev/` - Development scripts (s8r-dev)
- `migration/` - Migration-related scripts (s8r-migration-monitor, s8r-architecture-verify)
- `help/` - Help documentation scripts (s8r-help)
- `utils/` - Utility scripts (s8r-docs, s8r-quality, s8r-version)
- `s8r-all/` - All s8r scripts in one place for convenience

## Script Migrations

Some scripts have both original and new versions. To manage these versions, use:

```bash
# List available script versions
./bin/utils/use-new-scripts.sh list

# Switch to new versions of scripts
./bin/utils/use-new-scripts.sh use-new

# Switch back to original versions
./bin/utils/use-new-scripts.sh use-old
```

## Important Note

These scripts are copies of the scripts in the root directory. Our goal is to eventually move all scripts from the root directory into this organized structure. Until then, be aware that:

1. Scripts exist in multiple locations
2. You should choose ONE location to use consistently
3. We recommend using the bin directory structure for better organization
4. Any updates to scripts should be made in all locations until we complete the transition

In the future, we will remove the scripts from the root directory entirely and only maintain them in the bin structure.
EOF

print_success "S8r scripts organization completed!"
print_message "You can now access all s8r scripts through the bin directory."