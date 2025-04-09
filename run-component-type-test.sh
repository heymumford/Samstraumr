#!/bin/bash
# Script to compile and run the manual component type test without JUnit

# Set colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Compiling and running manual component type test${NC}"

# Clean up test-classes directory
CLASSES_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/target/test-classes"
mkdir -p $CLASSES_DIR

# Create isolated directory if needed
mkdir -p $CLASSES_DIR/isolated

# Clean up potential previous build artifacts
rm -rf $CLASSES_DIR/org/s8r/domain/component/ComponentType.class
rm -rf $CLASSES_DIR/org/s8r/domain/exception/InvalidComponentTypeException.class
rm -rf $CLASSES_DIR/org/s8r/domain/validation/ComponentTypeValidator.class
rm -rf $CLASSES_DIR/isolated/ManualComponentTypeTest.class

# Compile domain identity classes first
SRC_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/main/java"
echo -e "${GREEN}Compiling ComponentId class...${NC}"
javac -d $CLASSES_DIR $SRC_DIR/org/s8r/domain/identity/ComponentId.java 

# Compile exception classes 
echo -e "${GREEN}Compiling exception classes...${NC}"
javac -d $CLASSES_DIR -cp $CLASSES_DIR $SRC_DIR/org/s8r/domain/exception/ComponentException.java
javac -d $CLASSES_DIR -cp $CLASSES_DIR $SRC_DIR/org/s8r/domain/exception/InvalidComponentTypeException.java

# Compile domain component classes
echo -e "${GREEN}Compiling ComponentType enum...${NC}"
javac -d $CLASSES_DIR -cp $CLASSES_DIR $SRC_DIR/org/s8r/domain/component/ComponentType.java

# Compile validator class
echo -e "${GREEN}Compiling ComponentTypeValidator class...${NC}"
javac -d $CLASSES_DIR -cp $CLASSES_DIR $SRC_DIR/org/s8r/domain/validation/ComponentTypeValidator.java

# Compile and run the manual test
TEST_FILE="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/isolated/ManualComponentTypeTest.java"

echo -e "${GREEN}Compiling manual test...${NC}"
javac -d $CLASSES_DIR -cp $CLASSES_DIR $TEST_FILE

if [ $? -ne 0 ]; then
  echo -e "${RED}Compilation of manual test failed!${NC}"
  exit 1
fi

echo -e "${GREEN}Running manual test...${NC}"
java -cp $CLASSES_DIR isolated.ManualComponentTypeTest

echo -e "${YELLOW}Test complete.${NC}"