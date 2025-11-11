<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Scientific Documentation Standards

This document outlines our approach to creating documentation that is accessible and useful for scientists and domain experts who may have limited computer science knowledge but deep expertise in their respective fields.

## Core Principles

Our documentation follows these core principles:

1. **Accessibility First**: Documentation should be accessible to scientists with varying levels of technical expertise.
2. **Domain-Centric**: Focus on how S8r applies to scientific domains, not just its technical implementation.
3. **Visual Learning**: Leverage diagrams, flowcharts, and visual examples to explain complex concepts.
4. **Progressive Disclosure**: Start with simple concepts and gradually introduce complexity.
5. **Real-World Examples**: Include examples from scientific domains (e.g., biology, physics, environmental science).

## Documentation Structure

All user-facing documentation should be structured as follows:

### 1. Introduction

- Non-technical overview explaining purpose and benefits
- Real-world analogies to scientific concepts
- Visual overview diagram showing how components relate to domain concepts

### 2. Core Concepts

- Explanation of key components using scientific analogies
- Side-by-side comparison of S8r concepts with scientific equivalents
- Visual representations of how data flows through the system

### 3. Getting Started

- Step-by-step instructions with screenshots
- Simple, immediately useful examples from scientific domains
- Minimal prerequisites and clear indication of what prior knowledge is assumed

### 4. Tutorials

- Domain-specific tutorials showing complete scientific workflows
- "Copy-paste-ready" examples that scientists can adapt
- Progressive tutorials from basic to advanced usage

### 5. How-To Guides

- Task-oriented instructions for common scientific use cases
- Clear problem statements and solutions
- Options for different levels of customization

### 6. Reference

- Comprehensive API documentation with scientific context
- Examples showing how each component applies to scientific research
- Common patterns and best practices for scientific applications

### 7. Troubleshooting

- Common errors explained in non-technical language
- Problem-symptom-solution format
- Guidance for getting help from the community

## Language Guidelines

When writing documentation:

1. **Avoid Jargon**: Explain technical terms or replace with more accessible equivalents
2. **Use Scientific Analogies**: Relate software concepts to scientific processes
3. **Be Concise**: Use short, clear sentences and explanations
4. **Provide Context**: Explain why certain approaches are recommended, not just how
5. **Use Active Voice**: Write in direct, clear language (e.g., "Configure the component" vs. "The component should be configured")

## Examples

### Poor Example (Too Technical)
```
Implement the CompositeAdapter interface to create a custom adapter 
that leverages polymorphic type erasure for integrating legacy components.
```

### Better Example (Scientist-Friendly)
```
Create a "translator" (adapter) that helps your scientific models 
work with S8r. Think of this adapter like a universal converter that 
allows instruments from different manufacturers to work together in 
your lab.
```

## Visual Documentation

All complex concepts should include:

1. **Conceptual Diagrams**: Show relationships between components and scientific concepts
2. **Flowcharts**: Illustrate processes and decision points
3. **Examples with Annotations**: Screenshots or code samples with explanatory notes
4. **Before/After Comparisons**: Show how S8r improves scientific workflows

## Review Process

All documentation should be reviewed by:

1. Technical experts for accuracy
2. Scientists from the target domains for usability
3. Newcomers to test clarity and effectiveness

## Documentation Checklist

When creating or updating documentation, ensure it:

- [ ] Starts with real-world scientific examples
- [ ] Explains concepts using domain-specific analogies
- [ ] Includes visual aids (diagrams, flowcharts)
- [ ] Avoids unnecessary technical jargon
- [ ] Provides full examples that can be copied and adapted
- [ ] Links to related documentation for further learning
- [ ] Has been reviewed by someone with the target level of expertise

## Tools and Templates

The following tools and templates are available:

1. **Scientific Example Generator**: Creates domain-specific examples
   ```bash
   ./bin/s8r-docs generate-example --domain biology
   ```

2. **Documentation Templates**: Pre-structured templates with scientist-friendly sections
   ```bash
   ./bin/s8r-docs create-guide --template scientific
   ```

3. **Concept Visualizer**: Generates diagrams relating S8r concepts to scientific domains
   ```bash
   ./bin/s8r-docs visualize-concept machine --domain physics
   ```

## Integration with Release Process

Documentation for scientists must be:

1. Updated with each release to reflect new features and improvements
2. Tested with representative users before release
3. Published in formats accessible to the scientific community

## Benefits

Following these standards ensures:

1. **Broader Adoption**: More scientists can use S8r without deep technical expertise
2. **Reduced Support Burden**: Clear documentation reduces the need for direct support
3. **Community Growth**: Accessible documentation encourages scientific contributions
4. **Interdisciplinary Collaboration**: Bridges the gap between computer science and other scientific domains