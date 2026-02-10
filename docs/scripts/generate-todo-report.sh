#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# generate-todo-report.sh - Generate a comprehensive TODO report for review
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
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
cd "$PROJECT_ROOT"

# Source the documentation library
if [ -f "${PROJECT_ROOT}/util/lib/doc-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/doc-lib.sh"
  print_info "Loaded documentation library (doc-lib.sh)"
else
  echo "Error: Documentation library not found."
  exit 1
fi

# Default settings
OUTPUT_DIR="$PROJECT_ROOT/docs/reports/todos"
REPORT_DATE=$(date +"%Y-%m-%d")
REPORT_FILE="$OUTPUT_DIR/todo-review-${REPORT_DATE}.md"
INCLUDE_HISTORY=true

# Ensure output directory exists
mkdir -p "$OUTPUT_DIR"

# Parse command-line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help)
      echo "Usage: $0 [options]"
      echo
      echo "Generate a comprehensive TODO report for review."
      echo
      echo "Options:"
      echo "  -h, --help             Show this help message"
      echo "  -o, --output FILE      Output file (default: $REPORT_FILE)"
      echo "  -n, --no-history       Don't include TODO history"
      echo
      exit 0
      ;;
    -o|--output)
      REPORT_FILE="$2"
      shift 2
      ;;
    -n|--no-history)
      INCLUDE_HISTORY=false
      shift
      ;;
    *)
      print_error "Unknown option: $1"
      exit 1
      ;;
  esac
done

print_header "Generating TODO Review Report"
print_info "Output file: $REPORT_FILE"

# Initialize the report
cat > "$REPORT_FILE" << EOL
# TODO Review Report

Generated: $(date "+%Y-%m-%d %H:%M:%S")

This report provides a comprehensive overview of TODOs in the Samstraumr codebase for review according to the standard TODO review process.

## Summary

EOL

# Generate summary statistics
print_info "Generating summary statistics..."

# Run extract-todos.sh to get the raw data
TEMP_REPORT=$(mktemp)
./docs/scripts/extract-todos.sh --output "$TEMP_REPORT" >/dev/null 2>&1 || true

# Extract statistics from the report
if [ -f "$TEMP_REPORT" ]; then
  # Extract counts
  TOTAL_COUNT=$(grep -o "Total TODOs: [0-9]*" "$TEMP_REPORT" | awk '{print $3}')
  P0_COUNT=$(grep -o "Critical (P0): [0-9]*" "$TEMP_REPORT" | awk '{print $3}')
  P1_COUNT=$(grep -o "High (P1): [0-9]*" "$TEMP_REPORT" | awk '{print $3}')
  P2_COUNT=$(grep -o "Medium (P2): [0-9]*" "$TEMP_REPORT" | awk '{print $3}')
  P3_COUNT=$(grep -o "Low (P3): [0-9]*" "$TEMP_REPORT" | awk '{print $3}')
  UNSPECIFIED_COUNT=$(grep -o "Unspecified Priority: [0-9]*" "$TEMP_REPORT" | awk '{print $3}')
  
  # Add to report
  cat >> "$REPORT_FILE" << EOL
- **Total TODOs**: ${TOTAL_COUNT:-0}
- **Critical (P0)**: ${P0_COUNT:-0}
- **High (P1)**: ${P1_COUNT:-0}
- **Medium (P2)**: ${P2_COUNT:-0}
- **Low (P3)**: ${P3_COUNT:-0}
- **Unspecified Priority**: ${UNSPECIFIED_COUNT:-0}

EOL
else
  print_warning "Could not generate TODO statistics"
  cat >> "$REPORT_FILE" << EOL
- **Total TODOs**: 0
- **Critical (P0)**: 0
- **High (P1)**: 0
- **Medium (P2)**: 0
- **Low (P3)**: 0
- **Unspecified Priority**: 0

EOL
fi

# Add critical TODOs section
print_info "Collecting critical TODOs..."
cat >> "$REPORT_FILE" << EOL
## Critical TODOs (P0)

These TODOs require immediate attention:

EOL

if [ -f "$TEMP_REPORT" ] && grep -q "### Critical (P0)" "$TEMP_REPORT"; then
  # Extract P0 TODOs
  sed -n '/### Critical (P0)/,/### High Priority (P1)/p' "$TEMP_REPORT" | sed '1d;$d' >> "$REPORT_FILE"
else
  echo "No critical TODOs found." >> "$REPORT_FILE"
fi

# Add high priority TODOs section
print_info "Collecting high priority TODOs..."
cat >> "$REPORT_FILE" << EOL

## High Priority TODOs (P1)

These TODOs should be addressed soon:

EOL

if [ -f "$TEMP_REPORT" ] && grep -q "### High Priority (P1)" "$TEMP_REPORT"; then
  # Extract P1 TODOs
  sed -n '/### High Priority (P1)/,/### Medium Priority (P2)/p' "$TEMP_REPORT" | sed '1d;$d' >> "$REPORT_FILE"
