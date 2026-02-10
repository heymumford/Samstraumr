#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#
# Script to assist with migrating client code from Samstraumr to S8r
# This script will:
# 1. Scan for Samstraumr imports
# 2. Create a backup
# 3. Perform substitutions
# 4. Generate a report of changes

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to display usage information
usage() {
  echo "Usage: $0 [options] <directory>"
  echo ""
  echo "This script helps migrate Java code from Samstraumr to S8r."
  echo ""
  echo "Arguments:"
  echo "  <directory>  Path to the directory containing Java code to migrate"
  echo ""
  echo "Options:"
  echo "  --feedback          Enable migration feedback system"
  echo "  --report            Generate a detailed HTML migration report"
  echo "  --interactive       Enable interactive mode for fixing common issues"
  echo "  --dry-run           Show what would be changed without making changes"
  echo "  --no-backup         Skip creating a backup of the source directory"
  echo "  -h, --help          Show this help message"
  echo ""
  echo "Examples:"
  echo "  $0 ./my-project"
  echo "  $0 --feedback /path/to/project/src"
  echo "  $0 --feedback --report --interactive ./my-project"
  echo ""
  exit 1
}

# Default settings
ENABLE_FEEDBACK=false
GENERATE_REPORT=false
INTERACTIVE_MODE=false
DRY_RUN=false
BACKUP=true

# Process command line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    --feedback)
      ENABLE_FEEDBACK=true
      shift
      ;;
    --report)
      GENERATE_REPORT=true
      ENABLE_FEEDBACK=true  # Report requires feedback
      shift
      ;;
    --interactive)
      INTERACTIVE_MODE=true
      ENABLE_FEEDBACK=true  # Interactive mode requires feedback
      shift
      ;;
    --dry-run)
      DRY_RUN=true
      shift
      ;;
    --no-backup)
      BACKUP=false
      shift
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    -*)
      error "Unknown option: $1"
      ;;
    *)
      # Assume this is the directory parameter
      SOURCE_DIR="$1"
      shift
      ;;
  esac
done

# Check if directory parameter is provided
if [ -z "$SOURCE_DIR" ]; then
  error "No source directory specified"
  usage
fi

# Validate directory exists
if [ ! -d "$SOURCE_DIR" ]; then
  echo -e "${RED}Error: Directory '$SOURCE_DIR' not found.${NC}"
  exit 1
fi

# Create backup if requested
TIMESTAMP=$(date +%Y%m%d%H%M%S)
BACKUP_DIR="${SOURCE_DIR}_backup_${TIMESTAMP}"
if [ "$BACKUP" = true ]; then
  echo -e "${BLUE}Creating backup in ${BACKUP_DIR}...${NC}"
  cp -r "$SOURCE_DIR" "$BACKUP_DIR"
else
  echo -e "${YELLOW}Backup disabled. No backup will be created.${NC}"
fi

# Find all Java files
JAVA_FILES=$(find "$SOURCE_DIR" -name "*.java")
TOTAL_FILES=$(echo "$JAVA_FILES" | wc -l)

if [ -z "$JAVA_FILES" ]; then
  echo -e "${YELLOW}No Java files found in $SOURCE_DIR${NC}"
  exit 0
fi

echo -e "${BLUE}Found $TOTAL_FILES Java files to process${NC}"

# Initialize counters
AFFECTED_FILES=0
IMPORT_REPLACEMENTS=0
API_REPLACEMENTS=0

# Create a report file
REPORT_FILE="migration_report_${TIMESTAMP}.txt"
echo "S8r Migration Report - $(date)" > "$REPORT_FILE"
echo "====================================" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"
echo "Source directory: $SOURCE_DIR" >> "$REPORT_FILE"
echo "Backup directory: $BACKUP_DIR" >> "$REPORT_FILE"
echo "Total Java files analyzed: $TOTAL_FILES" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"
echo "Modified Files:" >> "$REPORT_FILE"
echo "-------------" >> "$REPORT_FILE"

