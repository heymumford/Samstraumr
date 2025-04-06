#!/bin/bash
#==============================================================================
# extract-todos.sh
#
# Script to extract TODOs and FIXMEs from the codebase and generate a report
#==============================================================================

# Find project root directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
cd "$PROJECT_ROOT"

# Source the doc-lib library that contains the shared documentation utilities
if [ -f "${PROJECT_ROOT}/util/lib/doc-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/doc-lib.sh"
  USING_LIB=true
else
  echo "Warning: Documentation library not found. Using fallback functions."
  USING_LIB=false
  
  # Fallback implementations of required functions
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  YELLOW='\033[0;33m'
  BLUE='\033[0;34m'
  CYAN='\033[0;36m'
  NC='\033[0m' # No Color
  
  # Fallback output functions
  print_header() { echo -e "\n${YELLOW}==== $1 ====${NC}"; }
  print_success() { echo -e "${GREEN}✓ $1${NC}"; }
  print_error() { echo -e "${RED}✗ $1${NC}"; }
  print_info() { echo -e "${BLUE}→ $1${NC}"; }
fi

# Default output file
OUTPUT_FILE="todo-report.md"
MARKDOWN=true
OUTPUT_DIR="$PROJECT_ROOT"
VERBOSE=false
PRIORITY_FILTER=""
CATEGORY_FILTER=""

# Function to show usage
usage() {
  echo -e "${YELLOW}Usage:${NC} $0 [options]"
  echo
  echo "Extract TODOs and FIXMEs from the codebase and generate a report."
  echo
  echo -e "${YELLOW}Options:${NC}"
  echo "  -h, --help                 Show this help message"
  echo "  -o, --output FILE          Output file (default: todo-report.md)"
  echo "  -d, --directory DIR        Directory to scan (default: current directory)"
  echo "  -f, --format FORMAT        Output format: markdown or text (default: markdown)"
  echo "  -v, --verbose              Show verbose output"
  echo "  -p, --priority PRIORITY    Filter by priority (P0, P1, P2, P3)"
  echo "  -c, --category CATEGORY    Filter by category (BUG, FEAT, etc.)"
  echo
  echo -e "${YELLOW}Examples:${NC}"
  echo "  $0 --output todos.md"
  echo "  $0 --directory src/main"
  echo "  $0 --priority P1"
  echo "  $0 --category BUG"
  echo
}

# Parse command-line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help)
      usage
      exit 0
      ;;
    -o|--output)
      OUTPUT_FILE="$2"
      shift 2
      ;;
    -d|--directory)
      TARGET_DIR="$2"
      shift 2
      ;;
    -f|--format)
      if [[ "$2" == "text" ]]; then
        MARKDOWN=false
      elif [[ "$2" == "markdown" ]]; then
        MARKDOWN=true
      else
        print_error "Invalid format: $2. Must be 'markdown' or 'text'."
        exit 1
      fi
      shift 2
      ;;
    -v|--verbose)
      VERBOSE=true
      shift
      ;;
    -p|--priority)
      PRIORITY_FILTER="$2"
      shift 2
      ;;
    -c|--category)
      CATEGORY_FILTER="$2"
      shift 2
      ;;
    *)
      print_error "Unknown option: $1"
      usage
      exit 1
      ;;
  esac
done

# Set default target directory if not specified
if [[ -z "$TARGET_DIR" ]]; then
  TARGET_DIR="$PROJECT_ROOT"
fi

# Expand target directory to full path
TARGET_DIR="$(cd "$TARGET_DIR" 2>/dev/null && pwd)"
if [[ ! -d "$TARGET_DIR" ]]; then
  print_error "Target directory does not exist: $TARGET_DIR"
  exit 1
fi

# Print configuration
print_header "Extracting TODOs from ${TARGET_DIR#$PROJECT_ROOT/}"
print_info "Output file: ${OUTPUT_FILE}"
print_info "Format: $([ "$MARKDOWN" == true ] && echo "Markdown" || echo "Text")"
if [[ -n "$PRIORITY_FILTER" ]]; then
  print_info "Priority filter: $PRIORITY_FILTER"
fi
if [[ -n "$CATEGORY_FILTER" ]]; then
  print_info "Category filter: $CATEGORY_FILTER"
fi

# Create temporary file for raw output
TMP_FILE=$(mktemp)

