#!/bin/bash
# Script to compile and run the isolated test directly without Maven

# Set colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Compiling and running isolated test${NC}"

# Clean up test-classes directory
CLASSES_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/target/test-classes"
mkdir -p $CLASSES_DIR

# Get Maven classpath
MAVEN_CLASSPATH=$(mvn -q dependency:build-classpath)

# Add necessary JUnit dependencies 
echo -e "${GREEN}Compiling isolated test...${NC}"
javac -d $CLASSES_DIR -cp "$CLASSES_DIR:$MAVEN_CLASSPATH" \
  /home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/isolated/IsolatedTest.java

if [ $? -ne 0 ]; then
  echo -e "${RED}Compilation failed!${NC}"
  exit 1
fi

echo -e "${GREEN}Running isolated test...${NC}"
java -cp "$CLASSES_DIR:$MAVEN_CLASSPATH" org.junit.platform.console.ConsoleLauncher --select-class=isolated.IsolatedTest

echo -e "${YELLOW}Test complete.${NC}"