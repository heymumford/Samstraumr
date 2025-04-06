#\!/bin/bash

# For README, we'll add attribution to the footer if it doesn't already have the header
if [ "$1" = "/home/emumford/NativeLinuxProjects/Samstraumr/README.md" ]; then
  if grep -q "Copyright (c) 2025 \[Eric C. Mumford (@heymumford)" "$1"; then
    # If README already has the header, remove it and add footer attribution
    content=$(sed '/^<\!--/,/-->$/d' "$1")
    echo -e "$content\n\n---\n\nDocumentation and code generated with support from Claude 3.7, Gemini Deep Research." > "$1"
    echo "Removed header and added attribution to README footer: $1"
  elif \! grep -q "Generated with support from Claude" "$1"; then
    # If README doesn't have attribution in footer, add it
    echo -e "\n\n---\n\nDocumentation and code generated with support from Claude 3.7, Gemini Deep Research." >> "$1"
    echo "Added attribution to README footer: $1"
  else
    echo "Attribution already exists in README footer: $1"
  fi
else
  # For all other markdown files, update the header
  NEW_HEADER=$'<\!-- \nCopyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.\n-->\n\n'
  
  # Check if file already has the right header
  if grep -q "Copyright (c) 2025 \[Eric C. Mumford (@heymumford)" "$1"; then
    echo "Header already exists: $1"
  else
    # Remove any existing HTML comments at the start of the file
    content=$(sed '/^<\!--/,/-->$/d' "$1")
    # Add the new header and content
    echo -e "$NEW_HEADER$content" > "$1"
    echo "Updated header: $1"
  fi
fi
