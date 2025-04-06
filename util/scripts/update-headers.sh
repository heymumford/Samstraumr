#!/usr/bin/env bash
#==============================================================================
# Wrapper script for updating headers across the codebase
# Uses the consolidated header library
#==============================================================================
set -e

# Source the header library
source "$(git rev-parse --show-toplevel)/util/lib/header-lib.sh"

# Default header template
HEADER_TEMPLATE="$(git rev-parse --show-toplevel)/util/config/java-standard-header-template.txt"

# Parse arguments
DIRECTORY="$(git rev-parse --show-toplevel)"
FILE_TYPE="all"

# Parse arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    --dir)
      DIRECTORY="$2"
      shift 2
      ;;
    --type)
      FILE_TYPE="$2"
      shift 2
      ;;
    --template)
      HEADER_TEMPLATE="$2"
      shift 2
      ;;
    --help)
      echo "Usage: $(basename "$0") [--dir DIRECTORY] [--type java|md|all] [--template TEMPLATE_FILE]"
      exit 0
      ;;
    *)
      echo "Unknown argument: $1"
      exit 1
      ;;
  esac
done

# Ensure directory exists
if [ ! -d "$DIRECTORY" ]; then
  echo "Error: Directory $DIRECTORY does not exist"
  exit 1
fi

# Ensure template file exists
if [ ! -f "$HEADER_TEMPLATE" ]; then
  echo "Error: Template file $HEADER_TEMPLATE does not exist"
  exit 1
fi

# Update headers based on file type
case "$FILE_TYPE" in
  java)
    echo "Updating Java headers in $DIRECTORY"
    update_java_headers "$DIRECTORY" "$HEADER_TEMPLATE"
    ;;
  md)
    echo "Updating Markdown headers in $DIRECTORY"
    update_md_headers "$DIRECTORY" "$HEADER_TEMPLATE"
    ;;
  all)
    echo "Updating all headers in $DIRECTORY"
    update_java_headers "$DIRECTORY" "$HEADER_TEMPLATE"
    update_md_headers "$DIRECTORY" "$HEADER_TEMPLATE"
    ;;
  *)
    echo "Error: Unknown file type $FILE_TYPE. Use java, md, or all."
    exit 1
    ;;
esac

echo "Header updates completed successfully"
