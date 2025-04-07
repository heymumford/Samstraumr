# Architecture Decision Records

This directory contains Architecture Decision Records (ADRs) for the Samstraumr project.

## What are Architecture Decision Records?

Architecture Decision Records (ADRs) are documents that capture important architectural decisions made along with their context and consequences. They provide a historical record of the technical choices made during the project development.

Each ADR describes:
- The architectural decision that was made
- The context and forces that led to the decision
- The rationale behind the decision
- The consequences and trade-offs of the decision

## How to Create a New ADR

To create a new ADR:

1. Run the ADR creation script:
   ```bash
   ./bin/new-adr "Title of the decision"
   ```

   For example:
   ```bash
   ./bin/new-adr "Use PostgreSQL for persistent storage"
   ```

2. Edit the generated file to fill in the details.
3. Update the status when the decision is accepted or rejected.

## Index of ADRs

Here's a chronological list of all ADRs:

| ID | Title | Status |
|----|-------|--------|
| [ADR-0001](0001-record-architecture-decisions.md) | Record architecture decisions | Accepted |
| [ADR-0002](0002-automated-c4-architecture-diagrams.md) | Automated C4 architecture diagrams | Accepted |
| [ADR-0003](0003-adopt-clean-architecture-for-system-design.md) | Adopt Clean Architecture for system design | Accepted |
| [ADR-0004](0004-test-architecture-documentation-generation.md) | Test Architecture Documentation Generation | Accepted |
| [ADR-0005](0005-refactor-package-structure-to-align-with-clean-architecture.md) | Refactor Package Structure to Align with Clean Architecture | Accepted |
| [ADR-0006](0006-implement-comprehensive-testing-pyramid-strategy.md) | Implement Comprehensive Testing Pyramid Strategy | Accepted |
| [ADR-0007](0007-adopt-component-based-architecture-for-system-modularity.md) | Adopt Component-Based Architecture for System Modularity | Accepted |
| [ADR-0008](0008-implement-hierarchical-identity-system.md) | Implement Hierarchical Identity System | Accepted |
| [ADR-0009](0009-adopt-lifecycle-state-management-pattern.md) | Adopt Lifecycle State Management Pattern | Accepted |
| [ADR-0010](0010-implement-event-driven-communication-model.md) | Implement Event-Driven Communication Model | Accepted |
| [ADR-0011](0011-adopt-standardized-error-handling-strategy.md) | Adopt Standardized Error Handling Strategy | Accepted |

## Statuses

- **Proposed**: The ADR is proposed and under discussion
- **Accepted**: The ADR has been accepted and the decision is in effect
- **Rejected**: The ADR was rejected, and the decision will not be implemented
- **Deprecated**: The ADR was once accepted but is no longer relevant
- **Superseded**: The ADR was accepted but has been replaced by a newer decision (link to the new ADR)
