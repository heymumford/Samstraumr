#!/bin/bash
# 
# Samstraumr root scripts removal tool
# This script safely removes scripts from the root directory after they've been moved to bin/
#

set -e

# Command-line arguments
DRY_RUN=false
INTERACTIVE=true
FORCE=false

# Print message in color
print_message() {
  echo -e "\033[1;34m$1\033[0m"
}

# Print error message in color
print_error() {
  echo -e "\033[1;31m$1\033[0m"
}

# Print success message in color
print_success() {
  echo -e "\033[1;32m$1\033[0m"
}

# Print warning message in color
print_warning() {
  echo -e "\033[1;33m$1\033[0m"
}

# Print section header
print_section() {
  echo -e "\033[1;36m==== $1 ====\033[0m"
}

# Ask yes/no question (if in interactive mode)
ask_yes_no() {
  local prompt="$1"
  
  if [[ "$INTERACTIVE" == "false" ]]; then
    if [[ "$FORCE" == "true" ]]; then
      return 0
    else
      print_warning "Not confirmed: $prompt (use --force to auto-confirm)"
      return 1
    fi
  fi
  
  local response
  while true; do
    read -p "$prompt (y/n) " response
    case "$response" in
      [yY]* ) return 0 ;;
      [nN]* ) return 1 ;;
      * ) echo "Please answer yes or no." ;;
    esac
  done
}

# Display help
show_help() {
  echo "Usage: $0 [options]"
  echo
  echo "Safely removes scripts from the root directory that have been moved to bin/"
  echo
  echo "Options:"
  echo "  -h, --help          Show this help message"
  echo "  -d, --dry-run       Show what would be done without making changes"
  echo "  -f, --force         Automatically answer yes to all prompts"
  echo "  -n, --non-interactive  Run in non-interactive mode (no prompts)"
  echo
  echo "Examples:"
  echo "  $0                  Run in interactive mode (default)"
  echo "  $0 --dry-run        Show what would be done without making changes"
  echo "  $0 --force          Remove all scripts without prompting"
  exit 0
}

# Find the Samstraumr project root
find_project_root() {
  local script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  echo "$(cd "$script_dir/.." && pwd)"
}

# Process command line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    -h|--help)
      show_help
      ;;
    -d|--dry-run)
      DRY_RUN=true
      shift
      ;;
    -f|--force)
      FORCE=true
      shift
      ;;
    -n|--non-interactive)
      INTERACTIVE=false
      shift
      ;;
    *)
      print_error "Unknown option: $1"
      echo "Use --help to see available options."
      exit 1
      ;;
  esac
done

PROJECT_ROOT=$(find_project_root)
BIN_DIR="${PROJECT_ROOT}/bin"

print_section "Samstraumr Root Scripts Removal Tool"
print_message "Project root: $PROJECT_ROOT"
print_message "Bin directory: $BIN_DIR"

if [[ "$DRY_RUN" == "true" ]]; then
  print_warning "DRY RUN MODE: No files will be changed"
fi

if [[ "$FORCE" == "true" ]]; then
  print_warning "FORCE MODE: Will automatically confirm all prompts"
fi

# Check if bin directory is in PATH
if ! echo "$PATH" | tr ':' '\n' | grep -q "$BIN_DIR"; then
  print_warning "⚠️  WARNING: $BIN_DIR is not in your PATH!"
  print_warning "This means you won't be able to run Samstraumr scripts directly."
  print_warning "Run bin/setup-path.sh first to set up your PATH."
  
  if ! ask_yes_no "Continue anyway?"; then
    print_message "Exiting without making changes."
    exit 0
  fi
fi

# Only prompt for aliases in interactive mode
if [[ "$INTERACTIVE" == "true" ]]; then
  print_warning "⚠️  Have you run bin/create-aliases.sh to set up aliases for these scripts?"
  if ! ask_yes_no "Have you set up aliases?"; then
    print_warning "It's recommended to set up aliases before removing root scripts."
    print_warning "Run bin/create-aliases.sh to set up aliases."
    
    if ! ask_yes_no "Continue anyway?"; then
      print_message "Exiting without making changes."
      exit 0
    fi
  fi
fi

# Check for all s8r* scripts in the root
print_message "Checking for s8r* scripts in the root directory..."
ROOT_SCRIPTS=$(find "$PROJECT_ROOT" -maxdepth 1 -name "s8r*" -type f -o -name "s8r*" -type l | sort)

if [[ -z "$ROOT_SCRIPTS" ]]; then
  print_message "No s8r* scripts found in the root directory."
  exit 0
fi

# Display scripts to be removed
print_message "The following scripts will be removed from the root directory:"
echo "$ROOT_SCRIPTS" | while read -r script; do
  script_name=$(basename "$script")
  echo "  - $script_name"
done

# Ask for confirmation
if ! ask_yes_no "Are you sure you want to remove these scripts?"; then
  print_message "Operation cancelled. No changes were made."
  exit 0
fi

# Proceed with removal
print_message "Removing scripts..."
echo "$ROOT_SCRIPTS" | while read -r script; do
  # Get script name
  script_name=$(basename "$script")
  
  # Look for the script in bin subdirectories
  found=false
  for category in core build test component dev migration help utils ai; do
    bin_script="$BIN_DIR/$category/$script_name"
    if [[ -f "$bin_script" ]]; then
      print_message "Found $script_name in bin/$category"
      
      if [[ "$DRY_RUN" == "true" ]]; then
        print_message "Would remove: $script"
      else
        rm -v "$script"
        print_success "Removed $script"
      fi
      
      found=true
      break
    fi
  done
  
  # If the script wasn't found in any bin subdirectory
  if [[ "$found" == "false" ]]; then
    print_warning "⚠️  WARNING: $script_name not found in bin directory structure!"
    print_warning "This script might be lost if you remove it."
    
    if ask_yes_no "Remove $script_name anyway?"; then
      if [[ "$DRY_RUN" == "true" ]]; then
        print_message "Would remove: $script"
      else
        rm -v "$script"
        print_success "Removed $script"
      fi
    else
      print_message "Skipping $script_name."
    fi
  fi
done

if [[ "$DRY_RUN" == "true" ]]; then
  print_success "DRY RUN COMPLETED - No files were changed"
else
  print_success "Root scripts removal completed!"
fi

print_message "You can now use the bin directory executables or the aliases you created."
print_message "Make sure to update any documentation, CI/CD pipelines, or personal workflows."

print_section "Next Steps"
print_message "1. Update any scripts or documentation that referenced the root scripts"
print_message "2. Update CI/CD pipelines to use bin directory scripts or aliases"
print_message "3. Consider adding a note to the README about the new script locations"
