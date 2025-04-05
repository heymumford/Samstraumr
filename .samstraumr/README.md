# Samstraumr Configuration System

This directory contains the unified configuration system for the Samstraumr framework.

## Directory Structure

- `config.json`: Primary configuration file in JSON format
- `config.sh`: Auto-generated Bash configuration for shell scripts
- `templates/`: Configuration templates
- `user/`: User-specific settings (gitignored)

## Configuration Precedence

1. User-specific settings (`.samstraumr/user/config.json`)
2. Project configuration (`.samstraumr/config.json`)
3. Default templates (`.samstraumr/templates/config.json.template`)

## Updating Configuration

The primary source of truth is `config.json`. When this file is modified, run:

```bash
./s8r config generate
```

This will regenerate the `config.sh` file for Bash script compatibility.

## For Developers

- Shell scripts should source `.samstraumr/config.sh`
- The s8r CLI tool uses `.samstraumr/config.json` directly
- User-specific settings should be placed in `.samstraumr/user/config.json`

## Schema Documentation

The configuration schema includes:

- `project`: Project metadata
- `paths`: Directory and file paths
- `packages`: Java package structures
- `test`: Test-related settings and profiles
- `maven`: Maven build configuration
- `commands`: Command mappings for the s8r CLI
