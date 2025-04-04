# Maintenance Scripts

This directory contains scripts for maintaining the Samstraumr project.

## Available Scripts

### `update-version.sh`

Updates version numbers throughout the codebase.

```bash
./update-version.sh 0.5.2
```

This will:
- Update the version in version.properties
- Update the last updated date
- Update all POM files
- Update the README.md version

### `cleanup-maven.sh`

Cleans the Maven local repository cache to resolve dependency issues.

```bash
./cleanup-maven.sh
```

This will:
- Remove problematic artifacts from the local Maven repository
- Clear Maven's timestamp cache
- Force Maven to re-download dependencies

## Adding New Maintenance Scripts

When adding new maintenance scripts:

1. Make sure they're executable (`chmod +x script.sh`)
2. Update this README.md with the script's purpose
3. Make sure the script can determine the project root directory regardless of where it's called from
4. Follow the same script structure and error handling pattern as existing scripts
