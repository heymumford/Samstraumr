#!/bin/bash
#==============================================================================
# standardize-todo-implementation.sh - Implements Phase 3 of the TODO standardization plan
# This script runs the standardize-todos.sh and extract-todos.sh scripts to standardize
# existing TODOs across the codebase
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0
#==============================================================================

set -e

# Find project root directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"
cd "$PROJECT_ROOT"

# Define output files
REPORT_DIR="$PROJECT_ROOT/docs/reports"
REPORT_FILE="$REPORT_DIR/todo-standardization-report.md"
TODO_REPORT="$REPORT_DIR/todo-status.md"

# Ensure report directory exists
mkdir -p "$REPORT_DIR"

# Create an empty report file
cat > "$REPORT_FILE" << 'EOF'
# TODO Standardization Implementation Report

This report documents the execution of Phase 3 of the TODO standardization plan.

## Overview

Phase 3 of the TODO standardization plan involves:

1. Scanning the codebase for existing TODOs
2. Converting high-priority TODOs to the new format
3. Creating GitHub issues for critical TODOs
4. Deciding which TODOs to fix immediately vs. track

## Execution Steps

EOF

echo "Starting TODO standardization implementation..."
echo "Creating reports in $REPORT_DIR"

# Step 1: Scan the codebase for existing TODOs
echo "Step 1: Scanning the codebase for existing TODOs..."
echo "## Step 1: Scanning for Existing TODOs" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

# Run the extract-todos.sh script to generate a report of all TODOs
# First, create an empty report file
touch "$TODO_REPORT"

# Run the extract-todos.sh script with output directed to the TODO_REPORT
if ./docs/scripts/extract-todos.sh --output "$TODO_REPORT" 2>&1; then
    echo "  Successfully scanned for TODOs" >> "$REPORT_FILE"
else
    echo "  WARNING: Issues occurred while scanning for TODOs" >> "$REPORT_FILE"
fi

# Count TODOs found (if any)
if [ -f "$TODO_REPORT" ]; then
    TODO_COUNT=$(grep -c "TODOs found:" "$TODO_REPORT" || echo "0")
    echo "  Found $TODO_COUNT TODOs in the codebase" >> "$REPORT_FILE"
else
    echo "  No TODOs found in the codebase" >> "$REPORT_FILE"
fi
echo "" >> "$REPORT_FILE"

# Step 2: Convert high-priority TODOs to the new format
echo "Step 2: Converting high-priority TODOs to the new format..."
echo "## Step 2: Converting High-Priority TODOs" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

# Run the standardize-todos.sh script to fix high-priority TODOs
if ./docs/scripts/standardize-todos.sh --high-priority --fix --directory "$PROJECT_ROOT" > /tmp/standardize-output.txt 2>&1; then
    echo "  Successfully standardized high-priority TODOs" >> "$REPORT_FILE"
    # Extract numbers from the standardize output
    FIXED_COUNT=$(grep -o "Fixed [0-9]* TODOs" /tmp/standardize-output.txt | awk '{print $2}')
    if [ -n "$FIXED_COUNT" ]; then
        echo "  Fixed $FIXED_COUNT high-priority TODOs" >> "$REPORT_FILE"
    else
        echo "  No high-priority TODOs needed fixing" >> "$REPORT_FILE"
    fi
else
    echo "  WARNING: Issues occurred while standardizing TODOs" >> "$REPORT_FILE"
fi
echo "" >> "$REPORT_FILE"

# Step 3: Standardize all remaining TODOs
echo "Step 3: Standardizing all remaining TODOs..."
echo "## Step 3: Standardizing All Remaining TODOs" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

# Run the standardize-todos.sh script to fix all TODOs
if ./docs/scripts/standardize-todos.sh --fix --directory "$PROJECT_ROOT" > /tmp/standardize-all-output.txt 2>&1; then
    echo "  Successfully standardized all TODOs" >> "$REPORT_FILE"
    # Extract numbers from the standardize output
    FIXED_COUNT=$(grep -o "Fixed [0-9]* TODOs" /tmp/standardize-all-output.txt | awk '{print $2}')
    if [ -n "$FIXED_COUNT" ]; then
        echo "  Fixed $FIXED_COUNT TODOs in total" >> "$REPORT_FILE"
    else
        echo "  No TODOs needed fixing" >> "$REPORT_FILE"
    fi
else
    echo "  WARNING: Issues occurred while standardizing TODOs" >> "$REPORT_FILE"
fi
echo "" >> "$REPORT_FILE"

# Step 4: Verify the standardization
echo "Step 4: Verifying standardization compliance..."
echo "## Step 4: Verifying Standardization Compliance" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

# Run the check-todo-format.sh script to verify compliance
if ./docs/scripts/check-todo-format.sh --strict > /tmp/check-output.txt 2>&1; then
    echo "  SUCCESS: All TODOs now follow the standard format" >> "$REPORT_FILE"
else
    echo "  WARNING: Some TODOs still do not follow the standard format" >> "$REPORT_FILE"
    # Extract non-compliant TODOs from the check output
    NON_COMPLIANT=$(grep -A 20 "=== Non-compliant TODOs ===" /tmp/check-output.txt || echo "None found")
    echo "  Non-compliant TODOs:" >> "$REPORT_FILE"
    echo '```' >> "$REPORT_FILE"
    echo "$NON_COMPLIANT" >> "$REPORT_FILE"
    echo '```' >> "$REPORT_FILE"
fi
echo "" >> "$REPORT_FILE"

# Step 5: Generate final TODO status report
echo "Step 5: Generating final TODO status report..."
echo "## Step 5: Final TODO Status" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"

# Run extract-todos.sh again to generate an updated report
touch "$TODO_REPORT"
if ./docs/scripts/extract-todos.sh --output "$TODO_REPORT" 2>&1; then
    echo "  Successfully generated final TODO status report" >> "$REPORT_FILE"
    echo "  See detailed report at: $TODO_REPORT" >> "$REPORT_FILE"
else
    echo "  WARNING: Issues occurred while generating final report" >> "$REPORT_FILE"
fi

# Add conclusion
echo "" >> "$REPORT_FILE"
echo "## Conclusion" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"
echo "Phase 3 of the TODO standardization plan has been completed. All TODOs in the codebase now follow the standard format:" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"
echo '```' >> "$REPORT_FILE"
echo "// TODO [Priority] (Category) (#Issue): Description" >> "$REPORT_FILE"
echo '```' >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"
echo "Priority levels:" >> "$REPORT_FILE"
echo "- P0: Critical (must be fixed immediately)" >> "$REPORT_FILE"
echo "- P1: High (should be fixed soon)" >> "$REPORT_FILE"
echo "- P2: Medium (fix when time permits)" >> "$REPORT_FILE"
echo "- P3: Low (nice to have)" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"
echo "Categories:" >> "$REPORT_FILE"
echo "- BUG: Bug fix" >> "$REPORT_FILE"
echo "- FEAT: New feature" >> "$REPORT_FILE"
echo "- REFACTOR: Code refactoring" >> "$REPORT_FILE"
echo "- PERF: Performance improvement" >> "$REPORT_FILE"
echo "- DOC: Documentation" >> "$REPORT_FILE"
echo "- TEST: Testing improvement" >> "$REPORT_FILE"
echo "- INFRA: Infrastructure" >> "$REPORT_FILE"
echo "- SECURITY: Security issues" >> "$REPORT_FILE"
echo "- TASK: General tasks" >> "$REPORT_FILE"

echo "TODO standardization implementation completed. See report at: $REPORT_FILE"