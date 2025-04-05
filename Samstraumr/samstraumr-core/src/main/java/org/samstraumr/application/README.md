# Purpose
Application layer containing use cases, service orchestration, and coordination between domain and infrastructure.

## Contents
Files in this directory should be application services, ports (interfaces), and DTOs that implement use cases and coordinate domain logic.

## Naming Convention
Files should follow: `[usecase/entity]-[action]-Service.[extension]` or `[entity]-[direction]-Port.[extension]`

Example: `ComponentService.java`, `MachineRepository.java` (port)

## Do Not Include
- Domain entities or core business logic (use domain layer)
- Infrastructure implementations (adapters, repositories, etc.)
- UI/presentation concerns
- Technical utilities without application logic

## Related Directories
- `../domain/`: Contains domain entities used by application services
- `../infrastructure/`: Contains implementations of application ports
- `../adapter/`: Contains adapters that use application services