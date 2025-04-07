#!/bin/bash
# 
# Root directory cleanup script
# This script cleans up the root directory by removing duplicate files,
# organizing script files, and creating appropriate symlinks.
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

print_message "Starting root directory cleanup..."

# 1. Remove all .orig files from the root directory
print_message "Removing .orig files..."
find . -maxdepth 1 -name "*.orig" -exec rm -v {} \;

# 2. Create bin directory if it doesn't exist
if [[ ! -d "$BASE_DIR/bin" ]]; then
  print_message "Creating bin directory..."
  mkdir -p "$BASE_DIR/bin"
fi

# 3. Clean up version scripts
print_message "Organizing version scripts..."
# Keep only s8r-version-robust and make it the standard
if [[ -f "$BASE_DIR/s8r-version-robust" ]]; then
  # Remove older version files
  if [[ -f "$BASE_DIR/s8r-version-new" ]]; then
    rm -v "$BASE_DIR/s8r-version-new"
  fi
  if [[ -f "$BASE_DIR/s8r-version.new" ]]; then
    rm -v "$BASE_DIR/s8r-version.new"
  fi
  # Keep s8r-version-simple as a separate utility
  # Create symlink for s8r-version
  if [[ -L "$BASE_DIR/s8r-version" ]]; then
    rm -v "$BASE_DIR/s8r-version"
  fi
  ln -sf "$BASE_DIR/s8r-version-robust" "$BASE_DIR/s8r-version"
  print_success "s8r-version now points to s8r-version-robust"
fi

# 4. Move s8r scripts to bin directory
print_message "Moving s8r script files to bin directory..."

# Clean up test scripts
if [[ -f "$BASE_DIR/s8r-test-new" ]]; then
  # Compare with s8r-test to see if we should keep the new version
  if [[ -f "$BASE_DIR/s8r-test" ]]; then
    if diff -q "$BASE_DIR/s8r-test" "$BASE_DIR/s8r-test-new" > /dev/null; then
      print_message "s8r-test and s8r-test-new are identical. Keeping s8r-test."
      rm -v "$BASE_DIR/s8r-test-new"
    else
      print_message "s8r-test-new is different from s8r-test. Keeping both for now."
      # You can manually review and decide later
    fi
  fi
fi

# Clean up build scripts
if [[ -f "$BASE_DIR/s8r-build-new" ]]; then
  # Compare with s8r-build to see if we should keep the new version
  if [[ -f "$BASE_DIR/s8r-build" ]]; then
    if diff -q "$BASE_DIR/s8r-build" "$BASE_DIR/s8r-build-new" > /dev/null; then
      print_message "s8r-build and s8r-build-new are identical. Keeping s8r-build."
      rm -v "$BASE_DIR/s8r-build-new"
    else
      print_message "s8r-build-new is different from s8r-build. Keeping both for now."
      # You can manually review and decide later
    fi
  fi
fi

# 5. Clean up symlinks to test scripts
print_message "Organizing test script symlinks..."

# Move test scripts to util/scripts/test directory
for script in maven-profile-test.sh maven-structure-test.sh run-architecture-tests.sh test-maven-structure.sh; do
  # Check if the script exists in the root directory
  if [[ -f "$BASE_DIR/$script" && ! -L "$BASE_DIR/$script" ]]; then
    print_message "Moving $script to util/scripts/test/"
    mv -v "$BASE_DIR/$script" "$BASE_DIR/util/scripts/test/$script"
    print_message "⚠️  Warning: $script moved to util/scripts/test/ - update any references"
  fi
  
  # Remove symlinks
  if [[ -L "$BASE_DIR/$script" ]]; then
    print_message "Removing symlink for $script"
    rm -v "$BASE_DIR/$script"
  fi
done

# 6. Move other utility scripts to appropriate directories
print_message "Moving utility scripts to appropriate directories..."

# Move trigger-workflow.sh to util/scripts/ci/
if [[ -f "$BASE_DIR/trigger-workflow.sh" && ! -L "$BASE_DIR/trigger-workflow.sh" ]]; then
  print_message "Moving trigger-workflow.sh to util/scripts/ci/"
  mkdir -p "$BASE_DIR/util/scripts/ci/"
  mv -v "$BASE_DIR/trigger-workflow.sh" "$BASE_DIR/util/scripts/ci/trigger-workflow.sh"
  print_message "⚠️  Warning: trigger-workflow.sh moved to util/scripts/ci/ - update any references"
fi
# Remove symlink if it exists
if [[ -L "$BASE_DIR/trigger-workflow.sh" ]]; then
  rm -v "$BASE_DIR/trigger-workflow.sh"
fi

# Move use-java21.sh to util/scripts/java/
if [[ -f "$BASE_DIR/use-java21.sh" && ! -L "$BASE_DIR/use-java21.sh" ]]; then
  print_message "Moving use-java21.sh to util/scripts/java/"
  mkdir -p "$BASE_DIR/util/scripts/java/"
  mv -v "$BASE_DIR/use-java21.sh" "$BASE_DIR/util/scripts/java/use-java21.sh"
  print_message "⚠️  Warning: use-java21.sh moved to util/scripts/java/ - update any references"
