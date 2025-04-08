#!/bin/bash
# Script to restore problematic test files

# Set colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# Define backup directory
BACKUP_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/test-backup"

# Define script directory for better relative paths
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo -e "${YELLOW}Restoring problematic test files from $BACKUP_DIR${NC}"

# Create directories if they don't exist
mkdir -p /home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/

# Restore the files
echo -e "${GREEN}Restoring test steps directory...${NC}"
mv $BACKUP_DIR/steps /home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/
echo -e "${GREEN}Restoring test runner directory...${NC}"
mv $BACKUP_DIR/runner /home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/

echo -e "${YELLOW}Test files restored.${NC}"
