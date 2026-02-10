#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#\!/bin/bash

# Script to create symbolic links for reorganized scripts

# Root directory
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

create_symlink() {
  local source_file="$1"
  local target_file="$2"
  
  # Check if target exists
  if [ \! -f "$target_file" ]; then
    echo "ERROR: Target file does not exist: $target_file"
    return 1
  fi
  
  # Handle existing symlink/file
  if [ -L "$source_file" ]; then
    echo "Removing existing symlink: $source_file"
    rm "$source_file"
  elif [ -f "$source_file" ]; then
    echo "WARNING: Original file exists, not overwriting: $source_file"
    return 1
  fi
  
  # Create the symbolic link
  echo "Creating symlink: $source_file -> $target_file"
  ln -s "$target_file" "$source_file"
  return 0
}

# Create symbolic links for all reorganized scripts
echo "Creating symbolic links for reorganized scripts..."

# Maven structure scripts
create_symlink "$ROOT_DIR/test-maven-structure.sh" "$ROOT_DIR/util/scripts/test/maven-structure-test.sh"
create_symlink "$ROOT_DIR/maven-structure-test.sh" "$ROOT_DIR/util/scripts/test/maven-structure-test.sh"
create_symlink "$ROOT_DIR/maven-profile-test.sh" "$ROOT_DIR/util/scripts/test/maven-profile-test.sh"

# Architecture test scripts
create_symlink "$ROOT_DIR/run-architecture-tests.sh" "$ROOT_DIR/util/scripts/test/run-architecture-tests.sh"

# Initialization and workflow scripts
create_symlink "$ROOT_DIR/run-initializer.sh" "$ROOT_DIR/util/scripts/initialize.sh"
create_symlink "$ROOT_DIR/trigger-workflow.sh" "$ROOT_DIR/util/scripts/ci/trigger-workflow.sh"

# Utility scripts
create_symlink "$ROOT_DIR/update-standardized-headers.sh" "$ROOT_DIR/util/scripts/update-standardized-headers.sh"
create_symlink "$ROOT_DIR/use-java21.sh" "$ROOT_DIR/util/scripts/java/use-java21.sh"

echo "Symbolic links creation completed."
