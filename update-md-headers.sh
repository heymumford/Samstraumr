#!/bin/bash
# Script to remove legacy headers from Markdown files

# Find all Markdown files
md_files=$(find /home/emumford/NativeLinuxProjects/Samstraumr -type f -name "*.md" | grep -v "target/")

for file in $md_files; do
  # Skip non-documentation files
  if [[ "$file" == *"README.md" ]]; then
    # Special handling for README.md which needs version but not other metadata
    sed -i '/^```$/,/^```$/s/^Last updated:.*$//' "$file"
    sed -i '/^```$/,/^```$/s/^Author:.*$//' "$file"
    sed -i '/^```$/,/^```$/s/^Contributors:.*$//' "$file"
    # Remove empty lines in the version block
    sed -i '/^```$/,/^```$/s/^[[:space:]]*$//g' "$file"
    # Clean up multiple newlines
    sed -i ':a;N;$!ba;s/\n\n\n\n/\n\n/g' "$file"
  else
    # For other markdown files, remove the entire version block
    sed -i '/^```$/,/^```$/d' "$file"
    # Clean up multiple newlines after removal
    sed -i ':a;N;$!ba;s/\n\n\n\n/\n\n/g' "$file"
  fi
  
  echo "Updated $file"
done

echo "Completed updating Markdown files"