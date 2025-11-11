<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Index

This directory contains comprehensive documentation for migrating from the Samstraumr framework to the S8r framework.

## Core Migration Guides

- [Samstraumr to S8r Migration Guide](samstraumr-to-s8r-migration.md) - Main migration guide with key concepts and code examples
- [Tube to Component Migration](tube-to-component-migration.md) - Detailed guide for migrating from Tubes to Components
- [Migration Utilities Guide](migration-utilities-guide.md) - Comprehensive guide to using the migration utilities
- [Port Interfaces Guide](port-interfaces-guide.md) - How to use port interfaces during migration
- [Adapter Reference](adapter-reference.md) - Technical reference for all adapter classes

## Specific Migration Scenarios

- [Bundle to Composite Refactoring](bundle-to-composite-refactoring.md) - Guide for migrating Bundle implementations to Composites
- [Package Flattening Guide](package-flattening-guide.md) - How to adapt to the new flattened package structure
- [Directory Flattening Migration Guide](directory-flattening-migration-guide.md) - How to handle directory structure changes
- [Using Domain Adapters](using-domain-adapters.md) - Guide for working with domain adapters

## Technical References

- [Port Implementation Summary](port-implementation-summary.md) - Summary of port interface implementations

## Migration Scripts

The following scripts are available to assist with migration:

- `/util/migrate-code.sh` - Automated code migration script
- `/util/scripts/migrate-packages.sh` - Script for updating package declarations and imports

## Best Practices

When migrating from Samstraumr to S8r, consider these best practices:

1. **Incremental Migration** - Migrate one component at a time, using adapters for integration
2. **Start with Tests** - Begin by updating tests to ensure they work with both old and new implementations
3. **Use Adapters** - Leverage the adapter classes to bridge legacy and new code
4. **Domain First** - Focus first on migrating domain logic, then infrastructure
5. **Verify Behavior** - Ensure behavior is consistent before and after migration

## Getting Help

If you encounter issues during migration:

1. Check the documentation in this directory
2. Look for examples in the codebase
3. Consult the automated tests for usage patterns
