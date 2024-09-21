#!/bin/bash

# Base directory for the current project
PROJECT_DIR="/Users/vorthruna/Projects/Samstraumr/examples/samstraumr-causality-engine"

# Create the new directory structure
echo "Creating new directory structure..."
mkdir -p "$PROJECT_DIR/app/java/src/main/java/com/samstraumr/tubes"
mkdir -p "$PROJECT_DIR/app/java/src/main/java/com/samstraumr/utils"
mkdir -p "$PROJECT_DIR/app/python/src/com/samstraumr/utils"
mkdir -p "$PROJECT_DIR/app/r/src/com/samstraumr/utils"
mkdir -p "$PROJECT_DIR/test/java/com/samstraumr/tubes"
mkdir -p "$PROJECT_DIR/test/python"
mkdir -p "$PROJECT_DIR/test/r"
mkdir -p "$PROJECT_DIR/docs"

# Move files into the new structure, but only if they exist
echo "Moving files..."

# Move Java code if it exists
if [ -d "$PROJECT_DIR/src/main/java/com/samstraumr/tubes" ]; then
  mv "$PROJECT_DIR/src/main/java/com/samstraumr/tubes/"* "$PROJECT_DIR/app/java/src/main/java/com/samstraumr/tubes/"
fi

if [ -d "$PROJECT_DIR/src/main/java/com/samstraumr/utils" ]; then
  mv "$PROJECT_DIR/src/main/java/com/samstraumr/utils/"* "$PROJECT_DIR/app/java/src/main/java/com/samstraumr/utils/"
fi

# Move Python code if it exists
if [ -d "$PROJECT_DIR/src/python/utils" ]; then
  mv "$PROJECT_DIR/src/python/utils/"* "$PROJECT_DIR/app/python/src/com/samstraumr/utils/"
fi

# Move R code if it exists
if [ -d "$PROJECT_DIR/src/r/utils" ]; then
  mv "$PROJECT_DIR/src/r/utils/"* "$PROJECT_DIR/app/r/src/com/samstraumr/utils/"
fi

# Move tests if they exist
if [ -d "$PROJECT_DIR/test/java/com/samstraumr/tubes" ]; then
  mv "$PROJECT_DIR/test/java/com/samstraumr/tubes/"* "$PROJECT_DIR/test/java/com/samstraumr/tubes/"
fi

if [ -d "$PROJECT_DIR/test/python" ]; then
  mv "$PROJECT_DIR/test/python/"* "$PROJECT_DIR/test/python/"
fi

if [ -d "$PROJECT_DIR/test/r" ]; then
  mv "$PROJECT_DIR/test/r/"* "$PROJECT_DIR/test/r/"
fi

# Move Dockerfile, LICENSE, README.md, and pom.xml to the /app folder
if [ -f "$PROJECT_DIR/Dockerfile" ]; then
  mv "$PROJECT_DIR/Dockerfile" "$PROJECT_DIR/app/"
fi

if [ -f "$PROJECT_DIR/LICENSE" ]; then
  mv "$PROJECT_DIR/LICENSE" "$PROJECT_DIR/app/"
fi

if [ -f "$PROJECT_DIR/README.md" ]; then
  mv "$PROJECT_DIR/README.md" "$PROJECT_DIR/app/"
fi

if [ -f "$PROJECT_DIR/pom.xml" ]; then
  mv "$PROJECT_DIR/pom.xml" "$PROJECT_DIR/app/"
fi

# Create placeholder docs if they don't exist
echo "Creating docs..."
touch "$PROJECT_DIR/docs/INSTALLATION.md"
touch "$PROJECT_DIR/docs/API.md"

echo "Project reorganization complete."

