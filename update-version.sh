#!/bin/bash
# Script to update version numbers throughout the codebase
# Usage: ./update-version.sh [new-version]

set -e

# Function to print colored output
print_header() {
  echo -e "\033[1;34m===== $1 =====\033[0m"
}

print_success() {
  echo -e "\033[1;32m✓ $1\033[0m"
}

print_warning() {
  echo -e "\033[1;33m! $1\033[0m"
}

# Check if new version is provided
if [ -z "$1" ]; then
  echo "Usage: ./update-version.sh [new-version]"
  echo "Example: ./update-version.sh 0.5.0"
  exit 1
fi

NEW_VERSION="$1"
VERSION_PROPERTIES_FILE="Samstraumr/version.properties"

print_header "Updating Version to $NEW_VERSION"

# Read current version
CURRENT_VERSION=$(grep "samstraumr.version=" $VERSION_PROPERTIES_FILE | cut -d= -f2)
echo "Current version: $CURRENT_VERSION"
echo "New version: $NEW_VERSION"

# Update version.properties
sed -i "s/samstraumr.version=.*/samstraumr.version=$NEW_VERSION/" $VERSION_PROPERTIES_FILE
print_success "Updated version.properties"

# Update last updated date
TODAY=$(date +"%B %d, %Y")
sed -i "s/samstraumr.last.updated=.*/samstraumr.last.updated=$TODAY/" $VERSION_PROPERTIES_FILE
print_success "Updated last updated date to $TODAY"

# Update POM files
sed -i "s/<version>$CURRENT_VERSION<\/version>/<version>$NEW_VERSION<\/version>/g" pom.xml
sed -i "s/<version>$CURRENT_VERSION<\/version>/<version>$NEW_VERSION<\/version>/g" Samstraumr/pom.xml
sed -i "s/<version>$CURRENT_VERSION<\/version>/<version>$NEW_VERSION<\/version>/g" Samstraumr/samstraumr-core/pom.xml

# Update version property in pom.xml files
sed -i "s/<samstraumr.version>$CURRENT_VERSION<\/samstraumr.version>/<samstraumr.version>$NEW_VERSION<\/samstraumr.version>/g" pom.xml
sed -i "s/<samstraumr.version>$CURRENT_VERSION<\/samstraumr.version>/<samstraumr.version>$NEW_VERSION<\/samstraumr.version>/g" Samstraumr/pom.xml

print_success "Updated POM files"

# Update README.md version
sed -i "s/Version: [0-9.]\+/Version: $NEW_VERSION/g" README.md
print_success "Updated README.md"

print_header "Version Update Complete"
echo "Build the project to apply version changes to all resources"
echo "Run: mvn clean install"