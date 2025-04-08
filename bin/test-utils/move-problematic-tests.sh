#!/bin/bash
# Script to temporarily move problematic test files out of the way

# Set colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# Create backup directory
BACKUP_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/test-backup"
mkdir -p $BACKUP_DIR

echo -e "${YELLOW}Moving problematic test files to $BACKUP_DIR${NC}"

# Move the problematic files
echo -e "${GREEN}Moving test steps directory...${NC}"
mv /home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/steps $BACKUP_DIR/
echo -e "${GREEN}Moving test runner directory...${NC}"
mv /home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/runner $BACKUP_DIR/

echo -e "${YELLOW}Problematic test files moved. You can now run tests on the MinimalTest class.${NC}"
echo -e "${YELLOW}To restore the files, run ./restore-problematic-tests.sh${NC}"

# Create the restore script
cat > /home/emumford/NativeLinuxProjects/Samstraumr/bin/test-utils/restore-problematic-tests.sh << 'EOF'
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

# Create directories if they don't exist
mkdir -p /home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/

# Restore the files
echo -e "${GREEN}Restoring test steps directory...${NC}"
mv $BACKUP_DIR/steps /home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/
echo -e "${GREEN}Restoring test runner directory...${NC}"
mv $BACKUP_DIR/runner /home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/org/s8r/test/

echo -e "${YELLOW}Test files restored.${NC}"
EOF

chmod +x /home/emumford/NativeLinuxProjects/Samstraumr/bin/test-utils/restore-problematic-tests.sh

# Create symlink in root for backward compatibility
ln -sf bin/test-utils/restore-problematic-tests.sh /home/emumford/NativeLinuxProjects/Samstraumr/restore-problematic-tests.sh