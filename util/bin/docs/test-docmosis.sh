#!/bin/bash
#==============================================================================
# Filename: test-docmosis.sh
# Description: Simple smoke test for Docmosis integration
#==============================================================================

# Path variables
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="${SCRIPT_DIR}"

# Define color codes for terminal output
COLOR_RED='\033[0;31m'
COLOR_GREEN='\033[0;32m'
COLOR_YELLOW='\033[0;33m'
COLOR_BLUE='\033[0;34m'
COLOR_RESET='\033[0m'

echo -e "${COLOR_BLUE}=== Docmosis Integration Smoke Test ===${COLOR_RESET}"
echo ""

# Check for Docmosis key in environment or config
USER_CONFIG_FILE="${HOME}/.s8r/config.json"
if [ -z "${DOCMOSIS_KEY}" ]; then
  if [ -f "${USER_CONFIG_FILE}" ]; then
    echo -e "${COLOR_BLUE}Reading Docmosis key from config file: ${USER_CONFIG_FILE}${COLOR_RESET}"
    export DOCMOSIS_KEY=$(grep -o '"key": "[^"]*"' "${USER_CONFIG_FILE}" | cut -d'"' -f4)
    export DOCMOSIS_SITE=$(grep -o '"site": "[^"]*"' "${USER_CONFIG_FILE}" | cut -d'"' -f4)
  fi
fi

# Verify key is available
if [ -z "${DOCMOSIS_KEY}" ]; then
  echo -e "${COLOR_RED}Error: Docmosis license key not found${COLOR_RESET}"
  echo "Please ensure the key is set in one of the following ways:"
  echo "1. In ~/.s8r/config.json file"
  echo "2. As DOCMOSIS_KEY environment variable"
  exit 1
fi

# Check license key format (basic validation)
if [[ ! "${DOCMOSIS_KEY}" =~ ^[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]{4}-[A-Z0-9]-[A-Z0-9]{4}$ ]]; then
  echo -e "${COLOR_YELLOW}Warning: Docmosis key format looks unusual${COLOR_RESET}"
else
  echo -e "${COLOR_GREEN}Docmosis key format is valid${COLOR_RESET}"
fi

# Check for Docmosis installation
LIB_DIR="${PROJECT_ROOT}/lib"
DOCMOSIS_VERSION="4.8.0"
DOCMOSIS_JAR="${LIB_DIR}/com/docmosis/docmosis-java/${DOCMOSIS_VERSION}/docmosis-java-${DOCMOSIS_VERSION}.jar"

if [ ! -f "${DOCMOSIS_JAR}" ]; then
  echo -e "${COLOR_YELLOW}Docmosis JAR not found. Running installation...${COLOR_RESET}"
  
  # Run installation script
  if [ -f "${PROJECT_ROOT}/install-docmosis.sh" ]; then
    "${PROJECT_ROOT}/install-docmosis.sh"
    if [ $? -ne 0 ]; then
      echo -e "${COLOR_RED}Error: Failed to install Docmosis${COLOR_RESET}"
      exit 1
    fi
  else
    echo -e "${COLOR_RED}Error: Installation script not found${COLOR_RESET}"
    exit 1
  fi
else
  echo -e "${COLOR_GREEN}Docmosis JAR found at:${COLOR_RESET} ${DOCMOSIS_JAR}"
fi

# Create a small test directory
TEST_DIR="${PROJECT_ROOT}/target/docmosis-test"
mkdir -p "${TEST_DIR}"

# Create a simple test template
TEST_TEMPLATE="${TEST_DIR}/test-template.txt"
cat > "${TEST_TEMPLATE}" << EOF
Docmosis Smoke Test
===================
Time: <<datetime>>
License: <<key>>
EOF

echo -e "${COLOR_BLUE}Creating a simple smoke test...${COLOR_RESET}"
echo "This will test Docmosis engine initialization with your license key"

# Run a simple Java program to test Docmosis
cat > "${TEST_DIR}/DocmosisTest.java" << EOF
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
            String templatePath = "${TEST_TEMPLATE}";
            String outputPath = "${TEST_DIR}/output.pdf";
            
            System.out.println("Testing Docmosis with license key: " + docmosisKey);
            
            // Initialize Docmosis using reflection
            Class<?> systemManagerClass = Class.forName("com.docmosis.SystemManager");
            
            // Initialize Docmosis
            Method initializeMethod = systemManagerClass.getMethod("initialise");
            initializeMethod.invoke(null);
            
            // Set license key
            Method setKeyMethod = systemManagerClass.getMethod("setKey", String.class);
            setKeyMethod.invoke(null, docmosisKey);
            
            // Set site
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
echo -e "${COLOR_BLUE}Compiling test class...${COLOR_RESET}"
cd "${TEST_DIR}"

# Find the Docmosis JAR
CLASSPATH="${DOCMOSIS_JAR}"

# Compile
javac -cp "${CLASSPATH}" DocmosisTest.java
if [ $? -ne 0 ]; then
  echo -e "${COLOR_RED}Error: Failed to compile test class${COLOR_RESET}"
  exit 1
fi

# Run the test
echo -e "${COLOR_BLUE}Running Docmosis test...${COLOR_RESET}"
java -cp "${CLASSPATH}:." DocmosisTest

# Check result
if [ $? -eq 0 ]; then
  echo -e "${COLOR_GREEN}Docmosis integration test PASSED!${COLOR_RESET}"
  echo "A sample document was generated at: ${TEST_DIR}/output.pdf"
  echo ""
  echo "You can now use Docmosis with the s8r tool:"
  echo "  ./s8r docs [output-dir] [format]"
else
  echo -e "${COLOR_RED}Docmosis integration test FAILED${COLOR_RESET}"
  echo "Please check the error messages above for details."
  exit 1
fi