# If using library, use the extract_todos function
if [[ "$USING_LIB" == true ]] && type extract_todos &>/dev/null; then
  print_info "Using doc-lib.sh extract_todos function"
  
  # Call the library function
  TOTAL_COUNT=$(extract_todos "$OUTPUT_FILE" "$PRIORITY_FILTER")
  
  print_success "TODO report generated using library function"
  
  # No need to continue with the rest of the script
  if [[ -f "$OUTPUT_FILE" ]]; then
    # Extract the counts for summary display
    P0_COUNT=$(grep -c "^| P0 |" "$OUTPUT_FILE" || echo 0)
    P1_COUNT=$(grep -c "^| P1 |" "$OUTPUT_FILE" || echo 0)
    P2_COUNT=$(grep -c "^| P2 |" "$OUTPUT_FILE" || echo 0)
    P3_COUNT=$(grep -c "^| P3 |" "$OUTPUT_FILE" || echo 0)
    UNSPECIFIED_COUNT=$((TOTAL_COUNT - P0_COUNT - P1_COUNT - P2_COUNT - P3_COUNT))
    
    print_success "TODO report generated: $OUTPUT_FILE"
    echo "Found $TOTAL_COUNT TODOs (P0: $P0_COUNT, P1: $P1_COUNT, P2: $P2_COUNT, P3: $P3_COUNT, Unspecified: $UNSPECIFIED_COUNT)"
    
    # Clean up and exit
    rm -f "$TMP_FILE"
    exit 0
  fi
  
  # If we get here, something went wrong with the library function
  print_warning "Library function failed, falling back to native implementation"
fi

# Legacy implementation (fallback)
print_info "Using legacy TODO extraction method"

