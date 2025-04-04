#!/bin/bash
# Install Samstraumr git hooks

# Detect script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../.." && pwd)"

echo "Installing git hooks for Samstraumr..."

# Create hooks directory if it doesn't exist
mkdir -p "${PROJECT_ROOT}/.git/hooks"

# Install prepare-commit-msg hook
cp "${SCRIPT_DIR}/prepare-commit-msg" "${PROJECT_ROOT}/.git/hooks/"
chmod +x "${PROJECT_ROOT}/.git/hooks/prepare-commit-msg"

echo "Hooks installed successfully!"
echo "The following hooks are active:"
echo "- prepare-commit-msg: Prevents AI assistant attribution in commit messages"