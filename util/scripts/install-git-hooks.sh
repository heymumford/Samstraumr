#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Script to install the git hooks for Samstraumr
# This ensures consistent code quality and prevents accidental commits with issues

# Source common functions
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/../lib/common.sh"

# Display header
echo "=================================="
echo " Installing Git Hooks"
echo "=================================="
echo "Setting up hooks to enforce quality standards"
echo

# Create the hooks directory if it doesn't exist
HOOKS_DIR="$REPO_ROOT/.git/hooks"
mkdir -p "$HOOKS_DIR"

# Install the pre-commit hook
echo "Installing pre-commit hook..."
cat > "$HOOKS_DIR/pre-commit" << 'EOF'
#!/bin/bash

# Pre-commit hook for Samstraumr
# This hook runs various checks before allowing a commit to proceed
# Exit with non-zero status to abort the commit

# Source the common library
source "$(git rev-parse --show-toplevel)/util/lib/common.sh"

# Display header
echo "=================================="
echo " Running Pre-Commit Checks"
echo "=================================="
echo "Verifying code quality before commit"
echo

# Get the list of staged Java files
STAGED_JAVA_FILES=$(git diff --cached --name-only --diff-filter=ACM | grep "\.java$")
STAGED_FILES_COUNT=$(echo "$STAGED_JAVA_FILES" | grep -v "^$" | wc -l)

if [ "$STAGED_FILES_COUNT" -gt 0 ]; then
    echo -e "\033[1mFound $STAGED_FILES_COUNT staged Java files\033[0m"
    
    # Run style checks on staged files
    echo -e "\033[34m→ Running style checks...\033[0m"
    if [ -x "$REPO_ROOT/util/scripts/check-imports.sh" ]; then
        "$REPO_ROOT/util/scripts/check-imports.sh"
        if [ $? -ne 0 ]; then
            echo -e "\033[31m✗ Import check failed. Please run 'util/scripts/fix-imports.sh' and stage your changes.\033[0m"
            exit 1
        fi
    fi
    
    # Check for circular dependencies if we have more than 5 files staged
    # (smaller changes don't need this heavy check)
    if [ "$STAGED_FILES_COUNT" -gt 5 ]; then
        echo -e "\033[34m→ Checking for circular dependencies...\033[0m"
        if [ -x "$REPO_ROOT/util/scripts/check-circular-dependencies.sh" ]; then
            "$REPO_ROOT/util/scripts/check-circular-dependencies.sh"
            if [ $? -ne 0 ]; then
                echo -e "\033[31m✗ Circular dependencies detected. Please fix these issues before committing.\033[0m"
                exit 1
            fi
        fi
    else
        echo -e "\033[34m→ Skipping circular dependency check (less than 6 files staged)\033[0m"
    fi
fi

# Verify proper test references
echo -e "\033[34m→ Verifying test references...\033[0m"
if [ -d "$REPO_ROOT/modules/samstraumr-core/src/test" ]; then
    # Check that any added test classes are properly annotated
    NEW_TEST_FILES=$(git diff --cached --name-only --diff-filter=A | grep -E "Test\.java$|Tests\.java$")
    for TEST_FILE in $NEW_TEST_FILES; do
        if ! grep -q "@Tag(" "$TEST_FILE" 2>/dev/null; then
            echo -e "\033[31m✗ Test file $TEST_FILE is missing required @Tag annotation.\033[0m"
            echo "  Please add appropriate test tags. See org.s8r.test.annotation package for options."
            exit 1
        fi
    done
fi

# Run architecture tests for significant changes
if [ "$STAGED_FILES_COUNT" -gt 10 ]; then
    echo -e "\033[34m→ Running architecture validation...\033[0m"
    if [ -x "$REPO_ROOT/run-architecture-tests.sh" ]; then
        # Only run a subset of architecture tests for quick validation
        cd "$REPO_ROOT" && ./run-architecture-tests.sh quick
        if [ $? -ne 0 ]; then
            echo -e "\033[31m✗ Architecture validation failed. Please fix architecture issues before committing.\033[0m"
            exit 1
        fi
    fi
else
    echo -e "\033[34m→ Skipping architecture validation (less than 11 files staged)\033[0m"
fi

echo -e "\033[32m✓ All pre-commit checks passed!\033[0m"
exit 0
EOF

# Make the hook executable
chmod +x "$HOOKS_DIR/pre-commit"

# Install the pre-push hook
echo "Installing pre-push hook..."
cat > "$HOOKS_DIR/pre-push" << 'EOF'
#!/bin/bash

# Pre-push hook for Samstraumr
# This hook runs full tests before allowing a push to proceed
# Exit with non-zero status to abort the push

# Source the common library
source "$(git rev-parse --show-toplevel)/util/lib/common.sh"

# Display header
echo "=================================="
echo " Running Pre-Push Checks"
echo "=================================="
echo "Ensuring code quality before pushing"
echo

# Skip checks if --no-verify flag is used
if [ "$1" == "--no-verify" ]; then
    echo -e "\033[33mWarning: Skipping pre-push checks (--no-verify flag used)\033[0m"
    exit 0
fi

# Check if we're pushing to a protected branch
CURRENT_BRANCH=$(git symbolic-ref HEAD | sed -e 's,.*/\(.*\),\1,')
PROTECTED_BRANCHES="main master develop release"

