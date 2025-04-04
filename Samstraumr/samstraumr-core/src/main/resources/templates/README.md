# Docmosis Templates

This directory contains templates for document generation using Docmosis.

## Templates

### change-report.docx

A template for generating change management reports. This template should be created in MS Word and include the following fields:

- `<<version>>`: The version number
- `<<timestamp>>`: The generation timestamp
- `<<changes>>`: A table of changes made in this version
- `<<testResults>>`: A table of test results

## Usage

To use these templates:

1. Install your Docmosis license key
2. Ensure you have the Docmosis JARs installed:
   ```
   ./install-docmosis.sh
   ```
3. Build with the docmosis-report profile:
   ```
   mvn clean install -P docmosis-report
   ```
4. Create a Word document template using the Docmosis field syntax
5. Place the template in this directory

## Barcode Support

Docmosis supports barcodes via Barcode4j. The Maven profile automatically includes this dependency.

## More Information

For more information about Docmosis, see:
- http://www.docmosis.com/resources/docmosis-java.html