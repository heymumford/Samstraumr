# Testing S8r R&D Environment Setup

## Overview

This document outlines a structured approach to test the complete setup process for a Samstraumr (S8r) R&D environment. It ensures that developers can reliably create new development environments with minimal friction and that the auto-installation of prerequisites works correctly across different Linux distributions.

## Test Objectives

1. Verify that a new S8r environment can be created from scratch on a clean Linux container
2. Ensure the bootstrapping process automatically installs all required prerequisites
3. Validate that the development toolchain is properly configured
4. Confirm that the test suite runs successfully in a fresh environment
5. Validate the polyglot development support (Java, Python, TypeScript)

## Test Environment Matrix

Test setup should be verified on the following platforms:

| Platform | Version | Architecture | Container Type |
|----------|---------|--------------|----------------|
| Ubuntu   | 22.04   | x86_64       | Docker         |
| Ubuntu   | 22.04   | ARM64        | Docker         |
| Debian   | 12      | x86_64       | Docker         |
| Fedora   | 38      | x86_64       | Docker         |
| Alpine   | 3.18    | x86_64       | Docker         |

## Prerequisites Testing

### Core System Requirements

The bootstrapping process should detect and install these core requirements:

- Git (minimum version 2.30)
- Bash (minimum version 5.0)
- cURL or wget
- Build essentials (gcc, make, etc.)
- Python (minimum version 3.10)
- Java JDK (minimum version 21)
- Node.js (minimum version 18)

### Test Script

```bash
#!/bin/bash
# Script to test prerequisite installation

echo "Testing S8r prerequisite detection and installation..."

# Create test log
LOG_FILE="s8r-prereq-test-$(date +%Y%m%d%H%M%S).log"
touch $LOG_FILE

# Function to log with timestamp
log() {
    echo "[$(date +%Y-%m-%d\ %H:%M:%S)] $1" | tee -a $LOG_FILE
}

# Check and record initial system state
log "Initial system state:"
log "-------------------"

if command -v git &> /dev/null; then
    log "✓ Git: $(git --version)"
else
    log "✗ Git: Not installed"
fi

if command -v java &> /dev/null; then
    log "✓ Java: $(java -version 2>&1 | head -n 1)"
else
    log "✗ Java: Not installed"
fi

if command -v python3 &> /dev/null; then
    log "✓ Python: $(python3 --version)"
else
    log "✗ Python: Not installed"
fi

if command -v node &> /dev/null; then
    log "✓ Node.js: $(node --version)"
else
    log "✗ Node.js: Not installed"
fi

# Run the S8r initialization script
log "\nRunning S8r initialization..."
./s8r-init

# Check and record final system state
log "\nFinal system state after S8r initialization:"
log "-------------------"

if command -v git &> /dev/null; then
    log "✓ Git: $(git --version)"
else
    log "✗ Git: Not installed"
fi

if command -v java &> /dev/null; then
    log "✓ Java: $(java -version 2>&1 | head -n 1)"
else
    log "✗ Java: Not installed"
fi

if command -v python3 &> /dev/null; then
    log "✓ Python: $(python3 --version)"
else
    log "✗ Python: Not installed"
fi

if command -v node &> /dev/null; then
    log "✓ Node.js: $(node --version)"
else
    log "✗ Node.js: Not installed"
fi

# Verify Java environment
if [ -d "$HOME/.sdkman" ]; then
    log "✓ SDKMAN installed"
else
    log "✗ SDKMAN not installed"
fi

# Verify Python environment
if [ -d ".venv" ]; then
    log "✓ Python virtual environment created"
else
    log "✗ Python virtual environment not created"
fi

# Verify Node.js environment
if [ -f "package.json" ]; then
    log "✓ Node.js project initialized"
else
    log "✗ Node.js project not initialized"
fi

log "\nPrerequisite testing completed."
```

## Setup Testing Process

### 1. Container Initialization

```bash
# Create a new container for testing
docker run -it --name s8r-test-ubuntu ubuntu:22.04 /bin/bash

# Inside the container
apt-get update
apt-get install -y curl git
```

### 2. Repository Setup

```bash
# Clone the S8r repository
git clone https://github.com/samstraumr/samstraumr.git
cd samstraumr

# Run the initialization script
./s8r-init
```

### 3. Automated Environment Verification

```bash
# Run the environment verification
./s8r-verify
```

This script should:
- Check for all required tools and their versions
- Verify environment variables are set correctly
- Validate that all language environments are properly configured
- Test that sample code in each language can be compiled and run

## Test Cases

### TC-ENV-01: Fresh Container Setup

**Objective**: Verify that S8r can be installed on a completely fresh container with minimal pre-installed components.

