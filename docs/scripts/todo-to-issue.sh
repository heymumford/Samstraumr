#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# todo-to-issue.sh - Create GitHub issues from TODOs
# This script creates GitHub issues from high-priority TODOs
#==============================================================================

set -e

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
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

# Default settings
DRY_RUN=true
PRIORITY="P0,P1"
UPDATE_CODE=true
VERBOSE=false
GITHUB_TOKEN=""
LABEL_PREFIX="todo-"
EXCLUDE_DIRS=".git node_modules target build dist vendor"

# Function to show usage
show_help() {
  echo "Usage: $0 [options]"
  echo
  echo "Creates GitHub issues from high-priority TODOs in the codebase."
  echo
  echo "Options:"
  echo "  -h, --help              Show this help message"
  echo "  -p, --priority LEVELS   Priority levels to process (comma-separated, default: P0,P1)"
  echo "  -n, --no-update         Don't update TODOs in code with issue numbers"
  echo "  -d, --dry-run           Don't create issues, just show what would be done"
  echo "  -v, --verbose           Show verbose output"
  echo "  -t, --token TOKEN       GitHub token (or use GITHUB_TOKEN env var)"
  echo "  -l, --label-prefix      Prefix for labels (default: todo-)"
  echo "  -x, --exclude DIRS      Exclude directories (comma-separated)"
  echo
  echo "Requirements:"
  echo "  - GitHub CLI (gh) must be installed and authenticated"
  echo "  - TODOs must follow the standard format: // TODO [Priority] (Category): Description"
  echo
  echo "Examples:"
  echo "  $0 -p P0,P1                  # Create issues for P0 and P1 TODOs"
  echo "  $0 -p P0 -d                  # Dry run for P0 TODOs only"
  echo
}

# Parse command-line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help)
      show_help
      exit 0
      ;;
    -p|--priority)
      PRIORITY="$2"
      shift 2
      ;;
    -n|--no-update)
      UPDATE_CODE=false
      shift
      ;;
    -d|--dry-run)
      DRY_RUN=true
      shift
      ;;
    -v|--verbose)
      VERBOSE=true
      shift
      ;;
    -t|--token)
      GITHUB_TOKEN="$2"
      shift 2
      ;;
    -l|--label-prefix)
      LABEL_PREFIX="$2"
      shift 2
      ;;
    -x|--exclude)
      EXCLUDE_DIRS="$2"
      shift 2
      ;;
    *)
      error "Unknown option: $1"
      show_help
      exit 1
      ;;
  esac
done

# Check if GitHub CLI is installed
if ! command -v gh &> /dev/null; then
  error "GitHub CLI (gh) not found. Please install it: https://cli.github.com/"
  exit 1
fi

# Check if authenticated with GitHub
if ! gh auth status &> /dev/null; then
  error "Not authenticated with GitHub. Please run 'gh auth login' first."
  exit 1
fi

# Set GitHub token if provided
if [[ -n "$GITHUB_TOKEN" ]]; then
  export GITHUB_TOKEN
fi

# Parse priority levels
IFS=',' read -ra PRIORITY_LEVELS <<< "$PRIORITY"

info "Scanning for TODOs with priorities: ${PRIORITY}"
if [[ "$DRY_RUN" == true ]]; then
  info "DRY RUN: No issues will be created"
fi

# Build exclude pattern for find command
EXCLUDE_PATTERN=""
for dir in $EXCLUDE_DIRS; do
  EXCLUDE_PATTERN="$EXCLUDE_PATTERN -not -path '*/$dir/*'"
done

# Create temporary file for storing todo information
TODO_TMP=$(mktemp)

