#!/bin/bash

# Script to generate an ADR test suite
# This creates a new test class for validating implementation of an ADR

# Source common functions
source "$(dirname "$0")/../lib/common.sh"

# Display usage instructions
function show_usage {
  echo "Usage: $0 <ADR-NUMBER> <ADR-TITLE> [test_author]"
  echo 
  echo "Example: $0 12 \"Implement Circuit Breaker Pattern\" \"Jane Doe\""
  echo
  echo "This will generate:"
  echo "  - A new test file: CircuitBreakerPatternTest.java"
  echo "  - With proper annotations, structure, and test stubs"
  echo "  - Adds the ADR to the RunArchitectureTests.java file"
  exit 1
}

# Check parameters
if [ "$#" -lt 2 ]; then
  show_usage
fi

# Setup variables
ADR_NUMBER=$(printf "%04d" $1)
ADR_TITLE=$2
TEST_AUTHOR=${3:-"Samstraumr Team"}
CURRENT_DATE=$(date +"%Y-%m-%d")
TEST_CLASS_NAME=$(echo "$ADR_TITLE" | sed 's/[^a-zA-Z0-9]/ /g' | awk '{for(i=1;i<=NF;i++) $i=toupper(substr($i,1,1)) substr($i,2)} 1' | sed 's/ //g')Test

# Package and directory information
BASE_DIR="$REPO_ROOT/Samstraumr/samstraumr-core/src/test/java/org/s8r/architecture"
PACKAGE="org.s8r.architecture"

# Create the test file content
cat > "$BASE_DIR/$TEST_CLASS_NAME.java" << EOF
package $PACKAGE;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.test.annotation.UnitTest;
import org.s8r.architecture.util.*;

/**
 * Tests for the ${ADR_TITLE} as described in ADR-${ADR_NUMBER}.
 * 
 * <p>This test suite validates the implementation of the ${ADR_TITLE}
 * within the Samstraumr framework.
 *
 * @author ${TEST_AUTHOR}
 * @since ${CURRENT_DATE}
 */
@UnitTest
@Tag("architecture")
@Tag("adr-validation")
@DisplayName("${ADR_TITLE} Tests (ADR-${ADR_NUMBER})")
public class ${TEST_CLASS_NAME} {

    @BeforeEach
    void setUp() {
        // TODO: Initialize test environment
    }

    @Nested
    @DisplayName("Core Functionality Tests")
    class CoreFunctionalityTests {
        
        @Test
        @DisplayName("TODO: Replace with actual test name")
        void firstTest() {
            // TODO: Implement test
            // Use TestComponentFactory, TestMachineFactory, or other utility classes as needed
            assertTrue(true, "Replace with actual test implementation");
        }
        
        @Test
        @DisplayName("TODO: Replace with actual test name")
        void secondTest() {
            // TODO: Implement test
            assertTrue(true, "Replace with actual test implementation");
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("TODO: Replace with actual test name")
        void integrationTest() {
            // TODO: Implement test
            assertTrue(true, "Replace with actual test implementation");
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("TODO: Replace with actual test name")
        void edgeCaseTest() {
            // TODO: Implement test
            assertTrue(true, "Replace with actual test implementation");
        }
    }
}
EOF

# Update the RunArchitectureTests class to include the new ADR
RUN_ARCH_TESTS="$BASE_DIR/RunArchitectureTests.java"

# Extract the current ADR list
ADR_LIST=$(grep -A 15 "<ul>" "$RUN_ARCH_TESTS" | grep "<li>" | grep -v PLACEHOLDER)

# Create the new ADR entry
NEW_ADR_ENTRY="   *   <li>ADR-${ADR_NUMBER}: ${ADR_TITLE}</li>"

# Check if the ADR is already listed
if ! echo "$ADR_LIST" | grep -q "ADR-${ADR_NUMBER}"; then
  # Update the RunArchitectureTests.java file
  sed -i "/   \*   <\/ul>/i \ $NEW_ADR_ENTRY" "$RUN_ARCH_TESTS"
  echo "Updated $RUN_ARCH_TESTS with ADR-${ADR_NUMBER}"
fi

echo "Generated test class: $BASE_DIR/$TEST_CLASS_NAME.java"
echo "Test for ADR-${ADR_NUMBER}: ${ADR_TITLE}"
echo
echo "Next steps:"
echo "1. Implement the test methods in $TEST_CLASS_NAME.java"
echo "2. Run the test with: ./run-architecture-tests.sh"
chmod +x "$BASE_DIR/$TEST_CLASS_NAME.java"