#!/bin/bash
# Script to remove legacy headers from Java files

# Find all Java files
java_files=$(find /home/emumford/NativeLinuxProjects/Samstraumr -type f -name "*.java")

for file in $java_files; do
  # Check if file has a comment header block
  if grep -q "^\/\*" "$file"; then
    # Get the package line number
    package_line=$(grep -n "^package" "$file" | head -1 | cut -d: -f1)
    
    if [ ! -z "$package_line" ]; then
      # Delete everything before the package line
      sed -i "1,$(($package_line-1))d" "$file"
      echo "Updated $file"
    fi
  fi
done

echo "Completed updating Java files"