# Find TODOs and FIXMEs in the codebase
find "$TARGET_DIR" -type f \
  -not -path "*/\.*" \
  -not -path "*/node_modules/*" \
  -not -path "*/target/*" \
  -not -path "*/build/*" \
  -not -path "*/dist/*" \
  | xargs grep -l -E "(TODO|FIXME)" 2>/dev/null \
  | while read -r file; do
    # Get relative path
    rel_path="${file#$PROJECT_ROOT/}"
    # Extract the file extension
    ext="${file##*.}"
    
    # Show progress if verbose
    if [[ "$VERBOSE" == true ]]; then
      print_info "Scanning: $rel_path"
    fi
    
    # Get line numbers with TODOs
    grep -n -E "(TODO|FIXME)" "$file" | while read -r line; do
      # Extract line number and content
      line_num=$(echo "$line" | cut -d: -f1)
      line_content=$(echo "$line" | cut -d: -f2-)
      
      # Extract TODO priority if available
      priority=""
      if [[ "$line_content" =~ \[P[0-3]\] ]]; then
        priority=$(echo "$line_content" | grep -o -E '\[P[0-3]\]' | tr -d '[]')
      fi
      
      # Filter by priority if requested
      if [[ -n "$PRIORITY_FILTER" && "$priority" != "$PRIORITY_FILTER" ]]; then
        continue
      fi
      
      # Extract category if available
      category=""
      if [[ "$line_content" =~ \([A-Z]+\) ]]; then
        category=$(echo "$line_content" | grep -o -E '\([A-Z]+\)' | tr -d '()')
      fi
      
      # Filter by category if requested
      if [[ -n "$CATEGORY_FILTER" && "$category" != "$CATEGORY_FILTER" ]]; then
        continue
      fi
      
      # Extract GitHub issue if available
      issue=""
      if [[ "$line_content" =~ \(#[0-9]+\) ]]; then
        issue=$(echo "$line_content" | grep -o -E '\(#[0-9]+\)' | tr -d '()')
      fi
      
      # Write to temp file
      echo "$rel_path:$line_num:$priority:$category:$issue:$line_content" >> "$TMP_FILE"
    done
  done

# Count TODOs by priority
P0_COUNT=$(grep -c ":P0:" "$TMP_FILE" || echo 0)
P1_COUNT=$(grep -c ":P1:" "$TMP_FILE" || echo 0)
P2_COUNT=$(grep -c ":P2:" "$TMP_FILE" || echo 0)
P3_COUNT=$(grep -c ":P3:" "$TMP_FILE" || echo 0)
UNSPECIFIED_COUNT=$(grep -v ":[P][0-3]:" "$TMP_FILE" | wc -l)
TOTAL_COUNT=$(wc -l < "$TMP_FILE")

# Start generating the report
if [[ "$MARKDOWN" == true ]]; then
  # Markdown header
  cat > "$OUTPUT_FILE" << EOF
# TODO Report

This report lists all TODOs and FIXMEs in the codebase. Generated on $(date).

## Summary

- **Total TODOs**: $TOTAL_COUNT
- **Critical (P0)**: $P0_COUNT
- **High (P1)**: $P1_COUNT
- **Medium (P2)**: $P2_COUNT
- **Low (P3)**: $P3_COUNT
- **Unspecified Priority**: $UNSPECIFIED_COUNT

## TODOs by Priority

EOF

  # P0 TODOs
  if [[ $P0_COUNT -gt 0 ]]; then
    echo "### Critical (P0)" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    grep ":P0:" "$TMP_FILE" | sort | while read -r line; do
      file=$(echo "$line" | cut -d: -f1)
      line_num=$(echo "$line" | cut -d: -f2)
      line_content=$(echo "$line" | cut -d: -f6-)
      issue=$(echo "$line" | cut -d: -f5)
      
      echo "- **$file:$line_num**" >> "$OUTPUT_FILE"
      # Clean up the line content for better display
      clean_content=$(echo "$line_content" | sed 's/^\s*\/\/\s*//;s/^\s*#\s*//;s/^\s*<!--\s*//;s/\s*-->\s*$//')
      
      # Format with issue link if available
      if [[ -n "$issue" && "$issue" != "::" ]]; then
        echo "  - $clean_content [GitHub Issue]($issue)" >> "$OUTPUT_FILE"
      else
        echo "  - $clean_content" >> "$OUTPUT_FILE"
      fi
      echo "" >> "$OUTPUT_FILE"
    done
  fi

  # P1 TODOs
  if [[ $P1_COUNT -gt 0 ]]; then
    echo "### High Priority (P1)" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    grep ":P1:" "$TMP_FILE" | sort | while read -r line; do
      file=$(echo "$line" | cut -d: -f1)
      line_num=$(echo "$line" | cut -d: -f2)
      line_content=$(echo "$line" | cut -d: -f6-)
      issue=$(echo "$line" | cut -d: -f5)
      
      echo "- **$file:$line_num**" >> "$OUTPUT_FILE"
      clean_content=$(echo "$line_content" | sed 's/^\s*\/\/\s*//;s/^\s*#\s*//;s/^\s*<!--\s*//;s/\s*-->\s*$//')
      
      if [[ -n "$issue" && "$issue" != "::" ]]; then
        echo "  - $clean_content [GitHub Issue]($issue)" >> "$OUTPUT_FILE"
      else
        echo "  - $clean_content" >> "$OUTPUT_FILE"
      fi
      echo "" >> "$OUTPUT_FILE"
    done
  fi

  # P2 TODOs
  if [[ $P2_COUNT -gt 0 ]]; then
    echo "### Medium Priority (P2)" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    grep ":P2:" "$TMP_FILE" | sort | while read -r line; do
      file=$(echo "$line" | cut -d: -f1)
      line_num=$(echo "$line" | cut -d: -f2)
      line_content=$(echo "$line" | cut -d: -f6-)
      issue=$(echo "$line" | cut -d: -f5)
      
      echo "- **$file:$line_num**" >> "$OUTPUT_FILE"
      clean_content=$(echo "$line_content" | sed 's/^\s*\/\/\s*//;s/^\s*#\s*//;s/^\s*<!--\s*//;s/\s*-->\s*$//')
      
      if [[ -n "$issue" && "$issue" != "::" ]]; then
        echo "  - $clean_content [GitHub Issue]($issue)" >> "$OUTPUT_FILE"
      else
        echo "  - $clean_content" >> "$OUTPUT_FILE"
      fi
      echo "" >> "$OUTPUT_FILE"
    done
  fi

  # P3 TODOs
  if [[ $P3_COUNT -gt 0 ]]; then
    echo "### Low Priority (P3)" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    grep ":P3:" "$TMP_FILE" | sort | while read -r line; do
      file=$(echo "$line" | cut -d: -f1)
      line_num=$(echo "$line" | cut -d: -f2)
      line_content=$(echo "$line" | cut -d: -f6-)
      issue=$(echo "$line" | cut -d: -f5)
      
      echo "- **$file:$line_num**" >> "$OUTPUT_FILE"
      clean_content=$(echo "$line_content" | sed 's/^\s*\/\/\s*//;s/^\s*#\s*//;s/^\s*<!--\s*//;s/\s*-->\s*$//')
      
      if [[ -n "$issue" && "$issue" != "::" ]]; then
        echo "  - $clean_content [GitHub Issue]($issue)" >> "$OUTPUT_FILE"
      else
        echo "  - $clean_content" >> "$OUTPUT_FILE"
      fi
      echo "" >> "$OUTPUT_FILE"
    done
  fi

  # Unspecified Priority TODOs
  if [[ $UNSPECIFIED_COUNT -gt 0 ]]; then
    echo "### Unspecified Priority" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    grep -v ":[P][0-3]:" "$TMP_FILE" | sort | while read -r line; do
      file=$(echo "$line" | cut -d: -f1)
      line_num=$(echo "$line" | cut -d: -f2)
      line_content=$(echo "$line" | cut -d: -f6-)
      issue=$(echo "$line" | cut -d: -f5)
      
      echo "- **$file:$line_num**" >> "$OUTPUT_FILE"
      clean_content=$(echo "$line_content" | sed 's/^\s*\/\/\s*//;s/^\s*#\s*//;s/^\s*<!--\s*//;s/\s*-->\s*$//')
      
      if [[ -n "$issue" && "$issue" != "::" ]]; then
        echo "  - $clean_content [GitHub Issue]($issue)" >> "$OUTPUT_FILE"
      else
        echo "  - $clean_content" >> "$OUTPUT_FILE"
      fi
      echo "" >> "$OUTPUT_FILE"
    done
  fi

  # Add footer
  cat >> "$OUTPUT_FILE" << EOF

## Todo Format

Todos should follow this format:

\`\`\`
// TODO [Priority] (Category) (#Issue): Description
\`\`\`

Example:
\`\`\`
// TODO [P1] (BUG) (#123): Fix null pointer exception in TubeIdentity constructor
\`\`\`

### Priority Levels

- **P0**: Critical - Must be fixed immediately
- **P1**: High - Should be fixed soon
- **P2**: Medium - Fix when time permits
- **P3**: Low - Nice to have

### Categories

- **BUG**: Bug fix
- **FEAT**: New feature
- **REFACTOR**: Code improvement
- **PERF**: Performance improvement
- **DOC**: Documentation
- **TEST**: Testing improvement
EOF

else
  # Text format
  cat > "$OUTPUT_FILE" << EOF
TODO REPORT
==========

Generated: $(date)

SUMMARY
-------
Total TODOs: $TOTAL_COUNT
Critical (P0): $P0_COUNT
High (P1): $P1_COUNT
Medium (P2): $P2_COUNT
Low (P3): $P3_COUNT
Unspecified Priority: $UNSPECIFIED_COUNT

EOF

  # P0 TODOs
  if [[ $P0_COUNT -gt 0 ]]; then
    echo "CRITICAL (P0)" >> "$OUTPUT_FILE"
    echo "-------------" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    grep ":P0:" "$TMP_FILE" | sort | while read -r line; do
      file=$(echo "$line" | cut -d: -f1)
      line_num=$(echo "$line" | cut -d: -f2)
      line_content=$(echo "$line" | cut -d: -f6-)
      issue=$(echo "$line" | cut -d: -f5)
      
      echo "$file:$line_num" >> "$OUTPUT_FILE"
      clean_content=$(echo "$line_content" | sed 's/^\s*\/\/\s*//;s/^\s*#\s*//;s/^\s*<!--\s*//;s/\s*-->\s*$//')
      
      if [[ -n "$issue" && "$issue" != "::" ]]; then
        echo "  $clean_content (GitHub Issue: $issue)" >> "$OUTPUT_FILE"
      else
        echo "  $clean_content" >> "$OUTPUT_FILE"
      fi
      echo "" >> "$OUTPUT_FILE"
    done
  fi

  # Repeat for other priorities, similar to the Markdown format but with text formatting
  # ...

fi

# Clean up
rm "$TMP_FILE"

print_success "TODO report generated: $OUTPUT_FILE"
echo "Found $TOTAL_COUNT TODOs (P0: $P0_COUNT, P1: $P1_COUNT, P2: $P2_COUNT, P3: $P3_COUNT, Unspecified: $UNSPECIFIED_COUNT)"

exit 0