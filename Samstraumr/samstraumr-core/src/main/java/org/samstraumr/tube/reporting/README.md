# Change Management Reporting

This package provides tools for generating change management reports using Docmosis.

## Overview

The change management reporting system extracts version changes from Git commit history and combines them with test results to generate comprehensive reports in PDF format. These reports are useful for documenting changes between versions and ensuring proper change management processes.

## Components

### ChangeManagementReport

The core class responsible for generating reports using Docmosis. It dynamically loads the Docmosis library if available and generates PDF reports from templates.

### ChangeManagementData

Utility class for extracting change data from Git history and parsing test results.

### ChangeReportGenerator

Command-line tool for generating change management reports. It takes a from-version and to-version parameter and generates a report of all changes between them.

## Setup and Usage

### Prerequisites

1. Docmosis license key
2. Docmosis JAR files (java-4.8.0.jar)
3. Barcode4j (optional, for barcode support)

### Installation

1. Install the Docmosis JARs:
   ```
   ./install-docmosis.sh
   ```

2. Set your license key as an environment variable:
   ```
   export DOCMOSIS_KEY=your-license-key
   ```
   
   You may want to add this to your `~/.bashrc` or `~/.zshrc` to make it persistent:
   ```
   echo 'export DOCMOSIS_KEY=your-license-key' >> ~/.bashrc
   source ~/.bashrc
   ```

3. Build with the Docmosis profile:
   ```
   mvn clean install -P docmosis-report
   ```

### Creating Templates

Create a Word document template (*.docx) and save it in the `src/main/resources/templates` directory. Use Docmosis field syntax to define fields:

- `<<version>>` - The version number
- `<<timestamp>>` - Generation timestamp
- `<<changes>>` - List of changes
- `<<testResults>>` - Test result summary

### Generating Reports

Use the convenience script to generate reports:

```
./generate-change-report.sh 1.2.8 1.2.9
```

Or use the Java class directly:

```
java -cp target/samstraumr-core.jar org.samstraumr.tube.reporting.ChangeReportGenerator 1.2.8 1.2.9
```

## Example Report Fields

- Project Name: Samstraumr
- Version: 1.2.9
- Generated: 2025-04-03 14:30:00
- Changes: 
  - Added change management reporting
  - Fixed issue with tube initialization
  - Improved test coverage for core modules
- Test Results:
  - Tube Tests: Passed
  - Composite Tests: Passed
  - Machine Tests: Passed
  - Stream Tests: Failed
  - Acceptance Tests: Not Run