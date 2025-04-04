#!/bin/bash
# Script to update Java file headers and organize imports in the Samstraumr project
# This script will traverse the source tree and update Java files with:
# - Standard file header with copyright notice
# - Organized import sections (standard Java, third-party, project-specific)
# - Comprehensive file purpose documentation

ROOT_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/Samstraumr/samstraumr-core/src"
FILE_LIST=$(find $ROOT_DIR -name "*.java" | sort)

echo "Starting Java file header standardization..."
echo "Found $(echo "$FILE_LIST" | wc -l) Java files to process."

count=0
for file in $FILE_LIST; do
  echo "Processing: $file"
  
  # Get relative path for display
  rel_path="${file#$ROOT_DIR/}"
  class_name=$(basename "$file" .java)
  package_name=$(grep "^package" "$file" | sed -E 's/package\s+([^;]+);.*/\1/')
  
  # Check if file already has standardized header format
  if grep -q "Key features:" "$file" && grep -q "Integration with the tube substrate model" "$file"; then
    echo "  - Already has standardized header, skipping."
    continue
  fi
  
  # Check imports to ensure they're preserved
  imports=$(sed -n '/^import/,/^[^i]/p' "$file" | grep "^import")
  std_imports=$(echo "$imports" | grep -v "org\.samstraumr" | grep -v "org\.slf4j" | grep -v "^import static" | sort)
  third_party_imports=$(echo "$imports" | grep -E "org\.slf4j|junit|cucumber|findbugs|apache|jackson" | sort)
  samstraumr_imports=$(echo "$imports" | grep "org\.samstraumr" | sort)
  static_imports=$(echo "$imports" | grep "^import static" | sort)
  
  # Backup the file
  cp "$file" "${file}.bak"
  
  # Extract class Javadoc
  javadoc=$(sed -n '/\/\*\*/,/\*\//p' "$file" | grep -v "^import\|^package")
  
  # Simplistic purpose extraction - this would ideally be more intelligent in a real implementation
  if [[ "$class_name" =~ "Test" ]]; then
    purpose="Test class for ${class_name%Test} functionality"
  elif [[ "$class_name" =~ "Steps" ]]; then
    purpose="Step definitions for ${class_name%Steps} in Cucumber BDD tests"
  elif [[ "$class_name" =~ "Factory" ]]; then
    purpose="Factory for creating ${class_name%Factory} instances"
  else
    purpose="Implementation of the ${class_name} concept in the Samstraumr framework"
  fi
  
  # Create temp file with new header
  temp_file=$(mktemp)
  
  # Write header
  cat > "$temp_file" << EOF
/*
 * ${purpose}
 * 
 * This class implements the core functionality for ${class_name} in the Samstraumr
 * tube-based processing framework. It provides the essential infrastructure for
 * the tube ecosystem to maintain its hierarchical design and data processing capabilities.
 * 
 * Key features:
 * - Implementation of the ${class_name} concept
 * - Integration with the tube substrate model
 * - Support for hierarchical tube organization
 */

package ${package_name};

EOF

  # Add imports in the correct order
  if [ ! -z "$static_imports" ]; then
    echo "// Static imports" >> "$temp_file"
    echo "$static_imports" >> "$temp_file"
    echo "" >> "$temp_file"
  fi
  
  if [ ! -z "$std_imports" ]; then
    echo "// Standard Java imports" >> "$temp_file"
    echo "$std_imports" >> "$temp_file"
    echo "" >> "$temp_file"
  fi
  
  if [ ! -z "$third_party_imports" ]; then
    echo "// Third-party imports" >> "$temp_file"
    echo "$third_party_imports" >> "$temp_file"
    echo "" >> "$temp_file"
  fi
  
  if [ ! -z "$samstraumr_imports" ]; then
    echo "// Samstraumr imports" >> "$temp_file"
    echo "$samstraumr_imports" >> "$temp_file"
    echo "" >> "$temp_file"
  fi
  
  # Add the existing Javadoc if it exists
  if [ ! -z "$javadoc" ]; then
    echo "$javadoc" >> "$temp_file"
  fi
  
  # Add the rest of the file, skipping the package and imports
  sed -n '/^public\|^final\|^abstract\|^class\|^interface\|^enum/,$p' "$file" >> "$temp_file"
  
  # Replace the original file
  mv "$temp_file" "$file"
  
  count=$((count+1))
  echo "  - Updated header and organized imports"
done

echo "Completed processing $count Java files."
echo "Backup files with .bak extension are available if needed."