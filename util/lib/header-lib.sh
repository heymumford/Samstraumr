#!/usr/bin/env bash
#==============================================================================
# Header Library - Consolidated header-related functions
# This library contains functions for managing file headers across the codebase
#==============================================================================

# Add a license header to a file
add_license_header() {
  local file="$1"
  local license_template="$2"
  local file_type="${file##*.}"
  
  # Only add if file doesn't already have a header
  if ! grep -q "Copyright" "$file"; then
    case "$file_type" in
      java)
        # Java-style comment header
        sed -i "1s|^|/*\n * $(cat "$license_template")\n */\n\n|" "$file"
        ;;
      sh|bash)
        # Shell-style comment header
        sed -i "1s|^|#\n# $(cat "$license_template")\n#\n\n|" "$file"
        ;;
      md|markdown)
        # Markdown-style comment header
        sed -i "1s|^|<!-- \n$(cat "$license_template")\n-->\n\n|" "$file"
        ;;
      xml|html)
        # XML-style comment header
        sed -i "1s|^|<!--\n$(cat "$license_template")\n-->\n\n|" "$file"
        ;;
      *)
        # Default to shell-style comments
        sed -i "1s|^|#\n# $(cat "$license_template")\n#\n\n|" "$file"
        ;;
    esac
    echo "Added header to $file"
  else
    echo "Header already exists in $file"
  fi
}

# Update headers in Java files
update_java_headers() {
  local directory="$1"
  local header_template="$2"
  
  find "$directory" -name "*.java" -type f | while read -r file; do
    # Check if file has a header
    if grep -q "Copyright" "$file"; then
      # Replace existing header
      sed -i '/\/\*\(/,/\*\//c\/*\n * '"$(cat "$header_template")"'\n */' "$file"
      echo "Updated header in $file"
    else
      # Add new header
      add_license_header "$file" "$header_template"
    fi
  done
}

# Update headers in Markdown files
update_md_headers() {
  local directory="$1"
  local header_template="$2"
  
  find "$directory" -name "*.md" -type f | while read -r file; do
    # Check if file has a header
    if grep -q "Copyright" "$file"; then
      # Replace existing header
      sed -i '/<!--/,/-->/c\<!--\n'"$(cat "$header_template")"'\n-->' "$file"
      echo "Updated header in $file"
    else
      # Add new header
      add_license_header "$file" "$header_template"
    fi
  done
}

# Simplify existing headers that are too verbose
simplify_headers() {
  local directory="$1"
  local new_header="Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7."
  
  # Process different file types
  find "$directory" \( -name "*.java" -o -name "*.md" -o -name "*.sh" \) -type f | while read -r file; do
    if grep -q "Copyright" "$file"; then
      case "${file##*.}" in
        java)
          # Java files
          sed -i '/\/\*\(/,/\*\//c\/*\n * '"$new_header"'\n */' "$file"
          ;;
        md)
          # Markdown files
          sed -i '/<!--/,/-->/c\<!-- \n'"$new_header"'\n-->' "$file"
          ;;
        sh|bash)
          # Shell scripts
          sed -i '1,10s/^# Copyright.*/# '"$new_header"'/' "$file"
          ;;
      esac
      echo "Simplified header in $file"
    fi
  done
}
