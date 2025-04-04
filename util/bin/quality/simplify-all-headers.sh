#!/bin/bash
#==============================================================================
# Filename: simplify-all-headers.sh
# Description: Run all header simplification scripts at once
#==============================================================================

# Determine script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="${SCRIPT_DIR}"

echo "=============================================================="
echo "Simplifying all file headers in the Samstraumr project"
echo "=============================================================="
echo ""

# Run bash header simplification
echo "Step 1: Simplifying bash script headers..."
"${PROJECT_ROOT}/simplify-headers.sh"
echo ""

# Run Java header simplification
echo "Step 2: Simplifying Java file headers..."
"${PROJECT_ROOT}/simplify-java-headers.sh"
echo ""

# Update header templates
echo "Step 3: Updating header templates and utilities..."
"${PROJECT_ROOT}/update-template-headers.sh"
echo ""

echo "=============================================================="
echo "Header simplification complete!"
echo "=============================================================="
echo ""
echo "All file headers have been simplified to remove author, copyright,"
echo "and date information following the standards documented in:"
echo "  docs/reference/standards/HeaderStandards.md"