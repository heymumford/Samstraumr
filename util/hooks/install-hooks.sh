#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# ==============================================================================
# Install Git Hooks
# ==============================================================================
# Installs local git hooks from util/hooks/ to .git/hooks/
# Run this once after cloning the repository.
# ==============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
HOOKS_DIR="$REPO_ROOT/.git/hooks"

echo "Installing git hooks..."

# Create hooks directory if it doesn't exist
mkdir -p "$HOOKS_DIR"

# Install pre-push hook
if [[ -f "$SCRIPT_DIR/pre-push" ]]; then
    cp "$SCRIPT_DIR/pre-push" "$HOOKS_DIR/pre-push"
    chmod +x "$HOOKS_DIR/pre-push"
    echo "  ✓ pre-push hook installed"
else
    echo "  ⚠ pre-push hook not found"
fi

echo ""
echo "Git hooks installed successfully!"
echo ""
echo "Usage:"
echo "  - Pre-push hook runs automatically before 'git push'"
echo "  - Skip with: SKIP_PREPUSH=true git push"
echo "  - Quick mode: QUICK_MODE=true git push"
echo ""
