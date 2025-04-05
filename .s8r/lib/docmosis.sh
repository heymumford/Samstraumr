#!/usr/bin/env bash
#==============================================================================
# S8r Docmosis Library Functions
# Shared code for Docmosis integration
#==============================================================================

# Find repository root
PROJECT_ROOT="${PROJECT_ROOT:-$(git rev-parse --show-toplevel)}"

# Source common library if available and not already sourced
if [ -f "${PROJECT_ROOT}/.s8r/lib/common.sh" ] && ! type -t info > /dev/null; then
  source "${PROJECT_ROOT}/.s8r/lib/common.sh"
fi

# Docmosis functions will use these prefixed versions to avoid conflicts
function docmosis_info() { echo -e "${BLUE:-\033[0;34m}$1${RESET:-\033[0m}"; }
function docmosis_success() { echo -e "${GREEN:-\033[0;32m}$1${RESET:-\033[0m}"; }
function docmosis_error() { echo -e "${RED:-\033[0;31m}Error: $1${RESET:-\033[0m}" >&2; return 1; }
function docmosis_warn() { echo -e "${YELLOW:-\033[0;33m}Warning: $1${RESET:-\033[0m}" >&2; }

# Configuration constants
DOCMOSIS_VERSION="4.8.0"
BARCODE4J_VERSION="2.1"
USER_CONFIG_DIR="${HOME}/.s8r"
USER_CONFIG_FILE="${USER_CONFIG_DIR}/config.json"
PROJECT_DOCMOSIS_CONFIG="${PROJECT_ROOT}/.s8r/config/docmosis.properties"
SYSTEM_DOCMOSIS_DIR="/etc/s8r"
SYSTEM_DOCMOSIS_CONFIG="${SYSTEM_DOCMOSIS_DIR}/docmosis.properties"

# Check if Docmosis is available (returns 0 if available, 1 if not)
is_docmosis_available() {
  # If DOCMOSIS_KEY is explicitly empty, return unavailable
  if [ -z "${DOCMOSIS_KEY}" ] && [ "${DOCMOSIS_KEY}" = "" ]; then
    return 1
  fi
  
  # First check if configuration exists with non-empty key
  if [ -n "${DOCMOSIS_KEY}" ]; then
    return 0
  fi
  
  # Try loading the configuration
  if load_docmosis_config &>/dev/null && [ -n "${DOCMOSIS_KEY}" ]; then
    return 0
  fi
  
  # Check if we're in a CI environment where docmosis might be deliberately disabled
  if [ -n "${CI}" ] || [ -n "${GITHUB_ACTIONS}" ]; then
    return 1
  fi
  
  # Additional check for installation to ensure we truly have Docmosis
  if ! is_docmosis_installed; then
    return 1
  fi
  
  # Not available
  return 1
}

# Load Docmosis configuration and set environment variables
load_docmosis_config() {
  # Check for existing environment variables first
  if [ -n "${DOCMOSIS_KEY}" ] && [ -n "${DOCMOSIS_SITE}" ]; then
    docmosis_info "Using Docmosis configuration from environment variables"
    return 0
  fi
  
  # Try loading from various configuration locations
  local config_sources=(
    "${PROJECT_DOCMOSIS_CONFIG}"
    "${USER_CONFIG_FILE}"
    "${SYSTEM_DOCMOSIS_CONFIG}"
  )
  
  for config_source in "${config_sources[@]}"; do
    if [ -f "${config_source}" ]; then
      docmosis_info "Reading Docmosis configuration from ${config_source}"
      
      # Check file extension to determine format
      if [[ "${config_source}" == *.properties ]]; then
        # Properties file format
        if grep -q "docmosis.key" "${config_source}"; then
          export DOCMOSIS_KEY=$(grep "docmosis.key" "${config_source}" | cut -d'=' -f2)
          export DOCMOSIS_SITE=$(grep "docmosis.site" "${config_source}" | cut -d'=' -f2)
          return 0
        fi
      elif [[ "${config_source}" == *.json ]]; then
        # JSON format
        if grep -q '"key":' "${config_source}"; then
          export DOCMOSIS_KEY=$(grep -o '"key": "[^"]*"' "${config_source}" | cut -d'"' -f4)
          export DOCMOSIS_SITE=$(grep -o '"site": "[^"]*"' "${config_source}" | cut -d'"' -f4)
          return 0
        fi
      fi
    fi
  done
  
  # No configuration found
  docmosis_warn "No Docmosis configuration found"
  return 1
}