else
  echo "No high priority TODOs found." >> "$REPORT_FILE"
fi

# Add action plan section
print_info "Adding action plan template..."
cat >> "$REPORT_FILE" << EOL

## Action Plan

Based on this review, the following TODOs will be addressed:

| Priority | File:Line | Description | Assignee | Target Date | GitHub Issue |
|----------|-----------|-------------|----------|-------------|--------------|
| P0 | | | | | |
| P1 | | | | | |
| P2 | | | | | |

## TODOs to be Converted to GitHub Issues

The following TODOs should be converted to GitHub issues:

| Priority | File:Line | Description | Category |
|----------|-----------|-------------|----------|
| | | | |

## TODOs to be Removed

The following TODOs are obsolete and should be removed:

| File:Line | Reason |
|-----------|--------|
| | |

EOL

# Add GitHub issues section if gh command is available
if command -v gh &>/dev/null; then
  print_info "Collecting GitHub issues for TODOs..."
  cat >> "$REPORT_FILE" << EOL

## Related GitHub Issues

The following GitHub issues were created from TODOs:

EOL
  
  # Get open issues with the TODO label
  if gh issue list --label "todo" --state open --limit 100 >/dev/null 2>&1; then
    echo "| Issue | Title | Priority | Labels |" >> "$REPORT_FILE"
    echo "|-------|-------|----------|--------|" >> "$REPORT_FILE"
    
    gh issue list --label "todo" --state open --limit 100 --json number,title,labels | \
    jq -r '.[] | "| #\(.number) | \(.title) | \(.labels | map(.name) | map(select(. | startswith("P"))) | join(", ")) | \(.labels | map(.name) | join(", ")) |"' >> "$REPORT_FILE" || \
    echo "Error fetching GitHub issues" >> "$REPORT_FILE"
  else
    echo "No GitHub issues with TODO label found." >> "$REPORT_FILE"
  fi
else
  print_warning "GitHub CLI not found. Skipping GitHub issues section."
fi

# Add TODO history section if requested
if [ "$INCLUDE_HISTORY" = true ]; then
  print_info "Generating TODO history..."
  cat >> "$REPORT_FILE" << EOL

## TODO History

Changes in TODOs over time:

EOL
  
  # Check for previous reports
  PREVIOUS_REPORTS=$(find "$OUTPUT_DIR" -name "todo-review-*.md" -not -name "$(basename "$REPORT_FILE")" | sort -r | head -5)
  
  if [ -n "$PREVIOUS_REPORTS" ]; then
    echo "| Date | Total | P0 | P1 | P2 | P3 | Unspecified |" >> "$REPORT_FILE"
    echo "|------|-------|----|----|----|----|-------------|" >> "$REPORT_FILE"
    
    # Current report stats
    echo "| $REPORT_DATE | ${TOTAL_COUNT:-0} | ${P0_COUNT:-0} | ${P1_COUNT:-0} | ${P2_COUNT:-0} | ${P3_COUNT:-0} | ${UNSPECIFIED_COUNT:-0} |" >> "$REPORT_FILE"
    
    # Previous reports stats
    for report in $PREVIOUS_REPORTS; do
      report_date=$(basename "$report" | sed -E 's/todo-review-([0-9-]+)\.md/\1/')
      
      # Extract counts from previous report
      prev_total=$(grep -o "Total TODOs: [0-9]*" "$report" | awk '{print $3}')
      prev_p0=$(grep -o "Critical (P0): [0-9]*" "$report" | awk '{print $3}')
      prev_p1=$(grep -o "High (P1): [0-9]*" "$report" | awk '{print $3}')
      prev_p2=$(grep -o "Medium (P2): [0-9]*" "$report" | awk '{print $3}')
      prev_p3=$(grep -o "Low (P3): [0-9]*" "$report" | awk '{print $3}')
      prev_unspec=$(grep -o "Unspecified Priority: [0-9]*" "$report" | awk '{print $3}')
      
      echo "| $report_date | ${prev_total:-0} | ${prev_p0:-0} | ${prev_p1:-0} | ${prev_p2:-0} | ${prev_p3:-0} | ${prev_unspec:-0} |" >> "$REPORT_FILE"
    done
  else
    echo "No previous TODO reports found." >> "$REPORT_FILE"
  fi
fi

# Add conclusion section
print_info "Finalizing report..."
cat >> "$REPORT_FILE" << EOL

## Recommendations

Based on this review, the following actions are recommended:

1. **Immediate Actions**
   - [Add specific recommendations here]

2. **Next Sprint**
   - [Add specific recommendations here]

3. **Backlog Items**
   - [Add specific recommendations here]

## Next Review

The next TODO review is scheduled for: [Date]

---

*This report was automatically generated as part of the TODO review process.*
*For more information, see [TODO Review Process](../reference/standards/todo-review-process.md)*
EOL

# Clean up
rm -f "$TEMP_REPORT"

print_success "TODO review report generated: $REPORT_FILE"