# Documentation Fixes Plan

This document outlines the plan to fix identified documentation issues in the Samstraumr project.

## 1. Fix Copyright Header Issues

### 1.1 Standardize Copyright Headers in Java Files
- Remove the duplicate copyright header in `DocumentGenerator.java`
- Check other Java files for similar duplication issues
- Ensure all Java files use a consistent format

### 1.2 Update Headers in Java Files
```bash
#!/bin/bash
# Standardize copyright headers in Java files

find . -name "*.java" -type f -not -path "*/node_modules/*" -not -path "*/target/*" | while read -r file; do
    # Check for duplicate copyright notices
    if grep -q "Copyright (c) 2025 Samstraumr Team" "$file"; then
        # Create a temporary file with the proper header only
        TEMP_FILE=$(mktemp)
        cat "$file" | grep -v "Copyright (c) 2025 Samstraumr Team" > "$TEMP_FILE"
        
        # Replace original file
        mv "$TEMP_FILE" "$file"
        echo "Fixed duplicate header in $file"
    fi
done
```

## 2. Fix Documentation Standards

### 2.1 Consolidate Documentation Standards
- Merge content from both documentation standards files into a single file
- Use the kebab-case version (`documentation-standards.md`) as the primary file
- Update the content to resolve any contradictions
- Remove the CamelCase version

### 2.2 Fix Documentation Standards Content
- Update file organization tables
- Ensure the standards document follows its own guidelines
- Fix any typos and incorrect references

## 3. Update Outdated CLI Commands

### 3.1 Update Contributing Guide
- Update `contributing.md` to reference the current CLI commands
- Change `./run-tests.sh all` to `./s8r test all`
- Change `./build-checks.sh` to `./s8r quality check`

### 3.2 Check Other Docs for CLI References
- Scan all documentation for references to outdated CLI commands
- Update any found references to use the current `./s8r` commands

## 4. Fix Internal Links

### 4.1 Review and Fix Links in README.md
- Ensure all links in README.md accurately reference existing files
- Standardize casing in links to match file system

### 4.2 Check Links in Architecture Documentation
- Review links in architecture documentation 
- Fix any broken links between architecture documents

## 5. Document Organization Cleanup

### 5.1 Resolve Duplicate Files
- Decide on a single source of truth for duplicated content
- Remove redundant test annotation directories
- Document the correct location for various types of files

### 5.2 Fix Filename Conventions
- Ensure all documentation follows kebab-case naming
- Rename any files that don't follow this convention

## Implementation Order

1. Fix copyright header duplication issues
2. Consolidate documentation standards files
3. Update CLI command references
4. Fix internal links and references
5. Clean up documentation organization

## Validation Process

After implementing these fixes, we will:
1. Run a documentation scan to verify no duplicate headers exist
2. Test internal links to ensure they correctly resolve
3. Review standards compliance across the documentation
4. Verify CLI commands work as documented