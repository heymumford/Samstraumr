#!/bin/bash
# This is a redirect script that points to the new location

# Skip warnings when called from s8r
if [[ "$SAMSTRAUMR_CLI" != "s8r" ]]; then
  echo -e "\033[1;33mWARNING: check-encoding.sh has been moved to util/bin/quality/check-encoding.sh\033[0m"
  echo -e "Please use \033[1;32m./util/bin/quality/check-encoding.sh\033[0m instead."
  echo ""
fi

# When called from s8r, perform check directly to avoid color variable conflicts
if [[ "$SAMSTRAUMR_CLI" == "s8r" ]]; then
  # Parse arguments
  VERBOSE=false
  FIX_MODE=false
  
  # Parse command line arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -v|--verbose)
        VERBOSE=true
        shift
        ;;
      -f|--fix)
        FIX_MODE=true
        shift
        ;;
      -h|--help)
        echo "Usage: ./check-encoding.sh [options]"
        echo ""
        echo "Options:"
        echo "  -v, --verbose   Enable verbose output"
        echo "  -f, --fix       Fix encoding and line ending issues"
        echo "  -h, --help      Display this help message"
        exit 0
        ;;
      *)
        echo "Unknown option: $1"
        exit 1
        ;;
    esac
  done
  
  # Set project root
  PROJECT_ROOT="/home/emumford/NativeLinuxProjects/Samstraumr"
  
  # Run the check (simplified version)
  echo "Checking file encodings and line endings..."
  
  # Find files with wrong encoding (non-UTF-8)
  echo "Checking for non-UTF-8 encoded files..."
  cd "$PROJECT_ROOT" && find . -type f -name "*.java" -o -name "*.xml" -o -name "*.properties" -o -name "*.md" | while read -r file; do
    if [[ "$file" != *"target/"* && "$file" != *".git/"* ]]; then
      if ! file -bi "$file" | grep -q "charset=utf-8"; then
        echo "  Non-UTF-8 encoding: $file"
        if $FIX_MODE; then
          echo "    Converting to UTF-8..."
          iconv -f ISO-8859-1 -t UTF-8 "$file" > "${file}.utf8" && mv "${file}.utf8" "$file"
        fi
      elif $VERBOSE; then
        echo "  OK: $file (UTF-8)"
      fi
    fi
  done
  
  # Find files with wrong line endings (Windows CRLF)
  echo "Checking for CRLF line endings..."
  cd "$PROJECT_ROOT" && find . -type f -name "*.java" -o -name "*.xml" -o -name "*.properties" -o -name "*.md" | while read -r file; do
    if [[ "$file" != *"target/"* && "$file" != *".git/"* && "$file" != *".bat" && "$file" != *".cmd" ]]; then
      if grep -q $'\r' "$file"; then
        echo "  CRLF line endings: $file"
        if $FIX_MODE; then
          echo "    Converting to LF..."
          dos2unix "$file" 2>/dev/null
        fi
      elif $VERBOSE; then
        echo "  OK: $file (LF)"
      fi
    fi
  done
  
  echo "Encoding check complete."
else
  # Forward to new script for normal usage
  /home/emumford/NativeLinuxProjects/Samstraumr/util/bin/quality/check-encoding.sh "$@"
fi