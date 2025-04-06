<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Documentation CI

This document describes how the Documentation CI system works in the Samstraumr project.

## Overview

The Documentation CI system automates the process of checking, generating, and deploying documentation for the Samstraumr project. It ensures that documentation follows standards, is up-to-date, and is easily accessible to developers and users.

## Components

The Documentation CI system consists of the following components:

1. **Documentation Checking**
   - Standards checking
   - Link validation
   - TODO format verification

2. **Documentation Generation**
   - API (JavaDoc) documentation
   - Markdown to HTML conversion
   - Changelog generation
   - README updates

3. **Documentation Deployment**
   - GitHub Pages integration
   - Artifacts archiving

## Workflow

The Documentation CI workflow is defined in `.github/workflows/documentation-ci.yml` and includes the following steps:

### Triggers

The CI system is triggered by:

- Push to the main branch affecting documentation files
- Pull requests to the main branch affecting documentation files
- Manual trigger (workflow_dispatch)

### Jobs

#### 1. Check Documentation

- Verifies that documentation follows project standards
- Checks for broken links in Markdown files
- Validates TODO formats, especially for high-priority TODOs
- Generates reports of any issues found

#### 2. Generate Documentation

- Creates API documentation using JavaDoc
- Converts Markdown files to HTML
- Updates the changelog based on git history
- Updates README with the latest project information
- Packages everything for GitHub Pages deployment

#### 3. Deploy to GitHub Pages

- Publishes documentation to GitHub Pages
- Only runs on the main branch, not for pull requests
- Provides a public URL for accessing the documentation

#### 4. Reporting

- Generates a summary of all CI activities
- Provides status of checks, generation, and deployment
- Accessible in the GitHub Actions tab

## Tools Used

The Documentation CI system uses the following tools:

- **Scripts in `docs/tools/`**
  - `generate-changelog.sh` - Creates changelog from git history
  - `generate-javadoc.sh` - Generates API documentation
  - `update-readme.sh` - Updates README with latest info

- **Scripts in `docs/scripts/`**
  - `check-documentation-standards.sh` - Verifies documentation standards
  - `check-todo-format.sh` - Validates TODO formats

- **GitHub Actions**
  - `gaurav-nelson/github-action-markdown-link-check` - Checks for broken links
  - `actions/upload-artifact` - Archives documentation artifacts
  - GitHub Pages deployment actions

## Configuration

The Documentation CI system uses the following configuration files:

- `.github/workflows/documentation-ci.yml` - Main workflow definition
- `.github/workflows/mlc_config.json` - Markdown link checker configuration

## Usage

### Running Locally

To run documentation checks and generation locally:

```bash
# Check documentation standards
./docs/scripts/check-documentation-standards.sh

# Generate API documentation
./docs/tools/generate-javadoc.sh --output target/site/docs/api --links --markdown

# Update changelog
./docs/tools/generate-changelog.sh --update

# Update README
./docs/tools/update-readme.sh
```

### Viewing Results

- Generated documentation is deployed to GitHub Pages at: `https://emumford.github.io/Samstraumr/`
- Check reports are available as workflow artifacts in GitHub Actions

## Integration with Version Management

The Documentation CI system integrates with the version management system:

1. On version bump, the documentation is automatically updated
2. Version references in all documentation are kept in sync
3. Documentation is tagged with the version number

## Common Issues and Solutions

### Broken Links

If the CI reports broken links:
1. Check the link in the documentation
2. Either fix the link or add it to the ignore patterns in `mlc_config.json`

### JavaDoc Errors

If JavaDoc generation fails:
1. Look for errors in the JavaDoc comments
2. Fix any malformed tags or references

### Deployment Issues

If deployment to GitHub Pages fails:
1. Ensure the repository has GitHub Pages enabled
2. Check the permissions for the GitHub Actions workflow

## Future Improvements

- Adding documentation coverage metrics
- Automated screenshots for visual components
- Integration with external documentation platforms
- Better handling of code examples