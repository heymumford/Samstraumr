#!/bin/bash

# Function to convert string to lowercase
to_lowercase() {
    echo "$1" | tr '[:upper:]' '[:lower:]'
}

# Array of languages
languages=("Java" "Python" "C++" "Go" "Rust" "Scala" "Javascript")

# Create base directories
mkdir -p "docs/concepts"
mkdir -p "docs/guides"
mkdir -p "docs/reference"
mkdir -p "framework/core"
mkdir -p "framework/extensions"
mkdir -p "framework/templates"
mkdir -p "lib/karate"
mkdir -p "lib/TBD"
mkdir -p "scripts"

# Create language-specific examples
for lang in "${languages[@]}"; do
  lang_dir=$(to_lowercase "$lang")
  mkdir -p "examples/${lang_dir}/example-1"
  touch "examples/${lang_dir}/example-1/main.${lang_dir}"
  touch "examples/${lang_dir}/example-1/test.${lang_dir}"
done

# Create placeholder README.md files
echo "# Samstraumr Framework" > "README.md"
echo "# Tube-Based Design Concepts" > "docs/concepts/tube-based-design.md"
echo "# Getting Started Guide" > "docs/guides/getting-started.md"
echo "# API Reference" > "docs/reference/api-reference.md"
echo "# Core Tube Implementation" > "framework/core/Tube.java"
echo "# Karate Integration Module" > "framework/extensions/KarateIntegration.java"
echo "# Tube Template" > "framework/templates/tube-template.xml"
echo "# Connector Template" > "framework/templates/connector-template.json"

# Create placeholder scripts
echo "#!/bin/bash" > "scripts/setup-karate.sh"
echo "#!/bin/bash" > "scripts/init-tbd.sh"

echo "Directory structure created successfully!"

