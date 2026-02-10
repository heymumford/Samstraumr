#!/bin/bash
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0
#
# Script to install Git hooks for Samstraumr

set -e

# Repository root
REPO_ROOT="$(git rev-parse --show-toplevel)"

# Git hooks directory
GIT_HOOKS_DIR="${REPO_ROOT}/.git/hooks"

# Source hooks directory
SOURCE_HOOKS_DIR="${REPO_ROOT}/util/git"

# Create hooks directory if it doesn't exist
mkdir -p "$GIT_HOOKS_DIR"

# Copy pre-push hook
if [ -f "${SOURCE_HOOKS_DIR}/pre-push-hook.sh" ]; then
    cp "${SOURCE_HOOKS_DIR}/pre-push-hook.sh" "${GIT_HOOKS_DIR}/pre-push"
    chmod +x "${GIT_HOOKS_DIR}/pre-push"
    echo "Installed pre-push hook"
else
    echo "pre-push hook not found at ${SOURCE_HOOKS_DIR}/pre-push-hook.sh"
fi

# Copy other hooks if they exist
for hook in pre-commit prepare-commit-msg commit-msg; do
    if [ -f "${SOURCE_HOOKS_DIR}/${hook}-hook.sh" ]; then
        cp "${SOURCE_HOOKS_DIR}/${hook}-hook.sh" "${GIT_HOOKS_DIR}/${hook}"
        chmod +x "${GIT_HOOKS_DIR}/${hook}"
        echo "Installed ${hook} hook"
    fi
done

echo "Git hooks installation complete"