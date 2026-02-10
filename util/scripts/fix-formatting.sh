#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Fix code formatting with Spotless and create index files
# This script applies Spotless formatting and builds the index files

set -e

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." &> /dev/null && pwd 2> /dev/null)"

# Set colored output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
RESET='\033[0m'

echo -e "${CYAN}===== Fixing code formatting with Spotless =====${RESET}"

# Change to project root directory
cd "$PROJECT_ROOT"

# Apply Spotless formatting to create the index
echo -e "${YELLOW}Applying Spotless formatting...${RESET}"
mvn spotless:apply

# Now build the index by running a check
echo -e "${YELLOW}Building Spotless index...${RESET}"
mvn spotless:check

echo -e "${GREEN}✓ Spotless formatting fixed and index built${RESET}"
