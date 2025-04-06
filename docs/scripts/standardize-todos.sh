#!/bin/bash
#==============================================================================
# standardize-todos.sh - Checks and standardizes TODOs in the codebase
# This script identifies non-compliant TODOs and offers to fix them
#==============================================================================

set -e

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

# Find repository root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
cd "$PROJECT_ROOT"

# Functions for prettier output
info() { echo -e "${BLUE}$1${NC}"; }
success() { echo -e "${GREEN}$1${NC}"; }
warning() { echo -e "${YELLOW}Warning: $1${NC}" >&2; }
error() { echo -e "${RED}Error: $1${NC}" >&2; }
header() { echo -e "\n${MAGENTA}=== $1 ===${NC}"; }
subheader() { echo -e "\n${CYAN}--- $1 ---${NC}"; }

# Default settings
DRY_RUN=true
AUTO_FIX=false
VERBOSE=false
TARGET_DIR="$PROJECT_ROOT"
REPORT_FILE="$PROJECT_ROOT/todo-report.md"
PRIORITY_CATEGORIES=true
EXCLUDE_DIRS=".git node_modules target build dist vendor"
FIX_HIGH_PRIORITY=false
GITHUB_ISSUE_CHECK=false

# Standard TODO format pattern
TODO_FORMAT_REGEX="^\s*\/\/\s*TODO\s+\[P[0-3]\](\s+\([A-Z]+\))?(\s+\(#[0-9]+\))?\s*:\s*.+$"

# Function to show usage
show_help() {
  echo -e "${YELLOW}Usage:${NC} $0 [options]"
  echo
  echo "Checks TODOs in the codebase and standardizes their format."
  echo
  echo -e "${YELLOW}Options:${NC}"
  echo "  -h, --help                 Show this help message"
  echo "  -v, --verbose              Show verbose output"
  echo "  -d, --directory DIR        Specify directory to scan (default: project root)"
  echo "  -f, --fix                  Auto-fix non-compliant TODOs"
  echo "  -r, --report FILE          Generate report file (default: todo-report.md)"
  echo "  -p, --priority-only        Only fix priority format, not categories"
  echo "  -x, --exclude DIRS         Exclude directories (comma-separated)"
  echo "  -i, --high-priority        Fix high priority TODOs only (P0, P1)"
  echo "  -g, --github-check         Verify GitHub issues exist (requires gh CLI)"
  echo
  echo -e "${YELLOW}Standard format:${NC}"
  echo "  // TODO [Priority] (Category) (#Issue): Description"
  echo
  echo -e "${YELLOW}Examples:${NC}"
  echo "  // TODO [P1] (BUG) (#123): Fix null pointer exception"
  echo "  // TODO [P2] (FEAT): Implement new feature"
  echo
  echo -e "${YELLOW}Priority levels:${NC}"
  echo "  P0 - Critical (must fix now)"
  echo "  P1 - High (should fix soon)"
  echo "  P2 - Medium (fix when time permits)"
  echo "  P3 - Low (nice to have)"
  echo
  echo -e "${YELLOW}Categories:${NC}"
  echo "  BUG, FEAT, REFACTOR, PERF, DOC, TEST, INFRA, SECURITY"
  echo
}

# Parse command-line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help)
      show_help
      exit 0
      ;;
    -v|--verbose)
      VERBOSE=true
      shift
      ;;
    -d|--directory)
      TARGET_DIR="$2"
      shift 2
      ;;
    -f|--fix)
      AUTO_FIX=true
      DRY_RUN=false
      shift
      ;;
    -r|--report)
      REPORT_FILE="$2"
      shift 2
      ;;
    -p|--priority-only)
      PRIORITY_CATEGORIES=false
      shift
      ;;
    -x|--exclude)
      EXCLUDE_DIRS="$2"
      shift 2
      ;;
    -i|--high-priority)
      FIX_HIGH_PRIORITY=true
      shift
      ;;
    -g|--github-check)
      GITHUB_ISSUE_CHECK=true
      shift
      ;;
    *)
      error "Unknown option: $1"
      show_help
      exit 1
      ;;
  esac
done

# Validate that target directory exists
if [[ ! -d "$TARGET_DIR" ]]; then
  error "Target directory does not exist: $TARGET_DIR"
  exit 1
fi

# Convert target directory to absolute path
TARGET_DIR="$(cd "$TARGET_DIR" && pwd)"

header "Scanning for TODOs in ${TARGET_DIR#$PROJECT_ROOT/}"

# Build exclude pattern for find command
EXCLUDE_PATTERN=""
for dir in $EXCLUDE_DIRS; do
  EXCLUDE_PATTERN="$EXCLUDE_PATTERN -not -path '*/$dir/*'"
done

# Create temporary file for storing todo information
TODO_TMP=$(mktemp)
NONCOMPLIANT_TMP=$(mktemp)
FIXABLE_TMP=$(mktemp)

