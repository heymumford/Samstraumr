#\!/bin/bash
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# XML utility library for handling XML files, particularly Maven POM files
# This library provides precise XML manipulation functions using xmlstarlet

# Source common utilities if not already sourced
if [ -z "$LIB_COMMON_SOURCED" ]; then
  # Find the common lib in the same directory
  SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
  if [ -f "$SCRIPT_DIR/common.sh" ]; then
    source "$SCRIPT_DIR/common.sh"
  else
    echo "ERROR: common.sh not found in $SCRIPT_DIR"
    exit 1
  fi
fi

# Check if xmlstarlet is installed
if \! command -v xmlstarlet &> /dev/null; then
  echo_error "xmlstarlet is not installed. Please install it using 'sudo apt-get install xmlstarlet'"
  exit 1
fi

# XML validation function
# Usage: xml_validate_file <xml_file>
xml_validate_file() {
  local xml_file=$1
  
  if [ \! -f "$xml_file" ]; then
    echo_error "File not found: $xml_file"
    return 1
  fi
  
  # Validate XML syntax
  xmlstarlet val "$xml_file" > /dev/null 2>&1
  local result=$?
  
  if [ $result -ne 0 ]; then
    echo_error "XML validation failed for $xml_file"
    # Show detailed errors
    xmlstarlet val "$xml_file"
    return 1
  else
    echo_success "XML validation passed for $xml_file"
    return 0
  fi
}

# POM file validation function
# Usage: pom_validate_file <pom_file>
pom_validate_file() {
  local pom_file=$1
  
  if [ \! -f "$pom_file" ]; then
    echo_error "POM file not found: $pom_file"
    return 1
  fi
  
  # First, validate XML syntax
  xml_validate_file "$pom_file" || return 1
  
  # Check if the POM has a project root element
  if \! xmlstarlet sel -t -v "/project" "$pom_file" > /dev/null 2>&1; then
    echo_error "Invalid POM file: $pom_file - missing project root element"
    return 1
  fi
  
  # Check for common Maven elements
  if \! xmlstarlet sel -t -v "/project/modelVersion" "$pom_file" > /dev/null 2>&1; then
    echo_warning "POM file $pom_file is missing modelVersion element"
  fi
  
  # Check for abbreviated tag names (<n> instead of <name>)
  if xmlstarlet sel -t -v "/project/n" "$pom_file" > /dev/null 2>&1; then
    echo_error "POM file $pom_file contains <n> tag instead of <name>"
    return 1
  fi
  
  echo_success "POM file $pom_file is valid"
  return 0
}

# Fix POM file issues
# Usage: pom_fix_file <pom_file>
pom_fix_file() {
  local pom_file=$1
  local fixed=0
  
  if [ \! -f "$pom_file" ]; then
    echo_error "POM file not found: $pom_file"
    return 1
  fi
  
  # Create a backup of the original file
  local backup_file="${pom_file}.bak"
  cp "$pom_file" "$backup_file"
  
  # Check for <n> tag and replace with <name>
  if xmlstarlet sel -t -v "/project/n" "$pom_file" > /dev/null 2>&1; then
    local project_name=$(xmlstarlet sel -t -v "/project/n" "$pom_file")
    
    # Create temporary file
    local temp_file="${pom_file}.tmp"
    
    # Extract the content before and after the <n> tag
    local line_num=$(grep -n "<n>" "$pom_file" | cut -d: -f1)
    local end_line_num=$(grep -n "</n>" "$pom_file" | cut -d: -f1)
    
    if [ -n "$line_num" ] && [ -n "$end_line_num" ]; then
      # Create a new XML file with correct <name> tag
      {
        head -n $((line_num-1)) "$pom_file"
        echo "  <name>${project_name}</name>"
        tail -n +$((end_line_num+1)) "$pom_file"
      } > "$temp_file"
      
      # Replace the original file
      mv "$temp_file" "$pom_file"
      fixed=1
      echo_success "Fixed POM file: Replaced <n> with <name> in $pom_file"
    else
      echo_warning "Could not locate exact <n> tag lines in $pom_file"
    fi
  fi
  
  # Validate the fixed file
  if [ $fixed -eq 1 ]; then
    if \! xml_validate_file "$pom_file"; then
      echo_error "Fix introduced XML errors. Restoring backup."
      mv "$backup_file" "$pom_file"
      return 1
    fi
  else
    echo_info "No issues found to fix in $pom_file"
  fi
  
  # Remove backup if all went well
  rm "$backup_file"
  
  return 0
}

# Process all POM files in a directory recursively
# Usage: pom_process_directory <directory> <action>
# Actions: validate, fix
pom_process_directory() {
  local directory=$1
  local action=$2
  local exit_code=0
  
  if [ \! -d "$directory" ]; then
    echo_error "Directory not found: $directory"
    return 1
  fi
  
  if [ -z "$action" ]; then
    action="validate"
  fi
  
  echo_info "Processing POM files in $directory (action: $action)"
  
  # Find all pom.xml files
  local pom_files=$(find "$directory" -name "pom.xml" -type f)
  
  if [ -z "$pom_files" ]; then
    echo_warning "No POM files found in $directory"
    return 0
  fi
  
  # Process each POM file
  for pom_file in $pom_files; do
    echo_info "Processing $pom_file"
    
    case "$action" in
      validate)
        pom_validate_file "$pom_file" || exit_code=1
        ;;
      fix)
        pom_fix_file "$pom_file" || exit_code=1
        ;;
      *)
        echo_error "Unknown action: $action. Use 'validate' or 'fix'."
        return 1
        ;;
    esac
  done
  
  return $exit_code
}

