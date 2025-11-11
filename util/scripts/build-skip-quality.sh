#!/bin/bash

# Setup environment
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Create custom pom.xmls with removed PMD plugin
mkdir -p /tmp/build-override
cp modules/samstraumr-core/pom.xml /tmp/build-override/samstraumr-core-pom.xml

# Use special skip profile
MAVEN_ARGS=""
MAVEN_ARGS="$MAVEN_ARGS -Dmaven.test.skip=false"
MAVEN_ARGS="$MAVEN_ARGS -Dpmd.skip=true"
MAVEN_ARGS="$MAVEN_ARGS -Dcheckstyle.skip=true"
MAVEN_ARGS="$MAVEN_ARGS -Dspotbugs.skip=true"
MAVEN_ARGS="$MAVEN_ARGS -Djacoco.skip=true"
MAVEN_ARGS="$MAVEN_ARGS -Dspotless.check.skip=true"
MAVEN_ARGS="$MAVEN_ARGS -Dskip-all-plugins=true"
MAVEN_ARGS="$MAVEN_ARGS -P skip-quality-checks"

# Show Java and Maven info
echo "=== Build Environment ==="
echo "Java version: $(java -version 2>&1 | head -n 1)"
echo "Maven version: $(mvn -version 2>&1 | head -n 1)"
echo "=======================" 

# Execute Maven directly on the core module first - skipping quality checks
cd modules/samstraumr-core
echo "Building core module directly..."
mvn clean compile test-compile $MAVEN_ARGS

# Go back to project root
cd ../..
echo "Building complete project..."
mvn $MAVEN_ARGS "$@"