# Function to extract a guess for the priority based on context
guess_priority() {
  local content="$1"
  
  # Try to guess priority based on content
  if [[ "$content" =~ (urgent|critical|immediately|asap|crash|security|vulnerability) ]]; then
    echo "P0"
  elif [[ "$content" =~ (important|soon|should|high|leak|issue|bug) ]]; then
    echo "P1"
  elif [[ "$content" =~ (later|refactor|improve|enhancement|clean) ]]; then
    echo "P2"
  else
    echo "P3"
  fi
}

# Function to extract a guess for the category based on context
guess_category() {
  local content="$1"
  
  # Try to guess category based on content
  if [[ "$content" =~ (bug|fix|issue|error|exception|crash|problem) ]]; then
    echo "BUG"
  elif [[ "$content" =~ (feature|add|implement|create|start|design|provide) ]]; then
    echo "FEAT"
  elif [[ "$content" =~ (refactor|clean|rename|move|improve|simplify|extract) ]]; then
    echo "REFACTOR"
  elif [[ "$content" =~ (performance|speed|memory|optimize|slow) ]]; then
    echo "PERF"
  elif [[ "$content" =~ (documentation|document|explain|clarify|comment) ]]; then
    echo "DOC"
  elif [[ "$content" =~ (test|verify|validate|assert|check|unit|integration) ]]; then
    echo "TEST"
  elif [[ "$content" =~ (infrastructure|build|deploy|ci|cd|pipeline|tooling) ]]; then
    echo "INFRA"
  elif [[ "$content" =~ (security|auth|safety|secure|protect|encryption) ]]; then
    echo "SECURITY"
  else
    # Default to TASK if we can't guess
    echo "TASK"
  fi
}

# Find all files containing "TODO" and analyze them
info "Finding files with TODOs..."
TODO_FILES=$(eval "find \"$TARGET_DIR\" -type f $EXCLUDE_PATTERN -name \"*.java\" -o -name \"*.sh\" -o -name \"*.js\" -o -name \"*.ts\" -o -name \"*.md\" -o -name \"*.html\" -o -name \"*.xml\" | xargs grep -l \"TODO\" 2>/dev/null" || echo "")

if [[ -z "$TODO_FILES" ]]; then
  warning "No files with TODOs found"
  exit 0
fi

# Counters
TOTAL_TODOS=0
COMPLIANT_TODOS=0
NONCOMPLIANT_TODOS=0
FIXED_TODOS=0

