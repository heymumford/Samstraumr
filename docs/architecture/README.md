<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# README

This directory contains all architecture documentation for the Samstraumr project, organized with clear naming conventions rather than nested directories.

## File Organization

Instead of using nested directories, we use file naming prefixes to indicate the category:

- `clean-*.md` - Documents related to Clean Architecture implementation
- `event-*.md` - Documents related to event-driven architecture
- `monitoring-*.md` - Documents related to monitoring and observability
- `pattern-*.md` - Documents related to architectural patterns

## Key Documents

- [strategy.md](strategy.md) - Overall architectural strategy
- [implementation.md](implementation.md) - Implementation details
- [directory-structure.md](directory-structure.md) - Directory structure guidelines

## Architecture Decision Records

Architecture Decision Records (ADRs) remain in their own directory to maintain their sequential nature and provide a clear historical record:

- [decisions/.md](decisions/.md) - Architecture Decision Records

## Related Documents

- [../reference/standards/](../reference/standards/) - Coding and documentation standards
- [../plans/](../plans/) - Project plans including architecture implementation plans
- [../concepts/](../concepts/) - Core concepts underlying the architecture

## Related Research

These research papers provide the conceptual foundation for aspects of Samstraumr's architecture:

- [Testing in the Age of AI](../research/test-in-age-of-ai.md) - Eric C. Mumford's research on AI-enhanced testing architecture
- [AI-Enhanced Testing Integration](../research/ai-enhanced-testing-integration.md) - Implementation strategy for AI capabilities
- [Critical Components of Cell Activity Simulation](../research/critical-components-of-simulating-and-monitoring-human-cell-activity-in-vitro.md) - Biological systems research that inspired the component architecture