# Set up Docmosis configuration interactively
setup_docmosis_config() {
  docmosis_info "Setting up Docmosis configuration"
  
  # Create config directories if they don't exist
  mkdir -p "${USER_CONFIG_DIR}"
  mkdir -p "$(dirname "${PROJECT_DOCMOSIS_CONFIG}")"
  
  # Ask for license key
  local key=""
  local site=""
  
  # Check if we already have one
  if [ -n "${DOCMOSIS_KEY}" ]; then
    docmosis_info "Current license key: ${DOCMOSIS_KEY:0:8}...${DOCMOSIS_KEY:(-4)}"
    read -p "Enter new license key (or press enter to keep current): " key
    if [ -z "$key" ]; then
      key="${DOCMOSIS_KEY}"
    fi
  else
    while [ -z "$key" ]; do
      read -p "Enter your Docmosis license key: " key
      if [ -z "$key" ]; then
        docmosis_warn "License key cannot be empty"
      fi
    done
  fi
  
  # Ask for site name
  if [ -n "${DOCMOSIS_SITE}" ]; then
    docmosis_info "Current site name: ${DOCMOSIS_SITE}"
    read -p "Enter site name (or press enter to keep current): " site
    if [ -z "$site" ]; then
      site="${DOCMOSIS_SITE}"
    fi
  else
    read -p "Enter site name (optional): " site
  fi
  
  # Save configuration to both properties and JSON formats
  echo "docmosis.key=${key}" > "${PROJECT_DOCMOSIS_CONFIG}"
  echo "docmosis.site=${site}" >> "${PROJECT_DOCMOSIS_CONFIG}"
  
  # Update JSON config
  if [ -f "${USER_CONFIG_FILE}" ]; then
    # Check if file is valid JSON
    if jq empty "${USER_CONFIG_FILE}" 2>/dev/null; then
      # Update existing JSON file
      local tmp_file=$(mktemp)
      jq ".docmosis = {\"key\": \"${key}\", \"site\": \"${site}\"}" "${USER_CONFIG_FILE}" > "${tmp_file}"
      mv "${tmp_file}" "${USER_CONFIG_FILE}"
    else
      # Replace invalid JSON with new configuration
      echo "{\"docmosis\": {\"key\": \"${key}\", \"site\": \"${site}\"}}" > "${USER_CONFIG_FILE}"
    fi
  else
    # Create new JSON file
    echo "{\"docmosis\": {\"key\": \"${key}\", \"site\": \"${site}\"}}" > "${USER_CONFIG_FILE}"
  fi
  
  # Set environment variables
  export DOCMOSIS_KEY="${key}"
  export DOCMOSIS_SITE="${site}"
  
  docmosis_success "Docmosis configuration saved successfully"
  return 0
}

# Check if Docmosis is installed
is_docmosis_installed() {
  local docmosis_jar="${PROJECT_ROOT}/lib/com/docmosis/docmosis-java/${DOCMOSIS_VERSION}/docmosis-java-${DOCMOSIS_VERSION}.jar"
  
  if [ -f "${docmosis_jar}" ]; then
    return 0
  else
    return 1
  fi
}

