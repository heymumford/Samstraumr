# Purpose
Infrastructure layer containing implementations of interfaces defined in the application layer.

## Contents
Files in this directory should be concrete implementations of application ports, adapters to external systems, and technical concerns.

## Naming Convention
Files should follow: `[technology/pattern]-[entity]-[implementation].[extension]`

Example: `InMemoryComponentRepository.java`, `Slf4jLogger.java`

## Do Not Include
- Domain entities or business logic (use domain layer)
- Application services or ports (use application layer)
- UI/presentation layer concerns
- Interfaces/ports (these should be in the application layer)

## Related Directories
- `../application/port/`: Contains interfaces implemented by infrastructure classes
- `../domain/`: Contains domain entities used by infrastructure implementations
- `../adapter/`: Contains adapters using infrastructure implementations