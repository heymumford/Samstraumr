# Samstraumr Library Functions

This directory contains shared bash library functions for the Samstraumr project.

## Unified Common Library

The `unified-common.sh` file is the primary library for all Samstraumr scripts. It consolidates functions from multiple sources into a single, comprehensive utility library.

## Documentation Library

The `doc-lib.sh` file provides specialized functions for documentation processing, standardization, and verification. It centralizes functionality from various documentation scripts to ensure consistency and reduce maintenance overhead.

### Usage

```bash
# Source the unified common library
source "${PROJECT_ROOT}/util/lib/unified-common.sh"

# Use library functions
print_header "Processing Started"
ensure_directory_exists "/path/to/directory"
if validate_semantic_version "$version"; then
  print_success "Valid version: $version"
fi
```

### Function Categories

The unified library provides the following function categories:

1. **Output Functions**
   - `print_header`, `print_section`: Section headers
   - `print_success`, `print_error`, `print_warning`, `print_info`: Status messages
   - `print_bold`, `print_debug`: Text formatting

2. **Argument Parsing**
   - `show_help_template`: Standardized help display
   - `parse_common_args`: Process standard arguments
   - `parse_args`: Comprehensive argument parsing using associative arrays

3. **File & Path Functions**
   - `ensure_directory_exists`: Create directories as needed
   - `is_valid_path`, `get_absolute_path`: Path validation and normalization
   - `path_for_package`, `find_package_directory`: Java package handling

4. **Maven Functions**
   - `get_maven_property`: Extract properties from POM files
   - `run_maven_command`, `run_maven`: Execute Maven commands
   - `build_maven_command`: Construct Maven command lines

5. **String Functions**
   - `to_lowercase`, `to_uppercase`: Case conversion
   - `trim_string`: Whitespace removal
   - `string_contains`: Substring checking

6. **Validation Functions**
   - `validate_not_empty`, `validate_is_number`: Input validation
   - `validate_in_list`: Option validation
   - `validate_semantic_version`: Version format validation

7. **System Functions**
   - `get_os_type`, `is_linux`, `is_mac`: OS detection
   - `get_cpu_count`, `get_available_memory`: System capabilities
   - `command_exists`: Command availability checking

8. **Build and Test Functions**
   - `run_test_with_status`: Test execution with status reporting
   - `print_test_status`: Standardized test status output
   - `initialize_script`: Script bootstrapping

9. **Git Functions**
   - `get_git_branch`, `is_git_repo`: Repository status
   - `has_git_changes`: Change detection
   - `get_git_root`: Repository location

## Documentation Library Usage

```bash
# Source the documentation library
source "${PROJECT_ROOT}/util/lib/doc-lib.sh"

# Use library functions
if is_kebab_case "file-name.md"; then
  print_success "File follows naming convention"
fi

# Generate README template
generate_readme_template "/path/to/directory"

# Check documentation standards
find_broken_links "/path/to/file.md"
```

### Function Categories

The documentation library provides the following function categories:

1. **File Naming Functions**
   - `to_kebab_case`, `is_kebab_case`: File naming convention handling
   - `find_non_kebab_case_files`: Find non-compliant filenames
   - `standardize_directory_filenames`: Update filenames to follow standards

2. **Markdown Content Analysis**
   - `has_level1_header`, `get_level1_header`: Header validation
   - `header_matches_filename`: Filename-title consistency check
   - `check_code_blocks`: Code block validation
   - `find_readme_files`, `find_missing_readmes`: README file management

3. **Cross-Reference Functions**
   - `extract_links`, `is_valid_link`: Link extraction and validation
   - `find_broken_links`: Broken link identification
   - `create_file_mapping`: Generate file reference maps

4. **TODO Processing**
   - `extract_todos`: Extract TODOs from codebase
   - `is_standard_todo`: Validate TODO format
   - `get_todo_priority`, `get_todo_category`, `get_todo_description`: Parse TODOs

5. **Document Generation**
   - `generate_readme_template`: Create standardized READMEs
   - `generate_header_report`: Generate documentation reports

6. **Documentation Fixing**
   - `fix_common_issues`: Automatically repair documentation problems
   - `update_cross_references`: Fix broken cross-references

## Legacy Libraries

The following libraries are maintained for backward compatibility:

- `common.sh`: Basic utility functions (use unified-common.sh instead)
- `build-lib.sh`: Build-specific functions
- `test-lib.sh`: Test-specific functions
- `quality-lib.sh`: Quality check functions
- `version-lib.sh`: Version management functions
- `script-template.sh`: Template for new scripts

## Best Practices

1. **Always use the unified library** for new scripts
2. **Prefer print_* functions** over direct color codes
3. **Use parse_args** for complex argument parsing
4. **Validate inputs** with validation functions
5. **Properly handle errors** using print_error and exit codes
6. **Add comprehensive documentation** to all new functions

## Migration

When migrating existing scripts to use the unified library:

1. Replace `source "${PROJECT_ROOT}/util/lib/common.sh"` with `source "${PROJECT_ROOT}/util/lib/unified-common.sh"`
2. Use fallback mechanism for backward compatibility:
   ```bash
   if [ -f "${PROJECT_ROOT}/util/lib/unified-common.sh" ]; then
     source "${PROJECT_ROOT}/util/lib/unified-common.sh"
   else
     source "${PROJECT_ROOT}/util/lib/common.sh"
   fi
   ```
3. Update function calls to use the unified function names
4. Update help formatting to use print_section and print_header