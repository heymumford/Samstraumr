#\!/usr/bin/env bash
#==============================================================================
# S8r Docmosis Library Functions
# Shared code for Docmosis integration
#==============================================================================

# Find repository root
PROJECT_ROOT="${PROJECT_ROOT:-$(git rev-parse --show-toplevel)}"

# Source common library if available and not already sourced
if [ -f "${PROJECT_ROOT}/.s8r/lib/common.sh" ] && \! type -t info > /dev/null; then
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

# Load Docmosis configuration and set environment variables
load_docmosis_config() {
  # Check for existing environment variables first
  if [ -n "${DOCMOSIS_KEY}" ] && [ -n "${DOCMOSIS_SITE}" ]; then
    docmosis_info "Using Docmosis configuration from environment variables"
    return 0
  }
  
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
  if [ \! -f "${docmosis_src}" ]; then
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
      if [ \! -f "${docmosis_src}" ]; then
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
  if \! load_docmosis_config; then
    docmosis_error "Failed to load Docmosis configuration"
    return 1
  fi
  
  # Verify Docmosis is installed
  if \! is_docmosis_installed; then
    docmosis_warn "Docmosis is not installed. Installing now..."
    if \! install_docmosis; then
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
            if (docmosisSite \!= null && \!docmosisSite.trim().isEmpty()) {
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
    docmosis_success "Docmosis integration test PASSED\!"
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
  local output_dir="${1:-target/docs}"
  local format="${2:-pdf}"
  
  # Validate format
  if [[ \! "$format" =~ ^(pdf|docx|html)$ ]]; then
    docmosis_error "Invalid format: $format. Must be one of: pdf, docx, html"
    return 1
  fi
  
  # Ensure configuration is loaded
  if \! load_docmosis_config; then
    docmosis_error "Failed to load Docmosis configuration"
    return 1
  fi
  
  # Verify Docmosis is installed
  if \! is_docmosis_installed; then
    docmosis_warn "Docmosis is not installed. Installing now..."
    if \! install_docmosis; then
      docmosis_error "Failed to install Docmosis"
      return 1
    fi
  fi
  
  # Create output directory if it doesn't exist
  mkdir -p "$output_dir"
  
  # Build the project with the docmosis profile
  docmosis_info "Building project with docmosis-report profile..."
  cd "${PROJECT_ROOT}" || return 1
  mvn clean package -P docmosis-report -DskipTests -Ddocmosis.key="${DOCMOSIS_KEY}" -Ddocmosis.site="${DOCMOSIS_SITE}"
  
  if [ $? -ne 0 ]; then
    docmosis_error "Failed to build project with docmosis-report profile"
    return 1
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
    docmosis_error "Documentation generation failed"
    return 1
  fi
}

# Generate change report
generate_change_report() {
  local from_version="$1"
  local to_version="$2"
  local output_dir="${3:-target/reports}"
  
  # Validate required arguments
  if [ -z "$from_version" ] || [ -z "$to_version" ]; then
    docmosis_error "Missing required arguments. Usage: generate_change_report <from-version> <to-version> [output-dir]"
    return 1
  fi
  
  # Ensure configuration is loaded
  if \! load_docmosis_config; then
    docmosis_error "Failed to load Docmosis configuration"
    return 1
  fi
  
  # Verify Docmosis is installed
  if \! is_docmosis_installed; then
    docmosis_warn "Docmosis is not installed. Installing now..."
    if \! install_docmosis; then
      docmosis_error "Failed to install Docmosis"
      return 1
    fi
  fi
  
  # Create output directory if it doesn't exist
  mkdir -p "$output_dir"
  
  # Build the project with the docmosis profile
  docmosis_info "Building project with docmosis-report profile..."
  cd "${PROJECT_ROOT}" || return 1
  mvn clean package -P docmosis-report -DskipTests -Ddocmosis.key="${DOCMOSIS_KEY}" -Ddocmosis.site="${DOCMOSIS_SITE}"
  
  if [ $? -ne 0 ]; then
    docmosis_error "Failed to build project with docmosis-report profile"
    return 1
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
    docmosis_error "Change report generation failed"
    return 1
  fi
}
