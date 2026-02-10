#!/bin/bash
#
# standardize-test-tags.sh - Script to standardize test tags across feature files
#
# This script updates feature files to use the standardized test tag structure
# defined in the test-pyramid-tags.md document.
#
# Usage: ./standardize-test-tags.sh [directory]
#
# If directory is not specified, it will process all feature files in the current project.

set -e

SCRIPT_DIR=$(dirname "$(readlink -f "$0")")
PROJECT_ROOT=$(git rev-parse --show-toplevel 2>/dev/null || echo "$SCRIPT_DIR/../../")
TARGET_DIR=${1:-"$PROJECT_ROOT"}

echo "=== Samstraumr Test Tag Standardization ==="
echo "Target directory: $TARGET_DIR"

# Create a backup directory for original files
BACKUP_DIR="$PROJECT_ROOT/tag-standardization-backup-$(date +%Y%m%d%H%M%S)"
mkdir -p "$BACKUP_DIR"
echo "Backup directory created: $BACKUP_DIR"

# Find all feature files
FEATURE_FILES=$(find "$TARGET_DIR" -name "*.feature" -type f)
FILE_COUNT=$(echo "$FEATURE_FILES" | wc -l)
echo "Found $FILE_COUNT feature files to process"

# Log file for changes
LOG_FILE="$PROJECT_ROOT/tag-standardization-changes.log"
echo "# Test Tag Standardization Changes - $(date)" > "$LOG_FILE"
echo "## Files processed: $FILE_COUNT" >> "$LOG_FILE"
echo "" >> "$LOG_FILE"

# Tag mapping functions
map_level_tag() {
    local tag=$1
    case $tag in
        "@L0_Tube") echo "@L0_Unit";;
        "@L0_Core") echo "@L0_Unit";;
        "@L1_Composite") echo "@L1_Component";;
        "@L1_Bundle") echo "@L1_Component";;
        "@L2_Machine") echo "@L2_Integration";;
        "@L3_System") echo "@L3_System";; # No change needed
        *) echo "$tag";;
    esac
}

map_criticality_tag() {
    local tag=$1
    case $tag in
        "@ATL") echo "@Functional";;
        "@BTL") echo "@ErrorHandling";;
        *) echo "$tag";;
    esac
}

map_capability_tag() {
    local tag=$1
    case $tag in
        "@Flow") echo "@DataFlow";;
        "@State") echo "@State";; # No change needed
        "@Identity") echo "@Identity";; # No change needed
        "@Awareness") echo "@Monitoring";;
        "@Init") echo "@Lifecycle";;
        "@Lifecycle") echo "@Lifecycle";; # No change needed
        *) echo "$tag";;
    esac
}

