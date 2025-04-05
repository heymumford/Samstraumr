<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Archived Temp Reorg Plan

## Current Issues:

- Too many nested directories
- Inconsistent naming conventions
- Similar functionality split across directories
- Complex paths for running scripts

## Goals:

- Flatter directory structure
- Consistent naming with kebab-case
- Group by functionality with prefixes
- Simpler paths for running scripts

## Proposed Structure:

### Util/ (root)

- `README.md` - Main documentation
- `version.sh` (renamed from `version`) - Main version utility
- All test scripts moved directly here

### Util/build/

- Keep `build-optimal.sh` and `build-performance.*` scripts
- Move `java-env-setup.sh` to util/ as `setup-java-env.sh`
- Keep `generate-build-report.sh`

### Util/scripts/

- Combine all maintenance, quality, and badges scripts here
- Use consistent prefix naming:
  - `check-*.sh` for checking scripts
  - `fix-*.sh` for fixing scripts
  - `update-*.sh` for update scripts
  - `setup-*.sh` for setup scripts
  - `generate-*.sh` for generation scripts

## File Renaming Plan:

|                   Current Path                    |                 New Path                  |
|---------------------------------------------------|-------------------------------------------|
| `util/version`                                    | `util/version.sh`                         |
| `util/test/run-tests.sh`                          | `util/test-run.sh`                        |
| `util/test/run-atl-tests.sh`                      | `util/test-run-atl.sh`                    |
| `util/test/run-all-tests.sh`                      | `util/test-run-all.sh`                    |
| `util/test/mapping/map-test-type.sh`              | `util/test-map-type.sh`                   |
| `util/build/build-optimal.sh`                     | (Keep as is)                              |
| `util/build/build-performance.sh`                 | (Keep as is)                              |
| `util/build/generate-build-report.sh`             | (Keep as is)                              |
| `util/build/java-env-setup.sh`                    | `util/setup-java-env.sh`                  |
| `util/build/java17-compat.sh`                     | `util/setup-java17-compat.sh`             |
| `util/maintenance/cleanup-maven.sh`               | `util/scripts/clean-maven.sh`             |
| `util/maintenance/fix-line-endings.sh`            | `util/scripts/fix-line-endings.sh`        |
| `util/maintenance/headers/update-java-headers.sh` | `util/scripts/update-java-headers.sh`     |
| `util/maintenance/headers/update-md-headers.sh`   | `util/scripts/update-md-headers.sh`       |
| `util/maintenance/setup-fast.sh`                  | `util/scripts/setup-fast-build.sh`        |
| `util/maintenance/update-and-test.sh`             | `util/update-version-test.sh`             |
| `util/maintenance/update-version.sh`              | (Deprecate in favor of `util/version.sh`) |
| `util/quality/build-checks.sh`                    | `util/scripts/check-build-quality.sh`     |
| `util/quality/check-encoding.sh`                  | `util/scripts/check-encoding.sh`          |
| `util/quality/enable-quality-tools.sh`            | `util/scripts/setup-quality-tools.sh`     |
| `util/quality/fix-pmd.sh`                         | `util/scripts/fix-pmd.sh`                 |
| `util/quality/fix-scripts/fix-logger.sh`          | `util/scripts/fix-logger.sh`              |
| `util/quality/skip-quality-build.sh`              | `util/scripts/build-skip-quality.sh`      |
| `util/badges/generate-badges.sh`                  | `util/scripts/generate-badges.sh`         |

## Implementation Plan:

1. Create new directories
2. Copy scripts to their new locations
3. Update script references within scripts
4. Update README files
5. Test functionality
6. Create deprecation warnings for old locations
