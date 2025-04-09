#!/bin/bash
# Script to restore problematic test files

# Set colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# Define backup directory
BACKUP_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/test-backup"

echo -e "${YELLOW}Restoring problematic test files from $BACKUP_DIR${NC}"

# Restore ALZ001 test steps
echo -e "${GREEN}Restoring ALZ001 test steps...${NC}"
SRC_DIR="$BACKUP_DIR/alz001/alz001"
DEST_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/steps"
if [ -d "$SRC_DIR" ]; then
  mkdir -p "$DEST_DIR"
  mv "$SRC_DIR" "$DEST_DIR/"
  echo -e "${GREEN}  - ALZ001 steps restored${NC}"
fi

# Restore ALZ001 test runners
echo -e "${GREEN}Restoring ALZ001 test runners...${NC}"
RUNNER_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/runner"
mkdir -p "$RUNNER_DIR"
find "$BACKUP_DIR/runner" -type f -name "*.java" | while read f; do
  dest_file="$RUNNER_DIR/$(basename $f)"
  mv "$f" "$dest_file"
  echo -e "${GREEN}  - Restored file: $dest_file${NC}"
done

echo -e "${YELLOW}Test files restored.${NC}"