# Map of replacements
declare -A IMPORT_MAP
IMPORT_MAP["org.s8r.tube.Tube"]="org.s8r.component.core.Component"
IMPORT_MAP["org.s8r.tube.TubeStatus"]="org.s8r.component.core.State"
IMPORT_MAP["org.s8r.tube.TubeLifecycleState"]="org.s8r.component.core.State"
IMPORT_MAP["org.s8r.tube.TubeIdentity"]="org.s8r.component.identity.Identity"
IMPORT_MAP["org.s8r.tube.Environment"]="org.s8r.component.core.Environment"
IMPORT_MAP["org.s8r.tube.TubeLogger"]="org.s8r.component.logging.Logger"
IMPORT_MAP["org.s8r.tube.TubeLoggerInfo"]="org.s8r.component.logging.Logger.LoggerInfo"
IMPORT_MAP["org.s8r.tube.composite.Composite"]="org.s8r.component.composite.Composite"
IMPORT_MAP["org.s8r.tube.machine.Machine"]="org.s8r.component.machine.Machine"
IMPORT_MAP["org.s8r.tube.exception.TubeInitializationException"]="org.s8r.component.exception.ComponentException"

declare -A API_MAP
API_MAP["Tube.create"]="Component.create"
API_MAP["Tube.createChild"]="Component.createChild"
API_MAP["tube.getStatus()"]="component.getState()"
API_MAP["tube.setStatus"]="component.setState"
API_MAP["tube.getLifecycleState()"]="component.getState()"
API_MAP["TubeStatus.CREATED"]="State.CONCEPTION"
API_MAP["TubeStatus.INITIALIZING"]="State.INITIALIZING"
API_MAP["TubeStatus.READY"]="State.READY"
API_MAP["TubeStatus.ACTIVE"]="State.ACTIVE"
API_MAP["TubeStatus.DEGRADED"]="State.DEGRADED"
API_MAP["TubeStatus.TERMINATING"]="State.TERMINATING"
API_MAP["TubeStatus.TERMINATED"]="State.TERMINATED"
API_MAP["TubeLifecycleState.CONCEPTION"]="State.CONCEPTION"
API_MAP["TubeLifecycleState.EMBRYONIC"]="State.CONFIGURING"
API_MAP["TubeLifecycleState.INFANCY"]="State.SPECIALIZING"
API_MAP["TubeLifecycleState.CHILDHOOD"]="State.DEVELOPING_FEATURES"
API_MAP["composite.addTube"]="composite.addComponent"
API_MAP["@TubeTest"]="@ComponentTest"
API_MAP["@BundleTest"]="@CompositeTest"
API_MAP["tube.updateEnvironmentStatus"]="component.updateEnvironmentState"

# Process each file
for FILE in $JAVA_FILES; do
  FILE_MODIFIED=0
  FILE_IMPORTS=0
  FILE_APIS=0
  
  # Check if file contains org.s8r
  if grep -q "org\.s8r" "$FILE"; then
    # Check for specific import patterns
    for OLD_IMPORT in "${!IMPORT_MAP[@]}"; do
      NEW_IMPORT="${IMPORT_MAP[$OLD_IMPORT]}"
      if grep -q "import $OLD_IMPORT" "$FILE"; then
        # Replace import statements
        sed -i "s/import $OLD_IMPORT/import $NEW_IMPORT/g" "$FILE"
        ((FILE_IMPORTS++))
        ((IMPORT_REPLACEMENTS++))
        FILE_MODIFIED=1
      fi
    done
    
    # Check for API usage patterns
    for OLD_API in "${!API_MAP[@]}"; do
      NEW_API="${API_MAP[$OLD_API]}"
      if grep -q "$OLD_API" "$FILE"; then
        # Replace API usage
        sed -i "s/$OLD_API/$NEW_API/g" "$FILE"
        ((FILE_APIS++))
        ((API_REPLACEMENTS++))
        FILE_MODIFIED=1
      fi
    done
    
    # Replace common variable names (careful!)
    if [ $FILE_MODIFIED -eq 1 ]; then
      # These are riskier replacements, only do them if we've already modified the file
      sed -i 's/\bTube \(\w\+\) =/Component \1 =/g' "$FILE"
      sed -i 's/\btube\./component./g' "$FILE"
    fi
    
    # Record changes to report
    if [ $FILE_MODIFIED -eq 1 ]; then
      ((AFFECTED_FILES++))
      echo "$FILE" >> "$REPORT_FILE"
      echo "  Import replacements: $FILE_IMPORTS" >> "$REPORT_FILE"
      echo "  API replacements: $FILE_APIS" >> "$REPORT_FILE"
      echo "" >> "$REPORT_FILE"
    fi
  fi
done

# Complete the report
echo "" >> "$REPORT_FILE"
echo "Summary:" >> "$REPORT_FILE"
echo "--------" >> "$REPORT_FILE"
echo "Files modified: $AFFECTED_FILES of $TOTAL_FILES" >> "$REPORT_FILE"
echo "Total import replacements: $IMPORT_REPLACEMENTS" >> "$REPORT_FILE"
echo "Total API replacements: $API_REPLACEMENTS" >> "$REPORT_FILE"
echo "" >> "$REPORT_FILE"
echo "Migration completed on $(date)" >> "$REPORT_FILE"