for BRANCH in $PROTECTED_BRANCHES; do
    if [ "$CURRENT_BRANCH" == "$BRANCH" ]; then
        echo -e "\033[34m→ Pushing to protected branch '$BRANCH'. Running full validation.\033[0m"
        
        # Run full dependency check
        echo -e "\033[34m→ Running circular dependency check...\033[0m"
        if [ -x "$REPO_ROOT/util/scripts/check-circular-dependencies.sh" ]; then
            "$REPO_ROOT/util/scripts/check-circular-dependencies.sh"
            if [ $? -ne 0 ]; then
                echo -e "\033[31m✗ Circular dependencies detected. Please fix these issues before pushing.\033[0m"
                exit 1
            fi
        fi
        
        # Run full architecture tests
        echo -e "\033[34m→ Running architecture validation...\033[0m"
        if [ -x "$REPO_ROOT/run-architecture-tests.sh" ]; then
            cd "$REPO_ROOT" && ./run-architecture-tests.sh
            if [ $? -ne 0 ]; then
                echo -e "\033[31m✗ Architecture validation failed. Please fix architecture issues before pushing.\033[0m"
                exit 1
            fi
        fi
        
        # Run key unit tests
        echo -e "\033[34m→ Running core tests...\033[0m"
        if [ -x "$REPO_ROOT/s8r" ]; then
            cd "$REPO_ROOT" && ./s8r test unit
            if [ $? -ne 0 ]; then
                echo -e "\033[31m✗ Unit tests failed. Please fix failing tests before pushing.\033[0m"
                exit 1
            fi
        fi
        
        break
    fi
done

# If not pushing to a protected branch, run a faster verification
if [[ ! "$PROTECTED_BRANCHES" =~ "$CURRENT_BRANCH" ]]; then
    echo -e "\033[34m→ Pushing to feature branch '$CURRENT_BRANCH'. Running streamlined validation.\033[0m"
    
    # Run modified files through the dependency checker
    echo -e "\033[34m→ Checking modified files for circular dependencies...\033[0m"
    MODIFIED_FILES_COUNT=$(git diff --name-only HEAD@{1} | grep "\.java$" | wc -l)
    if [ "$MODIFIED_FILES_COUNT" -gt 5 ]; then
        if [ -x "$REPO_ROOT/util/scripts/check-circular-dependencies.sh" ]; then
            "$REPO_ROOT/util/scripts/check-circular-dependencies.sh"
            if [ $? -ne 0 ]; then
                echo -e "\033[31m✗ Circular dependencies detected. Please fix these issues before pushing.\033[0m"
                exit 1
            fi
        fi
    else
        echo -e "\033[34m→ Skipping circular dependency check (only $MODIFIED_FILES_COUNT files modified)\033[0m"
    fi
fi

echo -e "\033[32m✓ All pre-push checks passed!\033[0m"
exit 0
EOF

# Make the hook executable
chmod +x "$HOOKS_DIR/pre-push"

# Install commit-msg hook for commit message formatting
echo "Installing commit-msg hook..."
cat > "$HOOKS_DIR/commit-msg" << 'EOF'
#!/bin/bash

# Commit-msg hook for Samstraumr
# This hook enforces commit message conventions
# Based on Conventional Commits (https://www.conventionalcommits.org/)

# Get the commit message file
COMMIT_MSG_FILE=$1

# Read commit message
COMMIT_MSG=$(cat "$COMMIT_MSG_FILE")

# Check if the commit is a merge commit
if [[ "$COMMIT_MSG" =~ ^Merge ]]; then
    exit 0
fi

# Check if commit message follows the format: type: subject
if ! grep -qE "^(feat|fix|docs|style|refactor|perf|test|chore|build|ci|revert)(\(.+\))?: .+" "$COMMIT_MSG_FILE"; then
    echo -e "\033[31mError: Invalid commit message format.\033[0m"
    echo -e "\033[33mCommit message must start with a type followed by a colon and subject."
    echo "Valid types: feat, fix, docs, style, refactor, perf, test, chore, build, ci, revert"
    echo -e "Example: 'feat: add new feature' or 'fix(component): resolve null pointer issue'\033[0m"
    exit 1
fi

# Check subject line length (50 chars recommended, 72 max)
SUBJECT_LINE=$(head -n 1 "$COMMIT_MSG_FILE")
if [ ${#SUBJECT_LINE} -gt 72 ]; then
    echo -e "\033[31mError: Commit subject line is too long (${#SUBJECT_LINE} > 72 characters)\033[0m"
    echo -e "\033[33mPlease keep the subject line under 72 characters\033[0m"
    exit 1
fi

# If subject line is over 50 chars, warn but don't fail
if [ ${#SUBJECT_LINE} -gt 50 ]; then
    echo -e "\033[33mWarning: Consider keeping subject line under 50 characters for better readability\033[0m"
    echo -e "\033[33mCurrent length: ${#SUBJECT_LINE} characters\033[0m"
fi

exit 0
EOF

# Make the hook executable
chmod +x "$HOOKS_DIR/commit-msg"

echo -e "\n\033[32m✓ Git hooks installed successfully!\033[0m"
echo -e "The following hooks are now active:"
echo -e "  - pre-commit: Runs code quality checks before allowing a commit"
echo -e "  - pre-push: Runs tests and architecture validation before pushing"
echo -e "  - commit-msg: Enforces consistent commit message format"
echo
echo -e "To skip these checks in special cases:"
echo -e "  - For commits: git commit --no-verify"
echo -e "  - For pushes: git push --no-verify"
echo -e "\033[33mNote: Skipping checks should be done only in exceptional cases!\033[0m"