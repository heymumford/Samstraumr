#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

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
