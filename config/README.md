<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Configuration Directory

This directory contains configuration files for the Samstraumr project. It's intended to keep the root directory clean by centralizing configuration.

## Contents

- `surefire-settings.xml` - Maven Surefire plugin configuration for test execution
- `s8r.config` - Configuration settings for the s8r scripts

## Usage

Most tools and scripts are configured to look for these files in their new location. If you encounter any issues with tools not finding configuration files, you may need to update the tool's configuration to point to this directory.

## Adding New Configuration

When adding new configuration files:

1. Place them in this directory
2. Update the .gitignore file if they contain sensitive information
3. Update documentation to reference the new location