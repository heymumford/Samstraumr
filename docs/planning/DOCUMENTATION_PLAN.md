# Samstraumr Documentation Reorganization Plan

## Overview

This document outlines the plan for reorganizing the Samstraumr documentation to create a more structured, accessible, and comprehensive resource. The reorganization focuses on:

1. Creating a well-structured docs directory with clear categories
2. Transforming the root README.md into a detailed table of contents
3. Aligning terminology across all documentation
4. Ensuring documentation is accessible to different audiences

## New Documentation Structure

```
/docs/
├── README.md                        # Documentation overview
├── concepts/                        # Core concepts and foundations
│   ├── core-concepts.md            # Fundamental building blocks and principles
│   ├── systems-theory-foundation.md # Theoretical underpinnings
│   ├── state-management.md         # Dual state approach
│   └── identity-addressing.md      # Naming and hierarchical addressing
├── guides/                          # Practical implementation guides
│   ├── getting-started.md          # First steps with Samstraumr
│   ├── migration-guide.md          # Integrating with existing systems
│   ├── tube-patterns.md            # Common implementation patterns
│   └── composition-strategies.md   # Combining tubes into larger structures
├── reference/                       # Technical reference
│   ├── api-reference.md            # Complete API documentation
│   ├── configuration.md            # Configuration options
│   ├── glossary.md                 # Terminology definitions
│   └── faq.md                      # Frequently asked questions
├── testing/                         # Testing strategy and implementation
│   ├── test-strategy.md            # Overall testing approach
│   ├── bdd-with-cucumber.md        # BDD implementation
│   └── testing-annotations.md      # Custom test annotations
├── contribution/                    # Contribution guidelines
│   ├── contributing.md             # How to contribute
│   ├── code-style.md               # Coding standards
│   └── pull-request-workflow.md    # PR process
└── research/                        # Research papers and proposals
    ├── llm-context-proposal.md     # LLM integration proposal
    └── distributed-systems.md      # Distributed computing extension
```

## Root README.md Transformation

The root README.md has been transformed into a comprehensive guide that:

1. Clearly explains what Samstraumr is in both technical and value-proposition terms
2. Provides context for different audiences (developers, architects, business stakeholders)
3. Offers a structured, detailed table of contents for all documentation
4. Includes quick-start information for immediate engagement
5. Links to the most important documentation sections

## Documentation Categories

### Concepts

Focused on the theoretical foundations and core ideas of Samstraumr, these documents explain the "why" and "what" of the framework.

### Guides

Practical, task-oriented documents that help users implement Samstraumr concepts. These guide users through specific processes and implementations.

### Reference

Technical details, specifications, and reference information for users who need precise information about APIs, configuration options, and terminology.

### Testing

Comprehensive documentation of Samstraumr's testing approach, including the integration of industry-standard terminology with domain-specific concepts.

### Contribution

Guidelines and workflows for contributing to the Samstraumr project, ensuring consistent and high-quality contributions.

### Research

Forward-looking proposals, experimental features, and research papers exploring advanced applications of Samstraumr concepts.

## Implementation Plan

1. Create the new directory structure
2. Migrate existing content to the new structure
3. Create new content to fill gaps
4. Update cross-references and links
5. Review and edit all content for consistency
6. Replace the existing documentation with the new structure

## Content Migration Map

| Existing File | New Location | Action |
|---------------|--------------|--------|
| /docs/CoreConcepts.md | /docs/concepts/core-concepts.md | Updated |
| /docs/SystemsTheoryFoundation.md | /docs/concepts/systems-theory-foundation.md | Updated |
| /docs/StateManagement.md | /docs/concepts/state-management.md | Updated |
| /docs/BundlesAndMachines.md | /docs/concepts/identity-addressing.md | Replaced with new content |
| /docs/GettingStarted.md | /docs/guides/getting-started.md | Updated |
| /docs/Migration.md | /docs/guides/migration-guide.md | To be migrated |
| /docs/TubePatterns.md | /docs/guides/tube-patterns.md | To be migrated |
| /docs/Glossary.md | /docs/reference/glossary.md | Updated |
| /docs/FAQ.md | /docs/reference/faq.md | Updated |
| /docs/Testing.md | /docs/testing/test-strategy.md | Replaced with new content |
| /docs/TestingStrategy.md | /docs/testing/test-strategy.md | Updated and merged |
| /docs/proposals/LLMContextCompositeTubeProposal.md | /docs/research/llm-context-proposal.md | To be migrated |

## Final Steps

1. Update all internal links in documentation
2. Ensure README.md links are correct
3. Add navigation aids (back to top links, etc.)
4. Verify formatting is consistent across all documents
5. Add metadata (creation dates, last updated) to all documents