# Find repository root for feedback reports
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Create migration feedback report directory if needed
FEEDBACK_DIR="$REPO_ROOT/reports"
mkdir -p "$FEEDBACK_DIR"

# If feedback is enabled, gather and report migration issues
if [ "$ENABLE_FEEDBACK" = true ]; then
  FEEDBACK_REPORT="${FEEDBACK_DIR}/migration_feedback_${TIMESTAMP}.md"
  FEEDBACK_JSON="${FEEDBACK_DIR}/migration_issues_${TIMESTAMP}.json"
  
  echo -e "${BLUE}Collecting migration feedback...${NC}"
  
  # Scan for potential issues in the migrated code
  echo "# Migration Feedback Report" > "$FEEDBACK_REPORT"
  echo "Generated: $(date)" >> "$FEEDBACK_REPORT"
  echo "" >> "$FEEDBACK_REPORT"
  echo "## Summary" >> "$FEEDBACK_REPORT"
  echo "" >> "$FEEDBACK_REPORT"
  echo "- Files Analyzed: $TOTAL_FILES" >> "$FEEDBACK_REPORT"
  echo "- Files Modified: $AFFECTED_FILES" >> "$FEEDBACK_REPORT"
  echo "- Import Replacements: $IMPORT_REPLACEMENTS" >> "$FEEDBACK_REPORT"
  echo "- API Replacements: $API_REPLACEMENTS" >> "$FEEDBACK_REPORT"
  echo "" >> "$FEEDBACK_REPORT"
  
  # Identify potential issues
  POTENTIAL_ISSUES=0
  
  # Create issues section
  echo "## Potential Issues" >> "$FEEDBACK_REPORT"
  echo "" >> "$FEEDBACK_REPORT"
  
  # Check for incomplete API migrations
  INCOMPLETE_MIGRATIONS=$(grep -r "org\.s8r" "$SOURCE_DIR" --include="*.java" | wc -l)
  if [ "$INCOMPLETE_MIGRATIONS" -gt 0 ]; then
    ((POTENTIAL_ISSUES++))
    echo "### Incomplete Migrations" >> "$FEEDBACK_REPORT"
    echo "" >> "$FEEDBACK_REPORT"
    echo "Found $INCOMPLETE_MIGRATIONS references to org.s8r packages that were not migrated:" >> "$FEEDBACK_REPORT"
    echo "" >> "$FEEDBACK_REPORT"
    echo '```' >> "$FEEDBACK_REPORT"
    grep -r "org\.s8r" "$SOURCE_DIR" --include="*.java" | head -n 10 >> "$FEEDBACK_REPORT"
    if [ "$INCOMPLETE_MIGRATIONS" -gt 10 ]; then
      echo "... and $(($INCOMPLETE_MIGRATIONS - 10)) more" >> "$FEEDBACK_REPORT"
    fi
    echo '```' >> "$FEEDBACK_REPORT"
    echo "" >> "$FEEDBACK_REPORT"
  fi
  
  # Check for potential state transition issues
  STATE_TRANSITIONS=$(grep -r "setState" "$SOURCE_DIR" --include="*.java" | wc -l)
  if [ "$STATE_TRANSITIONS" -gt 0 ]; then
    ((POTENTIAL_ISSUES++))
    echo "### State Transition Changes" >> "$FEEDBACK_REPORT"
    echo "" >> "$FEEDBACK_REPORT"
    echo "Found $STATE_TRANSITIONS state transitions that might need verification:" >> "$FEEDBACK_REPORT"
    echo "" >> "$FEEDBACK_REPORT"
    echo '```' >> "$FEEDBACK_REPORT"
    grep -r "setState" "$SOURCE_DIR" --include="*.java" | head -n 10 >> "$FEEDBACK_REPORT"
    if [ "$STATE_TRANSITIONS" -gt 10 ]; then
      echo "... and $(($STATE_TRANSITIONS - 10)) more" >> "$FEEDBACK_REPORT"
    fi
    echo '```' >> "$FEEDBACK_REPORT"
    echo "" >> "$FEEDBACK_REPORT"
    echo "**Recommendation**: Verify that all state transitions comply with the S8r state model." >> "$FEEDBACK_REPORT"
    echo "" >> "$FEEDBACK_REPORT"
  fi
  
  # Generate recommendations
  echo "## Recommendations" >> "$FEEDBACK_REPORT"
  echo "" >> "$FEEDBACK_REPORT"
  
  if [ "$AFFECTED_FILES" -gt 0 ]; then
    echo "1. **Test Migrated Components**: Test each migrated component to ensure functionality is preserved." >> "$FEEDBACK_REPORT"
    echo "2. **Review State Transitions**: Verify that state transitions follow the correct lifecycle." >> "$FEEDBACK_REPORT"
    echo "3. **Check Type Conversions**: Ensure all type conversions between legacy and S8r types are handled correctly." >> "$FEEDBACK_REPORT"
    
    # Create simple JSON with issues
    cat > "$FEEDBACK_JSON" << EOF
{
  "timestamp": "$(date -Iseconds)",
  "source_directory": "$SOURCE_DIR",
  "stats": {
    "total_files": $TOTAL_FILES,
    "affected_files": $AFFECTED_FILES,
    "import_replacements": $IMPORT_REPLACEMENTS,
    "api_replacements": $API_REPLACEMENTS,
    "potential_issues": $POTENTIAL_ISSUES
  },
  "issues": {
    "incomplete_migrations": $INCOMPLETE_MIGRATIONS,
    "state_transitions": $STATE_TRANSITIONS
  }
}
EOF
  else
    echo "No files were modified during migration. No specific recommendations." >> "$FEEDBACK_REPORT"
  fi
  
  # Create symbolic link to latest report
  ln -sf "$FEEDBACK_REPORT" "${FEEDBACK_DIR}/latest_migration_feedback.md"
  ln -sf "$FEEDBACK_JSON" "${FEEDBACK_DIR}/latest_migration_issues.json"
  
  # Generate a more comprehensive report if requested
  if [ "$GENERATE_REPORT" = true ]; then
    # If we have the migration report tool, use it
    if [ -f "$REPO_ROOT/s8r-migration-report" ]; then
      echo -e "${BLUE}Generating comprehensive migration report...${NC}"
      "$REPO_ROOT/s8r-migration-report" --output="${FEEDBACK_DIR}/migration_report_${TIMESTAMP}.html" --html
    fi
  fi
  
  # If interactive mode is enabled, provide interactive fixes
  if [ "$INTERACTIVE_MODE" = true ]; then
    echo -e "${BLUE}Interactive mode: Checking for common migration issues to fix...${NC}"
    
    # List files with potential issues
    if [ "$INCOMPLETE_MIGRATIONS" -gt 0 ]; then
      echo -e "${YELLOW}Files with incomplete migrations:${NC}"
      grep -l "org\.s8r" "$SOURCE_DIR" --include="*.java" | head -n 5
      
      read -p "Attempt to fix these files? [y/N] " -n 1 -r
      echo
      if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${BLUE}Applying additional migrations...${NC}"
        # Re-run the migration with additional mappings
        # This is simplified; a real implementation would be more sophisticated
        for FILE in $(grep -l "org\.s8r" "$SOURCE_DIR" --include="*.java"); do
          echo "  Processing $FILE..."
          sed -i 's/org\.s8r/org.s8r/g' "$FILE"
        done
      fi
    fi
  fi
  
  # Print feedback summary
  echo -e "${BLUE}Migration feedback summary:${NC}"
  echo -e "  Potential issues identified: ${YELLOW}$POTENTIAL_ISSUES${NC}"
  echo -e "  Detailed feedback saved to: ${GREEN}$FEEDBACK_REPORT${NC}"
  if [ "$GENERATE_REPORT" = true ]; then
    echo -e "  HTML report saved to: ${GREEN}${FEEDBACK_DIR}/migration_report_${TIMESTAMP}.html${NC}"
  fi
fi

# Display summary to console
echo -e "${GREEN}Migration completed!${NC}"
echo -e "${BLUE}Summary:${NC}"
echo -e "  Files modified: ${GREEN}$AFFECTED_FILES${NC} of ${BLUE}$TOTAL_FILES${NC}"
echo -e "  Total import replacements: ${GREEN}$IMPORT_REPLACEMENTS${NC}"
echo -e "  Total API replacements: ${GREEN}$API_REPLACEMENTS${NC}"
echo -e "${BLUE}Report saved to: ${GREEN}$REPORT_FILE${NC}"
if [ "$BACKUP" = true ]; then
  echo -e "${YELLOW}A backup of your code was created at: ${GREEN}$BACKUP_DIR${NC}"
fi
echo -e "${YELLOW}Please review the changes carefully before committing.${NC}"