#!/bin/bash

# Define root directory for the new version
ROOT_DIR="Samstraumr-v0.2"

echo "Creating root directory: $ROOT_DIR"
mkdir -p $ROOT_DIR

# Create core framework structure
echo "Creating framework structure..."
mkdir -p $ROOT_DIR/framework/core
mkdir -p $ROOT_DIR/framework/extensions
mkdir -p $ROOT_DIR/framework/templates
mkdir -p $ROOT_DIR/framework/tests/core

# Create application structure
echo "Creating app structure..."
mkdir -p $ROOT_DIR/app/java/src/main/java/com/samstraumr/tubes
mkdir -p $ROOT_DIR/app/java/src/main/java/com/samstraumr/utils
mkdir -p $ROOT_DIR/app/java/src/main/java/com/samstraumr/integration
mkdir -p $ROOT_DIR/app/java/src/test/java/com/samstraumr/tubes
mkdir -p $ROOT_DIR/app/python/src/com/samstraumr/utils
mkdir -p $ROOT_DIR/app/python/tests
mkdir -p $ROOT_DIR/app/r/src/com/samstraumr/utils
mkdir -p $ROOT_DIR/app/r/tests

# Create documentation folder
echo "Creating documentation..."
mkdir -p $ROOT_DIR/docs
mkdir -p $ROOT_DIR/docs/concepts
mkdir -p $ROOT_DIR/docs/guides
mkdir -p $ROOT_DIR/docs/reference

# Create example folder
echo "Creating examples structure..."
mkdir -p $ROOT_DIR/examples/java/example-1
mkdir -p $ROOT_DIR/examples/python/example-1
mkdir -p $ROOT_DIR/examples/r/example-1

# Create image folder
echo "Creating images folder..."
mkdir -p $ROOT_DIR/images

# Create script folder
echo "Creating scripts folder..."
mkdir -p $ROOT_DIR/scripts

# Create empty files
echo "Generating initial files..."
touch $ROOT_DIR/README.md
touch $ROOT_DIR/LICENSE
touch $ROOT_DIR/framework/core/Tube.java
touch $ROOT_DIR/framework/core/TubeInterface.java
touch $ROOT_DIR/framework/core/TubeRegistry.java
touch $ROOT_DIR/framework/extensions/KafkaIntegration.java
touch $ROOT_DIR/framework/templates/connector-template.json
touch $ROOT_DIR/framework/templates/tube-template.xml
touch $ROOT_DIR/framework/tests/core/TubeTest.java
touch $ROOT_DIR/app/Dockerfile
touch $ROOT_DIR/app/README.md
touch $ROOT_DIR/app/java/src/main/java/com/samstraumr/tubes/YggdrasilBase.java
touch $ROOT_DIR/app/java/src/main/java/com/samstraumr/tubes/andromeda.java
touch $ROOT_DIR/app/java/src/main/java/com/samstraumr/tubes/calypso.java
touch $ROOT_DIR/app/java/src/main/java/com/samstraumr/tubes/cassandra.java
touch $ROOT_DIR/app/java/src/main/java/com/samstraumr/tubes/maia.java
touch $ROOT_DIR/app/java/src/main/java/com/samstraumr/tubes/phoebe.java
touch $ROOT_DIR/app/java/src/main/java/com/samstraumr/tubes/serena.java
touch $ROOT_DIR/app/java/src/main/java/com/samstraumr/tubes/yggdrasil.java
touch $ROOT_DIR/app/java/src/main/java/com/samstraumr/integration/IntegrationTest.java
touch $ROOT_DIR/app/python/src/com/samstraumr/utils/utils.py
touch $ROOT_DIR/app/r/src/com/samstraumr/utils/utils.r
touch $ROOT_DIR/docs/API.md
touch $ROOT_DIR/docs/INSTALLATION.md
touch $ROOT_DIR/docs/concepts/basic-structures.md
touch $ROOT_DIR/docs/concepts/core-philosophy.md
touch $ROOT_DIR/docs/concepts/what-samstraumr-is-not.md
touch $ROOT_DIR/docs/guides/tube-based-coding-principles.md
touch $ROOT_DIR/docs/reference/references.md

# Done
echo "Samstraumr v0.2 folder structure successfully created!"

