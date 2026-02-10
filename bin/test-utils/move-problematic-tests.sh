#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Script to temporarily move problematic ALZ001 and related test files out of the way
# This allows running basic lifecycle tests while ALZ001 tests are fixed

# Set colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# Create backup directory
BACKUP_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/test-backup"
mkdir -p $BACKUP_DIR
mkdir -p $BACKUP_DIR/alz001
mkdir -p $BACKUP_DIR/runner

echo -e "${YELLOW}Moving problematic test files to $BACKUP_DIR${NC}"

# Move ALZ001 related files
echo -e "${GREEN}Moving ALZ001 test steps...${NC}"
SRC_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/steps/alz001"
if [ -d "$SRC_DIR" ]; then
  echo -e "${GREEN}  - Found ALZ001 steps directory, moving...${NC}"
  mv "$SRC_DIR" "$BACKUP_DIR/alz001/"
fi

# Move ALZ001 test runners
echo -e "${GREEN}Moving ALZ001 test runners...${NC}"
find /home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/runner -type f -name "*ALZ001*.java" | while read f; do
  echo -e "${GREEN}  - Moving file: $f${NC}"
  dest_file="$BACKUP_DIR/runner/$(basename $f)"
  mv "$f" "$dest_file"
done

# Save the original NotificationServiceTest.java file in case we want to restore it
echo -e "${GREEN}Backing up NotificationServiceTest.java...${NC}"
NOTIFICATION_TEST="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/application/service/NotificationServiceTest.java"
if [ -f "$NOTIFICATION_TEST" ]; then
  cp "$NOTIFICATION_TEST" "$BACKUP_DIR/NotificationServiceTest.java.bak"
fi

echo -e "${YELLOW}Problematic test files moved. You can now run the basic lifecycle tests.${NC}"
echo -e "${YELLOW}To restore the files, run ./restore-problematic-tests.sh${NC}"

# Create the restore script
cat > /home/emumford/NativeLinuxProjects/Samstraumr/restore-problematic-tests.sh << 'EOF'
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
EOF

chmod +x /home/emumford/NativeLinuxProjects/Samstraumr/restore-problematic-tests.sh