fi
# Remove symlink if it exists
if [[ -L "$BASE_DIR/use-java21.sh" ]]; then
  rm -v "$BASE_DIR/use-java21.sh"
fi

# Move update-standardized-headers.sh to util/scripts/
if [[ -f "$BASE_DIR/update-standardized-headers.sh" && ! -L "$BASE_DIR/update-standardized-headers.sh" ]]; then
  print_message "Moving update-standardized-headers.sh to util/scripts/"
  mv -v "$BASE_DIR/update-standardized-headers.sh" "$BASE_DIR/util/scripts/update-standardized-headers.sh"
  print_message "⚠️  Warning: update-standardized-headers.sh moved to util/scripts/ - update any references"
fi
# Remove symlink if it exists
if [[ -L "$BASE_DIR/update-standardized-headers.sh" ]]; then
  rm -v "$BASE_DIR/update-standardized-headers.sh"
fi

# Move run-initializer.sh to util/scripts/initialize.sh
if [[ -f "$BASE_DIR/run-initializer.sh" && ! -L "$BASE_DIR/run-initializer.sh" ]]; then
  print_message "Moving run-initializer.sh to util/scripts/initialize.sh"
  mv -v "$BASE_DIR/run-initializer.sh" "$BASE_DIR/util/scripts/initialize.sh"
  print_message "⚠️  Warning: run-initializer.sh moved to util/scripts/initialize.sh - update any references"
fi
# Remove symlink if it exists
if [[ -L "$BASE_DIR/run-initializer.sh" ]]; then
  rm -v "$BASE_DIR/run-initializer.sh"
fi

# Move create-script-symlinks.sh to util/scripts/
if [[ -f "$BASE_DIR/create-script-symlinks.sh" && ! -L "$BASE_DIR/create-script-symlinks.sh" ]]; then
  print_message "Moving create-script-symlinks.sh to util/scripts/"
  mv -v "$BASE_DIR/create-script-symlinks.sh" "$BASE_DIR/util/scripts/create-script-symlinks.sh"
  chmod +x "$BASE_DIR/util/scripts/create-script-symlinks.sh"
  print_message "⚠️  Warning: create-script-symlinks.sh moved to util/scripts/ - update any references"
fi
# Remove symlink if it exists
if [[ -L "$BASE_DIR/create-script-symlinks.sh" ]]; then
  rm -v "$BASE_DIR/create-script-symlinks.sh"
fi

# 7. Check for duplicate test scripts
print_message "Checking for duplicate test scripts..."
if [[ -f "$BASE_DIR/maven-profile-test.sh" && -f "$BASE_DIR/test-maven-structure.sh" ]]; then
  if diff -q "$BASE_DIR/maven-profile-test.sh" "$BASE_DIR/test-maven-structure.sh" > /dev/null; then
    print_message "maven-profile-test.sh and test-maven-structure.sh are identical."
    print_message "Consider keeping only one of them."
  fi
fi

# 8. Clean up s8r_* (underscore) symlinks
if [[ -L "$BASE_DIR/s8r_init" && -f "$BASE_DIR/s8r-init" ]]; then
  print_message "Removing s8r_init symlink (prefer s8r-init)"
  rm -v "$BASE_DIR/s8r_init"
fi

if [[ -L "$BASE_DIR/s8r_list" && -f "$BASE_DIR/s8r-list" ]]; then
  print_message "Removing s8r_list symlink (prefer s8r-list)"
  rm -v "$BASE_DIR/s8r_list"
fi

# 9. Organize git and config files
print_message "Organizing git and config files..."

# Create necessary directories
mkdir -p "$BASE_DIR/util/git"
mkdir -p "$BASE_DIR/util/config"

# Git commit template
if [[ -f "$BASE_DIR/.git-commit-template" ]]; then
  print_message "Moving .git-commit-template to util/git/"
  mv -v "$BASE_DIR/.git-commit-template" "$BASE_DIR/util/git/"
  print_message "⚠️  Warning: .git-commit-template moved to util/git/ - update any references"
fi

# Config files (except docmosis.properties which needs to stay in root)
for config_file in .actrc .editorconfig .samstraumr.config surefire-settings.xml; do
  if [[ -f "$BASE_DIR/$config_file" ]]; then
    print_message "Moving $config_file to util/config/"
    mv -v "$BASE_DIR/$config_file" "$BASE_DIR/util/config/"
    print_message "⚠️  Warning: $config_file moved to util/config/ - update any references"
  fi
done

# Keep a copy of docmosis.properties in util/config but don't remove from root
if [[ -f "$BASE_DIR/docmosis.properties" ]]; then
  print_message "Copying docmosis.properties to util/config/ (keeping original in root)"
  cp -v "$BASE_DIR/docmosis.properties" "$BASE_DIR/util/config/"
fi

print_success "Root directory cleanup completed!"
print_message "Please review any remaining duplicates manually."