# Find TODOs with the specified priority levels
for LEVEL in "${PRIORITY_LEVELS[@]}"; do
  info "Searching for ${LEVEL} TODOs..."
  
  # Standard TODO format pattern for this priority level
  TODO_PATTERN="TODO\s+\[${LEVEL}\]"
  
  # Find all files containing TODOs with this priority level
  FILES_WITH_TODOS=$(eval "find \"$PROJECT_ROOT\" -type f $EXCLUDE_PATTERN -name \"*.java\" -o -name \"*.sh\" -o -name \"*.js\" -o -name \"*.ts\" -o -name \"*.md\" -o -name \"*.html\" -o -name \"*.xml\" | xargs grep -l \"${TODO_PATTERN}\" 2>/dev/null" || echo "")
  
  if [[ -z "$FILES_WITH_TODOS" ]]; then
    info "No files with ${LEVEL} TODOs found."
    continue
  fi
  
  # Process each file with TODOs
  for file in $FILES_WITH_TODOS; do
    if [[ "$VERBOSE" == true ]]; then
      info "Checking file: ${file#$PROJECT_ROOT/}"
    fi
    
    # Get relative path for GitHub links
    rel_path="${file#$PROJECT_ROOT/}"
    
    # Extract lines with TODOs of this priority level
    grep -n "$TODO_PATTERN" "$file" | while read -r line_info; do
      line_num=$(echo "$line_info" | cut -d: -f1)
      line_content=$(echo "$line_info" | cut -d: -f2-)
      
      # Skip if this TODO already has a GitHub issue
      if [[ "$line_content" =~ \(#[0-9]+\) ]]; then
        if [[ "$VERBOSE" == true ]]; then
          info "Skipping TODO that already has an issue: ${line_content}"
        fi
        continue
      fi
      
      # Extract category if available
      category=""
      if [[ "$line_content" =~ \(([A-Z]+)\) ]]; then
        category="${BASH_REMATCH[1]}"
      fi
      
      # Extract description
      description=""
      if [[ "$line_content" =~ TODO\s+\[${LEVEL}\](\s+\([A-Z]+\))?\s*:\s*(.*) ]]; then
        description="${BASH_REMATCH[2]}"
      elif [[ "$line_content" =~ TODO\s+\[${LEVEL}\]\s*(.*) ]]; then
        description="${BASH_REMATCH[1]}"
      else
        # Can't extract description, skip this TODO
        warning "Could not extract description from TODO: ${line_content}"
        continue
      fi
      
      # Create issue title
      issue_title="${description}"
      # Truncate if too long
      if [[ ${#issue_title} -gt 80 ]]; then
        issue_title="${issue_title:0:77}..."
      fi
      
      # Create issue body
      issue_body="## TODO from codebase\n\n"
      issue_body+="Found in [${rel_path}:${line_num}](${rel_path}#L${line_num})\n\n"
      issue_body+="### Original TODO\n\n\`\`\`\n${line_content}\n\`\`\`\n\n"
      issue_body+="### Description\n\n${description}\n\n"
      issue_body+="### Priority\n\n${LEVEL}\n\n"
      
      if [[ -n "$category" ]]; then
        issue_body+="### Category\n\n${category}\n\n"
      fi
      
      issue_body+="This issue was auto-generated from a TODO in the codebase."
      
      # Create labels
      labels="${LABEL_PREFIX}${LEVEL}"
      if [[ -n "$category" ]]; then
        labels="${labels},${LABEL_PREFIX}${category}"
      fi
      
      # Store TODO information
      echo "${file}:${line_num}:${line_content}:${issue_title}:${labels}" >> "$TODO_TMP"
      
      # Display information in verbose mode
      if [[ "$VERBOSE" == true ]]; then
        echo "  Title: ${issue_title}"
        echo "  Labels: ${labels}"
        echo "  Location: ${rel_path}:${line_num}"
      fi
    done
  done
done

# Check if we found any TODOs to process
if [[ ! -s "$TODO_TMP" ]]; then
  info "No high-priority TODOs without issues found."
  rm "$TODO_TMP"
  exit 0
fi

# Process TODOs and create issues
todo_count=$(wc -l < "$TODO_TMP")
info "Found ${todo_count} high-priority TODOs without GitHub issues"

# Only proceed if not in dry run mode
if [[ "$DRY_RUN" != true ]]; then
  while IFS= read -r todo_info; do
    file=$(echo "$todo_info" | cut -d: -f1)
    line_num=$(echo "$todo_info" | cut -d: -f2)
    line_content=$(echo "$todo_info" | cut -d: -f3)
    issue_title=$(echo "$todo_info" | cut -d: -f4)
    labels=$(echo "$todo_info" | cut -d: -f5)
    
    # Create the issue using GitHub CLI
    info "Creating issue: ${issue_title}"
    
    # Extract description for issue body
    if [[ "$line_content" =~ TODO\s+\[[P][0-3]\](\s+\([A-Z]+\))?\s*:\s*(.*) ]]; then
      description="${BASH_REMATCH[2]}"
    else
      description="$line_content"
    fi
    
    # Create relative path for GitHub links
    rel_path="${file#$PROJECT_ROOT/}"
    
    # Create issue body
    issue_body="## TODO from codebase\n\nFound in \`${rel_path}:${line_num}\`\n\n### Description\n\n${description}\n\n### Original TODO\n\`\`\`\n${line_content}\n\`\`\`\n\nThis issue was auto-generated from a TODO in the codebase."
    
    # Create issue
    issue_url=$(gh issue create --title "$issue_title" --body "$issue_body" --label "$labels" 2>/dev/null)
    issue_status=$?
    
    if [[ $issue_status -eq 0 ]]; then
      issue_number=$(echo "$issue_url" | grep -o '[0-9]*$')
      success "Created issue #${issue_number}: ${issue_url}"
      
      # Update TODO in code if requested
      if [[ "$UPDATE_CODE" == true ]]; then
        # Determine if the TODO has a category
        if [[ "$line_content" =~ (TODO\s+\[[P][0-3]\])\s+\(([A-Z]+)\) ]]; then
          # TODO with category: insert issue reference after category
          prefix="${BASH_REMATCH[1]} (${BASH_REMATCH[2]})"
          updated_line="${line_content/${prefix}/${prefix} (#${issue_number})}"
        elif [[ "$line_content" =~ (TODO\s+\[[P][0-3]\]) ]]; then
          # TODO without category: insert issue reference after priority
          prefix="${BASH_REMATCH[1]}"
          updated_line="${line_content/${prefix}/${prefix} (#${issue_number})}"
        else
          # Unexpected format, skip updating
          warning "Could not update TODO in ${file}:${line_num} - unexpected format"
          continue
        fi
        
        # Create a temporary file for the update
        temp_file=$(mktemp)
        
        # Replace the line
        awk -v line="$line_num" -v new_text="$updated_line" 'NR == line {print new_text} NR != line {print}' "$file" > "$temp_file"
        
        # Check if the replacement worked
        if [[ $? -eq 0 ]]; then
          # Copy the temp file back to the original
          cp "$temp_file" "$file"
          info "Updated TODO in ${rel_path}:${line_num} with issue #${issue_number}"
        else
          warning "Failed to update TODO in ${rel_path}:${line_num}"
        fi
        
        # Clean up
        rm "$temp_file"
      fi
    else
      error "Failed to create issue for TODO in ${rel_path}:${line_num}"
    fi
  done < "$TODO_TMP"
else
  # In dry run mode, just show what would be done
  info "DRY RUN: Would create issues for the following TODOs:"
  cat "$TODO_TMP" | while read -r todo_info; do
    file=$(echo "$todo_info" | cut -d: -f1)
    line_num=$(echo "$todo_info" | cut -d: -f2)
    line_content=$(echo "$todo_info" | cut -d: -f3)
    issue_title=$(echo "$todo_info" | cut -d: -f4)
    labels=$(echo "$todo_info" | cut -d: -f5)
    
    rel_path="${file#$PROJECT_ROOT/}"
    echo "  File: ${rel_path}:${line_num}"
    echo "  Title: ${issue_title}"
    echo "  Labels: ${labels}"
    echo "  Content: ${line_content}"
    echo
  done
fi

# Clean up
rm "$TODO_TMP"

if [[ "$DRY_RUN" == true ]]; then
  info "Dry run completed. Run without -d/--dry-run to create issues."
else
  success "Created GitHub issues for high-priority TODOs."
fi

exit 0