# Extract XML element value
# Usage: xml_get_element <xml_file> <xpath>
xml_get_element() {
  local xml_file=$1
  local xpath=$2
  
  if [ \! -f "$xml_file" ]; then
    echo_error "File not found: $xml_file"
    return 1
  fi
  
  xmlstarlet sel -t -v "$xpath" "$xml_file" 2>/dev/null
  return $?
}

# Set XML element value
# Usage: xml_set_element <xml_file> <xpath> <value>
xml_set_element() {
  local xml_file=$1
  local xpath=$2
  local value=$3
  
  if [ \! -f "$xml_file" ]; then
    echo_error "File not found: $xml_file"
    return 1
  fi
  
  # Create a backup of the original file
  local backup_file="${xml_file}.bak"
  cp "$xml_file" "$backup_file"
  
  # Edit the XML file
  xmlstarlet ed -L -u "$xpath" -v "$value" "$xml_file"
  local result=$?
  
  if [ $result -ne 0 ]; then
    echo_error "Failed to set element $xpath in $xml_file"
    # Restore the backup
    mv "$backup_file" "$xml_file"
    return 1
  else
    # Remove the backup
    rm "$backup_file"
    echo_success "Updated element $xpath in $xml_file"
    return 0
  fi
}

# IMPORTANT: NEVER USE GREP FOR XML FILES, ONLY XMLSTARLET
# This is permanently encoded into the project standards

# Check for common POM file issues and print report
# Usage: pom_check_issues <pom_file>
pom_check_issues() {
  local pom_file=$1
  local issues_found=0
  
  if [ \! -f "$pom_file" ]; then
    echo_error "POM file not found: $pom_file"
    return 1
  fi
  
  echo_info "Checking POM file for issues: $pom_file"
  
  # Check for XML syntax errors
  xmlstarlet val "$pom_file" > /dev/null 2>&1
  if [ $? -ne 0 ]; then
    echo_error "XML validation failed"
    issues_found=1
  fi
  
  # Check for abbreviated tag names (<n> instead of <name>)
  if xmlstarlet sel -t -v "/project/n" "$pom_file" > /dev/null 2>&1; then
    echo_error "Found <n> tag instead of <name>"
    issues_found=1
  fi
  
  # Check for missing required elements
  local required_elements=("modelVersion" "groupId" "artifactId" "version")
  
  for element in "${required_elements[@]}"; do
    if \! xmlstarlet sel -t -v "/project/$element" "$pom_file" > /dev/null 2>&1; then
      echo_warning "Missing required element: <$element>"
      issues_found=1
    fi
  done
  
  # Check for duplicate profiles
  local profiles=$(xmlstarlet sel -t -m "/project/profiles/profile/id" -v "." -n "$pom_file" | sort)
  local duplicate_profiles=$(echo "$profiles" | sort | uniq -d)
  
  if [ -n "$duplicate_profiles" ]; then
    echo_error "Found duplicate profile IDs:"
    echo "$duplicate_profiles"
    issues_found=1
  fi
  
  if [ $issues_found -eq 0 ]; then
    echo_success "No issues found in POM file"
    return 0
  else
    return 1
  fi
}

# Main function to process command line arguments
# Usage: xml_main_command <command> [args...]
xml_main_command() {
  local command=$1
  shift
  
  case "$command" in
    validate)
      if [ -f "$1" ]; then
        xml_validate_file "$1"
      elif [ -d "$1" ]; then
        pom_process_directory "$1" "validate"
      else
        echo_error "Invalid file or directory: $1"
        return 1
      fi
      ;;
    fix)
      if [ -f "$1" ]; then
        pom_fix_file "$1"
      elif [ -d "$1" ]; then
        pom_process_directory "$1" "fix"
      else
        echo_error "Invalid file or directory: $1"
        return 1
      fi
      ;;
    check)
      if [ -f "$1" ]; then
        pom_check_issues "$1"
      else
        echo_error "Invalid POM file: $1"
        return 1
      fi
      ;;
    get)
      xml_get_element "$1" "$2"
      ;;
    set)
      xml_set_element "$1" "$2" "$3"
      ;;
    help)
      echo "XML Utility Library"
      echo "Usage: xml_main_command <command> [args...]"
      echo
      echo "Commands:"
      echo "  validate <file|dir>   Validate XML or POM file(s)"
      echo "  fix <file|dir>        Fix common issues in POM file(s)"
      echo "  check <file>          Check for common issues in a POM file"
      echo "  get <file> <xpath>    Get value of an XML element"
      echo "  set <file> <xpath> <value>  Set value of an XML element"
      echo "  help                  Show this help message"
      ;;
    *)
      echo_error "Unknown command: $command"
      xml_main_command help
      return 1
      ;;
  esac
}

# Export library marker
export LIB_XML_SOURCED=1
