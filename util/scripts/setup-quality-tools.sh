#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Incremental Quality Tool Enablement Script for Samstraumr
# This script helps enable quality tools one by one

set -e # Exit on error

# Get script directory for relative path resolution
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." &> /dev/null && pwd 2> /dev/null || echo "$SCRIPT_DIR")"

# Change to project root directory
cd "$PROJECT_ROOT"

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_header() {
  echo -e "\n${BLUE}====== $1 ======${NC}\n"
}

print_success() {
  echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
  echo -e "${RED}✗ $1${NC}"
}

print_warning() {
  echo -e "${YELLOW}! $1${NC}"
}

print_header "Samstraumr Quality Tool Enablement Wizard"
echo "This script will help you enable quality tools one by one."
echo "You can choose which tools to enable and in what order."

# Check if build-checks.sh exists
if [ ! -f "$SCRIPT_DIR/build-checks.sh" ]; then
  print_error "build-checks.sh not found in $SCRIPT_DIR"
  exit 1
fi

# Make sure the script is executable
chmod +x "$SCRIPT_DIR/build-checks.sh"

# Show option menu
show_menu() {
  echo ""
  echo "Available quality tools:"
  echo "1) Spotless (Code Formatting)"
  echo "2) Checkstyle (Coding Standards)"
  echo "3) SpotBugs (Bug Detection)"
  echo "4) JaCoCo (Code Coverage)"
  echo "5) Run All Above The Line (ATL) Tests"
  echo "6) Run All Below The Line (BTL) Tests"
  echo "7) Run All Tools in Sequence"
  echo "8) Exit"
  echo ""
  echo -n "Enter your choice (1-8): "
}

run_tool() {
  local tool=$1
  case $tool in
    1|spotless)
      print_header "Running Spotless (Code Formatting)"
      "$SCRIPT_DIR/build-checks.sh" --only=spotless
      ;;
    2|checkstyle)
      print_header "Running Checkstyle (Coding Standards)"
      "$SCRIPT_DIR/build-checks.sh" --only=checkstyle
      ;;
    3|spotbugs)
      print_header "Running SpotBugs (Bug Detection)"
      "$SCRIPT_DIR/build-checks.sh" --only=spotbugs
      ;;
    4|jacoco)
      print_header "Running JaCoCo (Code Coverage)"
      "$SCRIPT_DIR/build-checks.sh" --only=jacoco
      ;;
    5|atl)
      print_header "Running ATL Tests"
      mvn test -P atl-tests
      ;;
    6|btl)
      print_header "Running BTL Tests"
      mvn test -P btl-tests
      ;;
    7|all)
      print_header "Running All Tools in Sequence"
      for t in {1..6}; do
        run_tool $t || true
        echo ""
      done
      ;;
    *)
      print_error "Invalid choice: $tool"
      ;;
  esac
}

# Main loop
while true; do
  show_menu
  read choice
  
  if [ "$choice" = "8" ]; then
    print_header "Exiting Quality Tool Enablement Wizard"
    exit 0
  fi
  
  if [ "$choice" -ge 1 ] && [ "$choice" -le 7 ]; then
    run_tool "$choice"
  else
    print_error "Invalid choice. Please enter a number between 1 and 8."
  fi
done