**Steps**:
1. Start a fresh container with only bash installed
2. Clone the S8r repository
3. Run the initialization script
4. Verify that all prerequisites are automatically installed
5. Validate that the environment is correctly configured

**Expected Result**: All prerequisites are automatically detected as missing and installed, and the environment is fully configured and ready for development.

### TC-ENV-02: Partial Setup

**Objective**: Verify that S8r detects existing components and only installs missing prerequisites.

**Steps**:
1. Start a container with some prerequisites pre-installed (e.g., Git and Java)
2. Clone the S8r repository
3. Run the initialization script
4. Verify that only missing prerequisites are installed
5. Validate that the environment is correctly configured

**Expected Result**: S8r correctly detects existing components, installs only what's missing, and properly configures the environment.

### TC-ENV-03: Java Environment Setup

**Objective**: Verify that Java development environment is correctly configured.

**Steps**:
1. Set up a fresh S8r environment
2. Run the Java verification test
3. Compile and run a simple Java sample
4. Execute Java-specific tests

**Expected Result**: Java environment is properly set up, with the correct JDK version, compiler, and runtime configuration.

### TC-ENV-04: Python Environment Setup

**Objective**: Verify that Python development environment is correctly configured.

**Steps**:
1. Set up a fresh S8r environment
2. Run the Python verification test
3. Execute a simple Python sample
4. Verify the virtual environment and dependencies

**Expected Result**: Python environment is properly set up, with the correct Python version, virtual environment, and dependencies.

### TC-ENV-05: TypeScript Environment Setup

**Objective**: Verify that TypeScript development environment is correctly configured.

**Steps**:
1. Set up a fresh S8r environment
2. Run the TypeScript verification test
3. Compile and run a simple TypeScript sample
4. Verify the Node.js and npm configuration

**Expected Result**: TypeScript environment is properly set up, with the correct Node.js version, TypeScript compiler, and dependencies.

### TC-ENV-06: Build Toolchain Verification

**Objective**: Verify that the build toolchain works correctly for all languages.

**Steps**:
1. Set up a fresh S8r environment
2. Run the build verification test for each language
3. Verify that the build completes successfully
4. Check that all artifacts are correctly generated

**Expected Result**: Build toolchain correctly compiles and packages code for all supported languages.

### TC-ENV-07: Test Suite Execution

**Objective**: Verify that the test suite can be executed in a fresh environment.

**Steps**:
1. Set up a fresh S8r environment
2. Run the test suite verification
3. Execute the full test suite
4. Verify that all tests pass

**Expected Result**: Test suite executes successfully and all tests pass in a fresh environment.

## Implementation

### S8r Initialization Script Requirements

The `s8r-init` script should:

1. Detect the Linux distribution and version
2. Check for all required prerequisites
3. Install missing prerequisites using the appropriate package manager
4. Set up language-specific environments:
   - Java: Install JDK 21 via SDKMAN
   - Python: Create virtual environment and install dependencies
   - Node.js: Install dependencies via npm/yarn
5. Configure the shell environment
6. Run a verification check to confirm the setup

### Distribution-Specific Handling

The script should handle different distributions appropriately:

#### Ubuntu/Debian
```bash
apt-get update
apt-get install -y openjdk-21-jdk python3.10 python3-pip python3-venv nodejs npm build-essential
```

#### Fedora
```bash
dnf install -y java-21-openjdk python3.10 python3-pip nodejs npm @development-tools
```

#### Alpine
```bash
apk add openjdk21 python3 py3-pip nodejs npm build-base
```

### Environment Variables Setup

```bash
# Add to ~/.bashrc or equivalent
export S8R_HOME=$(pwd)
export PATH=$S8R_HOME/bin:$PATH
export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
```

## Test Automation

### Containerized Testing Script

```bash
#!/bin/bash
# Automated testing of S8r environment setup across different distributions

DISTRIBUTIONS=("ubuntu:22.04" "debian:12" "fedora:38" "alpine:3.18")

for DIST in "${DISTRIBUTIONS[@]}"; do
  echo "Testing S8r setup on $DIST..."
  
  # Create temporary Dockerfile
  cat > Dockerfile.test <<EOF
FROM $DIST

# Install minimal requirements
RUN if command -v apt-get > /dev/null; then \
      apt-get update && apt-get install -y git curl; \
    elif command -v dnf > /dev/null; then \
      dnf install -y git curl; \
    elif command -v apk > /dev/null; then \
      apk add git curl; \
    fi

# Clone the repository
RUN git clone https://github.com/samstraumr/samstraumr.git /s8r

# Set working directory
WORKDIR /s8r

# Run the test
CMD ["./s8r-init", "--test"]
EOF

  # Build and run the test container
  CONTAINER_NAME="s8r-test-$(echo $DIST | tr ':' '-')"
  docker build -t $CONTAINER_NAME -f Dockerfile.test .
  docker run --name $CONTAINER_NAME $CONTAINER_NAME
  
  # Capture logs
  docker logs $CONTAINER_NAME > "$CONTAINER_NAME.log"
  
  # Clean up
  docker rm $CONTAINER_NAME
  docker rmi $CONTAINER_NAME
  
  echo "Test for $DIST completed. See $CONTAINER_NAME.log for details."
done

rm Dockerfile.test
echo "All tests completed."
```

