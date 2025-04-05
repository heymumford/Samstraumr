# Purpose
Domain-specific exceptions representing business rule violations and error conditions.

## Contents
Files in this directory should be exception classes that represent errors and exceptional conditions in the domain model.

## Naming Convention
Files should follow: `[entity/concept]-[problem]-Exception.[extension]`

Example: `InvalidStateTransitionException.java`, `ComponentNotFoundException.java`

## Do Not Include
- Generic technical exceptions (use standard Java exceptions)
- Infrastructure-level exceptions (database, network, etc.)
- Application-level exceptions not tied to domain concepts
- UI or presentation-level exceptions

## Related Directories
- `../component/`: Domain entities that may throw these exceptions
- `../machine/`: Machine entities that may throw these exceptions
- `../../application/service/`: Application services that handle these exceptions