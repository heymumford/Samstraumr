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

# Ensure all test scripts symlinks point to util/scripts/test
for script in maven-profile-test.sh maven-structure-test.sh run-architecture-tests.sh test-maven-structure.sh; do
  # Check if the script exists in util/scripts/test
  if [[ -f "$BASE_DIR/util/scripts/test/$script" ]]; then
    # Create/update symlink
    if [[ -L "$BASE_DIR/$script" ]]; then
      rm -v "$BASE_DIR/$script"
    elif [[ -f "$BASE_DIR/$script" ]]; then
      print_message "Moving $script to util/scripts/test/"
      mv -v "$BASE_DIR/$script" "$BASE_DIR/util/scripts/test/$script"
    fi
    ln -svf "util/scripts/test/$script" "$BASE_DIR/$script"
  else
    # If not in util/scripts/test, move it there first
    if [[ -f "$BASE_DIR/$script" && ! -L "$BASE_DIR/$script" ]]; then
      print_message "Moving $script to util/scripts/test/"
      mv -v "$BASE_DIR/$script" "$BASE_DIR/util/scripts/test/$script"
      ln -svf "util/scripts/test/$script" "$BASE_DIR/$script"
    fi
  fi
done

# 6. Clean up other symlinks
print_message "Organizing other symlinks..."

# Ensure trigger-workflow.sh points to util/scripts/ci
if [[ -f "$BASE_DIR/util/scripts/ci/trigger-workflow.sh" ]]; then
  if [[ -L "$BASE_DIR/trigger-workflow.sh" ]]; then
    rm -v "$BASE_DIR/trigger-workflow.sh"
  elif [[ -f "$BASE_DIR/trigger-workflow.sh" ]]; then
    print_message "Moving trigger-workflow.sh to util/scripts/ci/"
    mv -v "$BASE_DIR/trigger-workflow.sh" "$BASE_DIR/util/scripts/ci/trigger-workflow.sh"
  fi
  ln -svf "util/scripts/ci/trigger-workflow.sh" "$BASE_DIR/trigger-workflow.sh"
fi

# Ensure use-java21.sh points to util/scripts/java
if [[ -f "$BASE_DIR/util/scripts/java/use-java21.sh" ]]; then
  if [[ -L "$BASE_DIR/use-java21.sh" ]]; then
    rm -v "$BASE_DIR/use-java21.sh"
  elif [[ -f "$BASE_DIR/use-java21.sh" ]]; then
    print_message "Moving use-java21.sh to util/scripts/java/"
    mv -v "$BASE_DIR/use-java21.sh" "$BASE_DIR/util/scripts/java/use-java21.sh"
  fi
  ln -svf "util/scripts/java/use-java21.sh" "$BASE_DIR/use-java21.sh"
fi

# Ensure update-standardized-headers.sh points to util/scripts
if [[ -f "$BASE_DIR/util/scripts/update-standardized-headers.sh" ]]; then
  if [[ -L "$BASE_DIR/update-standardized-headers.sh" ]]; then
    rm -v "$BASE_DIR/update-standardized-headers.sh"
  elif [[ -f "$BASE_DIR/update-standardized-headers.sh" ]]; then
    print_message "Moving update-standardized-headers.sh to util/scripts/"
    mv -v "$BASE_DIR/update-standardized-headers.sh" "$BASE_DIR/util/scripts/update-standardized-headers.sh"
  fi
  ln -svf "util/scripts/update-standardized-headers.sh" "$BASE_DIR/update-standardized-headers.sh"
fi

# Ensure run-initializer.sh points to util/scripts
if [[ -f "$BASE_DIR/util/scripts/initialize.sh" ]]; then
  if [[ -L "$BASE_DIR/run-initializer.sh" ]]; then
    rm -v "$BASE_DIR/run-initializer.sh"
  elif [[ -f "$BASE_DIR/run-initializer.sh" ]]; then
    print_message "Moving run-initializer.sh to util/scripts/initialize.sh"
    mv -v "$BASE_DIR/run-initializer.sh" "$BASE_DIR/util/scripts/initialize.sh"
  fi
  ln -svf "util/scripts/initialize.sh" "$BASE_DIR/run-initializer.sh"
fi

# Create create-script-symlinks.sh in util/scripts if it doesn't exist
if [[ -f "$BASE_DIR/create-script-symlinks.sh" && ! -f "$BASE_DIR/util/scripts/create-script-symlinks.sh" ]]; then
  print_message "Moving create-script-symlinks.sh to util/scripts/"
  mv -v "$BASE_DIR/create-script-symlinks.sh" "$BASE_DIR/util/scripts/create-script-symlinks.sh"
  chmod +x "$BASE_DIR/util/scripts/create-script-symlinks.sh"
  ln -svf "util/scripts/create-script-symlinks.sh" "$BASE_DIR/create-script-symlinks.sh"
elif [[ -f "$BASE_DIR/create-script-symlinks.sh" && -f "$BASE_DIR/util/scripts/create-script-symlinks.sh" ]]; then
  # Replace with symlink if it exists in both places
  rm -v "$BASE_DIR/create-script-symlinks.sh"
  ln -svf "util/scripts/create-script-symlinks.sh" "$BASE_DIR/create-script-symlinks.sh"
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

print_success "Root directory cleanup completed!"
print_message "Please review any remaining duplicates manually."