# Loop through the files and check TODOs
for file in $TODO_FILES; do
  if [[ "$VERBOSE" == true ]]; then
    info "Checking file: ${file#$PROJECT_ROOT/}"
  fi

  # Get file extension
  ext="${file##*.}"
  
  # Determine comment style based on file extension
  case "$ext" in
    java|js|ts|c|cpp|h|hpp|cs)
      comment_start="//"
      comment_marker="\/\/"
      ;;
    sh)
      comment_start="#"
      comment_marker="#"
      ;;
    md|html)
      comment_start="<!--"
      comment_marker="<!--"
      ;;
    xml)
      comment_start="<!--"
      comment_marker="<!--"
      ;;
    *)
      comment_start="//"
      comment_marker="\/\/"
      ;;
  esac

  # Extract lines with TODOs
  while IFS= read -r line_info; do
    line_num=$(echo "$line_info" | cut -d: -f1)
    line_content=$(echo "$line_info" | cut -d: -f2-)
    
    # Increment total TODOs counter
    ((TOTAL_TODOS++))
    
    # Check if TODO follows the standard format
    if [[ "$line_content" =~ $TODO_FORMAT_REGEX ]]; then
      # TODO already complies with the standard format
      ((COMPLIANT_TODOS++))
      echo "${file}:${line_num}:${line_content}" >> "$TODO_TMP"
    else
      # TODO does not comply with the standard format
      ((NONCOMPLIANT_TODOS++))
      echo "${file}:${line_num}:${line_content}" >> "$NONCOMPLIANT_TMP"

      # Check if we can fix it
      if [[ "$AUTO_FIX" == true ]]; then
        # Try to extract existing information
        todo_text=$(echo "$line_content" | sed -E "s/.*TODO\s*:?\s*(.*)/\1/")
        
        # If there's nothing after TODO, use the whole line
        if [[ -z "$todo_text" ]]; then
          todo_text=$(echo "$line_content" | sed -E "s/.*TODO\s*(.*)/\1/")
        fi
        
        # Extract GitHub issue if present
        github_issue=""
        if [[ "$line_content" =~ \#([0-9]+) ]]; then
          github_issue="#${BASH_REMATCH[1]}"
        fi
        
        # Guess priority and category if needed
        guessed_priority=$(guess_priority "$todo_text")
        guessed_category=$(guess_category "$todo_text")
        
        # Only fix high priority TODOs if requested
        if [[ "$FIX_HIGH_PRIORITY" == true && "$guessed_priority" =~ P[2-3] ]]; then
          continue
        fi
        
        # Create new standardized TODO
        indent=$(echo "$line_content" | sed -E "s/^(\s*).*$/\1/")
        new_line="${indent}${comment_start} TODO [${guessed_priority}]"
        
        # Add category if needed
        if [[ "$PRIORITY_CATEGORIES" == true ]]; then
          new_line="${new_line} (${guessed_category})"
        fi
        
        # Add GitHub issue if present
        if [[ -n "$github_issue" ]]; then
          new_line="${new_line} (${github_issue})"
        fi
        
        # Add the description
        new_line="${new_line}: ${todo_text}"
        
        # Record the fix
        echo "${file}:${line_num}:${line_content}:${new_line}" >> "$FIXABLE_TMP"
      fi
    fi
  done < <(grep -n "TODO" "$file" | grep -v "standardize-todos\|extract-todos")
done

# Fix TODOs if requested
if [[ "$AUTO_FIX" == true && -s "$FIXABLE_TMP" ]]; then
  subheader "Fixing non-compliant TODOs"
  
  while IFS= read -r fix_info; do
    file=$(echo "$fix_info" | cut -d: -f1)
    line_num=$(echo "$fix_info" | cut -d: -f2)
    old_line=$(echo "$fix_info" | cut -d: -f3)
    new_line=$(echo "$fix_info" | cut -d: -f4-)
    
    if [[ "$VERBOSE" == true ]]; then
      info "Fixing TODO in ${file#$PROJECT_ROOT/}:${line_num}"
      echo "  Old: $old_line"
      echo "  New: $new_line"
    fi
    
    # Create a temporary file for the replacement
    temp_file=$(mktemp)
    
    # Replace the line in the file
    awk -v line="$line_num" -v new_text="$new_line" 'NR == line {print new_text} NR != line {print}' "$file" > "$temp_file"
    
    # Check if the replacement worked
    if [[ $? -eq 0 ]]; then
      # Copy the temp file back to the original
      cp "$temp_file" "$file"
      ((FIXED_TODOS++))
    else
      warning "Failed to fix TODO in ${file#$PROJECT_ROOT/}:${line_num}"
    fi
    
    # Clean up
    rm "$temp_file"
  done < "$FIXABLE_TMP"
  
  success "Fixed $FIXED_TODOS TODOs"
fi

# Print summary
header "TODO Standardization Summary"
echo "Total TODOs: $TOTAL_TODOS"
echo "Compliant TODOs: $COMPLIANT_TODOS"
echo "Non-compliant TODOs: $NONCOMPLIANT_TODOS"

if [[ "$AUTO_FIX" == true ]]; then
  echo "Fixed TODOs: $FIXED_TODOS"
  echo "Remaining non-compliant TODOs: $((NONCOMPLIANT_TODOS - FIXED_TODOS))"
else
  echo "Run with --fix to automatically fix non-compliant TODOs"
fi

# Generate report if needed
if [[ -n "$REPORT_FILE" ]]; then
  subheader "Generating report"
  info "Report file: $REPORT_FILE"
  
  # Check if extract-todos.sh exists and use it
  if [[ -x "$SCRIPT_DIR/extract-todos.sh" ]]; then
    "$SCRIPT_DIR/extract-todos.sh" --output "$REPORT_FILE"
  else
    # Basic report if extract-todos.sh is not available
    cat > "$REPORT_FILE" << EOF
# TODO Standardization Report

Generated on $(date)

## Summary

- Total TODOs: $TOTAL_TODOS
- Compliant TODOs: $COMPLIANT_TODOS
- Non-compliant TODOs: $NONCOMPLIANT_TODOS
EOF

    if [[ "$AUTO_FIX" == true ]]; then
      cat >> "$REPORT_FILE" << EOF
- Fixed TODOs: $FIXED_TODOS
- Remaining non-compliant: $((NONCOMPLIANT_TODOS - FIXED_TODOS))
EOF
    fi

    cat >> "$REPORT_FILE" << EOF

## Standard Format

TODOs should follow this format:

\`\`\`
// TODO [Priority] (Category) (#Issue): Description
\`\`\`

Example:
\`\`\`
// TODO [P1] (BUG) (#123): Fix null pointer exception
\`\`\`

### Priority Levels

- **P0**: Critical - Must be fixed immediately
- **P1**: High - Should be fixed soon
- **P2**: Medium - Fix when time permits
- **P3**: Low - Nice to have

### Categories

- **BUG**: Bug fix
- **FEAT**: New feature
- **REFACTOR**: Code refactoring
- **PERF**: Performance improvement
- **DOC**: Documentation
- **TEST**: Testing
- **INFRA**: Infrastructure
- **SECURITY**: Security issue
- **TASK**: General task
EOF
  fi
  
  success "Report generated: $REPORT_FILE"
fi

# Clean up
rm "$TODO_TMP" "$NONCOMPLIANT_TMP" "$FIXABLE_TMP"

if [[ "$NONCOMPLIANT_TODOS" -eq 0 ]]; then
  success "All TODOs follow the standard format!"
elif [[ "$AUTO_FIX" == true && "$FIXED_TODOS" -eq "$NONCOMPLIANT_TODOS" ]]; then
  success "All non-compliant TODOs have been fixed!"
fi

exit 0