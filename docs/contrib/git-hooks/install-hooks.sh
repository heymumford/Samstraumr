#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Install Samstraumr git hooks

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RESET='\033[0m'

# Detect script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../.." && pwd)"

echo -e "${BLUE}Installing git hooks for Samstraumr...${RESET}"

# Create hooks directory if it doesn't exist
mkdir -p "${PROJECT_ROOT}/.git/hooks"

# Check if act is installed (needed for pre-commit hook)
if ! command -v act &> /dev/null; then
  echo -e "${YELLOW}Warning: 'act' is not installed. The pre-commit hook will skip CI checks.${RESET}"
  echo -e "${YELLOW}To install act, visit: https://github.com/nektos/act${RESET}"
fi

# Install prepare-commit-msg hook
cp "${SCRIPT_DIR}/prepare-commit-msg" "${PROJECT_ROOT}/.git/hooks/"
chmod +x "${PROJECT_ROOT}/.git/hooks/prepare-commit-msg"
echo -e "${GREEN}✓ Installed prepare-commit-msg hook${RESET}"

# Install pre-commit hook
cp "${SCRIPT_DIR}/pre-commit" "${PROJECT_ROOT}/.git/hooks/"
chmod +x "${PROJECT_ROOT}/.git/hooks/pre-commit"
echo -e "${GREEN}✓ Installed pre-commit hook${RESET}"

echo -e "\n${GREEN}Hooks installed successfully!${RESET}"
echo -e "${BLUE}The following hooks are active:${RESET}"
echo -e "- prepare-commit-msg: Prevents AI assistant attribution in commit messages"
echo -e "- pre-commit: Runs local CI checks before commits (use --no-verify to bypass)"