standardize_file_tags() {
    local file=$1
    local basename=$(basename "$file")
    local changes=0
    
    echo "Processing $basename..."
    
    # Create backup of original file
    cp "$file" "$BACKUP_DIR/$(basename "$file")"
    
    # Extract feature tag line - look for lines that start with @
    local feature_tag_line=$(grep -n "^@" "$file" | head -1 | cut -d: -f1)
    if [[ -z "$feature_tag_line" ]]; then
        echo "  No feature tag line found, skipping"
        return
    fi
    
    # Check if this is really a feature tag line (followed by a Feature: line)
    local next_line=$((feature_tag_line + 1))
    local next_line_content=$(sed -n "${next_line}p" "$file")
    if [[ ! "$next_line_content" =~ Feature: ]]; then
        echo "  Tag line not followed by Feature line, skipping"
        return
    fi
    
    # Get the current tags
    local current_tags=$(sed -n "${feature_tag_line}p" "$file" | grep -o '@[A-Za-z0-9_]\+')
    
    # Initialize tag categories
    local level_tag=""
    local type_tag=""
    local feature_tag=""
    local has_functional=false
    local has_error_handling=false
    
    # Analyze existing tags
    for tag in $current_tags; do
        if [[ "$tag" == "@L0_"* || "$tag" == "@L1_"* || "$tag" == "@L2_"* || "$tag" == "@L3_"* ]]; then
            level_tag=$(map_level_tag "$tag")
        elif [[ "$tag" == "@ATL" ]]; then
            has_functional=true
        elif [[ "$tag" == "@BTL" ]]; then
            has_error_handling=true
        elif [[ "$tag" == "@Flow" || "$tag" == "@State" || "$tag" == "@Identity" || "$tag" == "@Awareness" ]]; then
            feature_tag=$(map_capability_tag "$tag")
        fi
    done
    
    # Build new tag line
    local new_tags="$level_tag"
    
    # Add appropriate functional tag if needed
    if [[ "$has_functional" == true && ! "$new_tags" =~ "@Functional" ]]; then
        new_tags="$new_tags @Functional"
    fi
    
    # Add appropriate error handling tag if needed
    if [[ "$has_error_handling" == true && ! "$new_tags" =~ "@ErrorHandling" ]]; then
        new_tags="$new_tags @ErrorHandling"
    fi
    
    # Add feature tag if found
    if [[ -n "$feature_tag" && ! "$new_tags" =~ "$feature_tag" ]]; then
        new_tags="$new_tags $feature_tag"
    fi
    
    # Make sure we have at least the level tag
    if [[ -z "$level_tag" ]]; then
        echo "  Could not determine level tag, skipping"
        return
    fi
    
    # Replace the feature tag line
    local current_line=$(sed -n "${feature_tag_line}p" "$file")
    local next_line=$(sed -n "${next_line}p" "$file")
    
    # Check if "Feature:" is on the same line or the next line
    if [[ "$current_line" =~ Feature: ]]; then
        # Tags and Feature: on same line
        local new_line="${current_line//@*Feature:/$new_tags Feature:}"
        
        if [[ "$current_line" != "$new_line" ]]; then
            sed -i "${feature_tag_line}s/.*/$new_line/" "$file"
            changes=$((changes + 1))
            
            # Log the change
            echo "### $basename" >> "$LOG_FILE"
            echo "Original: \`$current_line\`" >> "$LOG_FILE"
            echo "New:      \`$new_line\`" >> "$LOG_FILE"
            echo "" >> "$LOG_FILE"
        fi
    else
        # Tags and Feature: on separate lines
        local new_line="$new_tags"
        
        if [[ "$current_line" != "$new_line" ]]; then
            sed -i "${feature_tag_line}s/.*/$new_line/" "$file"
            changes=$((changes + 1))
            
            # Log the change
            echo "### $basename" >> "$LOG_FILE"
            echo "Original: \`$current_line\`" >> "$LOG_FILE"
            echo "New:      \`$new_line\`" >> "$LOG_FILE"
            echo "" >> "$LOG_FILE"
        fi
    fi
    
    # Now process scenario tags - look for lines starting with @
    local scenario_lines=$(grep -n "^[[:space:]]*@" "$file" | grep -v "^${feature_tag_line}:" | cut -d: -f1)
    
    for line in $scenario_lines; do
        local current_scenario_line=$(sed -n "${line}p" "$file")
        local next_scenario_line=$((line + 1))
        local next_line_content=$(sed -n "${next_scenario_line}p" "$file")
        
        # Check if this is really a scenario tag line (followed by a Scenario: line)
        if [[ ! "$current_scenario_line" =~ Scenario: && ! "$next_line_content" =~ Scenario: ]]; then
            continue
        fi
        
        local current_scenario_tags=$(echo "$current_scenario_line" | grep -o '@[A-Za-z0-9_]\+')
        
        # Map scenario tags
        local new_scenario_tags=""
        local has_functional_scenario=false
        local has_error_handling_scenario=false
        local has_feature_tag=false
        
        for tag in $current_scenario_tags; do
            if [[ "$tag" == "@ATL" ]]; then
                has_functional_scenario=true
            elif [[ "$tag" == "@BTL" ]]; then
                has_error_handling_scenario=true
            elif [[ "$tag" == "@Flow" || "$tag" == "@State" || "$tag" == "@Identity" || "$tag" == "@Awareness" ]]; then
                new_scenario_tags="$new_scenario_tags $(map_capability_tag "$tag")"
                has_feature_tag=true
            elif [[ "$tag" == "@Pattern" ]]; then
                # Don't directly map @Pattern, will be handled by specific patterns
                true
            else
                new_scenario_tags="$new_scenario_tags $tag"
            fi
        done
        
        # Add functional tag if needed
        if [[ "$has_functional_scenario" == true && ! "$new_scenario_tags" =~ "@Functional" ]]; then
            new_scenario_tags="@Functional $new_scenario_tags"
        fi
        
        # Add error handling tag if needed
        if [[ "$has_error_handling_scenario" == true && ! "$new_scenario_tags" =~ "@ErrorHandling" ]]; then
            new_scenario_tags="@ErrorHandling $new_scenario_tags"
        fi
        
        # Add a default DataFlow tag if no feature tag is present
        if [[ "$has_feature_tag" == false && ! "$new_scenario_tags" =~ "@" ]]; then
            # Use the first feature tag from the feature line if available
            if [[ -n "$feature_tag" ]]; then
                new_scenario_tags="@Functional $feature_tag $new_scenario_tags"
            else
                new_scenario_tags="@Functional @DataFlow $new_scenario_tags"
            fi
        fi
        
        # Trim leading/trailing spaces
        new_scenario_tags=$(echo "$new_scenario_tags" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
        
        # Check if this is a single-line or multi-line scenario
        if [[ "$current_scenario_line" =~ Scenario: ]]; then
            # Single line: Tags and Scenario: on the same line
            local new_scenario_line="${current_scenario_line//@*Scenario:/$new_scenario_tags Scenario:}"
            
            if [[ "$current_scenario_line" != "$new_scenario_line" ]]; then
                sed -i "${line}s/.*/$new_scenario_line/" "$file"
                changes=$((changes + 1))
                
                # Log the change
                echo "#### Scenario tag in $basename line $line" >> "$LOG_FILE"
                echo "Original: \`$current_scenario_line\`" >> "$LOG_FILE"
                echo "New:      \`$new_scenario_line\`" >> "$LOG_FILE"
                echo "" >> "$LOG_FILE"
            fi
        else
            # Multi-line: Tags and Scenario: on separate lines
            local new_scenario_line="$new_scenario_tags"
            
            if [[ "$current_scenario_line" != "$new_scenario_line" ]]; then
                sed -i "${line}s/.*/$new_scenario_line/" "$file"
                changes=$((changes + 1))
                
                # Log the change
                echo "#### Scenario tag in $basename line $line" >> "$LOG_FILE"
                echo "Original: \`$current_scenario_line\`" >> "$LOG_FILE"
                echo "New:      \`$new_scenario_line\`" >> "$LOG_FILE"
                echo "" >> "$LOG_FILE"
            fi
        fi
    done
    
    echo "  Made $changes changes to $basename"
}

# Process each feature file
for file in $FEATURE_FILES; do
    standardize_file_tags "$file"
done

echo "=== Standardization Complete ==="
echo "Original files backed up to: $BACKUP_DIR"
echo "Changes logged to: $LOG_FILE"
echo "Total files processed: $FILE_COUNT"