# Install Docmosis JAR files
install_docmosis() {
  docmosis_info "Installing Docmosis JAR files"
  
  # Ensure lib directory exists
  local lib_dir="${PROJECT_ROOT}/lib"
  mkdir -p "${lib_dir}/com/docmosis/docmosis-java/${DOCMOSIS_VERSION}"
  mkdir -p "${lib_dir}/net/sf/barcode4j/barcode4j/${BARCODE4J_VERSION}"
  
  # Check if JAR files exist in user's home directory
  local docmosis_src="${HOME}/java-${DOCMOSIS_VERSION}.jar"
  local docmosis_javadoc="${HOME}/java-${DOCMOSIS_VERSION}-javadoc.jar"
  local docmosis_download_url="https://www.docmosis.com/resources/java-${DOCMOSIS_VERSION}.zip"
  
  # If JAR not found, attempt to download it
  if [ ! -f "${docmosis_src}" ]; then
    docmosis_info "Docmosis JAR file not found at ${docmosis_src}"
    docmosis_info "Attempting to download Docmosis..."
    
    # Create a temporary directory for download
    local tmp_dir=$(mktemp -d)
    local download_zip="${tmp_dir}/docmosis.zip"
    
    # Download the Docmosis package
    if command -v curl &> /dev/null; then
      curl -L -o "${download_zip}" "${docmosis_download_url}"
    elif command -v wget &> /dev/null; then
      wget -O "${download_zip}" "${docmosis_download_url}"
    else
      docmosis_error "Neither curl nor wget are available to download Docmosis"
      echo "Please install Docmosis manually by downloading from ${docmosis_download_url}"
      echo "and place the jar file at ${docmosis_src}"
      rm -rf "${tmp_dir}"
      return 1
    fi
    
    # Unzip the downloaded file
    if [ -f "${download_zip}" ]; then
      docmosis_info "Extracting Docmosis..."
      unzip -q "${download_zip}" -d "${tmp_dir}"
      
      # Find and copy the jar files
      if [ -f "${tmp_dir}/java-${DOCMOSIS_VERSION}.jar" ]; then
        cp "${tmp_dir}/java-${DOCMOSIS_VERSION}.jar" "${docmosis_src}"
        docmosis_success "Docmosis JAR downloaded and installed at ${docmosis_src}"
      fi
      
      if [ -f "${tmp_dir}/java-${DOCMOSIS_VERSION}-javadoc.jar" ]; then
        cp "${tmp_dir}/java-${DOCMOSIS_VERSION}-javadoc.jar" "${docmosis_javadoc}"
      fi
      
      # Clean up temp directory
      rm -rf "${tmp_dir}"
      
      # Check if we have the JAR now
      if [ ! -f "${docmosis_src}" ]; then
        docmosis_error "Could not find or download Docmosis JAR"
        return 1
      fi
    else
      docmosis_error "Failed to download Docmosis"
      rm -rf "${tmp_dir}"
      return 1
    fi
  fi
  
  # Copy the JAR files to the local repository with proper Maven naming
  docmosis_info "Copying Docmosis JAR files to local repository structure..."
  cp "${docmosis_src}" "${lib_dir}/com/docmosis/docmosis-java/${DOCMOSIS_VERSION}/docmosis-java-${DOCMOSIS_VERSION}.jar"
  
  if [ -f "${docmosis_javadoc}" ]; then
    cp "${docmosis_javadoc}" "${lib_dir}/com/docmosis/docmosis-java/${DOCMOSIS_VERSION}/docmosis-java-${DOCMOSIS_VERSION}-javadoc.jar"
  fi
  
  # Create a POM file for the local repository
  cat > "${lib_dir}/com/docmosis/docmosis-java/${DOCMOSIS_VERSION}/docmosis-java-${DOCMOSIS_VERSION}.pom" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.docmosis</groupId>
  <artifactId>docmosis-java</artifactId>
  <version>${DOCMOSIS_VERSION}</version>
  <packaging>jar</packaging>
  <name>Docmosis Java</name>
  <description>Java library for document generation using templates</description>
  
  <dependencies>
    <dependency>
      <groupId>net.sf.barcode4j</groupId>
      <artifactId>barcode4j</artifactId>
      <version>${BARCODE4J_VERSION}</version>
      <optional>true</optional>
    </dependency>
  </dependencies>
</project>
EOF

  docmosis_success "Docmosis JAR files installed successfully"
  return 0
}

