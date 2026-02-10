#!/bin/bash
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0
#
# Script to migrate test code to use updated API patterns

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Load common functions
if [ -f "$SCRIPT_DIR/../lib/common.sh" ]; then
    source "$SCRIPT_DIR/../lib/common.sh"
else
    echo -e "${RED}Error: common.sh not found${NC}"
    exit 1
fi

# Default paths
TEST_DIR="$PROJECT_ROOT/modules/samstraumr-core/src/test"
BACKUP_DIR="$PROJECT_ROOT/api-migration-backup"

# Flags
DRY_RUN=0
VERBOSE=0
SPECIFIC_FILE=""

# Function to show help
show_help() {
    echo "Usage: $0 [options]"
    echo "Migrates test code to use updated API patterns"
    echo ""
    echo "Options:"
    echo "  -h, --help              Show this help message"
    echo "  -t, --test-dir DIR      Test directory to migrate (default: $TEST_DIR)"
    echo "  -b, --backup-dir DIR    Backup directory (default: $BACKUP_DIR)"
    echo "  -d, --dry-run           Show what would be changed without making changes"
    echo "  -v, --verbose           Show detailed output"
    echo "  -f, --file FILE         Process only the specified file"
    echo ""
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        -h|--help)
            show_help
            exit 0
            ;;
        -t|--test-dir)
            TEST_DIR="$2"
            shift
            shift
            ;;
        -b|--backup-dir)
            BACKUP_DIR="$2"
            shift
            shift
            ;;
        -d|--dry-run)
            DRY_RUN=1
            shift
            ;;
        -v|--verbose)
            VERBOSE=1
            shift
            ;;
        -f|--file)
            SPECIFIC_FILE="$2"
            shift
            shift
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            show_help
            exit 1
            ;;
    esac
done

# Backup function
backup_file() {
    local file="$1"
    local relative_path="${file#$TEST_DIR/}"
    local backup_path="$BACKUP_DIR/$relative_path"
    local backup_dir="$(dirname "$backup_path")"
    
    mkdir -p "$backup_dir"
    cp "$file" "$backup_path"
    
    if [ $VERBOSE -eq 1 ]; then
        echo -e "${BLUE}Backed up${NC}: $file -> $backup_path"
    fi
}

# Log function
log_operation() {
    local operation="$1"
    local file="$2"
    local details="$3"
    
    if [ $VERBOSE -eq 1 ]; then
        echo -e "${YELLOW}$operation${NC}: $file ${details:+($details)}"
    fi
}

# Process a single file
process_file() {
    local file="$1"
    local backup_needed=0
    local changes_made=0
    
    # Check if we need to make changes
    if grep -q "import org.s8r.domain.lifecycle" "$file" || \
       grep -q "import org.s8r.domain.identity" "$file" || \
       grep -q "import org.s8r.domain.notification" "$file" || \
       grep -q "LifecycleState" "$file" || \
       grep -q "setValue(" "$file" || \
       grep -q "getValue(" "$file"; then
        backup_needed=1
    else
        # No changes needed
        if [ $VERBOSE -eq 1 ]; then
            echo -e "${GREEN}No changes needed${NC}: $file"
        fi
        return 0
    fi
    
    # Create backup if needed
    if [ $backup_needed -eq 1 ] && [ $DRY_RUN -eq 0 ]; then
        backup_file "$file"
    fi
    
    local tmp_file="$(mktemp)"
    
    # Process imports
    if [ $DRY_RUN -eq 0 ]; then
        # Import changes
        sed -E 's/import org.s8r.domain.lifecycle/import org.s8r.component/g' "$file" > "$tmp_file"
        sed -E -i 's/import org.s8r.domain.identity/import org.s8r.component.identity/g' "$tmp_file"
        sed -E -i 's/import org.s8r.domain.notification/import org.s8r.component.notification/g' "$tmp_file"
        
        # Add compatibility imports if needed
        if grep -q "setValue(" "$file" || grep -q "getValue(" "$file"; then
            # Check if import already exists
            if ! grep -q "import org.s8r.test.util.EnvironmentCompatUtil" "$tmp_file"; then
                # Add import after the last import statement
                sed -i '/^import/a import org.s8r.test.util.EnvironmentCompatUtil;' "$tmp_file"
                log_operation "Added import" "$file" "EnvironmentCompatUtil"
            fi
        fi
        
        if grep -q "LifecycleState" "$file" || grep -q "createAdam" "$file" || grep -q "createChild" "$file"; then
            # Check if import already exists
            if ! grep -q "import org.s8r.test.util.ComponentCompatUtil" "$tmp_file"; then
                # Add import after the last import statement
                sed -i '/^import/a import org.s8r.test.util.ComponentCompatUtil;' "$tmp_file"
                log_operation "Added import" "$file" "ComponentCompatUtil"
            fi
        fi
        
        # Method call changes
        sed -E -i 's/component.getValue\(([^)]+)\)/component.getParameter(\1)/g' "$tmp_file"
        sed -E -i 's/component.setValue\(([^,]+),([^)]+)\)/component.setParameter(\1,\2)/g' "$tmp_file"
        
        # Enum reference changes
        sed -E -i 's/LifecycleState\./State\./g' "$tmp_file"
        
        # Move the updated file back
        mv "$tmp_file" "$file"
        changes_made=1
    else
        # Dry run - just show what would change
        echo -e "${YELLOW}Would update imports in${NC}: $file"
        echo -e "${YELLOW}Would update method calls in${NC}: $file"
    fi
    
    if [ $changes_made -eq 1 ]; then
        echo -e "${GREEN}Updated${NC}: $file"
    fi
}

# Main script logic
echo -e "${BLUE}API Migration Script${NC}"
echo -e "${BLUE}===================${NC}"

# Create backup directory if needed and if not a dry run
if [ $DRY_RUN -eq 0 ]; then
    mkdir -p "$BACKUP_DIR"
    echo -e "${BLUE}Backup directory${NC}: $BACKUP_DIR"
fi

# If specific file is provided, only process that file
if [ ! -z "$SPECIFIC_FILE" ]; then
    if [ -f "$SPECIFIC_FILE" ]; then
        echo -e "${BLUE}Processing file${NC}: $SPECIFIC_FILE"
        process_file "$SPECIFIC_FILE"
    else
        echo -e "${RED}Error: File not found${NC}: $SPECIFIC_FILE"
        exit 1
    fi
else
    # Process all test files
    echo -e "${BLUE}Processing test directory${NC}: $TEST_DIR"
    
    # Count files to process
    file_count=$(find "$TEST_DIR" -name "*.java" -type f | wc -l)
    echo -e "${BLUE}Found${NC} $file_count ${BLUE}Java files${NC}"
    
    # Process each file
    find "$TEST_DIR" -name "*.java" -type f | while read file; do
        process_file "$file"
    done
fi

# Completion message
if [ $DRY_RUN -eq 1 ]; then
    echo -e "${GREEN}Dry run completed. No changes were made.${NC}"
else
    echo -e "${GREEN}Migration completed. Backups stored in${NC}: $BACKUP_DIR"
fi

exit 0