## Continuous Integration Integration

Add a CI job to test environment setup on each supported platform:

```yaml
name: Environment Setup Test

on:
  push:
    branches: [ main ]
    paths:
      - 's8r-init'
      - 'bin/**'
      - 'scripts/**'
  pull_request:
    branches: [ main ]
    paths:
      - 's8r-init'
      - 'bin/**'
      - 'scripts/**'

jobs:
  test-ubuntu:
    runs-on: ubuntu-latest
    container: ubuntu:22.04
    steps:
      - uses: actions/checkout@v3
      - name: Run S8r initialization test
        run: ./s8r-init --test
        
  test-debian:
    runs-on: ubuntu-latest
    container: debian:12
    steps:
      - uses: actions/checkout@v3
      - name: Run S8r initialization test
        run: ./s8r-init --test
        
  test-fedora:
    runs-on: ubuntu-latest
    container: fedora:38
    steps:
      - uses: actions/checkout@v3
      - name: Run S8r initialization test
        run: ./s8r-init --test
        
  test-alpine:
    runs-on: ubuntu-latest
    container: alpine:3.18
    steps:
      - uses: actions/checkout@v3
      - name: Run S8r initialization test
        run: ./s8r-init --test
```

## Developer Experience Verification

To ensure the setup provides a good developer experience, test these specific scenarios:

1. First-time setup: Measure time from fresh container to working environment
2. Subsequent runs: Verify that repeated initialization is fast and doesn't re-download components
3. IDE integration: Test setup with common IDEs (VS Code, IntelliJ IDEA)
4. Development workflow: Test full cycle (code, build, test, commit)

## Environment Verification Checklist

```markdown
# S8r Environment Verification Checklist

## System Prerequisites
- [ ] Git v2.30+ installed
- [ ] Bash v5.0+ installed
- [ ] Build tools installed
- [ ] Internet connection available

## Java Environment
- [ ] JDK 21+ installed
- [ ] JAVA_HOME set correctly
- [ ] Maven correctly configured
- [ ] Java sample compiles and runs

## Python Environment
- [ ] Python 3.10+ installed
- [ ] Virtual environment created
- [ ] Dependencies installed
- [ ] Python sample runs correctly

## TypeScript Environment
- [ ] Node.js v18+ installed
- [ ] npm/yarn configured
- [ ] TypeScript compiler available
- [ ] TypeScript sample compiles and runs

## Developer Tools
- [ ] S8r CLI tools available in PATH
- [ ] Editor/IDE configurations generated
- [ ] Git hooks installed
- [ ] Code quality tools configured

## Verification Test
- [ ] Build completes successfully
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] End-to-end sample works
```

## Troubleshooting Guide

Include common issues and solutions:

### Common Issues

1. **Missing system dependencies**
   - Symptom: Installation fails with cryptic error message
   - Solution: Run `./s8r-init --diagnose` to identify missing dependencies

2. **Java version conflict**
   - Symptom: Build fails with "Unsupported class file version"
   - Solution: Run `java -version` to check current version and `./s8r java-switch 21` to switch to Java 21

3. **Python dependency conflicts**
   - Symptom: Import errors despite successful setup
   - Solution: Verify virtual environment is activated with `which python` and reinstall with `./s8r-init --reinstall-python`

4. **Network connectivity issues**
   - Symptom: Timeouts during download of dependencies
   - Solution: Check network connection and proxy settings, then run `./s8r-init --offline` to use cached packages

5. **Permission issues**
   - Symptom: "Permission denied" errors during setup
   - Solution: Check file permissions and run `chmod +x ./s8r-init` if needed

## Conclusion

This document provides a comprehensive approach to testing the S8r R&D environment setup process. By systematically testing the setup across different platforms and configurations, we can ensure that developers can reliably create new environments with minimal friction and that all the necessary prerequisites are automatically installed and configured.

The testing process should be integrated into the CI/CD pipeline to ensure that setup continues to work correctly as the codebase evolves. Regular testing of the setup process will help identify issues early and ensure that new team members can quickly get up and running with S8r development.