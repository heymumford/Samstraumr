# Script Simplification Plan

## Current Issues

1. **Excessive Duplication**: 
   - Significant code duplication between `run-tests.sh` and `run-atl-tests.sh`
   - Duplicated argument parsing logic
   - Duplicated test execution functions
   - Duplicated output handling code

2. **Scattered Color Definitions**:
   - Color codes defined independently in multiple files
   - Inconsistent naming (`COLOR_RED` vs `STATUS_RED`)
   - Multiple implementations of similar formatting utilities

3. **Inconsistent Command-Line Parsing**:
   - Different patterns used across scripts
   - Some use `case` statements, others use custom logic
   - Common argument parsing functions in `common.sh` aren't consistently used

4. **Test Type Mapping Redundancy**:
   - Test type mapping implemented in:
     - `s8r` (lines 410-479)
     - `test-lib.sh` (lines 17-98)
     - `run-tests.sh` (lines 102-240)

5. **Duplicated Maven Command Building**:
   - Maven command construction duplicated in multiple files
   - Similar logic with slight variations
   - No centralized utility for command generation

## Simplification Plan

### Phase 1: enhance common libraries

1. **Centralize Color and Formatting Logic**:
   - Use only `common.sh` for color definitions
   - Create standardized print functions for consistent output
   - Remove duplicate color definitions from other scripts

2. **Add Standardized Argument Parsing**:
   - Create a generalized argument parsing function in `common.sh`
   - Support common flag patterns (-v/--verbose, etc.)
   - Use associative arrays for configuration

3. **Create Maven Command Builder**:
   - Implement a unified function for Maven command construction
   - Support profiles, goals, and other common parameters
   - Handle clean, skip tests, and other flags consistently

4. **Implement Script Initialization Helper**:
   - Create a standard initialization function for all scripts
   - Handle path determination and config loading
   - Set up error handling and default options

### Phase 2: consolidate test scripts

1. **Merge Test Execution Scripts**:
   - Combine `run-tests.sh` and `run-atl-tests.sh` into a single script
   - Use a parameterized approach for different test types
   - Centralize test reporting and result handling

2. **Centralize Test Type Mapping**:
   - Move all test type mapping logic to `test-lib.sh`
   - Create utility functions for test type conversion
   - Ensure consistent handling of test types across the codebase

3. **Simplify Test Output and Reporting**:
   - Create a unified test reporting function in `test-lib.sh`
   - Standardize test result formatting and display
   - Improve verbose mode output for better debugging

### Phase 3: update main cli (s8r)

1. **Refactor Command Handling**:
   - Use the command pattern for cleaner organization
   - Leverage the enhanced libraries for consistent behavior
   - Reduce the main script size by modularizing functionality

2. **Improve Help Documentation**:
   - Move help text to separate files
   - Implement dynamic help generation for commands
   - Ensure consistent help format across all commands

### Phase 4: modern shell features

1. **Use Associative Arrays** for configuration and argument storage
2. **Leverage Parameter Expansion** for cleaner variable defaults
3. **Implement Process Substitution** for more elegant command chaining
4. **Use Here Strings** for more readable command inputs
5. **Enforce Local Variables** to prevent scope leakage

### Phase 5: testing and documentation

1. **Develop Test Cases** for the simplified scripts
2. **Document New Patterns** for future script development
3. **Update CLI Documentation** to reflect changes
4. **Create Migration Guide** for updating existing scripts

## Implementation Order

1. Enhance common libraries (`common.sh`, `test-lib.sh`)
2. Implement the unified test execution script
3. Update the main CLI (`s8r`)
4. Test all changes thoroughly
5. Update documentation
