#!/bin/bash
#
# Script to check Maven dependency versions against the latest available
# Uses temporary files in the target directory which is cleaned by Maven

set -e

# Create target directory if it doesn't exist
mkdir -p target/version-check

# Create a minimal pom.xml for checking versions
cat > target/version-check/pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.samstraumr</groupId>
    <artifactId>version-check</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <parent>
        <groupId>org.samstraumr</groupId>
        <artifactId>samstraumr</artifactId>
        <version>2.0.0</version>
        <relativePath>../../pom.xml</relativePath>
    </parent>

    <name>Version Check</name>
    <description>Project to check for dependency updates</description>
</project>
EOF

echo "Checking for dependency updates..."
cd target/version-check
echo -e "\n\n=== DEPENDENCY UPDATES ==="
mvn versions:display-dependency-updates -q
echo -e "\n\n=== PLUGIN UPDATES ==="
mvn versions:display-plugin-updates -q
echo -e "\n\n=== PROPERTY UPDATES ==="
mvn versions:display-property-updates -q
cd ../..

echo "Dependency check completed."