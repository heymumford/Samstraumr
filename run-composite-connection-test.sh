#!/bin/bash
# Script to compile and run the manual composite connection test without JUnit

# Set colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Compiling and running manual composite connection test${NC}"

# Create necessary directories
CLASSES_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/target/test-classes"
mkdir -p $CLASSES_DIR
mkdir -p $CLASSES_DIR/isolated

# Clean up potentially conflicting files
find $CLASSES_DIR -name "ConnectionCycleException*.class" -delete
find $CLASSES_DIR -name "CompositeConnectionValidator*.class" -delete
find $CLASSES_DIR -name "ManualCompositeConnectionTest*.class" -delete

# Source directory
SRC_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/main/java"
TEST_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java"

# Compile identity classes first
echo -e "${GREEN}Compiling identity classes...${NC}"
javac -d $CLASSES_DIR $SRC_DIR/org/s8r/domain/identity/ComponentId.java

# Compile exception classes
echo -e "${GREEN}Compiling exception classes...${NC}"
javac -d $CLASSES_DIR -cp $CLASSES_DIR $SRC_DIR/org/s8r/domain/exception/ComponentException.java
javac -d $CLASSES_DIR -cp $CLASSES_DIR $SRC_DIR/org/s8r/domain/exception/NonExistentComponentReferenceException.java
javac -d $CLASSES_DIR -cp $CLASSES_DIR $SRC_DIR/org/s8r/domain/exception/ConnectionCycleException.java

# Compile connection classes
echo -e "${GREEN}Compiling connection classes...${NC}"
javac -d $CLASSES_DIR -cp $CLASSES_DIR $SRC_DIR/org/s8r/domain/component/composite/ConnectionType.java
javac -d $CLASSES_DIR -cp $CLASSES_DIR $SRC_DIR/org/s8r/domain/component/composite/ComponentConnection.java

# Compile validator
echo -e "${GREEN}Compiling validator...${NC}"
javac -d $CLASSES_DIR -cp $CLASSES_DIR $SRC_DIR/org/s8r/domain/validation/CompositeConnectionValidator.java

# Compile the test class
echo -e "${GREEN}Compiling test class...${NC}"
javac -d $CLASSES_DIR -cp $CLASSES_DIR $TEST_DIR/isolated/ManualCompositeConnectionTest.java

if [ $? -ne 0 ]; then
  echo -e "${RED}Compilation failed!${NC}"
  exit 1
fi

# Run the test
echo -e "${GREEN}Running test...${NC}"
java -cp $CLASSES_DIR isolated.ManualCompositeConnectionTest

echo -e "${YELLOW}Test complete.${NC}"