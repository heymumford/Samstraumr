#!/bin/bash
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
  echo "Usage: $0 <directory>"
  echo ""
  echo "This script helps migrate Java code from Samstraumr to S8r."
  echo ""
  echo "Arguments:"
  echo "  <directory>  Path to the directory containing Java code to migrate"
  echo ""
  echo "Examples:"
  echo "  $0 ./my-project"
  echo "  $0 /path/to/project/src"
  echo ""
  exit 1
}

# Check if directory parameter is provided
if [ $# -ne 1 ]; then
  usage
fi

SOURCE_DIR="$1"

# Validate directory exists
if [ ! -d "$SOURCE_DIR" ]; then
  echo -e "${RED}Error: Directory '$SOURCE_DIR' not found.${NC}"
  exit 1
fi

# Create backup
TIMESTAMP=$(date +%Y%m%d%H%M%S)
BACKUP_DIR="${SOURCE_DIR}_backup_${TIMESTAMP}"
echo -e "${BLUE}Creating backup in ${BACKUP_DIR}...${NC}"
cp -r "$SOURCE_DIR" "$BACKUP_DIR"

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
IMPORT_MAP["org.samstraumr.tube.Tube"]="org.s8r.component.core.Component"
IMPORT_MAP["org.samstraumr.tube.TubeStatus"]="org.s8r.component.core.State"
IMPORT_MAP["org.samstraumr.tube.TubeLifecycleState"]="org.s8r.component.core.State"
IMPORT_MAP["org.samstraumr.tube.TubeIdentity"]="org.s8r.component.identity.Identity"
IMPORT_MAP["org.samstraumr.tube.Environment"]="org.s8r.component.core.Environment"
IMPORT_MAP["org.samstraumr.tube.TubeLogger"]="org.s8r.component.logging.Logger"
IMPORT_MAP["org.samstraumr.tube.TubeLoggerInfo"]="org.s8r.component.logging.Logger.LoggerInfo"
IMPORT_MAP["org.samstraumr.tube.composite.Composite"]="org.s8r.component.composite.Composite"
IMPORT_MAP["org.samstraumr.tube.machine.Machine"]="org.s8r.component.machine.Machine"
IMPORT_MAP["org.samstraumr.tube.exception.TubeInitializationException"]="org.s8r.component.exception.ComponentException"

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
  
  # Check if file contains org.samstraumr
  if grep -q "org\.samstraumr" "$FILE"; then
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

# Display summary to console
echo -e "${GREEN}Migration completed!${NC}"
echo -e "${BLUE}Summary:${NC}"
echo -e "  Files modified: ${GREEN}$AFFECTED_FILES${NC} of ${BLUE}$TOTAL_FILES${NC}"
echo -e "  Total import replacements: ${GREEN}$IMPORT_REPLACEMENTS${NC}"
echo -e "  Total API replacements: ${GREEN}$API_REPLACEMENTS${NC}"
echo -e "${BLUE}Report saved to: ${GREEN}$REPORT_FILE${NC}"
echo -e "${YELLOW}A backup of your code was created at: ${GREEN}$BACKUP_DIR${NC}"
echo -e "${YELLOW}Please review the changes carefully before committing.${NC}"