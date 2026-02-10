#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# Filename: archive-planning-documents.sh
# Description: Archives completed planning documents by moving them to the 
#              archived directory with the 'archived-' prefix
#==============================================================================

# Determine script paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# Source the doc-lib library that contains the shared documentation utilities
if [ -f "${PROJECT_ROOT}/util/lib/doc-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/doc-lib.sh"
  USING_LIB=true
else
  # Define minimal color codes if libraries are not available
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  YELLOW='\033[0;33m'
  BLUE='\033[0;34m'
  NC='\033[0m' # No Color
  
  # Function to print a header
  print_header() {
    echo -e "\n${YELLOW}==== $1 ====${NC}"
  }
  
  # Function to print success message
  print_success() {
    echo -e "${GREEN}✓ $1${NC}"
  }
  
  # Function to print error message
  print_error() {
    echo -e "${RED}✗ $1${NC}"
  }
  
  # Function to print info message
  print_info() {
    echo -e "${BLUE}→ $1${NC}"
  }
fi

# Planning directories
PLANNING_DIR="${PROJECT_ROOT}/docs/planning"
COMPLETED_DIR="${PLANNING_DIR}/completed"
ARCHIVED_DIR="${PLANNING_DIR}/archived"

# Function to archive a document
archive_document() {
  local source_file="$1"
  local dry_run="${2:-false}"
  
  if [[ ! -f "$source_file" ]]; then
    print_error "Source file does not exist: $source_file"
    return 1
  fi
  
  local filename=$(basename "$source_file")
  local archived_name="archived-${filename}"
  local target_file="${ARCHIVED_DIR}/${archived_name}"
  
  # Check if target file already exists
  if [[ -f "$target_file" ]]; then
    print_error "Target file already exists: $target_file"
    return 1
  fi
  
  if [[ "$dry_run" == "true" ]]; then
    print_info "Would archive: $filename → $archived_name"
    return 0
  else
    # Copy file to archived directory with prefix
    cp "$source_file" "$target_file"
    
    if [[ $? -eq 0 ]]; then
      print_success "Archived: $filename → $archived_name"
      
      # Remove original file
      rm "$source_file"
      
      if [[ $? -eq 0 ]]; then
        print_success "Removed original file: $source_file"
        return 0
      else
        print_error "Failed to remove original file: $source_file"
        return 1
      fi
    else
      print_error "Failed to archive: $filename"
      return 1
    fi
  fi
}

# Function to report on completed documents
report_completed_documents() {
  local dry_run="${1:-false}"
  
  print_header "Completed Document Analysis"
  
  # Check if the completed directory exists
  if [[ ! -d "$COMPLETED_DIR" ]]; then
    print_error "Completed directory does not exist: $COMPLETED_DIR"
    return 1
  fi
  
  # Check if the archived directory exists, create if not
  if [[ ! -d "$ARCHIVED_DIR" ]]; then
    if [[ "$dry_run" == "true" ]]; then
      print_info "Would create archived directory: $ARCHIVED_DIR"
    else
      mkdir -p "$ARCHIVED_DIR"
      print_success "Created archived directory: $ARCHIVED_DIR"
    fi
  fi
  
  local count=0
  
  # Generate list of completed documents
  print_info "Scanning for completed documents to archive..."
  
  for filepath in "$COMPLETED_DIR"/*.md; do
    if [[ -f "$filepath" ]]; then
      local filename=$(basename "$filepath")
      
      # Skip README files
      if [[ "$filename" == "README.md" ]]; then
        continue
      fi
      
      print_info "Found completed document: $filename"
      count=$((count + 1))
      
      if [[ "$dry_run" == "false" ]]; then
        archive_document "$filepath" "$dry_run"
      fi
    fi
  done
  
  if [[ $count -eq 0 ]]; then
    print_info "No completed documents found for archiving."
  else
    if [[ "$dry_run" == "true" ]]; then
      print_info "Found $count completed documents to archive."
    else
      print_success "Archived $count completed documents."
    fi
  fi
  
  return 0
}

# Function to report on obsolete documents
report_obsolete_documents() {
  local dry_run="${1:-false}"
  
  print_header "Obsolete Document Analysis"
  
  # These are documents that we know are obsolete and should be archived
  declare -a obsolete_docs=(
    "${PLANNING_DIR}/readme-new-draft.md"
    "${PLANNING_DIR}/readme-implementation.md"
  )
  
  local count=0
  
  for filepath in "${obsolete_docs[@]}"; do
    if [[ -f "$filepath" ]]; then
      local filename=$(basename "$filepath")
      print_info "Found obsolete document: $filename"
      count=$((count + 1))
      
      if [[ "$dry_run" == "false" ]]; then
        archive_document "$filepath" "$dry_run"
      fi
    fi
  done
  
  if [[ $count -eq 0 ]]; then
    print_info "No obsolete documents found for archiving."
  else
    if [[ "$dry_run" == "true" ]]; then
      print_info "Found $count obsolete documents to archive."
    else
      print_success "Archived $count obsolete documents."
    fi
  fi
  
  return 0
}

# Main function
main() {
  local dry_run=false
  
  # Parse command-line arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        print_header "Planning Document Archiving Tool"
        echo ""
        echo "Archives completed planning documents by moving them to the archived directory"
        echo "with the 'archived-' prefix."
        echo ""
        echo "Usage: $0 [options]"
        echo ""
        echo "Options:"
        echo "  -h, --help    Show this help message"
        echo "  -d, --dry-run Show what would be archived without making changes"
        echo ""
        exit 0
        ;;
      -d|--dry-run)
        dry_run=true
        shift
        ;;
      *)
        print_error "Unknown option: $1"
        echo "Use --help for usage information."
        exit 1
        ;;
    esac
  done
  
  print_header "Planning Document Archiving"
  
  if [ "$dry_run" = "true" ]; then
    print_info "Running in dry-run mode (no changes will be made)"
  fi
  
  # Process completed documents
  report_completed_documents "$dry_run"
  
  # Process obsolete documents
  report_obsolete_documents "$dry_run"
  
  print_info "Document archiving process complete."
}

# Run the main function
main "$@"