# Test Docmosis installation
test_docmosis() {
  docmosis_info "Testing Docmosis integration"
  
  # Ensure configuration is loaded
  if ! load_docmosis_config; then
    docmosis_error "Failed to load Docmosis configuration"
    return 1
  fi
  
  # Verify Docmosis is installed
  if ! is_docmosis_installed; then
    docmosis_warn "Docmosis is not installed. Installing now..."
    if ! install_docmosis; then
      docmosis_error "Failed to install Docmosis"
      return 1
    fi
  fi
  
  # Create a test directory
  local test_dir="${PROJECT_ROOT}/target/docmosis-test"
  mkdir -p "${test_dir}"
  
  # Create a simple test template
  local test_template="${test_dir}/test-template.txt"
  cat > "${test_template}" << EOF
Docmosis Smoke Test
===================
Time: <<datetime>>
License: <<key>>
EOF

  docmosis_info "Creating a simple smoke test..."
  
  # Run a Java test program
  cat > "${test_dir}/DocmosisTest.java" << EOF
import java.io.File;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class DocmosisTest {
    public static void main(String[] args) {
        try {
            String docmosisKey = System.getenv("DOCMOSIS_KEY");
            String docmosisSite = System.getenv("DOCMOSIS_SITE");
            String templatePath = "${test_template}";
            String outputPath = "${test_dir}/output.pdf";
            
            System.out.println("Testing Docmosis with license key: " + docmosisKey);
            
            // Initialize Docmosis using reflection
            Class<?> systemManagerClass = Class.forName("com.docmosis.SystemManager");
            
            // Initialize Docmosis
            Method initializeMethod = systemManagerClass.getMethod("initialise");
            initializeMethod.invoke(null);
            
            // Set license key
            Method setKeyMethod = systemManagerClass.getMethod("setKey", String.class);
            setKeyMethod.invoke(null, docmosisKey);
            
            // Set site if provided
            if (docmosisSite != null && !docmosisSite.trim().isEmpty()) {
                Method setSiteMethod = systemManagerClass.getMethod("setSite", String.class);
                setSiteMethod.invoke(null, docmosisSite);
            }
            
            // Get renderer
            Method getRendererMethod = systemManagerClass.getMethod("getRenderer");
            Object renderer = getRendererMethod.invoke(null);
            Class<?> rendererClass = renderer.getClass();
            
            // Prepare template data
            Map<String, Object> data = new HashMap<>();
            data.put("datetime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            data.put("key", docmosisKey.substring(0, 8) + "..." + docmosisKey.substring(docmosisKey.length() - 4));
            
            // Render document
            Method renderMethod = rendererClass.getMethod("render", String.class, String.class, Map.class);
            renderMethod.invoke(renderer, templatePath, outputPath, data);
            
            // Shutdown Docmosis
            Method shutdownMethod = systemManagerClass.getMethod("shutdown");
            shutdownMethod.invoke(null);
            
            // Verify output file was created
            File output = new File(outputPath);
            if (output.exists() && output.length() > 0) {
                System.out.println("SMOKE TEST PASSED: Document generated successfully at " + outputPath);
                System.exit(0);
            } else {
                System.out.println("SMOKE TEST FAILED: Document was not generated");
                System.exit(1);
            }
            
        } catch (Exception e) {
            System.out.println("SMOKE TEST FAILED: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
EOF

  # Compile and run the test
  docmosis_info "Compiling test class..."
  cd "${test_dir}"
  
  # Find the Docmosis JAR
  local classpath="${PROJECT_ROOT}/lib/com/docmosis/docmosis-java/${DOCMOSIS_VERSION}/docmosis-java-${DOCMOSIS_VERSION}.jar"
  
  # Compile
  javac -cp "${classpath}" DocmosisTest.java
  if [ $? -ne 0 ]; then
    docmosis_error "Failed to compile test class"
    return 1
  fi
  
  # Run the test
  docmosis_info "Running Docmosis test..."
  java -cp "${classpath}:." DocmosisTest
  
  # Check result
  if [ $? -eq 0 ]; then
    docmosis_success "Docmosis integration test PASSED!"
    echo "A sample document was generated at: ${test_dir}/output.pdf"
    return 0
  else
    docmosis_error "Docmosis integration test FAILED"
    echo "Please check the error messages above for details."
    return 1
  fi
}

# Generate documentation with Docmosis
generate_docs() {
  local output_dir="${1:-${PROJECT_ROOT}/target/docs}"
  local format="${2:-pdf}"
  
  # Validate format
  if [[ ! "$format" =~ ^(pdf|docx|html|md|markdown)$ ]]; then
    docmosis_error "Invalid format: $format. Must be one of: pdf, docx, html, md, markdown"
    return 1
  fi
  
  # Create output directory if it doesn't exist
  mkdir -p "$output_dir"
  
  # For debugging
  docmosis_info "Output directory: $output_dir"
  docmosis_info "Format: $format"
  
  # Check if Docmosis is available
  if is_docmosis_available; then
    # Ensure configuration is loaded
    if ! load_docmosis_config; then
      docmosis_warn "Failed to load Docmosis configuration"
      docmosis_info "Falling back to Maven site documentation"
      generate_fallback_docs "$output_dir" "$format"
      return $?
    fi
    
    # Verify Docmosis is installed
    if ! is_docmosis_installed; then
      docmosis_warn "Docmosis is not installed. Installing now..."
      if ! install_docmosis; then
        docmosis_warn "Failed to install Docmosis"
        docmosis_info "Falling back to Maven site documentation"
        generate_fallback_docs "$output_dir" "$format"
        return $?
      fi
    fi
    
    # Build the project with the docmosis profile
    docmosis_info "Building project with docmosis-report profile..."
    cd "${PROJECT_ROOT}" || return 1
    mvn clean package -P docmosis-report -DskipTests -Ddocmosis.key="${DOCMOSIS_KEY}" -Ddocmosis.site="${DOCMOSIS_SITE}"
    
    if [ $? -ne 0 ]; then
      docmosis_warn "Failed to build project with docmosis-report profile"
      docmosis_info "Falling back to Maven site documentation"
      generate_fallback_docs "$output_dir" "$format"
      return $?
    fi
    
    # Run the documentation generator
    docmosis_info "Generating documentation in ${format} format..."
    cd "${PROJECT_ROOT}/Samstraumr/samstraumr-core" || return 1
    
    # Set environment variables for the generator
    export DOCMOSIS_KEY="${DOCMOSIS_KEY}"
    export DOCMOSIS_SITE="${DOCMOSIS_SITE}"
    export DOCMOSIS_FORMAT="${format}"
    
    # Run documentation generator
    java -cp target/samstraumr-core.jar \
         -Ddocmosis.key="${DOCMOSIS_KEY}" \
         -Ddocmosis.site="${DOCMOSIS_SITE}" \
         org.samstraumr.tube.reporting.DocumentGenerator "${output_dir}" "${format}"
    
    # Check if generation was successful
    if [ $? -eq 0 ]; then
      docmosis_success "Documentation generated successfully in ${output_dir}"
      echo "Generated files:"
      ls -la "${output_dir}"
      
      # Try to open the generated file if it exists
      local generated_files=("${output_dir}"/*.${format})
      if [ -f "${generated_files[0]}" ]; then
        if command -v xdg-open &>/dev/null; then
          docmosis_info "Opening generated document..."
          xdg-open "${generated_files[0]}" &>/dev/null &
        elif command -v open &>/dev/null; then
          docmosis_info "Opening generated document..."
          open "${generated_files[0]}" &>/dev/null &
        fi
      fi
      return 0
    else
      docmosis_warn "Documentation generation failed"
      docmosis_info "Falling back to Maven site documentation"
      generate_fallback_docs "$output_dir" "$format"
      return $?
    fi
  else
    # Docmosis not available, use fallback method
    docmosis_info "Docmosis not available, using Maven site documentation"
    generate_fallback_docs "$output_dir" "$format"
    return $?
  fi
}

# Fallback documentation generator using plain files
generate_fallback_docs() {
  local output_dir="${1:-${PROJECT_ROOT}/target/docs}"
  local format="${2:-md}"
  
  docmosis_info "Using fallback document generator for format: $format"
  docmosis_info "Output directory: $output_dir"
  
  # Create output directory if it doesn't exist
  mkdir -p "$output_dir"
  
  # Simply generate a basic markdown documentation
  docmosis_info "Generating markdown documentation in ${output_dir}..."
  
  # Create a readme index file
  echo "# Samstraumr Documentation" > "${output_dir}/index.md"
  echo "" >> "${output_dir}/index.md"
  echo "This documentation was generated without Docmosis integration." >> "${output_dir}/index.md"
  echo "To enable full document generation, configure Docmosis with \`./s8r docmosis setup\`." >> "${output_dir}/index.md"
  echo "" >> "${output_dir}/index.md"
  echo "## Contents" >> "${output_dir}/index.md"
  
  # Add direct content to ensure something is generated
  cat >> "${output_dir}/index.md" << EOF

## Project Overview

Samstraumr is a framework for building component-based systems with well-defined identity and lifecycle management.

### Key Features

1. Component model with explicit identity
2. Lifecycle management
3. Composite pattern for component aggregation
4. Machine orchestration for component execution

## Documentation

For full documentation, configure Docmosis with:

\`\`\`
./s8r docmosis setup
\`\`\`

## Core Concepts

- **Tubes**: Basic components with identity and lifecycle
- **Composites**: Component aggregates
- **Machines**: Execution environment for components

EOF
  
  # Also copy some of the key markdown files directly
  docmosis_info "Copying key documentation files..."
  for key_file in README.md CHANGELOG.md docs/README.md; do
    if [ -f "${PROJECT_ROOT}/${key_file}" ]; then
      # Get directory of the target file
      local dir_name="$(dirname "${output_dir}/${key_file}")"
      # Create the directory if it doesn't exist
      mkdir -p "$dir_name"
      # Copy the file
      cp "${PROJECT_ROOT}/${key_file}" "${output_dir}/${key_file}"
      # Add to index
      echo "- [${key_file}](${key_file})" >> "${output_dir}/index.md"
    fi
  done
  
  docmosis_success "Markdown documentation generated in ${output_dir}"
  
  # Try to open index.md if exists
  if [ -f "${output_dir}/index.md" ]; then
    if command -v xdg-open &>/dev/null; then
      docmosis_info "Opening documentation..."
      xdg-open "${output_dir}/index.md" &>/dev/null &
    elif command -v open &>/dev/null; then
      docmosis_info "Opening documentation..."
      open "${output_dir}/index.md" &>/dev/null &
    fi
  fi
  return 0
}

# Generate change report
generate_change_report() {
  local from_version="$1"
  local to_version="$2"
  local output_dir="${3:-${PROJECT_ROOT}/target/reports}"
  
  # Validate required arguments
  if [ -z "$from_version" ] || [ -z "$to_version" ]; then
    docmosis_error "Missing required arguments. Usage: generate_change_report <from-version> <to-version> [output-dir]"
    return 1
  fi
  
  # Create output directory if it doesn't exist
  mkdir -p "$output_dir"
  
  # Check if Docmosis is available
  if is_docmosis_available; then
    # Ensure configuration is loaded
    if ! load_docmosis_config; then
      docmosis_warn "Failed to load Docmosis configuration"
      docmosis_info "Falling back to Git-based change log generation"
      generate_fallback_change_report "$from_version" "$to_version" "$output_dir"
      return $?
    fi
    
    # Verify Docmosis is installed
    if ! is_docmosis_installed; then
      docmosis_warn "Docmosis is not installed. Installing now..."
      if ! install_docmosis; then
        docmosis_warn "Failed to install Docmosis"
        docmosis_info "Falling back to Git-based change log generation"
        generate_fallback_change_report "$from_version" "$to_version" "$output_dir"
        return $?
      fi
    fi
    
    # Build the project with the docmosis profile
    docmosis_info "Building project with docmosis-report profile..."
    cd "${PROJECT_ROOT}" || return 1
    mvn clean package -P docmosis-report -DskipTests -Ddocmosis.key="${DOCMOSIS_KEY}" -Ddocmosis.site="${DOCMOSIS_SITE}"
    
    if [ $? -ne 0 ]; then
      docmosis_warn "Failed to build project with docmosis-report profile"
      docmosis_info "Falling back to Git-based change log generation"
      generate_fallback_change_report "$from_version" "$to_version" "$output_dir"
      return $?
    fi
    
    # Generate the report
    docmosis_info "Generating change management report from ${from_version} to ${to_version}..."
    cd "${PROJECT_ROOT}/Samstraumr/samstraumr-core" || return 1
    
    # Run change report generator
    java -cp target/samstraumr-core.jar \
         -Ddocmosis.key="${DOCMOSIS_KEY}" \
         -Ddocmosis.site="${DOCMOSIS_SITE}" \
         org.samstraumr.tube.reporting.ChangeReportGenerator "$from_version" "$to_version" "$output_dir"
    
    # Check if generation was successful
    if [ $? -eq 0 ]; then
      docmosis_success "Change report generated successfully in ${output_dir}"
      echo "Generated files:"
      ls -la "${output_dir}"
      
      # Try to open the generated file
      local report_files=("${output_dir}"/ChangeReport-*.docx)
      if [ -f "${report_files[0]}" ]; then
        if command -v xdg-open &>/dev/null; then
          docmosis_info "Opening change report..."
          xdg-open "${report_files[0]}" &>/dev/null &
        elif command -v open &>/dev/null; then
          docmosis_info "Opening change report..."
          open "${report_files[0]}" &>/dev/null &
        fi
      fi
      return 0
    else
      docmosis_warn "Change report generation with Docmosis failed"
      docmosis_info "Falling back to Git-based change log generation"
      generate_fallback_change_report "$from_version" "$to_version" "$output_dir"
      return $?
    fi
  else
    # Docmosis not available, use fallback method
    docmosis_info "Docmosis not available, using Git-based change log generation"
    generate_fallback_change_report "$from_version" "$to_version" "$output_dir"
    return $?
  fi
}

# Fallback change report generator using Git log
generate_fallback_change_report() {
  local from_version="$1"
  local to_version="$2"
  local output_dir="${3:-${PROJECT_ROOT}/target/reports}"
  
  docmosis_info "Using fallback change report generator"
  docmosis_info "From version: $from_version, To version: $to_version"
  docmosis_info "Output directory: $output_dir"
  
  # Ensure output directory exists by creating it directly
  mkdir -p "$output_dir"
  
  # Generate a simple markdown change report using git log
  docmosis_info "Generating Git-based change report..."
  
  # Format versions for git
  local git_from_version="${from_version}"
  local git_to_version="${to_version}"
  
  # Add 'v' prefix if not present for git tag compatibility
  if [[ ! "$git_from_version" =~ ^v ]]; then
    git_from_version="v${git_from_version}"
  fi
  
  # Don't add prefix if using HEAD or main
  if [[ "$git_to_version" != "HEAD" && "$git_to_version" != "main" && ! "$git_to_version" =~ ^v ]]; then
    git_to_version="v${git_to_version}"
  fi
  
  # Prepare report filename (with absolute path)
  local report_file="${output_dir}/ChangeReport-${from_version}-to-${to_version}.md"
  docmosis_info "Will write report to: $report_file"
  
  # Create report header
  cat > "$report_file" << EOF
# Change Report: ${from_version} to ${to_version}

Generated: $(date '+%Y-%m-%d %H:%M:%S')

This report was generated without Docmosis integration.
To enable full document generation, configure Docmosis with \`./s8r docmosis setup\`.

## Changes

EOF
  
  # Add git log output
  cd "${PROJECT_ROOT}" || return 1
  
  # Use a fallback if tags don't exist
  git log --no-merges --pretty=format:"* %ad - %s (%an)" --date=short -n 50 >> "$report_file"
  
  # Add simple statistics section
  echo -e "\n\n## Statistics\n" >> "$report_file"
  echo "* Total recent commits: $(git rev-list --count HEAD~50..HEAD)" >> "$report_file"
  echo "* Recent files changed: $(git diff --name-only HEAD~50..HEAD | wc -l)" >> "$report_file"
  
  # Add authors section
  echo -e "\n## Contributors\n" >> "$report_file"
  git log --pretty=format:"* %an <%ae>" -n 50 | sort | uniq >> "$report_file"
  
  docmosis_success "Change report generated at ${report_file}"
  
  # Try to open the generated file using xdg-open if available
  if [ -f "$report_file" ]; then
    if command -v xdg-open &>/dev/null; then
      docmosis_info "Opening change report with xdg-open..."
      xdg-open "$report_file" &>/dev/null &
    elif command -v open &>/dev/null; then
      docmosis_info "Opening change report with open..."
      open "$report_file" &>/dev/null &
    else
      docmosis_info "Report available at: $report_file"
    fi
  fi
  
  return 0
}
